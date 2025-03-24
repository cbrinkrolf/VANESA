package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Receptor extends Protein {
	public Receptor(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.receptor, parent);
		attributeSetter();
	}

	protected Receptor(final String label, final String name, final String biologicalElement, final Pathway parent) {
		super(label, name, biologicalElement, parent);
	}
}
