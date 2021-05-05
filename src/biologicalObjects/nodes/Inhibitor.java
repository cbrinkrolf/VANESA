package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Inhibitor extends BiologicalNodeAbstract {
	public Inhibitor(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.inhibitor);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
