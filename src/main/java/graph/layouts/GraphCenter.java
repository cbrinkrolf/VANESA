package graph.layouts;

import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.jung.classes.MyGraph;

public class GraphCenter {
	private final Point2D center;
	private final double width;
	private final double height;
	private double minX = Double.MAX_VALUE;
	private double minY = Double.MAX_VALUE;

	public GraphCenter(final MyGraph g) {
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		for (BiologicalNodeAbstract bna : g.getAllVertices()) {
			final Point2D p = g.getVertexLocation(bna);
			if (p.getX() < minX) {
				minX = p.getX();
			}
			if (p.getX() > maxX) {
				maxX = p.getX();
			}
			if (p.getY() < minY) {
				minY = p.getY();
			}
			if (p.getY() > maxY) {
				maxY = p.getY();
			}
		}
		for (int i = 0; i < g.getAnnotationManager().getAnnotations().size(); i++) {
			final RectangularShape s = g.getAnnotationManager().getAnnotations().get(i).getShape();
			if (s.getMinX() < minX) {
				minX = s.getMinX();
			}
			if (s.getMaxX() > maxX) {
				maxX = s.getMaxX();
			}
			if (s.getMinY() < minY) {
				minY = s.getMinY();
			}
			if (s.getMaxY() > maxY) {
				maxY = s.getMaxY();
			}
		}
		width = maxX - minX;
		height = maxY - minY;
		center = new Point2D.Double(minX + width / 2, minY + height / 2);
	}

	public Point2D getCenter() {
		return center;
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}
}
