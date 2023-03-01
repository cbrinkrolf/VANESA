package configurations.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import gui.MainWindow;
import gui.PopUpDialog;

public class Settings extends JDialog implements ActionListener {
    private static final long serialVersionUID = -4497946706066898835L;
    private static final String INTERNET_LABEL = "Internet";
    private static final String GRAPH_SETTINGS_LABEL = "Graph Settings";
    private static final String VISUALIZATION_LABEL = "Visualization";

    private final JButton defaultButton = new JButton("default");

    private final JTabbedPane tabbedPanel = new JTabbedPane();
    private final InternetConnectionDialog internetSettings = new InternetConnectionDialog();
    private final GraphSettingsDialog graphSettings = new GraphSettingsDialog();
    private final VisualizationDialog visualizationSettings = new VisualizationDialog();

    public Settings(int type) {
        JOptionPane optionPanel = new JOptionPane(tabbedPanel, JOptionPane.PLAIN_MESSAGE);
        JButton cancel = new JButton("cancel");
        JButton acceptButton = new JButton("accept");
        JButton[] buttons = {acceptButton, defaultButton, cancel};
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
        tabbedPanel.setSelectedIndex(type);
        enableSettings(true);
        setSize(300, 300);
        setLocationRelativeTo(MainWindow.getInstance().getFrame());
        pack();
        setResizable(false);
        setVisible(true);
    }

    public void enableSettings(boolean enable) {
        internetSettings.enableDisplay(enable);
        defaultButton.setEnabled(enable);
        graphSettings.getPanel().setEnabled(enable);
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
        }
    }

    private void onAcceptClicked() {
        String tab_name = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());
        switch (tab_name) {
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
        }
    }

    private void onCancelClicked() {
        setVisible(false);
    }

    public void actionPerformed(ActionEvent e) {
        if ("ok".equals(e.getActionCommand())) {
            boolean applyAll = true;
            if (!internetSettings.applyNewSettings())
                applyAll = false;
            else if (!graphSettings.applyNewSettings())
                applyAll = false;
            if (applyAll) {
                setVisible(false);
            } else {
                PopUpDialog.getInstance().show("Error", "Error found - please change settings!");
            }
        }
    }
}
