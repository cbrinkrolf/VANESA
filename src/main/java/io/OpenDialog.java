package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import biologicalElements.Pathway;
import configurations.SettingsManager;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.AsyncTaskExecutor;
import gui.MainWindow;
import gui.PopUpDialog;
import io.graphML.GraphMLReader;
import io.kgml.KGMLReader;
import io.sbml.JSBMLInput;
import io.vaml.VAMLInput;
import petriNet.PetriNetProperties;

public final class OpenDialog {
	private final JFileChooser chooser;

	public OpenDialog(List<SuffixAwareFilter> filters) {
		chooser = new JFileChooser(SettingsManager.getInstance().getFileOpenDirectory());
		chooser.setAcceptAllFileFilterUsed(false);
		for (SuffixAwareFilter filter : filters) {
			chooser.addChoosableFileFilter(filter);
		}
	}

	public void show() {
		int option = chooser.showOpenDialog(MainWindow.getInstance().getFrame());
		if (option == JFileChooser.APPROVE_OPTION) {
			SettingsManager.getInstance().setFileOpenDirectory(chooser.getCurrentDirectory().getAbsolutePath());
			final FileFilter fileFilter = chooser.getFileFilter();
			final File file = chooser.getSelectedFile();
			AsyncTaskExecutor.runUIBlocking("Loading data from file. Please wait a second",
					() -> open(fileFilter, file));
		}
	}

	private void open(final FileFilter fileFilter, final File file) {
		if (fileFilter == null) {
			return;
		}
		if (fileFilter == SuffixAwareFilter.VAML) {
			openVAML(file);
		} else if (fileFilter == SuffixAwareFilter.SBML) {
			openSBML(file);
		} else if (fileFilter == SuffixAwareFilter.GRAPH_TEXT_FILE) {
			openGraphText(file);
		} else if (fileFilter == SuffixAwareFilter.GRAPH_ML) {
			openGraphML(file);
		} else if (fileFilter == SuffixAwareFilter.KGML) {
			openKGML(file);
		} else if (fileFilter == SuffixAwareFilter.VANESA_SIM_RESULT) {
			openSimulationResult(file);
		}
		if (GraphContainer.getInstance().containsPathway()) {
			if (GraphInstance.getPathway().hasGotAtLeastOneElement()) {
				MainWindow.getInstance().updateAllGuiElements();
				MyGraph g = GraphInstance.getMyGraph();
				g.normalCentering();
			}
		}
		MainWindow.getInstance().getFrame().repaint();
	}

	private static void openVAML(File file) {
		try {
			new VAMLInput(file);
		} catch (IOException e) {
			PopUpDialog.getInstance().show("VAML read error.", "Failed to load VAML file.");
			e.printStackTrace();
		}
	}

	private static void openSBML(File file) {
		JSBMLInput jsbmlInput = new JSBMLInput(null);
		String result;
		try {
			result = jsbmlInput.loadSBMLFile(new FileInputStream(file), file);
			if (result.length() > 0) {
				PopUpDialog.getInstance().show("Information", result);
			}
		} catch (FileNotFoundException e) {
			try {
				file = new File(file.getAbsolutePath() + ".sbml");
				result = jsbmlInput.loadSBMLFile(new FileInputStream(file), file);
				if (result.length() > 0) {
					PopUpDialog.getInstance().show("Information", result);
				}
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			PopUpDialog.getInstance().show("Error!", e.getMessage());
			e.printStackTrace();
		}
	}

	private static void openGraphText(File file) {
		try {
			new TxtInput(new FileInputStream(file), file);
		} catch (Exception e) {
			PopUpDialog.getInstance().show("Error!", e.getMessage());
			e.printStackTrace();
		}
	}

	private static void openGraphML(File file) {
		final GraphMLReader reader = new GraphMLReader(file);
		final Pathway pw = reader.read();
		if (reader.hasErrors() || pw == null) {
			PopUpDialog.getInstance().show("Error!", "Failed to load GraphML file.");
		} else {
			pw.setFile(file);
		}
	}

	private static void openKGML(File file) {
		final KGMLReader reader = new KGMLReader(file);
		final Pathway pw = reader.read();
		if (reader.hasErrors() || pw == null) {
			PopUpDialog.getInstance().show("Error!", "Failed to load KGML file.");
		} else {
			pw.setFile(file);
		}
	}

	private static void openSimulationResult(File file) {
		try {
			PetriNetProperties petrinet = GraphInstance.getPathway().getPetriPropertiesNet();
			petrinet.loadVanesaSimulationResult(file);
			GraphContainer con = GraphContainer.getInstance();
			if (con.containsPathway()) {
				if (GraphInstance.getPathway().hasGotAtLeastOneElement()) {
					MainWindow.getInstance().updateAllGuiElements();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			PopUpDialog.getInstance().show("Error!", "Failed to load simulation results file.");
		}

	}
}
