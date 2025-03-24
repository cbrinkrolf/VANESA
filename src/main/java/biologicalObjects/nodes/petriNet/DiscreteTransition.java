package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.List;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;

public class DiscreteTransition extends Transition {
	private String delay = "1";

	public DiscreteTransition(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.discreteTransition, parent);
		setDefaultShape(VertexShapes.getDiscreteTransitionShape());
		setColor(Color.WHITE);
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("delay");
		return list;
	}
}
