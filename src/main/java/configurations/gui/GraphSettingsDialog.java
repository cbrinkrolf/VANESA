package configurations.gui;

import java.awt.Font;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;

import biologicalElements.Pathway;
import configurations.Settings;
import configurations.Workspace;
import gui.JFontChooserButton;

import configurations.GraphSettings;
import graph.GraphInstance;
import gui.JIntTextField;
import net.miginfocom.swing.MigLayout;

public class GraphSettingsDialog extends BaseSettingsPanel {
	private final GraphSettings graphSettings = GraphSettings.getInstance();

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

	private final JCheckBox showEdgesCheckBox = new JCheckBox();

	private final JSlider opacitySlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);

	private final JIntTextField pixelOffset = new JIntTextField();

	private JCheckBox overrideVertexFontCheckBox;
	private JFontChooserButton vertexFontChooser;
	private JLabel vertexFontLabel;
	private JCheckBox overrideEdgeFontCheckBox;
	private JFontChooserButton edgeFontChooser;
	private JLabel edgeFontLabel;

	private JCheckBox omitInvisibleNodes;
	private JCheckBox disabledAntiAliasing;

	private JCheckBox useDefaultTransformers;
	private JCheckBox useDefaultTransformersSatellite;

	private JIntTextField minVertexLabelFontSize;
	private JIntTextField minEdgeLabelFontSize;

	public GraphSettingsDialog() {
		super();
		addVisualSettings();
		addPerformanceSettings();
		updateSettings(Workspace.getCurrentSettings());
	}

	private void addVisualSettings() {
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
		addSetting("Displayed node label", "Which node label should be displayed?", nodeLabelPanel,
				() -> only_label.setSelected(true));

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
		addSetting("Displayed edge label", "Which edge label should be displayed?", edgeLabelPanel,
				() -> only_label_e.setSelected(true));

		final JPanel graphBackgroundPanel = new JPanel(new MigLayout("ins 0, left, fill"));
		graphBackgroundPanel.setBackground(null);
		graphBackgroundGroup.add(white);
		graphBackgroundGroup.add(black);
		graphBackgroundPanel.add(white);
		white.setBackground(null);
		graphBackgroundPanel.add(black);
		black.setBackground(null);
		addSetting("Graph Background", "What kind of background do you prefer?", graphBackgroundPanel,
				() -> white.setSelected(true));

		showEdgesCheckBox.setBackground(null);
		addSetting("Show Edges", "Whether edges should be rendered or not", showEdgesCheckBox,
				() -> showEdgesCheckBox.setSelected(true));

		opacitySlider.setMajorTickSpacing(50);
		opacitySlider.setPaintTicks(true);
		opacitySlider.setPaintLabels(true);
		opacitySlider.addChangeListener(e -> graphSettings.setEdgeOpacity(opacitySlider.getValue()));
		addSetting("Edge Opacity",
				"Opacity of edges, value between 0 and 255. A value of 0 draws the edges fully transparent.",
				opacitySlider, () -> opacitySlider.setValue(255));

		addSetting("Pixel offset for edge picking", "How close the mouse needs to click in order to select edges",
				pixelOffset, () -> pixelOffset.setValue(3));

		final JPanel vertexFontPanel = new JPanel(new MigLayout("ins 0, left, fill", "[][][grow]"));
		vertexFontPanel.setBackground(null);
		overrideVertexFontCheckBox = new JCheckBox();
		overrideVertexFontCheckBox.setBackground(null);
		overrideVertexFontCheckBox.addChangeListener(
				e -> vertexFontChooser.setEnabled(overrideVertexFontCheckBox.isSelected()));
		vertexFontPanel.add(overrideVertexFontCheckBox);
		vertexFontChooser = new JFontChooserButton("Choose", "Select font for vertex labels");
		vertexFontChooser.setEnabled(overrideVertexFontCheckBox.isSelected());
		vertexFontChooser.addFontSelectedListener(this::evaluateVertexFontLabel);
		vertexFontPanel.add(vertexFontChooser);
		vertexFontLabel = new JLabel("default");
		vertexFontPanel.add(vertexFontLabel);
		addSetting("Node label font", "Which font should be used for node label rendering", vertexFontPanel,
				() -> overrideVertexFontCheckBox.setSelected(false));

		final JPanel edgeFontPanel = new JPanel(new MigLayout("ins 0, left, fill", "[][][grow]"));
		edgeFontPanel.setBackground(null);
		overrideEdgeFontCheckBox = new JCheckBox();
		overrideEdgeFontCheckBox.setBackground(null);
		overrideEdgeFontCheckBox.addChangeListener(
				e -> edgeFontChooser.setEnabled(overrideEdgeFontCheckBox.isSelected()));
		edgeFontPanel.add(overrideEdgeFontCheckBox);
		edgeFontChooser = new JFontChooserButton("Choose", "Select font for edge labels");
		edgeFontChooser.setEnabled(overrideEdgeFontCheckBox.isSelected());
		edgeFontChooser.addFontSelectedListener(this::evaluateEdgeFontLabel);
		edgeFontPanel.add(edgeFontChooser);
		edgeFontLabel = new JLabel("default");
		edgeFontPanel.add(edgeFontLabel);
		addSetting("Edge label font", "Which font should be used for edge label rendering", edgeFontPanel,
				() -> overrideEdgeFontCheckBox.setSelected(false));
	}

	private void addPerformanceSettings() {
		addHeader("Performance");
		omitInvisibleNodes = new JCheckBox();
		omitInvisibleNodes.setBackground(null);
		addSetting("Omit invisible nodes",
				"Omit drawing of invisible nodes. Pathway needs to be re-opened to be effective!", omitInvisibleNodes,
				() -> omitInvisibleNodes.setSelected(true));

		disabledAntiAliasing = new JCheckBox();
		disabledAntiAliasing.setBackground(null);
		addSetting("disable anti-aliasing",
				"Increases graph drawing performance. Exported graph images are not affected!", disabledAntiAliasing,
				() -> disabledAntiAliasing.setSelected(false));

		useDefaultTransformers = new JCheckBox();
		useDefaultTransformers.setBackground(null);
		addSetting("Use default style for graph", "Use default transformers to visualize the graph (faster)",
				useDefaultTransformers, () -> useDefaultTransformers.setSelected(false));

		useDefaultTransformersSatellite = new JCheckBox();
		useDefaultTransformersSatellite.setBackground(null);
		addSetting("Use default style for satellite graph",
				"Use default transformers to visualize the satellite view of the graph (faster)",
				useDefaultTransformersSatellite, () -> useDefaultTransformersSatellite.setSelected(false));

		minVertexLabelFontSize = new JIntTextField();
		addSetting("Minimal font size of node labels",
				"Minimal font size of node labels that define if node labels are drawn or not", minVertexLabelFontSize,
				() -> minVertexLabelFontSize.setValue(6));

		minEdgeLabelFontSize = new JIntTextField();
		addSetting("Minimal font size of edge labels",
				"Minimal font size of edge labels that define if edge labels are drawn or not", minEdgeLabelFontSize,
				() -> minEdgeLabelFontSize.setValue(6));
	}

	@Override
	public void updateSettings(final Settings settings) {
		// Visual settings
		if (graphSettings.getNodeLabel() == GraphSettings.SHOW_LABEL) {
			only_label.setSelected(true);
		} else if (graphSettings.getNodeLabel() == GraphSettings.SHOW_NAME) {
			only_name.setSelected(true);
		} else if (graphSettings.getNodeLabel() == GraphSettings.SHOW_LABEL_AND_NAME) {
			name_label.setSelected(true);
		} else if (graphSettings.getNodeLabel() == GraphSettings.SHOW_NONE) {
			nothing.setSelected(true);
		}
		if (graphSettings.getEdgeLabel() == GraphSettings.SHOW_LABEL) {
			only_label_e.setSelected(true);
		} else if (graphSettings.getEdgeLabel() == GraphSettings.SHOW_NAME) {
			only_name_e.setSelected(true);
		} else if (graphSettings.getEdgeLabel() == GraphSettings.SHOW_LABEL_AND_NAME) {
			name_label_e.setSelected(true);
		} else if (graphSettings.getEdgeLabel() == GraphSettings.SHOW_NONE) {
			nothing_e.setSelected(true);
		}
		if (graphSettings.isBackgroundColor()) {
			black.setSelected(true);
		} else {
			white.setSelected(true);
		}
		updateFontSettings();
		showEdgesCheckBox.setSelected(graphSettings.getDrawEdges());
		opacitySlider.setValue(graphSettings.getEdgeOpacity());
		pixelOffset.setText(String.valueOf(graphSettings.getPixelOffset()));
		useDefaultTransformers.setSelected(graphSettings.isDefaultTransformers());
		useDefaultTransformersSatellite.setSelected(graphSettings.isDefaultTransformersSatellite());
		// Performance settings
		omitInvisibleNodes.setSelected(settings.isOmitPaintInvisibleNodes());
		disabledAntiAliasing.setSelected(settings.isDisabledAntiAliasing());
	}

	private void updateFontSettings() {
		final Font vertexFont = graphSettings.getVertexFont();
		overrideVertexFontCheckBox.setSelected(vertexFont != null);
		if (vertexFont != null) {
			vertexFontChooser.setSelectedFont(vertexFont);
		}
		evaluateVertexFontLabel(vertexFont);
		final Font edgeFont = graphSettings.getEdgeFont();
		overrideEdgeFontCheckBox.setSelected(edgeFont != null);
		if (edgeFont != null) {
			edgeFontChooser.setSelectedFont(edgeFont);
		}
		evaluateEdgeFontLabel(edgeFont);
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			vertexFontChooser.setDefaultFont(pathway.getGraph().getVisualizationViewer().getFont());
			edgeFontChooser.setDefaultFont(pathway.getGraph().getVisualizationViewer().getFont());
		}
		minVertexLabelFontSize.setValue(graphSettings.getMinVertexFontSize());
		minEdgeLabelFontSize.setValue(graphSettings.getMinEdgeFontSize());
	}

	@Override
	public boolean applySettings() {
		Workspace.getCurrentSettings().batchEdit((settings -> {
			if (only_label.isSelected()) {
				graphSettings.setNodeLabel(GraphSettings.SHOW_LABEL);
			} else if (only_name.isSelected()) {
				graphSettings.setNodeLabel(GraphSettings.SHOW_NAME);
			} else if (name_label.isSelected()) {
				graphSettings.setNodeLabel(GraphSettings.SHOW_LABEL_AND_NAME);
			} else if (nothing.isSelected()) {
				graphSettings.setNodeLabel(GraphSettings.SHOW_NONE);
			}
			if (only_label_e.isSelected()) {
				graphSettings.setEdgeLabel(GraphSettings.SHOW_LABEL);
			} else if (only_name_e.isSelected()) {
				graphSettings.setEdgeLabel(GraphSettings.SHOW_NAME);
			} else if (name_label_e.isSelected()) {
				graphSettings.setEdgeLabel(GraphSettings.SHOW_LABEL_AND_NAME);
			} else if (nothing_e.isSelected()) {
				graphSettings.setEdgeLabel(GraphSettings.SHOW_NONE);
			}
			if (black.isSelected()) {
				graphSettings.setBackgroundColor(true);
			} else if (white.isSelected()) {
				graphSettings.setBackgroundColor(false);
			}
			if (overrideVertexFontCheckBox.isSelected()) {
				graphSettings.setVertexFont(vertexFontChooser.getSelectedFont());
			} else {
				graphSettings.setVertexFont(null);
			}
			if (overrideEdgeFontCheckBox.isSelected()) {
				graphSettings.setEdgeFont(edgeFontChooser.getSelectedFont());
			} else {
				graphSettings.setEdgeFont(null);
			}
			graphSettings.setMinVertexFontSize(minVertexLabelFontSize.getValue(6));
			graphSettings.setMinEdgeFontSize(minEdgeLabelFontSize.getValue(6));
			graphSettings.setDrawEdges(showEdgesCheckBox.isSelected());
			graphSettings.setEdgeOpacity(opacitySlider.getValue());
			graphSettings.setPixelOffset(pixelOffset.getValue(3));
			graphSettings.setDefaultTransformers(useDefaultTransformers.isSelected());
			graphSettings.setDefaultTransformersSatellite(useDefaultTransformersSatellite.isSelected());
			settings.setOmitPaintInvisibleNodes(omitInvisibleNodes.isSelected());
			settings.setDisabledAntiAliasing(disabledAntiAliasing.isSelected());
		}));
		// Update current visualization
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			pathway.changeBackground(black.isSelected() ? "black" : "white");
			pathway.getGraph().getEdgeDrawPaintFunction().updateEdgeAlphaValue();
			pathway.getGraph().disableAntliasing(disabledAntiAliasing.isSelected());
			pathway.getGraph().updateLabelVisibilityOnZoom();
			pathway.getGraph().getVisualizationViewer().repaint();
		}
		return true;
	}

	public void onCancelClick() {
		useDefaultTransformers.setSelected(graphSettings.isDefaultTransformers());
		useDefaultTransformersSatellite.setSelected(graphSettings.isDefaultTransformersSatellite());
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			pathway.getGraph().disableAntliasing(graphSettings.isDisabledAntiAliasing());
			pathway.getGraph().updateLabelVisibilityOnZoom();
			pathway.getGraph().getVisualizationViewer().repaint();
		}
	}

	private void evaluateVertexFontLabel(final Font font) {
		vertexFontLabel.setText(font != null ? font.getFontName() + ", " + font.getSize() : "default");
	}

	private void evaluateEdgeFontLabel(final Font font) {
		edgeFontLabel.setText(font != null ? font.getFontName() + ", " + font.getSize() : "default");
	}
}
