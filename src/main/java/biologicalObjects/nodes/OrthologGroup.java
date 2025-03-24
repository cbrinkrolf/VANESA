package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class OrthologGroup extends Complex {
	public OrthologGroup(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.orthologGroup, parent);
		attributeSetter();
	}
}
