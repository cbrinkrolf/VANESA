package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class SolubleReceptor extends Receptor {

	public SolubleReceptor(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.solubleReceptor);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
