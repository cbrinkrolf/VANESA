package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Disease extends BiologicalNodeAbstract {
	public Disease(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.disease, pathway);
		attributeSetter();
	}
}
