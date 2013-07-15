package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.BiologicalNodeAbstract;
//import edu.uci.ics.jung.graph.Edge;

public class Compound extends BiologicalEdgeAbstract{

	public Compound(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to);
		setBiologicalElement(Elementdeclerations.compoundEdge);
	}

}
