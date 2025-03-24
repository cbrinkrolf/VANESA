package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class MembraneReceptor extends Receptor {
	public MembraneReceptor(final String label, final String name) {
		super(label, name, Elementdeclerations.membraneReceptor);
		attributeSetter();
	}
}
