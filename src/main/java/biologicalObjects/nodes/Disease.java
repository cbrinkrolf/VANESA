package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Disease extends BiologicalNodeAbstract {
	public Disease(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.disease, parent);
		attributeSetter();
	}
}
