package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.List;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;
import graph.rendering.shapes.ContinuousTransitionShape;

public class ContinuousTransition extends Transition {
	// private final double delay = 1;
	// scalar or scalar function for maximum speed
	private String maximalSpeed = "1";

	public ContinuousTransition(final String label, final String name) {
		super(label, name);
		setDefaultShape(VertexShapes.getContinuousTransitionShape());
		setNodeShape(new ContinuousTransitionShape());
		setBiologicalElement(Elementdeclerations.continuousTransition);
		setColor(Color.WHITE);
	}

	public ContinuousTransition(final String label, final String name, final Pathway pathway) {
		super(label, name, pathway);
		setDefaultShape(VertexShapes.getContinuousTransitionShape());
		setNodeShape(new ContinuousTransitionShape());
		setBiologicalElement(Elementdeclerations.continuousTransition);
		setColor(Color.WHITE);
	}

	public String getMaximalSpeed() {
		return maximalSpeed;
	}

	public void setMaximalSpeed(final String maximalSpeed) {
		this.maximalSpeed = maximalSpeed;
	}

	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("maximalSpeed");
		return list;
	}
}
