package graph.annotations;

import graph.rendering.annotations.RectangleAnnotationShape;

public class RectangleAnnotation extends VanesaAnnotation {
	public RectangleAnnotation() {
		shape = new RectangleAnnotationShape();
	}

	private float width = 1;
	private float height = 1;

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
}
