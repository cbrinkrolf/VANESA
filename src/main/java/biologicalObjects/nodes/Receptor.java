package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Receptor extends Protein {
	public Receptor(final String label, final String name) {
		super(label, name, Elementdeclerations.receptor);
		attributeSetter();
	}

	protected Receptor(final String label, final String name, final String biologicalElement) {
		super(label, name, biologicalElement);
	}
}
