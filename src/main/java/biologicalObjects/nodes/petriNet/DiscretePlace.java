package biologicalObjects.nodes.petriNet;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;

public class DiscretePlace extends Place {
	public DiscretePlace(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.discretePlace, parent, true);
		setDefaultShape(VertexShapes.getEllipse());
	}
}
