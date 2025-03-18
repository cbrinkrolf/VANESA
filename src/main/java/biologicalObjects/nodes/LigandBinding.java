package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class LigandBinding extends BiologicalNodeAbstract {
	public LigandBinding(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.ligandBinding, pathway);
		attributeSetter();
	}
}
