/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on Jul 20, 2004
 */
package graph.jung.graphDrawing;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

public class VertexShapes {
	private static float vertexSize = 20;
	private static float varf = 1;

	private static Ellipse2D ellipse = null;
	private static Rectangle2D rectangle = null;
	private static Shape doubleRectangle = null;
	private static Shape doubleEllipse = null;
	private static RoundRectangle2D roundRectangle = null;
	private static Map<Shape, Shape> coarsedShapes = new HashMap<>();
	private static Map<Integer, Shape> polygons = new HashMap<>();
	private static Map<Integer, Shape> stars = new HashMap<>();
	private static Shape discreteTransition = null;
	private static Shape continuousTransition = null;
	private static Shape inhibitorArrowHead = null;
	private static Shape pnInhibitorArrowHead = null;

	/**
	 * Creates a <code>VertexShapeFactory</code> with the specified vertex size and
	 * aspect ratio functions.
	 */
	// public VertexShapes(float vertexSize, float varf) {
	// VertexShapes.vertexSize = vertexSize;
	// VertexShapes.varf = varf;
	// }

	/**
	 * Returns a <code>Rectangle2D</code> whose width and height are defined by this
	 * instance's size and aspect ratio functions for this vertex.
	 */
	public static Rectangle2D getRectangle() {
		if (rectangle == null) {
			Rectangle2D rectangle = new Rectangle2D.Float();
			float width = (float) vertexSize;
			float height = width * (float) varf;
			float h_offset = -(width / 2);
			float v_offset = -(height / 2);
//        theRectangle.setFrame(h_offset, v_offset, width, height);
//        return theRectangle;
			rectangle.setFrame(h_offset, v_offset, width, height);
			VertexShapes.rectangle = rectangle;
		}
		return rectangle;
	}

	public static Shape getDoubleRectangle() {
		if (doubleRectangle == null) {
			Rectangle2D rectangle = new Rectangle2D.Float();
			float width = vertexSize;
			float height = width * varf;
			float h_offset = -(width / 2);
			float v_offset = -(height / 2);
//        theRectangle.setFrame(h_offset, v_offset, width, height);
//        return theRectangle;
			rectangle.setFrame(h_offset, v_offset, width, height);

			width = (float) (vertexSize * 0.5);
			height = (float) (width * varf * 1.6);
			h_offset = -(width / 2);
			v_offset = -(height / 2);
			Rectangle2D rectangle2 = new Rectangle2D.Float();
			rectangle2.setFrame(h_offset, v_offset, width, height);

			GeneralPath gp = new GeneralPath(rectangle2);
			gp.append(rectangle, false);
			doubleRectangle = gp;
		}

		return doubleRectangle;
	}

	/**
	 * Returns a <code>Ellipse2D</code> whose width and height are defined by this
	 * instance's size and aspect ratio functions for this vertex.
	 */
	public static Ellipse2D getEllipse() {
		if (ellipse == null) {
			Ellipse2D ellipse = new Ellipse2D.Float();
			Rectangle2D frame = getRectangle();
			ellipse.setFrame(frame);
			VertexShapes.ellipse = ellipse;
		}
		return ellipse;
	}

	public static Shape makeCoarse(Shape shape) {
		if (!coarsedShapes.containsKey(shape)) {
			Rectangle2D frame = shape.getBounds2D();
			GeneralPath gp = new GeneralPath(shape);
			float width = (float) frame.getWidth();
			float height = (float) frame.getHeight();
			gp.moveTo(0, -height / 2 + 1);
			gp.lineTo(0, height / 2);
			gp.moveTo(-width / 2 + 1, 0);
			gp.lineTo(width / 2, 0);
			coarsedShapes.put(shape, gp);
			return gp;
		} else {
			return coarsedShapes.get(shape);
		}
	}

