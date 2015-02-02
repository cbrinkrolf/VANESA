package graph.layouts.hebLayout;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;


/**
 * All edge shapes must be defined so that their endpoints are at
 * (0,0) and (1,0). They will be scaled, rotated and translated into
 * position by the PluggableRenderer.
 * 
 * @author tloka
 * @param <Edge>
 */

public class HEBEdgeShape<V,E> extends EdgeShape<V,E>{
	 /**
     * An edge shape that renders as a CubicCurve between vertex
     * endpoints.  The two control points are at
     * (1/3*length, 2*controlY) and (2/3*length, controlY)
     * giving a 'spiral' effect.
     */
    public static class HEBCurve<V,E>
         extends AbstractEdgeShapeTransformer<V,E> implements IndexedRendering<V,E> {
                 
        protected EdgeIndexFunction<V,E> parallelEdgeIndexFunction;
        
        private static Point2D centerPoint;
        
        private HashMap<BiologicalNodeAbstract,Integer> layer;
        private Integer maxLayer;
        
        public Point2D getCenterPoint(){
        	return centerPoint;
        }
        
        public HEBCurve(Point2D cP, HashMap<BiologicalNodeAbstract,Integer> l){
        	centerPoint = cP;
        	layer = l;
        	maxLayer = -1;
        	for (Integer d : l.values()) {
        	     if (d > maxLayer) maxLayer = d;
        	}
        }

		@SuppressWarnings("unchecked")
        public void setEdgeIndexFunction(EdgeIndexFunction<V,E> parallelEdgeIndexFunction) {
            this.parallelEdgeIndexFunction = parallelEdgeIndexFunction;
            loop.setEdgeIndexFunction(parallelEdgeIndexFunction);
       }
 
        /**
         * @return the parallelEdgeIndexFunction
         */
        public EdgeIndexFunction<V, E> getEdgeIndexFunction() {
            return parallelEdgeIndexFunction;
        }
 
        /**
         * Get the shape for this edge, returning either the
         * shared instance or, in the case of self-loop edges, the
         * Loop shared instance.
         */
        @SuppressWarnings("unchecked")
        public Shape transform(Context<Graph<V,E>,E> context) {
            Graph<V,E> graph = context.graph;
            E e = context.element;
            if(!(e instanceof BiologicalEdgeAbstract)){
            	return new EdgeShape.CubicCurve<V, E>().transform(context);
            }
            BiologicalEdgeAbstract edge = (BiologicalEdgeAbstract) e;
           Pair<V> endpoints = graph.getEndpoints(e);
           Pair<BiologicalNodeAbstract> endpointNodes = new Pair<BiologicalNodeAbstract>((BiologicalNodeAbstract) endpoints.getFirst(), (BiologicalNodeAbstract) endpoints.getSecond());
           
           // If Loop, draw loop.
           if(endpoints != null) {
               boolean isLoop = endpoints.getFirst().equals(endpoints.getSecond());
               if (isLoop) {
                   return loop.transform(context);
               }
           }
           
           // Dont draw if selected group parameters fit.
           if(!HEBLayoutConfig.getInstance().getShowInternalEdges() && endpointNodes.getFirst().getParentNode()!=null){
        	   if(HEBLayoutConfig.GROUP_DEPTH==HEBLayoutConfig.ROUGHEST_LEVEL && endpointNodes.getFirst().getLastParentNode()==endpointNodes.getSecond().getLastParentNode()){
        		   return new Line2D.Double(0.0,0.0,0.0,0.0);
        	   }
        	   if(HEBLayoutConfig.GROUP_DEPTH==HEBLayoutConfig.FINEST_LEVEL && endpointNodes.getFirst().getParentNode()==endpointNodes.getSecond().getParentNode()){
        		   return new Line2D.Double(0.0,0.0,0.0,0.0);
        	   }
           }
           
           // current MyGraph
           MyGraph myGraph = GraphInstance.getMyGraph();           
           
           // location of the startNode.
           Point2D startPoint = new Point2D.Double(myGraph.getVertexLocation(endpointNodes.getFirst()).getX(),
        		   myGraph.getVertexLocation(endpointNodes.getFirst()).getY());
                      
           // location of the endNode.
           Point2D endPoint = new Point2D.Double(myGraph.getVertexLocation(endpointNodes.getSecond()).getX(),
        		   myGraph.getVertexLocation(endpointNodes.getSecond()).getY());
                      
           // The circle's attributes.
           Point2D center = getCenterPoint();
           double circleRadius = Point2D.distance(centerPoint.getX(), centerPoint.getY(), startPoint.getX(), startPoint.getY());
           
           // Compute control points
           double angle;
           double bundling_error = (100-HEBLayoutConfig.EDGE_BUNDLING_PERCENTAGE)*0.01;
           BiologicalNodeAbstract lcp = endpointNodes.getFirst().getLastCommonParentNode(endpointNodes.getSecond());
           List<Pair<java.lang.Double>> controlPoints = new ArrayList<Pair<java.lang.Double>>();
           for(BiologicalNodeAbstract node : endpointNodes.getFirst().getAllParentNodesSorted()){
        	   Set<Point2D> childNodePoints = new HashSet<Point2D>();
        	   for(BiologicalNodeAbstract n : node.getCurrentShownChildrenNodes(myGraph)){
        		   childNodePoints.add(myGraph.getVertexLocation(n));
        	   }
        	   angle = Circle.getAngle(center,Circle.averagePoint(childNodePoints));
        	   Pair<java.lang.Double> cP = new Pair<java.lang.Double>(angle-bundling_error*(angle-Circle.getAngle(center,startPoint)),layer.get(node).doubleValue());
        	   controlPoints.add(cP);
        	   if(node==lcp)
        		   break;
           }
           if(lcp==null){
        	   edge.setColor(Color.RED);
           }
           boolean lcpreachedflag = false;
           List<BiologicalNodeAbstract> node2Parents = endpointNodes.getSecond().getAllParentNodesSorted();
           for(int i=node2Parents.size()-1; i>=0; i--){
        	   if(node2Parents.get(i)==lcp){
        		   lcpreachedflag = true;
        		   continue;
        	   }
        	   if(lcp==null||lcpreachedflag){
        		   BiologicalNodeAbstract node = node2Parents.get(i);
        		   Set<Point2D> childNodePoints = new HashSet<Point2D>();
        		   for(BiologicalNodeAbstract n : node.getCurrentShownChildrenNodes(myGraph)){
        			   childNodePoints.add(myGraph.getVertexLocation(n));
        		   }
            	   angle = Circle.getAngle(center,Circle.averagePoint(childNodePoints));
        		   Pair<java.lang.Double> cP = new Pair<java.lang.Double>(angle-bundling_error*(angle-Circle.getAngle(center,endPoint)),layer.get(node).doubleValue());
        		   controlPoints.add(cP);
        		   Collection<BiologicalNodeAbstract> children = node.getInnerNodes();
        		   children.retainAll(endpointNodes.getSecond().getAllParentNodes());
        		   if(children.isEmpty()){
        			   children.add(endpointNodes.getSecond());
        		   }
        		   node = children.iterator().next();
        	   }
           }
    
           // if no control points exist, draw quadratic bezier with center as control point
           if(controlPoints.size()==0){
        	   Point2D centerTransform = new Point2D.Double(center.getX(),center.getY());
        	   centerTransform = Circle.computeControlPoint(centerTransform,center,startPoint,endPoint);
        	   return new QuadCurve2D.Double(0.0, 0.0, centerTransform.getX(), centerTransform.getY(), 1.0, 0.0);
           }
           
           // build piecewise bezier curve
           List<QuadCurve2D> lines = new ArrayList<QuadCurve2D>();
           Point2D lastPoint = new Point2D.Double(0.0f,0.0f);
           for(int i=0; i<controlPoints.size(); i++){
        	   Point2D nP = Circle.getPointOnCircle(center, circleRadius, controlPoints.get(i).getFirst());
        	   nP = Circle.moveInCenterDirection(nP, center, 100*controlPoints.get(i).getSecond()/(maxLayer+1));
        	   Point2D nP2 = new Point2D.Double(1.0f,0.0f);
        	   if(i+1<controlPoints.size()){
        		   nP2 = Circle.getPointOnCircle(center, circleRadius, controlPoints.get(i+1).getFirst());
        		   nP2 = Circle.moveInCenterDirection(nP2, center, 100*controlPoints.get(i+1).getSecond()/(maxLayer+1));
            	   nP2 = new Point2D.Double((nP.getX()+nP2.getX())/2,(nP.getY()+nP2.getY())/2);
            	   nP2 = Circle.computeControlPoint(nP2,center,startPoint,endPoint);
        	   }
        	   nP = Circle.computeControlPoint(nP,center,startPoint,endPoint);
        	   lines.add(new QuadCurve2D.Double(lastPoint.getX(), lastPoint.getY(), nP.getX(), nP.getY(), nP2.getX(), nP2.getY()));
        	   lastPoint = nP2;
           }
           Path2D path = new Path2D.Double();
           for(QuadCurve2D l : lines){
             path.append(l,true);     
           }           
           return path;
        }
    }
    
    
}
