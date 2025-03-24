package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Complex extends BiologicalNodeAbstract {
	public Complex(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.complex, parent);
		attributeSetter();
	}

	protected Complex(final String label, final String name, final String biologicalElement, final Pathway parent) {
		super(label, name, biologicalElement, parent);
	}
}
