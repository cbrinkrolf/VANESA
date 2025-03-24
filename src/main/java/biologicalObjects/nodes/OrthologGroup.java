package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class OrthologGroup extends Complex {
	public OrthologGroup(final String label, final String name) {
		super(label, name, Elementdeclerations.orthologGroup);
		attributeSetter();
	}
}
