package gui.annotation;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import biologicalElements.GraphicalElement;
import edu.uci.ics.jung.visualization.annotations.Annotation;

public class MyAnnotation extends GraphicalElement {
	private final RectangularShape shape;
	private Annotation<?> a;
	private String text;
	private String name = "";
	private Color fillColor;
	private Color outlineColor;
	private Color textColor;
	private int alpha = 255;
	/**
	 * 0|1 2|3
	 */
	private int titlePos = 0;

	/**
	 * 0= none;1= single line; 2=double line
	 */
	private int outlineType;

	public MyAnnotation(RectangularShape shape, String text, Color fillColor, Color outlineColor, Color textColor) {
		this.shape = shape;
		this.text = text;
		this.fillColor = fillColor;
		this.outlineColor = outlineColor;
		this.textColor = textColor;

		if (!StringUtils.isEmpty(text)) {
			a = new Annotation<>(text, Annotation.Layer.LOWER, fillColor, false,
					new Point2D.Double(shape.getMinX(), shape.getMinY()));
		} else {
			a = new Annotation<>(shape, Annotation.Layer.LOWER, fillColor, true, new Point2D.Double(0, 0));
		}
	}

	public MyAnnotation(Map<String, String> properties) {

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

		if (!StringUtils.isEmpty(text)) {
			a = new Annotation<>(text, Annotation.Layer.LOWER, fillColor, false,
					new Point2D.Double(shape.getMinX(), shape.getMinY()));
		} else {
			a = new Annotation<>(shape, Annotation.Layer.LOWER, fillColor, true, new Point2D.Double(0, 0));
		}
	}

	public int getAlpha() {
		return alpha;
	}

	public Annotation<?> getAnnotation() {
		return this.a;
	}

	public Map<String, String> getAsPropertyMap() {
		Map<String, String> properties = new HashMap<>();
		properties.put("title", text);
		properties.put("titlePos", titlePos + "");
		properties.put("fillColor", fillColor.getRGB() + "");
		properties.put("outlineColor", outlineColor.getRGB() + "");
		properties.put("textColor", textColor.getRGB() + "");
		properties.put("alpha", alpha + "");
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

	public Color getFillColor() {
		return fillColor;
	}

	public String getName() {
		return name;
	}

	public Color getOutlineColor() {
		return outlineColor;
	}

	public int getOutlineType() {
		return outlineType;
	}

	public RectangularShape getShape() {
		return this.shape;
	}

	public String getText() {
		return this.text;
	}

	public Color getTextColor() {
		return textColor;
	}

	public int getTitlePos() {
		return titlePos;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public void setAnnotation(Annotation<?> a) {
		this.a = a;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
	}

	public void setOutlineType(int outlineType) {
		this.outlineType = outlineType;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public void setTitlePos(int titlePos) {
		this.titlePos = titlePos;
	}
}
