/**
 * 
 */
package configurations.gui;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.Container;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;
import configurations.NetworkSettings;
import configurations.NetworkSettingsSingelton;

/**
 * @author Sebastian
 * 
 */
public class GraphSettingsDialog extends JFrame {

	JPanel panel;
	JOptionPane pane;
	NetworkSettings settings = NetworkSettingsSingelton.getInstance();

	ButtonGroup group = new ButtonGroup();
	JRadioButton only_name = new JRadioButton("", true);
	JRadioButton only_label = new JRadioButton("", true);
	JRadioButton nothing = new JRadioButton("", true);
	JRadioButton name_label = new JRadioButton("", true);

	ButtonGroup group2 = new ButtonGroup();
	JRadioButton only_name_e = new JRadioButton("", true);
	JRadioButton only_label_e = new JRadioButton("", true);
	JRadioButton nothing_e = new JRadioButton("", true);
	JRadioButton name_label_e = new JRadioButton("", true);

	ButtonGroup group3 = new ButtonGroup();
	JRadioButton black = new JRadioButton("", true);
	JRadioButton white = new JRadioButton("", true);

	MainWindow w = MainWindowSingelton.getInstance();
	GraphInstance graphInstance = new GraphInstance();
	GraphContainer con = ContainerSingelton.getInstance();;

	/**
	 * 
	 */
	public GraphSettingsDialog() {

		group.add(only_label);
		group.add(only_name);
		group.add(name_label);
		group.add(nothing);

		if (settings.getNodeLabel() == 1) {
			only_label.setSelected(true);
		} else if (settings.getNodeLabel() == 2) {
			only_name.setSelected(true);
		} else if (settings.getNodeLabel() == 3) {
			name_label.setSelected(true);
		} else if (settings.getNodeLabel() == 4) {
			nothing.setSelected(true);
		}

		group2.add(only_label_e);
		group2.add(only_name_e);
		group2.add(name_label_e);
		group2.add(nothing_e);

		if (settings.getEdgeLabel() == 1) {
			only_label_e.setSelected(true);
		} else if (settings.getEdgeLabel() == 2) {
			only_name_e.setSelected(true);
		} else if (settings.getEdgeLabel() == 3) {
			name_label_e.setSelected(true);
		} else if (settings.getEdgeLabel() == 4) {
			nothing_e.setSelected(true);
		}

		group3.add(white);
		group3.add(black);

		if (settings.isBackgroundColor()) {
			black.setSelected(true);
		} else {
			white.setSelected(false);
		}

		Container contentPane = getContentPane();
		MigLayout layout = new MigLayout("", "[left]");

		panel = new JPanel(layout);

		panel.add(new JLabel("Which node label should be displayed?"), "span 4");
		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel connectionPanel = new JPanel();

		connectionPanel.add(only_label);
		connectionPanel.add(new JLabel("label  |"));
		connectionPanel.add(only_name);

		connectionPanel.add(new JLabel("name  |"));
		connectionPanel.add(name_label);

		connectionPanel.add(new JLabel("both  |"));
		connectionPanel.add(nothing);

		connectionPanel.add(new JLabel("nothing  |"));

		panel.add(connectionPanel, "span,wrap ,growx ,gap 10, gaptop 2");

		panel.add(new JLabel("Which edge label should be displayed?"), "span 4");
		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel connectionPanel2 = new JPanel();

		connectionPanel2.add(only_label_e);
		connectionPanel2.add(new JLabel("label  |"));
		connectionPanel2.add(only_name_e);

		connectionPanel2.add(new JLabel("name  |"));
		connectionPanel2.add(name_label_e);

		connectionPanel2.add(new JLabel("both  |"));
		connectionPanel2.add(nothing_e);

		connectionPanel2.add(new JLabel("nothing  |"));

		panel.add(connectionPanel2, "span,wrap ,growx ,gap 10, gaptop 2");

		panel.add(new JLabel("What kind of background do you prefer?"),
				"span 4");
		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel connectionPanel3 = new JPanel();

		connectionPanel3.add(white);
		connectionPanel3.add(new JLabel("white  |"));

		connectionPanel3.add(black);
		connectionPanel3.add(new JLabel("black"));

		panel.add(connectionPanel3, "wrap,align left, gap 10, gaptop 2");

	}

	public JPanel getPanel() {
		return panel;
	}

	public void enableDispaly(boolean enabled) {

		only_label.setEnabled(enabled);
		only_name.setEnabled(enabled);
		name_label.setEnabled(enabled);
		nothing.setEnabled(enabled);

		only_label_e.setEnabled(enabled);
		only_name_e.setEnabled(enabled);
		name_label_e.setEnabled(enabled);
		nothing_e.setEnabled(enabled);

		white.setEnabled(enabled);
		black.setEnabled(enabled);

	}

	public boolean applyDefaults() {

		if (con.containsPathway()) {
			if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
				only_label.setSelected(true);
				//settings.setNodeLabel(1);

				only_label_e.setSelected(true);
				//settings.setEdgeLabel(1);

				white.setSelected(true);
				graphInstance.getPathway().changeBackground("white");
				settings.setBackgroundColor(false);

			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
				return false;
			}
		} else {
			JOptionPane.showMessageDialog(w, "Please create a network before.");
			return false;
		}
		return true;
	}

	public boolean applyNewSettings() {

		if (con.containsPathway()) {
			if (graphInstance.getPathway().hasGotAtLeastOneElement()) {

				if (only_label.isSelected()) {
					//settings.setNodeLabel(1);
				} else if (only_name.isSelected()) {
					//settings.setNodeLabel(2);
				} else if (name_label.isSelected()) {
					//settings.setNodeLabel(3);
				} else if (nothing.isSelected()) {
					//settings.setNodeLabel(4);
				}

				if (only_label_e.isSelected()) {
					//settings.setEdgeLabel(1);
				} else if (only_name_e.isSelected()) {
					//settings.setEdgeLabel(2);
				} else if (name_label_e.isSelected()) {
					//settings.setEdgeLabel(3);
				} else if (nothing_e.isSelected()) {
					//settings.setEdgeLabel(4);
				}

				if (black.isSelected()) {
					settings.setBackgroundColor(true);
					graphInstance.getPathway().changeBackground("black");
				} else if (white.isSelected()) {
					settings.setBackgroundColor(false);
					graphInstance.getPathway().changeBackground("white");
				}

			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
				return false;
			}
		} else {
			JOptionPane.showMessageDialog(w, "Please create a network before.");
			return false;
		}
		return true;
	}
}
