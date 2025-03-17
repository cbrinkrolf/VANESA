package graph.rendering.shapes;

import graph.GraphNode;
import graph.Rect;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class NodeShape {
	protected static final BasicStroke DEFAULT_STROKE = new BasicStroke(3);

	public abstract void paint(final Graphics2D g, final GraphNode node, final Color strokeColor,
			final Color fillColor);

	public abstract Rect getBounds(final GraphNode node);

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
