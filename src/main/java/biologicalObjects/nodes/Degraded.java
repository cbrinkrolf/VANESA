package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Degraded extends BiologicalNodeAbstract {
	public Degraded(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.degraded, pathway);
		attributeSetter();
	}
}
