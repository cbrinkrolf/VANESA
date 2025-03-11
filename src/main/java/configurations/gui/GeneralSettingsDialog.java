package configurations.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import configurations.Settings;
import configurations.Workspace;
import configurations.XMLResourceBundle;
import gui.JIntTextField;
import gui.JValidatedURLTextField;
import io.image.ComponentImageWriter;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

public class GeneralSettingsDialog extends BaseSettingsPanel {
	private static final int ABBREVIATE_WIDTH = 50;

	private final JCheckBox removeSVGClipPathsCheckBox = new JCheckBox();
	private final JCheckBox removePDFClipPathsCheckBox = new JCheckBox();
	private final JComboBox<String> defaultImageFormatComboBox = new JComboBox<>();
	private final List<String> formatList = Arrays.asList(ComponentImageWriter.IMAGE_TYPE_PNG,
			ComponentImageWriter.IMAGE_TYPE_SVG, ComponentImageWriter.IMAGE_TYPE_PDF);

	private final JCheckBox overrideOMPathCheckBox = new JCheckBox();
	private final JButton overrideOMPathButton = new JButton("Choose...");
	private String overrideOMPath;
	private final JLabel overrideOMPathLabel = new JLabel();
	private final JCheckBox overridePNlibPathCheckBox = new JCheckBox();
	private final JButton overridePNlibPathButton = new JButton("Choose...");
	private String overridePNlibPath;
	private final JLabel overridePNlibPathLabel = new JLabel();
	private final JCheckBox cleanWorkingDirAfterCompilationCheckBox = new JCheckBox();

	private final JValidatedURLTextField apiUrlTextField = new JValidatedURLTextField();
	private final JIntTextField proxyPortTextField = new JIntTextField();
	private final JTextField proxyHostTextField = new JTextField();

	public GeneralSettingsDialog() {
		super();
		addExportSettings();
		addSimulationSettings();
		addInternetSettings();
		updateSettings(Workspace.getCurrentSettings());
	}

	private void addExportSettings() {
		addHeader("Export Settings");
		addSetting("Remove SVG clip paths", "Specify deletion of clip paths during image export",
				removeSVGClipPathsCheckBox, () -> removeSVGClipPathsCheckBox.setSelected(false));
		addSetting("Remove PDF clip paths", "Specify deletion of clip paths during image export",
				removePDFClipPathsCheckBox, () -> removePDFClipPathsCheckBox.setSelected(false));
		addSetting("Default image file format", "Specify the default file format used during image export",
				defaultImageFormatComboBox, () -> defaultImageFormatComboBox.setSelectedIndex(0));
		removeSVGClipPathsCheckBox.setBackground(null);
		removePDFClipPathsCheckBox.setBackground(null);
		for (String s : formatList) {
			defaultImageFormatComboBox.addItem(s);
		}
	}

