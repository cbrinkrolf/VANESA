package biologicalObjects.edges;

import biologicalElements.ElementDeclarations;
//import edu.uci.ics.jung.graph.Edge;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Glycosylation extends BiologicalEdgeAbstract {
	public Glycosylation(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to, ElementDeclarations.glycosylationEdge);
	}
}
