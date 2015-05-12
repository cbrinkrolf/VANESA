package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;


public class MRNA extends RNA{

	public MRNA(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.mRNA);
		attributeSetter(this.getClass().getSimpleName(), this);
	}

}
