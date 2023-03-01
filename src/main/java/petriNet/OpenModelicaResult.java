package petriNet;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import configurations.ConnectionSettings;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.PopUpDialog;
import io.SuffixAwareFilter;

public class OpenModelicaResult extends SwingWorker<Object, Object> {
    private final JFileChooser chooser;
    private FileFilter fileFilter;
    private File file;
    private final int option;
    private final GraphContainer con = GraphContainer.getInstance();
    private final GraphInstance graphInstance = new GraphInstance();

    public OpenModelicaResult() {
        chooser = new JFileChooser(ConnectionSettings.getInstance().getFileOpenDirectory());
        chooser.setAcceptAllFileFilterUsed(false);
        // chooser.addChoosableFileFilter(SuffixAwareFilter.NEW_MODELICA_RESULT_DESCRIPTION);
        chooser.addChoosableFileFilter(SuffixAwareFilter.VANESA_SIM_RESULT);
        option = chooser.showOpenDialog(MainWindow.getInstance().getFrame());
    }

    private void open() {
        if (fileFilter != null && fileFilter == SuffixAwareFilter.VANESA_SIM_RESULT) {
            if (con.containsPathway()) {
                if (graphInstance.getPathway().hasGotAtLeastOneElement()
                        && graphInstance.getPathway().isPetriNet()) {
                    // graphInstance.getPathway().setPetriNet(true);
                    PetriNetProperties petrinet = graphInstance.getPathway().getPetriPropertiesNet();
                    petrinet.loadVanesaSimulationResult(file);

                } else {
                    PopUpDialog.getInstance().show("Error", "Please create a Petri net first.");
                }
            } else {
                PopUpDialog.getInstance().show("Error", "Please create a network before.");
            }
        }
    }

    @Override
    protected Void doInBackground() throws Exception {

        if (option == JFileChooser.APPROVE_OPTION) {
            ConnectionSettings.getInstance().setFileOpenDirectory(chooser.getCurrentDirectory().getAbsolutePath());
            fileFilter = chooser.getFileFilter();
            file = chooser.getSelectedFile();
            SwingUtilities.invokeLater(() -> {
                MainWindow.getInstance().showProgressBar("Loading data from file. Please wait a second");
                // bar.init(100, " Open ", true);
                // bar.setProgressBarString("Loading data from file. Please wait a second");
            });
        }
        return null;
    }

    @Override
    public void done() {
        // TODO if BN holds PN
        open();
        if (fileFilter != null) {
            MainWindow.getInstance().closeProgressBar();
            // bar.closeWindow();
            if (con.containsPathway()) {
                if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
                    // GraphInstance.getMyGraph().getVisualizationViewer().restart();
                    MyGraph g = GraphInstance.getMyGraph();
                    g.normalCentering();
                    // TODO possible improvement for additionally loaded simulation results
                    // MainWindow.getInstance().initSimResGraphs();
                    MainWindow.getInstance().updateAllGuiElements();
                }
            }
        }
    }
}
