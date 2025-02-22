package graph.eventhandlers;

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
import gui.annotation.AnnotationPainter;
import gui.annotation.MyAnnotation;
import gui.annotation.MyAnnotationManager;

public class MyAnnotatingGraphMousePlugin<V, E> extends AnnotatingGraphMousePlugin<V, E> {
	private int currentType = AnnotationPainter.RECTANGLE;
	private final VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv;

	public MyAnnotatingGraphMousePlugin(RenderContext<V, E> rc) {
		super(rc);
		this.annotationManager = GraphInstance.getMyGraph().getAnnotationManager();
		vv = GraphInstance.getMyGraph().getVisualizationViewer();
	}

	public int getCurrentType() {
		return currentType;
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
		MyAnnotation an = null;
		RectangularShape arect = null;
		if (currentType == AnnotationPainter.TEXT) {
			String annotationString = JOptionPane.showInputDialog(vv, "Annotation:");
			if (annotationString != null && annotationString.length() > 0) {
				Point2D p = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(down);
				Annotation<String> annotation = new Annotation<>(annotationString, layer, annotationColor, false, p);
				arect = (RectangularShape) rectangularShape.clone();
				Point2D.Double p1 = new Point2D.Double(arect.getMinX(), arect.getMinY());
				Point2D.Double p2 = new Point2D.Double(arect.getMaxX(), arect.getMaxY());
				Point2D p1inv = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p1);
				Point2D p2inv = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p2);
				arect.setFrameFromDiagonal(p1inv, p2inv);

				an = new MyAnnotation(annotation, arect, annotationString);
			}
		} else if (e.getModifiers() == modifiers) {
			if (down != null) {
				Point2D out = e.getPoint();
				arect = (RectangularShape) rectangularShape.clone();
				// Point2D p = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(down);
				// arect.setFrameFromDiagonal(down, out);
				arect.setFrameFromDiagonal(down.getX(), down.getY(), out.getX(), out.getY());
				Shape s = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(arect);
				Annotation<Shape> annotation = new Annotation<>(s, layer, annotationColor, fill, out);
				Point2D.Double p1 = new Point2D.Double(arect.getMinX(), arect.getMinY());
				Point2D.Double p2 = new Point2D.Double(arect.getMaxX(), arect.getMaxY());
				Point2D p1inv = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p1);
				Point2D p2inv = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p2);
				arect.setFrameFromDiagonal(p1inv, p2inv);
				an = new MyAnnotation(annotation, arect, "");
			}
		}
		down = null;
		vv.removePostRenderPaintable(lensPaintable);
		// vv.repaint();
		if (an != null) {
			if (this.currentType == AnnotationPainter.TEXT || (arect.getWidth() > 5 && arect.getHeight() > 5)) {
				// ((MyAnnotationManager)this.annotationManager).remove(an);
				((MyAnnotationManager) annotationManager).add(layer, an);
				vv.repaint();
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}
}
