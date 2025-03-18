package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Metabolite extends BiologicalNodeAbstract {
	public Metabolite(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.metabolite, pathway);
		attributeSetter();
	}
}