	private void addSimulationSettings() {
		final JPanel overrideOMPathPanel = new JPanel(new MigLayout("ins 0, left, fill", "[][][grow]"));
		overrideOMPathPanel.setBackground(null);
		overrideOMPathPanel.add(overrideOMPathCheckBox);
		overrideOMPathPanel.add(overrideOMPathButton);
		overrideOMPathPanel.add(overrideOMPathLabel);
		final JPanel overridePNLibPathPanel = new JPanel(new MigLayout("ins 0, left, fill", "[][][grow]"));
		overridePNLibPathPanel.setBackground(null);
		overridePNLibPathPanel.add(overridePNlibPathCheckBox);
		overridePNLibPathPanel.add(overridePNlibPathButton);
		overridePNLibPathPanel.add(overridePNlibPathLabel);
		addHeader("Simulation Settings");
		addSetting("Override system OpenModelica path",
				"Override the automatically detected OpenModelica path. Root folder of OpenModelica that contains folders: bin, lib, tools, among others.",
				overrideOMPathPanel, () -> {
					overrideOMPathCheckBox.setSelected(false);
					overrideOMPath = "";
					overrideOMPathLabel.setText("");
				});
		addSetting("Override PNlib path",
				"Override automatically installed PNlib. Root folder that contains one or multiple folders / versions of PNlib. Each folder of a PNlib version must contain a folder named 'PNlib' containing a 'package.mo' file.",
				overridePNLibPathPanel, () -> {
					overridePNlibPathCheckBox.setSelected(false);
					overridePNlibPath = "";
					overridePNlibPathLabel.setText("");
				});
		addSetting("Clean working directory after compiling",
				"Deletes all unnecessary files for simulation generated during compilation",
				cleanWorkingDirAfterCompilationCheckBox,
				() -> cleanWorkingDirAfterCompilationCheckBox.setSelected(true));
		overrideOMPathCheckBox.setBackground(null);
		overrideOMPathCheckBox.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				overrideOMPathButton.setEnabled(overrideOMPathCheckBox.isSelected());
			}
		});
		overrideOMPathButton.addActionListener(e -> onChooseOMPathClicked());
		overridePNlibPathCheckBox.setBackground(null);
		overridePNlibPathCheckBox.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				overridePNlibPathButton.setEnabled(overridePNlibPathCheckBox.isSelected());
			}
		});
		overridePNlibPathButton.addActionListener(e -> onChoosePNlibPathClicked());
		cleanWorkingDirAfterCompilationCheckBox.setBackground(null);
	}

	private void addInternetSettings() {
		addHeader("Proxy Settings");
		addSetting("Proxy Host", "Host address of the proxy", proxyHostTextField,
				() -> proxyHostTextField.setText(XMLResourceBundle.SETTINGS.getString("settings.default.proxy.host")));
		addSetting("Proxy Port", "Port of the proxy", proxyPortTextField,
				() -> proxyHostTextField.setText(XMLResourceBundle.SETTINGS.getString("settings.default.proxy.port")));
		addHeader("API Settings");
		addSetting("API Url", "Base URL of the VANESA API used for database queries", apiUrlTextField,
				() -> apiUrlTextField.setText(XMLResourceBundle.SETTINGS.getString("settings.default.api.url")));
	}

	@Override
	public void updateSettings(final Settings settings) {
		// Export settings
		removeSVGClipPathsCheckBox.setSelected(settings.isSVGClipPaths());
		removePDFClipPathsCheckBox.setSelected(settings.isPDFClipPaths());
		defaultImageFormatComboBox.setSelectedIndex(formatList.indexOf(settings.getDefaultImageExportFormat()));
		// Simulation settings
		overrideOMPath = settings.getOMPath();
		overrideOMPathLabel.setText(StringUtils.abbreviate(overrideOMPath, ABBREVIATE_WIDTH));
		overridePNlibPath = settings.getPNlibPath();
		overridePNlibPathLabel.setText(StringUtils.abbreviate(overridePNlibPath, ABBREVIATE_WIDTH));
		overrideOMPathCheckBox.setSelected(settings.isOverrideOMPath());
		overrideOMPathButton.setEnabled(overrideOMPathCheckBox.isSelected());
		overridePNlibPathCheckBox.setSelected(settings.isOverridePNlibPath());
		overridePNlibPathButton.setEnabled(overridePNlibPathCheckBox.isSelected());
		cleanWorkingDirAfterCompilationCheckBox.setSelected(settings.isCleanWorkingDirAfterCompilation());
		// Internet settings
		apiUrlTextField.setText(settings.getApiUrl());
		proxyPortTextField.setText(settings.getProxyPort());
		proxyHostTextField.setText(settings.getProxyHost());
	}

	@Override
	public boolean applySettings() {
		Workspace.getCurrentSettings().batchEdit((settings) -> {
			// Export settings
			settings.setSVGClipPaths(removeSVGClipPathsCheckBox.isSelected());
			settings.setPDFClipPaths(removePDFClipPathsCheckBox.isSelected());
			settings.setDefaultImageExportFormat(formatList.get(defaultImageFormatComboBox.getSelectedIndex()));
			// Simulation settings
			settings.setOMPath(overrideOMPath, overrideOMPathCheckBox.isSelected());
			settings.setPNlibPath(overridePNlibPath, overridePNlibPathCheckBox.isSelected());
			settings.setOverrideOMPath(overrideOMPathCheckBox.isSelected());
			settings.setOverridePNlibPath(overridePNlibPathCheckBox.isSelected());
			settings.setCleanWorkingDirAfterCompilation(cleanWorkingDirAfterCompilationCheckBox.isSelected());
			// Internet settings
			settings.setApiUrl(apiUrlTextField.getText());
			settings.setProxyHost(proxyHostTextField.getText());
			settings.setProxyPort(proxyPortTextField.getText());
		});
		// Validation
		return apiUrlTextField.isTextValid();
	}

	private void onChooseOMPathClicked() {
		final JFileChooser chooser = new JFileChooser();
		String path = ".";
		if (isValidDirectoryPath(overrideOMPath)) {
			path = overrideOMPath;
		} else {
			final String envPath = System.getenv("OPENMODELICAHOME");
			if (isValidDirectoryPath(envPath)) {
				path = envPath;
			}
		}
		chooser.setCurrentDirectory(new File(path));
		chooser.setDialogTitle("Choose path to OpenModelica installation");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final int option = chooser.showSaveDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			overrideOMPath = chooser.getSelectedFile().getAbsolutePath();
			overrideOMPathLabel.setText(StringUtils.abbreviate(overrideOMPath, ABBREVIATE_WIDTH));
		}
	}

	private boolean isValidDirectoryPath(final String path) {
		return StringUtils.isNotEmpty(path) && new File(path).exists() && new File(path).isDirectory();
	}

	private void onChoosePNlibPathClicked() {
		final JFileChooser chooser = new JFileChooser();
		String path = ".";
		if (StringUtils.isNotEmpty(overridePNlibPath)) {
			path = overridePNlibPath;
		}
		chooser.setCurrentDirectory(new File(path));
		chooser.setDialogTitle("Choose path to PNlib");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final int option = chooser.showSaveDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			overridePNlibPath = chooser.getSelectedFile().getAbsolutePath();
			overridePNlibPathLabel.setText(StringUtils.abbreviate(overridePNlibPath, ABBREVIATE_WIDTH));
		}
	}
}