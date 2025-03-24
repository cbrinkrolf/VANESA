package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class MembraneChannel extends Protein {
	public MembraneChannel(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.membraneChannel, parent);
		attributeSetter();
	}
}
