package graph.rendering.annotations;

import graph.GraphAnnotation;
import graph.Rect;
import graph.annotations.OvalAnnotation;

import java.awt.*;
import java.awt.geom.Point2D;

public class OvalAnnotationShape extends AnnotationShape {
	@Override
	public void paint(final Graphics2D g, final GraphAnnotation annotation, final Color strokeColor,
			final Color fillColor) {
		if (annotation instanceof OvalAnnotation) {
			final float radiusX = ((OvalAnnotation) annotation).getRadiusX();
			final float radiusY = ((OvalAnnotation) annotation).getRadiusY();
			g.setColor(fillColor);
			g.fillOval((int) -radiusX, (int) -radiusY, (int) (radiusX * 2), (int) (radiusY * 2));
			g.setColor(strokeColor);
			g.drawOval((int) -radiusX, (int) -radiusY, (int) (radiusX * 2), (int) (radiusY * 2));
		}
	}

	@Override
	public Rect getBounds(final GraphAnnotation annotation) {
		if (annotation instanceof OvalAnnotation) {
			final float radiusX = ((OvalAnnotation) annotation).getRadiusX();
			final float radiusY = ((OvalAnnotation) annotation).getRadiusY();
			return new Rect(-radiusX, -radiusY, radiusX * 2, radiusY * 2);
		}
		return Rect.EMPTY;
	}

	@Override
	public float getBoundsDistance(final GraphAnnotation annotation, final Point2D directionVector) {
		if (annotation instanceof OvalAnnotation) {
			final var x = directionVector.getX() * ((OvalAnnotation) annotation).getRadiusX();
			final var y = directionVector.getY() * ((OvalAnnotation) annotation).getRadiusY();
			return (float) Math.sqrt(x * x + y * y);
		}
		return 0;
	}
}
