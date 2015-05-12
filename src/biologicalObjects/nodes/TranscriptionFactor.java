package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;


public class TranscriptionFactor extends Protein {

	public TranscriptionFactor(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.transcriptionFactor);
		attributeSetter(this.getClass().getSimpleName(), this);
	}


}
