package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Protein extends BiologicalNodeAbstract {
	private String aaSequence = "";

	public Protein(final String label, final String name) {
		super(label, name, Elementdeclerations.protein);
		attributeSetter(getClass().getSimpleName(), this);
	}

	protected Protein(final String label, final String name, final String biologicalElement) {
		super(label, name, biologicalElement);
	}

	public String getAaSequence() {
		return aaSequence;
	}

	public void setAaSequence(String aaSequence) {
		this.aaSequence = aaSequence;
	}
}
