package graph.layouts.hctLayout;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import graph.layouts.HierarchicalCircleLayout;

public class HCTLayout extends HierarchicalCircleLayout{
	
	List<BiologicalNodeAbstract> groupParents = new ArrayList<BiologicalNodeAbstract>();
	HashMap<BiologicalNodeAbstract, Pair<Double>> groupAngles = new HashMap<BiologicalNodeAbstract, Pair<Double>>();
	HashMap<Integer, List<BiologicalNodeAbstract>> circleNodes = new HashMap<Integer, List<BiologicalNodeAbstract>>();
	HashMap<BiologicalNodeAbstract, HashMap<Integer,List<BiologicalNodeAbstract>>> groups = new HashMap<BiologicalNodeAbstract, HashMap<Integer,List<BiologicalNodeAbstract>>>();
	
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

            if (d != null)
            {
                order = computeOrder();
                
                computeCircleData(d);
                
                myGraph.moveVertex(rootNode, getPointOnCircle(0, 0).getX(), getPointOnCircle(0,0).getY());
                groupNodes();
                drawNodes();
            }
            setEdgeShapes();
    }
	
	public void drawNodes(){
		for(BiologicalNodeAbstract key : groups.keySet()){
			for(int k : groups.get(key).keySet()){
				double position = 0;
				int groupSize = groups.get(key).get(k).size();
				double space = groupAngles.get(key).getSecond()/(groupSize);
				double startAngle = groupAngles.get(key).getFirst();
				for(BiologicalNodeAbstract n : groups.get(key).get(k)){
					position += space/2;
					Point2D p = getPointOnCircle(startAngle+position, k);
	                myGraph.moveVertex(n, p.getX(), p.getY());
	                circleVertexDataMap.get(n).setVertexAngle(startAngle+position);
					position += space/2;
				}
			}
		}
	}
	
	
	@Override
	public void groupNodes() {
		for(BiologicalNodeAbstract n : order){
			if(getGroupParent(n)!=rootNode && !groupParents.contains(getGroupParent(n)))
				groupParents.add(getGroupParent(n));
		}
		for(BiologicalNodeAbstract n : groupParents){
			double startAngle = ((double) groupParents.indexOf(n)/groupParents.size())*2.0*Math.PI;
			double angleRange = (double) 2.0*Math.PI/(groupParents.size()+1);
			Pair<Double> angles = new Pair<Double>(startAngle, angleRange);
			groupAngles.put(n, angles);
		}
		for(BiologicalNodeAbstract n : order){
			BiologicalNodeAbstract currentNode = n.getCurrentShownParentNode(myGraph);
			int circle = computeCircle(currentNode);
			apply(currentNode);
			CircleVertexDataHCT data = (CircleVertexDataHCT) circleVertexDataMap.get(currentNode);
			data.setCircleNumber(circle);
			if(currentNode!=rootNode){
				groups.putIfAbsent(getGroupParent(currentNode), new HashMap<Integer,List<BiologicalNodeAbstract>>());
				groups.get(getGroupParent(currentNode)).putIfAbsent(circle, new ArrayList<BiologicalNodeAbstract>());
				if(!groups.get(getGroupParent(currentNode)).get(circle).contains(currentNode))
					groups.get(getGroupParent(currentNode)).get(circle).add(currentNode);
			}
		}
		for(BiologicalNodeAbstract key : groups.keySet()){
			System.out.println("GroupParent: " + key.getLabel());
			System.out.println("Angle: " + groupAngles.get(key).getFirst());
			for(int k : groups.get(key).keySet()){
				System.out.println("Circle: " + k);
				for(BiologicalNodeAbstract n : groups.get(key).get(k)){
					System.out.println(n.getLabel());
				}
			}
		}
	}
	
	public BiologicalNodeAbstract getGroupParent(BiologicalNodeAbstract n){
		if(n == rootNode){
			return n;
		}
		if(n.getLastParentNode()==null){
			return n;
		}
		return n.getLastParentNode();
	}
	
	public int computeCircle(BiologicalNodeAbstract n){
		if(n == rootNode){
			return 0;
		} else if (n.getParentNode() != null && n.getParentNode().getRootNode()==n){
			return n.getAllParentNodes().size();
		} 
		return n.getAllParentNodes().size()+1;
	}
	
	@Override
	public List<BiologicalNodeAbstract> getNodesGroup(BiologicalNodeAbstract n) {
		// TODO Auto-generated method stub
		return null;
	}

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