	public static Shape getDoubleEllipse() {

		if (doubleEllipse == null) {
			Ellipse2D ellipse = new Ellipse2D.Float();
			Ellipse2D ellipse2 = new Ellipse2D.Float();

			Rectangle2D frame = getRectangle();

			Rectangle2D rectangle = new Rectangle2D.Float();
			float width = vertexSize - 5;
			float height = width * varf;
			float h_offset = -(width / 2);
			float v_offset = -(height / 2);
//        theRectangle.setFrame(h_offset, v_offset, width, height);
//        return theRectangle;
			rectangle.setFrame(h_offset, v_offset, width, height);

			ellipse2.setFrame(rectangle);
			ellipse.setFrame(frame);

			GeneralPath gp = new GeneralPath(ellipse);
			gp.append(ellipse2, false);
			doubleEllipse = gp;
		}
		return doubleEllipse;
	}

	/**
	 * Returns a <code>RoundRectangle2D</code> whose width and height are defined by
	 * this instance's size and aspect ratio functions for this vertex. The arc size
	 * is set to be half the minimum of the height and width of the frame.
	 */
	public static RoundRectangle2D getRoundRectangle() {
		if (roundRectangle == null) {
			RoundRectangle2D roundRectangle = new RoundRectangle2D.Float();
			Rectangle2D frame = getRectangle();
			float arc_size = (float) Math.min(frame.getHeight(), frame.getWidth()) / 2;
//        theRoundRectangle.setRoundRect(frame.getX(), frame.getY(),
//                frame.getWidth(), frame.getHeight(), arc_size, arc_size);
//        return theRoundRectangle;
			roundRectangle.setRoundRect(frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight(), arc_size,
					arc_size);
			VertexShapes.roundRectangle = roundRectangle;
		}
		return roundRectangle;
	}

	/**
	 * Returns a regular <code>num_sides</code>-sided <code>Polygon</code> whose
	 * bounding box's width and height are defined by this instance's size and
	 * aspect ratio functions for this vertex.
	 * 
	 * @param num_sides the number of sides of the polygon; must be >= 3.
	 */
	public static Shape getRegularPolygon(int num_sides) {
		if (!polygons.containsKey(num_sides)) {

			GeneralPath polygon = new GeneralPath();
			if (num_sides < 3)
				throw new IllegalArgumentException("Number of sides must be >= 3");
			// Rectangle2D frame = getRectangle(v);
			float width = VertexShapes.vertexSize;// (float)frame.getWidth();
			float height = 20;// (float)frame.getHeight();

			// generate coordinates
			double angle = 0;
//        thePolygon.reset();
//        thePolygon.moveTo(0,0);
//        thePolygon.lineTo(width, 0);
			polygon.reset();
			polygon.moveTo(0, 0);
			polygon.lineTo(width, 0);
			double theta = (2 * Math.PI) / num_sides;
			for (int i = 2; i < num_sides; i++) {
				angle -= theta;
				float delta_x = (float) (width * Math.cos(angle));
				float delta_y = (float) (width * Math.sin(angle));
//            Point2D prev = thePolygon.getCurrentPoint();
//            thePolygon.lineTo((float)prev.getX() + delta_x, (float)prev.getY() + delta_y);
				Point2D prev = polygon.getCurrentPoint();
				polygon.lineTo((float) prev.getX() + delta_x, (float) prev.getY() + delta_y);
			}
//        thePolygon.closePath();
			polygon.closePath();

			// scale polygon to be right size, translate to center at (0,0)
//        Rectangle2D r = thePolygon.getBounds2D();
			Rectangle2D r = polygon.getBounds2D();
			double scale_x = width / r.getWidth();
			double scale_y = height / r.getHeight();
			float translationX = (float) (r.getMinX() + r.getWidth() / 2);
			float translationY = (float) (r.getMinY() + r.getHeight() / 2);

			AffineTransform at = AffineTransform.getScaleInstance(scale_x, scale_y);
			at.translate(-translationX, -translationY);
//        Shape shape = at.createTransformedShape(thePolygon);
			Shape shape = at.createTransformedShape(polygon);
			polygons.put(num_sides, shape);
			return shape;
		}
		return polygons.get(num_sides);
	}

