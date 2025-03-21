package graph.rendering.annotations;

import graph.GraphAnnotation;
import graph.rendering.ShapeWithBounds;

import java.awt.Graphics2D;
import java.awt.Color;

public abstract class AnnotationShape extends ShapeWithBounds<GraphAnnotation> {
	public abstract void paint(final Graphics2D g, final GraphAnnotation annotation, final Color strokeColor,
			final Color fillColor);
}
