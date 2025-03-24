package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class Protein extends BiologicalNodeAbstract {
	private String aaSequence = "";

	public Protein(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.protein, parent);
		attributeSetter();
	}

	protected Protein(final String label, final String name, final String biologicalElement, final Pathway parent) {
		super(label, name, biologicalElement, parent);
	}

	public String getAaSequence() {
		return aaSequence;
	}

	public void setAaSequence(String aaSequence) {
		this.aaSequence = aaSequence;
	}
}
