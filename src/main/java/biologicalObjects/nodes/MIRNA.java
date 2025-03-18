package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class MIRNA extends RNA {
	public MIRNA(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.miRNA, pathway);
		attributeSetter();
	}
}
