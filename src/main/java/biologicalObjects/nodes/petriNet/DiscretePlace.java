package biologicalObjects.nodes.petriNet;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;
import graph.rendering.shapes.PlaceShape;

public class DiscretePlace extends Place {
	public DiscretePlace(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.discretePlace, true, pathway);
		setDefaultShape(VertexShapes.getEllipse());
		setDefaultNodeShape(new PlaceShape());
	}
}
