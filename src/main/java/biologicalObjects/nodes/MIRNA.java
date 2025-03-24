package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class MIRNA extends RNA {
	public MIRNA(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.miRNA, parent);
		attributeSetter();
	}
}
