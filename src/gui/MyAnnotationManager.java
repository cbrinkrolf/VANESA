package gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import edu.uci.ics.jung.visualization.annotations.AnnotationManager;
import edu.uci.ics.jung.visualization.annotations.AnnotationPaintable;

public class MyAnnotationManager extends AnnotationManager {

	//private List<MyAnnotation> annotations;
	private List<MyAnnotation> annotations; 
	private HashMap<Annotation<?>, MyAnnotation> annotationMap;
	
	
	
	public MyAnnotationManager(RenderContext<?, ?> rc) {
		super(rc);
		//this.annotations = new ArrayList<MyAnnotation>();
		annotations = new ArrayList<MyAnnotation>();
		annotationMap = new HashMap<Annotation<?>, MyAnnotation>();
		// TODO Auto-generated constructor stub
	}
	
	public void add(Annotation.Layer layer, Annotation<?> annotation){
		System.out.println("Annotation has not been added correctly!");
	}

	public void add(Annotation.Layer layer, MyAnnotation annotation) {
		//System.out.println("added");
		//System.out.println("added");
		//System.out.println(annotation.getAnnotation());
		
		//System.out.println(annotation.getPaint());
		//super.add(layer, annotation);
		//Annotation a2 = new Annotation(new Rectangle(60,60), layer, new Color(255,0,0), true, new Point2D.Double(0,0));
		//super.add(Annotation.Layer.UPPER, a2);
		//this.annotations.add(annotation);
		super.add(layer, annotation.getAnnotation());
		
		annotations.add(annotation);
		annotationMap.put(annotation.getAnnotation(), annotation);
		//System.out.println("shape: "+annotation.getShape());
		//System.out.println("anzahl: " + this.annotations.size());
		
	}
	
	@Override
	public void remove(Annotation<?> annotation){
		super.remove(annotation);
		annotations.remove(annotationMap.get(annotation));
		annotationMap.remove(annotation);
	}

	public void remove(MyAnnotation annotation) {
		//this.annotations.remove(annotation);
		
		super.remove(annotation.getAnnotation());
		annotations.remove(annotation);
		annotationMap.remove(annotation.getAnnotation());
		//System.out.println("anzahl: " + this.mapping.size());
	}
	
	public AnnotationPaintable getLowerAnnotationPaintable(){
		return this.lowerAnnotationPaintable;
	}
	
	public AnnotationPaintable getUpperAnnotationPaintable(){
		return this.upperAnnotationPaintable;
	}
	
	public List<MyAnnotation> getAnnotations(){
		return this.annotations;
	}
	
	public void setEnable(MyAnnotation annotation, boolean enable){
		if(enable){
			super.add(Annotation.Layer.LOWER, annotation.getAnnotation());
		}else{
			super.remove(annotation.getAnnotation());
		}
	}
	
	public boolean isEnabled(MyAnnotation ma){
				
		return super.getLowerAnnotationPaintable().getAnnotations().contains(ma.getAnnotation());
	}

}
