package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class GeneOntology extends BiologicalNodeAbstract {
	public GeneOntology(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.go, parent);
		attributeSetter();
	}
}
