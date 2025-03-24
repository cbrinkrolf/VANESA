package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class LigandBinding extends BiologicalNodeAbstract {
	public LigandBinding(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.ligandBinding, parent);
		attributeSetter();
	}
}
