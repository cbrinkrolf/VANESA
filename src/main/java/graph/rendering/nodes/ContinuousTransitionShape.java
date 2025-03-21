package graph.rendering.nodes;

import graph.GraphNode;
import graph.Rect;

import java.awt.*;

public class ContinuousTransitionShape extends TransitionShape {
	@Override
	public void paint(final Graphics2D g, final GraphNode node, final Color strokeColor, final Color fillColor) {
		super.paint(g, node, strokeColor, fillColor);
		// Draw additional inner rectangle
		final Rect bounds = getBounds(node);
		g.drawRect((int) (bounds.x + 5), (int) (bounds.y + 5), (int) bounds.width - 10, (int) bounds.height - 10);
	}
}
