package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Inhibitor extends BiologicalNodeAbstract {
	public Inhibitor(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.inhibitor, pathway);
		attributeSetter();
	}
}
