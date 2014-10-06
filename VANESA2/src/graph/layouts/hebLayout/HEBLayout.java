package graph.layouts.hebLayout;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.Graph;

public class HEBLayout extends CircleLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract>{
	
	private Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph;
	private Set<Set<BiologicalNodeAbstract>> bnaGroups;

	public HEBLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		super(g);
		graph = g;
		// TODO Auto-generated constructor stub
	}

	/**
	 * Build groups with nodes of the same parent node.
	 */
	public void groupNodes(){
		//Nodes in the graph.
		Set<BiologicalNodeAbstract> graphNodes = new HashSet<BiologicalNodeAbstract>();
		graphNodes.addAll(graph.getVertices());

		if(graphNodes.size()<2){return;}

		bnaGroups = new HashSet<Set<BiologicalNodeAbstract>>();
		Set<BiologicalNodeAbstract> group = new HashSet<BiologicalNodeAbstract>();
		
		for(BiologicalNodeAbstract bna : graphNodes){
			if(group.isEmpty()){
				group.add(bna);
				bnaGroups.add(group);
				continue;
			}
			Set<BiologicalNodeAbstract> bnaGroup = new HashSet<BiologicalNodeAbstract>();
			for(Set<BiologicalNodeAbstract> oldGroup : bnaGroups){
				if(bna.getParentNode() == oldGroup.iterator().next().getParentNode() && bna.getParentNode()!=null){
					bnaGroup = oldGroup;
					break;
				}
			}
			if(bnaGroup.isEmpty()){
				bnaGroup.add(bna);
				bnaGroups.add(bnaGroup);
			} else {
				bnaGroup.add(bna);
			}
		}

		Vector<BiologicalNodeAbstract> vertexList = new Vector<BiologicalNodeAbstract>();
		for(Set<BiologicalNodeAbstract> subSet : bnaGroups){
			for(BiologicalNodeAbstract bna : subSet){
				vertexList.add(bna);
			}
		}
		setVertexOrder(vertexList);
	}
	
}
