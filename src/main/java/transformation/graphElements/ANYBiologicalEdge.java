package transformation.graphElements;

import biologicalElements.Elementdeclerations;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ANYBiologicalEdge extends BiologicalEdgeAbstract {
	public ANYBiologicalEdge(final String label, final String name, final BiologicalNodeAbstract from,
			final BiologicalNodeAbstract to) {
		super(label, name, from, to, Elementdeclerations.anyBEA);
	}
}
