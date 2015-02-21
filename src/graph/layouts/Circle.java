package graph.layouts;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Iterator;
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
    	
    	// Translation of coordinate system
    	Point2D cP = new Point2D.Double(basis.getX()-startPoint.getX(),basis.getY()-startPoint.getY());
    	double angle = Circle.getAngle(startPoint, endPoint);
    	double r[] = new double[4];
    	r[0]=Math.cos(angle);
    	r[1]=Math.sin(angle);
    	r[2]=-r[1];
    	r[3]=r[0];
    	
    	// Rotation of coordinate system
    	cP.setLocation(r[0]*cP.getX()+r[1]*cP.getY(),r[2]*cP.getX()+r[3]*cP.getY());

    	// compression of x-axis
    	cP.setLocation(1/Math.sqrt(Math.pow(endPoint.getX()-startPoint.getX(),2)+Math.pow(endPoint.getY()-startPoint.getY(),2))*cP.getX(),cP.getY());
   	
    	return cP;
    
    } 
        
	
}
