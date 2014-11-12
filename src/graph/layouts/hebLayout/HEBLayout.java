package graph.layouts.hebLayout;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.Transformer;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Positioner;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;

public class HEBLayout extends CircleLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract>{
	
	private Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph;
	private List<BiologicalNodeAbstract> order = new ArrayList<BiologicalNodeAbstract>();
	private List<List<BiologicalNodeAbstract>> bnaGroups;
	private Set<BiologicalNodeAbstract> graphNodes;
	private Point2D centerPoint;
	
	protected Map<BiologicalNodeAbstract, CircleVertexData> circleVertexDataMap =
           new HashMap<BiologicalNodeAbstract, CircleVertexData>();



	public HEBLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		super(g);
		graph = g;
	}
	
	public HEBLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g, List<BiologicalNodeAbstract> order){
		super(g);
		graph = g;
		this.order = order;
	}

	/**
	 * Build groups with nodes of the same parent node.
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
		newHEBLayoutComparator comp = new newHEBLayoutComparator(order);
		newOrder.sort(comp);
		order = newOrder;
//		for(BiologicalNodeAbstract node : order){
//			System.out.println(node.getLabel());
//		}
		
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
			for(int i=0; i<HEBLayoutConfig.GROUP_DEPTH; i++){
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

                double height = d.getHeight();
                double width = d.getWidth();

                if (getRadius() <= 0) {
                	setRadius(0.45 * (height < width ? height : width));
                }
                
                centerPoint = new Point2D.Double(width/2, height/2);

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
                		data.setAngle(angle);
                		vertex_no++;
                	}
                    group_no++;
                }
            }
            setEdgeShapes();
    }
	
	public void setEdgeShapes(){
			Transformer<Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract>, Shape>
				est = new HEBEdgeShape.HEBCurve<BiologicalNodeAbstract, BiologicalEdgeAbstract>(getCenterPoint());
			
			GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().setEdgeShapeTransformer(est);
	}
	
	public void setLabelPositions(){
		VisualizationViewer vv = GraphInstance.getMyGraph().getVisualizationViewer();
		CirclePositioner cp = new CirclePositioner();
		vv.getRenderer().setVertexLabelRenderer(cp);
		
	}
	
	public void reset(){
		initialize();
	}
	
	public Point2D getCenterPoint(){
		return centerPoint;
	}
	
	public List<BiologicalNodeAbstract> getOrder(){
		return order;
	}
	
	public void fuseInOrder(BiologicalNodeAbstract node){
		if(!node.isCoarseNode()){
			return;
		}
		Set<BiologicalNodeAbstract> rootNodes = new HashSet<BiologicalNodeAbstract>();
		rootNodes.addAll(node.getAllRootNodes());
		order.sort(new OrderFusionComparator(order, rootNodes));
	}
	
	public CircleVertexData getCircleVertexData(BiologicalNodeAbstract v){
		return getCircleData(v);
	}
	
	@Override
    protected CircleVertexData getCircleData(BiologicalNodeAbstract v) {
        return circleVertexDataMap.get(v);
	}
	
	public List<List<BiologicalNodeAbstract>> getBnaGroups(){
		return bnaGroups;
	}
	
	/**
	 * Switches the nodes of two groups in the layout order.
	 * @param group1 First group
	 * @param group2 Second group
	 * @author tloka
	 */
