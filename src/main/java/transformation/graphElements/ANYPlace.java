package transformation.graphElements;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.petriNet.Place;
import graph.jung.graphDrawing.VertexShapes;

public class ANYPlace extends Place {
	public ANYPlace(final String label, final String name) {
		super(label, name, Elementdeclerations.place, true);
		setDefaultShape(VertexShapes.makeCoarse(VertexShapes.getEllipse()));
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
