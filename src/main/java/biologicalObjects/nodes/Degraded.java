package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Degraded extends BiologicalNodeAbstract {
	public Degraded(final String label, final String name) {
		super(label, name, Elementdeclerations.degraded);
		attributeSetter();
	}
}
