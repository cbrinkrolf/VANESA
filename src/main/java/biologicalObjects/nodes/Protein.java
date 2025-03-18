package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Protein extends BiologicalNodeAbstract {
	private String aaSequence = "";

	public Protein(final String label, final String name, final Pathway pathway) {
		this(label, name, ElementDeclarations.protein, pathway);
	}

	protected Protein(String label, String name, final String biologicalElement, final Pathway pathway) {
		super(label, name, biologicalElement, pathway);
		attributeSetter();
	}

	public String getAaSequence() {
		return aaSequence;
	}

	public void setAaSequence(String aaSequence) {
		this.aaSequence = aaSequence;
	}
}
