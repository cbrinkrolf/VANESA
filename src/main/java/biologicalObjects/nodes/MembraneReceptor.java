package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class MembraneReceptor extends Receptor {
	public MembraneReceptor(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.membraneReceptor);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
