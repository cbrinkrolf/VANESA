package biologicalObjects.edges;

import biologicalElements.ElementDeclarations;
//import edu.uci.ics.jung.graph.Edge;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Dephosphorylation extends BiologicalEdgeAbstract {
	public Dephosphorylation(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to, ElementDeclarations.dephosphorylationEdge);
	}
}
