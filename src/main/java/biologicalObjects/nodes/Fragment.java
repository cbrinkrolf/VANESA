package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Fragment extends BiologicalNodeAbstract {
	public Fragment(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.fragment, pathway);
		attributeSetter();
	}
}