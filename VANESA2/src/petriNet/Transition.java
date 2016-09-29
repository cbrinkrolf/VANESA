package petriNet;

import graph.jung.graphDrawing.VertexShapes;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import biologicalElements.Elementdeclerations;

public class Transition extends PNNode {

	private boolean simulationActive;

	//private boolean fireTransition = true;

	private String firingCondition = "true";//"time>9.8";
	
	//private Vector<Double> simActualSpeed = new Vector<Double>();
	private Color plotColor;
	
	private boolean knockedOut = false;
	
	public String getFiringCondition() {
		return firingCondition;
	}

	public void setFiringCondition(String firingCondition) {
		this.firingCondition = firingCondition;
	}

//	public boolean isFireTransition() {
//		return fireTransition;
//	}
//
//	public void setFireTransition(boolean fireTransition) {
//		this.fireTransition = fireTransition;
//	}

	
	public Transition(String label, String name) {		
		super(label, name);
		if (label.equals("")) setLabel(name);
		if (name.equals("")) setName(label);
		shapes = new VertexShapes();
		/*setShape(shapes.getRectangle());
		Rectangle bounds = getShape().getBounds();
		// System.out.println("hoehe: "+bounds.getHeight());
		// System.out.println("weite: "+bounds.getWidth());
		AffineTransform transform = new AffineTransform();
		// transform.translate(x2, y2 - bounds.getHeight() / 2);
		transform.scale(bounds.getWidth() * 3, bounds.getHeight());
		setShape(transform.createTransformedShape(getShape()));
		this.setColor(new Color(255, 255, 255));
		// this.set
		
		Shape s = shapes.getRectangle();*/
		// s.
		// Rectangle bounds = s.getBounds();
		AffineTransform transform2 = new AffineTransform();
		
		transform2.translate(1, 1);
		transform2.scale(1, 2);
		setDefaultShape(transform2.createTransformedShape(shapes.getRectangle()));
		setDefaultColor(Color.white);
		
		setBiologicalElement(Elementdeclerations.transition);
	}

	@Override
	public void rebuildShape(VertexShapes vs) {
		/*Shape s = vs.getRectangle();
		// s.
		// Rectangle bounds = s.getBounds();
		AffineTransform transform = new AffineTransform();
		transform.translate(1, 1);
		transform.scale(1, 2);
		setShape(transform.createTransformedShape(s));*/
		// setShape(s);
	}

	public boolean isSimulationActive() {
		return simulationActive;
	}

	public void setSimulationActive(boolean simulationActive) {
		this.simulationActive = simulationActive;
	}

	public void setPlotColor(Color plotColor) {
		this.plotColor = plotColor;
	}

	public Color getPlotColor() {
		return plotColor;
	}

	public boolean isKnockedOut() {
		return knockedOut;
	}

	public void setKnockedOut(boolean knockedOut) {
		this.knockedOut = knockedOut;
	}

}
