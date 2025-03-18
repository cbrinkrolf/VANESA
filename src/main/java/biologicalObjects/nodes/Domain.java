package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Domain extends BiologicalNodeAbstract {
	public Domain(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.domain, pathway);
		attributeSetter();
	}
}
