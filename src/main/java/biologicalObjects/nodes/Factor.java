package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Factor extends BiologicalNodeAbstract {
	public Factor(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.factor, parent);
		attributeSetter();
	}
}
