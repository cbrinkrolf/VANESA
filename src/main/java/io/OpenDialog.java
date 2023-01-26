package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;

import biologicalElements.Pathway;
import configurations.ConnectionSettings;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MyPopUp;
import io.graphML.GraphMLReader;
import xmlInput.sbml.JSBMLinput;
import xmlInput.sbml.VAMLInput;

public class OpenDialog extends SwingWorker<Object, Object> {

	private String fileFormat;
	private File file;

	private final String sbmlDescription = "System Biology Markup Language (*.sbml)";
	private final String sbml = "sbml";

	// private final String modellicaResultDescription = "Modellica Simulation Result File (*.plt)";
	// private final String modellicaSimulation = "plt";

	// private final String modellicaResultDescriptionNew = "New Modelica Simulation Result File (*.csv)";
	// private final String modellicaSimulationNew = "csv";

	private final String vamlDescription = "VANESA Markup Language (*.vaml)";
	private final String vaml = "vaml";

	private final String graphMlDescription = "GraphML (*.graphml)";
	private final String graphml = "graphml";

	// private final String moDescription = "Modelica File (*.mo)";
	// private final String mo = "mo";

	private final String txtDescription = "Graph Text File (*.txt)";
	private final String txt = "txt";

	private final int option;
	private final JFileChooser chooser;

	private final GraphContainer con = GraphContainer.getInstance();
	private final GraphInstance graphInstance = new GraphInstance();

	private Pathway pathway = null;

	public OpenDialog(Pathway pw) {
		this();
		pathway = pw;
	}

	public OpenDialog() {

		chooser = new JFileChooser(ConnectionSettings.getInstance().getFileOpenDirectory());

		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new MyFileFilter(sbml, sbmlDescription));
		chooser.addChoosableFileFilter(new MyFileFilter(vaml, vamlDescription));
		// chooser.addChoosableFileFilter(new MyFileFilter(mo, moDescription));

//		chooser.addChoosableFileFilter(new MyFileFilter(modellicaSimulation,
//				modellicaResultDescription));
//		chooser.addChoosableFileFilter(new MyFileFilter(modellicaSimulationNew,
//				modellicaResultDescriptionNew));

		chooser.addChoosableFileFilter(new MyFileFilter(graphml, graphMlDescription));
		chooser.addChoosableFileFilter(new MyFileFilter(txt, txtDescription));

		option = chooser.showOpenDialog(MainWindow.getInstance().getFrame());

		if (option == JFileChooser.APPROVE_OPTION) {
			ConnectionSettings.getInstance().setFileOpenDirectory(chooser.getCurrentDirectory().getAbsolutePath());
		}
	}

	private void open() {
		if (fileFormat != null) {
			// System.out.println(fileFormat);
			// ConnectionSettings.setFileSaveDirectory(file.getAbsolutePath());

			if (fileFormat.equals(vamlDescription)) {
				try {
					try {
						new VAMLInput(file);
					} catch (XMLStreamException e) {
						MyPopUp.getInstance().show("VAML read error.",
								"An error occured during the loading. " + "The VAML file is not valid.");
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (fileFormat.equals(sbmlDescription)) {

				JSBMLinput jsbmlInput;
				jsbmlInput = pathway == null ? new JSBMLinput() : new JSBMLinput(pathway);
				String result;
				try {
					result = jsbmlInput.loadSBMLFile(new FileInputStream(file), file);
					if (result.length() > 0) {
						MyPopUp.getInstance().show("Information", result);
					}

				} catch (FileNotFoundException e) {
					try {
						file = new File(file.getAbsolutePath() + ".sbml");
						result = jsbmlInput.loadSBMLFile(new FileInputStream(file), file);
						if (result.length() > 0) {
							MyPopUp.getInstance().show("Information", result);
						}

					} catch (FileNotFoundException ex) {
						ex.printStackTrace();
					}
				} catch(Exception e){
					MyPopUp.getInstance().show("Error!", e.getMessage());
					e.printStackTrace();
				}
				
			} else if (fileFormat.equals(txtDescription)) {
				try {
					new TxtInput(new FileInputStream(file), file);
				} catch (Exception e) {
					MyPopUp.getInstance().show("Error!", e.getMessage());
					e.printStackTrace();
				}
			} else if (fileFormat.equals(graphMlDescription)) {
				final GraphMLReader reader = new GraphMLReader(file);
				final Pathway pw = reader.read();
				if (reader.hasErrors() || pw == null) {
					MyPopUp.getInstance().show("Error!", "Failed to load GraphML file.");
				} else {
					pw.setFile(file);
				}
			}
		}
	}

	@Override
	protected Void doInBackground() throws Exception {

		if (option == JFileChooser.APPROVE_OPTION) {

			fileFormat = chooser.getFileFilter().getDescription();
			file = chooser.getSelectedFile();

			Runnable run = new Runnable() {
				@Override
				public void run() {
					MainWindow.getInstance().showProgressBar("Loading data from file. Please wait a second");
					// bar.init(100, " Open ", true);
					// bar.setProgressBarString("Loading data from file. Please wait a second");
				}
			};
			SwingUtilities.invokeLater(run);
		}
		return null;
	}

	@Override
	public void done() {
		open();
		if (fileFormat != null) {
			// bar.closeWindow();
			MainWindow.getInstance().closeProgressBar();

			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// GraphInstance.getMyGraph().getVisualizationViewer().restart();
					MainWindow.getInstance().updateAllGuiElements();
					MyGraph g = GraphInstance.getMyGraph();
					g.normalCentering();
				}
			}
			MainWindow.getInstance().getFrame().repaint();
		}
	}
}
