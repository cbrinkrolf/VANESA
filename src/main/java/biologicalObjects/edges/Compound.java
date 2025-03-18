package biologicalObjects.edges;

import biologicalElements.ElementDeclarations;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Compound extends BiologicalEdgeAbstract {
	public Compound(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to, ElementDeclarations.compoundEdge);
	}
}
