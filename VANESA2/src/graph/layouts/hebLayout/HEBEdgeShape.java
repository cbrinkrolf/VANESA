package graph.layouts.hebLayout;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
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
         
        /**
         * singleton instance of the CubicCurve edge shape
         */
        private static Shape instance = new CubicCurve2D.Float();
                 
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
           if(endpoints != null) {
               boolean isLoop = endpoints.getFirst().equals(endpoints.getSecond());
               if (isLoop) {
                   return loop.transform(context);
               }
           }
           
           // current MyGraph
           MyGraph myGraph = GraphInstance.getMyGraph();
           
           Set<Point2D> group1 = new HashSet<Point2D>();
           for(BiologicalNodeAbstract bna : ((HEBLayout) myGraph.getLayout()).getNodesGroup(endpointNodes.getFirst())){
        	   group1.add(myGraph.getVertexLocation(bna));
           }
           Set<Point2D> group2 = new HashSet<Point2D>();
           for(BiologicalNodeAbstract bna : ((HEBLayout) myGraph.getLayout()).getNodesGroup(endpointNodes.getSecond())){
        	   group2.add(myGraph.getVertexLocation(bna));
           }
           
           if(group1.equals(group2) && !HEBLayoutConfig.getInstance().getShowInternalEdges()){
        	   instance = new CubicCurve2D.Float();
        	   ((CubicCurve2D) instance).setCurve(0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f);
        	   return instance;
           }
           
           
           // location of the startNode.
           Point2D startPoint = new Point2D.Double(myGraph.getVertexLocation(endpointNodes.getFirst()).getX(),
        		   myGraph.getVertexLocation(endpointNodes.getFirst()).getY());
                      
           // location of the endNode.
           Point2D endPoint = new Point2D.Double(myGraph.getVertexLocation(endpointNodes.getSecond()).getX(),
        		   myGraph.getVertexLocation(endpointNodes.getSecond()).getY());
                      
           // The circle's center.
           Point2D center = getCenterPoint();
           
           BiologicalNodeAbstract lcp = endpointNodes.getFirst().getLastCommonParentNode(endpointNodes.getSecond());
           List<Pair<java.lang.Double>> controlPoints = new ArrayList<Pair<java.lang.Double>>();
           for(BiologicalNodeAbstract node : endpointNodes.getFirst().getAllParentNodesSorted()){
        	   Set<Point2D> childNodePoints = new HashSet<Point2D>();
        	   for(BiologicalNodeAbstract n : node.getCurrentShownChildrenNodes(myGraph)){
        		   childNodePoints.add(myGraph.getVertexLocation(n));
        	   }
        	   Pair<java.lang.Double> cP = new Pair<java.lang.Double>(Circle.getAngle(center,averagePoint(childNodePoints)),layer.get(node).doubleValue());
        	   controlPoints.add(cP);
        	   if(node==lcp)
        		   break;
           }
           boolean lcpreachedflag = false;
           List<BiologicalNodeAbstract> node2Parents = endpointNodes.getSecond().getAllParentNodesSorted();
           for(int i=node2Parents.size()-1; i>=0; i--){
        	   if(node2Parents.get(i)==lcp)
        		   lcpreachedflag = true;
        	   if(lcp==null||lcpreachedflag){
        		   BiologicalNodeAbstract node = node2Parents.get(i);
        		   Set<Point2D> childNodePoints = new HashSet<Point2D>();
        		   for(BiologicalNodeAbstract n : node.getCurrentShownChildrenNodes(myGraph)){
        			   childNodePoints.add(myGraph.getVertexLocation(n));
        		   }
        		   Pair<java.lang.Double> cP = new Pair<java.lang.Double>(Circle.getAngle(center,averagePoint(childNodePoints)),layer.get(node).doubleValue());
        		   controlPoints.add(cP);
        		   Collection<BiologicalNodeAbstract> children = node.getInnerNodes();
        		   children.retainAll(endpointNodes.getSecond().getAllParentNodes());
        		   if(children.isEmpty()){
        			   children.add(endpointNodes.getSecond());
        		   }
        		   node = children.iterator().next();
        	   }
           }
           controlPoints.add(new Pair<java.lang.Double>(Circle.getAngle(center, myGraph.getVertexLocation(endpointNodes.getSecond())),0.0));
           
           double moveQuotient = group1.equals(group2) ? 
        		   HEBLayoutConfig.GROUPINTERNAL_EDGE_BENDING_PERCENTAGE : HEBLayoutConfig.EDGE_BENDING_PERCENTAGE;
           
           double bundlingQuotient = HEBLayoutConfig.EDGE_BUNDLING_PERCENTAGE;
           
           double circleRadius = Point2D.distance(centerPoint.getX(), centerPoint.getY(), startPoint.getX(), startPoint.getY());
    
           double group1Angle = Circle.getAngle(center,averagePoint(group1));
           double group2Angle = Circle.getAngle(center,averagePoint(group2));
           
           //If points are in the same group, take the middle Point of them as control point of both.
           if(group1.equals(group2)){
        	   Set<Point2D> points = new HashSet<Point2D>();
        	   points.add(startPoint);
        	   points.add(endPoint);
        	   group1Angle = Circle.getAngle(center, averagePoint(points));
        	   group2Angle = group1Angle;
           }
                     
           // Computation of the first control point to bundle all edges connecting the same groups.
           Point2D cPoint1 = Circle.getPointOnCircle(center, circleRadius, group1Angle);
           cPoint1 = moveInCenterDirection(cPoint1, center, moveQuotient);  
           if(!group1.equals(group2)){
        	   	cPoint1 = moveInCenterDirection(cPoint1, startPoint, -bundlingQuotient);
           }
           cPoint1 = computeControlPoint(cPoint1, center, startPoint, endPoint, edge);
           
           // Computation of the second control point to bundle all edges connecting the same groups.
           Point2D cPoint2 = Circle.getPointOnCircle(center, circleRadius, group2Angle);
           cPoint2 = moveInCenterDirection(cPoint2, center, moveQuotient); 
           if(!group1.equals(group2)){
        	   cPoint2 = moveInCenterDirection(cPoint2, endPoint, -bundlingQuotient);
           }
           cPoint2 = computeControlPoint(cPoint2, center, startPoint, endPoint,edge);
           
           List<QuadCurve2D> lines = new ArrayList<QuadCurve2D>();
           Point2D lastPoint = new Point2D.Double(0.0f,0.0f);
           for(int i=0; i<controlPoints.size(); i++){
        	   Point2D nP = Circle.getPointOnCircle(center, circleRadius, controlPoints.get(i).getFirst());
        	   nP = moveInCenterDirection(nP, center, 100*controlPoints.get(i).getSecond()/(maxLayer+1));
        	   Point2D nP2 = new Point2D.Double(1.0f,0.0f);
        	   if(i+1<controlPoints.size()){
        		   nP2 = Circle.getPointOnCircle(center, circleRadius, controlPoints.get(i+1).getFirst());
        		   nP2 = moveInCenterDirection(nP2, center, 100*controlPoints.get(i+1).getSecond()/(maxLayer+1));
            	   nP2 = new Point2D.Double((nP.getX()+nP2.getX())/2,(nP.getY()+nP2.getY())/2);
            	   nP2 = computeControlPoint(nP2,center,startPoint,endPoint,edge);
        	   }
        	   nP = computeControlPoint(nP,center,startPoint,endPoint,edge);
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
    
    public static double gradient(double x1, double y1, double x2, double y2){
    	return (y2-y1)/(x2-x1);
    }
    
    public static double gradientAngle(double gradient){
    	return Math.atan(gradient);
    }
    
    private static Point2D averagePoint(Set<Point2D> nodes){
    	Point2D avPoint = new Point2D.Double(0,0);
    	for(Point2D node : nodes){
    		avPoint.setLocation(avPoint.getX()+(node.getX()/nodes.size()), avPoint.getY()+(node.getY()/nodes.size()));
    	}
    	return avPoint;
    }
    
    public static Point2D moveInCenterDirection(Point2D point, Point2D center, double percentage){
    	return new Point2D.Double(percentage/100*(center.getX()-point.getX())+point.getX(),
    			percentage/100*(center.getY()-point.getY())+point.getY());
    }
    
    /**
     * Method to compute the control Points in the coordinate system with Start=(0,0) and End=(1,0).
     * @param basis The controlPoint to be computed
     * @param center The center of the circle
     * @param startPoint The location of start node
     * @param endPoint The location of end node
    
     * @return The controlPoint in transformed coordinates.
     */
    private static Point2D computeControlPoint(Point2D basis, Point2D center, Point2D startPoint, Point2D endPoint, BiologicalEdgeAbstract edge){
    	
    	double startBasisDistance = Point2D.distance(basis.getX(), basis.getY(), startPoint.getX(), startPoint.getY());
    	double startEndDistance = Point2D.distance(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
    	double startEndAngle = Circle.getAngle(startPoint, endPoint);
    	double startBasisAngle = Circle.getAngle(startPoint, basis);
    	    	
   		double alpha = startBasisAngle - startEndAngle;

   		double beta = Math.PI/2-alpha;
    	
   		double c = startBasisDistance;
   		double a = startBasisDistance*Math.cos(beta);
   		double b = Math.sqrt(Math.pow(c,2)-Math.pow(a, 2));
    	
//    	if(edge.getFrom().getLabel().equals("9")){
//    		System.out.println(edge.getTo().getLabel());
//    		System.out.println(alpha);
//    		System.out.println(beta);
//    		System.out.println(c);
//    		System.out.println(a);
//    		System.out.println(b);
//    		System.out.println();
//    		
//    	}
   		Point2D longerControlPoint = new Point2D.Double(b/startEndDistance,a);
   		return longerControlPoint;
//   		return new Point2D.Double(b/startEndDistance,a);
   	} 
        
}
