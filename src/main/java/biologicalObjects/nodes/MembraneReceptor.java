package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class MembraneReceptor extends Receptor {
	public MembraneReceptor(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.membraneReceptor, parent);
		attributeSetter();
	}
}
