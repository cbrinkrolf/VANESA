package graph.layouts;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import graph.layouts.hebLayout.Circle;

public abstract class HierarchicalCircleLayout extends CircleLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract>{
	
	protected Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph;
	protected MyGraph myGraph;
	protected List<BiologicalNodeAbstract> order = new ArrayList<BiologicalNodeAbstract>();
	protected Set<BiologicalNodeAbstract> graphNodes;
	protected Point2D centerPoint;
	protected Map<BiologicalNodeAbstract, CircleVertexData> circleVertexDataMap =
           new HashMap<BiologicalNodeAbstract, CircleVertexData>();
	protected BiologicalNodeAbstract rootNode;
	protected HashMap<BiologicalNodeAbstract,Integer> circles;
	protected Integer maxCircle;

	public HierarchicalCircleLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		super(g);
		graph = g;
		myGraph = GraphInstance.getMyGraph();
	}
	
	public HierarchicalCircleLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g, List<BiologicalNodeAbstract> order){
		super(g);
		graph = g;
		this.order = order;
	}
		
	@Override
	public abstract void initialize();
	
	public abstract void groupNodes();
	
	public abstract List<BiologicalNodeAbstract> getNodesGroup(BiologicalNodeAbstract n);
	
	public abstract HierarchicalCircleLayoutConfig getConfig();
	
	public abstract void setEdgeShapes();
	
	@Override
    protected CircleVertexData getCircleData(BiologicalNodeAbstract v) {
        return circleVertexDataMap.get(v);
	}
	
	public List<BiologicalNodeAbstract> computeOrder(){
		List<BiologicalNodeAbstract> newOrder = new ArrayList<BiologicalNodeAbstract>();
		for(BiologicalNodeAbstract node : graph.getVertices()){
			newOrder.addAll(node.getLeafNodes());
		}
		HierarchicalOrderComparator comp = new HierarchicalOrderComparator(order);
		newOrder.sort(comp);
		return newOrder;
	}
	
	public void computeCircleData(Dimension d){
		double height = d.getHeight();
        double width = d.getWidth();

        setRadius(HierarchicalCircleLayoutConfig.CIRCLE_SIZE * (height < width ? height : width));
        
        centerPoint = new Point2D.Double(width/2, height/2);
	}
	
	/**
	 * Includes a node to the current order.
	 * @param node Node to be added to the order.
	 * @author tloka
	 */
	public void addToOrder(BiologicalNodeAbstract node){
		if(!node.isCoarseNode()){
			return;
		}
		Set<BiologicalNodeAbstract> rootNodes = new HashSet<BiologicalNodeAbstract>();
		rootNodes.addAll(node.getLeafNodes());
		order.sort(new OrderFusionComparator(order, rootNodes));
	}
	
	/**
	 * Computes the point on the layout circle based on the input angle.
	 * @param angle Input angle
	 * @return Point on the circle.
	 * @author tloka
	 */
	public Point2D getPointOnCircle(double angle, int circle){
		double x = (float)Math.cos(angle) * circle* getRadius() + getCenterPoint().getX();
		double y = (float)Math.sin(angle)* circle * getRadius() + getCenterPoint().getY();
		return new Point2D.Double(x,y);
	}
	
	/**
	 * Moves the nodes on the circle based on the movement of a point.
	 * @param p New position of the basis point (e.g. old mouse position)
	 * @param down Old position of the basis point (e.g. new mouse position)
	 * @param vv Visualization Viewer.
	 * @author tloka
	 */
	public void moveOnCircle(Point p, Point down, VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv){
        
		Layout<BiologicalNodeAbstract,BiologicalEdgeAbstract> layout = vv.getGraphLayout();
        
		//compute movement angle
		Point2D graphPoint = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p);
        Point2D graphDown = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(down);
        Point2D centerPointTransform = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(getCenterPoint());
    	double oldMousePositionAngle = Circle.getAngle(centerPointTransform, graphDown);
    	double newMousePositionAngle = Circle.getAngle(centerPointTransform,graphPoint);
        double movementAngle = newMousePositionAngle-oldMousePositionAngle;
        
        //move all selected nodes on the circle
		Point2D nodePoint;
        double nodeAngle;
        double finalAngle;
        for(BiologicalNodeAbstract v : vv.getPickedVertexState().getPicked()) {
        	
        	if(v == rootNode){
        		continue;
        	}

        	nodePoint = GraphInstance.getMyGraph().getVertexLocation(v);
        	nodeAngle = Circle.getAngle(getCenterPoint(),nodePoint);
        	finalAngle = nodeAngle+(movementAngle);
        	
        	Point2D vp = layout.transform(v);

        	vp.setLocation(getPointOnCircle(finalAngle, circleVertexDataMap.get(v).getCircleNumber()));
        	GraphInstance.getMyGraph().moveVertex(v, vp.getX(), vp.getY());
        	layout.setLocation(v, vp);     	
        }
	}
	
	/**
	 * Saves the current node order + optional relayouting.
	 */
	public void saveCurrentOrder(){
		order.sort(new AngleComparator());
		if(getConfig().getAutoRelayout()){
			groupNodes();
			initialize();
		} else {
			MyGraph g = GraphInstance.getMyGraph();
        	Point2D vp;
			for(BiologicalNodeAbstract n : g.getAllVertices()){
					vp = getPointOnCircle(Circle.getAngle(getCenterPoint(),g.getVertexLocation(n)), circleVertexDataMap.get(n).getCircleNumber());
				g.moveVertex(n, vp.getX(), vp.getY());
			}
		}
	}
	
	/**
	 * Adds circle data of a vertex.
	 * @param v vertex
	 * @return The circle data of the vertex.
	 * @author tloka
	 */
	protected CircleVertexData apply(BiologicalNodeAbstract v){
		CircleVertexData cvData = new CircleVertexData();
		circleVertexDataMap.put(v, cvData);
		return cvData;
	}

	/**
	 * Data structure to save the circle data of a node.
	 * @author tobias
	 */
	public static class CircleVertexData extends CircleLayout.CircleVertexData{
		
        private double angle;
		private int circleNumber = 1;
        
        public int getCircleNumber(){
        	return circleNumber;
        }
        
        public void setCircleNumber(int no){
			circleNumber = no;
		}
        
        public double getVertexAngle(){
        	return getAngle();
        }

        @Override
        protected double getAngle() {
                return angle;
        }

        @Override
        protected void setAngle(double angle) {
                this.angle = angle;
        }
        
        public void setVertexAngle(double angle) {
        	this.setAngle(angle);
        }

        @Override
        public String toString() {
                return "CircleVertexData: angle=" + angle;
        }
	}
	
	/**
	 * Comparator to save the current shown node order with respect to their groups.
	 * @author tobias
	 *
	 */
	protected class AngleComparator implements Comparator<BiologicalNodeAbstract>{
				
		public AngleComparator(){
		}
		
		public int compare (BiologicalNodeAbstract n1, BiologicalNodeAbstract n2){
			MyGraph myGraph = GraphInstance.getMyGraph();
			BiologicalNodeAbstract n1P = n1.getCurrentShownParentNode(myGraph).getParentNode();
			BiologicalNodeAbstract n2P = n2.getCurrentShownParentNode(myGraph).getParentNode();
			Set<BiologicalNodeAbstract> n1G = new HashSet<BiologicalNodeAbstract>();
			Set<BiologicalNodeAbstract> n2G = new HashSet<BiologicalNodeAbstract>();
			
			if(n1P != null){n1G.addAll(n1P.getCurrentShownChildrenNodes(myGraph));}
			else{n1G.add(n1.getCurrentShownParentNode(myGraph));}
			
			if(n2P != null){n2G.addAll(n2P.getCurrentShownChildrenNodes(myGraph));}
			else{n2G.add(n2.getCurrentShownParentNode(myGraph));}
			
			double n1GAngle = 360;
			double n2GAngle = 360;

			for(BiologicalNodeAbstract n : n1G){
				double angle = Circle.getAngle(centerPoint,  myGraph.getVertexLocation(n));
				if(angle<0){
					angle += 2*Math.PI;
				}
				if(angle<n1GAngle){
					n1GAngle = angle;
				}
			}
			for(BiologicalNodeAbstract n : n2G){
				double angle = Circle.getAngle(centerPoint,  myGraph.getVertexLocation(n));
				if(angle<0){
					angle += 2*Math.PI;
				}
				if(angle<n2GAngle){
					n2GAngle = angle;
				}
			}
			if(n1GAngle == n2GAngle){
				n1GAngle = Circle.getAngle(centerPoint, myGraph.getVertexLocation(n1));
				n2GAngle = Circle.getAngle(centerPoint, myGraph.getVertexLocation(n2));
				if(n1GAngle<0)
					n1GAngle += 2*Math.PI;
				if(n2GAngle<0)
					n2GAngle += 2*Math.PI;
			}
			return n1GAngle>n2GAngle ? 1 : -1;
		}
	}

	/**
	 * Comparator for the node order. If no order exists yet, the order represents
	 * the hierarchical structure of the network.
	 * @author tobias
	 */
	protected class HierarchicalOrderComparator implements Comparator<BiologicalNodeAbstract>{
		
	List<BiologicalNodeAbstract> order;
	
	public HierarchicalOrderComparator(List<BiologicalNodeAbstract> order){
		this.order = order;
	}
	
	public int compare(BiologicalNodeAbstract n1, BiologicalNodeAbstract n2){
		
		//If both nodes already exist in the order, keep the order of these nodes.
		if(order.contains(n1) && order.contains(n2)){
			return order.indexOf(n1)-order.indexOf(n2);
		} 
		
		//Else: compute the order of both nodes.
		Set<BiologicalNodeAbstract> n1parents = n1.getAllParentNodes();
		Set<BiologicalNodeAbstract> n2parents = n2.getAllParentNodes();
				
		if(n1parents.isEmpty() && n2parents.isEmpty()){
			return n1.getID()-n2.getID();
		}
		if(n1parents.isEmpty() && !n2parents.isEmpty()){
			return compare(n1,n2.getLastParentNode());
		}
		if(!n1parents.isEmpty() && n2parents.isEmpty()){
			return compare(n1.getLastParentNode(),n2);
		}
		if(n1.getParentNode()==n2.getParentNode()){
			return 0;
		}
		Set<BiologicalNodeAbstract> intersect = new HashSet<BiologicalNodeAbstract>();
		intersect.addAll(n1parents);
		intersect.retainAll(n2parents);
		if(intersect.isEmpty()){
			return n1.getLastParentNode().getID()-n2.getLastParentNode().getID();
		} else {
			BiologicalNodeAbstract comp1 = n1;
			BiologicalNodeAbstract comp2 = n2;
			for(BiologicalNodeAbstract child : n1.getLastCommonParentNode(n2).getInnerNodes()){
				if(n1.getAllParentNodes().contains(child) || n1==child){
					comp1 = child;
				}
				if(n2.getAllParentNodes().contains(child) || n2==child){
					comp2 = child;
				}
			}
			return comp1.getID()-comp2.getID();
		}
	}
}

	/**
	 * Comparator for adding nodes to an already existing order.
	 * @author tobias
	 */
	protected class OrderFusionComparator implements Comparator<BiologicalNodeAbstract>{
		List<BiologicalNodeAbstract> order;
		Set<BiologicalNodeAbstract> fusionNodes;
		int firstNodeIndex = -1;
		HierarchicalOrderComparator hierarchicalOrderComparator;
	
		public OrderFusionComparator(List<BiologicalNodeAbstract> o, Set<BiologicalNodeAbstract> fN){
			this.order = o;
			this.fusionNodes = fN;
			for(BiologicalNodeAbstract node : order){
				if(fusionNodes.contains(node)){
					firstNodeIndex = order.indexOf(node);
					break;
				}
			}
		
			//Add all nodes to order that does not exist yet.
			List<BiologicalNodeAbstract> intersection = new ArrayList<BiologicalNodeAbstract>();
			intersection.addAll(fusionNodes);
			intersection.removeAll(this.order);
			order.addAll(intersection);
		
			hierarchicalOrderComparator = new HierarchicalOrderComparator(new ArrayList<BiologicalNodeAbstract>());
		}
	
		public int compare(BiologicalNodeAbstract n1, BiologicalNodeAbstract n2){
		
			//If both nodes already exist in the order, keep the order of these nodes.
			if(fusionNodes.contains(n1) && fusionNodes.contains(n2)){
				return hierarchicalOrderComparator.compare(n1, n2);
			} 
		
			if(fusionNodes.contains(n1)){
				return firstNodeIndex-order.indexOf(n2);
			}
		
			if(fusionNodes.contains(n2)){
				return order.indexOf(n1)-firstNodeIndex;
			}
			return order.indexOf(n1)-order.indexOf(n2);
		}
	}
	
	public CircleVertexData getCircleVertexData(BiologicalNodeAbstract v){
		return getCircleData(v);
	}
	
	public Point2D getCenterPoint(){
		return centerPoint;
	}
	
	public List<BiologicalNodeAbstract> getOrder(){
		return order;
	}

}
