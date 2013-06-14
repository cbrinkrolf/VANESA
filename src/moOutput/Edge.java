package moOutput;

import java.awt.geom.Point2D;

public class Edge {
	public boolean firstIsTransition;
	public String from;
	public String to;
	public Point2D frompoint;
	public Point2D topoint;
	
	public Edge(boolean firstIsTransition, String from, String to, Point2D frompoint, Point2D topoint){
		this.firstIsTransition=firstIsTransition;
		this.from=from;
		this.to=to;
		this.frompoint=frompoint;
		this.topoint=topoint;
	}
}
