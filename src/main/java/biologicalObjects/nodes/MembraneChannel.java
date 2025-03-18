package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class MembraneChannel extends Protein {
	public MembraneChannel(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.membraneChannel, pathway);
	}
}
