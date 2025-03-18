package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Factor extends BiologicalNodeAbstract {
	public Factor(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.factor, pathway);
		attributeSetter();
	}
}
