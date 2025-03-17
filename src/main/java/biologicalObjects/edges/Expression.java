package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Edge;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Expression extends BiologicalEdgeAbstract {
	public Expression(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to, Elementdeclerations.expressionEdge);
	}
}
