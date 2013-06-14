package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
import edu.uci.ics.jung.graph.Edge;

public class PhysicalInteraction extends BiologicalEdgeAbstract{

	public PhysicalInteraction(Edge edge, String label, String name) {
		super(edge, label, name);
		this.setBiologicalElement(Elementdeclerations.physicalInteraction);
		setAbstract(false);
	}

}
