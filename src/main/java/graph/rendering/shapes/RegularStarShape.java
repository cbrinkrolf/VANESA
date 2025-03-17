package graph.rendering.shapes;

import graph.GraphNode;
import graph.Rect;

import java.awt.*;
import java.awt.geom.Point2D;

public class RegularStarShape extends NodeShape {
	private static final int RADIUS = 10;
	private static final int INSET_RADIUS = 5;
	private final double[] pathPointsX;
	private final double[] pathPointsY;
	private final int[] scaledPathPointsX;
	private final int[] scaledPathPointsY;

	public RegularStarShape(final int sides) {
		pathPointsX = new double[sides * 2];
		pathPointsY = new double[sides * 2];
		scaledPathPointsX = new int[pathPointsX.length];
		scaledPathPointsY = new int[pathPointsX.length];
		final double stepAngle = Math.PI * 2 / (sides * 2);
		for (int i = 0; i < sides * 2; i++) {
			final var radius = i % 2 == 0 ? INSET_RADIUS : RADIUS;
			pathPointsX[i] = radius * Math.cos(stepAngle * i);
			pathPointsY[i] = radius * Math.sin(stepAngle * i);
		}
	}

	@Override
	public void paint(final Graphics2D g, final GraphNode node, final Color strokeColor, final Color fillColor) {
		for (int i = 0; i < scaledPathPointsX.length; i++) {
			scaledPathPointsX[i] = (int) (pathPointsX[i] * node.getSize());
			scaledPathPointsY[i] = (int) (pathPointsY[i] * node.getSize());
		}
		g.setColor(fillColor);
		g.fillPolygon(scaledPathPointsX, scaledPathPointsY, pathPointsX.length);
		g.setColor(strokeColor);
		g.setStroke(DEFAULT_STROKE);
		g.drawPolygon(scaledPathPointsX, scaledPathPointsY, pathPointsX.length);
	}

	@Override
	public Rect getBounds(final GraphNode node) {
		double radius = RADIUS * node.getSize();
		return new Rect(-radius, -radius, radius * 2, radius * 2);
	}

	@Override
	public float getBoundsDistance(final GraphNode node, final Point2D directionVector) {
		// TODO: calculate exact solution
		return (float) (RADIUS * node.getSize());
	}
}
