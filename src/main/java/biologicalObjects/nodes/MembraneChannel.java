package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class MembraneChannel extends Protein {
	public MembraneChannel(final String label, final String name) {
		super(label, name, Elementdeclerations.membraneChannel);
		attributeSetter();
	}
}
