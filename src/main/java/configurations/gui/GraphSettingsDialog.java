package configurations.gui;

import java.awt.Font;
import java.awt.Frame;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import org.drjekyll.fontchooser.FontDialog;

import configurations.GraphSettings;
import configurations.SettingsManager;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;
import util.MyJFormattedTextField;
import util.MyNumberFormat;

public class GraphSettingsDialog extends BaseSettingsPanel {
	private final GraphSettings settings = GraphSettings.getInstance();

	private final ButtonGroup nodeLabelGroup = new ButtonGroup();
	private final JRadioButton only_name = new JRadioButton("Name", true);
	private final JRadioButton only_label = new JRadioButton("Label", true);
	private final JRadioButton nothing = new JRadioButton("Nothing", true);
	private final JRadioButton name_label = new JRadioButton("Both", true);

	private final ButtonGroup edgeLabelGroup = new ButtonGroup();
	private final JRadioButton only_name_e = new JRadioButton("Name", true);
	private final JRadioButton only_label_e = new JRadioButton("Label", true);
	private final JRadioButton nothing_e = new JRadioButton("Nothing", true);
	private final JRadioButton name_label_e = new JRadioButton("Both", true);

	private final ButtonGroup graphBackgroundGroup = new ButtonGroup();
	private final JRadioButton black = new JRadioButton("Black", true);
	private final JRadioButton white = new JRadioButton("White", true);

	private JCheckBox showEdgesCheckBox = new JCheckBox();

	private JSlider opacityslider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
	private int edgeOpacityOld = settings.getEdgeOpacity();

