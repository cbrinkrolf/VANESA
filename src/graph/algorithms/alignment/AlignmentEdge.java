package graph.algorithms.alignment;

import java.awt.Color;

import biologicalElements.GraphElementAbstract;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
//import edu.uci.ics.jung.graph.Edge;

public class AlignmentEdge extends BiologicalEdgeAbstract{
	
	//private Edge edge;
	
	public AlignmentEdge(BiologicalNodeAbstract from, BiologicalNodeAbstract to){
		//this.edge = e;
		super("", "", from, to);
		this.setAbstract(false);
		this.setColor(Color.RED);
		this.setIsEdge(true);
	}
	
	//public Edge getEdge(){
	//	return edge;
	//}
}
