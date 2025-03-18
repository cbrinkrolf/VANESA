package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Other extends BiologicalNodeAbstract {
	public Other(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.others, pathway);
		attributeSetter();
	}
}
