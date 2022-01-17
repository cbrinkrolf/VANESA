package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class HomodimerFormation extends BiologicalNodeAbstract {

	public HomodimerFormation(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.homodimerFormation);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
