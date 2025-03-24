package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Factor extends BiologicalNodeAbstract {
	public Factor(final String label, final String name) {
		super(label, name, Elementdeclerations.factor);
		attributeSetter();
	}
}
