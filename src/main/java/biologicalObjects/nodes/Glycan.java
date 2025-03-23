package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Glycan extends BiologicalNodeAbstract {
	public Glycan(final String label, final String name) {
		super(label, name, Elementdeclerations.glycan);
		attributeSetter(getClass().getSimpleName(), this);
	}
}
