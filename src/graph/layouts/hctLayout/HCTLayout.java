package graph.layouts.hctLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
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
import graph.layouts.HierarchicalCircleLayout.CircleVertexData;
import graph.layouts.hctLayout.OldHCTLayout.CircleVertexDataHCT;
import graph.layouts.hebLayout.Circle;
import graph.layouts.hebLayout.HEBEdgeShape;
import graph.layouts.hebLayout.HEBLayoutConfig;

public class HCTLayout extends HierarchicalCircleLayout{
	
	List<BiologicalNodeAbstract> groupParents = new ArrayList<BiologicalNodeAbstract>();
	HashMap<BiologicalNodeAbstract, Pair<Double>> groupAngles = new HashMap<BiologicalNodeAbstract, Pair<Double>>();
	HashMap<Integer, List<BiologicalNodeAbstract>> circleNodes = new HashMap<Integer, List<BiologicalNodeAbstract>>();
	HashMap<BiologicalNodeAbstract, HashMap<Integer,List<BiologicalNodeAbstract>>> groups = new HashMap<BiologicalNodeAbstract, HashMap<Integer,List<BiologicalNodeAbstract>>>();
	
	protected HashMap<Integer,List<BiologicalNodeAbstract>> bnaGroups;
	protected List<Integer> groupKeys;
	protected HashMap<BiologicalNodeAbstract,Integer> layer;
	protected Integer maxLayer;
	
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
            if (bnaGroups == null){
                groupNodes();
            }
            
            computeCircleData(d);

            int group_no = 0;
            int vertex_no = 0;
            
            //distance between two groups (added to small distance between two nodes)
            final double nodeDistance = 2*Math.PI / ((getConfig().GROUP_DISTANCE_FACTOR-1)*bnaGroups.size()+graphNodes.size());
            final double groupDistance = (getConfig().GROUP_DISTANCE_FACTOR-1)*nodeDistance;
            for(Integer i : groupKeys){
            	for(BiologicalNodeAbstract v : bnaGroups.get(i)){
            		apply(v);
            		double angle = group_no*groupDistance+vertex_no*nodeDistance;
            		GraphInstance.getMyGraph().moveVertex(v, 
            				Math.cos(angle) * getRadius() * Math.log10(graphNodes.size()) * maxLayer + centerPoint.getX(),
            				Math.sin(angle) * getRadius() * Math.log10(graphNodes.size()) * maxLayer + centerPoint.getY());

        			CircleVertexDataHCT data = (CircleVertexDataHCT) circleVertexDataMap.get(v);
        			data.setCircleNumber(maxLayer);            		
        			data.setVertexAngle(angle);
            		vertex_no++;
            	}
                group_no++;
            }
            
