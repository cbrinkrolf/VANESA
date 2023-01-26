package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.List;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class ContinuousTransition extends Transition {
	// private final double delay = 1;
	// scalar or scalar function for maximum speed
	private String maximalSpeed = "1";

	public ContinuousTransition(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.continuousTransition);
		setColor(Color.WHITE);
		/*
		 * Shape s = shapes.getDoubleRectangle(); Rectangle bounds = s.getBounds(); //
		 * System.out.println("hoehe: "+bounds.getHeight()); //
		 * System.out.println("weite: "+bounds.getWidth()); AffineTransform transform =
		 * new AffineTransform(); // transform.translate(x2, y2 - bounds.getHeight() /
		 * 2); transform.scale(bounds.getWidth() * 3, bounds.getHeight()); s =
		 * transform.createTransformedShape(s);
		 */
		AffineTransform transform2 = new AffineTransform();

//		/transform2.translate(1, 1);
		transform2.scale(1, 2);
		setDefaultShape(transform2.createTransformedShape(new VertexShapes().getDoubleRectangle()));
	}

	public String getMaximalSpeed() {
		return maximalSpeed;
	}

	public void setMaximalSpeed(String maximalSpeed) {
		this.maximalSpeed = maximalSpeed;
	}

	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("maximalSpeed");
		return list;
	}
}
