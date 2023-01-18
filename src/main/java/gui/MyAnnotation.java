package gui;

import java.awt.geom.RectangularShape;

import edu.uci.ics.jung.visualization.annotations.Annotation;

public class MyAnnotation{

	private RectangularShape shape;
	private Annotation<?> a;
	private String text;
	private String name = "";
	
	public MyAnnotation(Annotation<?> annotation, RectangularShape shape, String text){
		this.a = annotation;
		this.shape = shape;
		this.text = text;
	}
	
	public RectangularShape getShape(){
		return this.shape;
	}
	
	
	public Annotation<?> getAnnotation(){
		return this.a;
	}
	
	public void setAnnotation(Annotation<?> a){
		this.a = a;
	}
	
	public String getText(){
		return this.text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
