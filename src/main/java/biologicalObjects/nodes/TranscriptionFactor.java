package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;

public class TranscriptionFactor extends Protein {
	public TranscriptionFactor(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.transcriptionFactor, parent);
		attributeSetter();
	}
}
