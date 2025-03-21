package graph.rendering.nodes;

import graph.GraphNode;
import graph.rendering.ShapeWithBounds;

import java.awt.Graphics2D;
import java.awt.Color;

public abstract class NodeShape extends ShapeWithBounds<GraphNode> {
	public abstract void paint(final Graphics2D g, final GraphNode node, final Color strokeColor,
			final Color fillColor);
}
