package io;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.SystemUtils;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNEdge;
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
import io.graphML.SaveGraphML;
import moOutput.MOoutput;
import util.ImageExport;
import xmlOutput.sbml.JSBMLoutput;
import xmlOutput.sbml.PNMLOutput;
import xmlOutput.sbml.VAMLoutput;

public class SaveDialog {

	private boolean sbmlBool = true;
	private boolean graphMLBool = true;
	private boolean moBool = true;
	private boolean gonBool = true;
	private boolean vaBool = true;
	private boolean txtBool = true;
	private boolean itxtBool = true;
	private boolean pnmlBool = true;
	private boolean csvBool = true;
	private boolean pngBool = true;
	private boolean yamlBool = true;
	private boolean ymlBool = true;
	private boolean svgBool = true;
	private boolean pdfBool = true;

	private String fileFormat;
	private File file;

	private String sbmlDescription = "System Biology Markup Language (*.sbml)";
	private String sbml = "sbml";

	private String pnmlDescription = "Petri Net Markup Language (*.pnml)";
	private String pnml = "pnml";

	private String vamlDescription = "VANESA Markup Language (*.vaml)";
	private String vaml = "vaml";

	private String graphMlDescription = "Graph Markup Language (*.graphml)";
	private String graphMl = "graphml";

	private String moDescription = "Modelica Model (*.mo)";
	private String mo = "mo";

	private String csmlDescription = "Cell Illustrator (*.csml)";
	private String csml = "csml";

	private String txtDescription = "Graph Text File (*.txt)";
	private String txt = "txt";

	//private String irinaDescription = "Irina Export File (*.itxt)";
	//private String irinaTxt = "itxt";

	private String csvDescription = "CSV Result Export (*.csv)";
	private String csv = "csv";

	private String pngDescription = "PNG Image (*.png)";
	private String png = "png";

	private String svgDescription = "SVG Image (*.svg)";
	private String svg = "svg";

	private String yamlDescription = "YAML File (*.yaml)";
	private String yaml = "yaml";

	private String pdfDescription = "PDF File (*.pdf)";
	private String pdf = "pdf";

	private Component c = null;
	private JFileChooser chooser;

	private String pathWorkingDirectory;

	/*
	 * public SaveDialog(boolean sbml) { this(sbml?1:1); }//
	 */
	// use power of 2
	public static int FORMAT_SBML = 1;
	public static int FORMAT_GRAPHML = 2;
	public static int FORMAT_MO = 4;
	public static int FORMAT_GON = 8;
	public static int FORMAT_VA = 16;
	public static int FORMAT_TXT = 32;
	public static int FORMAT_ITXT = 64;
	public static int FORMAT_PNML = 128;
	public static int FORMAT_CSV = 256;
	public static int FORMAT_PNG = 512;
	public static int FORMAT_YAML = 1024;
	public static int FORMAT_SVG = 2048;
	public static int FORMAT_PDF = 4096;

