package graph.rendering.shapes;

import graph.GraphNode;
import graph.Rect;

import java.awt.*;
import java.awt.geom.Point2D;

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
		final Rect bounds = getBounds(node);
		g.setColor(fillColor);
		g.fillRoundRect((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height, rounding, rounding);
		g.setColor(strokeColor);
		g.setStroke(DEFAULT_STROKE);
		g.drawRoundRect((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height, rounding, rounding);
	}

	@Override
	public Rect getBounds(final GraphNode node) {
		double width = this.width * node.getSize();
		double height = this.height * node.getSize();
		return new Rect(-width * 0.5, -height * 0.5, width, height);
	}

	@Override
	public float getBoundsDistance(final GraphNode node, final Point2D directionVector) {
		final var halfWidth = width * node.getSize() * 0.5f;
		final var halfHeight = height * node.getSize() * 0.5f;
		final var xFactor = halfWidth / Math.abs(directionVector.getX());
		final var yFactor = halfHeight / Math.abs(directionVector.getY());
		return (float) Math.min(xFactor, yFactor);
	}
}
