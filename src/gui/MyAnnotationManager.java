package gui;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import edu.uci.ics.jung.visualization.annotations.AnnotationManager;
import edu.uci.ics.jung.visualization.annotations.AnnotationPaintable;

public class MyAnnotationManager extends AnnotationManager {

	//private List<MyAnnotation> annotations;
	private Annotation currentAnnotation;
	private HashMap<Annotation, MyAnnotation> annotations; 
	
	
	

	public Annotation getCurrentAnnotation() {
		return currentAnnotation;
	}

	public void setCurrentAnnotation(Annotation currentAnnotation) {
		this.currentAnnotation = currentAnnotation;
	}

	public MyAnnotationManager(RenderContext<?, ?> rc) {
		super(rc);
		//this.annotations = new ArrayList<MyAnnotation>();
		annotations = new HashMap<Annotation, MyAnnotation>();
		// TODO Auto-generated constructor stub
	}
	
	public void add(Annotation.Layer layer, Annotation<?> annotation) {
		//System.out.println("2");
		super.add(layer, annotation);
		this.currentAnnotation = annotation;
		//annotation.setPaint(Color.red);
	}

	public void add(Annotation.Layer layer, MyAnnotation annotation) {
		//System.out.println("added");
		//System.out.println(annotation.getAnnotation());
		
		//System.out.println(annotation.getPaint());
		//super.add(layer, annotation);
		//Annotation a2 = new Annotation(new Rectangle(60,60), layer, new Color(255,0,0), true, new Point2D.Double(0,0));
		//super.add(Annotation.Layer.UPPER, a2);
		//this.annotations.add(annotation);
		annotations.put(annotation.getAnnotation(), annotation);
		//System.out.println("shape: "+annotation.getShape());
		this.currentAnnotation = null;
		//System.out.println("anzahl: " + this.annotations.size());
		
	}

	public void remove(Annotation<?> annotation) {
		//this.annotations.remove(annotation);
		annotations.remove(annotation);
		super.remove(annotation);
		//System.out.println("anzahl: " + this.mapping.size());
	}
	
	public AnnotationPaintable getLowerAnnotationPaintable(){
		return this.lowerAnnotationPaintable;
	}
	
	public AnnotationPaintable getUpperAnnotationPaintable(){
		return this.upperAnnotationPaintable;
	}
	
	public Collection<MyAnnotation> getAnnotations(){
		return this.annotations.values();
	}

}
