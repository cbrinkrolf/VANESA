package graph.rendering.shapes;

import graph.GraphEdge;

import java.awt.*;
import java.awt.geom.Point2D;

public class CircleTipShape extends EdgeTipShape {
	private static final int RADIUS = 7;
	private static final int DIAMETER = RADIUS * 2;

	@Override
	public void paint(final Graphics2D g, final GraphEdge<?> edge, final Point2D directionVector,
			final double distanceToNodeShape) {
		final var centerX = -directionVector.getX() * (RADIUS - distanceToNodeShape);
		final var centerY = -directionVector.getY() * (RADIUS - distanceToNodeShape);
		g.fillOval((int) centerX - RADIUS, (int) centerY - RADIUS, DIAMETER, DIAMETER);
	}
}
