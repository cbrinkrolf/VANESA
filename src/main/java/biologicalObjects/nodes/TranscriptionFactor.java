package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class TranscriptionFactor extends Protein {

	public TranscriptionFactor(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.transcriptionFactor);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
