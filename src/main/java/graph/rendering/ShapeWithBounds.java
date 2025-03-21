package graph.rendering;

import graph.Rect;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;

public abstract class ShapeWithBounds<T> {
	protected static final BasicStroke DEFAULT_STROKE = new BasicStroke(3);

	public abstract Rect getBounds(final T element);

	public abstract float getBoundsDistance(final T element, final Point2D directionVector);

	public boolean isMouseInside(final T node, final Point2D localMousePosition) {
		final var distanceFromCenter = Math.sqrt(localMousePosition.getX() * localMousePosition.getX()
				+ localMousePosition.getY() * localMousePosition.getY());
		final var boundsDistance = getBoundsDistance(node,
				new Point2D.Double(localMousePosition.getX() / distanceFromCenter,
						localMousePosition.getY() / distanceFromCenter));
		return distanceFromCenter < boundsDistance;
	}
}
