package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.List;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transition extends PNNode {

	private boolean simulationActive;

	@Setter(AccessLevel.NONE)
	private String firingCondition = "true";// "time>9.8";

	// private Vector<Double> simActualSpeed = new Vector<Double>();

	private boolean knockedOut = false;

	public void setFiringCondition(String firingCondition) {
		this.firingCondition = firingCondition.trim();
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
		if (label.equals(""))
			setLabel(name);
		if (name.equals(""))
			setName(label);
		/*
		 * setShape(shapes.getRectangle()); Rectangle bounds = getShape().getBounds();
		 * // System.out.println("hoehe: "+bounds.getHeight()); //
		 * System.out.println("weite: "+bounds.getWidth()); AffineTransform transform =
		 * new AffineTransform(); // transform.translate(x2, y2 - bounds.getHeight() /
		 * 2); transform.scale(bounds.getWidth() * 3, bounds.getHeight());
		 * setShape(transform.createTransformedShape(getShape())); this.setColor(new
		 * Color(255, 255, 255)); // this.set
		 * 
		 * Shape s = shapes.getRectangle();
		 */
		// s.
		// Rectangle bounds = s.getBounds();
		AffineTransform transform2 = new AffineTransform();

		transform2.translate(1, 1);
		transform2.scale(1, 2);
		setDefaultShape(transform2.createTransformedShape(new VertexShapes().getRectangle()));
		setDefaultColor(Color.white);

		setBiologicalElement(Elementdeclerations.transition);
	}

	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("firingCondition");
		return list;
	}
}
