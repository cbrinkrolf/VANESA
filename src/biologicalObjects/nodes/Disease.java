package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Disease extends BiologicalNodeAbstract {
	public Disease(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.disease);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
