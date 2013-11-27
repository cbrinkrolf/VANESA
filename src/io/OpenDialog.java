package io;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;

import xmlInput.sbml.SBMLInput;
import xmlInput.sbml.VAMLInput;
import configurations.ConnectionSettings;

public class OpenDialog extends SwingWorker {

	private String fileFormat;
	private File file;

	private final String sbmlDescription = "System Biology Markup Language (*.sbml)";
	private final String sbml = "sbml";

	private final String modellicaResultDescription = "Modellica Simulation Result File (*.plt)";
	private final String modellicaSimulation = "plt";

	private final String modellicaResultDescriptionNew = "New Modelica Simulation Result File (*.csv)";
	private final String modellicaSimulationNew = "csv";

	private final String vamlDescription = "VANESA Markup Language (*.vaml)";
	private final String vaml = "vaml";

	private final String graphMlDescription = "Graph Markup Language (*.gml)";
	private final String moDescription = "Modelica File (*.mo)";

	private final String phosphoMlDescription = "PhosphoGrid Text File (*.txt)";
	private final String poDescription = "txt";

	private String txtDescription = "Graph Text File (*.txt)";
	private String txt = "txt";

	private final int option;
	final ProgressBar bar = new ProgressBar();

	private final String mo = "mo";

	private JFileChooser chooser;

	GraphContainer con = ContainerSingelton.getInstance();
	GraphInstance graphInstance = new GraphInstance();

	public OpenDialog() {

		if (ConnectionSettings.getFileDirectory() != null) {
			chooser = new JFileChooser(ConnectionSettings.getFileDirectory());
		} else {
			chooser = new JFileChooser();
		}

		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new MyFileFilter(vaml, vamlDescription));
		// chooser.addChoosableFileFilter(new MyFileFilter(mo, moDescription));
		chooser.addChoosableFileFilter(new MyFileFilter(sbml, sbmlDescription));
//		chooser.addChoosableFileFilter(new MyFileFilter(modellicaSimulation,
//				modellicaResultDescription));
//		chooser.addChoosableFileFilter(new MyFileFilter(modellicaSimulationNew,
//				modellicaResultDescriptionNew));

		chooser.addChoosableFileFilter(new MyFileFilter(poDescription,
				phosphoMlDescription));
		chooser.addChoosableFileFilter(new MyFileFilter(txt, txtDescription));
		

		option = chooser.showOpenDialog(null);

	}

	private void open() {
		if (fileFormat != null) {
		//	System.out.println(fileFormat);
			ConnectionSettings.setFileDirectory(file.getAbsolutePath());

			if (fileFormat.equals(vamlDescription)) {
				try {
					try {
						new VAMLInput(file);
					} catch (XMLStreamException e) {
						JOptionPane.showMessageDialog(null,
								"An error occured during the loading. "
										+ "The VAML file is not valid.",
								"VAML read error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}  else if (fileFormat.equals(sbmlDescription)) {
				try {
					try {
						SBMLInput sbmlInput = new SBMLInput();
						String result = sbmlInput.loadSBMLFile(file);
						if(result.length() > 0){
							JOptionPane.showMessageDialog(MainWindowSingelton.getInstance(),
									result);
						}
					} catch (XMLStreamException e) {
						JOptionPane.showMessageDialog(null,
								"An error occured during the loading. "
										+ "The SBML file is not valid.",
								"SBML read error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (fileFormat.equals(txtDescription)) {
				try {
					new TxtInput(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (fileFormat.equals(phosphoMlDescription)) {
				try {
					new PhosphoInput(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println(file.getAbsolutePath());

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
					bar.init(100, "  Open ", true);
					bar
							.setProgressBarString("Loading data from file. Please wait a second");
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
			bar.closeWindow();

			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {

					//GraphInstance.getMyGraph().getVisualizationViewer()
					//		.restart();
					MyGraph g = GraphInstance.getMyGraph();
					g.normalCentering();
					MainWindowSingelton.getInstance().updateAllGuiElements();

				}
			}
		}
	}
}
