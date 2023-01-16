package gui;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.HashMap;
import java.util.Map;

public class RangeInfo {

    public String text;
    public Color fillColor;
    public Color outlineColor;
    public Color textColor;
    public RectangularShape shape;
    public int alpha=255;
    /**
     * 0|1
     * 2|3
     */
    public int titlePos;
    /**
     * 0= none;1= single line; 2=double line
     */
    public int outlineType;

    public RangeInfo(RectangularShape shape, String text, Color fillColor, Color outlineColor, Color textColor) {
        super();
        this.shape = shape;
        this.text = text;
        this.fillColor = fillColor;
        this.outlineColor = outlineColor;
        this.textColor = textColor;
    }

    public RangeInfo(Map<String, String> properties, RangeSelector outer) {
        super();
        
        this.text = properties.get("title");
        this.titlePos = Integer.parseInt(properties.get("titlePos"));
        this.fillColor = new Color(Integer.parseInt(properties.get("fillColor")));
        this.outlineColor = new Color(Integer.parseInt(properties.get("outlineColor")));
        this.textColor = new Color(Integer.parseInt(properties.get("textColor")));
        this.alpha = Integer.parseInt(properties.get("fillColor"));
        this.outlineType = Integer.parseInt(properties.get("outlineType"));
        boolean ellipse = Boolean.parseBoolean(properties.get("isEllipse"));
        double minX = Double.parseDouble(properties.get("minX"));
        double minY = Double.parseDouble(properties.get("minY"));
        double maxX = Double.parseDouble(properties.get("maxX"));
        double maxY = Double.parseDouble(properties.get("maxY"));
        this.shape = ellipse ? new Ellipse2D.Double() : new Rectangle2D.Double();
        shape.setFrameFromDiagonal(minX, minY, maxX, maxY);
    }

    public Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<String, String>();
        //this.text+="blaaaaa";
        //System.out.println("drin");
        properties.put("title", this.text);
        properties.put("titlePos", this.titlePos + "");
        properties.put("fillColor", fillColor.getRGB() + "");
        properties.put("outlineColor", outlineColor.getRGB() + "");
        properties.put("textColor", textColor.getRGB() + "");
        properties.put("alpha", this.alpha+ "");
        properties.put("outlineType", outlineType + "");
        boolean ellipse = this.shape instanceof Ellipse2D;
        properties.put("isEllipse", ellipse + "");
       
        double minX = this.shape.getMinX();
        //System.out.println("minx"+shape.getMinX());
        double minY = shape.getMinY();
        double maxX = shape.getMaxX();
        double maxY = shape.getMaxY();
        //System.out.println("old: "+new Point2D.Double(minX,minY));
        //System.out.println("new :"+i.getPathway().getGraph().getVisualizationViewer().getRenderContext().getMultiLayerTransformer().transform(new Point2D.Double(minX,minY)));
      
       
        properties.put("minX", minX + "");
        properties.put("minY", minY + "");
        properties.put("maxX", maxX + "");
        properties.put("maxY", maxY + "");
        return properties;
    }
}