     	   Set<Point2D> childNodePoints = new HashSet<Point2D>();
            for(BiologicalNodeAbstract n : myGraph.getAllVertices()){
            	childNodePoints.clear();
     		   if(layer.get(n)>=1 && !n.isCoarseNode()){
     			   if(n!=rootNode){
     				   BiologicalNodeAbstract parentNode = n.getParentNode();
     				   for(BiologicalNodeAbstract pNode : n.getAllParentNodes()){
     					   if(pNode.getRootNode()==n){
     						   parentNode = pNode;
     						   break;
     					   }
     				   }
     				   for(BiologicalNodeAbstract child : parentNode.getCurrentShownChildrenNodes(myGraph)){
     					   if(child!=n){
     						   childNodePoints.add(myGraph.getVertexLocation(child));
     					   }
     				   }
     			   }
          	   apply(n);
   			CircleVertexDataHCT data = (CircleVertexDataHCT) circleVertexDataMap.get(n);
   			data.setCircleNumber(maxLayer-layer.get(n));            		
   			data.setVertexAngle(Circle.getAngle(getCenterPoint(),HEBEdgeShape.averagePoint(childNodePoints)));
     		   Point2D nodeLocation = Circle.getPointOnCircle(getCenterPoint(), getRadius() * Math.log10(graphNodes.size()) * data.getCircleNumber(), data.getVertexAngle());
           	   myGraph.moveVertex(n,nodeLocation.getX(),nodeLocation.getY());
     		   }
     	   }
     	   
        }
        setEdgeShapes();
    }
	
	@Override
	public void groupNodes() {
		layer = new HashMap<BiologicalNodeAbstract,Integer>();
		maxLayer = 0;
		int l;
		for(BiologicalNodeAbstract node : getGraph().getVertices()){
			l=0;
			BiologicalNodeAbstract p = node;
			BiologicalNodeAbstract rootNode;
			while(p!=null){
				rootNode = p.getRootNode();
				if(layer.containsKey(p)){
					layer.put(p,Math.max(l, layer.get(p)));
				} else {
					layer.put(p, l);
				}
				if(rootNode!=null){
					if(layer.containsKey(rootNode)){
						layer.put(rootNode,Math.max(l, layer.get(rootNode)));
					} else {
						layer.put(rootNode, l);
					}
				}
				maxLayer = Math.max(l, maxLayer);
				p=p.getParentNode();
				l+=1;		
			}
		}
		maxLayer+=1;
		layer.put(this.rootNode,maxLayer);
		
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
			if(layer.get(currentNode)!=0){
				graphNodes.remove(currentNode);
				continue;
			}
			
			referenceParent = currentNode.getLastParentNode();
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
		List<BiologicalNodeAbstract> selection = new ArrayList<BiologicalNodeAbstract>();
		BiologicalNodeAbstract parent;
		switch (getConfig().SELECTION){
		case SINGLE:
			selection.add(n);
			break;
		case PATH:
			selection.add(n);
			int circle = circleVertexDataMap.get(n).getCircleNumber();
			List<BiologicalNodeAbstract> neighbors = new ArrayList<BiologicalNodeAbstract>();
			while(circle>1){
				circle -= 1;
				for(BiologicalNodeAbstract ancestor : groups.get(getGroupParent(n)).get(circle)){
					neighbors.addAll(graph.getNeighbors(ancestor));
					neighbors.retainAll(selection);
					if(neighbors.size()>0 && !selection.contains(ancestor)){
						selection.add(ancestor);
						break;
					}
				}
			}
			parent = n.getParentNode();
			if(parent!=null && parent.getRootNode()==n){
				for(BiologicalNodeAbstract child : parent.getCurrentShownChildrenNodes(myGraph)){
					if(!selection.contains(child)){
						selection.add(child);
					}
				}
			} else {
				for(BiologicalNodeAbstract child : n.getCurrentShownChildrenNodes(myGraph)){
					if(!selection.contains(child)){
						selection.add(child);
					}
				}
			}
			break;
		case SUBPATH:
			parent = n.getParentNode();
			if(parent!=null && parent.getRootNode()==n){
				for(BiologicalNodeAbstract child : parent.getCurrentShownChildrenNodes(myGraph)){
					if(!selection.contains(child)){
						selection.add(child);
					}
				}
			} else {
				for(BiologicalNodeAbstract child : n.getCurrentShownChildrenNodes(myGraph)){
					if(!selection.contains(child)){
						selection.add(child);
					}
				}
			}
			break;
		
		case GROUP:
			parent = n.getLastParentNode();
			for(BiologicalNodeAbstract child : parent.getCurrentShownChildrenNodes(myGraph)){
				if(!selection.contains(child)){
					selection.add(child);
				}
			}
			break;
			
		default:
			selection.add(n);
			break;
		}
		return selection;
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
			Set<BiologicalNodeAbstract> parentNodes = new HashSet<BiologicalNodeAbstract>();
			parentNodes.addAll(first.getAllParentNodes());
			parentNodes.retainAll(second.getAllParentNodes());
			if(!parentNodes.isEmpty() || first==rootNode || second==rootNode){
				Path2D path = new Path2D.Double();
				Point2D lastPoint = new Point2D.Double(0.0,0.0);
				BiologicalNodeAbstract startNode = first;
				BiologicalNodeAbstract endNode = second;
				if(layer.get(first)>layer.get(second)){
					startNode = second;
					endNode = first;
					lastPoint = new Point2D.Double(1.0,0.0);
				}
				BiologicalNodeAbstract parentNode = startNode.getParentNode();
				while(parentNode!=null && parentNode.getRootNode()!=endNode){
					BiologicalNodeAbstract ancestor = parentNode.getRootNode();
					Set<Point2D> childNodePoints = new HashSet<Point2D>();
					for(BiologicalNodeAbstract child : parentNode.getCurrentShownChildrenNodes(myGraph)){
						childNodePoints.add(myGraph.getVertexLocation(child));
		     		}         	
		   			double angle = Circle.getAngle(getCenterPoint(),HEBEdgeShape.averagePoint(childNodePoints));
		     		Point2D location = Circle.getPointOnCircle(getCenterPoint(), getRadius()*(maxLayer-layer.get(parentNode)), angle);
		     		location = HEBEdgeShape.computeControlPoint(location, getCenterPoint(), myGraph.getVertexLocation(first), myGraph.getVertexLocation(second), edge);
		     		Line2D line = new Line2D.Double(lastPoint,location);
		     		path.append(line, true);
		     		lastPoint = location;
		     		parentNode = parentNode.getParentNode();
		     	}
				Line2D line;
				if(endNode==first){
					line = new Line2D.Double(lastPoint,new Point2D.Double(0.0,0.0));

				} else {
					line = new Line2D.Double(lastPoint,new Point2D.Double(1.0,0.0));
				}
		     	path.append(line, true);
		     	return path;
			}
			
			if(getConfig().getShowExternalEdges()){
				edge.setColor(Color.RED);
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
