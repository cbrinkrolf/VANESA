package biologicalObjects.edges;

import biologicalElements.ElementDeclarations;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class PhysicalInteraction extends BiologicalEdgeAbstract {
	public PhysicalInteraction(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to, ElementDeclarations.physicalInteraction);
	}
}
