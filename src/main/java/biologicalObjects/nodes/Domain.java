package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Domain extends BiologicalNodeAbstract {
	public Domain(final String label, final String name) {
		super(label, name, Elementdeclerations.domain);
		attributeSetter(getClass().getSimpleName(), this);
	}
}
