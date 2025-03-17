package graph.rendering.shapes;

import graph.GraphNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CoarseShape extends NodeShape {
	private final NodeShape baseShape;

	public CoarseShape(final NodeShape baseShape) {
		this.baseShape = baseShape;
	}

	@Override
	public void paint(final Graphics2D g, final GraphNode node, final Color strokeColor, final Color fillColor) {
		baseShape.paint(g, node, strokeColor, fillColor);
		// Draw coarse cross
		final Rectangle2D bounds = getBounds(node);
		g.drawLine(0, (int) bounds.getY(), 0, (int) (bounds.getHeight() * 0.5f));
		g.drawLine((int) bounds.getX(), 0, (int) (bounds.getWidth() * 0.5f), 0);
	}

	@Override
	public Rectangle2D getBounds(final GraphNode node) {
		return baseShape.getBounds(node);
	}

	@Override
	public float getBoundsDistance(final GraphNode node, final Point2D directionVector) {
		return baseShape.getBoundsDistance(node, directionVector);
	}
}
