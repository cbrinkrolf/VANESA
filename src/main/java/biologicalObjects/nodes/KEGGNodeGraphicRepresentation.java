package biologicalObjects.nodes;

public class KEGGNodeGraphicRepresentation {
    private String nodeLabel = "";
    private double xPos = 0;
    private double yPos = 0;
    private String shape = "";
    private String width = "";
    private String height = "";
    private String foregroundColour = "";
    private String backgroundColour = "";

    public String getNodeLabel() {
        return nodeLabel;
    }

    public void setNodeLabel(String nodeLabel) {
        this.nodeLabel = nodeLabel;
    }

    public double getXPos() {
        return xPos;
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
    }

    public double getYPos() {
        return yPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getForegroundColour() {
        return foregroundColour;
    }

    public void setForegroundColour(String foregroundColour) {
        this.foregroundColour = foregroundColour;
    }

    public String getBackgroundColour() {
        return backgroundColour;
    }

    public void setBackgroundColour(String backgroundColour) {
        this.backgroundColour = backgroundColour;
    }
}
