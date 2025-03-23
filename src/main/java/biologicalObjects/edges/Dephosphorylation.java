package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Edge;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Dephosphorylation extends BiologicalEdgeAbstract {
	public Dephosphorylation(final String label, final String name, final BiologicalNodeAbstract from,
			final BiologicalNodeAbstract to) {
		super(label, name, from, to, Elementdeclerations.dephosphorylationEdge);
	}
}
