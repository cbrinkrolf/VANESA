package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class GeneOntology extends BiologicalNodeAbstract {
	public GeneOntology(final String label, final String name) {
		super(label, name, Elementdeclerations.go);
		attributeSetter();
	}
}
