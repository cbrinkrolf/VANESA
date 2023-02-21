package io;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.stream.XMLStreamException;

import io.graphML.GraphMLWriter;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import configurations.ConnectionSettings;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.framework.utils.exception.VoidRepositoryException;
import gonOutput.GONoutput;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MyPopUp;
import moOutput.MOoutput;
import transformation.Rule;
import transformation.RuleManager;
import transformation.YamlRuleWriter;
import util.ImageExport;
import io.sbml.JSBMLOutput;

public class SaveDialog {
    // use power of 2
    public static int FORMAT_SBML = 1;
    public static int FORMAT_GRAPHML = 2;
    public static int FORMAT_MO = 4;
    public static int FORMAT_CSML = 8;
    // removed: FORMAT_VAML = 16;
    public static int FORMAT_TXT = 32;
    // removed: FORMAT_ITXT = 64;
    public static int FORMAT_PNML = 128;
    public static int FORMAT_CSV = 256;
    public static int FORMAT_PNG = 512;
    public static int FORMAT_YAML = 1024;
    public static int FORMAT_SVG = 2048;
    public static int FORMAT_PDF = 4096;

    public static final int DATA_TYPE_TRANSFORMATION_RULES = 1;
    public static final int DATA_TYPE_VISUALIZATION_SETTINGS = 2;
    public static final int DATA_TYPE_GRAPH_PICTURE = 3;
    public static final int DATA_TYPE_NETWORK_EXPORT = 4;
    public static final int DATA_TYPE_SIMULATION_RESULTS = 5;

    private FileFilter fileFilter;
    private int dataType;
    private File file;
    private Component c = null;
    private JFileChooser chooser;

    public SaveDialog(int format, int dataType, Component c, Component relativeTo, String simId) {
        if (relativeTo == null) {
            relativeTo = MainWindow.getInstance().getFrame();
        }
        String error = "";
        this.prepare(format);
        this.c = c;
        this.dataType = dataType;
        int option = chooser.showSaveDialog(relativeTo);
        if (option == JFileChooser.APPROVE_OPTION) {
            // Save path to settings.xml
            ConnectionSettings.getInstance().setFileSaveDirectory(chooser.getCurrentDirectory().getAbsolutePath());
            fileFilter = chooser.getFileFilter();
            file = chooser.getSelectedFile();
            boolean overwrite = true;
            if (file.exists()) {
                int response = JOptionPane.showConfirmDialog(relativeTo, "Overwrite existing file?",
                        "Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.CANCEL_OPTION) {
                    overwrite = false;
                }
            }
            if (overwrite) {
                try {
                    write(simId);
                } catch (IOException | HeadlessException | XMLStreamException | TranscoderException | InvalidIDException
                         | VoidRepositoryException e) {
                    error += e.getMessage();
                    MyPopUp.getInstance().show("Error!", "An error occurred:\r\n" + error);
                    e.printStackTrace();
                }
            }
        }
    }

