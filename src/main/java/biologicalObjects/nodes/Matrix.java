package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Matrix extends BiologicalNodeAbstract {
	public Matrix(final String label, final String name) {
		super(label, name, Elementdeclerations.matrix);
		attributeSetter();
	}
}
