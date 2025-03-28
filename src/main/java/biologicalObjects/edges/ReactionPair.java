package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ReactionPair extends BiologicalEdgeAbstract {
	private ReactionPairEdge reactionPairEdge = new ReactionPairEdge();
	private boolean hasReactionPairEdge = false;

	public ReactionPair(final String label, final String name, final BiologicalNodeAbstract from,
			final BiologicalNodeAbstract to) {
		super(label, name, from, to, Elementdeclerations.reactionPairEdge);
	}

	public ReactionPairEdge getReactionPairEdge() {
		return reactionPairEdge;
	}

	public void setReactionPairEdge(ReactionPairEdge reactionPairEdge) {
		this.reactionPairEdge = reactionPairEdge;
	}

	public boolean hasReactionPairEdge() {
		return hasReactionPairEdge;
	}

	public void setHasReactionPairEdge(boolean hasReactionPairEdge) {
		this.hasReactionPairEdge = hasReactionPairEdge;
	}
}
