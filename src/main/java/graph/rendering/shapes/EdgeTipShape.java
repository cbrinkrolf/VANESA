package graph.rendering.shapes;

import graph.GraphEdge;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class EdgeTipShape {
	public abstract void paint(final Graphics2D g, final GraphEdge<?> edge, final Point2D directionVector,
			final double distanceToNodeShape);
}
