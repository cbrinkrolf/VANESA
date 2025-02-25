package configurations;

import java.awt.Font;

public class GraphSettings {
	private static GraphSettings instance;

	private Integer nodeLabel;
	private Integer edgeLabel;
	private boolean backgroundColor = false;
	private boolean drawEdges = true;
	private int edgeOpacity = 255;
	private int pixelOffset = 3;
	private Font vertexFont = null;
	private Font edgeFont = null;
	private boolean disabledAntiAliasing;
	private boolean defaultTransformators = false;
	private boolean defaultTransformatorsSatellite = false;
	private int minVertexFontSize = 6;
	private int minEdgeFontSize = 6;

	public static final int SHOW_LABEL = 0;
	public static final int SHOW_NAME = 1;
	public static final int SHOW_LABEL_AND_NAME = 2;
	public static final int SHOW_NONE = 3;

	public GraphSettings() {
		nodeLabel = GraphSettings.SHOW_LABEL;
		edgeLabel = GraphSettings.SHOW_LABEL;
	}

	public static GraphSettings getInstance() {
		if (instance == null) {
			instance = new GraphSettings();
		}
		return instance;
	}

	public boolean isBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(boolean backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Integer getNodeLabel() {
		return nodeLabel;
	}

	public void setNodeLabel(Integer nodeLabel) {
		this.nodeLabel = nodeLabel;
		// GraphInstance.getMyGraph().updateAllNodeLabels();
	}

	public Integer getEdgeLabel() {
		return edgeLabel;
	}

	public void setDrawEdges(boolean draw) {
		this.drawEdges = draw;
	}

	public boolean getDrawEdges() {
		return drawEdges;
	}

	public int getEdgeOpacity() {
		return edgeOpacity;
	}

	public void setEdgeOpacity(int newopacity) {
		if (newopacity >= 0 && newopacity <= 255) {
			this.edgeOpacity = newopacity;
		}
	}

	public void setEdgeLabel(Integer edgeLabel) {
		this.edgeLabel = edgeLabel;
		// GraphInstance.getMyGraph().updateAllEdgeLabels();
	}

	public int getPixelOffset() {
		return pixelOffset;
	}

	public void setPixelOffset(int pixelOffset) {
		this.pixelOffset = pixelOffset;
	}

	public Font getVertexFont() {
		return vertexFont;
	}

	public void setVertexFont(Font vertexFont) {
		this.vertexFont = vertexFont;
	}

	public Font getEdgeFont() {
		return edgeFont;
	}

	public void setEdgeFont(Font edgeFont) {
		this.edgeFont = edgeFont;
	}

	public boolean isDisabledAntiAliasing() {
		return disabledAntiAliasing;
	}

	public void setDisabledAntiAliasing(boolean disabledAntiAliasing) {
		this.disabledAntiAliasing = disabledAntiAliasing;
	}

	public boolean isDefaultTransformators() {
		return defaultTransformators;
	}

	public void setDefaultTransformators(boolean defaultTransformators) {
		this.defaultTransformators = defaultTransformators;
	}

	public boolean isDefaultTransformatorsSatellite() {
		return defaultTransformatorsSatellite;
	}

	public void setDefaultTransformatorsSatellite(boolean defaultTransformatorsSatellite) {
		this.defaultTransformatorsSatellite = defaultTransformatorsSatellite;
	}

	public int getMinVertexFontSize() {
		return minVertexFontSize;
	}

	public void setMinVertexFontSize(int minVertexFontSize) {
		this.minVertexFontSize = minVertexFontSize;
	}

	public int getMinEdgeFontSize() {
		return minEdgeFontSize;
	}

	public void setMinEdgeFontSize(int minEdgeFontSize) {
		this.minEdgeFontSize = minEdgeFontSize;
	}
}
