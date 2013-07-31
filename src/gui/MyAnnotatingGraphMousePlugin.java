package gui;

import java.awt.event.MouseEvent;
import java.awt.geom.RectangularShape;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import graph.GraphInstance;

public class MyAnnotatingGraphMousePlugin<V, E> extends
		AnnotatingGraphMousePlugin<V, E> {

	public MyAnnotatingGraphMousePlugin(RenderContext<V, E> rc) {
		super(rc);
		this.annotationManager = GraphInstance.getMyGraph()
				.getAnnotationManager();
		// System.out.println("rc: "+rc);
		// TODO Auto-generated constructor stub

	}

	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);

		Annotation a = ((MyAnnotationManager) this.annotationManager)
				.getCurrentAnnotation();
		RectangularShape s = (RectangularShape)this.getRectangularShape().clone();
		if (a != null) {
			if (s.getWidth() < 20 || s.getHeight() < 20) {
				this.annotationManager.remove(a);
			} else {
				MyAnnotation ma = new MyAnnotation(a, s);
				//System.out.println(this.getRectangularShape().getWidth());
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
				//System.out.println("addGM");
				((MyAnnotationManager) this.annotationManager).add(layer, ma);
			}
		}
	}

}
