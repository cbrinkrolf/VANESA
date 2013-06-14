package heatmap;

import java.awt.Point;
import java.util.Random;

public class HeatmapPoint {
	public Point coordinates;
	public int strength;
	
	public static int DRAWINGRADIUS = 220;
	
	public HeatmapPoint(int x, int y, int strength) {
		this.coordinates = new Point(x,y);
		this.strength = strength;
	}
	
	public void paintAsSeededPoints(double[][] newData, int radius, boolean changeStrength,
			int plusX, int plusY, double scaleFactor) {
		radius = (int) Math.floor(radius*scaleFactor);
		plusX = (int) Math.floor(plusX*scaleFactor);
		plusY = (int) Math.floor(plusY*scaleFactor);
		
		Random random = new Random();
		int x;
		int y;
		for (int i=0; i<this.strength*40; i++) {
			x=random.nextInt(radius*2)-radius;
			y=random.nextInt(radius*2)-radius;
			double distance = Math.sqrt(x*x+y*y);
			//test if the pixel is in the drawing circle
			if (distance<=radius) {
				int newX = plusX+(int) Math.floor(this.coordinates.x*scaleFactor)+x;
				int newY = plusY+(int) Math.floor(this.coordinates.y*scaleFactor)+y;
				
				//if (this.strength!=30)
				this.paintPixel(newData, newX, newY, random.nextInt(100));
			}
		}
	}
	
	private void paintPixel(double[][] newData, int x, int y, int strength) {
		//test if the pixel is out of the picture
		if ((x>=0) && (x<newData.length)
			&& (y>=0) && (y<newData[x].length)
			&& ((newData[x][y]==0) || (newData[x][y] > strength)) // nur wenn der neue wert kleiner (dunkler) ist
			){
				newData[x][y] = strength;
				
		}
	}
	
	public void paintAsCircle(double[][] newData, int radius, boolean changeStrength,
			int plusX, int plusY, double scaleFactor) {
		radius = (int) Math.floor(radius*scaleFactor);
		plusX = (int) Math.floor(plusX*scaleFactor);
		plusY = (int) Math.floor(plusY*scaleFactor);
		
		for (int x=-radius; x<radius; x++) {
			for (int y=-radius; y<radius; y++) {
				double distance = Math.sqrt(x*x+y*y);
				//test if the pixel is in the drawing circle
				if (distance<=radius) {
					int newX = plusX+(int) Math.floor(this.coordinates.x*scaleFactor)+x;
					int newY = plusY+(int) Math.floor(this.coordinates.y*scaleFactor)+y;
					int newStrength = 0;
					double from = 0.7;
					if ((changeStrength) && (distance/radius>from)) {
						newStrength = (int) Math.round(this.strength+(400-this.strength)*(distance/radius-from));
						newStrength = Math.min(500, newStrength);
					}
					else {
						newStrength = this.strength;
					}
					
					this.paintPixel(newData, newX, newY, newStrength);
				}
			}
		}
		
	}
	public void paintTo(double[][] newData) {
		//this.paintAsCircle(newData, HeatmapPoint.DRAWINGRADIUS, true, 0, 0, 1);	
		this.paintAsSeededPoints(newData, HeatmapPoint.DRAWINGRADIUS, true, 0, 0, 1);	
	}
	
	public void paintTo(double[][] newData, int plusX, int plusY, double scaleFactor, String visualisationStyle) {
		if (visualisationStyle.equals(AdoptedHeatmap.VISUALIZATION_HEAT)) {
			this.paintAsCircle(newData, HeatmapPoint.DRAWINGRADIUS, true, plusX, plusY, scaleFactor);
		}
		else {
			this.paintAsSeededPoints(newData, HeatmapPoint.DRAWINGRADIUS, true, plusX, plusY, scaleFactor);	
		}
			
	}
}
