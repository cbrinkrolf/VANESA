package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Degraded extends BiologicalNodeAbstract {
	public Degraded(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.degraded, parent);
		attributeSetter();
	}
}
