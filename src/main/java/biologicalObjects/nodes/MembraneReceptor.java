package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class MembraneReceptor extends Receptor {
	public MembraneReceptor(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.membraneReceptor, pathway);
	}
}
