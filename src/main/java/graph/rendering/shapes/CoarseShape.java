package graph.rendering.shapes;

import graph.GraphNode;
import graph.Rect;

import java.awt.*;
import java.awt.geom.Point2D;

public class CoarseShape extends NodeShape {
	private final NodeShape baseShape;

	public CoarseShape(final NodeShape baseShape) {
		this.baseShape = baseShape;
	}

	@Override
	public void paint(final Graphics2D g, final GraphNode node, final Color strokeColor, final Color fillColor) {
		baseShape.paint(g, node, strokeColor, fillColor);
		// Draw coarse cross
		final Rect bounds = getBounds(node);
		g.drawLine((int) bounds.x, 0, (int) (bounds.width * 0.5f), 0);
		g.drawLine(0, (int) bounds.y, 0, (int) (bounds.height * 0.5f));
	}

	@Override
	public Rect getBounds(final GraphNode node) {
		return baseShape.getBounds(node);
	}

	@Override
	public float getBoundsDistance(final GraphNode node, final Point2D directionVector) {
		return baseShape.getBoundsDistance(node, directionVector);
	}
}
