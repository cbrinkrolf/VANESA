package transformation.graphElements;

import java.awt.Color;
import java.util.List;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.petriNet.Transition;
import graph.rendering.nodes.CoarseShape;
import graph.rendering.nodes.TransitionShape;

public class ANYTransition extends Transition {
	public ANYTransition(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.transition, pathway);
		attributeSetter();
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
