package petriNet;

import graph.jung.graphDrawing.VertexShapes;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import biologicalElements.Elementdeclerations;

//import edu.uci.ics.jung.graph.Vertex;

public class ContinuousTransition extends Transition {

	//private final double delay = 1;

	// scalar or scalar function for maximum speed
	private String maximumSpeed = "1";

	public ContinuousTransition(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.continuousTransition);
		setColor(Color.WHITE);
		shapes = new VertexShapes();
		/*Shape s = shapes.getDoubleRectangle();
		Rectangle bounds = s.getBounds();
		// System.out.println("hoehe: "+bounds.getHeight());
		// System.out.println("weite: "+bounds.getWidth());
		AffineTransform transform = new AffineTransform();
		// transform.translate(x2, y2 - bounds.getHeight() / 2);
		transform.scale(bounds.getWidth() * 3, bounds.getHeight());
		s = transform.createTransformedShape(s);
		*/
		AffineTransform transform2 = new AffineTransform();
		
//		/transform2.translate(1, 1);
		transform2.scale(1, 2);
		setDefaultShape(transform2.createTransformedShape(shapes.getDoubleRectangle()));

	}

	//public double getDelay() {
		//return delay;
	//}

	public String getMaximumSpeed() {
		return maximumSpeed;
	}

	public void setMaximumSpeed(String maximumSpeed) {
		this.maximumSpeed = maximumSpeed;
	}

	public void rebuildShape(VertexShapes vs) {
		/*
		 * System.out.println("rebuild"); Shape s = null; //
		 * vs.getDoubleRectangle(getVertex()); // s. // Rectangle bounds =
		 * s.getBounds(); AffineTransform transform = new AffineTransform();
		 * transform.translate(1, 1); transform.scale(1, 2);
		 * setShape(transform.createTransformedShape(s));
		 */
		// setShape(s);
	}

}
