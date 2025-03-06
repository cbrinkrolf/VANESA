package configurations.gui;

import gui.MainWindow;

import javax.swing.*;

public class SettingsPanel extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final String INTERNET_LABEL = "Internet";
	private static final String GRAPH_SETTINGS_LABEL = "Graph Settings";
	private static final String VISUALIZATION_LABEL = "Node Visualization";
	private static final String SIMULATION_LABEL = "Simulation";
	private static final String EXPORT_LABEL = "Export";

	private final JTabbedPane tabbedPanel = new JTabbedPane();
	private final InternetConnectionDialog internetSettings = new InternetConnectionDialog();
	private final GraphSettingsDialog graphSettings = new GraphSettingsDialog();
	private final VisualizationDialog visualizationSettings = new VisualizationDialog();
	private final SimulationSettingsDialog simulationSettings = new SimulationSettingsDialog();
	private final ExportSettingsDialog exportSettings = new ExportSettingsDialog();

	public SettingsPanel(int type) {
		final JOptionPane optionPanel = new JOptionPane(tabbedPanel, JOptionPane.PLAIN_MESSAGE);
		final JButton cancel = new JButton("cancel");
		final JButton acceptButton = new JButton("accept");
		final JButton defaultButton = new JButton("default");
		final JButton[] buttons = { acceptButton, defaultButton, cancel };
		optionPanel.setOptions(buttons);
		setTitle("Settings");
		setModal(true);
		setContentPane(optionPanel);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		cancel.addActionListener(e -> onCancelClicked());
		defaultButton.addActionListener(e -> onDefaultClicked());
		acceptButton.addActionListener(e -> onAcceptClicked());
		tabbedPanel.addTab(INTERNET_LABEL, null, internetSettings, INTERNET_LABEL);
		tabbedPanel.addTab(GRAPH_SETTINGS_LABEL, null, graphSettings.getPanel(), GRAPH_SETTINGS_LABEL);
		tabbedPanel.addTab(VISUALIZATION_LABEL, null, visualizationSettings.getPanel(), VISUALIZATION_LABEL);
		tabbedPanel.addTab(SIMULATION_LABEL, null, simulationSettings.getPanel(), SIMULATION_LABEL);
		tabbedPanel.addTab(EXPORT_LABEL, null, exportSettings.getPanel(), EXPORT_LABEL);
		tabbedPanel.setSelectedIndex(type);
		setSize(300, 300);
		setLocationRelativeTo(MainWindow.getInstance().getFrame());
		pack();
		// On linux the settings window sometimes moves behind the main window, so we
		// force it to always be on top
		// setAlwaysOnTop(true);
		setModal(false);
		requestFocus();
		setResizable(false);
		setVisible(true);
	}

	private void onDefaultClicked() {
		String tab_name = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());
		switch (tab_name) {
		case INTERNET_LABEL:
			internetSettings.applyDefaults();
			break;
		case GRAPH_SETTINGS_LABEL:
			graphSettings.applyDefaults();
			break;
		case VISUALIZATION_LABEL:
			visualizationSettings.setDefaultYamlPath();
			break;
		case SIMULATION_LABEL:
			simulationSettings.applyDefaults();
			break;
		case EXPORT_LABEL:
			exportSettings.applyDefaults();
		}
	}

	private void onAcceptClicked() {
		String tabName = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());
		switch (tabName) {
		case INTERNET_LABEL:
			setVisible(!internetSettings.applyNewSettings());
			break;
		case GRAPH_SETTINGS_LABEL:
			setVisible(!graphSettings.applyNewSettings());
			break;
		case VISUALIZATION_LABEL:
			visualizationSettings.acceptConfig();
			dispose();
			break;
		case SIMULATION_LABEL:
			setVisible(!simulationSettings.applyNewSettings());
			break;
		case EXPORT_LABEL:
			setVisible(!exportSettings.applyNewSettings());
		}
	}

	private void onCancelClicked() {
		String tabName = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());
		switch (tabName) {
		case GRAPH_SETTINGS_LABEL:
			setVisible(!graphSettings.onCancelClick());
			break;
		}
		setVisible(false);
	}
}
