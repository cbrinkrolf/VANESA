package graph.layouts.hebLayout;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import edu.uci.ics.jung.graph.util.Pair;

public class Circle {
	
	/**
	 * Converts any angle to an angle in interval [0,2PI]
	 * @param oldAngle Old angle (can be positive or negative)
	 * @return New angle in [0,2PI] interval
	 * @author tloka
	 */
	public static double convertAngle(double oldAngle){
		double angle = oldAngle%(2*Math.PI);
		if(angle<0) angle+=(2*Math.PI);
		return angle;
	}
	
	/**
	 * Computes the angle of a given Point in relation to the circle's center point.
	 * @param centerPoint The center point of the circle.
	 * @param nodePosition The point of interest.
	 * @return The angle of the point in relation to the circle's center.
	 * @author tloka
	 */
	public static double getAngle(Point2D centerPoint, Point2D nodePosition){
		double angle = Math.atan2(nodePosition.getY()-centerPoint.getY(), nodePosition.getX()-centerPoint.getX());
		return angle;//convertAngle(angle);
	}
	
	public static Point2D getPointOnCircle(Point2D centerPoint, double radius, double angle){
		return new Point2D.Double(Math.cos(angle) * radius + centerPoint.getX(),
				Math.sin(angle) * radius + centerPoint.getY());
	}
	
//	public static double getAngleDifference(double startAngle, double endAngle){
//		double start = convertAngle(startAngle);
//		double end = convertAngle(endAngle);
//		if(start==end)
//			return 0;
//		if(end>start){
//			if(end>start+Math.PI){
//				return -(start+2*Math.PI-end);
//			} else {
//				return end-start;
//			}
//		} else {
//			if(start>end+Math.PI){
//				return end+2*Math.PI-start;
//			} else {
//				return -(start-end);
//			}
//		}
//	}
	
	/**
	 * Gives the Point coordinates in a rotated coordinate system.
	 * @param point
	 * @param angle
	 * @return Point coordinates in rotated coordinate system.
	 */
	public static Point2D rotatePoint(Point2D point, double angle){
		Point2D rotatedPoint = new Point2D.Double(0,0);
		rotatedPoint.setLocation(-Math.sin(angle)*point.getX()+Math.cos(angle)*point.getY(), 
				Math.cos(angle)*point.getX()+Math.sin(angle*point.getY()));
		return rotatedPoint;
	}
}
