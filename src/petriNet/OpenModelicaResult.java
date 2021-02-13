package petriNet;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import configurations.ConnectionSettings;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MyPopUp;
import io.MyFileFilter;

public class OpenModelicaResult extends SwingWorker<Object, Object> {
	private JFileChooser chooser;

	private String fileFormat;
	private File file;

	private final String vanesaResultDescription = "VANESA Simulation Result File (*.csv)";
	private final String vanesaSimulation = "csv";

	//private final String modelicaResultDescription = "New Modelica Simulation Result File (*.csv)";
	//private final String modelicaSimulation = "csv";

	private final int option;

	private GraphContainer con = GraphContainer.getInstance();
	private GraphInstance graphInstance = new GraphInstance();

	public OpenModelicaResult() {

		if (ConnectionSettings.getFileDirectory() != null) {
			chooser = new JFileChooser(ConnectionSettings.getFileDirectory());
		} else {
			chooser = new JFileChooser();
		}

		chooser.setAcceptAllFileFilterUsed(false);
		// chooser.addChoosableFileFilter(new MyFileFilter(modelicaSimulation,
		// modelicaResultDescription));
		chooser.addChoosableFileFilter(new MyFileFilter(vanesaSimulation, vanesaResultDescription));

		option = chooser.showOpenDialog(null);

	}

	private void open() {
		if (fileFormat != null) {
			ConnectionSettings.setFileDirectory(file.getAbsolutePath());

			if (fileFormat.equals(vanesaResultDescription)) {
				if (con.containsPathway()) {
					if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
						graphInstance.getPathway().setPetriNet(true);
						PetriNetProperties petrinet = graphInstance.getPathway().getPetriPropertiesNet();
						petrinet.loadVanesaSimulationResult(file);

					} else {
						MyPopUp.getInstance().show("Error", "Please create a network before.");
					}
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network before.");
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
					//bar.init(100, "  Open ", true);
					//bar.setProgressBarString("Loading data from file. Please wait a second");
				}
			};
			SwingUtilities.invokeLater(run);

		}
		return null;
	}

	@Override
	public void done() {
		// TODO if BN holds PN
		open();
		if (fileFormat != null) {
			MainWindow.getInstance().closeProgressBar();
			//bar.closeWindow();

			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// GraphInstance.getMyGraph().getVisualizationViewer()
					// .restart();
					MyGraph g = GraphInstance.getMyGraph();
					g.normalCentering();
					MainWindow.getInstance().initSimResGraphs();
					MainWindow.getInstance().updateAllGuiElements();
				}
			}
		}
	}
}
