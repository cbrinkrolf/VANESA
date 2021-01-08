package biologicalObjects.nodes.petriNet;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class DiscretePlace extends Place {

	

	public DiscretePlace(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.discretePlace);
		shapes = new VertexShapes();
		setDefaultShape(shapes.getEllipse());
		setDiscrete(true);

	}
}
