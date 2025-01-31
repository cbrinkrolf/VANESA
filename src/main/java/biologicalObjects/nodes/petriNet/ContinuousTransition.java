package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.List;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class ContinuousTransition extends Transition {
	// private final double delay = 1;
	// scalar or scalar function for maximum speed
	private String maximalSpeed = "1";

	public ContinuousTransition(String label, String name) {
		super(label, name);
		setDefaultShape(VertexShapes.getContinuousTransitionShape());
		setBiologicalElement(Elementdeclerations.continuousTransition);
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
