package configurations.gui;

import gui.MainWindow;

import javax.swing.*;

public class SettingsPanel extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final String GENERAL_TAB_LABEL = "General";
	private static final String GRAPH_TAB_LABEL = "Graph";
	private static final String VISUALIZATION_TAB_LABEL = "Node Visualization";

	private final JTabbedPane tabbedPanel = new JTabbedPane();
	private final GeneralSettingsDialog internetSettings = new GeneralSettingsDialog();
	private final GraphSettingsDialog graphSettings = new GraphSettingsDialog();
	private final VisualizationDialog visualizationSettings = new VisualizationDialog();

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
		tabbedPanel.addTab(GENERAL_TAB_LABEL, null, internetSettings, GENERAL_TAB_LABEL);
		tabbedPanel.addTab(GRAPH_TAB_LABEL, null, graphSettings.getPanel(), GRAPH_TAB_LABEL);
		tabbedPanel.addTab(VISUALIZATION_TAB_LABEL, null, visualizationSettings.getPanel(), VISUALIZATION_TAB_LABEL);
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
		case GENERAL_TAB_LABEL:
			internetSettings.applyDefaults();
			break;
		case GRAPH_TAB_LABEL:
			graphSettings.applyDefaults();
			break;
		case VISUALIZATION_TAB_LABEL:
			visualizationSettings.setDefaultYamlPath();
			break;
		}
	}

	private void onAcceptClicked() {
		String tabName = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());
		switch (tabName) {
		case GENERAL_TAB_LABEL:
			setVisible(!internetSettings.applyNewSettings());
			break;
		case GRAPH_TAB_LABEL:
			setVisible(!graphSettings.applyNewSettings());
			break;
		case VISUALIZATION_TAB_LABEL:
			visualizationSettings.acceptConfig();
			dispose();
			break;
		}
	}

	private void onCancelClicked() {
		String tabName = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());
		if (tabName.equals(GRAPH_TAB_LABEL)) {
			setVisible(!graphSettings.onCancelClick());
		}
		setVisible(false);
	}
}
