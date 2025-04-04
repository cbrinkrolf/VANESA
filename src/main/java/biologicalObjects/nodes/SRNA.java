package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class SRNA extends RNA {
	public SRNA(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.sRNA, parent);
		attributeSetter();
	}
}
