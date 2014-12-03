package graph.layouts;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.Layout;
import graph.jung.classes.MyGraph;
//import edu.uci.ics.jung.graph.Vertex;
//import edu.uci.ics.jung.visualization.Layout;

public class GraphCenter {

	private Collection<BiologicalNodeAbstract> vertices;
	private Point2D center;
	private double width;
	private double height;
	private Layout<BiologicalNodeAbstract, BiologicalEdgeAbstract> layout;
	
	private double x_min=0;
	private double x_max=0;
	private double y_min=0;
	private double y_max=0;
	private double radius = 0;
	
	
	boolean firstValues = false;

	
	public GraphCenter(MyGraph g, Layout<BiologicalNodeAbstract, BiologicalEdgeAbstract> l){
		layout=l;
		vertices=g.getAllVertices();
		initBoundaries();
	}
	
	private void initBoundaries(){
		Iterator<BiologicalNodeAbstract> it = vertices.iterator();
		BiologicalNodeAbstract v;
		while (it.hasNext()){
			
			v = it.next();		
			Point2D point = layout.transform(v);
			//System.out.println(point);
			
			if(!firstValues){
				
				x_min =point.getX();
				x_max=point.getX();
				y_min=point.getY();
				y_max=point.getY();
				firstValues=true;
			}else{
				updateboundaries(point);
			}		
		}
		calculateLengths();
		calculateCenter();
	}
	
	public double getRadius(){
		return radius;
	}
	
	private void calculateLengths(){
		width = x_max-x_min;
		height = y_max-y_min;
	}
	
	private void calculateCenter(){
		double x_center = x_min + (width/2);
		double y_center = y_min + (height/2);
		center = new Point2D.Double(x_center,y_center);
	}
	
	private void updateboundaries(Point2D point){
		
		double x = point.getX();
		double y = point.getY();
		
		if(x>x_max){
			x_max=x;
		} 		
		if(x<x_min){
			x_min=x;
		}			
		if(y>y_max){
			y_max=y;
		}		
		if(y<y_min){
			y_min=y;
		}		
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

	/**
	 * @return the x_min
	 */
	public double getXmin() {
		return x_min;
	}


	/**
	 * @return the y_min
	 */
	public double getYmin() {
		return y_min;
	}
}
