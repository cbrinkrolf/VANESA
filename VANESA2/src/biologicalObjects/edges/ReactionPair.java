package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Edge;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ReactionPair extends BiologicalEdgeAbstract {

	ReactionPairEdge rpEdge = new ReactionPairEdge();
	boolean hasRPairEdge = false;
	
	public ReactionPair(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to);
		setBiologicalElement(Elementdeclerations.reactionPairEdge);
		setAbstract(false);
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
