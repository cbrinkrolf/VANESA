package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class LNCRNA extends RNA {
	public LNCRNA(final String label, final String name) {
		super(label, name, Elementdeclerations.lncRNA);
		attributeSetter();
	}
}
