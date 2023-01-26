package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class MRNA extends RNA {
	public MRNA(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.mRNA);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