	private MyJFormattedTextField pixelOffset = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());

	private JLabel vertexFontLabel;
	private JLabel edgeFontLabel;

	private JButton vertexFontChooser;
	private JButton edgeFontChooser;

	private JCheckBox overrideVertexFontCheckBox;
	private JCheckBox overrideEdgeFontCheckBox;

	private Font vertexFontOld = settings.getVertexFont();
	private Font edgeFontOld = settings.getEdgeFont();

	private JCheckBox omitInvisibleNodes;
	private JCheckBox disabledAntiAliasing;

	private JCheckBox useDefaultTransformators;
	private JCheckBox useDefaultTransformatorsSatellite;

	private JSpinner minVertexLabelFontSize;
	private JSpinner minEdgeLabelFontSize;

	private int minVertexLabelFontSizeOld = settings.getMinVertexFontSize();
	private int minEdgeLabelFontSizeOld = settings.getMinEdgeFontSize();

	private final MainWindow w = MainWindow.getInstance();
	private final GraphContainer con = GraphContainer.getInstance();

	public GraphSettingsDialog() {
		super();
		if (settings.getNodeLabel() == GraphSettings.SHOW_LABEL) {
			only_label.setSelected(true);
		} else if (settings.getNodeLabel() == GraphSettings.SHOW_NAME) {
			only_name.setSelected(true);
		} else if (settings.getNodeLabel() == GraphSettings.SHOW_LABEL_AND_NAME) {
			name_label.setSelected(true);
		} else if (settings.getNodeLabel() == GraphSettings.SHOW_NONE) {
			nothing.setSelected(true);
		}

		if (settings.getEdgeLabel() == GraphSettings.SHOW_LABEL) {
			only_label_e.setSelected(true);
		} else if (settings.getEdgeLabel() == GraphSettings.SHOW_NAME) {
			only_name_e.setSelected(true);
		} else if (settings.getEdgeLabel() == GraphSettings.SHOW_LABEL_AND_NAME) {
			name_label_e.setSelected(true);
		} else if (settings.getEdgeLabel() == GraphSettings.SHOW_NONE) {
			nothing_e.setSelected(true);
		}

		if (settings.isBackgroundColor()) {
			black.setSelected(true);
		} else {
			white.setSelected(true);
		}

		showEdgesCheckBox.setSelected(settings.getDrawEdges());
		opacityslider.setValue(settings.getEdgeOpacity());

		addHeader("Visual Style");
		final JPanel nodeLabelPanel = new JPanel(new MigLayout("ins 0, left, fill"));
		nodeLabelPanel.setBackground(null);
		nodeLabelGroup.add(only_label);
		nodeLabelGroup.add(only_name);
		nodeLabelGroup.add(name_label);
		nodeLabelGroup.add(nothing);
		nodeLabelPanel.add(only_label);
		only_label.setBackground(null);
		nodeLabelPanel.add(only_name);
		only_name.setBackground(null);
		nodeLabelPanel.add(name_label);
		name_label.setBackground(null);
		nodeLabelPanel.add(nothing);
		nothing.setBackground(null);
		addSetting("Displayed node label", "Which node label should be displayed?", nodeLabelPanel);

		final JPanel edgeLabelPanel = new JPanel(new MigLayout("ins 0, left, fill"));
		edgeLabelPanel.setBackground(null);
		edgeLabelGroup.add(only_label_e);
		edgeLabelGroup.add(only_name_e);
		edgeLabelGroup.add(name_label_e);
		edgeLabelGroup.add(nothing_e);
		edgeLabelPanel.add(only_label_e);
		only_label_e.setBackground(null);
		edgeLabelPanel.add(only_name_e);
		only_name_e.setBackground(null);
		edgeLabelPanel.add(name_label_e);
		name_label_e.setBackground(null);
		edgeLabelPanel.add(nothing_e);
		nothing_e.setBackground(null);
		addSetting("Displayed edge label", "Which edge label should be displayed?", edgeLabelPanel);

		final JPanel graphBackgroundPanel = new JPanel(new MigLayout("ins 0, left, fill"));
		graphBackgroundPanel.setBackground(null);
		graphBackgroundGroup.add(white);
		graphBackgroundGroup.add(black);
		graphBackgroundPanel.add(white);
		white.setBackground(null);
		graphBackgroundPanel.add(black);
		black.setBackground(null);
		addSetting("Graph Background", "What kind of background do you prefer?", graphBackgroundPanel);

		showEdgesCheckBox.setBackground(null);
		addSetting("Show Edges", "Whether edges should be rendered or not", showEdgesCheckBox);

		opacityslider.setMajorTickSpacing(50);
		opacityslider.setPaintTicks(true);
		opacityslider.setPaintLabels(true);
		opacityslider.addChangeListener(e -> {
			settings.setEdgeOpacity(opacityslider.getValue());
			if (con.containsPathway()) {
				GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
			}
		});
		addSetting("Edge Opacity",
				"Opacity of edges, value between 0 and 255. A value of 0 draws the edges fully transparent.",
				opacityslider);

		pixelOffset.setText(String.valueOf(settings.getPixelOffset()));
		pixelOffset.setValue(settings.getPixelOffset());
		addSetting("Pixel offset for edge picking", "How close the mouse needs to click in order to select edges",
				pixelOffset);

		final JPanel vertexFontPanel = new JPanel(new MigLayout("ins 0, left, fill", "[][][]"));
		vertexFontPanel.setBackground(null);
		overrideVertexFontCheckBox = new JCheckBox();
		overrideVertexFontCheckBox.setBackground(null);
		overrideVertexFontCheckBox.addChangeListener(e -> {
			vertexFontChooser.setEnabled(overrideVertexFontCheckBox.isSelected());
			if (!overrideVertexFontCheckBox.isSelected()) {
				settings.setVertexFont(null);
			}
			if (con.containsPathway()) {
				GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
			}
		});
		overrideVertexFontCheckBox.setSelected(settings.getVertexFont() != null);
		vertexFontPanel.add(overrideVertexFontCheckBox);
		vertexFontChooser = new JButton("Choose");
		vertexFontChooser.setEnabled(overrideVertexFontCheckBox.isSelected());
		vertexFontChooser.addActionListener(e -> onChooseVertexFontClick());
		vertexFontPanel.add(vertexFontChooser);
		vertexFontLabel = new JLabel("default font");
		evaluateVertexFontLabel();
		vertexFontPanel.add(vertexFontLabel);
		addSetting("Node label font", "Which font should be used for node label rendering", vertexFontPanel);

		final JPanel edgeFontPanel = new JPanel(new MigLayout("ins 0, left, fill", "[][][]"));
		edgeFontPanel.setBackground(null);
		overrideEdgeFontCheckBox = new JCheckBox();
		overrideEdgeFontCheckBox.setBackground(null);
		overrideEdgeFontCheckBox.addChangeListener(e -> {
			edgeFontChooser.setEnabled(overrideEdgeFontCheckBox.isSelected());
			if (!overrideEdgeFontCheckBox.isSelected()) {
				settings.setEdgeFont(null);
			}
			if (con.containsPathway()) {
				GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
			}
		});
		overrideEdgeFontCheckBox.setSelected(settings.getEdgeFont() != null);
		edgeFontPanel.add(overrideEdgeFontCheckBox);
		edgeFontChooser = new JButton("Choose");
		edgeFontChooser.setEnabled(overrideEdgeFontCheckBox.isSelected());
		edgeFontChooser.addActionListener(e -> onChooseEdgeFontClick());
		edgeFontPanel.add(edgeFontChooser);
		edgeFontLabel = new JLabel("default font");
		evaluateEdgeFontLabel();
		edgeFontPanel.add(edgeFontLabel);
		addSetting("Edge label font", "Which font should be used for edge label rendering", edgeFontPanel);

		// Performance settings
		addHeader("Performance");
		omitInvisibleNodes = new JCheckBox();
		omitInvisibleNodes.setBackground(null);
		omitInvisibleNodes.setSelected(SettingsManager.getInstance().isOmitPaintInvisibleNodes());
		addSetting("Omit invisible nodes", "Omit drawing of invisible nodes. Pathway needs to be re-opened to be effective!", omitInvisibleNodes);

		disabledAntiAliasing = new JCheckBox();
		disabledAntiAliasing.setBackground(null);
		disabledAntiAliasing.setSelected(SettingsManager.getInstance().isDisabledAntiAliasing());
		disabledAntiAliasing.addChangeListener(e -> onDeactiveAntiAliasingClicked());
		addSetting("disable anti-aliasing", "Increases graph drawing performance. Exported graph images are not affected!", disabledAntiAliasing);

		useDefaultTransformators = new JCheckBox();
		useDefaultTransformators.setBackground(null);
		useDefaultTransformators.setSelected(settings.isDefaultTransformators());
		useDefaultTransformators.addChangeListener(e -> onUseDefaultTransformatorsClicked());
		addSetting("Use default style for graph", "Use default transformers to visualize the graph (faster)", useDefaultTransformators);

		useDefaultTransformatorsSatellite = new JCheckBox();
		useDefaultTransformatorsSatellite.setBackground(null);
		useDefaultTransformatorsSatellite.setSelected(settings.isDefaultTransformatorsSatellite());
		useDefaultTransformatorsSatellite.addChangeListener(e -> onUseDefaultTransformatorsSatelliteClicked());
		addSetting("Use default style for satellite graph", "Use default transformers to visualize the satellite view of the graph (faster)", useDefaultTransformatorsSatellite);

		minVertexLabelFontSize = new JSpinner(new SpinnerNumberModel(settings.getMinVertexFontSize(), 0, 100, 1));
		minVertexLabelFontSize.addChangeListener(e -> vertexFontSpinnerChangedEvent());
		addSetting("Minimal font size of node labels", "Minimal font size of node labels that define if node labels are drawn or not", minVertexLabelFontSize);

		minEdgeLabelFontSize = new JSpinner(new SpinnerNumberModel(settings.getMinVertexFontSize(), 0, 100, 1));
		minEdgeLabelFontSize.addChangeListener(e -> edgeFontSpinnerChangedEvent());
		addSetting("Minimal font size of edge labels", "Minimal font size of edge labels that define if edge labels are drawn or not", minEdgeLabelFontSize);
	}

	@Override
	public boolean applyDefaults() {
		only_label.setSelected(true);
		only_label_e.setSelected(true);
		white.setSelected(true);
		if (con.containsPathway()) {
			GraphInstance.getPathway().changeBackground("white");
		}
		settings.setBackgroundColor(false);
		showEdgesCheckBox.setSelected(true);
		opacityslider.setValue(255);
		settings.setPixelOffset(3);
		pixelOffset.setText(String.valueOf(settings.getPixelOffset()));
		pixelOffset.setValue(String.valueOf(settings.getPixelOffset()));
		overrideVertexFontCheckBox.setSelected(true);
		overrideEdgeFontCheckBox.setSelected(true);
		omitInvisibleNodes.setSelected(true);
		disabledAntiAliasing.setSelected(false);
		useDefaultTransformators.setSelected(false);
		useDefaultTransformatorsSatellite.setSelected(false);
		minVertexLabelFontSize.getModel().setValue(6);
		minEdgeLabelFontSize.getModel().setValue(6);
		if (con.containsPathway()) {
			GraphInstance.getPathway().getGraph().updateLabelVisibilityOnZoom();
			GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
		}
		return true;
	}

	@Override
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
		settings.setDrawEdges(showEdgesCheckBox.isSelected());
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
		settings.setMinVertexFontSize(minVertexLabelFontSizeOld);
		settings.setMinEdgeFontSize(minEdgeLabelFontSizeOld);
		if (con.containsPathway()) {
			GraphInstance.getPathway().getGraph().updateLabelVisibilityOnZoom();
			GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
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
		dialog.setLocationRelativeTo(this);
		dialog.setAlwaysOnTop(true);
		dialog.requestFocus();
		dialog.setVisible(true);
		if (!dialog.isCancelSelected()) {
			settings.setVertexFont(dialog.getSelectedFont());
			evaluateVertexFontLabel();
			contentPanel.repaint();
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
		dialog.setLocationRelativeTo(this);
		dialog.setAlwaysOnTop(true);
		dialog.requestFocus();
		dialog.setVisible(true);
		if (!dialog.isCancelSelected()) {
			settings.setEdgeFont(dialog.getSelectedFont());
			evaluateEdgeFontLabel();
			contentPanel.repaint();
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
			vertexFontLabel.setText(f.getFontName() + ", " + f.getSize());
		} else {
			vertexFontLabel.setText("default");
		}
	}

	private void evaluateEdgeFontLabel() {
		if (settings.getEdgeFont() != null) {
			Font f = settings.getEdgeFont();
			edgeFontLabel.setText(f.getFontName() + ", " + f.getSize());
		} else {
			edgeFontLabel.setText("default");
		}
	}

	private void vertexFontSpinnerChangedEvent() {
		if (minVertexLabelFontSize.getModel() instanceof SpinnerNumberModel) {
			int size = ((SpinnerNumberModel) minVertexLabelFontSize.getModel()).getNumber().intValue();
			settings.setMinVertexFontSize(size);
			if (con.containsPathway()) {
				GraphInstance.getPathway().getGraph().updateLabelVisibilityOnZoom();
				GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
			}
		}
	}

	private void edgeFontSpinnerChangedEvent() {
		if (minEdgeLabelFontSize.getModel() instanceof SpinnerNumberModel) {
			int size = ((SpinnerNumberModel) minEdgeLabelFontSize.getModel()).getNumber().intValue();
			settings.setMinEdgeFontSize(size);
			if (con.containsPathway()) {
				GraphInstance.getPathway().getGraph().updateLabelVisibilityOnZoom();
				GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
			}
		}
	}
}
