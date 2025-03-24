package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Fragment extends BiologicalNodeAbstract {
	public Fragment(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.fragment, parent);
		attributeSetter();
	}
}