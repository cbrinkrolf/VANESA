package transformation.gui;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import configurations.ConnectionSettings;
import gui.MainWindow;
import io.SuffixAwareFilter;
import transformation.Rule;
import transformation.RuleManager;
import transformation.YamlRuleReader;

public class OpenTransformationRules extends SwingWorker<Object, Object> {
    private final JFileChooser chooser;
    private FileFilter fileFilter;
    private File file;
    private final int option;
    private final RuleManagementWindow rmw;

    public OpenTransformationRules(RuleManagementWindow rmw) {
        this.rmw = rmw;
        chooser = new JFileChooser(ConnectionSettings.getInstance().getFileOpenDirectory());

        chooser.setAcceptAllFileFilterUsed(false);
        // chooser.addChoosableFileFilter(SuffixAwareFilter.NEW_MODELICA_RESULT_DESCRIPTION);
        chooser.addChoosableFileFilter(SuffixAwareFilter.TRANSFORMATION_RULES);

        option = chooser.showOpenDialog(MainWindow.getInstance().getFrame());

    }

    private void open() {
        if (fileFilter != null && fileFilter == SuffixAwareFilter.TRANSFORMATION_RULES) {
            List<Rule> rules = new YamlRuleReader().getRules(file);
            RuleManager.getInstance().addRules(rules);
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
        open();
        if (fileFilter != null) {
            MainWindow.getInstance().closeProgressBar();
            rmw.repaintPanel();
            // bar.closeWindow();
        }
    }
}
