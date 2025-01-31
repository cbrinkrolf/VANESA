package biologicalObjects.nodes.petriNet;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class DiscretePlace extends Place {
	public DiscretePlace(final String label, final String name) {
		super(label, name, Elementdeclerations.discretePlace, true);
		setDefaultShape(VertexShapes.getEllipse());
	}
}
