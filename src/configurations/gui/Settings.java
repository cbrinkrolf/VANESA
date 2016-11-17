package configurations.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import gui.MainWindowSingleton;

public class Settings extends JDialog implements ActionListener {
	private static final long serialVersionUID = -4497946706066898835L;

	private JButton cancel = new JButton("cancel");
	private JButton defaultButton = new JButton("default");
	private JButton acceptButton = new JButton("accept");
	private JButton[] buttons = { acceptButton, defaultButton, cancel };

	private JTabbedPane tabbedPane = new JTabbedPane();
	private ConnectionTab database = new ConnectionTab();
	private InternetConnectionDialog internet = new InternetConnectionDialog();
	private GraphAlignmentDialog ali = new GraphAlignmentDialog();
	private GraphSettingsDialog gset = new GraphSettingsDialog();
	private VisualizationDialog visDialog = new VisualizationDialog();

	private String database_label = new String("Database Settings");
	private String interner_label = new String("Internet");
	private String ali_label = new String("Graph Alignment");
	private String gset_label = new String("Graph Settings");
	private String visDialog_label = new String("Visualization");

	private JOptionPane optionPane;

	public Settings(int type) {
		optionPane = new JOptionPane(tabbedPane, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		this.setTitle("Settings");
		this.setModal(true);

		this.setContentPane(optionPane);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");
		defaultButton.addActionListener(this);
		defaultButton.setActionCommand("default");
		acceptButton.addActionListener(this);
		acceptButton.setActionCommand("accept");

		tabbedPane.addTab(database_label, null, database, database_label);
		tabbedPane.addTab(interner_label, null, internet, interner_label);
		tabbedPane.addTab(ali_label, null, ali.getPanel(), ali_label);
		tabbedPane.addTab(gset_label, null, gset.getPanel(), gset_label);
		tabbedPane.addTab(visDialog_label, null, visDialog.getPanel(), visDialog_label);

		tabbedPane.setSelectedIndex(type);
		enableSettings(true);

		this.setSize(300, 300);
		this.setLocationRelativeTo(MainWindowSingleton.getInstance());
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
	}

	public void enableSettings(boolean enable) {
		database.enableDispaly(enable);
		internet.enableDispaly(enable);
		defaultButton.setEnabled(enable);
		gset.setEnabled(enable);
	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		if ("cancel".equals(event)) {
			this.setVisible(false);

		} else if ("default".equals(event)) {
			String tab_name = tabbedPane.getTitleAt(tabbedPane
					.getSelectedIndex());

			if (tab_name.equals(database_label))
				database.applyDefaults();
			else if (tab_name.equals(interner_label))
				internet.applyDefaults();
			else if (tab_name.equals(ali_label))
				ali.applyDefaults();
			else if (tab_name.equals(gset_label))
				gset.applyDefaults();
			else if (tab_name.equals(visDialog_label)){
				visDialog.setDefaultYamlPath();
			}
		} else if ("accept".equals(event)) {
			String tab_name = tabbedPane.getTitleAt(tabbedPane
					.getSelectedIndex());

			if (tab_name.equals(database_label))
				this.setVisible(!database.applyNewSettings());
			else if (tab_name.equals(interner_label))
				this.setVisible(!internet.applyNewSettings());
			else if (tab_name.equals(ali_label))
				this.setVisible(!ali.applyNewSettings());
			else if (tab_name.equals(gset_label))
				this.setVisible(!gset.applyNewSettings());
			else if (tab_name.equals(visDialog_label)){
				visDialog.acceptConfig();
				this.dispose();
			}

			//this.setVisible(false);
		} else if ("ok".equals(event)) {
			boolean apply_all = true;

			if (!database.applyNewSettings())
				apply_all = false;
			else if (!internet.applyNewSettings())
				apply_all = false;
			else if (!ali.applyNewSettings())
				apply_all = false;
			else if (!gset.applyNewSettings())
				apply_all = false;

			if (apply_all) {
				this.setVisible(false);
			} else {
				JOptionPane.showMessageDialog(this,
						"Error found - please change settings!");
			}
		}
	}
}
