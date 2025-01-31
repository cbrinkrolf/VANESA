package biologicalObjects.nodes.petriNet;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class ContinuousPlace extends Place {
	public ContinuousPlace(final String label, final String name) {
		super(label, name, Elementdeclerations.continuousPlace, false);
		setDefaultShape(VertexShapes.getDoubleEllipse());
	}
}
