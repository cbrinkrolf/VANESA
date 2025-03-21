package graph.rendering.annotations;

import graph.GraphAnnotation;
import graph.Rect;
import graph.annotations.RectangleAnnotation;

import java.awt.*;
import java.awt.geom.Point2D;

public class RectangleAnnotationShape extends AnnotationShape {
	@Override
	public void paint(final Graphics2D g, final GraphAnnotation annotation, final Color strokeColor,
			final Color fillColor) {
		if (annotation instanceof RectangleAnnotation) {
			float width = ((RectangleAnnotation) annotation).getWidth();
			float height = ((RectangleAnnotation) annotation).getHeight();
			g.setColor(fillColor);
			g.fillRect((int) (-width * 0.5f), (int) (-height * 0.5f), (int) width, (int) height);
			g.setColor(strokeColor);
			g.drawRect((int) (-width * 0.5f), (int) (-height * 0.5f), (int) width, (int) height);
		}
	}

	@Override
	public Rect getBounds(final GraphAnnotation annotation) {
		if (annotation instanceof RectangleAnnotation) {
			float width = ((RectangleAnnotation) annotation).getWidth();
			float height = ((RectangleAnnotation) annotation).getHeight();
			return new Rect(-width * 0.5f, -height * 0.5f, width, height);
		}
		return Rect.EMPTY;
	}

	@Override
	public float getBoundsDistance(final GraphAnnotation annotation, final Point2D directionVector) {
		if (annotation instanceof RectangleAnnotation) {
			float width = ((RectangleAnnotation) annotation).getWidth();
			float height = ((RectangleAnnotation) annotation).getHeight();
			final var xFactor = width * 0.5f / Math.abs(directionVector.getX());
			final var yFactor = height * 0.5f / Math.abs(directionVector.getY());
			return (float) Math.min(xFactor, yFactor);
		}
		return 0;
	}
}
