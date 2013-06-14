package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;


public class MembraneChannel extends Protein{

	public MembraneChannel(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.membraneChannel);
		setAbstract(false);
	}


}
