package graph.layouts;

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
	protected List<BiologicalNodeAbstract> order = new ArrayList<BiologicalNodeAbstract>();
	protected List<List<BiologicalNodeAbstract>> bnaGroups;
	protected Set<BiologicalNodeAbstract> graphNodes;
	protected Point2D centerPoint;
	protected Map<BiologicalNodeAbstract, CircleVertexData> circleVertexDataMap =
           new HashMap<BiologicalNodeAbstract, CircleVertexData>();

	public HierarchicalCircleLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		super(g);
		graph = g;
	}
	
	public HierarchicalCircleLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g, List<BiologicalNodeAbstract> order){
		super(g);
		graph = g;
		this.order = order;
	}
	
	/**
	 * Must be implemented by extending classes to link to the corresponding config-class.
	 * @return the corresponding config-class
	 * @author tloka
	 */
	public abstract HierarchicalCircleLayoutConfig getConfig();
	
	@Override
	public abstract void initialize();
	
	/**
	 * Must be implemented by extending classes. Sets the edge shape for the implemented layout.
	 * @author tloka
	 */
	public abstract void setEdgeShapes();
	
	@Override
    protected CircleVertexData getCircleData(BiologicalNodeAbstract v) {
        return circleVertexDataMap.get(v);
	}

	
	/**
	 * Build groups with nodes of the same parent node in the given depth.
	 * @author tloka
	 */
	public void groupNodes(){
		graphNodes = new HashSet<BiologicalNodeAbstract>();
		graphNodes.addAll(graph.getVertices());

		if(graphNodes.size()<2){return;}

		List<BiologicalNodeAbstract> newOrder = new ArrayList<BiologicalNodeAbstract>();
		for(BiologicalNodeAbstract node : graph.getVertices()){
			newOrder.addAll(node.getAllRootNodes());
		}
		HierarchicalOrderComparator comp = new HierarchicalOrderComparator(order);
		newOrder.sort(comp);
		order = newOrder;
		
		bnaGroups = new ArrayList<List<BiologicalNodeAbstract>>();
		List<BiologicalNodeAbstract> newGroup = new ArrayList<BiologicalNodeAbstract>();
		BiologicalNodeAbstract currentNode;
		Set<BiologicalNodeAbstract> addedNodes = new HashSet<BiologicalNodeAbstract>();
		for(BiologicalNodeAbstract node : order){
			currentNode = node.getCurrentShownParentNode(GraphInstance.getMyGraph());
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
			BiologicalNodeAbstract lastCurrentNodeParent = currentNode;
			Set<BiologicalNodeAbstract> currentNodeParents = new HashSet<BiologicalNodeAbstract>();
			BiologicalNodeAbstract lastGroupNodeParent = groupNode;
			Set<BiologicalNodeAbstract> groupNodeParents = new HashSet<BiologicalNodeAbstract>();
			for(int i=0; i<getConfig().GROUP_DEPTH; i++){
				if(lastGroupNodeParent.getParentNode()!=null){
					lastGroupNodeParent = lastGroupNodeParent.getParentNode();
					groupNodeParents.add(lastGroupNodeParent);
				}
				if(lastCurrentNodeParent.getParentNode()!=null){
					lastCurrentNodeParent = lastCurrentNodeParent.getParentNode();
					currentNodeParents.add(lastCurrentNodeParent);
				}
			}
			
			currentNodeParents.retainAll(groupNodeParents);

			if(!currentNodeParents.isEmpty()){
				if(!newGroup.contains(currentNode)){
					newGroup.add(currentNode);
					addedNodes.add(currentNode);
				}
			} else {
				newGroup = new ArrayList<BiologicalNodeAbstract>();
				newGroup.add(currentNode);
				addedNodes.add(currentNode);
				bnaGroups.add(newGroup);
			}
		}
	}
	
	/**
	 * Sets the position of the vertex labels.
	 * @author tloka
	 */
	public void setLabelPositions(){
//		VisualizationViewer vv = GraphInstance.getMyGraph().getVisualizationViewer();
//		CirclePositioner cp = new CirclePositioner();
//		vv.getRenderer().setVertexLabelRenderer(cp);
	}
	

	
	/**
	 * Gives back the coordinates of the circle's center point.
	 * @return the center point of the circle
	 * @author tloka
	 */
	public Point2D getCenterPoint(){
		return centerPoint;
	}
	
	/**
	 * Gives back the current order of the nodes.
	 * @return List of all leaf-nodes in the current order.
	 * @author tloka
	 */
	public List<BiologicalNodeAbstract> getOrder(){
		return order;
	}
	
	/**
	 * Includes a node to the current order.
	 * @param node Node to be added to the order.
	 * @author tloka
	 */
	public void fuseInOrder(BiologicalNodeAbstract node){
		if(!node.isCoarseNode()){
			return;
		}
		Set<BiologicalNodeAbstract> rootNodes = new HashSet<BiologicalNodeAbstract>();
		rootNodes.addAll(node.getAllRootNodes());
		order.sort(new OrderFusionComparator(order, rootNodes));
	}
	
	/**
	 * Get access to the circle data of a node.
	 * @param v The vertex.
	 * @return The circle data of the vertex.
	 * @author tloka
	 */
	public CircleVertexData getCircleVertexData(BiologicalNodeAbstract v){
		return getCircleData(v);
	}
	
	public List<List<BiologicalNodeAbstract>> getBnaGroups(){
		return bnaGroups;
	}
	
	/**
	 * Computes the point on the layout circle based on the input angle.
	 * @param angle Input angle
	 * @return Point on the circle.
	 * @author tloka
	 */
	public Point2D getPointOnCircle(double angle){
		double x = (float)Math.cos(angle) * getRadius() + getCenterPoint().getX();
		double y = (float)Math.sin(angle)*getRadius() + getCenterPoint().getY();
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

        	nodePoint = GraphInstance.getMyGraph().getVertexLocation(v);
        	nodeAngle = Circle.getAngle(getCenterPoint(),nodePoint);
        	finalAngle = nodeAngle+(movementAngle);
        	
        	Point2D vp = layout.transform(v);

        	vp.setLocation(getPointOnCircle(finalAngle));
        	GraphInstance.getMyGraph().moveVertex(v, vp.getX(), vp.getY());
        	layout.setLocation(v, vp);     	
        }
	}
	
	/**
	 * Saves the current node order + relayouting.
	 */
	public void saveCurrentOrder(){
		order.sort(new AngleComparator());
		groupNodes();
		initialize();
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

	/**
	 * Data structure to save the circle data of a node.
	 * @author tobias
	 */
	public static class CircleVertexData extends CircleLayout.CircleVertexData{
		
        private double angle;
        
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
			if(n1GAngle == n2GAngle)
				return 0;
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


	/**
	 * Computes the Position of the vertex labels.
	 * @author tobias
	 */
//	public class CirclePositioner extends BasicVertexLabelRenderer<BiologicalNodeAbstract, BiologicalEdgeAbstract> {
//
//		public CirclePositioner(){
//			super();
//		}
//		
//		@Override
//		protected Point getAnchorPoint(Rectangle2D vertexBounds, Dimension labelSize, Position position){
//			if(!(GraphInstance.getMyGraph().getLayout() instanceof HierarchicalCircleLayout)){
//				return super.getAnchorPoint(vertexBounds, labelSize, position);
//			}
//			Point2D vertexCenter = new Point2D.Double(vertexBounds.getCenterX(),vertexBounds.getCenterY());
//			
//			//TOBI: Find correct transformation to the coordinate system.
//			Point2D circleCenterPoint = GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).inverseTransform(((HierarchicalCircleLayout) GraphInstance.getMyGraph().getLayout()).getCenterPoint());
//			double x = vertexBounds.getCenterX()-Math.abs(vertexBounds.getCenterX()-circleCenterPoint.getX())/circleCenterPoint.getX()*vertexBounds.getWidth()/2-Math.abs(vertexBounds.getCenterX()-circleCenterPoint.getX())/circleCenterPoint.getX()*labelSize.getWidth();
//			if(circleCenterPoint.getX()<vertexCenter.getX()){
//				x= vertexBounds.getCenterX()+Math.abs(vertexBounds.getCenterX()-circleCenterPoint.getX())/circleCenterPoint.getX()*vertexBounds.getWidth()/2;
//
//			}
//			double y = vertexBounds.getCenterY() - labelSize.getHeight()/2 - Math.abs(vertexBounds.getCenterY()-circleCenterPoint.getY())/circleCenterPoint.getY()*vertexBounds.getHeight();
//			if(circleCenterPoint.getY()<vertexCenter.getY()){
//				y = vertexBounds.getCenterY() + labelSize.getHeight()/2 + Math.abs(vertexBounds.getCenterY()-circleCenterPoint.getY())/circleCenterPoint.getY()*vertexBounds.getHeight();
//			}
//			return new Point((int) x, (int) y);
//			
//		}	
//	}
}
