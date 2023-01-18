package graph.gui;

public class Boundary {

	private double lowerBoundary;
	private boolean lowerBoundarySet = false;
	private double upperBoundary;
	private boolean upperBoundarySet = false;
	
	public double getLowerBoundary() {
		return lowerBoundary;
	}
	public void setLowerBoundary(double lowerBoundary) {
		this.lowerBoundary = lowerBoundary;
		this.lowerBoundarySet = true;
	}
	public boolean isLowerBoundarySet() {
		return lowerBoundarySet;
	}
	
	public double getUpperBoundary() {
		return upperBoundary;
	}
	public void setUpperBoundary(double upperBoundary) {
		this.upperBoundary = upperBoundary;
		this.upperBoundarySet = true;
	}
	public boolean isUpperBoundarySet() {
		return upperBoundarySet;
	}
	
}
