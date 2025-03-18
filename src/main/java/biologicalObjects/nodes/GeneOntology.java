package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class GeneOntology extends BiologicalNodeAbstract {
	public GeneOntology(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.go, pathway);
		attributeSetter();
	}
}
