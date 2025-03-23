package transformation.graphElements;

import java.awt.Color;
import java.util.List;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.petriNet.Transition;
import graph.jung.graphDrawing.VertexShapes;

public class ANYTransition extends Transition {
	public ANYTransition(final String label, final String name) {
		super(label, name, Elementdeclerations.transition);
		attributeSetter(getClass().getSimpleName(), this);
		setDefaultShape(VertexShapes.makeCoarse(
				VertexShapes.TRANSITION_TRANSFORM.createTransformedShape(VertexShapes.getRectangle())));
		setDefaultColor(Color.white);
	}

	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("maximalSpeed");
		list.add("delay");
		return list;
	}
}
