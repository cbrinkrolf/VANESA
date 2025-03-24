package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Matrix extends BiologicalNodeAbstract {
	public Matrix(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.matrix, parent);
		attributeSetter();
	}
}
