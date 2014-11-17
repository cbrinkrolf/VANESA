package graph.layouts.hebLayout;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;

import org.apache.commons.collections15.Transformer;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import graph.GraphInstance;
import graph.layouts.HierarchicalCircleLayout;

public class HEBLayout extends HierarchicalCircleLayout{
	


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
                		data.setVertexAngle(angle);
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
}
