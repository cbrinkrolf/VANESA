package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class TranscriptionFactor extends Protein {
	public TranscriptionFactor(final String label, final String name) {
		super(label, name, Elementdeclerations.transcriptionFactor);
		attributeSetter();
	}
}
