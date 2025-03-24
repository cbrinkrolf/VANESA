package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class MRNA extends RNA {
	public MRNA(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.mRNA, parent);
		attributeSetter();
	}
}
