package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Site extends BiologicalNodeAbstract {
	public Site(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.site, parent);
		attributeSetter();
	}
}
