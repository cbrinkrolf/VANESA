package biologicalObjects.nodes.petriNet;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;
import graph.rendering.shapes.ContinuousPlaceShape;

public class ContinuousPlace extends Place {
	public ContinuousPlace(final String label, final String name) {
		super(label, name, Elementdeclerations.continuousPlace, false);
		setDefaultShape(VertexShapes.getDoubleEllipse());
		setNodeShape(new ContinuousPlaceShape());
	}

	public ContinuousPlace(final String label, final String name, final Pathway pathway) {
		super(label, name, Elementdeclerations.continuousPlace, false, pathway);
		setDefaultShape(VertexShapes.getDoubleEllipse());
		setNodeShape(new ContinuousPlaceShape());
	}
}
