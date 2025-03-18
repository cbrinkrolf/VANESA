package biologicalObjects.edges;

import biologicalElements.ElementDeclarations;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class HiddenCompound extends BiologicalEdgeAbstract {
	public HiddenCompound(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to, ElementDeclarations.hiddenCompoundEdge);
	}
}
