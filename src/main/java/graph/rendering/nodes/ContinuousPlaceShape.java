package graph.rendering.nodes;

import graph.GraphNode;

import java.awt.Graphics2D;
import java.awt.Color;

public class ContinuousPlaceShape extends PlaceShape {
	@Override
	public void paint(final Graphics2D g, final GraphNode node, final Color strokeColor, final Color fillColor) {
		super.paint(g, node, strokeColor, fillColor);
		// Draw additional inner circle
		final int radius = (int) (RADIUS * node.getSize()) - 5;
		final int diameter = radius * 2;
		g.drawOval(-radius, -radius, diameter, diameter);
	}
}
