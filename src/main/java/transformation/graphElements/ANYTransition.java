package transformation.graphElements;

import java.awt.Color;
import java.util.List;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.petriNet.Transition;
import graph.jung.graphDrawing.VertexShapes;
import graph.rendering.shapes.CoarseShape;
import graph.rendering.shapes.TransitionShape;

public class ANYTransition extends Transition {
	public ANYTransition(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.transition, pathway);
		attributeSetter();
		setDefaultShape(VertexShapes.makeCoarse(
				VertexShapes.TRANSITION_TRANSFORM.createTransformedShape(VertexShapes.getRectangle())));
		setDefaultNodeShape(new CoarseShape(new TransitionShape()));
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
