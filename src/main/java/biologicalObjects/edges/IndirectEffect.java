package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Edge;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class IndirectEffect extends BiologicalEdgeAbstract {
	public IndirectEffect(final String label, final String name, final BiologicalNodeAbstract from,
			final BiologicalNodeAbstract to) {
		super(label, name, from, to, Elementdeclerations.indirectEffectEdge);
	}
}
