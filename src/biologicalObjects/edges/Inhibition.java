package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Edge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Inhibition extends BiologicalEdgeAbstract {

	private boolean absoluteInhibition = true;

	public Inhibition(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to);
		setBiologicalElement(Elementdeclerations.inhibitionEdge);
	}
}
