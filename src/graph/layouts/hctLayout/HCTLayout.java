package graph.layouts.hctLayout;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.internal.matchers.IsCollectionContaining;

import test.samples.substance.api.GetDecorationType;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import graph.GraphInstance;
import graph.algorithms.alignment.BiologicalNodeSet;
import graph.jung.classes.MyGraph;
import graph.layouts.HierarchicalCircleLayout;
import graph.layouts.HierarchicalCircleLayout.CircleVertexData;

public class HCTLayout extends HierarchicalCircleLayout{
	
	private Set<BiologicalNodeAbstract> placedNodes = new HashSet<BiologicalNodeAbstract>();
	
	public HCTLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		this(g,null);
	}
	
	public HCTLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g, BiologicalNodeAbstract root){
		super(g);
		if(root!=null){
			rootNode = root;
		} else {
			rootNode = computeRootNode();
		}
	}
	
	public HCTLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g, List<BiologicalNodeAbstract> order, BiologicalNodeAbstract root){
		super(g,order);
		if(root!=null){
			rootNode = root;
		} else {
			rootNode = computeRootNode();
		}
	}
	
	public HCTLayoutConfig getConfig(){
		return HCTLayoutConfig.getInstance();
	}
	
	@Override
	public void initialize()
    {
            Dimension d = getSize();
            setLabelPositions();

            if (d != null)
            {
                if (bnaGroups == null){
                    groupNodes();
                }
                
                graphNodes.remove(rootNode);
                bnaGroups.removeIf(p -> p.contains(rootNode));

                double height = d.getHeight();
                double width = d.getWidth();

                if (getRadius() <= 0) {
                	setRadius(0.25 * (height < width ? height : width));
                }
                
                centerPoint = new Point2D.Double(width/2, height/2);

                HashSet<BiologicalNodeAbstract> rootNodeSet = new HashSet<BiologicalNodeAbstract>();
                rootNodeSet.add(rootNode);
                List<BiologicalNodeAbstract> newOrder = new ArrayList<BiologicalNodeAbstract>();
                for(BiologicalNodeAbstract bna : graph.getVertices()){
                	newOrder.addAll(bna.getAllRootNodes());
                }
                newOrder.sort(new HierarchicalOrderComparator(order));
                order = newOrder;
                placeNodes();
            }
            setEdgeShapes();
    }
	
	public void placeNodes(){
		circleVertexDataMap.clear();
//		BiologicalNodeAbstract currentNode;
		MyGraph myGraph = GraphInstance.getMyGraph();
		HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> ancestor = new HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract>();
		HashMap<BiologicalNodeAbstract, List<BiologicalNodeAbstract>> ancestorChildren = new HashMap<BiologicalNodeAbstract, List<BiologicalNodeAbstract>>();
		HashMap<Integer, List<BiologicalNodeAbstract>> circleNodes = new HashMap<Integer, List<BiologicalNodeAbstract>>();
		int circle = 0;
		for(BiologicalNodeAbstract bna : order){
			HashSet<BiologicalNodeAbstract> hierarchyNodes = new HashSet<BiologicalNodeAbstract>();
			hierarchyNodes.add(bna);
			hierarchyNodes.addAll(bna.getAllParentNodes());
			for(BiologicalNodeAbstract currentNode : hierarchyNodes){

			circle = currentNode.getAllParentNodes().size()+1;
			if(currentNode.getParentNode()!=null && currentNode.getParentNode().getRootNode()==currentNode){
				circle = circle-1;
			}
			if(currentNode == rootNode){
				circle = 0;
			}
			apply(currentNode);
			((CircleVertexDataHCT) circleVertexDataMap.get(currentNode)).setCircleNumber(circle);
			circleNodes.putIfAbsent(circle, new ArrayList<BiologicalNodeAbstract>());
			if(!circleNodes.get(circle).contains(currentNode) && (myGraph.getAllVertices().contains(currentNode) || !myGraph.getAllVertices().contains(currentNode.getRootNode()))){
					circleNodes.get(circle).add(currentNode);
			}
			
			if(currentNode.getParentNode() == null){
				continue;
			} else if(currentNode.getParentNode().getRootNode() == null){
				ancestor.put(currentNode, currentNode.getParentNode());
				ancestorChildren.putIfAbsent(currentNode.getParentNode(), new ArrayList<BiologicalNodeAbstract>());
				ancestorChildren.get(currentNode.getParentNode()).add(currentNode);
			} else {
				ancestor.put(currentNode, currentNode.getParentNode().getRootNode());
				ancestorChildren.putIfAbsent(currentNode.getParentNode().getRootNode(), new ArrayList<BiologicalNodeAbstract>());
				ancestorChildren.get(currentNode.getParentNode().getRootNode()).add(currentNode);
			}
			}
		}
		
		//geht davon aus, dass jeder Kreis besetzt ist.
		for(int i=0; i<circleNodes.keySet().size(); i++){
			List<BiologicalNodeAbstract> nodes = circleNodes.get(i);
			if(nodes == null){
				continue;
			}
			for(int j=0; j<nodes.size(); j++){
				System.out.println(nodes.get(j).getLabel() + ": " + i);
				if(i<=1){
					circleVertexDataMap.get(nodes.get(j)).setVertexAngle((double)((double) j/nodes.size())*2*Math.PI);
				} else {
					double seperator = 1.0-(double)getConfig().GROUP_DISTANCE_FACTOR/100;
					double angleArea = (double) 2*Math.PI/circleNodes.get(i-1).size();
					double nodeIndex = ancestorChildren.get(ancestor.get(nodes.get(j))).indexOf(nodes.get(j));
					double nodeSize = ancestorChildren.get(ancestor.get(nodes.get(j))).size();
					double movement = nodeIndex/nodeSize*angleArea*seperator;
					movement = movement - 0.5*angleArea*seperator;
					
					circleVertexDataMap.get(nodes.get(j)).setVertexAngle(circleVertexDataMap.get(ancestor.get(nodes.get(j))).getVertexAngle() + movement);
				}
			}
		}
		
		for(BiologicalNodeAbstract bna : myGraph.getAllVertices()){
			double angle = circleVertexDataMap.get(bna).getVertexAngle();
			int circ = circleVertexDataMap.get(bna).getCircleNumber();
			myGraph.moveVertex(bna, 
			Math.cos(angle) * circ*getRadius() + centerPoint.getX(),
			Math.sin(angle) * circ*getRadius() + centerPoint.getY());
		}
	}
	
