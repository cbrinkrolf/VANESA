package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class LigandBinding extends BiologicalNodeAbstract {
	public LigandBinding(final String label, final String name) {
		super(label, name, Elementdeclerations.ligandBinding);
		attributeSetter(getClass().getSimpleName(), this);
	}
}
