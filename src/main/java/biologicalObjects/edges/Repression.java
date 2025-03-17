package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Edge;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Repression extends BiologicalEdgeAbstract {
	public Repression(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to, Elementdeclerations.repressionEdge);
	}
}
