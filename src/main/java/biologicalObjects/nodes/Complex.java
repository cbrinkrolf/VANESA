package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Complex extends BiologicalNodeAbstract {
	public Complex(final String label, final String name) {
		super(label, name, Elementdeclerations.complex);
		attributeSetter();
	}

	protected Complex(final String label, final String name, final String biologicalElement) {
		super(label, name, biologicalElement);
	}
}
