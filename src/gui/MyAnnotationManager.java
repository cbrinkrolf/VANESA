package gui;

import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import edu.uci.ics.jung.visualization.annotations.AnnotationManager;
import edu.uci.ics.jung.visualization.annotations.AnnotationPaintable;
import graph.GraphInstance;

public class MyAnnotationManager extends AnnotationManager {

	// private List<MyAnnotation> annotations;
	private List<MyAnnotation> annotations;
	private HashMap<Annotation<?>, MyAnnotation> annotationMap;

	public MyAnnotationManager(RenderContext<?, ?> rc) {
		super(rc);
		// this.annotations = new ArrayList<MyAnnotation>();
		annotations = new ArrayList<MyAnnotation>();
		annotationMap = new HashMap<Annotation<?>, MyAnnotation>();
	}

	public void add(Annotation.Layer layer, Annotation<?> annotation) {
		System.out.println("Annotation has not been added correctly!");
	}

	public void add(Annotation.Layer layer, MyAnnotation annotation) {
		super.add(annotation.getAnnotation().getLayer(), annotation.getAnnotation());

		annotations.add(annotation);
		annotationMap.put(annotation.getAnnotation(), annotation);
		// System.out.println("shape: "+annotation.getShape());
		// System.out.println("anzahl: " + this.annotations.size());
	//	System.out.println("add, count: " + this.annotationMap.size());

	}

	@Override
	public void remove(Annotation<?> annotation) {
		//System.out.println("remove annotation");
		super.remove(annotation);
		annotations.remove(annotationMap.get(annotation));
		annotationMap.remove(annotation);
	}

	public void remove(MyAnnotation annotation) {
		// this.annotations.remove(annotation);
		if (annotationMap.containsValue(annotation)) {
			super.remove(annotation.getAnnotation());
			annotations.remove(annotation);
			annotationMap.remove(annotation.getAnnotation());
		}
		//System.out.println("remove, count: " + this.annotationMap.size());
	}

	public AnnotationPaintable getLowerAnnotationPaintable() {
		return this.lowerAnnotationPaintable;
	}

	public AnnotationPaintable getUpperAnnotationPaintable() {
		return this.upperAnnotationPaintable;
	}

	public List<MyAnnotation> getAnnotations() {
		return this.annotations;
	}

	public void setEnable(MyAnnotation annotation, boolean enable) {
		if (enable) {
			super.add(annotation.getAnnotation().getLayer(), annotation.getAnnotation());
		} else {
			super.remove(annotation.getAnnotation());
		}
	}

	public boolean isEnabled(MyAnnotation ma) {

		return super.getLowerAnnotationPaintable().getAnnotations().contains(ma.getAnnotation());
	}
	
	public void moveAnnotation(MyAnnotation ma, double xOffset, double yOffset){
		this.remove(ma);
		Annotation<?> a2;
		RectangularShape s = ma.getShape();
		s.setFrameFromDiagonal(s.getMinX() + xOffset, s.getMinY() + yOffset, s.getMaxX() + xOffset, s.getMaxY() + yOffset);

		if (ma.getText().length() > 0) {
			a2 = new Annotation<>(ma.getText(), Annotation.Layer.UPPER, ma.getAnnotation().getPaint(), false,
					new Point2D.Double(s.getMinX(), s.getMinY()));
		} else {
			a2 = new Annotation<>(s, Annotation.Layer.LOWER, ma.getAnnotation().getPaint(), true, new Point2D.Double(0, 0));
		}
		ma.setAnnotation(a2);
		add(a2.getLayer(), ma);
	}

	public void moveAllAnnotation(double xOffset, double yOffset) {

		List<MyAnnotation> list = new ArrayList<MyAnnotation>(this.annotations);
		MyAnnotation ma;
		for (int i = 0; i < list.size(); i++) {
			ma = list.get(i);
			this.moveAnnotation(ma, xOffset, yOffset);
		}
	}

	public MyAnnotation getMyAnnotations(Point2D p) {
		Annotation<?> an = super.getAnnotation(p);
		if (this.annotationMap.containsKey(an)) {
			return annotationMap.get(an);
		}
		return null;
	}

	public void updateMyAnnotation(MyAnnotation ma) {
		this.remove(ma);
		Annotation<?> a2;
		if (ma.getText().length() > 0) {
			a2 = new Annotation<>(ma.getText(), Annotation.Layer.UPPER, ma.getAnnotation().getPaint(), false,
					new Point2D.Double(ma.getShape().getMinX(), ma.getShape().getMinY()));
		} else {
			a2 = new Annotation<>(ma.getShape(), Annotation.Layer.LOWER, ma.getAnnotation().getPaint(), true, new Point2D.Double(0, 0));
		}
		ma.setAnnotation(a2);
		add(a2.getLayer(), ma);
		GraphInstance.getMyGraph().getVisualizationViewer().repaint();
	}
}
