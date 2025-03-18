package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class OrthologGroup extends Complex {
	public OrthologGroup(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.orthologGroup, pathway);
		attributeSetter();
	}
}
