package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Edge;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ReactionPair extends BiologicalEdgeAbstract {

	private ReactionPairEdge rpEdge = new ReactionPairEdge();
	private boolean hasRPairEdge = false;
	
	public ReactionPair(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to);
		setBiologicalElement(Elementdeclerations.reactionPairEdge);
	}
	
	@Override
	public void setReactionPairEdge(ReactionPairEdge edge){
		rpEdge = edge;
	}

	@Override
	public ReactionPairEdge getReactionPairEdge(){
		return rpEdge;
	}
	
	public void hasRPairEdge(boolean bool){
		hasRPairEdge = bool;
	}
	
	public boolean hasRPairEdge(){
		return hasRPairEdge;
	}
	
}
