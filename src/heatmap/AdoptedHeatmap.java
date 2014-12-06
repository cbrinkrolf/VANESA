package heatmap;

/** 
 * AdoptedHeatmap is a modification of the Heatmap-Class to provide
 * a heatmap-like visualization to be used for visual
 * graph comparison.
 * 
 * The values from the camparison will be interpolated for better 
 * viewing. so if we have values like this:
 * 0 0 1 1 0 0
 * 
 * this will grow to 
 * 0 0 2 3 4 4 3 2 0 0
 */

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;

public class AdoptedHeatmap extends HeatMap{
	
	public static final Color[] GRADIENT_RED_BLUE_WHITE = Gradient.createMultiGradient(new Color[]{new Color(204, 0, 0), new Color(51, 153, 204), Color.WHITE}, 70);

	public static final String VISUALIZATION_SEEDED_POINTS = "Seeded points";
	public static final String VISUALIZATION_HEAT = "Heat";
	
	public static Color[] defaultGradient = GRADIENT_RED_BLUE_WHITE;

	public static String defaultVisualizationStyle = VISUALIZATION_HEAT;
	
	
	
	public AdoptedHeatmap() {
		
		super(new double[1][1], false, AdoptedHeatmap.defaultGradient);
		this.currentVisualizationStyle = defaultVisualizationStyle; 
	}
	private static final long serialVersionUID = 1L;
	
	//saves information about the coordinates and strengths of the heatmap
	private ArrayList<HeatmapPoint> data = new ArrayList<HeatmapPoint>();
	
	/** will clear the points data */
	public void resetPoints() {
		this.data.clear();
	}
	
	/** add a point */ 
	public void addPoint(int x, int y, int strength) {
		this.data.add(new HeatmapPoint(x,y,strength));
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(500,500);
	}
	
	/** main function for running as stand-alone class for testing purposes */
	public static void main(String[] args) {
		
		//create the main window
		JFrame f = new JFrame();
		
		//create a heatmap and add 3 data points
		AdoptedHeatmap h = new AdoptedHeatmap();
		h.addPoint(10, 10, 30);
		h.addPoint(100, 100, 30);
		h.addPoint(50, 50, 60);
		h.addPoint(120, 150, 30);
		h.addPoint(180, 180, 60);
		h.addPoint(-180, -140, 60);
		h.addPoint(100, -116, 60);
		h.addPoint(100, -28, 60);

		h.updateData();
		f.add(h);
		h.repaint();
		
		//show the main window
		f.pack();
		f.setVisible(true);
	}
	
	public static void fillDataWith(double[][] data, int value) {
		for (int x = 0; x<data.length;x++) {
			for (int y = 0; y<data[x].length;y++) {
				data[x][y] = value;
			}
		}
	}
	
	private int offsetX = 0;
	private int offsetY = 0;
	
	public static final double scaleFactor = 0.25; 
	
	public void updateData() {
		int maxX=0;
		int maxY=0;
		int minX=0;
		int minY=0;
		
		for(HeatmapPoint p: data) {
			maxX = Math.max(maxX, p.coordinates.x);
			maxY = Math.max(maxY, p.coordinates.y);
			minX = Math.min(minX, p.coordinates.x);
			minY = Math.min(minY, p.coordinates.y);
		}
		
		if (this.bounds!=null) {
			maxX = Math.max(maxX, this.bounds.width);
			maxY = Math.max(maxY, this.bounds.height);
		}
		
		this.setOffsetX(0);
		this.setOffsetY(0);
		
		if (minX<0) this.setOffsetX((-1)*minX);
		if (minY<0) this.setOffsetY((-1)*minY);
		
		this.setOffsetX(this.getOffsetX() + HeatmapPoint.DRAWINGRADIUS);
		this.setOffsetY(this.getOffsetY() + HeatmapPoint.DRAWINGRADIUS);
		
		int breiteX = this.getOffsetX()+maxX+1+HeatmapPoint.DRAWINGRADIUS;
		int breiteY = this.getOffsetY()+maxY+1+HeatmapPoint.DRAWINGRADIUS;
		
		double[][] newData = new double[(int) Math.ceil(breiteX*scaleFactor)][(int) Math.ceil(breiteY*scaleFactor)];
		
		//fillDataWith(newData, Gradient.GRADIENT_HEAT.length-1);
		for(HeatmapPoint p: data) {
			//newData[p.coordinates.x][p.coordinates.y] = p.strength;
			p.paintTo(newData, this.getOffsetX(), this.getOffsetY(), this.scaleFactor, this.currentVisualizationStyle);
		}
		super.updateData(newData, false);
	}
	
	Dimension bounds;

	private String currentVisualizationStyle;
	public void setMaximumBound(Dimension dimension) {
		this.bounds = dimension;
	}

	/**
	 * @param offsetX the offsetX to set
	 */
	private void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	/**
	 * @return the offsetX
	 */
	public int getOffsetX() {
		return offsetX;
	}

	/**
	 * @param offsetY the offsetY to set
	 */
	private void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	/**
	 * @return the offsetY
	 */
	public int getOffsetY() {
		return offsetY;
	}

	public void updateVisualizationStyle(String style) {
		this.currentVisualizationStyle = style;
	}
}
