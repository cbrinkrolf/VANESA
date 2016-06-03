package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;

public class Drug extends BiologicalNodeAbstract{
	public Drug(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.drug);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
