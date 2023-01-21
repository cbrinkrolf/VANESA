package transformation.gui;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import configurations.ConnectionSettings;
import gui.MainWindow;
import io.MyFileFilter;
import transformation.Rule;
import transformation.RuleManager;
import transformation.YamlRuleReader;

public class OpenTransformationRules extends SwingWorker<Object, Object> {
	private JFileChooser chooser;

	private String fileFormat;
	private File file;

	private final String rulesDescription = "Transformation rules (*.yaml)";
	private final String rulesSuffix = "yaml";

	// private final String modelicaResultDescription = "New Modelica Simulation
	// Result File (*.csv)";
	// private final String modelicaSimulation = "csv";

	private final int option;

	private RuleManagementWindow rmw;

	public OpenTransformationRules(RuleManagementWindow rmw) {
		this.rmw = rmw;
		chooser = new JFileChooser(ConnectionSettings.getInstance().getFileOpenDirectory());

		chooser.setAcceptAllFileFilterUsed(false);
		// chooser.addChoosableFileFilter(new MyFileFilter(modelicaSimulation,
		// modelicaResultDescription));
		chooser.addChoosableFileFilter(new MyFileFilter(rulesSuffix, rulesDescription));

		option = chooser.showOpenDialog(MainWindow.getInstance().getFrame());

	}

	private void open() {
		if (fileFormat != null) {
			if (fileFormat.equals(rulesDescription)) {
				List<Rule> rules = new YamlRuleReader().getRules(file);
				RuleManager.getInstance().addRules(rules);
			}
		}
	}

	@Override
	protected Void doInBackground() throws Exception {

		if (option == JFileChooser.APPROVE_OPTION) {
			ConnectionSettings.getInstance().setFileOpenDirectory(chooser.getCurrentDirectory().getAbsolutePath());

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
			MainWindow.getInstance().closeProgressBar();
			rmw.repaintPanel();
			// bar.closeWindow();
		}
	}
}
