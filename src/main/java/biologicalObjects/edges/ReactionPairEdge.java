package biologicalObjects.edges;

public class ReactionPairEdge {
    private String reactionPairID = "";
    private String name = "";
    private String type = "";

    public String getReactionPairID() {
        return reactionPairID;
    }

    public void setReactionPairID(String reactionPairID) {
        this.reactionPairID = reactionPairID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object[][] getRPairDetails() {
        return new Object[][]{
                {"Name", getName()}, {"Reaction_ID", getReactionPairID()}, {"Reaction Type", getType()}
        };
    }
}
