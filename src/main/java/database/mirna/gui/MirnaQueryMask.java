package database.mirna.gui;

import api.VanesaApi;
import api.payloads.Response;
import api.payloads.dbMirna.*;
import biologicalElements.Pathway;
import biologicalObjects.edges.Expression;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.MIRNA;
import com.fasterxml.jackson.core.type.TypeReference;
import database.gui.QueryMask;
import database.mirna.MirnaStatistics;
import graph.CreatePathway;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MyPopUp;
import gui.eventhandlers.TextfeldColorChanger;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.geom.Point2D;

public class MirnaQueryMask extends QueryMask {
    private final JTextField name;
    private final JTextField accession;
    private final JTextField sequences;
    private final JTextField gene;
    private final JCheckBox hsaOnly;
    private final JRadioButton sources;
    private final JRadioButton targets;
    private final JRadioButton sourcesAndTargets;

    public MirnaQueryMask() {
        name = new JTextField(20);
        name.setText("hsa-miR-15a");
        name.addFocusListener(new TextfeldColorChanger());
        gene = new JTextField(20);
        gene.setText("");
        gene.addFocusListener(new TextfeldColorChanger());
        accession = new JTextField(20);
        accession.setText("");
        accession.addFocusListener(new TextfeldColorChanger());
        sequences = new JTextField(20);
        sequences.setText("");
        sequences.addFocusListener(new TextfeldColorChanger());
        hsaOnly = new JCheckBox("human only");
        hsaOnly.setSelected(true);
        ButtonGroup typeGroup = new ButtonGroup();
        sources = new JRadioButton("sources");
        targets = new JRadioButton("targets");
        sourcesAndTargets = new JRadioButton("both");
        sourcesAndTargets.setSelected(true);
        typeGroup.add(sources);
        typeGroup.add(targets);
        typeGroup.add(sourcesAndTargets);
        JButton enrichGenes = new JButton("enrich genes");
        enrichGenes.addActionListener(e -> enrichGenes());
        JButton enrichMirnas = new JButton("enrich miRNAs");
        enrichMirnas.addActionListener(e -> enrichMirnas());
        panel.add(new JLabel("miRNA Search Window"), "span 4");
        panel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10, gap 5");
        panel.add(new JLabel(new ImageIcon(imagePath.getPath("dataServer.png"))), "span 2 5");
        panel.add(new JLabel("miRNA name"), "span 2, gap 5 ");
        panel.add(name, "span,wrap,growx ,gap 10");
        panel.add(new JLabel("Gene name"), "span 2, gap 5 ");
        panel.add(gene, "span,wrap,growx ,gap 10");
        panel.add(new JLabel("Accession"), "span 2, gap 5 ");
        panel.add(accession, "span, wrap, growx, gap 10");
        //panel.add(new JLabel("Sequence"),"span 2, gap 5 ");
        //panel.add(sequences,"span, wrap, growx, gap 10");
        panel.add(hsaOnly, "span 2");
        panel.add(sources, "flowx, span, split 3");
        panel.add(targets);
        panel.add(sourcesAndTargets, "wrap");
        panel.add(enrichGenes, "flowx, span, split 3");
        panel.add(enrichMirnas, "wrap");
        addControlButtons();
    }

    @Override
    public String getMaskName() {
        return "miRNA";
    }

    @Override
    protected void reset() {
        name.setText("");
        gene.setText("");
        accession.setText("");
        sequences.setText("");
        hsaOnly.setSelected(true);
    }

    @Override
    protected String search() {
        if (StringUtils.isNotEmpty(getNameInput()) || StringUtils.isNotEmpty(getAccessionInput()) ||
                StringUtils.isNotEmpty(getSequenceInput())) {
            MatureSearchRequestPayload payload = new MatureSearchRequestPayload();
            payload.hsaOnly = isHsaOnly();
            payload.name = getNameInput();
            payload.accession = getAccessionInput();
            payload.sequence = getSequenceInput();
            Response<MatureSearchResponsePayload> response = VanesaApi.postSync("/db_mirna/mature/search", payload,
                    new TypeReference<>() {
                    });
            if (response.hasError()) {
                return response.error;
            }
            handleMatureSearchResults(response.payload);
        } else {
            TargetGeneSearchRequestPayload payload = new TargetGeneSearchRequestPayload();
            payload.hsaOnly = isHsaOnly();
            payload.accession = getGeneInput();
            Response<TargetGeneSearchResponsePayload> response = VanesaApi.postSync("/db_mirna/target_gene/search",
                    payload, new TypeReference<>() {
                    });
            if (response.hasError()) {
                return response.error;
            }
            handleTargetGeneSearchResults(response.payload);
        }
        return null;
    }

