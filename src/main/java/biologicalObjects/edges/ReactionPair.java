package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Edge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReactionPair extends BiologicalEdgeAbstract {

	private ReactionPairEdge reactionPairEdge = new ReactionPairEdge();
	private boolean hasReactionPairEdge = false;

	public ReactionPair(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to);
		setBiologicalElement(Elementdeclerations.reactionPairEdge);
	}
}
