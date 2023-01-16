package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class OrthologGroup extends Complex{

	public OrthologGroup(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.orthologGroup);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
