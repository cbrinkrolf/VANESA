package graph.rendering.shapes;

import graph.GraphEdge;

import java.awt.*;
import java.awt.geom.Point2D;

public class ArrowTipShape extends EdgeTipShape {
	private static final int ARROW_TIP_HEIGHT = 16;
	private static final int ARROW_TIP_WIDTH = 16;

	@Override
	public void paint(final Graphics2D g, final GraphEdge<?> edge, final Point2D directionVector,
			final double distanceToNodeShape) {
		final var triangleVertexTopX = directionVector.getX() * distanceToNodeShape;
		final var triangleVertexTopY = directionVector.getY() * distanceToNodeShape;
		final var triangleVertexBottomX = triangleVertexTopX - directionVector.getX() * ARROW_TIP_HEIGHT;
		final var triangleVertexBottomY = triangleVertexTopY - directionVector.getY() * ARROW_TIP_HEIGHT;
		final var directionVectorBackwardCCWPerpendicularX = -directionVector.getY() * ARROW_TIP_WIDTH * 0.5f;
		final var directionVectorBackwardCCWPerpendicularY = directionVector.getX() * ARROW_TIP_WIDTH * 0.5f;
		g.fillPolygon(new int[] { (int) triangleVertexTopX,
						(int) (triangleVertexBottomX + directionVectorBackwardCCWPerpendicularX),
						(int) (triangleVertexBottomX - directionVectorBackwardCCWPerpendicularX), },
				new int[] { (int) triangleVertexTopY,
						(int) (triangleVertexBottomY + directionVectorBackwardCCWPerpendicularY),
						(int) (triangleVertexBottomY - directionVectorBackwardCCWPerpendicularY), }, 3);
	}
}
