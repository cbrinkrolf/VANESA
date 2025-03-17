package graph.rendering.shapes;

import graph.GraphNode;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ContinuousTransitionShape extends TransitionShape {
	@Override
	public void paint(final Graphics2D g, final GraphNode node, final Color strokeColor, final Color fillColor) {
		super.paint(g, node, strokeColor, fillColor);
		// Draw additional inner rectangle
		final Rectangle2D bounds = getBounds(node);
		g.drawRect((int) (bounds.getX() + 5), (int) (bounds.getY() + 5), (int) bounds.getWidth() - 10,
				(int) bounds.getHeight() - 10);
	}
}
