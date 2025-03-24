package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Metabolite extends BiologicalNodeAbstract {
	public Metabolite(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.metabolite, parent);
		attributeSetter();
	}
}
