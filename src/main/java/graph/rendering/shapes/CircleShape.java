package graph.rendering.shapes;

import graph.GraphNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CircleShape extends NodeShape {
	protected static final int RADIUS = 10;

	@Override
	public void paint(final Graphics2D g, final GraphNode node, final Color strokeColor, final Color fillColor) {
		double radius = RADIUS * node.getSize();
		int diameter = (int) (radius * 2);
		g.setColor(fillColor);
		g.fillOval(-(int) radius, -(int) radius, diameter, diameter);
		g.setColor(strokeColor);
		g.setStroke(new BasicStroke(3));
		g.drawOval(-(int) radius, -(int) radius, diameter, diameter);
	}

	@Override
	public Rectangle2D getBounds(final GraphNode node) {
		double radius = RADIUS * node.getSize();
		return new Rectangle2D.Double(-radius, -radius, radius * 2, radius * 2);
	}

	@Override
	public float getBoundsDistance(final GraphNode node, final Point2D directionVector) {
		return (float) (RADIUS * node.getSize());
	}
}
