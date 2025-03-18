package biologicalObjects.nodes;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class TranscriptionFactor extends Protein {
	public TranscriptionFactor(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.transcriptionFactor, pathway);
	}
}
