package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class DNA extends BiologicalNodeAbstract {
	private String ntSequence = "";
	private Double logFC = 0.0;

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

	public Double getLogFC() {
		return logFC;
	}

	public void setLogFC(Double logFC) {
		this.logFC = logFC;
	}
}
