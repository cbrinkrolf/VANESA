package petriNet;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindowSingleton;
import gui.ProgressBar;
import io.MyFileFilter;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import biologicalElements.PetriNet;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import configurations.ConnectionSettings;

public class OpenModellicaResult extends SwingWorker{
	private JFileChooser chooser;

	private String fileFormat;
	private File file;


	private final String modellicaResultDescription = "Modellica Simulation Result File (*.plt)";
	private final String modellicaSimulation = "plt";

	private final String modellicaResultDescriptionNew = "New Modelica Simulation Result File (*.csv)";
	private final String modellicaSimulationNew = "csv";
	
	private final int option;
	private final ProgressBar bar = new ProgressBar();
	
	private GraphContainer con = ContainerSingelton.getInstance();
	private GraphInstance graphInstance = new GraphInstance();

	public OpenModellicaResult () {

		if (ConnectionSettings.getFileDirectory() != null) {
			chooser = new JFileChooser(ConnectionSettings.getFileDirectory());
		} else {
			chooser = new JFileChooser();
		}

		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new MyFileFilter(modellicaSimulationNew,
				modellicaResultDescriptionNew));
		chooser.addChoosableFileFilter(new MyFileFilter(modellicaSimulation,
				modellicaResultDescription));



		option = chooser.showOpenDialog(null);

	}

	private void open() {
		if (fileFormat != null) {
			ConnectionSettings.setFileDirectory(file.getAbsolutePath());

 if (fileFormat.equals(modellicaResultDescription)) {
				if (con.containsPathway()) {
					if (graphInstance.getPathway().hasGotAtLeastOneElement()) {

						graphInstance.getPathway().setPetriNet(true);
						PetriNet petrinet = graphInstance.getPathway()
								.getPetriNet();
						petrinet.setPetriNetSimulationFile(file
								.getAbsolutePath(), false);
						petrinet.initializePetriNet(new HashMap<BiologicalEdgeAbstract, String>());

					} else {
						JOptionPane.showMessageDialog(MainWindowSingleton
								.getInstance(),
								"Please load or create a network first!");
					}
				} else {
					JOptionPane.showMessageDialog(MainWindowSingleton
							.getInstance(),
							"Please load or create a network first!");
				}

			} else if (fileFormat.equals(modellicaResultDescriptionNew)) {
				if (con.containsPathway()) {
					if (graphInstance.getPathway().hasGotAtLeastOneElement()) {

						graphInstance.getPathway().setPetriNet(true);
						PetriNet petrinet = graphInstance.getPathway()
								.getPetriNet();
						petrinet.setPetriNetSimulationFile(file
								.getAbsolutePath(), false);
						petrinet.initializePetriNet(new HashMap<BiologicalEdgeAbstract, String>());

					} else {
						JOptionPane.showMessageDialog(MainWindowSingleton
								.getInstance(),
								"Please load or create a network first!");
					}
				} else {
					JOptionPane.showMessageDialog(MainWindowSingleton
							.getInstance(),
							"Please load or create a network first!");
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
						//	.restart();
					MyGraph g = GraphInstance.getMyGraph();
					g.normalCentering();
					MainWindowSingleton.getInstance().updateAllGuiElements();

				}
			}
		}
	}

}
