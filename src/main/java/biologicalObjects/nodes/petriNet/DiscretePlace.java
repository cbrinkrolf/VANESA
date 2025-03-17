package biologicalObjects.nodes.petriNet;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;
import graph.rendering.shapes.PlaceShape;

public class DiscretePlace extends Place {
	public DiscretePlace(final String label, final String name) {
		super(label, name, Elementdeclerations.discretePlace, true);
		setDefaultShape(VertexShapes.getEllipse());
		setNodeShape(new PlaceShape());
	}

	public DiscretePlace(final String label, final String name, final Pathway pathway) {
		super(label, name, Elementdeclerations.discretePlace, true, pathway);
		setDefaultShape(VertexShapes.getEllipse());
		setNodeShape(new PlaceShape());
	}
}
