package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Complex extends BiologicalNodeAbstract {
	public Complex(final String label, final String name) {
		super(label, name, Elementdeclerations.complex);
		attributeSetter(getClass().getSimpleName(), this);
	}

	protected Complex(final String label, final String name, final String biologicalElement) {
		super(label, name, biologicalElement);
	}
}
