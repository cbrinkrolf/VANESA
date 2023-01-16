package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Receptor extends Protein {

	public Receptor(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.receptor);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
