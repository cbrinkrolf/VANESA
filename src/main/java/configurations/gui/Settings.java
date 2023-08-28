package configurations.gui;

import gui.MainWindow;

import javax.swing.*;

public class Settings extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final String INTERNET_LABEL = "Internet";
	private static final String GRAPH_SETTINGS_LABEL = "Graph Settings";
	private static final String VISUALIZATION_LABEL = "Node Visualization";
	private static final String SIMULATION_LABEL = "Simulation";

	private final JButton defaultButton = new JButton("default");

	private final JTabbedPane tabbedPanel = new JTabbedPane();
	private final InternetConnectionDialog internetSettings = new InternetConnectionDialog();
	private final GraphSettingsDialog graphSettings = new GraphSettingsDialog();
	private final VisualizationDialog visualizationSettings = new VisualizationDialog();
	private final SimulationSettingsDialog simulationSettings = new SimulationSettingsDialog();

	public Settings(int type) {
		JOptionPane optionPanel = new JOptionPane(tabbedPanel, JOptionPane.PLAIN_MESSAGE);
		JButton cancel = new JButton("cancel");
		JButton acceptButton = new JButton("accept");
		JButton[] buttons = { acceptButton, defaultButton, cancel };
		optionPanel.setOptions(buttons);
		setTitle("Settings");
		setModal(true);
		setContentPane(optionPanel);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		cancel.addActionListener(e -> onCancelClicked());
		defaultButton.addActionListener(e -> onDefaultClicked());
		acceptButton.addActionListener(e -> onAcceptClicked());
		tabbedPanel.addTab(INTERNET_LABEL, null, internetSettings.getPanel(), INTERNET_LABEL);
		tabbedPanel.addTab(GRAPH_SETTINGS_LABEL, null, graphSettings.getPanel(), GRAPH_SETTINGS_LABEL);
		tabbedPanel.addTab(VISUALIZATION_LABEL, null, visualizationSettings.getPanel(), VISUALIZATION_LABEL);
		tabbedPanel.addTab(SIMULATION_LABEL, null, simulationSettings.getPanel(), SIMULATION_LABEL);
		tabbedPanel.setSelectedIndex(type);
		setSize(300, 300);
		setLocationRelativeTo(MainWindow.getInstance().getFrame());
		pack();
		// On linux the settings window sometimes moves behind the main window, so we
		// force it to always be on top
		setAlwaysOnTop(true);
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
		}
	}

	private void onCancelClicked() {
		setVisible(false);
	}
}
