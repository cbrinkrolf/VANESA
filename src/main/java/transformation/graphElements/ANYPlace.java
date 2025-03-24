package transformation.graphElements;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.petriNet.Place;
import graph.jung.graphDrawing.VertexShapes;

public class ANYPlace extends Place {
	public ANYPlace(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.place, parent, true);
		setDefaultShape(VertexShapes.makeCoarse(VertexShapes.getEllipse()));
		attributeSetter();
	}
}
