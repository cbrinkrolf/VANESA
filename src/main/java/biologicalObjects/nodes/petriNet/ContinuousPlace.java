package biologicalObjects.nodes.petriNet;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;
import graph.rendering.shapes.ContinuousPlaceShape;

public class ContinuousPlace extends Place {
	public ContinuousPlace(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.continuousPlace, false, pathway);
		setDefaultShape(VertexShapes.getDoubleEllipse());
		setDefaultNodeShape(new ContinuousPlaceShape());
	}
}
