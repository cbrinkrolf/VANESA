package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Inhibitor extends BiologicalNodeAbstract {
	public Inhibitor(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.inhibitor, parent);
		attributeSetter();
	}
}
