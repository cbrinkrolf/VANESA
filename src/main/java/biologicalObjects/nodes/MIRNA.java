package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class MIRNA extends RNA {
	public MIRNA(final String label, final String name) {
		super(label, name, Elementdeclerations.miRNA);
		attributeSetter();
	}
}
