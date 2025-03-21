package graph.rendering.annotations;

import graph.GraphAnnotation;
import graph.Rect;

import java.awt.*;
import java.awt.geom.Point2D;

public class TextAnnotationShape extends AnnotationShape {
	@Override
	public void paint(final Graphics2D g, final GraphAnnotation annotation, final Color strokeColor,
			final Color fillColor) {

	}

	@Override
	public Rect getBounds(final GraphAnnotation annotation) {
		return null;
	}

	@Override
	public float getBoundsDistance(final GraphAnnotation annotation, final Point2D directionVector) {
		return 0;
	}
}
