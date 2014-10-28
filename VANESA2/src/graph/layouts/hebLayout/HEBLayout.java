package graph.layouts.hebLayout;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
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
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
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

		if(order.isEmpty()){
			for(BiologicalNodeAbstract graphNode : graph.getVertices()){
					order.addAll(graphNode.getAllRootNodes());
			}
			order.sort(new Comparator<BiologicalNodeAbstract>(){
				
				private MyGraph myGraph = GraphInstance.getMyGraph();
				public int compare(BiologicalNodeAbstract n1, BiologicalNodeAbstract n2){
					Integer n1ParentID = n1.getCurrentShownParentNode(myGraph).getID();
					Integer n2ParentID = n1.getCurrentShownParentNode(myGraph).getID();
					if(n1.getCurrentShownParentNode(myGraph).getParentNode()!=null){
						n1ParentID = n1.getCurrentShownParentNode(myGraph).getParentNode().getID();
					}
					if(n2.getCurrentShownParentNode(myGraph).getParentNode()!=null){
						n2ParentID = n2.getCurrentShownParentNode(myGraph).getParentNode().getID();
					}
					
					return n1ParentID.compareTo(n2ParentID);
				}
			});
		}
		
		bnaGroups = new ArrayList<List<BiologicalNodeAbstract>>();
		List<BiologicalNodeAbstract> newGroup = new ArrayList<BiologicalNodeAbstract>();
		BiologicalNodeAbstract currentNode;
		for(BiologicalNodeAbstract node : order){
			currentNode = node.getCurrentShownParentNode(GraphInstance.getMyGraph());
			if(newGroup.contains(currentNode)){
				continue;
			}
			if(newGroup.isEmpty()){
				newGroup.add(currentNode);
				bnaGroups.add(newGroup);
				continue;
			}
			if(currentNode.getParentNode() != null && (currentNode.getParentNode() == newGroup.iterator().next().getParentNode())){
				newGroup.add(currentNode);
			} else {
				newGroup = new ArrayList<BiologicalNodeAbstract>();
				newGroup.add(currentNode);
				bnaGroups.add(newGroup);
			}
		}
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
                				Math.cos(angle) * getRadius() + width / 2,
                				Math.sin(angle) * getRadius() + height / 2);

                		CircleVertexData data = getCircleData(v);
                		data.setAngle(angle);
                		vertex_no++;
                	}
                    group_no++;
                }
            }
            setEdgeShapes();
            setLabelPositions();
    }
	
	public void setEdgeShapes(){
//			Shape shape = new EdgeShape.Line<BiologicalNodeAbstract, BiologicalEdgeAbstract>().transform(Context.getInstance(graph, edge));
//			edge.setShape(shape);
			
			Transformer est = new HEBEdgeShape.HEBCurve<BiologicalNodeAbstract, BiologicalEdgeAbstract>(getCenterPoint());
			
			GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().setEdgeShapeTransformer(est);
	}
	
	public void setLabelPositions(){
		//VisualizationViewer vv = GraphInstance.getMyGraph().getVisualizationViewer();
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
	
	@Override
    protected CircleVertexData getCircleData(BiologicalNodeAbstract v) {
        return circleVertexDataMap.get(v);
	}
	
	private CircleVertexData apply(BiologicalNodeAbstract v){
		CircleVertexData cvData = new CircleVertexData();
		circleVertexDataMap.put(v, cvData);
		return cvData;
	}


protected static class CircleVertexData extends CircleLayout.CircleVertexData{
		
        private double angle;

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
	
}
