package graph;

import graph.rendering.annotations.AnnotationShape;

import java.awt.Color;

public interface GraphAnnotation {
	boolean isVisible();

	Color getColor();

	AnnotationShape getShape();
}
