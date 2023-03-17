package configurations.gui;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;

import configurations.NetworkSettings;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import gui.PopUpDialog;
import net.miginfocom.swing.MigLayout;

/**
 * @author Sebastian
 */
public class GraphSettingsDialog {
	private JPanel panel;
	private NetworkSettings settings = NetworkSettings.getInstance();

	private ButtonGroup group = new ButtonGroup();
	private JRadioButton only_name = new JRadioButton("", true);
	private JRadioButton only_label = new JRadioButton("", true);
	private JRadioButton nothing = new JRadioButton("", true);
	private JRadioButton name_label = new JRadioButton("", true);

	private ButtonGroup group2 = new ButtonGroup();
	private JRadioButton only_name_e = new JRadioButton("", true);
	private JRadioButton only_label_e = new JRadioButton("", true);
	private JRadioButton nothing_e = new JRadioButton("", true);
	private JRadioButton name_label_e = new JRadioButton("", true);

	private ButtonGroup group3 = new ButtonGroup();
	private JRadioButton black = new JRadioButton("", true);
	private JRadioButton white = new JRadioButton("", true);

	private ButtonGroup group4 = new ButtonGroup();
	private JRadioButton yes = new JRadioButton("", true);
	private JRadioButton no = new JRadioButton("", true);

	private JSlider opacityslider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);

	private MainWindow w = MainWindow.getInstance();
	private GraphContainer con = GraphContainer.getInstance();;

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

		group4.add(yes);
		group4.add(no);

		if (settings.getDrawEdges())
			yes.setSelected(true);
		else
			no.setSelected(true);

		opacityslider.setValue(settings.getEdgeOpacity());

		// Container contentPane = getContentPane();
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

		panel.add(new JLabel("What kind of background do you prefer?"), "span 4");
		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel connectionPanel3 = new JPanel();

		connectionPanel3.add(white);
		connectionPanel3.add(new JLabel("white  |"));

		connectionPanel3.add(black);
		connectionPanel3.add(new JLabel("black"));

		panel.add(connectionPanel3, "wrap,align left, gap 10, gaptop 2");

		// Edge drawing
		panel.add(new JLabel("Should the graph edges be displayed?"), "span 4");
		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel edgedrawpanel = new JPanel();

		edgedrawpanel.add(yes);
		edgedrawpanel.add(new JLabel("yes   |"));
		edgedrawpanel.add(no);
		edgedrawpanel.add(new JLabel("no"));

		panel.add(edgedrawpanel, "wrap,align left, gap 10, gaptop 2");

		// Edge opacity
		panel.add(new JLabel("opacity of the edges?"), "span 3");
		panel.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel edgeopacitypanel = new JPanel();

		opacityslider.setMajorTickSpacing(50);
//		opacityslider.setMinorTickSpacing();
		opacityslider.setPaintTicks(true);
		opacityslider.setPaintLabels(true);
		panel.add(opacityslider);

		panel.add(edgeopacitypanel, "wrap,align left, gap 10, gaptop 2");
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

		yes.setEnabled(enabled);
		no.setEnabled(enabled);
	}

	public boolean applyDefaults() {
		if (con.containsPathway() && GraphInstance.getPathway().hasGotAtLeastOneElement()) {
			only_label.setSelected(true);
			// settings.setNodeLabel(1);
			only_label_e.setSelected(true);
			// settings.setEdgeLabel(1);
			white.setSelected(true);
			GraphInstance.getPathway().changeBackground("white");
			settings.setBackgroundColor(false);
			yes.setSelected(true);
		} else {
			PopUpDialog.getInstance().show("Error", "Please create a network before.");
			return false;
		}
		return true;
	}

	public boolean applyNewSettings() {
		if (con.containsPathway() && GraphInstance.getPathway().hasGotAtLeastOneElement()) {
			if (only_label.isSelected()) {
				settings.setNodeLabel(1);
			} else if (only_name.isSelected()) {
				settings.setNodeLabel(2);
			} else if (name_label.isSelected()) {
				settings.setNodeLabel(3);
			} else if (nothing.isSelected()) {
				settings.setNodeLabel(4);
			}
			if (only_label_e.isSelected()) {
				settings.setEdgeLabel(1);
			} else if (only_name_e.isSelected()) {
				System.out.println("selected");
				settings.setEdgeLabel(2);
			} else if (name_label_e.isSelected()) {
				settings.setEdgeLabel(3);
			} else if (nothing_e.isSelected()) {
				settings.setEdgeLabel(4);
			}
			if (black.isSelected()) {
				settings.setBackgroundColor(true);
				GraphInstance.getPathway().changeBackground("black");
			} else if (white.isSelected()) {
				settings.setBackgroundColor(false);
				GraphInstance.getPathway().changeBackground("white");
			}
			if (yes.isSelected()) {
				settings.setDrawEdges(true);
			} else {
				settings.setDrawEdges(false);
				no.setSelected(true);
			}
			settings.setEdgeOpacity(opacityslider.getValue());
			con.getPathway(w.getCurrentPathway()).getGraph().getEdgeDrawPaintFunction().updateEdgeAlphaValue();
		} else {
			PopUpDialog.getInstance().show("Error", "Please create a network before.");
			return false;
		}
		return true;
	}
}
