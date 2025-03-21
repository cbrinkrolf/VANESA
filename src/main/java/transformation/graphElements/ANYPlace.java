package transformation.graphElements;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.petriNet.Place;
import graph.rendering.nodes.CoarseShape;
import graph.rendering.nodes.PlaceShape;

public class ANYPlace extends Place {
	public ANYPlace(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.place, true, pathway);
		setDefaultNodeShape(new CoarseShape(new PlaceShape()));
		attributeSetter();
	}
}
