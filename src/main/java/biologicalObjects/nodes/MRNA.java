package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class MRNA extends RNA {
	public MRNA(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.mRNA, pathway);
		attributeSetter();
	}
}
