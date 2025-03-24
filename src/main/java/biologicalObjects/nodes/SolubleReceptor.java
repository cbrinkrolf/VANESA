package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class SolubleReceptor extends Receptor {
	public SolubleReceptor(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.solubleReceptor, parent);
		attributeSetter();
	}
}