    private void handleMatureSearchResults(MatureSearchResponsePayload payload) {
        if (payload.results == null || payload.results.length == 0) {
            showNoEntriesPopUp();
            return;
        }
        MirnaMatureSearchResultWindow resultWindow = new MirnaMatureSearchResultWindow(payload.results);
        resultWindow.show();
        DBMirnaMature[] results = resultWindow.getSelectedValues();
        if (results == null || results.length == 0) {
            return;
        }
        int count = 0;
        for (DBMirnaMature mature : results) {
            GenesTargetedByMatureRequestPayload requestPayload = new GenesTargetedByMatureRequestPayload();
            // TODO split search to search for sources and/or targets, depending on search criterion
            requestPayload.hsaOnly = isHsaOnly();
            requestPayload.name = mature.name;
            Response<GenesTargetedByMatureResponsePayload> response = VanesaApi.postSync(
                    "/db_mirna/target_gene/targeted_by_mature", requestPayload, new TypeReference<>() {
                    });
            if (response.payload != null && response.payload.results != null && response.payload.results.length > 0) {
                count += response.payload.results.length;
                Pathway pw = new CreatePathway("miRNA network for " + mature.name).getPathway();
                MyGraph myGraph = pw.getGraph();
                MIRNA root = new MIRNA(mature.name, mature.name);
                if (mature.sequence != null) {
                    root.setNtSequence(mature.sequence);
                }
                pw.addVertex(root, new Point2D.Double(0, 0));
                for (DBMirnaTargetGene targetGene : response.payload.results) {
                    DNA dna = new DNA(targetGene.accession, targetGene.accession);
                    pw.addVertex(dna, new Point2D.Double(0, 0));
                    Expression e = new Expression("", "", root, dna); // TODO: this isn't expression???
                    e.setDirected(true);
                    pw.addEdge(e);
                }
                myGraph.restartVisualizationModel();
                myGraph.changeToGEMLayout();
                myGraph.fitScaleOfViewer(myGraph.getSatelliteView());
                myGraph.normalCentering();
                MainWindow.getInstance().closeProgressBar();
                MainWindow window = MainWindow.getInstance();
                window.updateOptionPanel();
                window.getFrame().setVisible(true);
            }
            // TODO: show error
        }
        if (count == 0) {
            showNoEntriesPopUp();
        }
    }

    private void showNoEntriesPopUp() {
        MyPopUp.getInstance().show("miRNA Search", "No entries have been found!");
    }

    private void handleTargetGeneSearchResults(TargetGeneSearchResponsePayload payload) {
        if (payload.results == null || payload.results.length == 0) {
            showNoEntriesPopUp();
            return;
        }
        MirnaTargetGeneSearchResultWindow resultWindow = new MirnaTargetGeneSearchResultWindow(payload.results);
        resultWindow.show();
        DBMirnaTargetGene[] results = resultWindow.getSelectedValues();
        if (results == null || results.length == 0) {
            return;
        }
        int count = 0;
        for (DBMirnaTargetGene targetGene : results) {
            Response<MaturesTargetingGeneResponsePayload> response = retrieveMaturesTargetingGene(isHsaOnly(),
                    targetGene.accession);
            if (response.payload != null && response.payload.results != null && response.payload.results.length > 0) {
                count += response.payload.results.length;
                Pathway pw = new CreatePathway("miRNA network for " + targetGene.accession).getPathway();
                MyGraph myGraph = pw.getGraph();
                DNA root = new DNA(targetGene.accession, targetGene.accession);
                pw.addVertex(root, new Point2D.Double(0, 0));
                for (DBMirnaMature mature : response.payload.results) {
                    MIRNA mirna = new MIRNA(mature.name, mature.name);
                    if (mature.sequence != null) {
                        mirna.setNtSequence(mature.sequence);
                    }
                    pw.addVertex(mirna, new Point2D.Double(0, 0));
                    Expression e = new Expression("", "", mirna, root); // TODO: this isn't expression???
                    e.setDirected(true);
                    pw.addEdge(e);
                }
                myGraph.restartVisualizationModel();
                myGraph.changeToGEMLayout();
                myGraph.fitScaleOfViewer(myGraph.getSatelliteView());
                myGraph.normalCentering();
                MainWindow.getInstance().closeProgressBar();
                MainWindow window = MainWindow.getInstance();
                window.updateOptionPanel();
                window.getFrame().setVisible(true);
            }
            // TODO: show error
        }
        if (count == 0) {
            showNoEntriesPopUp();
        }
    }

