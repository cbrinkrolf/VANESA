package configurations.gui;

import java.awt.Font;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import biologicalElements.Pathway;
import configurations.Settings;
import configurations.Workspace;
import gui.JFontChooserButton;

import configurations.GraphSettings;
import graph.GraphInstance;
import gui.JIntTextField;
import net.miginfocom.swing.MigLayout;

public class GraphSettingsDialog extends BaseSettingsPanel {

	private static final long serialVersionUID = -8611326577796735342L;

	private final GraphSettings graphSettings = GraphSettings.getInstance();

	private final ButtonGroup nodeLabelGroup = new ButtonGroup();
	private final JRadioButton onlyName = new JRadioButton("Name", true);
	private final JRadioButton onlyLabel = new JRadioButton("Label", true);
	private final JRadioButton nothing = new JRadioButton("Nothing", true);
	private final JRadioButton nameAndLabel = new JRadioButton("Both", true);

	private final ButtonGroup edgeLabelGroup = new ButtonGroup();
	private final JRadioButton onlyNameRadioButton = new JRadioButton("Name", true);
	private final JRadioButton onlyLabelRadioButton = new JRadioButton("Label", true);
	private final JRadioButton nothingRadioButton = new JRadioButton("Nothing", true);
	private final JRadioButton nameAndLabelRadioButton = new JRadioButton("Both", true);

	private final ButtonGroup graphBackgroundGroup = new ButtonGroup();
	private final JRadioButton black = new JRadioButton("Black", true);
	private final JRadioButton white = new JRadioButton("White", true);

	private final JCheckBox showEdgesCheckBox = new JCheckBox();

	private final JSlider opacitySlider = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 255);

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
		nodeLabelGroup.add(onlyLabel);
		nodeLabelGroup.add(onlyName);
		nodeLabelGroup.add(nameAndLabel);
		nodeLabelGroup.add(nothing);
		nodeLabelPanel.add(onlyLabel);
		onlyLabel.setBackground(null);
		nodeLabelPanel.add(onlyName);
		onlyName.setBackground(null);
		nodeLabelPanel.add(nameAndLabel);
		nameAndLabel.setBackground(null);
		nodeLabelPanel.add(nothing);
		nothing.setBackground(null);
		addSetting("Displayed node label", "Which node label should be displayed?", nodeLabelPanel,
				() -> onlyLabel.setSelected(true));

		final JPanel edgeLabelPanel = new JPanel(new MigLayout("ins 0, left, fill"));
		edgeLabelPanel.setBackground(null);
		edgeLabelGroup.add(onlyLabelRadioButton);
		edgeLabelGroup.add(onlyNameRadioButton);
		edgeLabelGroup.add(nameAndLabelRadioButton);
		edgeLabelGroup.add(nothingRadioButton);
		edgeLabelPanel.add(onlyLabelRadioButton);
		onlyLabelRadioButton.setBackground(null);
		edgeLabelPanel.add(onlyNameRadioButton);
		onlyNameRadioButton.setBackground(null);
		edgeLabelPanel.add(nameAndLabelRadioButton);
		nameAndLabelRadioButton.setBackground(null);
		edgeLabelPanel.add(nothingRadioButton);
		nothingRadioButton.setBackground(null);
		addSetting("Displayed edge label", "Which edge label should be displayed?", edgeLabelPanel,
				() -> onlyLabelRadioButton.setSelected(true));

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
		overrideVertexFontCheckBox
				.addChangeListener(e -> vertexFontChooser.setEnabled(overrideVertexFontCheckBox.isSelected()));
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
		overrideEdgeFontCheckBox
				.addChangeListener(e -> edgeFontChooser.setEnabled(overrideEdgeFontCheckBox.isSelected()));
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
			onlyLabel.setSelected(true);
		} else if (graphSettings.getNodeLabel() == GraphSettings.SHOW_NAME) {
			onlyName.setSelected(true);
		} else if (graphSettings.getNodeLabel() == GraphSettings.SHOW_LABEL_AND_NAME) {
			nameAndLabel.setSelected(true);
		} else if (graphSettings.getNodeLabel() == GraphSettings.SHOW_NONE) {
			nothing.setSelected(true);
		}
		if (graphSettings.getEdgeLabel() == GraphSettings.SHOW_LABEL) {
			onlyLabelRadioButton.setSelected(true);
		} else if (graphSettings.getEdgeLabel() == GraphSettings.SHOW_NAME) {
			onlyNameRadioButton.setSelected(true);
		} else if (graphSettings.getEdgeLabel() == GraphSettings.SHOW_LABEL_AND_NAME) {
			nameAndLabelRadioButton.setSelected(true);
		} else if (graphSettings.getEdgeLabel() == GraphSettings.SHOW_NONE) {
			nothingRadioButton.setSelected(true);
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
			if (onlyLabel.isSelected()) {
				graphSettings.setNodeLabel(GraphSettings.SHOW_LABEL);
			} else if (onlyName.isSelected()) {
				graphSettings.setNodeLabel(GraphSettings.SHOW_NAME);
			} else if (nameAndLabel.isSelected()) {
				graphSettings.setNodeLabel(GraphSettings.SHOW_LABEL_AND_NAME);
			} else if (nothing.isSelected()) {
				graphSettings.setNodeLabel(GraphSettings.SHOW_NONE);
			}
			if (onlyLabelRadioButton.isSelected()) {
				graphSettings.setEdgeLabel(GraphSettings.SHOW_LABEL);
			} else if (onlyNameRadioButton.isSelected()) {
				graphSettings.setEdgeLabel(GraphSettings.SHOW_NAME);
			} else if (nameAndLabelRadioButton.isSelected()) {
				graphSettings.setEdgeLabel(GraphSettings.SHOW_LABEL_AND_NAME);
			} else if (nothingRadioButton.isSelected()) {
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
