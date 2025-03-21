package graph.annotations;

import graph.rendering.annotations.TextAnnotationShape;

public class TextAnnotation extends VanesaAnnotation {
	private String text;

	public TextAnnotation() {
		shape = new TextAnnotationShape();
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}
}
