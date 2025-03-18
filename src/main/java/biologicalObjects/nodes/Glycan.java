package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Glycan extends BiologicalNodeAbstract {
	public Glycan(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.glycan, pathway);
		attributeSetter();
	}
}
