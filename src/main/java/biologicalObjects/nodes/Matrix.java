package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Matrix extends BiologicalNodeAbstract {
	public Matrix(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.matrix, pathway);
		attributeSetter();
	}
}
