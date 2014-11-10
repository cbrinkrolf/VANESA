package graph.layouts.hebLayout;

import java.awt.geom.Point2D;

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
		double angle = Math.atan2(nodePosition.getY()-centerPoint.getY(), nodePosition.getX()-centerPoint.getY());
		return convertAngle(angle);
	}
}
