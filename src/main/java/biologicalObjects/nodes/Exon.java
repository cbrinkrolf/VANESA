package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Exon extends BiologicalNodeAbstract {
	public Exon(final String label, final String name) {
		super(label, name, Elementdeclerations.exon);
		attributeSetter();
	}
}
