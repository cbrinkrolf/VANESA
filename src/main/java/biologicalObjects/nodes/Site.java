package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Site extends BiologicalNodeAbstract {
	public Site(final String label, final String name) {
		super(label, name, Elementdeclerations.site);
		attributeSetter(getClass().getSimpleName(), this);
	}
}
