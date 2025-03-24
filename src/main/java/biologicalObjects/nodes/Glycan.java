package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Glycan extends BiologicalNodeAbstract {
	public Glycan(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.glycan, parent);
		attributeSetter();
	}
}
