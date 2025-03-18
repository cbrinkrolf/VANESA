package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class CompoundNode extends BiologicalNodeAbstract {
	public CompoundNode(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.compound, pathway);
		attributeSetter();
	}
}
