package graph.layouts.hebLayout;

import java.awt.Dimension;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
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
	
	protected HashMap<Integer,List<BiologicalNodeAbstract>> bnaGroups;
	protected List<Integer> groupKeys;

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
                for(Integer i : groupKeys){
                	for(BiologicalNodeAbstract v : bnaGroups.get(i)){
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
		
		bnaGroups = new HashMap<Integer,List<BiologicalNodeAbstract>>();
		Set<BiologicalNodeAbstract> addedNodes = new HashSet<BiologicalNodeAbstract>();
		groupKeys = new ArrayList<Integer>();
		BiologicalNodeAbstract currentNode;

		for(BiologicalNodeAbstract node : order){
			currentNode = node.getCurrentShownParentNode(myGraph);
			if(addedNodes.contains(currentNode)){
				continue;
			}
			
			if(currentNode.getParentNode()==null){
				groupKeys.add(currentNode.getID());
				bnaGroups.put(currentNode.getID(), new ArrayList<BiologicalNodeAbstract>());
				bnaGroups.get(currentNode.getID()).add(currentNode);
				addedNodes.add(currentNode);
				continue;
			}
			
			if(!groupKeys.contains(currentNode.getParentNode().getID())){
				groupKeys.add(currentNode.getParentNode().getID());
				bnaGroups.put(currentNode.getParentNode().getID(),new ArrayList<BiologicalNodeAbstract>());
			}
			bnaGroups.get(currentNode.getParentNode().getID()).add(currentNode);
			addedNodes.add(currentNode);
		}
	}
	
	public void setEdgeShapes(){
		HashMap<BiologicalNodeAbstract,Integer> layer = new HashMap<BiologicalNodeAbstract,Integer>();
		int l;
		for(BiologicalNodeAbstract node : getGraph().getVertices()){
			l=0;
			BiologicalNodeAbstract p = node;
			while(p!=null){
				if(layer.containsKey(p)){
					layer.put(p,Math.max(l, layer.get(p)));
				} else {
					layer.put(p, l);
				}
				p=p.getParentNode();
				l+=1;		
			}
		}
		Transformer<Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract>, Shape>
			est = new HEBEdgeShape.HEBCurve<BiologicalNodeAbstract, BiologicalEdgeAbstract>(getCenterPoint(),layer);
		GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().setEdgeShapeTransformer(est);
	}
	
	/**
	 * Get the group of a node.
	 * @param node The node.
	 * @return The group of the node.
	 * @author tloka
	 */
	public List<BiologicalNodeAbstract> getNodesGroup(BiologicalNodeAbstract node){
		for(List<BiologicalNodeAbstract> group : bnaGroups.values()){
			if(group.contains(node)){
				return group;
			}
		}
		return new ArrayList<BiologicalNodeAbstract>();
	}
	
	public HashMap<Integer, List<BiologicalNodeAbstract>> getBnaGroups(){
		return bnaGroups;
	}
}
