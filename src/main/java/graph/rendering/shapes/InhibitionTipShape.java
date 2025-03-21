package graph.rendering.shapes;

import graph.GraphEdge;

import java.awt.*;
import java.awt.geom.Point2D;

public class InhibitionTipShape extends EdgeTipShape {
	private static final int BAR_LENGTH_HALF = 5;

	@Override
	public void paint(final Graphics2D g, final GraphEdge<?> edge, final Point2D directionVector,
			final double distanceToNodeShape) {
		g.drawLine((int) (-directionVector.getY() * BAR_LENGTH_HALF), (int) (directionVector.getX() * BAR_LENGTH_HALF),
				(int) (directionVector.getY() * BAR_LENGTH_HALF), (int) (-directionVector.getX() * BAR_LENGTH_HALF));
	}
}
