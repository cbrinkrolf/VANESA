package gui.annotation;

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
        text = properties.get("title");
        titlePos = Integer.parseInt(properties.get("titlePos"));
        fillColor = new Color(Integer.parseInt(properties.get("fillColor")));
        outlineColor = new Color(Integer.parseInt(properties.get("outlineColor")));
        textColor = new Color(Integer.parseInt(properties.get("textColor")));
        alpha = Integer.parseInt(properties.get("fillColor"));
        outlineType = Integer.parseInt(properties.get("outlineType"));
        boolean ellipse = Boolean.parseBoolean(properties.get("isEllipse"));
        double minX = Double.parseDouble(properties.get("minX"));
        double minY = Double.parseDouble(properties.get("minY"));
        double maxX = Double.parseDouble(properties.get("maxX"));
        double maxY = Double.parseDouble(properties.get("maxY"));
        shape = ellipse ? new Ellipse2D.Double() : new Rectangle2D.Double();
        shape.setFrameFromDiagonal(minX, minY, maxX, maxY);
    }

    public Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("title", text);
        properties.put("titlePos", titlePos + "");
        properties.put("fillColor", fillColor.getRGB() + "");
        properties.put("outlineColor", outlineColor.getRGB() + "");
        properties.put("textColor", textColor.getRGB() + "");
        properties.put("alpha", alpha+ "");
        properties.put("outlineType", outlineType + "");
        boolean ellipse = shape instanceof Ellipse2D;
        properties.put("isEllipse", ellipse + "");
       
        double minX = shape.getMinX();
        double minY = shape.getMinY();
        double maxX = shape.getMaxX();
        double maxY = shape.getMaxY();
        properties.put("minX", String.valueOf(minX));
        properties.put("minY", String.valueOf(minY));
        properties.put("maxX", String.valueOf(maxX));
        properties.put("maxY", String.valueOf(maxY));
        return properties;
    }
}