    public static Response<MaturesTargetingGeneResponsePayload> retrieveMaturesTargetingGene(boolean hsaOnly,
                                                                                             String geneAccession) {
        MaturesTargetingGeneRequestPayload requestPayload = new MaturesTargetingGeneRequestPayload();
        requestPayload.hsaOnly = hsaOnly;
        requestPayload.accession = geneAccession;
        return VanesaApi.postSync("/db_mirna/mature/targeting_gene", requestPayload, new TypeReference<>() {
        });
    }

    public static Response<GenesTargetedByMatureResponsePayload> retrieveGenesTargetedByMature(boolean hsaOnly,
                                                                                               String matureName) {
        GenesTargetedByMatureRequestPayload requestPayload = new GenesTargetedByMatureRequestPayload();
        requestPayload.hsaOnly = hsaOnly;
        requestPayload.name = matureName;
        return VanesaApi.postSync(
                "/db_mirna/target_gene/targeted_by_mature", requestPayload, new TypeReference<>() {
                });
    }

    @Override
    protected boolean doSearchCriteriaExist() {
        return StringUtils.isNotEmpty(getNameInput()) || StringUtils.isNotEmpty(getAccessionInput()) ||
                StringUtils.isNotEmpty(getSequenceInput()) || StringUtils.isNotEmpty(getGeneInput());
    }

    private String getNameInput() {
        return name.getText();
    }

    private String getAccessionInput() {
        return accession.getText();
    }

    private String getSequenceInput() {
        return sequences.getText();
    }

    private String getGeneInput() {
        return gene.getText();
    }

    private boolean isHsaOnly() {
        return hsaOnly.isSelected();
    }

    private boolean isSourcesSelected() {
        return (sourcesAndTargets.isSelected() || sources.isSelected());
    }

    private boolean isTargetsSelected() {
        return (sourcesAndTargets.isSelected() || targets.isSelected());
    }

    private void enrichGenes() {
        MirnaStatistics mirna = new MirnaStatistics(new GraphInstance().getPathway());
        mirna.enrichGenes(isSourcesSelected(), isTargetsSelected(), isHsaOnly());
    }

    private void enrichMirnas() {
        MirnaStatistics mirna = new MirnaStatistics(new GraphInstance().getPathway());
        mirna.enrichMirnas(isSourcesSelected(), isTargetsSelected(), isHsaOnly());
    }

    @Override
    protected void showInfoWindow() {
        String instructions =
                "<html>" +
                        "<h3>The miRNA search window</h3>" +
                        "<ul>" +
                        "<li>Through the miRNA search window you can access microRNA information<br>" +
                        "available in miRBase, miRTarBase, and TarBase.<br>" +
                        "MiRBase is a biological database that acts as an archive of microRNA sequences and<br>" +
                        "annotations and TarBase is a comprehensive database of experimentally supported animal<br>" +
                        "microRNA targets.</li>" +
                        "<li>The search window is a query mask that gives the user the possibility to consult the miRNA<br>" +
                        "database for information of interest.</li>" +
                        "<li>By searching the database for one of the following attributes name, accession or sequence<br>" +
                        "the database will be checked for all pathways that meet the given demands.<br>" +
                        "As a result a list of possible pathways will be displayed to the user. In the following step the<br>" +
                        "user can choose either one or more pathways of interest.</li>" +
                        "</ul>" +
                        "</html>";
        JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(), instructions, "miRNA Information",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
