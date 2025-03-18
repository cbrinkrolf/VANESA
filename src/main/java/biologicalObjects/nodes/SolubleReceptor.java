package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class SolubleReceptor extends Receptor {
	public SolubleReceptor(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.solubleReceptor, pathway);
	}
}
