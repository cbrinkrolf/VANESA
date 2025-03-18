package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class HomodimerFormation extends BiologicalNodeAbstract {
	public HomodimerFormation(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.homodimerFormation, pathway);
		attributeSetter();
	}
}
