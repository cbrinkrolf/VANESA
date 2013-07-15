package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;


public class Receptor extends Protein{

	public Receptor(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.receptor);
	}

	
}
