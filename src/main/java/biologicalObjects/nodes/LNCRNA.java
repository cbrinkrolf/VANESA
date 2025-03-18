package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class LNCRNA extends RNA {
	public LNCRNA(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.lncRNA, pathway);
		attributeSetter();
	}
}
