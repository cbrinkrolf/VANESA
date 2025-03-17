package transformation.graphElements;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.petriNet.Place;
import graph.jung.graphDrawing.VertexShapes;
import graph.rendering.shapes.CoarseShape;
import graph.rendering.shapes.PlaceShape;

public class ANYPlace extends Place {
	public ANYPlace(final String label, final String name) {
		super(label, name, Elementdeclerations.place, true);
		setDefaultShape(VertexShapes.makeCoarse(VertexShapes.getEllipse()));
		setNodeShape(new CoarseShape(new PlaceShape()));
		attributeSetter(this.getClass().getSimpleName(), this);
	}

	public ANYPlace(final String label, final String name, final Pathway pathway) {
		super(label, name, Elementdeclerations.place, true, pathway);
		setDefaultShape(VertexShapes.makeCoarse(VertexShapes.getEllipse()));
		setNodeShape(new CoarseShape(new PlaceShape()));
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
