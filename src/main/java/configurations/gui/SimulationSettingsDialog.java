package configurations.gui;

import java.io.File;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import configurations.ConnectionSettings;
import net.miginfocom.swing.MigLayout;

public class SimulationSettingsDialog {

	private ConnectionSettings settings = ConnectionSettings.getInstance();

	private JPanel panel;

	private JLabel lblPathOM;
	private JLabel lblPathPNlib;

	private String pathOM;
	private String pathPNlib;

	private JCheckBox overrideOMPath;
	private JCheckBox overridePNlibPath;

	public SimulationSettingsDialog() {
		pathOM = settings.getOMPath();
		pathPNlib = settings.getPNlibPath();

		lblPathOM = new JLabel(pathOM);
		lblPathPNlib = new JLabel(pathPNlib);

		// Container contentPane = getContentPane();
		MigLayout layout = new MigLayout("", "[left]");

		panel = new JPanel(layout);

		panel.add(new JLabel("Specify path of OpenModelica:"), "span 1");
		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		panel.add(new JLabel("OpenModelica path: "));
		panel.add(lblPathOM, "wrap");

		JButton chooseOMPath = new JButton("Choose folder");
		chooseOMPath.addActionListener(e -> onChooseOMPathClicked());
		chooseOMPath.setToolTipText("Root folder of OpenModelica that contains folders: bin, lib, tools, among others.");
		panel.add(chooseOMPath);

		overrideOMPath = new JCheckBox();
		overrideOMPath.setSelected(settings.isOverrideOMPath());
		overrideOMPath.setText("override system defaults:");
		overrideOMPath.setHorizontalTextPosition(SwingConstants.LEFT);
		panel.add(overrideOMPath, "wrap");

		panel.add(new JLabel("Specify path of PNlib:"), "span 1");
		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		panel.add(new JLabel("PNlib path: "));
		panel.add(lblPathPNlib, "wrap");

		JButton choosePNlibPath = new JButton("Choose folder");
		choosePNlibPath.addActionListener(e -> onChoosePNlibPathClicked());
		choosePNlibPath.setToolTipText("Root folder that contains one or multiple folders / versions of PNlib. Each folder of a PNlib verison must contain a folder named 'PNlib' containing a 'package.mo' file.");
		panel.add(choosePNlibPath);

		overridePNlibPath = new JCheckBox();
		overridePNlibPath.setSelected(settings.isOverridePNlibPath());
		overridePNlibPath.setText("override system defaults:");
		overridePNlibPath.setHorizontalTextPosition(SwingConstants.LEFT);
		panel.add(overridePNlibPath, "wrap");

	}

	public JPanel getPanel() {
		return panel;
	}

	public boolean applyDefaults() {
		pathOM = "";
		lblPathOM.setText(pathOM); ;
		pathPNlib = "";
		lblPathPNlib.setText(pathPNlib);
		overrideOMPath.setSelected(false);
		overridePNlibPath.setSelected(false);
		
		return true;
	}

	public boolean applyNewSettings() {
		settings.setOMPath(pathOM);
		settings.setPNlibPath(pathPNlib);
		settings.setOverrideOMPath(overrideOMPath.isSelected());
		settings.setOverridePNlibPath(overridePNlibPath.isSelected());
		return true;
	}

	private void onChooseOMPathClicked() {
		JFileChooser chooser = new JFileChooser();
		String path = ".";
		
		Map<String, String> env = System.getenv();
		if (env.containsKey("OPENMODELICAHOME") && new File(env.get("OPENMODELICAHOME")).isDirectory()) {
			path = env.get("OPENMODELICAHOME");
		}
		
		if (pathOM.length() > 0) {
			path = pathOM;
		}
		chooser.setCurrentDirectory(new File(path));
		chooser.setDialogTitle("Choose path to OpenModelica installation");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int option = chooser.showSaveDialog(panel);
		if (option == JFileChooser.APPROVE_OPTION) {
			pathOM = chooser.getSelectedFile().getAbsolutePath();
			lblPathOM.setText(pathOM);
		}
	}

	private void onChoosePNlibPathClicked() {
		JFileChooser chooser = new JFileChooser();
		String path = ".";
		if (pathPNlib.length() > 0) {
			path = pathPNlib;
		}
		chooser.setCurrentDirectory(new File(path));
		chooser.setDialogTitle("Choose path to PNlib");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int option = chooser.showSaveDialog(panel);
		if (option == JFileChooser.APPROVE_OPTION) {
			pathPNlib = chooser.getSelectedFile().getAbsolutePath();
			lblPathPNlib.setText(pathPNlib);
		}
	}
}
