package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;


public class LigandBinding extends BiologicalNodeAbstract{

	public LigandBinding(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.ligandBinding);
	}

}
