package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Exon extends BiologicalNodeAbstract {
	public Exon(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.exon, parent);
		attributeSetter();
	}
}
