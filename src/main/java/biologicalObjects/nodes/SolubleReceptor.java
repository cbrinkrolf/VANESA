package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class SolubleReceptor extends Receptor {
	public SolubleReceptor(final String label, final String name) {
		super(label, name, Elementdeclerations.solubleReceptor);
		attributeSetter(getClass().getSimpleName(), this);
	}
}
