package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class MRNA extends RNA {
	public MRNA(final String label, final String name) {
		super(label, name, Elementdeclerations.mRNA);
		attributeSetter(getClass().getSimpleName(), this);
	}
}
