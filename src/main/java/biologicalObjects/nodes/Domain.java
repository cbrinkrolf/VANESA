package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Domain extends BiologicalNodeAbstract {
	public Domain(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.domain, parent);
		attributeSetter();
	}
}
