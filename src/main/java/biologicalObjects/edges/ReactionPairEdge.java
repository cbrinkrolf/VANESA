package biologicalObjects.edges;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReactionPairEdge {

	private String reactionPairID = "";
	private String name = "";
	private String type = "";

	public Object[][] getRPairDetails() {

		Object[][] values = { { "Name", getName() }, { "Reaction_ID", getReactionPairID() },
				{ "Reaction Type", getType() },

		};
		return values;
	}
}
