package graph.layouts.hebLayout;

import java.awt.Dimension;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import graph.GraphInstance;
import graph.layouts.HierarchicalCircleLayout;

public class HEBLayout extends HierarchicalCircleLayout{
	
	protected List<List<BiologicalNodeAbstract>> bnaGroups;

	public HEBLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		super(g);
	}
	
	public HEBLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g, List<BiologicalNodeAbstract> order){
		super(g,order);
	}
	
	public HEBLayoutConfig getConfig(){
		return HEBLayoutConfig.getInstance();
	}
	
	@Override
	public void initialize()
    {
            Dimension d = getSize();

            if (d != null)
            {
                if (bnaGroups == null){
                    groupNodes();
                }
                
                computeCircleData(d);

                int group_no = 0;
                int vertex_no = 0;
                
                //distance between two groups (added to small distance between two nodes)
                final double nodeDistance = HEBLayoutConfig.nodeDistance(bnaGroups.size(), graphNodes.size());
                final double groupDistance = (HEBLayoutConfig.GROUP_DISTANCE_FACTOR-1)*nodeDistance;
                for (List<BiologicalNodeAbstract> group : bnaGroups){
                	for(BiologicalNodeAbstract v : group){
                		apply(v);
                		double angle = group_no*groupDistance+vertex_no*nodeDistance;
                		GraphInstance.getMyGraph().moveVertex(v, 
                				Math.cos(angle) * getRadius() + centerPoint.getX(),
                				Math.sin(angle) * getRadius() + centerPoint.getY());

                		CircleVertexData data = getCircleData(v);
                		data.setVertexAngle(angle);
                		vertex_no++;
                	}
                    group_no++;
                }
            }
            setEdgeShapes();
    }
	
	/**
	 * Build groups with nodes of the same parent node in the given depth.
	 * @author tloka
	 */
	public void groupNodes(){
		graphNodes = new HashSet<BiologicalNodeAbstract>();
		graphNodes.addAll(graph.getVertices());

		if(graphNodes.size()<2){return;}

		order = computeOrder();
		
		bnaGroups = new ArrayList<List<BiologicalNodeAbstract>>();
		List<BiologicalNodeAbstract> newGroup = new ArrayList<BiologicalNodeAbstract>();
		Set<BiologicalNodeAbstract> addedNodes = new HashSet<BiologicalNodeAbstract>();
		BiologicalNodeAbstract currentNode;

		for(BiologicalNodeAbstract node : order){
			currentNode = node.getCurrentShownParentNode(myGraph);
			if(addedNodes.contains(currentNode)){
				continue;
			}
			
			if(newGroup.isEmpty()){
				newGroup.add(currentNode);
				addedNodes.add(currentNode);
				bnaGroups.add(newGroup);
				continue;
			}
			
			BiologicalNodeAbstract groupNode = newGroup.iterator().next();
			if(groupNode.getHierarchyDistance(currentNode) < 0 || groupNode.getHierarchyDistance(currentNode)>getConfig().GROUP_DEPTH){
				newGroup = new ArrayList<BiologicalNodeAbstract>();
				newGroup.add(currentNode);
				addedNodes.add(currentNode);
				bnaGroups.add(newGroup);
			} else {
				if(!newGroup.contains(currentNode)){
					newGroup.add(currentNode);
					addedNodes.add(currentNode);
				}
			}
		}
	}
	
	public void setEdgeShapes(){
		Transformer<Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract>, Shape>
			est = new HEBEdgeShape.HEBCurve<BiologicalNodeAbstract, BiologicalEdgeAbstract>(getCenterPoint());
		
		GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().setEdgeShapeTransformer(est);
	}
	
	/**
	 * Get the group of a node.
	 * @param node The node.
	 * @return The group of the node.
	 * @author tloka
	 */
	public List<BiologicalNodeAbstract> getNodesGroup(BiologicalNodeAbstract node){
		for(List<BiologicalNodeAbstract> group : bnaGroups){
			if(group.contains(node)){
				return group;
			}
		}
		return new ArrayList<BiologicalNodeAbstract>();
	}
	
	public List<List<BiologicalNodeAbstract>> getBnaGroups(){
		return bnaGroups;
	}
}
