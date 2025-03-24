package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.List;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;

public class ContinuousTransition extends Transition {
	// private final double delay = 1;
	// scalar or scalar function for maximum speed
	private String maximalSpeed = "1";

	public ContinuousTransition(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.continuousTransition, parent);
		setDefaultShape(VertexShapes.getContinuousTransitionShape());
		setColor(Color.WHITE);
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
