package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Metabolite extends BiologicalNodeAbstract {
	public Metabolite(final String label, final String name) {
		super(label, name, Elementdeclerations.metabolite);
		attributeSetter();
	}
}
