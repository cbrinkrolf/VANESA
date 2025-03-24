package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Inhibitor extends BiologicalNodeAbstract {
	public Inhibitor(final String label, final String name) {
		super(label, name, Elementdeclerations.inhibitor);
		attributeSetter();
	}
}
