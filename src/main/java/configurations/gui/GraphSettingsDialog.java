package configurations.gui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.drjekyll.fontchooser.FontDialog;

import configurations.GraphSettings;
import configurations.SettingsManager;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;
import util.MyJFormattedTextField;
import util.MyNumberFormat;

/**
 * @author Sebastian
 */
public class GraphSettingsDialog {
	private JPanel panel;
	private JPanel panelLeft;
	private JPanel panelRight;
	private GraphSettings settings = GraphSettings.getInstance();

	private ButtonGroup groupNodes = new ButtonGroup();
	private JRadioButton only_name = new JRadioButton("", true);
	private JRadioButton only_label = new JRadioButton("", true);
	private JRadioButton nothing = new JRadioButton("", true);
	private JRadioButton name_label = new JRadioButton("", true);

	private ButtonGroup groupEdges = new ButtonGroup();
	private JRadioButton only_name_e = new JRadioButton("", true);
	private JRadioButton only_label_e = new JRadioButton("", true);
	private JRadioButton nothing_e = new JRadioButton("", true);
	private JRadioButton name_label_e = new JRadioButton("", true);

	private ButtonGroup groupBackground = new ButtonGroup();
	private JRadioButton black = new JRadioButton("", true);
	private JRadioButton white = new JRadioButton("", true);

	private ButtonGroup groupShowEdges = new ButtonGroup();
	private JRadioButton yes = new JRadioButton("", true);
	private JRadioButton no = new JRadioButton("", true);

	private JSlider opacityslider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
	private int edgeOpacityOld = settings.getEdgeOpacity();

