package graph.layouts;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.Layout;
import graph.GraphInstance;

public class SubLayoutCenter {

	private Set vertices;
	private Point2D center;
	private double width;
	private double height;
	private Layout layout;
	
	private double x_min=0;
	private double x_max=0;
	private double y_min=0;
	private double y_max=0;
	private double radius = 0;
	
	boolean firstValues = false;
	private boolean entireScreen=false;
	private int vertexNum = 0;
	private GraphInstance graphInstance= new GraphInstance();
	
	public SubLayoutCenter(Set vertices, Layout layout){
		this.layout=layout;
		this.vertices=vertices;
		vertexNum= vertices.size();
		
		Iterator it = vertices.iterator();
		while (it.hasNext()){
			Vertex v= (Vertex)it.next();
			if (vertexNum==v.getGraph().numVertices()) entireScreen = true;
			break;
		}
		
		if(entireScreen) initScreenBoundaries();
		else initBoundaries();
	}
	
	private void initScreenBoundaries(){
		
		Dimension d =GraphInstance.getMyGraph().getVisibleRect();
		d.setSize(d.getWidth()-30, d.getHeight()-30);
		x_min =30;
		x_max=d.getWidth();
		y_min=30;
		y_max=d.getHeight();
		calculateLengths();
	    center = GraphInstance.getMyGraph().getScreenCenter();
		calculateRadius();
	}
	
	private void initBoundaries(){
		Iterator it = vertices.iterator();
		while (it.hasNext()){
			Vertex v= (Vertex)it.next();
			Point2D point = layout.getLocation(v);
			
			if(!firstValues){
				
				x_min = point.getX();
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
		calculateRadius();
	}
	
	public double getRadius(){
		return radius;
	}
	
	private void calculateRadius(){
		if(center.getX()-x_min < center.getY()-y_min){
			radius = center.getX()-x_min;
		}else{
			radius = center.getY()-y_min;
		}
	}
	
	private void calculateLengths(){
		width = x_max-x_min;
		height = y_max-y_min;
	}
	
	private void calculateCenter(){
		double x_center = x_min + ((x_max-x_min)/2);
		double y_center = y_min + ((y_max-y_min)/2);
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
}
