package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Fragment extends BiologicalNodeAbstract {
	public Fragment(final String label, final String name) {
		super(label, name, Elementdeclerations.fragment);
		attributeSetter();
	}
}