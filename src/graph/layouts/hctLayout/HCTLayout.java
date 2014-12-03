package graph.layouts.hctLayout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import test.samples.substance.api.GetDecorationType;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import graph.GraphInstance;
import graph.layouts.HierarchicalCircleLayout;

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
                placeNodes(rootNodeSet, null, 0);
            }
    }
	
	public void placeNodes(Collection<BiologicalNodeAbstract> nodeList, BiologicalNodeAbstract ancestor, int radiusfactor){
		int vertex_no = 0;
		double angle = 0;
		for(BiologicalNodeAbstract v : nodeList){
			apply(v);
			if(radiusfactor<=1){
				angle = vertex_no*2*Math.PI/nodeList.size();
			} else {
				angle = circleVertexDataMap.get(ancestor).getVertexAngle()+vertex_no*5;
			}
			GraphInstance.getMyGraph().moveVertex(v, 
				Math.cos(angle) * radiusfactor*getRadius() + centerPoint.getX(),
				Math.sin(angle) * radiusfactor*getRadius() + centerPoint.getY());

			CircleVertexData data = getCircleData(v);
			data.setVertexAngle(angle);
			vertex_no++;
			placedNodes.add(v);
			Set<BiologicalNodeAbstract> nextCircleNeighbors = new HashSet<BiologicalNodeAbstract>();
			nextCircleNeighbors.addAll(graph.getNeighbors(v));
			nextCircleNeighbors.removeAll(placedNodes);
			placeNodes(nextCircleNeighbors, v, radiusfactor+1);
		}
	}

	@Override
	public void setEdgeShapes() {
		//do nothing
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
	
	public void getCircle(BiologicalNodeAbstract node){
		
	}
	
}
