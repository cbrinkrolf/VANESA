package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class HomodimerFormation extends BiologicalNodeAbstract {
	public HomodimerFormation(final String label, final String name) {
		super(label, name, Elementdeclerations.homodimerFormation);
		attributeSetter(getClass().getSimpleName(), this);
	}
}