	/**
	 * Returns a regular <code>Polygon</code> of <code>num_points</code> points
	 * whose bounding box's width and height are defined by this instance's size and
	 * aspect ratio functions for this vertex.
	 * 
	 * @param num_points the number of points of the polygon; must be >= 5.
	 */
	public static Shape getRegularStar(int num_points) {
		if (!stars.containsKey(num_points)) {
			GeneralPath polygon = new GeneralPath();
			if (num_points < 5)
				throw new IllegalArgumentException("Number of sides must be >= 5");
			Rectangle2D frame = getRectangle();
			float width = (float) frame.getWidth();
			float height = (float) frame.getHeight();

			// generate coordinates
			double theta = (2 * Math.PI) / num_points;
			double angle = -theta / 2;
//        thePolygon.reset();
//        thePolygon.moveTo(0,0);
			polygon.reset();
			polygon.moveTo(0, 0);
			float delta_x = width * (float) Math.cos(angle);
			float delta_y = width * (float) Math.sin(angle);
//        Point2D prev = thePolygon.getCurrentPoint();
//        thePolygon.lineTo((float)prev.getX() + delta_x, (float)prev.getY() + delta_y);
			Point2D prev = polygon.getCurrentPoint();
			polygon.lineTo((float) prev.getX() + delta_x, (float) prev.getY() + delta_y);

			for (int i = 1; i < num_points; i++) {
				angle += theta;
				delta_x = width * (float) Math.cos(angle);
				delta_y = width * (float) Math.sin(angle);
//            prev = thePolygon.getCurrentPoint();
//            thePolygon.lineTo((float)prev.getX() + delta_x, (float)prev.getY() + delta_y);
				prev = polygon.getCurrentPoint();
				polygon.lineTo((float) prev.getX() + delta_x, (float) prev.getY() + delta_y);

				angle -= theta * 2;
				delta_x = width * (float) Math.cos(angle);
				delta_y = width * (float) Math.sin(angle);
//            prev = thePolygon.getCurrentPoint();
//            thePolygon.lineTo((float)prev.getX() + delta_x, (float)prev.getY() + delta_y);
				prev = polygon.getCurrentPoint();
				polygon.lineTo((float) prev.getX() + delta_x, (float) prev.getY() + delta_y);
			}
//        thePolygon.closePath();
			polygon.closePath();

			// scale polygon to be right size, translate to center at (0,0)
//        Rectangle2D r = thePolygon.getBounds2D();
			Rectangle2D r = polygon.getBounds2D();
			double scale_x = width / r.getWidth();
			double scale_y = height / r.getHeight();

			float translationX = (float) (r.getMinX() + r.getWidth() / 2);
			float translationY = (float) (r.getMinY() + r.getHeight() / 2);

			AffineTransform at = AffineTransform.getScaleInstance(scale_x, scale_y);
			at.translate(-translationX, -translationY);

//        Shape shape = at.createTransformedShape(thePolygon);
			Shape shape = at.createTransformedShape(polygon);
			stars.put(num_points, shape);
			return shape;
		}
		return stars.get(num_points);
	}

	public static Shape getDiscreteTransitionShape() {
		if (VertexShapes.discreteTransition == null) {
			AffineTransform transform2 = new AffineTransform();
			transform2.translate(1, 1);
			transform2.scale(1, 2);
			discreteTransition = transform2.createTransformedShape(VertexShapes.getRectangle());
		}
		return discreteTransition;
	}

	public static Shape getContinuousTransitionShape() {
		if (VertexShapes.continuousTransition == null) {
			AffineTransform transform2 = new AffineTransform();
			transform2.scale(1, 2);
			continuousTransition = transform2.createTransformedShape(VertexShapes.getDoubleRectangle());
		}
		return continuousTransition;
	}

	public static Shape getInhibitorArrowHead() {
		if (inhibitorArrowHead == null) {
			float width = 10;
			float length = 8;
			float offset = 4;
			GeneralPath arrow = new GeneralPath();
			arrow.moveTo(offset, 0);
			arrow.lineTo(offset, length * 0.5f);
			arrow.lineTo(offset - width, length * 0.5f);
			arrow.lineTo(offset - width, -length * 0.5f);
			arrow.lineTo(offset, -length * 0.5f);
			arrow.lineTo(offset, 0);
			inhibitorArrowHead = arrow;
		}
		return inhibitorArrowHead;

	}
	
	public static Shape getPNInhibitorArrowHead() {
		if(pnInhibitorArrowHead == null){
			pnInhibitorArrowHead = new Ellipse2D.Double(-10, -5, 10, 10);
		}
		return pnInhibitorArrowHead;
	}
}