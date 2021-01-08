package biologicalObjects.nodes.petriNet;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class ContinuousPlace extends Place {

	public ContinuousPlace(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.continuousPlace);

		shapes = new VertexShapes();

		setDefaultShape(shapes.getDoubleEllipse());
		setDiscrete(false);

	}
}
