package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Collector extends BiologicalNodeAbstract {
	public Collector(final String label, final String name) {
		super(label, name, Elementdeclerations.collector);
		attributeSetter();
	}
}
