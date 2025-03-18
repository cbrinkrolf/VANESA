package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Collector extends BiologicalNodeAbstract {
	public Collector(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.collector, pathway);
		attributeSetter();
	}
}
