package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Domain extends BiologicalNodeAbstract {
	public Domain(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.domain);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
