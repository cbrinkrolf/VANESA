package graph.layouts.hebLayout;

import java.awt.geom.Point2D;
import java.util.Set;

public class Circle {
	
	/**
	 * Computes the angle of a given Point in relation to the circle's center point.
	 * @param centerPoint The center point of the circle.
	 * @param nodePosition The point of interest.
	 * @return The angle of the point in relation to the circle's center.
	 * @author tloka
	 */
	public static double getAngle(Point2D centerPoint, Point2D nodePosition){
		double angle = Math.atan2(nodePosition.getY()-centerPoint.getY(), nodePosition.getX()-centerPoint.getX());
		return angle;
	}
	
	/**
	 * Computes the point on a circle line in relation to the circle's center point and the point's angle.
	 * @param centerPoint The center point of the circle.
	 * @param radius The radius of the circle.
	 * @param angle The angle of the point.
	 * @return The computed coordinate of the point on the circle.
	 * @author tloka
	 */
	public static Point2D getPointOnCircle(Point2D centerPoint, double radius, double angle){
		return new Point2D.Double(Math.cos(angle) * radius + centerPoint.getX(),
				Math.sin(angle) * radius + centerPoint.getY());
	}
	
	/**
	 * Computes the (unweighted) average point of a given set of nodes.
	 * @param nodes The input nodes.
	 * @return The (unweighted) average coordinate.
	 */
	public static Point2D averagePoint(Set<Point2D> nodes){
    	Point2D avPoint = new Point2D.Double(0,0);
    	for(Point2D node : nodes){
    		avPoint.setLocation(avPoint.getX()+(node.getX()/nodes.size()), avPoint.getY()+(node.getY()/nodes.size()));
    	}
    	return avPoint;
    }
    
	/**
	 * Moves a point in the direction of the center (or any other point).
	 * @param point The point to be moved.
	 * @param center The point that gives direction and length of movement.
	 * @param percentage How much of the way to be moved (in percent).
	 * @return The coordinate of the moved point.
	 */
    public static Point2D moveInCenterDirection(Point2D point, Point2D center, double percentage){
    	return new Point2D.Double(percentage/100*(center.getX()-point.getX())+point.getX(),
    			percentage/100*(center.getY()-point.getY())+point.getY());
    }
    
    /**
     * Transform a point into coordinates with basis Start=(0,0) and End=(1,0).
     * @param basis The controlPoint to be computed (in not transformed coorcinates)
     * @param center The center of the circle (in not transformed coorcinates)
     * @param startPoint The location of start node (in not transformed coorcinates)
     * @param endPoint The location of end node (in not transformed coorcinates)
    
     * @return The controlPoint in transformed coordinates.
     */
    public static Point2D computeControlPoint(Point2D basis, Point2D center, Point2D startPoint, Point2D endPoint){
    	
    	double startBasisDistance = Point2D.distance(basis.getX(), basis.getY(), startPoint.getX(), startPoint.getY());
    	double startEndDistance = Point2D.distance(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
    	double startEndAngle = Circle.getAngle(startPoint, endPoint);
    	double startBasisAngle = Circle.getAngle(startPoint, basis);
    	    	
   		double alpha = startBasisAngle - startEndAngle;

   		double beta = Math.PI/2-alpha;
    	
   		double c = startBasisDistance;
   		double a = startBasisDistance*Math.cos(beta);
   		double b = Math.sqrt(Math.pow(c,2)-Math.pow(a, 2));
    	
   		Point2D longerControlPoint = new Point2D.Double(b/startEndDistance,a);
   		return longerControlPoint;
   	} 
        
	
}
