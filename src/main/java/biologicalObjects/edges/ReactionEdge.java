package biologicalObjects.edges;

import biologicalElements.ElementDeclarations;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ReactionEdge extends BiologicalEdgeAbstract {
	public ReactionEdge(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to, ElementDeclarations.reactionEdge);
		// setAbstract(false);
	}
}
