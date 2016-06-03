package graph.layouts.hebLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
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
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.GradientEdgePaintTransformer;
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
                computeCircleNumbers();
                
                computeCircleData(d);

                int group_no = 0;
                int vertex_no = 0;
                
                //larger circle for a larger number of nodes on the outter circle.
                setRadius(getRadius()*Math.log10(graphNodes.size()));
                
                //distance between two ndoes of the same group
                final double nodeDistance = HEBLayoutConfig.nodeDistance(bnaGroups.size(), graphNodes.size());
                
                //distance between two groups (added to small distance between two nodes)
                final double groupDistance = HEBLayoutConfig.groupDistance(nodeDistance);
                
                // Move nodes on their circle position
                for(Integer i : groupKeys){
                	for(BiologicalNodeAbstract v : bnaGroups.get(i)){
                		double angle = group_no*groupDistance+vertex_no*nodeDistance;
                		GraphInstance.getMyGraph().moveVertex(v, 
                				Math.cos(angle) * getRadius() + centerPoint.getX(),
                				Math.sin(angle) * getRadius() + centerPoint.getY());

                		apply(v);
                		CircleVertexData data = getCircleData(v);
                		data.setVertexAngle(angle);
                		// All nodes on the outter circle
                		data.setCircleNumber(1);
                		vertex_no++;
                	}
                    group_no++;
                }
            }
            // compute the edge shapes
            setEdgeShapes();
    }
	
	/**
	 * Computes for all graph nodes and their parents the circle they are part of.
	 * @author tloka
	 */
	public void computeCircleNumbers(){
		circles = new HashMap<BiologicalNodeAbstract,Integer>();
		maxCircle = 0;
		int c;
		for(BiologicalNodeAbstract node : getGraph().getVertices()){
			c=0;
			BiologicalNodeAbstract p = node;
			while(p!=null){
				if(circles.containsKey(p)){
					circles.put(p,Math.max(c, circles.get(p)));
				} else {
					circles.put(p, c);
				}
				maxCircle = Math.max(c,maxCircle);
				p=p.getParentNode();
				c+=1;		
			}
		}
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
		BiologicalNodeAbstract referenceParent;

		for(BiologicalNodeAbstract node : order){
			currentNode = node.getCurrentShownParentNode(myGraph);
			if(addedNodes.contains(currentNode)){
				continue;
			}
			
			referenceParent = HEBLayoutConfig.GROUP_DEPTH==HEBLayoutConfig.FINEST_LEVEL ? currentNode.getParentNode() : currentNode.getLastParentNode();
			if(referenceParent==null){
				groupKeys.add(currentNode.getID());
				bnaGroups.put(currentNode.getID(), new ArrayList<BiologicalNodeAbstract>());
				bnaGroups.get(currentNode.getID()).add(currentNode);
				addedNodes.add(currentNode);
				continue;
			}
			
			if(!groupKeys.contains(referenceParent.getID())){
				groupKeys.add(referenceParent.getID());
				bnaGroups.put(referenceParent.getID(),new ArrayList<BiologicalNodeAbstract>());
			}
			bnaGroups.get(referenceParent.getID()).add(currentNode);
			addedNodes.add(currentNode);
		}
	}
	
	@Override
	public void setEdgeShapes(){
		Transformer<Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract>, Shape>
			est = new HEBEdgeShape.HEBCurve<BiologicalNodeAbstract, BiologicalEdgeAbstract>(getCenterPoint(),circles);
		GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().setEdgeShapeTransformer(est);
		
		HEBEdgePaintTransformer ptrans =  new HEBEdgePaintTransformer(
				HEBLayoutConfig.EDGE_OUTCOLOR, HEBLayoutConfig.EDGE_INCOLOR, 
				GraphInstance.getMyGraph().getVisualizationViewer());
		GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().setEdgeDrawPaintTransformer(ptrans);
		
		GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().setEdgeArrowTransformer(new ShowEdgeArrowsTransformer<BiologicalNodeAbstract, BiologicalEdgeAbstract>());
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
	
	/**
	 * Creates a color gradient for directed edges in HEBLayout.
	 * @author tobias
	 *
	 */
	class HEBEdgePaintTransformer extends GradientEdgePaintTransformer<BiologicalNodeAbstract,BiologicalEdgeAbstract>{
		public HEBEdgePaintTransformer(Color c1, Color c2, VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv) {
			super(c1,c2,vv);
		}
		
		@Override
		public Paint transform(BiologicalEdgeAbstract e){
			if(e.isDirected()){
				return super.transform(e);
			} else {
				Color oldc2 = c2;
				c2 = c1;
				Paint p = super.transform(e);
				c2 = oldc2;
				return p;
			}
			
		}
	}
}
