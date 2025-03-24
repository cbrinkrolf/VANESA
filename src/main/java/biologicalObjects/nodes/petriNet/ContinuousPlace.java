package biologicalObjects.nodes.petriNet;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;

public class ContinuousPlace extends Place {
	public ContinuousPlace(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.continuousPlace, parent, false);
		setDefaultShape(VertexShapes.getDoubleEllipse());
	}
}