	public SaveDialog(int format, Component c, Component relativeTo, String simId) {

		if (relativeTo == null) {
			relativeTo = MainWindow.getInstance().getFrame();
		}
		this.prerapre(format);
		this.c = c;

		int option = chooser.showSaveDialog(relativeTo);
		if (option == JFileChooser.APPROVE_OPTION) {
			// Save path to settings.xml
			File fileDir = chooser.getCurrentDirectory();
			XMLConfiguration xmlSettings = null;
			File f = new File(pathWorkingDirectory + File.separator + "settings.xml");
			try {
				if (f.exists()) {
					xmlSettings = new XMLConfiguration(pathWorkingDirectory + File.separator + "settings.xml");
				} else {
					xmlSettings = new XMLConfiguration();
					xmlSettings.setFileName(pathWorkingDirectory + File.separator + "settings.xml");
				}
				xmlSettings.setProperty("SaveDialog-Path", fileDir.getAbsolutePath());
				xmlSettings.save();
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}

			fileFormat = chooser.getFileFilter().getDescription();
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
				} catch (FileNotFoundException | HeadlessException | XMLStreamException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public SaveDialog(int format, List<JFreeChart> charts, boolean directoryOnly, Component relativeTo) {
		if (relativeTo == null) {
			relativeTo = MainWindow.getInstance().getFrame();
		}
		String error = "";
		this.prerapre(format);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int option = chooser.showSaveDialog(relativeTo);
		if (option == JFileChooser.APPROVE_OPTION) {
			// Save path to settings.xml
			File fileDir = chooser.getCurrentDirectory();
			XMLConfiguration xmlSettings = null;
			File f = new File(pathWorkingDirectory + File.separator + "settings.xml");
			try {
				if (f.exists()) {
					xmlSettings = new XMLConfiguration(pathWorkingDirectory + File.separator + "settings.xml");
				} else {
					xmlSettings = new XMLConfiguration();
					xmlSettings.setFileName(pathWorkingDirectory + File.separator + "settings.xml");
				}
				xmlSettings.setProperty("SaveDialog-Path", fileDir.getAbsolutePath());
				// System.out.println(fileDir.getAbsolutePath());
				xmlSettings.save();
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}

			fileFormat = chooser.getFileFilter().getDescription();

			file = chooser.getSelectedFile();
			ConnectionSettings.setFileDirectory(file.getAbsolutePath());
			// System.out.println(file.getAbsolutePath());

			String pathSim;
			pathSim = file.getAbsolutePath() + File.separator;
			File dirSim = new File(pathSim);
			if (dirSim.isDirectory()) {
			} else {
			}

			int width = 320;
			int height = 200;
			boolean overwrite = false;
			boolean stop = false;
			for (JFreeChart chart : charts) {
				String name = chart.getTitle().getText();

				// PDF
				if (fileFormat.equals(pdfDescription)) {
					PDFDocument pdfDoc = new PDFDocument();
					// pdfDoc.setTitle("PDFBarChartDemo1");
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

				} else if (fileFormat.equals(pngDescription)) {
					// PNG
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
							ChartUtils.saveChartAsPNG(new File(pathSim + name + ".png"), chart, width * 2,
									height * 2);
						} catch (IOException e) {
							error+=e.getMessage();
							e.printStackTrace();
						}
					}

				} else if (fileFormat.equals(svgDescription)) {
					// SVG
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

						Writer out;
						try {
							out = new OutputStreamWriter(new FileOutputStream(new File(pathSim + name + ".svg")),
									"UTF-8");
							svgGenerator.stream(out, useCSS);
							out.close();
						} catch (IOException e) {
							error+=e.getMessage();
							e.printStackTrace();
						}
					}
				}
			}
			if(error.trim().length() == 0){
				MyPopUp.getInstance().show("Image export", "Exports of images was successful!");
			}else{
				MyPopUp.getInstance().show("Error during image export", error);
			}
		}
	}

	public SaveDialog(int format) {
		this(format, null, MainWindow.getInstance().getFrame(), null);
	}
	
	private void prerapre(int format) {

		// Get working directory
		if (SystemUtils.IS_OS_WINDOWS) {
			pathWorkingDirectory = System.getenv("APPDATA");
		} else {
			pathWorkingDirectory = System.getenv("HOME");
		}
		pathWorkingDirectory += File.separator + "vanesa";
		sbmlBool = (format & FORMAT_SBML) == FORMAT_SBML;
		moBool = (format & FORMAT_MO) == FORMAT_MO;
		graphMLBool = (format & FORMAT_GRAPHML) == FORMAT_GRAPHML;
		gonBool = (format & FORMAT_GON) == FORMAT_GON;
		vaBool = (format & FORMAT_VA) == FORMAT_VA;
		txtBool = (format & FORMAT_TXT) == FORMAT_TXT;
		itxtBool = (format & FORMAT_ITXT) == FORMAT_ITXT;
		pnmlBool = (format & FORMAT_PNML) == FORMAT_PNML;
		csvBool = (format & FORMAT_CSV) == FORMAT_CSV;
		pngBool = (format & FORMAT_PNG) == FORMAT_PNG;
		svgBool = (format & FORMAT_SVG) == FORMAT_SVG;
		yamlBool = (format & FORMAT_YAML) == FORMAT_YAML;
		pdfBool = (format & FORMAT_PDF) == FORMAT_PDF;

		if (ConnectionSettings.getFileDirectory() != null) {
			chooser = new JFileChooser(ConnectionSettings.getFileDirectory());
		} else {
			// Use the path that was used last time
			String path = "";

			try {
				XMLConfiguration xmlSettings = new XMLConfiguration(
						pathWorkingDirectory + File.separator + "settings.xml");
				path = xmlSettings.getString("SaveDialog-Path");
			} catch (ConfigurationException e) {
				System.out
						.println("There is probably no " + pathWorkingDirectory + File.separator + "settings.xml yet.");
			}
			chooser = new JFileChooser(path);
		}

		chooser.setAcceptAllFileFilterUsed(false);

		if (sbmlBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(sbml, sbmlDescription));
		}
		/*
		 * if (itxtBool) chooser.addChoosableFileFilter(new MyFileFilter(irinaTxt,
		 * irinaDescription));
		 */
		if (moBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(mo, moDescription));
		}
		if (graphMLBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(graphMl, graphMlDescription));
		}
		if (gonBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(csml, csmlDescription));
		}
		if (pdfBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(pdf, pdfDescription));
		}
		if (svgBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(svg, svgDescription));
		}
		if (pnmlBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(pnml, pnmlDescription));
		}
		if (vaBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(vaml, vamlDescription));
		}
		if (txtBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(txt, txtDescription));
		}
		if (csvBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(csv, csvDescription));
		}
		if (pngBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(png, pngDescription));
		}
		if (yamlBool) {
			chooser.addChoosableFileFilter(new MyFileFilter(yaml, yamlDescription));
		}
	}

	private void getCorrectFile(String ending) {

		String extension = file.getPath();
		int i = extension.lastIndexOf('.');
		if (i > 0 && i < extension.length() - 1) {
		} else {
			file = new File(file.getAbsolutePath() + "." + ending);
		}
	}


	private void write(String simId) throws FileNotFoundException, HeadlessException, XMLStreamException {

		ConnectionSettings.setFileDirectory(file.getAbsolutePath());
		if (fileFormat.equals(sbmlDescription)) {
			getCorrectFile(sbml);
			// create a sbmlOutput object
			// SBMLoutputNoWS sbmlOutput = new SBMLoutputNoWS(file, new
			// GraphInstance()
			// .getPathway());
			// //if (sbmlOutput.generateSBMLDocument())
			// JOptionPane.showMessageDialog(
			// MainWindowSingelton.getInstance(), sbmlDescription
			// + sbmlOutput.generateSBMLDocument());
			JSBMLoutput jsbmlOutput = new JSBMLoutput(new FileOutputStream(file), new GraphInstance().getPathway());

			String out = jsbmlOutput.generateSBMLDocument();
			if (out.length() > 0) {
				MyPopUp.getInstance().show("Error", out);
			} else {
				// System.out.println(file.getName());
				GraphContainer.getInstance().renamePathway(GraphInstance.getPathwayStatic(), file.getName());
				GraphInstance.getPathwayStatic().setName(file.getName());
				GraphInstance.getPathwayStatic().setTitle(file.getName());
				MainWindow.getInstance().renameSelectedTab(file.getName());
				MyPopUp.getInstance().show("JSbml export", "Saving was successful!");
			}

			// else
			// JOptionPane.showMessageDialog(
			// MainWindowSingelton.getInstance(), sbmlDescription
			// + " File not saved");
		} else if (fileFormat.equals(graphMlDescription)) {
			getCorrectFile(graphMl);
			new SaveGraphML(new FileOutputStream(file));
			MyPopUp.getInstance().show("Information", graphMlDescription + " File saved");
		} else if (fileFormat.equals(moDescription)) {
			getCorrectFile(mo);
			new MOoutput(new FileOutputStream(file), new GraphInstance().getPathway(), false);
			String path_colored = file.getAbsolutePath();
			if (path_colored.endsWith(".mo")) {
				path_colored = path_colored.substring(0, path_colored.length() - 3);
			}

			new MOoutput(new FileOutputStream(new File(path_colored + "_colored.mo")), new GraphInstance().getPathway(),
					true);

			MyPopUp.getInstance().show("Modelica export", moDescription + " File saved");
			// JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(),
			// moDescription + " File saved");

		} else if (fileFormat.equals(txtDescription)) {
			getCorrectFile(txt);
			new GraphTextWriter(new FileOutputStream(file), new GraphInstance().getPathway());
			MyPopUp.getInstance().show("Information", txtDescription + " File saved");
		} else if (fileFormat.equals(csvDescription)) {
			getCorrectFile(csv);
			// TODO adjust if BN holds PN
			String result = new CSVWriter().write(new FileOutputStream(file), new GraphInstance().getPathway(), simId);

			if (result.length() > 0) {
				MyPopUp.getInstance().show("Error", csvDescription + result);
			} else {
				MyPopUp.getInstance().show("csv", "Saving was successful!");
			}

		} else if (fileFormat.equals(pnmlDescription)) {
			GraphContainer con = GraphContainer.getInstance();
			MainWindow w = MainWindow.getInstance();
			if (!con.isPetriView()) {
				// maybe translate to PN first
			} else {

				getCorrectFile(pnml);
				BiologicalEdgeAbstract bea;
				ArrayList<PNEdge> edgeList = new ArrayList<PNEdge>();
				Iterator<BiologicalEdgeAbstract> itEdge = con.getPathway(w.getCurrentPathway()).getAllEdges()
						.iterator();
				while (itEdge.hasNext()) {
					bea = itEdge.next();
					if (bea instanceof PNEdge) {
						edgeList.add((PNEdge) bea);
					}

				}
				BiologicalNodeAbstract bna;
				ArrayList<Place> nodeList = new ArrayList<Place>();
				ArrayList<Transition> transitionList = new ArrayList<Transition>();
				Iterator<BiologicalNodeAbstract> itNode = con.getPathway(w.getCurrentPathway()).getAllGraphNodes()
						.iterator();

				while (itNode.hasNext()) {
					bna = itNode.next();
					if (bna instanceof Place) {
						nodeList.add((Place) bna);
					} else if (bna instanceof Transition) {
						transitionList.add((Transition) bna);
					}
				}
				// only output on file system possible
				PNMLOutput pnmlOutput = new PNMLOutput(file, edgeList, nodeList, transitionList);

				try {
					String result = pnmlOutput.generatePNMLDocument();

					if (result.length() > 0) {
						MyPopUp.getInstance().show("Error", pnmlDescription + "an error occured: " + result);
					} else {
						MyPopUp.getInstance().show("PNML export", "Saving was successful!");
					}
				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (InvalidIDException e) {
					e.printStackTrace();
				} catch (VoidRepositoryException e) {
					e.printStackTrace();
				}

			}
		} else if (fileFormat.equals(csmlDescription)) {
			getCorrectFile(csml);
			new GONoutput(new FileOutputStream(file), new GraphInstance().getPathway());
			MyPopUp.getInstance().show("Information", csmlDescription + " File saved");
		} else if (fileFormat.equals(vamlDescription)) {
			getCorrectFile(vaml);
			try {
				new VAMLoutput(new FileOutputStream(file), new GraphInstance().getPathway());
				MyPopUp.getInstance().show("Information", vamlDescription + " File saved");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (fileFormat.equals(pngDescription)) {
			getCorrectFile(png);
			if (c != null) {
				try {
					ImageExport.exportPic(c, new Rectangle(c.getWidth(), c.getHeight()), file,
							ImageExport.IMAGE_TYPE_PNG);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TranscoderException e) {
					e.printStackTrace();
				}
			}
		} else if (fileFormat.equals(svgDescription)) {
			getCorrectFile(svg);
			if (c != null) {
				try {
					ImageExport.exportPic(c, new Rectangle(c.getWidth(), c.getHeight()), file,
							ImageExport.IMAGE_TYPE_SVG);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TranscoderException e) {
					e.printStackTrace();
				}
			}
		} else if (fileFormat.equals(yamlDescription)) {
			getCorrectFile(yaml);
			try {
				String exportPath = chooser.getSelectedFile().getPath();

				if (exportPath.contains(".yaml") == false) {
					exportPath = exportPath + ".yaml";
				}

				InputStream internYaml = getClass().getClassLoader()
						.getResourceAsStream("resource/NodeProperties.yaml");
				FileOutputStream exportYaml = null;
				File exportFile = new File(exportPath);
				exportYaml = new FileOutputStream(exportFile);
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				bytesRead = internYaml.read(buffer);
				while (bytesRead != -1) {
					exportYaml.write(buffer, 0, bytesRead);
					bytesRead = internYaml.read(buffer);
				}
				;
				internYaml.close();
				exportYaml.close();
				MyPopUp.getInstance().show("Information", yamlDescription + " File exported");
				MainWindow.getInstance().setLoadedYaml(exportPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
