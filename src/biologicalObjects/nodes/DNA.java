package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class DNA extends BiologicalNodeAbstract {

	private String ntSequence = "";

	public DNA(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.dna);
		attributeSetter(this.getClass().getSimpleName(), this);
	}

	public String getNtSequence() {
		return ntSequence;
	}

	public void setNtSequence(String ntSequence) {
		this.ntSequence = ntSequence;
	}
}
