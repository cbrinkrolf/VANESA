package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Exon extends BiologicalNodeAbstract{
	public Exon(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.exon);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