//	public void switchGroups(List<BiologicalNodeAbstract> group1, List<BiologicalNodeAbstract> group2){
//		List<BiologicalNodeAbstract> rootGroup1 = new ArrayList<BiologicalNodeAbstract>();
//		List<BiologicalNodeAbstract> rootGroup2 = new ArrayList<BiologicalNodeAbstract>();
//		for(BiologicalNodeAbstract n : group1){
//			rootGroup1.addAll(n.getAllRootNodes());
//		}
//		for(BiologicalNodeAbstract n : group2){
//			rootGroup2.addAll(n.getAllRootNodes());
//		}
//		int indexGroup1 = order.indexOf(rootGroup1.get(0));
//		int indexGroup2 = order.indexOf(rootGroup2.get(0));
//		
//		rootGroup1.sort(new newHEBLayoutComparator(order));
//		rootGroup2.sort(new newHEBLayoutComparator(order));
//		if(indexGroup1<indexGroup2){
//			order.removeAll(rootGroup1);
//			order.addAll(indexGroup2-rootGroup1.size(),rootGroup1);
//			order.removeAll(rootGroup2);
//			order.addAll(indexGroup1, rootGroup2);
//		} else {
//			order.removeAll(rootGroup2);
//			order.addAll(indexGroup1-rootGroup2.size(),rootGroup2);
//			order.removeAll(rootGroup1);
//			order.addAll(indexGroup2, rootGroup1);
//		}
//	}
	
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
	
	private CircleVertexData apply(BiologicalNodeAbstract v){
		CircleVertexData cvData = new CircleVertexData();
		circleVertexDataMap.put(v, cvData);
		return cvData;
	}
	
	public List<BiologicalNodeAbstract> getNodesGroup(BiologicalNodeAbstract node){
		for(List<BiologicalNodeAbstract> group : bnaGroups){
			if(group.contains(node)){
				return group;
			}
		}
		return new ArrayList<BiologicalNodeAbstract>();
	}

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

        @Override
        public String toString() {
                return "CircleVertexData: angle=" + angle;
        }
}

protected class newHEBLayoutComparator implements Comparator<BiologicalNodeAbstract>{
		
	List<BiologicalNodeAbstract> order;
	
	public newHEBLayoutComparator(List<BiologicalNodeAbstract> order){
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

protected class OrderFusionComparator implements Comparator<BiologicalNodeAbstract>{
	List<BiologicalNodeAbstract> order;
	Set<BiologicalNodeAbstract> fusionNodes;
	int firstNodeIndex = -1;
	newHEBLayoutComparator HEBLayoutComparator;
	
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
		
		HEBLayoutComparator = new newHEBLayoutComparator(new ArrayList<BiologicalNodeAbstract>());
	}
	
	public int compare(BiologicalNodeAbstract n1, BiologicalNodeAbstract n2){
		
		//If both nodes already exist in the order, keep the order of these nodes.
		if(fusionNodes.contains(n1) && fusionNodes.contains(n2)){
			return HEBLayoutComparator.compare(n1, n2);
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


	public class CirclePositioner extends BasicVertexLabelRenderer<BiologicalNodeAbstract, BiologicalEdgeAbstract> {

		public CirclePositioner(){
			super();
		}
		
		@Override
		protected Point getAnchorPoint(Rectangle2D vertexBounds, Dimension labelSize, Position position){
			if(!(GraphInstance.getMyGraph().getLayout() instanceof HEBLayout)){
				return super.getAnchorPoint(vertexBounds, labelSize, position);
			}
			Point2D vertexCenter = new Point2D.Double(vertexBounds.getCenterX(),vertexBounds.getCenterY());
			
			//TOBI: Find correct transformation to the coordinate system.
			Point2D circleCenterPoint = GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).inverseTransform(((HEBLayout) GraphInstance.getMyGraph().getLayout()).getCenterPoint());
			double x = vertexBounds.getCenterX()-Math.abs(vertexBounds.getCenterX()-circleCenterPoint.getX())/circleCenterPoint.getX()*vertexBounds.getWidth()/2-Math.abs(vertexBounds.getCenterX()-circleCenterPoint.getX())/circleCenterPoint.getX()*labelSize.getWidth();
			if(circleCenterPoint.getX()<vertexCenter.getX()){
				x= vertexBounds.getCenterX()+Math.abs(vertexBounds.getCenterX()-circleCenterPoint.getX())/circleCenterPoint.getX()*vertexBounds.getWidth()/2;

			}
			double y = vertexBounds.getCenterY() - labelSize.getHeight()/2 - Math.abs(vertexBounds.getCenterY()-circleCenterPoint.getY())/circleCenterPoint.getY()*vertexBounds.getHeight();
			if(circleCenterPoint.getY()<vertexCenter.getY()){
				y = vertexBounds.getCenterY() + labelSize.getHeight()/2 + Math.abs(vertexBounds.getCenterY()-circleCenterPoint.getY())/circleCenterPoint.getY()*vertexBounds.getHeight();
			}
			return new Point((int) x, (int) y);
			
		}

	
}

	
}
