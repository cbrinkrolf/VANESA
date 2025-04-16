package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import biologicalElements.Pathway;
import configurations.Workspace;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.AsyncTaskExecutor;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.simulation.SimMenu;
import io.graphML.GraphMLReader;
import io.kgml.KGMLReader;
import io.pnResult.PNSimulationResultCSVReader;
import io.sbml.JSBMLInput;
import io.vaml.VAMLInput;
import petriNet.PetriNetProperties;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;

public final class OpenDialog {
	private final JFileChooser chooser;

	public OpenDialog(SuffixAwareFilter... filters) {
		chooser = new JFileChooser(Workspace.getCurrentSettings().getFileOpenDirectory());
		chooser.setAcceptAllFileFilterUsed(false);
		for (SuffixAwareFilter filter : filters) {
			chooser.addChoosableFileFilter(filter);
		}
	}

	public OpenDialog(List<SuffixAwareFilter> filters) {
		chooser = new JFileChooser(Workspace.getCurrentSettings().getFileOpenDirectory());
		chooser.setAcceptAllFileFilterUsed(false);
		for (SuffixAwareFilter filter : filters) {
			chooser.addChoosableFileFilter(filter);
		}
	}

	public void show() {
		int option = chooser.showOpenDialog(MainWindow.getInstance().getFrame());
		if (option == JFileChooser.APPROVE_OPTION) {
			Workspace.getCurrentSettings().setFileOpenDirectory(chooser.getCurrentDirectory().getAbsolutePath());
			openUIBlocking(chooser.getFileFilter(), chooser.getSelectedFile());
		}
	}

	public static void openUIBlocking(final FileFilter fileFilter, final File file) {
		AsyncTaskExecutor.runUIBlocking("Loading data from file. Please wait a second", () -> open(fileFilter, file),
				() -> {
					if (!fileFilter.getDescription().equals(SuffixAwareFilter.VANESA_SIM_RESULT.getDescription())) {
						if (GraphContainer.getInstance().containsPathway()) {
							if (GraphInstance.getPathway().hasGotAtLeastOneElement()) {
								MainWindow.getInstance().updateAllGuiElements();
								GraphInstance.getMyGraph().normalCentering();
							}
						}
						MainWindow.getInstance().getFrame().repaint();
					}
				});
	}

	private static void open(final FileFilter fileFilter, final File file) {
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

	private static void openGraphText(final File file) {
		try {
			new TxtInput(new FileInputStream(file), file);
		} catch (Exception e) {
			PopUpDialog.getInstance().show("Error!", e.getMessage());
			e.printStackTrace();
		}
	}

	private static void openGraphML(final File file) {
		final GraphMLReader reader = new GraphMLReader(file);
		final Pathway pw = reader.read();
		if (reader.hasErrors() || pw == null) {
			PopUpDialog.getInstance().show("Error!", "Failed to load GraphML file.");
		} else {
			pw.setFile(file);
		}
	}

	private static void openKGML(final File file) {
		final KGMLReader reader = new KGMLReader(file);
		final Pathway pw = reader.read();
		if (reader.hasErrors() || pw == null) {
			PopUpDialog.getInstance().show("Error!", "Failed to load KGML file.");
		} else {
			pw.setFile(file);
		}
	}

	private static void openSimulationResult(final File file) {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			final PetriNetProperties petriNet = pathway.getPetriPropertiesNet();
			final SimulationResultController simResController = petriNet.getSimResController();
			String fileName = file.getName();
			if (simResController.containsSimId(fileName)) {
				int i = 1;
				while (simResController.containsSimId(fileName + "(" + i + ")")) {
					i++;
				}
				fileName += "(" + i + ")";
			}
			final SimulationResult simRes = simResController.get(fileName);
			simRes.setName(fileName);
			final PNSimulationResultCSVReader reader = new PNSimulationResultCSVReader(file, pathway, simRes);
			reader.read();
			if (reader.hasErrors()) {
				PopUpDialog.getInstance().show("Error!", "Failed to load simulation result CSV file.");
			}
			pathway.setPlotColorPlacesTransitions(false);
			petriNet.setPetriNetSimulation(true);
			final SimMenu menu = pathway.getPetriNetSimulation().getMenu();
			if (menu != null) {
				menu.updateSimulationResults();
			}
			MainWindow.getInstance().addSimulationResults();
		}
	}
}
