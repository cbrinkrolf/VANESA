package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class GeneOntology extends BiologicalNodeAbstract{
	public GeneOntology(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.go);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
