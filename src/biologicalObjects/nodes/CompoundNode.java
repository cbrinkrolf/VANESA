package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class CompoundNode extends BiologicalNodeAbstract {
	
	public CompoundNode(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.compound);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
