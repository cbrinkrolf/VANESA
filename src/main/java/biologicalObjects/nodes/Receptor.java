package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Receptor extends Protein {
	public Receptor(final String label, final String name, final Pathway pathway) {
		this(label, name, ElementDeclarations.receptor, pathway);
	}

	protected Receptor(final String label, final String name, final String biologicalElement, final Pathway pathway) {
		super(label, name, biologicalElement, pathway);
	}
}
