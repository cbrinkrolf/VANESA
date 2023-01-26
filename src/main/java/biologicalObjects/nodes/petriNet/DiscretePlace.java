package biologicalObjects.nodes.petriNet;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class DiscretePlace extends Place {
	public DiscretePlace(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.discretePlace);
		setDefaultShape(new VertexShapes().getEllipse());
		setDiscrete(true);
	}
}
