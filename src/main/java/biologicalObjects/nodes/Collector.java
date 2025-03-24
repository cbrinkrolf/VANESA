package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Collector extends BiologicalNodeAbstract {
	public Collector(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.collector, parent);
		attributeSetter();
	}
}
