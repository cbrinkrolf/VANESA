package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Disease extends BiologicalNodeAbstract {
	public Disease(final String label, final String name) {
		super(label, name, Elementdeclerations.disease);
		attributeSetter();
	}
}
