package biologicalObjects.nodes.petriNet;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import graph.rendering.nodes.PlaceShape;

public class DiscretePlace extends Place {
	public DiscretePlace(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.discretePlace, true, pathway);
		setDefaultNodeShape(new PlaceShape());
	}
}
