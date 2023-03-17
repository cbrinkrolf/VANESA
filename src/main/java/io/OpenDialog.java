package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import biologicalElements.Pathway;
import configurations.ConnectionSettings;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.PopUpDialog;
import io.graphML.GraphMLReader;
import io.sbml.JSBMLInput;
import io.vaml.VAMLInput;

public class OpenDialog extends SwingWorker<Object, Object> {
    private FileFilter fileFilter;
    private File file;
    private final int option;
    private final JFileChooser chooser;
    private final GraphContainer con = GraphContainer.getInstance();

    public OpenDialog() {
        chooser = new JFileChooser(ConnectionSettings.getInstance().getFileOpenDirectory());
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(SuffixAwareFilter.SBML);
        chooser.addChoosableFileFilter(SuffixAwareFilter.VAML);
        // chooser.addChoosableFileFilter(SuffixAwareFilter.MO);
        // chooser.addChoosableFileFilter(SuffixAwareFilter.MODELICA_RESULT_DESCRIPTION);
        // chooser.addChoosableFileFilter(SuffixAwareFilter.NEW_MODELICA_RESULT_DESCRIPTION);
        chooser.addChoosableFileFilter(SuffixAwareFilter.GRAPH_ML);
        chooser.addChoosableFileFilter(SuffixAwareFilter.GRAPH_TEXT_FILE);
        option = chooser.showOpenDialog(MainWindow.getInstance().getFrame());
        if (option == JFileChooser.APPROVE_OPTION) {
            ConnectionSettings.getInstance().setFileOpenDirectory(chooser.getCurrentDirectory().getAbsolutePath());
        }
    }

    private void open() {
        if (fileFilter != null) {
            // ConnectionSettings.setFileSaveDirectory(file.getAbsolutePath());
            if (fileFilter == SuffixAwareFilter.VAML) {
                try {
                    new VAMLInput(file);
                } catch (IOException e) {
                    PopUpDialog.getInstance().show("VAML read error.", "Failed to load VAML file.");
                    e.printStackTrace();
                }
            } else if (fileFilter == SuffixAwareFilter.SBML) {
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
            } else if (fileFilter == SuffixAwareFilter.GRAPH_TEXT_FILE) {
                try {
                    new TxtInput(new FileInputStream(file), file);
                } catch (Exception e) {
                    PopUpDialog.getInstance().show("Error!", e.getMessage());
                    e.printStackTrace();
                }
            } else if (fileFilter == SuffixAwareFilter.GRAPH_ML) {
                final GraphMLReader reader = new GraphMLReader(file);
                final Pathway pw = reader.read();
                if (reader.hasErrors() || pw == null) {
                    PopUpDialog.getInstance().show("Error!", "Failed to load GraphML file.");
                } else {
                    pw.setFile(file);
                }
            }
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        if (option == JFileChooser.APPROVE_OPTION) {
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
        open();
        if (fileFilter != null) {
            // bar.closeWindow();
            MainWindow.getInstance().closeProgressBar();
            if (con.containsPathway()) {
                if (GraphInstance.getPathway().hasGotAtLeastOneElement()) {
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
