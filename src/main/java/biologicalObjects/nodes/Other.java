package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Other extends BiologicalNodeAbstract {
	public Other(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.others, parent);
		attributeSetter();
	}
}
