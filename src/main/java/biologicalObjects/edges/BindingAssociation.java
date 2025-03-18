package biologicalObjects.edges;

import biologicalElements.ElementDeclarations;
//import edu.uci.ics.jung.graph.Edge;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class BindingAssociation extends BiologicalEdgeAbstract {
	public BindingAssociation(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to, ElementDeclarations.bindingEdge);
	}
}
