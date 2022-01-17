package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class MembraneChannel extends Protein {

	public MembraneChannel(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.membraneChannel);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
