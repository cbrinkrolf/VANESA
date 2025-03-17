package graph.rendering.shapes;

import graph.GraphNode;

import java.awt.Graphics2D;
import java.awt.Color;

public class ContinuousPlaceShape extends PlaceShape {
	@Override
	public void paint(final Graphics2D g, final GraphNode node, final Color strokeColor, final Color fillColor) {
		super.paint(g, node, strokeColor, fillColor);
		// Draw additional inner circle
		double radius = RADIUS * node.getSize() - 5;
		g.drawOval(-(int) radius, -(int) radius, (int) (radius * 2), (int) (radius * 2));
	}
}
