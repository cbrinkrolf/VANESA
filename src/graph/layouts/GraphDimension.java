package graph.layouts;

import java.awt.Dimension;
import java.awt.geom.Point2D;

public class GraphDimension {

	private double x_min=0;
	private double x_max=0;
	private double y_min=0;
	private double y_max=0;
	boolean firstTime = true;
	
	
	public GraphDimension(){
		
	}
	
	public void updateboundaries(Point2D point){
		
		double x = point.getX();
		double y = point.getY();
		
		if(firstTime){
			
			x_min=x;
			x_max=x;
			y_min=y;
			y_max=y;
			firstTime =false;
			
		}else{
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
	}
	
	public Dimension getBoundaries(){
		return new Dimension((int)(x_max-x_min),(int)(y_max-y_min));
	}

	public double getX_max() {
		return x_max;
	}

	public double getX_min() {
		return x_min;
	}

	public double getY_max() {
		return y_max;
	}

	public double getY_min() {
		return y_min;
	}
}
