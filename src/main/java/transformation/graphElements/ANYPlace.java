package transformation.graphElements;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.petriNet.Place;
import graph.jung.graphDrawing.VertexShapes;
import graph.rendering.shapes.CoarseShape;
import graph.rendering.shapes.PlaceShape;

public class ANYPlace extends Place {
	public ANYPlace(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.place, true, pathway);
		setDefaultShape(VertexShapes.makeCoarse(VertexShapes.getEllipse()));
		setDefaultNodeShape(new CoarseShape(new PlaceShape()));
		attributeSetter();
	}
}
