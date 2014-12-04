package graph.layouts.hctLayout;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
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
                placeNodes(rootNodeSet, null, 0);
            }
            setEdgeShapes();
    }
	
	public void placeNodes(Collection<BiologicalNodeAbstract> nodeList, BiologicalNodeAbstract ancestor, int radiusfactor){
		int vertex_no = 0;
		double angle = 0;
		int circle;
		int listSize = nodeList.size();
		for(BiologicalNodeAbstract v : nodeList){
			apply(v);
			circle = radiusfactor;
			
			if(radiusfactor<=1){
				angle = vertex_no*2*Math.PI/listSize;
			} else {
				double part = listSize<=1 ? 0.5 : (double) vertex_no/(listSize-1);
				double angleVariance = -0.5/2 + part*0.5;
				angle = circleVertexDataMap.get(ancestor).getVertexAngle()+angleVariance;
			}
			GraphInstance.getMyGraph().moveVertex(v, 
				Math.cos(angle) * circle*getRadius() + centerPoint.getX(),
				Math.sin(angle) * circle*getRadius() + centerPoint.getY());
			
			CircleVertexData data = getCircleData(v);
			data.setVertexAngle(angle);
			((CircleVertexDataHCT) data).setCircleNumber(circle);
			vertex_no++;
			placedNodes.add(v);
			Set<BiologicalNodeAbstract> nextDepth = new HashSet<BiologicalNodeAbstract>();
			if(!v.isCoarseNode()){
				if(graph.getNeighbors(v) != null){
					nextDepth.addAll(graph.getNeighbors(v));
					nextDepth.removeAll(placedNodes);
					nextDepth.removeAll(nodeList);
					if(v != rootNode){
						nextDepth.removeIf(p -> !p.getAllParentNodes().contains(v.getParentNode()));
					}
					placeNodes(nextDepth, v, radiusfactor+1);
				}
			} else {
				nextDepth.addAll(v.getInnerNodes());
				placeNodes(nextDepth, v, radiusfactor+1);
			}
		}
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
