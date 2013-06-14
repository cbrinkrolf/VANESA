package petriNet;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Vector;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.BiologicalNodeAbstract;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Transition extends BiologicalNodeAbstract {

	private Vector TransitionIn = new Vector<Transition>();

	public Vector getTransitionIn() {
		return TransitionIn;
	}

	private boolean fireTransition = true;

	private String fireTransitionStatemanet = "time>9.8";

	public String getFireTransitionStatemanet() {
		return fireTransitionStatemanet;
	}

	public void setFireTransitionStatemanet(String fireTransitionStatemanet) {
		this.fireTransitionStatemanet = fireTransitionStatemanet;
	}

	public boolean isFireTransition() {
		return fireTransition;
	}

	public void setFireTransition(boolean fireTransition) {
		this.fireTransition = fireTransition;
	}

	public void setTransitionIn(Vector transitionIn) {
		TransitionIn = transitionIn;
	}

	private Vector TransitionOut = new Vector<Transition>();

	public Vector getTransitionOut() {
		return TransitionOut;
	}

	public void setTransitionOut(Vector transitionOut) {
		TransitionOut = transitionOut;
	}

	private String modellicaString;

	public String getModellicaString() {
		return modellicaString;
	}

	public void setModellicaString(String modellicaString) {
		this.modellicaString = modellicaString;
	}

	public Transition(String label, String name) {		
		super(label, name);
		if (label.equals("")) setLabel(name);
		if (name.equals("")) setName(label);
		shapes = new VertexShapes();
//		setShape(shapes.getRectangle(getVertex()));
		Rectangle bounds = getShape().getBounds();
		// System.out.println("hoehe: "+bounds.getHeight());
		// System.out.println("weite: "+bounds.getWidth());
		AffineTransform transform = new AffineTransform();
		// transform.translate(x2, y2 - bounds.getHeight() / 2);
		transform.scale(bounds.getWidth() * 3, bounds.getHeight());
		setShape(transform.createTransformedShape(getShape()));
		this.setColor(new Color(255, 255, 255));
		// this.set
		setAbstract(false);
		setReference(false);
		setBiologicalElement(Elementdeclerations.transition);
	}

	@Override
	public void rebuildShape(VertexShapes vs) {
		Shape s = null; //vs.getRectangle(getVertex());
		// s.
		// Rectangle bounds = s.getBounds();
		AffineTransform transform = new AffineTransform();
		transform.translate(1, 1);
		transform.scale(1, 2);
		setShape(transform.createTransformedShape(s));
		// setShape(s);
	}

}
