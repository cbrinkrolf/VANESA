package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Complex extends BiologicalNodeAbstract {
	public Complex(final String label, final String name, final Pathway pathway) {
		this(label, name, ElementDeclarations.complex, pathway);
	}

	protected Complex(final String label, final String name, final String biologicalElement, final Pathway pathway) {
		super(label, name, biologicalElement, pathway);
		attributeSetter();
	}
}
