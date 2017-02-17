package petriNet;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import biologicalElements.PetriNet;
import configurations.ConnectionSettings;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.ProgressBar;
import io.MyFileFilter;

public class OpenModellicaResult extends SwingWorker {
	private JFileChooser chooser;

	private String fileFormat;
	private File file;

	private final String vanesaResultDescription = "VANESA Simulation Result File (*.csv)";
	private final String vanesaSimulation = "csv";

	private final String modelicaResultDescription = "New Modelica Simulation Result File (*.csv)";
	private final String modelicaSimulation = "csv";

	private final int option;
	private final ProgressBar bar = new ProgressBar();

	private GraphContainer con = ContainerSingelton.getInstance();
	private GraphInstance graphInstance = new GraphInstance();

	public OpenModellicaResult() {

		if (ConnectionSettings.getFileDirectory() != null) {
			chooser = new JFileChooser(ConnectionSettings.getFileDirectory());
		} else {
			chooser = new JFileChooser();
		}

		chooser.setAcceptAllFileFilterUsed(false);
		//chooser.addChoosableFileFilter(new MyFileFilter(modelicaSimulation, modelicaResultDescription));
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
						PetriNet petrinet = graphInstance.getPathway().getPetriNet();
						petrinet.loadVanesaSimulationResult(file);

					} else {
						JOptionPane.showMessageDialog(MainWindow.getInstance(), "Please load or create a network first!");
					}
				} else {
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Please load or create a network first!");
				}

			} else if (fileFormat.equals(modelicaResultDescription)) {
				if (con.containsPathway()) {
					if (graphInstance.getPathway().hasGotAtLeastOneElement()) {

						graphInstance.getPathway().setPetriNet(true);
						PetriNet petrinet = graphInstance.getPathway().getPetriNet();
						petrinet.setPetriNetSimulationFile(file.getAbsolutePath());

					} else {
						JOptionPane.showMessageDialog(MainWindow.getInstance(), "Please load or create a network first!");
					}
				} else {
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Please load or create a network first!");
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
					bar.setProgressBarString("Loading data from file. Please wait a second");
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

					// GraphInstance.getMyGraph().getVisualizationViewer()
					// .restart();
					MyGraph g = GraphInstance.getMyGraph();
					g.normalCentering();
					MainWindow.getInstance().updateAllGuiElements();

				}
			}
		}
	}

}
