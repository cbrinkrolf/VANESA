package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Drug extends BiologicalNodeAbstract {
	public Drug(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.drug, pathway);
		attributeSetter();
	}
}
