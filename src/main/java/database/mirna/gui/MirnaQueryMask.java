package database.mirna.gui;

import api.payloads.Response;
import api.payloads.dbMirna.*;
import biologicalElements.Pathway;
import biologicalObjects.edges.Expression;
import biologicalObjects.edges.PhysicalInteraction;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.MIRNA;
import database.gui.QueryMask;
import database.mirna.MirnaSearch;
import graph.CreatePathway;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.eventhandlers.TextFieldColorChanger;
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
		name.addFocusListener(new TextFieldColorChanger());
		gene = new JTextField(20);
		gene.setText("");
		gene.addFocusListener(new TextFieldColorChanger());
		accession = new JTextField(20);
		accession.setText("");
		accession.addFocusListener(new TextFieldColorChanger());
		sequences = new JTextField(20);
		sequences.setText("");
		sequences.addFocusListener(new TextFieldColorChanger());
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

		panel.add(new JLabel("miRNA name"), "span 2, gap 5 ");
		panel.add(name, "span,wrap,growx ,gap 10");
		panel.add(new JLabel("Gene name"), "span 2, gap 5 ");
		panel.add(gene, "span,wrap,growx ,gap 10");
		panel.add(new JLabel("Accession"), "span 2, gap 5 ");
		panel.add(accession, "span, wrap, growx, gap 10");
		panel.add(new JLabel("Sequence"), "span 2, gap 5 ");
		panel.add(sequences, "span, wrap, growx, gap 10");
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
		if (StringUtils.isNotEmpty(getNameInput()) || StringUtils.isNotEmpty(getAccessionInput())
				|| StringUtils.isNotEmpty(getSequenceInput())) {
			Response<MatureSearchResponsePayload> response = MirnaSearch.searchMatures(isHsaOnly(), getNameInput(),
					getAccessionInput(), getSequenceInput());
			if (response.hasError()) {
				return response.error;
			}
			handleMatureSearchResults(response.payload);
		} else {
			Response<TargetGeneSearchResponsePayload> response = MirnaSearch.searchTargetGenes(isHsaOnly(),
					getGeneInput());
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
		MirnaMatureSearchResultWindow searchResultWindow = new MirnaMatureSearchResultWindow(payload.results);
		if (!searchResultWindow.show()) {
			return;
		}
		DBMirnaMature[] results = searchResultWindow.getSelectedValues();
		if (results == null || results.length == 0) {
			return;
		}
		int count = 0;
		for (DBMirnaMature mature : results) {
			Pathway pw = new CreatePathway("miRNA network for " + mature.name).getPathway();
			MIRNA root = new MIRNA(mature.name, mature.name, pw);
			if (mature.sequence != null) {
				root.setNtSequence(mature.sequence);
			}
			if (targets.isSelected() || sourcesAndTargets.isSelected()) {
				Response<MatureTargetGenesResponsePayload> response = MirnaSearch.retrieveMatureTargetGenes(isHsaOnly(),
						mature.name);
				if (response.payload != null && response.payload.results != null
						&& response.payload.results.length > 0) {
					count += response.payload.results.length;
					pw.addVertex(root, new Point2D.Double(0, 0));
					for (DBMirnaTargetGene targetGene : response.payload.results) {
						String label = targetGene.getAccession();
						DNA dna = new DNA(label, targetGene.name != null ? targetGene.name : label, pw);
						pw.addVertex(dna, new Point2D.Double(0, 0));
						PhysicalInteraction e = new PhysicalInteraction("", "", root, dna);
						e.setDirected(true);
						pw.addEdge(e);
					}
				} else if (response.hasError()) {
					// TODO: show errors
				}
			}
			if (sources.isSelected() || sourcesAndTargets.isSelected()) {
				Response<MatureSourceGenesResponsePayload> response = MirnaSearch.retrieveMatureSourceGenes(isHsaOnly(),
						mature.name);
				if (response.payload != null && response.payload.results != null
						&& response.payload.results.length > 0) {
					count += response.payload.results.length;
					pw.addVertex(root, new Point2D.Double(0, 0));
					for (DBMirnaSourceGene sourceGene : response.payload.results) {
						String label = sourceGene.getAccession();
						DNA dna = new DNA(label, sourceGene.name != null ? sourceGene.name : label, pw);
						pw.addVertex(dna, new Point2D.Double(0, 0));
						Expression e = new Expression("", "", dna, root);
						e.setDirected(true);
						pw.addEdge(e);
					}
				} else if (response.hasError()) {
					// TODO: show errors
				}
			}
			MyGraph myGraph = pw.getGraph();
			myGraph.restartVisualizationModel();
			myGraph.changeToGEMLayout();
			myGraph.fitScaleOfViewer(myGraph.getSatelliteView());
			myGraph.normalCentering();
			MainWindow.getInstance().closeProgressBar();
			MainWindow window = MainWindow.getInstance();
			window.updateOptionPanel();
			window.getFrame().setVisible(true);
		}
		if (count == 0) {
			showNoEntriesPopUp();
		}
	}

	private void showNoEntriesPopUp() {
		PopUpDialog.getInstance().show("miRNA Search", "No entries have been found!");
	}

	private void handleTargetGeneSearchResults(TargetGeneSearchResponsePayload payload) {
		if (payload.results == null || payload.results.length == 0) {
			showNoEntriesPopUp();
			return;
		}
		MirnaTargetGeneSearchResultWindow searchResultWindow = new MirnaTargetGeneSearchResultWindow(payload.results);
		if (!searchResultWindow.show()) {
			return;
		}
		DBMirnaTargetGene[] results = searchResultWindow.getSelectedValues();
		if (results == null || results.length == 0) {
			return;
		}
		int count = 0;
		for (DBMirnaTargetGene targetGene : results) {
			Response<TargetGeneMaturesResponsePayload> response = MirnaSearch.retrieveTargetGeneMatures(isHsaOnly(),
					targetGene.name);
			if (response.payload != null && response.payload.results != null && response.payload.results.length > 0) {
				count += response.payload.results.length;
				Pathway pw = new CreatePathway("miRNA network for " + targetGene.name).getPathway();
				MyGraph myGraph = pw.getGraph();
				String label = targetGene.getAccession();
				DNA root = new DNA(label, targetGene.name != null ? targetGene.name : label, pw);
				pw.addVertex(root, new Point2D.Double(0, 0));
				for (DBMirnaMature mature : response.payload.results) {
					MIRNA mirna = new MIRNA(mature.name, mature.name, pw);
					if (mature.sequence != null) {
						mirna.setNtSequence(mature.sequence);
					}
					pw.addVertex(mirna, new Point2D.Double(0, 0));
					PhysicalInteraction e = new PhysicalInteraction("", "", mirna, root);
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

	@Override
	protected boolean doSearchCriteriaExist() {
		return StringUtils.isNotEmpty(getNameInput()) || StringUtils.isNotEmpty(getAccessionInput())
				|| StringUtils.isNotEmpty(getSequenceInput()) || StringUtils.isNotEmpty(getGeneInput());
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
		Pathway pw = GraphInstance.getPathway();
		if (pw == null) {
			PopUpDialog.getInstance().show("Error", "Please create a network first!");
			return;
		}
		MirnaSearch.enrichGenes(pw, isSourcesSelected(), isTargetsSelected(), isHsaOnly());
	}

	private void enrichMirnas() {
		Pathway pw = GraphInstance.getPathway();
		if (pw == null) {
			PopUpDialog.getInstance().show("Error", "Please create a network first!");
			return;
		}
		MirnaSearch.enrichMirnas(pw, isSourcesSelected(), isTargetsSelected(), isHsaOnly());
	}

	@Override
	protected void showInfoWindow() {
		String instructions = "<html>" + "<h3>The miRNA search window</h3>" + "<ul>"
				+ "<li>Through the miRNA search window you can access micro-RNA information<br>"
				+ "available in miRBase, miRTarBase, and TarBase.<br>"
				+ "miRBase is a biological database that acts as an archive of miRNA sequences and<br>"
				+ "annotations and TarBase is a comprehensive database of experimentally supported animal<br>"
				+ "microRNA targets.</li>"
				+ "<li>The search window is a query mask that gives the user the possibility to consult the miRNA<br>"
				+ "database for information of interest.</li>"
				+ "<li>By searching the database for one of the following attributes name, accession or sequence<br>"
				+ "the database will be checked for all pathways that meet the given demands.<br>"
				+ "As a result a list of possible pathways will be displayed to the user. In the following step the<br>"
				+ "user can choose either one or more pathways of interest.</li>" + "</ul>" + "</html>";
		JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(), instructions, "miRNA Information",
				JOptionPane.INFORMATION_MESSAGE);
	}
}
