package graph.rendering.shapes;

import graph.GraphNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class NodeShape {
	public abstract void paint(final Graphics2D g, final GraphNode node, final Color strokeColor,
			final Color fillColor);

	public abstract Rectangle2D getBounds(final GraphNode node);

	public abstract float getBoundsDistance(final GraphNode node, final Point2D directionVector);

	public boolean isMouseInside(final GraphNode node, final Point2D localMousePosition) {
		final var distanceFromCenter = Math.sqrt(localMousePosition.getX() * localMousePosition.getX()
				+ localMousePosition.getY() * localMousePosition.getY());
		final var boundsDistance = getBoundsDistance(node,
				new Point2D.Double(localMousePosition.getX() / distanceFromCenter,
						localMousePosition.getY() / distanceFromCenter));
		return distanceFromCenter < boundsDistance;
	}
}
