package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Drug extends BiologicalNodeAbstract {
	public Drug(final String label, final String name) {
		super(label, name, Elementdeclerations.drug);
		attributeSetter();
	}
}
