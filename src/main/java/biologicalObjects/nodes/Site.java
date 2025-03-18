package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Site extends BiologicalNodeAbstract {
	public Site(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.site, pathway);
		attributeSetter();
	}
}
