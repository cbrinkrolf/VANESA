package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Other extends BiologicalNodeAbstract {
	public Other(final String label, final String name) {
		super(label, name, Elementdeclerations.others);
		attributeSetter(getClass().getSimpleName(), this);
	}
}
