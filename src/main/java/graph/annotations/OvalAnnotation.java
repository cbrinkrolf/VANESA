package graph.annotations;

import graph.rendering.annotations.OvalAnnotationShape;

public class OvalAnnotation extends VanesaAnnotation {
	private float radiusX = 1;
	private float radiusY = 1;

	public OvalAnnotation() {
		shape = new OvalAnnotationShape();
	}

	public void setRadius(final float radius) {
		radiusX = radius;
		radiusY = radius;
	}

	public float getRadiusX() {
		return radiusX;
	}

	public void setRadiusX(final float radiusX) {
		this.radiusX = radiusX;
	}

	public float getRadiusY() {
		return radiusY;
	}

	public void setRadiusY(final float radiusY) {
		this.radiusY = radiusY;
	}
}
