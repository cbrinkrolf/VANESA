package configurations.gui;

import configurations.Settings;
import configurations.SettingsChangedListener;
import configurations.Workspace;
import gui.MainWindow;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingsPanel extends JDialog implements SettingsChangedListener {
	private static final long serialVersionUID = -1788847745595402505L;
	private static final String GENERAL_TAB_LABEL = "General";
	private static final String GRAPH_TAB_LABEL = "Graph";
	private static final String VISUALIZATION_TAB_LABEL = "Node Visualization";

	private final JTabbedPane tabbedPanel = new JTabbedPane();
	private final GeneralSettingsDialog generalSettings = new GeneralSettingsDialog();
	private final GraphSettingsDialog graphSettings = new GraphSettingsDialog();
	private final VisualizationDialog visualizationSettings = new VisualizationDialog();

	public SettingsPanel(int type) {
		final JOptionPane optionPanel = new JOptionPane(tabbedPanel, JOptionPane.PLAIN_MESSAGE);
		final JButton cancel = new JButton("cancel");
		final JButton acceptButton = new JButton("accept");
		final JButton[] buttons = { acceptButton, cancel };
		optionPanel.setOptions(buttons);
		setTitle("Settings");
		setModal(true);
		setContentPane(optionPanel);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		cancel.addActionListener(e -> onCancelClicked());
		acceptButton.addActionListener(e -> onAcceptClicked());
		tabbedPanel.addTab(GENERAL_TAB_LABEL, null, generalSettings, GENERAL_TAB_LABEL);
		tabbedPanel.addTab(GRAPH_TAB_LABEL, null, graphSettings, GRAPH_TAB_LABEL);
		tabbedPanel.addTab(VISUALIZATION_TAB_LABEL, null, visualizationSettings, VISUALIZATION_TAB_LABEL);
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
		Workspace.addSettingsChangedListener(this);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				onCancelClicked();
			}
		});
	}

	private void onAcceptClicked() {
		String tabName = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());
		boolean allowedToClose = true;
		switch (tabName) {
		case GENERAL_TAB_LABEL:
			allowedToClose = generalSettings.applySettings();
			break;
		case GRAPH_TAB_LABEL:
			allowedToClose = graphSettings.applySettings();
			break;
		case VISUALIZATION_TAB_LABEL:
			allowedToClose = visualizationSettings.applySettings();
			break;
		}
		if (allowedToClose) {
			setVisible(false);
			onClose();
		}
	}

	private void onCancelClicked() {
		String tabName = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());
		if (tabName.equals(GRAPH_TAB_LABEL)) {
			graphSettings.onCancelClick();
			setVisible(false);
		}
		setVisible(false);
		onClose();
	}

	private void onClose() {
		Workspace.removeSettingsChangedListener(this);
	}

	@Override
	public void onSettingsChanged(final Settings settings) {
		generalSettings.updateSettings(settings);
		graphSettings.updateSettings(settings);
	}
}
