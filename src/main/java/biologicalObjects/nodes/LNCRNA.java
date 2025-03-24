package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class LNCRNA extends RNA {
	public LNCRNA(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.lncRNA, parent);
		attributeSetter();
	}
}
