package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class CompoundNode extends BiologicalNodeAbstract {
	public CompoundNode(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.compound, parent);
		attributeSetter();
	}
}
