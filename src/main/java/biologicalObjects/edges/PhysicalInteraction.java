package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class PhysicalInteraction extends BiologicalEdgeAbstract {

	public PhysicalInteraction(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to);
		this.setBiologicalElement(Elementdeclerations.physicalInteraction);
	}
}
