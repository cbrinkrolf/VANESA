package graph.rendering.shapes;

import graph.GraphNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class RoundedRectangleShape extends NodeShape {
	private final float width;
	private final float height;
	private final float rounding;

	public RoundedRectangleShape() {
		this(20, 20);
	}

	public RoundedRectangleShape(final float width, final float height) {
		this.width = width;
		this.height = height;
		rounding = Math.min(width, height) * 0.5f;
	}

	@Override
	public void paint(final Graphics2D g, final GraphNode node, final Color strokeColor, final Color fillColor) {
		int rounding = (int) (this.rounding * node.getSize());
		final Rectangle2D bounds = getBounds(node);
		g.setColor(fillColor);
		g.fillRoundRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight(),
				rounding, rounding);
		g.setColor(strokeColor);
		g.setStroke(new BasicStroke(3));
		g.drawRoundRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight(),
				rounding, rounding);
	}

	@Override
	public Rectangle2D getBounds(final GraphNode node) {
		double width = this.width * node.getSize();
		double height = this.height * node.getSize();
		return new Rectangle2D.Double(-width * 0.5, -height * 0.5, width, height);
	}

	@Override
	public float getBoundsDistance(final GraphNode node, final Point2D directionVector) {
		final var bounds = getBounds(node);
		final var halfWidth = bounds.getWidth() * 0.5f;
		final var halfHeight = bounds.getHeight() * 0.5f;
		final var xFactor = halfWidth / Math.abs(directionVector.getX());
		final var yFactor = halfHeight / Math.abs(directionVector.getY());
		return (float) Math.min(xFactor, yFactor);
	}
}