	private MyJFormattedTextField pixelOffset = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());

	private JLabel vertexFontLabel;
	private JLabel edgeFontLabel;

	private JButton vertexFontChooser;
	private JButton edgeFontChooser;

	private JCheckBox defaultVertexFont;
	private JCheckBox defaultEdgeFont;

	private Font vertexFontOld = settings.getVertexFont();
	private Font edgeFontOld = settings.getEdgeFont();

	private JCheckBox omitInvisibleNodes;
	private JCheckBox disabledAntiAliasing;

	private JCheckBox useDefaultTransformators;
	private JCheckBox useDefaultTransformatorsSatellite;

	private MainWindow w = MainWindow.getInstance();
	private GraphContainer con = GraphContainer.getInstance();

	public GraphSettingsDialog() {

		groupNodes.add(only_label);
		groupNodes.add(only_name);
		groupNodes.add(name_label);
		groupNodes.add(nothing);

		if (settings.getNodeLabel() == GraphSettings.SHOW_LABEL) {
			only_label.setSelected(true);
		} else if (settings.getNodeLabel() == GraphSettings.SHOW_NAME) {
			only_name.setSelected(true);
		} else if (settings.getNodeLabel() == GraphSettings.SHOW_LABEL_AND_NAME) {
			name_label.setSelected(true);
		} else if (settings.getNodeLabel() == GraphSettings.SHOW_NONE) {
			nothing.setSelected(true);
		}

		groupEdges.add(only_label_e);
		groupEdges.add(only_name_e);
		groupEdges.add(name_label_e);
		groupEdges.add(nothing_e);

		if (settings.getEdgeLabel() == GraphSettings.SHOW_LABEL) {
			only_label_e.setSelected(true);
		} else if (settings.getEdgeLabel() == GraphSettings.SHOW_NAME) {
			only_name_e.setSelected(true);
		} else if (settings.getEdgeLabel() == GraphSettings.SHOW_LABEL_AND_NAME) {
			name_label_e.setSelected(true);
		} else if (settings.getEdgeLabel() == GraphSettings.SHOW_NONE) {
			nothing_e.setSelected(true);
		}

		groupBackground.add(white);
		groupBackground.add(black);

		if (settings.isBackgroundColor()) {
			black.setSelected(true);
		} else {
			white.setSelected(false);
		}

		groupShowEdges.add(yes);
		groupShowEdges.add(no);

		if (settings.getDrawEdges())
			yes.setSelected(true);
		else
			no.setSelected(true);

		opacityslider.setValue(settings.getEdgeOpacity());

		// Container contentPane = getContentPane();
		MigLayout layout = new MigLayout("", "[left]");

		panelLeft = new JPanel(layout);

		panelLeft.add(new JLabel("Which node label should be displayed?"), "span 4");
		panelLeft.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel connectionPanel = new JPanel();

		connectionPanel.add(only_label);
		connectionPanel.add(new JLabel("label  |"));
		connectionPanel.add(only_name);

		connectionPanel.add(new JLabel("name  |"));
		connectionPanel.add(name_label);

		connectionPanel.add(new JLabel("both  |"));
		connectionPanel.add(nothing);

		connectionPanel.add(new JLabel("nothing  |"));

		panelLeft.add(connectionPanel, "span,wrap ,growx ,gap 10, gaptop 2");

		panelLeft.add(new JLabel("Which edge label should be displayed?"), "span 4");
		panelLeft.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel connectionPanel2 = new JPanel();

		connectionPanel2.add(only_label_e);
		connectionPanel2.add(new JLabel("label  |"));
		connectionPanel2.add(only_name_e);

		connectionPanel2.add(new JLabel("name  |"));
		connectionPanel2.add(name_label_e);

		connectionPanel2.add(new JLabel("both  |"));
		connectionPanel2.add(nothing_e);

		connectionPanel2.add(new JLabel("nothing  |"));

		panelLeft.add(connectionPanel2, "span,wrap ,growx ,gap 10, gaptop 2");

		panelLeft.add(new JLabel("What kind of background do you prefer?"), "span 4");
		panelLeft.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel connectionPanel3 = new JPanel();

		connectionPanel3.add(white);
		connectionPanel3.add(new JLabel("white  |"));

		connectionPanel3.add(black);
		connectionPanel3.add(new JLabel("black"));

		panelLeft.add(connectionPanel3, "wrap,align left, gap 10, gaptop 2");

		// Edge drawing
		panelLeft.add(new JLabel("Should the graph edges be displayed?"), "span 4");
		panelLeft.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel edgedrawpanel = new JPanel();

		edgedrawpanel.add(yes);
		edgedrawpanel.add(new JLabel("yes   |"));
		edgedrawpanel.add(no);
		edgedrawpanel.add(new JLabel("no"));

		panelLeft.add(edgedrawpanel, "wrap,align left, gap 10, gaptop 2");

		// Edge opacity
		panelLeft.add(new JLabel("Opacity of the edges?"), "span 3");
		panelLeft.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		JPanel edgeopacitypanel = new JPanel();

		opacityslider.setMajorTickSpacing(50);
		opacityslider.setToolTipText(
				"Opacity of edges, value between 0 and 255. A value of 0 draws the edges fully transparent.");
//		opacityslider.setMinorTickSpacing();
		opacityslider.setPaintTicks(true);
		opacityslider.setPaintLabels(true);
		opacityslider.setEnabled(true);
		opacityslider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				settings.setEdgeOpacity(opacityslider.getValue());
				if (con.containsPathway()) {
					GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
				}

			}
		});
		panelLeft.add(opacityslider);

		panelLeft.add(edgeopacitypanel, "wrap,align left, gap 10, gaptop 2");

		// pixel offset for edge selecting
		panelLeft.add(new JLabel("Pixel offset for edge picking?"), "span 3");
		panelLeft.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		// panel.add(new JLabel("Pixel offset:"));
		pixelOffset.setColumns(3);
		pixelOffset.setText(String.valueOf(settings.getPixelOffset()));
		pixelOffset.setValue(settings.getPixelOffset());
		panelLeft.add(pixelOffset, "wrap");

		vertexFontLabel = new JLabel("default font");
		panelLeft.add(new JLabel("Font for vertex label?"), "span 3");
		panelLeft.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		this.evaluateVertexFontLabel();

		panelLeft.add(vertexFontLabel, "");
		vertexFontChooser = new JButton("choose font");
		vertexFontChooser.addActionListener(e -> onChooseVertexFontClick());

		panelLeft.add(vertexFontChooser, "");

		defaultVertexFont = new JCheckBox();
		defaultVertexFont.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				vertexFontChooser.setEnabled(!defaultVertexFont.isSelected());
				if (defaultVertexFont.isSelected()) {
					settings.setVertexFont(null);
				}
				if (con.containsPathway()) {
					GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
				}
			}
		});
		if (settings.getVertexFont() == null) {
			defaultVertexFont.setSelected(true);
		}
		panelLeft.add(defaultVertexFont, "wrap");

		edgeFontLabel = new JLabel("default font");
		panelLeft.add(new JLabel("Font for edge label?"), "span 3");
		panelLeft.add(new JSeparator(), "span, growx, wrap 5, gaptop 10, gap 5");

		this.evaluateEdgeFontLabel();

		panelLeft.add(edgeFontLabel, "");
		edgeFontChooser = new JButton("choose font");
		edgeFontChooser.addActionListener(e -> onChooseEdgeFontClick());

		panelLeft.add(edgeFontChooser, "");

		defaultEdgeFont = new JCheckBox();
		defaultEdgeFont.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				edgeFontChooser.setEnabled(!defaultEdgeFont.isSelected());
				if (defaultEdgeFont.isSelected()) {
					settings.setEdgeFont(null);
				}
				if (con.containsPathway()) {
					GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
				}
			}
		});
		if (settings.getEdgeFont() == null) {
			defaultEdgeFont.setSelected(true);
		}
		panelLeft.add(defaultEdgeFont, "wrap");

		panelRight = new JPanel(new MigLayout("", "[left]"));

		omitInvisibleNodes = new JCheckBox();
		omitInvisibleNodes.setText("omit invisible nodes");
		omitInvisibleNodes
				.setToolTipText("Omit drawing of invisible nodes. Pathway needs to be re-opened to be effective!");
		omitInvisibleNodes.setSelected(SettingsManager.getInstance().isOmitPaintInvisibleNodes());

		panelRight.add(omitInvisibleNodes, "wrap");

		disabledAntiAliasing = new JCheckBox();
		disabledAntiAliasing.setText("disable anti-aliasing");
		disabledAntiAliasing
				.setToolTipText("Increases graph drawing performance. Exported graph images are not affected!");
		disabledAntiAliasing.setSelected(SettingsManager.getInstance().isDisabledAntiAliasing());
		disabledAntiAliasing.addChangeListener(e -> onDeactiveAntiAliasingClicked());

		panelRight.add(disabledAntiAliasing, "wrap");

		useDefaultTransformators = new JCheckBox();
		useDefaultTransformators.setText("default transformators");
		useDefaultTransformators.setToolTipText("use default transformators to visualize the graph (faster)");
		useDefaultTransformators.setSelected(settings.isDefaultTransformators());
		useDefaultTransformators.addChangeListener(e -> onUseDefaultTransformatorsClicked());

		panelRight.add(useDefaultTransformators, "wrap");

		useDefaultTransformatorsSatellite = new JCheckBox();
		useDefaultTransformatorsSatellite.setText("default transformators (satellite view)");
		useDefaultTransformatorsSatellite
				.setToolTipText("use default transformators to visualize the satellite view of the graph (faster)");
		useDefaultTransformatorsSatellite.setSelected(settings.isDefaultTransformatorsSatellite());
		useDefaultTransformatorsSatellite.addChangeListener(e -> onUseDefaultTransformatorsSatelliteClicked());

		panelRight.add(useDefaultTransformatorsSatellite, "wrap");

		panel = new JPanel(new GridLayout(0, 2));
		panel.add(panelLeft);
		panel.add(panelRight);

	}

	public JPanel getPanel() {
		return panel;
	}

	public boolean applyDefaults() {
		// if (con.containsPathway() &&
		// GraphInstance.getPathway().hasGotAtLeastOneElement()) {
		only_label.setSelected(true);
		// settings.setNodeLabel(1);
		only_label_e.setSelected(true);
		// settings.setEdgeLabel(1);
		white.setSelected(true);
		if (con.containsPathway()) {
			GraphInstance.getPathway().changeBackground("white");
		}
		settings.setBackgroundColor(false);
		yes.setSelected(true);
		opacityslider.setValue(255);
		// pixelOffset.setText(String.valueOf(3));
		settings.setPixelOffset(3);
		// System.out.println(pixelOffset.getText());
		// System.out.println(pixelOffset.getValue());
		pixelOffset.setText(String.valueOf(settings.getPixelOffset()));
		pixelOffset.setValue(String.valueOf(settings.getPixelOffset()));
		defaultVertexFont.setSelected(true);
		defaultEdgeFont.setSelected(true);
		omitInvisibleNodes.setSelected(true);
		disabledAntiAliasing.setSelected(false);
		useDefaultTransformators.setSelected(false);
		useDefaultTransformatorsSatellite.setSelected(false);
		// } else {
		// PopUpDialog.getInstance().show("Error", "Please create a network before.");
		// return false;
		// }
		return true;
	}

	public boolean applyNewSettings() {

		if (only_label.isSelected()) {
			settings.setNodeLabel(GraphSettings.SHOW_LABEL);
		} else if (only_name.isSelected()) {
			settings.setNodeLabel(GraphSettings.SHOW_NAME);
		} else if (name_label.isSelected()) {
			settings.setNodeLabel(GraphSettings.SHOW_LABEL_AND_NAME);
		} else if (nothing.isSelected()) {
			settings.setNodeLabel(GraphSettings.SHOW_NONE);
		}
		if (only_label_e.isSelected()) {
			settings.setEdgeLabel(GraphSettings.SHOW_LABEL);
		} else if (only_name_e.isSelected()) {
			// System.out.println("selected");
			settings.setEdgeLabel(GraphSettings.SHOW_NAME);
		} else if (name_label_e.isSelected()) {
			settings.setEdgeLabel(GraphSettings.SHOW_LABEL_AND_NAME);
		} else if (nothing_e.isSelected()) {
			settings.setEdgeLabel(GraphSettings.SHOW_NONE);
		}
		if (black.isSelected()) {
			settings.setBackgroundColor(true);
			if (con.containsPathway()) {
				GraphInstance.getPathway().changeBackground("black");
			}
		} else if (white.isSelected()) {
			settings.setBackgroundColor(false);
			if (con.containsPathway()) {
				GraphInstance.getPathway().changeBackground("white");
			}
		}
		if (yes.isSelected()) {
			settings.setDrawEdges(true);
		} else {
			settings.setDrawEdges(false);
			no.setSelected(true);
		}
		settings.setEdgeOpacity(opacityslider.getValue());
		settings.setPixelOffset(Integer.parseInt(pixelOffset.getText()));
		if (con.containsPathway() && GraphInstance.getPathway().hasGotAtLeastOneElement()) {
			con.getPathway(w.getCurrentPathway()).getGraph().getEdgeDrawPaintFunction().updateEdgeAlphaValue();
		}

		SettingsManager.getInstance().setOmitPaintInvisibleNodes(omitInvisibleNodes.isSelected());
		SettingsManager.getInstance().setDisabledAntiAliasing(disabledAntiAliasing.isSelected());
		if (con.containsPathway()) {
			con.getPathway(w.getCurrentPathway()).getGraph().disableAntliasing(disabledAntiAliasing.isSelected());
		}
		settings.setDefaultTransformators(useDefaultTransformators.isSelected());
		settings.setDefaultTransformatorsSatellite(useDefaultTransformatorsSatellite.isSelected());

		return true;
	}

	public boolean onCancelClick() {
		settings.setEdgeOpacity(edgeOpacityOld);
		settings.setVertexFont(vertexFontOld);
		settings.setEdgeFont(edgeFontOld);
		useDefaultTransformators.setSelected(settings.isDefaultTransformators());
		useDefaultTransformatorsSatellite.setSelected(settings.isDefaultTransformatorsSatellite());
		if (con.containsPathway()) {
			con.getPathway(w.getCurrentPathway()).getGraph().disableAntliasing(settings.isDisabledAntiAliasing());
		}
		return true;
	}

	private void onChooseVertexFontClick() {
		FontDialog dialog = new FontDialog((Frame) null, "Select font for vertex labels", true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setSelectedFont(settings.getVertexFont());
		if (settings.getVertexFont() == null && con.containsPathway()) {
			dialog.setSelectedFont(GraphInstance.getPathway().getGraph().getVisualizationViewer().getFont());
		}
		dialog.setLocationRelativeTo(panelLeft);
		dialog.setAlwaysOnTop(true);
		dialog.requestFocus();
		dialog.setVisible(true);
		if (!dialog.isCancelSelected()) {
			// System.out.printf("Selected font is: %s%n", dialog.getSelectedFont());
			settings.setVertexFont(dialog.getSelectedFont());
			evaluateVertexFontLabel();
			panelLeft.repaint();
			if (con.containsPathway()) {
				GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
			}
		}
	}

	private void onChooseEdgeFontClick() {
		FontDialog dialog = new FontDialog((Frame) null, "Select font for edge labels", true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setSelectedFont(settings.getVertexFont());
		if (settings.getEdgeFont() == null && con.containsPathway()) {
			dialog.setSelectedFont(GraphInstance.getPathway().getGraph().getVisualizationViewer().getFont());
		}
		dialog.setLocationRelativeTo(panelLeft);
		dialog.setAlwaysOnTop(true);
		dialog.requestFocus();
		dialog.setVisible(true);
		if (!dialog.isCancelSelected()) {
			// System.out.printf("Selected font is: %s%n", dialog.getSelectedFont());
			settings.setEdgeFont(dialog.getSelectedFont());
			evaluateEdgeFontLabel();
			panelLeft.repaint();
			if (con.containsPathway()) {
				GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
			}
		}
	}

	private void onDeactiveAntiAliasingClicked() {
		if (con.containsPathway()) {
			GraphInstance.getPathway().getGraph().disableAntliasing(disabledAntiAliasing.isSelected());
			GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
		}
	}

	private void onUseDefaultTransformatorsClicked() {
		if (con.containsPathway()) {
			if (useDefaultTransformators.isSelected()) {
				GraphInstance.getPathway().getGraph().dropTransformatorsOfVV(true);
			} else {
				GraphInstance.getPathway().getGraph().addTransformatorsToVV(true);
			}
		}
	}

	private void onUseDefaultTransformatorsSatelliteClicked() {
		if (con.containsPathway()) {
			if (useDefaultTransformatorsSatellite.isSelected()) {
				GraphInstance.getPathway().getGraph().dropTransformatorsOfSatellite(true);
			} else {
				GraphInstance.getPathway().getGraph().addTransformatorsToSatellite(true);
			}
		}
	}

	private void evaluateVertexFontLabel() {
		if (settings.getVertexFont() != null) {
			Font f = settings.getVertexFont();
			vertexFontLabel.setText("Font: " + f.getFontName() + ", " + f.getSize());
		} else {
			vertexFontLabel.setText("Font: default");
		}
	}

	private void evaluateEdgeFontLabel() {
		if (settings.getEdgeFont() != null) {
			Font f = settings.getEdgeFont();
			edgeFontLabel.setText("Font: " + f.getFontName() + ", " + f.getSize());
		} else {
			edgeFontLabel.setText("Font: default");
		}
	}
}
