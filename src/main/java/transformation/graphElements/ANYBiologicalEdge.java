package transformation.graphElements;

import biologicalElements.ElementDeclarations;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ANYBiologicalEdge extends BiologicalEdgeAbstract {

	public ANYBiologicalEdge(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to, ElementDeclarations.anyBEA);
	}
}
