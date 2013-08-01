package gui;

import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;

import edu.uci.ics.jung.visualization.annotations.Annotation;

public class MyAnnotation{

	private RectangularShape shape;
	private Annotation a;
	private String text;
	
	public MyAnnotation(Annotation annotation, RectangularShape shape, String text){
		this.a = annotation;
		this.shape = shape;
		this.text = text;
	}
	
	public RectangularShape getShape(){
		return this.shape;
	}
	
	
	public Annotation getAnnotation(){
		return this.a;
	}
	
	public String getText(){
		return this.text;
	}

}
