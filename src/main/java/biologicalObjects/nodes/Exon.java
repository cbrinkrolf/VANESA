package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Exon extends BiologicalNodeAbstract {
	public Exon(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.exon, pathway);
		attributeSetter();
	}
}
