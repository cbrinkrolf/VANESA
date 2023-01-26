package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class LigandBinding extends BiologicalNodeAbstract {
	public LigandBinding(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.ligandBinding);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
