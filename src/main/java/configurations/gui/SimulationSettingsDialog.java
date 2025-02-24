package configurations.gui;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import configurations.SettingsManager;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

public class SimulationSettingsDialog {
	private final SettingsManager settings = SettingsManager.getInstance();

	private final JPanel panel;
	private final JLabel lblPathOM;
	private final JLabel lblPathPNlib;
	private final JCheckBox overrideOMPath;
	private final JCheckBox overridePNlibPath;
	private final JCheckBox cleanWorkingDirAfterCompilation;

	private String pathOM;
	private String pathPNlib;

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
		chooseOMPath
				.setToolTipText("Root folder of OpenModelica that contains folders: bin, lib, tools, among others.");
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
		choosePNlibPath.setToolTipText(
				"Root folder that contains one or multiple folders / versions of PNlib. Each folder of a PNlib verison must contain a folder named 'PNlib' containing a 'package.mo' file.");
		panel.add(choosePNlibPath);

		overridePNlibPath = new JCheckBox();
		overridePNlibPath.setSelected(settings.isOverridePNlibPath());
		overridePNlibPath.setText("override system defaults:");
		overridePNlibPath.setHorizontalTextPosition(SwingConstants.LEFT);

		panel.add(overridePNlibPath, "wrap");

		cleanWorkingDirAfterCompilation = new JCheckBox();
		cleanWorkingDirAfterCompilation.setSelected(settings.isCleanWorkingDirAfterCompilation());
		cleanWorkingDirAfterCompilation.setText("Clean working directory after compiling");
		cleanWorkingDirAfterCompilation.setHorizontalTextPosition(SwingConstants.LEFT);
		cleanWorkingDirAfterCompilation
				.setToolTipText("delets all unnecessary files for simulation genererated during compilation");
		panel.add(cleanWorkingDirAfterCompilation, "span 2, wrap");

	}

	public JPanel getPanel() {
		return panel;
	}

	public boolean applyDefaults() {
		pathOM = "";
		lblPathOM.setText(pathOM);
		pathPNlib = "";
		lblPathPNlib.setText(pathPNlib);
		overrideOMPath.setSelected(false);
		overridePNlibPath.setSelected(false);
		cleanWorkingDirAfterCompilation.setSelected(true);
		return true;
	}

	public boolean applyNewSettings() {
		settings.setOMPath(pathOM, overrideOMPath.isSelected());
		settings.setPNlibPath(pathPNlib, overridePNlibPath.isSelected());
		settings.setOverrideOMPath(overrideOMPath.isSelected());
		settings.setOverridePNlibPath(overridePNlibPath.isSelected());
		settings.setCleanWorkingDirAfterCompilation(cleanWorkingDirAfterCompilation.isSelected());
		return true;
	}

	private void onChooseOMPathClicked() {
		final JFileChooser chooser = new JFileChooser();
		String path = ".";
		if (isValidDirectoryPath(pathOM)) {
			path = pathOM;
		} else {
			final String envPath = System.getenv("OPENMODELICAHOME");
			if (isValidDirectoryPath(envPath)) {
				path = envPath;
			}
		}

		chooser.setCurrentDirectory(new File(path));
		chooser.setDialogTitle("Choose path to OpenModelica installation");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final int option = chooser.showSaveDialog(panel);
		if (option == JFileChooser.APPROVE_OPTION) {
			pathOM = chooser.getSelectedFile().getAbsolutePath();
			lblPathOM.setText(pathOM);
		}
	}

	private boolean isValidDirectoryPath(final String path) {
		return StringUtils.isNotEmpty(path) && new File(path).exists() && new File(path).isDirectory();
	}

	private void onChoosePNlibPathClicked() {
		final JFileChooser chooser = new JFileChooser();
		String path = ".";
		if (StringUtils.isNotEmpty(pathPNlib)) {
			path = pathPNlib;
		}
		chooser.setCurrentDirectory(new File(path));
		chooser.setDialogTitle("Choose path to PNlib");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final int option = chooser.showSaveDialog(panel);
		if (option == JFileChooser.APPROVE_OPTION) {
			pathPNlib = chooser.getSelectedFile().getAbsolutePath();
			lblPathPNlib.setText(pathPNlib);
		}
	}
}