//	@Override
//	public void saveCurrentOrder() {
//		
//	};

	@Override
	public void setEdgeShapes() {
		GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().setEdgeShapeTransformer(new HctEdgeShape());
	}
	
	public BiologicalNodeAbstract computeRootNode(){
		int maxNeighborNodes = 0;
		BiologicalNodeAbstract rootNode = null;
		for(BiologicalNodeAbstract n : graph.getVertices()){
			if(n.isCoarseNode()){
				continue;
			}
			if(n.getParentNode()!=GraphInstance.getPathwayStatic() && n.getParentNode()!=null){
				continue;
			}
			if(graph.getNeighborCount(n)>maxNeighborNodes){
				maxNeighborNodes = graph.getNeighborCount(n);
				rootNode = n;
			}
		}
		return rootNode;
	}		
	
	protected class HctEdgeShape extends EdgeShape.Line<BiologicalNodeAbstract, BiologicalEdgeAbstract>{
		public HctEdgeShape(){
			super();
		}
		
		@Override
		public Shape transform(Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract> context){
			BiologicalEdgeAbstract edge = context.element;
			Pair<BiologicalNodeAbstract> endpoints = graph.getEndpoints(edge);
			BiologicalNodeAbstract first = endpoints.getFirst();
			BiologicalNodeAbstract second = endpoints.getSecond();
			if(first==rootNode || second==rootNode){
				return new Line2D.Float(0.0f, 0.0f, 1.0f, 0.0f);
			}
			Set<BiologicalNodeAbstract> parentNodes = new HashSet<BiologicalNodeAbstract>();
			parentNodes.addAll(first.getAllParentNodes());
			parentNodes.retainAll(second.getAllParentNodes());
			if(first.getParentNode()!=null && !parentNodes.isEmpty()){
				return new Line2D.Float(0.0f, 0.0f, 1.0f, 0.0f);
			}
			if(getConfig().getShowExternalEdges()){
				return new Line2D.Float(0.0f, 0.0f, 1.0f, 0.0f);
			}
			return new Line2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
		}
	}

	@Override
	protected CircleVertexData apply(BiologicalNodeAbstract v){
		CircleVertexData cvData = new CircleVertexDataHCT();
		circleVertexDataMap.put(v, cvData);
		return cvData;
	}
	
	public static class CircleVertexDataHCT extends CircleVertexData{
		
		private int circleNumber = 1;
		
		public CircleVertexDataHCT(){
			super();
		}
		
		public CircleVertexDataHCT(int no){
			super();
			circleNumber = no;
		}
		
		public void setCircleNumber(int no){
			circleNumber = no;
		}
		
		@Override
		public int getCircleNumber(){
			return circleNumber;
		}
		
	}
	
}
