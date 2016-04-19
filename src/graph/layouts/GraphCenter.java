package graph.layouts;

import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.Iterator;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.jung.classes.MyGraph;

public class GraphCenter {

	private Point2D center;
	private double width;
	private double height;
	
	private double minX = Double.MAX_VALUE;
	private double minY = Double.MAX_VALUE;
	private double maxX = -Double.MAX_VALUE;
	private double maxY = -Double.MAX_VALUE;
	private MyGraph g;
	
	public GraphCenter(MyGraph g){
		this.g = g;
		init();
	}
	
	private void init(){
		Iterator<BiologicalNodeAbstract> it = g.getAllVertices().iterator();
		
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			Point2D p = g.getVertexLocation(bna);
			if (p.getX() < minX) {
				minX = p.getX();
			}
			if (p.getX() > maxX) {
				maxX = p.getX();
			}
			if (p.getY() < minY) {
				minY = p.getY();
			}
			if (p.getY() > maxY) {
				maxY = p.getY();
			}

		}
		
		for(int i = 0; i<g.getAnnotationManager().getAnnotations().size(); i++){
			RectangularShape s = g.getAnnotationManager().getAnnotations().get(i).getShape();
			if (s.getMinX() < minX) {
				minX = s.getMinX();
			}
			if (s.getMaxX() > maxX) {
				maxX = s.getMaxX();
			}
			if (s.getMinY() < minY) {
				minY = s.getMinY();
			}
			if (s.getMaxY() > maxY) {
				maxY = s.getMaxY();
			}
		}
		this.width = maxX-minX;
		this.height = maxY-minY;
		this.center = new Point2D.Double(minX+width/2, minY+height/2);
	}
	
	public Point2D getCenter(){
		return center;
	}
	
	public double getHeight(){
		return height;
	}
	
	public double getWidth(){
		return width;
	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}
}
