package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Degraded extends BiologicalNodeAbstract {

	public Degraded(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.degraded);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
