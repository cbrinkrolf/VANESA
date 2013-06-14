package biologicalObjects.edges;

public class ReactionPairEdge {

	private String reactionPair = "";
	private String reactionPairName = "";
	private String reactionPairType = "";


	public Object[][] getRPairDetails() {

		Object[][] values = { { "Name", getName() },
				{ "Reaction_ID", getReactionPairID() },
				{ "Reaction Type", getType() },

		};
		return values;
	}
	
	
	
	public void setType(String reactionPair) {
		reactionPairType = reactionPair;
	}
	
	public String getType() {
		return reactionPairType;
	}

	public void setReactionPairID(String reactionPairID) {
		reactionPair = reactionPairID;
	}

	public String getReactionPairID() {
		return reactionPair;
	}
	
	public void setName(String reactionPair) {
		reactionPairName = reactionPair;
	}

	public String getName() {
		return reactionPairName;
	}
	

}
