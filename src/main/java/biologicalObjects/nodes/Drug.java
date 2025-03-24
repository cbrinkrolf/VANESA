package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Drug extends BiologicalNodeAbstract {
	public Drug(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.drug, parent);
		attributeSetter();
	}
}
