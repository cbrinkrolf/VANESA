package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class CompoundNode extends BiologicalNodeAbstract {
	public CompoundNode(final String label, final String name) {
		super(label, name, Elementdeclerations.compound);
		attributeSetter(getClass().getSimpleName(), this);
	}
}