    public SaveDialog(int format, List<JFreeChart> charts, Component relativeTo) {
        if (relativeTo == null) {
            relativeTo = MainWindow.getInstance().getFrame();
        }
        String error = "";
        this.prepare(format);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = chooser.showSaveDialog(relativeTo);
        if (option == JFileChooser.APPROVE_OPTION) {
            // Save path to settings.xml
            ConnectionSettings.getInstance().setFileSaveDirectory(chooser.getCurrentDirectory().getAbsolutePath());
            fileFilter = chooser.getFileFilter();
            file = chooser.getSelectedFile();
            String pathSim;
            pathSim = file.getAbsolutePath() + File.separator;
            int width = 320;
            int height = 200;
            boolean overwrite = false;
            boolean stop = false;
            for (JFreeChart chart : charts) {
                String name = chart.getTitle().getText();
                if (fileFilter == SuffixAwareFilter.PDF) {
                    PDFDocument pdfDoc = new PDFDocument();
                    pdfDoc.setAuthor("VANESA");
                    Page page = pdfDoc.createPage(new Rectangle(width, height));
                    PDFGraphics2D g2 = page.getGraphics2D();
                    chart.draw(g2, new Rectangle(0, 0, width, height));
                    if (new File(pathSim + name + ".pdf").exists() && !overwrite && !stop) {
                        int response = JOptionPane.showConfirmDialog(relativeTo, "Overwrite all existing files?",
                                "Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (!(response == JOptionPane.OK_OPTION)) {
                            stop = true;
                        } else {
                            overwrite = true;
                        }
                    }
                    if (!stop) {
                        pdfDoc.writeToFile(new File(pathSim + name + ".pdf"));
                    }
                } else if (fileFilter == SuffixAwareFilter.PNG) {
                    if (new File(pathSim + name + ".png").exists() && !overwrite) {
                        int response = JOptionPane.showConfirmDialog(relativeTo, "Overwrite all existing files?",
                                "Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (!(response == JOptionPane.OK_OPTION)) {
                            stop = true;
                        } else {
                            overwrite = true;
                        }
                    }
                    if (!stop) {
                        try {
                            ChartUtils.saveChartAsPNG(new File(pathSim + name + ".png"), chart, width * 2, height * 2);
                        } catch (IOException e) {
                            error += e.getMessage();
                            e.printStackTrace();
                        }
                    }
                } else if (fileFilter == SuffixAwareFilter.SVG) {
                    if (new File(pathSim + name + ".svg").exists() && !overwrite) {
                        int response = JOptionPane.showConfirmDialog(relativeTo, "Overwrite all existing files?",
                                "Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (!(response == JOptionPane.OK_OPTION)) {
                            stop = true;
                        } else {
                            overwrite = true;
                        }
                    }
                    if (!stop) {
                        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
                        Document document = domImpl.createDocument(null, "svg", null);
                        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
                        svgGenerator.setSVGCanvasSize(new Dimension(width, height));
                        chart.draw(svgGenerator, new Rectangle(width, height));
                        boolean useCSS = true; // we want to use CSS style attribute
                        try (Writer out = new OutputStreamWriter(new FileOutputStream(pathSim + name + ".svg"),
                                StandardCharsets.UTF_8)) {
                            svgGenerator.stream(out, useCSS);
                        } catch (IOException e) {
                            error += e.getMessage();
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (error.trim().length() == 0) {
                MyPopUp.getInstance().show("Image export", "Exports of images was successful!");
            } else {
                MyPopUp.getInstance().show("Error during image export", error);
            }
        }
    }

    public SaveDialog(int format, int dataType) {
        this(format, dataType, null, MainWindow.getInstance().getFrame(), null);
    }

    private void prepare(int format) {
        chooser = new JFileChooser(ConnectionSettings.getInstance().getFileSaveDirectory());
        chooser.setAcceptAllFileFilterUsed(false);
        if ((format & FORMAT_SBML) == FORMAT_SBML) {
            chooser.addChoosableFileFilter(SuffixAwareFilter.SBML);
        }
        if ((format & FORMAT_MO) == FORMAT_MO) {
            chooser.addChoosableFileFilter(SuffixAwareFilter.MO);
        }
        if ((format & FORMAT_GRAPHML) == FORMAT_GRAPHML) {
            chooser.addChoosableFileFilter(SuffixAwareFilter.GRAPH_ML);
        }
        if ((format & FORMAT_CSML) == FORMAT_CSML) {
            chooser.addChoosableFileFilter(SuffixAwareFilter.CSML);
        }
        if ((format & FORMAT_PDF) == FORMAT_PDF) {
            chooser.addChoosableFileFilter(SuffixAwareFilter.PDF);
        }
        if ((format & FORMAT_SVG) == FORMAT_SVG) {
            chooser.addChoosableFileFilter(SuffixAwareFilter.SVG);
        }
        if ((format & FORMAT_PNML) == FORMAT_PNML) {
            chooser.addChoosableFileFilter(SuffixAwareFilter.PNML);
        }
        if ((format & FORMAT_TXT) == FORMAT_TXT) {
            chooser.addChoosableFileFilter(SuffixAwareFilter.GRAPH_TEXT_FILE);
        }
        if ((format & FORMAT_CSV) == FORMAT_CSV) {
            chooser.addChoosableFileFilter(SuffixAwareFilter.CSV_RESULT);
        }
        if ((format & FORMAT_PNG) == FORMAT_PNG) {
            chooser.addChoosableFileFilter(SuffixAwareFilter.PNG);
        }
        if ((format & FORMAT_YAML) == FORMAT_YAML) {
            chooser.addChoosableFileFilter(SuffixAwareFilter.YAML);
        }
    }

    private void getCorrectFile(String ending) {
        String filePath = file.getPath();
        if (!filePath.endsWith("." + ending)) {
            file = new File(file.getAbsolutePath() + "." + ending);
        }
    }

    private void write(String simId) throws HeadlessException, XMLStreamException, IOException, TranscoderException,
            InvalidIDException, VoidRepositoryException {
        if (fileFilter == SuffixAwareFilter.SBML) {
            writeSBML();
        } else if (fileFilter == SuffixAwareFilter.GRAPH_ML) {
            writeGraphML();
        } else if (fileFilter == SuffixAwareFilter.MO) {
            writeMO();
        } else if (fileFilter == SuffixAwareFilter.GRAPH_TEXT_FILE) {
            writeGraphTextFile();
        } else if (fileFilter == SuffixAwareFilter.CSV_RESULT) {
            writeCSV(simId);
        } else if (fileFilter == SuffixAwareFilter.PNML) {
            writePNML();
        } else if (fileFilter == SuffixAwareFilter.CSML) {
            writeCSML();
        } else if (fileFilter == SuffixAwareFilter.PNG) {
            writePNG();
        } else if (fileFilter == SuffixAwareFilter.SVG) {
            writeSVG();
        } else if (fileFilter == SuffixAwareFilter.YAML) {
            writeYAML();
        }
    }

    private void writeYAML() throws IOException {
        getCorrectFile(SuffixAwareFilter.YAML.getExtension());
        String exportPath = chooser.getSelectedFile().getPath();
        if (!exportPath.contains(".yaml")) {
            exportPath = exportPath + ".yaml";
        }
        if (dataType == DATA_TYPE_VISUALIZATION_SETTINGS) {
            InputStream internYaml = getClass().getClassLoader().getResourceAsStream("NodeProperties.yaml");
            File exportFile = new File(exportPath);
            FileOutputStream exportYaml = new FileOutputStream(exportFile);
            byte[] buffer = new byte[4096];
            int bytesRead = internYaml.read(buffer);
            while (bytesRead != -1) {
                exportYaml.write(buffer, 0, bytesRead);
                bytesRead = internYaml.read(buffer);
            }
            internYaml.close();
            exportYaml.close();
            MyPopUp.getInstance().show("Information", SuffixAwareFilter.YAML + " File exported");
            MainWindow.getInstance().setLoadedYaml(exportPath);
        } else if (dataType == DATA_TYPE_TRANSFORMATION_RULES) {
            List<Rule> rules = RuleManager.getInstance().getRules();
            String result = YamlRuleWriter.writeRules(new FileOutputStream(file), rules);
            if (result.length() > 0) {
                MyPopUp.getInstance().show("Error", SuffixAwareFilter.YAML + "an error occurred: " + result);
            } else {
                MyPopUp.getInstance().show("YAML Rules", rules.size() + " rules were written to file!");
            }
        }
    }

    private void writeSVG() throws IOException, TranscoderException {
        getCorrectFile(SuffixAwareFilter.SVG.getExtension());
        if (c != null) {
            ImageExport.exportPic(c, new Rectangle(c.getWidth(), c.getHeight()), file, ImageExport.IMAGE_TYPE_SVG);
        }
    }

    private void writePNG() throws IOException, TranscoderException {
        getCorrectFile(SuffixAwareFilter.PNG.getExtension());
        if (c != null) {
            ImageExport.exportPic(c, new Rectangle(c.getWidth(), c.getHeight()), file, ImageExport.IMAGE_TYPE_PNG);
        }
    }

    private void writeCSML() throws FileNotFoundException {
        getCorrectFile(SuffixAwareFilter.CSML.getExtension());
        new GONoutput(new FileOutputStream(file), new GraphInstance().getPathway());
        MyPopUp.getInstance().show("Information", SuffixAwareFilter.CSML + " File saved");
    }

    private void writePNML() throws InvalidIDException, VoidRepositoryException {
        GraphContainer con = GraphContainer.getInstance();
        MainWindow w = MainWindow.getInstance();
        if (!con.isPetriView()) {
            // maybe translate to PN first
        } else {
            getCorrectFile(SuffixAwareFilter.PNML.getExtension());
            ArrayList<PNArc> edgeList = new ArrayList<>();
            for (BiologicalEdgeAbstract bea : con.getPathway(w.getCurrentPathway()).getAllEdges()) {
                if (bea instanceof PNArc) {
                    edgeList.add((PNArc) bea);
                }
            }
            ArrayList<Place> nodeList = new ArrayList<>();
            ArrayList<Transition> transitionList = new ArrayList<>();
            for (BiologicalNodeAbstract bna : con.getPathway(w.getCurrentPathway()).getAllGraphNodes()) {
                if (bna instanceof Place) {
                    nodeList.add((Place) bna);
                } else if (bna instanceof Transition) {
                    transitionList.add((Transition) bna);
                }
            }
            // only output on file system possible
            PNMLOutput pnmlOutput = new PNMLOutput(file, edgeList, nodeList, transitionList);
            String result = pnmlOutput.generatePNMLDocument();
            if (result.length() > 0) {
                MyPopUp.getInstance().show("Error", SuffixAwareFilter.PNML + "an error occured: " + result);
            } else {
                MyPopUp.getInstance().show("Information", "Saving was successful!");
            }
        }
    }

    private void writeCSV(String simId) throws FileNotFoundException {
        getCorrectFile(SuffixAwareFilter.CSV_RESULT.getExtension());
        // TODO adjust if BN holds PN
        String result = new CSVWriter().write(new FileOutputStream(file), new GraphInstance().getPathway(), simId);
        if (result.length() > 0) {
            MyPopUp.getInstance().show("Error", SuffixAwareFilter.CSV_RESULT + result);
        } else {
            MyPopUp.getInstance().show("Information", "Saving was successful!");
        }
    }

    private void writeGraphTextFile() throws FileNotFoundException {
        getCorrectFile(SuffixAwareFilter.GRAPH_TEXT_FILE.getExtension());
        String result = new GraphTextWriter().write(new FileOutputStream(file), new GraphInstance().getPathway());
        if (result.length() > 0) {
            MyPopUp.getInstance().show("Error", SuffixAwareFilter.GRAPH_TEXT_FILE + result);
        } else {
            MyPopUp.getInstance().show("Information", "Saving was successful!");
        }
    }

    private void writeMO() throws FileNotFoundException {
        getCorrectFile(SuffixAwareFilter.MO.getExtension());
        new MOoutput(new FileOutputStream(file), new GraphInstance().getPathway(), false);
        String path_colored = file.getAbsolutePath();
        if (path_colored.endsWith(".mo")) {
            path_colored = path_colored.substring(0, path_colored.length() - 3);
        }
        new MOoutput(new FileOutputStream(path_colored + "_colored.mo"), new GraphInstance().getPathway(), true);
        MyPopUp.getInstance().show("Modelica export", SuffixAwareFilter.MO + " File saved");
        // JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(), moDescription + " File saved");
    }

    private void writeGraphML() {
        getCorrectFile(SuffixAwareFilter.GRAPH_ML.getExtension());
        new GraphMLWriter(file).write(GraphInstance.getPathwayStatic());
        MyPopUp.getInstance().show("Information", SuffixAwareFilter.GRAPH_ML + " File saved");
    }

    private void writeSBML() throws FileNotFoundException, XMLStreamException {
        getCorrectFile(SuffixAwareFilter.SBML.getExtension());
        // create a sbmlOutput object
        // SBMLoutputNoWS sbmlOutput = new SBMLoutputNoWS(file, new GraphInstance().getPathway());
        // //if (sbmlOutput.generateSBMLDocument())
        // JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(), sbmlDescription + sbmlOutput.generateSBMLDocument());
        if (GraphInstance.getPathwayStatic().getFile() == null) {
            GraphInstance.getPathwayStatic().setFile(file);
        }
        // TODO creation of FileOutputStream overrides file already, even without call
        // write() method. Document should be generated first, if no errors thrown, then create FOS and write to file.
        JSBMLOutput jsbmlOutput = new JSBMLOutput(new FileOutputStream(file), new GraphInstance().getPathway());
        String out = jsbmlOutput.generateSBMLDocument();
        if (out.length() > 0) {
            MyPopUp.getInstance().show("Error", out);
        } else {
            GraphContainer.getInstance().renamePathway(GraphInstance.getPathwayStatic(), file.getName());
            GraphInstance.getPathwayStatic().setName(file.getName());
            GraphInstance.getPathwayStatic().setTitle(file.getName());
            MainWindow.getInstance().renameSelectedTab(file.getName());
            MyPopUp.getInstance().show("JSbml export", "Saving was successful!");
        }
        // else
        // JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(), sbmlDescription + " File not saved");
    }
}
