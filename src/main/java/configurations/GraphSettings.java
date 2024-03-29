package configurations;

public class GraphSettings {
    private static GraphSettings instance;

    private Integer nodeLabel;
    private Integer edgeLabel;
    private boolean backgroundColor = false;
    private boolean drawEdges = true;
    private int edgeOpacity = 255;
    private int pixelOffset = 3;
    
    public static final int SHOW_LABEL = 0;
    public static final int SHOW_NAME = 1;
    public static final int SHOW_LABEL_AND_NAME = 2;
    public static final int SHOW_NONE = 3;
    

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
        //GraphInstance.getMyGraph().updateAllNodeLabels();
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
        this.edgeOpacity = newopacity;
    }

    public void setEdgeLabel(Integer edgeLabel) {
        this.edgeLabel = edgeLabel;
        //GraphInstance.getMyGraph().updateAllEdgeLabels();
    }

    public int getPixelOffset() {
		return pixelOffset;
	}

	public void setPixelOffset(int pixelOffset) {
		this.pixelOffset = pixelOffset;
	}

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
}
