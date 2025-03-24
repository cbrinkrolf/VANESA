package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class HomodimerFormation extends BiologicalNodeAbstract {
	public HomodimerFormation(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.homodimerFormation, parent);
		attributeSetter();
	}
}
