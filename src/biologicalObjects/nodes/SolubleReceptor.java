package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;


public class SolubleReceptor extends Receptor {

	public SolubleReceptor(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.solubleReceptor);
	}

	
}
