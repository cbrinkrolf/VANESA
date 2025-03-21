package graph.rendering.nodes;

import graph.GraphNode;
import graph.Rect;

import java.awt.*;
import java.awt.geom.Point2D;

public class CircleShape extends NodeShape {
	protected static final int RADIUS = 10;

	@Override
	public void paint(final Graphics2D g, final GraphNode node, final Color strokeColor, final Color fillColor) {
		final int radius = (int) (RADIUS * node.getSize());
		final int diameter = radius * 2;
		g.setColor(fillColor);
		g.fillOval(-radius, -radius, diameter, diameter);
		g.setColor(strokeColor);
		g.setStroke(DEFAULT_STROKE);
		g.drawOval(-radius, -radius, diameter, diameter);
	}

	@Override
	public Rect getBounds(final GraphNode node) {
		double radius = RADIUS * node.getSize();
		return new Rect(-radius, -radius, radius * 2, radius * 2);
	}

	@Override
	public float getBoundsDistance(final GraphNode node, final Point2D directionVector) {
		return (float) (RADIUS * node.getSize());
	}
}
