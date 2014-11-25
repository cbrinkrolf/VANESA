package gui;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;

import javax.swing.JOptionPane;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import graph.GraphInstance;

public class MyAnnotatingGraphMousePlugin<V, E> extends
		AnnotatingGraphMousePlugin<V, E> {

	private int currentType = AnnotationPainter.RECTANGLE;
	private String annotationString;

	public MyAnnotatingGraphMousePlugin(RenderContext<V, E> rc) {
		super(rc);
		this.annotationManager = GraphInstance.getMyGraph()
				.getAnnotationManager();
		// System.out.println("rc: "+rc);
		// TODO Auto-generated constructor stub

	}

	public void setCurrentType(int type) {
		this.currentType = type;
		if (type == AnnotationPainter.RECTANGLE) {
			setRectangularShape(new Rectangle());
		} else {
			if (type == AnnotationPainter.ELLIPSE) {
				setRectangularShape(new Ellipse2D.Double());
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e.getSource();
		if (currentType == AnnotationPainter.TEXT) {
			annotationString = JOptionPane.showInputDialog(vv, "Annotation:");
			if (annotationString != null && annotationString.length() > 0) {
				Point2D p = vv.getRenderContext().getMultiLayerTransformer()
						.inverseTransform(down);
				
				Annotation<String> annotation = new Annotation<String>(
						annotationString, layer, annotationColor, false, p);
				annotationManager.add(layer, annotation);
			}
		} else if (e.getModifiers() == modifiers) {
			if (down != null) {
				Point2D out = e.getPoint();
				//System.out.println();
				RectangularShape arect = (RectangularShape) rectangularShape
						.clone();
				//Point2D p = vv.getRenderContext().getMultiLayerTransformer()
				//		.inverseTransform(down);
				//System.out.println(down);
				//System.out.println(super.getAnnotationColor());
				//arect.setFrameFromDiagonal(down, out);
				arect.setFrameFromDiagonal(down.getX(), down.getY(), out.getX(), out.getY());
				Shape s = vv.getRenderContext().getMultiLayerTransformer()
						.inverseTransform(arect);
				Annotation<Shape> annotation = new Annotation<Shape>(s, layer,
						annotationColor, fill, out);
				annotationManager.add(layer, annotation);
			}
		}
		down = null;
		vv.removePostRenderPaintable(lensPaintable);
		vv.repaint();

		Annotation<Annotation.Layer> a = ((MyAnnotationManager) this.annotationManager)
				.getCurrentAnnotation();
		RectangularShape s = (RectangularShape) this.getRectangularShape()
				.clone();
		// System.out.println("shape: "+s);
		if (a != null) {
			if (this.currentType != AnnotationPainter.TEXT
					&& (s.getWidth() < 20 || s.getHeight() < 20)) {
				this.annotationManager.remove(a);
			} else {
				MyAnnotation ma;
				Point2D.Double p1 = new Point2D.Double(s.getMinX(), s.getMinY());
				Point2D.Double p2 = new Point2D.Double(s.getMaxX(), s.getMaxY());
				
				Point2D p1inv = vv.getRenderContext().getMultiLayerTransformer()
						.inverseTransform(p1);
				Point2D p2inv = vv.getRenderContext().getMultiLayerTransformer()
						.inverseTransform(p2);
				//System.out.println("inv: "+vv.getRenderContext().getMultiLayerTransformer()
				//		.inverseTransform(p1));
				//System.out.println("t: "+vv.getRenderContext().getMultiLayerTransformer()
				//		.transform(p1));
				s.setFrameFromDiagonal(p1inv, p2inv);
				//System.out.println("neu: "+s.getMinX());
				if (currentType == AnnotationPainter.TEXT) {
					ma = new MyAnnotation(a, s,
							this.annotationString);
				} else {
					ma = new MyAnnotation(a, s, "");
				}
				//System.out.println("neu: "+ma.getShape().getMinX());
				// System.out.println(this.getRectangularShape().getWidth());
				// a.setPaint(new Color(255, 255, 255));
				/*
				 * RangeInfo r =new RangeInfo( this.getRectangularShape(),
				 * "bla", (Color)a.getPaint(), (Color)a.getPaint(),
				 * (Color)a.getPaint());
				 * RangeSelector.getInstance().addRangeInfo(r);
				 * System.out.println(this.annotationManager);
				 */
				// Annotation a2 = new Annotation(new Rectangle(60,60), layer,
				// new
				// Color(255,0,0), fill, new Point2D.Double(0,0));
				// a.setShape(this.getRectangularShape());
				// System.out.println(a);
				// System.out.println("addGM");
				//System.out.println("miiiin: "+ma.getShape().getMinX());
				((MyAnnotationManager) this.annotationManager).add(layer, ma);
			}
		}
	}

}
