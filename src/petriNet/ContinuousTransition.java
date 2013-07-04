package petriNet;

import graph.jung.graphDrawing.VertexShapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;

public class ContinuousTransition extends Transition {

	private final double delay = 1;

	public ContinuousTransition(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.contoniousTransition);
		setModellicaString("PNlib.TC");
		setColor(Color.WHITE);
		shapes = new VertexShapes();
		setShape(shapes.getDoubleRectangle());
		Rectangle bounds = getShape().getBounds();
		// System.out.println("hoehe: "+bounds.getHeight());
		// System.out.println("weite: "+bounds.getWidth());
		AffineTransform transform = new AffineTransform();
		// transform.translate(x2, y2 - bounds.getHeight() / 2);
		transform.scale(bounds.getWidth() * 3, bounds.getHeight());
		setShape(transform.createTransformedShape(getShape()));

		AffineTransform transform2 = new AffineTransform();

		transform2.translate(1, 1);
		transform2.scale(1, 2);
		setShape(transform2.createTransformedShape(shapes.getDoubleRectangle()));

	}

	public double getDelay() {
		return delay;
	}

	public void rebuildShape(VertexShapes vs) {
		/*System.out.println("rebuild");
		Shape s = null; // vs.getDoubleRectangle(getVertex());
		// s.
		// Rectangle bounds = s.getBounds();
		AffineTransform transform = new AffineTransform();
		transform.translate(1, 1);
		transform.scale(1, 2);
		setShape(transform.createTransformedShape(s));*/
		// setShape(s);
	}

}
