package graph.annotations;

import graph.GraphAnnotation;
import graph.rendering.annotations.AnnotationShape;

import java.awt.*;

public abstract class VanesaAnnotation implements GraphAnnotation {
	protected AnnotationShape shape;
	protected Color color = Color.WHITE;

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public Color getColor() {
		return color;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	@Override
	public AnnotationShape getShape() {
		return shape;
	}
}
