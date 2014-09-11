package graph.algorithms.gui;

import graph.ContainerSingelton;
import graph.GraphContainer;
import gui.MainWindow;
import gui.MainWindowSingleton;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
/**
 * 
 * @author mlewinsk
 * This class Applies a coloring theme to a given pathway/network.
 * The Input Hashtable maps Node-IDs (not jung 'vXXX') to a specific value which
 * will be translated to a color range (max-min).
 * The color range is given through a Bitmap.
 */
public class GraphColorizer {

	private double min, max, range, currentvalue, tmpvalue;
	private int imagewidth, imageindex;
	private Hashtable<BiologicalNodeAbstract,Double> nodevalues;
	private Map.Entry<BiologicalNodeAbstract,Double> nodevaluesentry;
	private Iterator<Map.Entry<BiologicalNodeAbstract,Double>> it;
	private BiologicalNodeAbstract currentnode;
	private BufferedImage rangesource;
	public static String rangeimage;
	
	public GraphColorizer(Hashtable<BiologicalNodeAbstract,Double> nodevalues, int rangeimage, boolean logarithmic){
		this.nodevalues = nodevalues;
		
		//Get image and assign the rangewidth
		try{
			switch (rangeimage) {
			case 0:
				rangesource = ImageIO.read(new File("src/gui/images/colorrange_bluesea.png"));
				break;
			case 1:		
				rangesource = ImageIO.read(new File("src/gui/images/colorrange_skyline.png"));
				break;
			case 2:				
				rangesource = ImageIO.read(new File("src/gui/images/colorrange_darkmiddle.png"));
				break;
			case 3:		
				rangesource = ImageIO.read(new File("src/gui/images/colorrange_dark.png"));
				break;
			case 4:		
				rangesource = ImageIO.read(new File("src/gui/images/colorrange_rainbow.png"));
				break;
			default:
				break;
			}
		}catch (IOException e){
			System.out.println("Error in GraphColorizer.java while reading image :"+rangeimage);
		}
			
//			rangesource = ImageIO.read(new File("pictures/colorrange_bluesea.png"));
//			rangesource = ImageIO.read(new File("pictures/colorrange_skyline.png"));
//			rangesource = ImageIO.read(new File("pictures/colorrange_darkmiddle.png"));
//			rangesource = ImageIO.read(new File("pictures/colorrange_dark.png"));
						
			imagewidth = rangesource.getWidth();			


		//do magic
		if(logarithmic)
			logDataSet();
		evaluateData();
		assignNodeColoring();		
		
	}

	
	//convert dataset to logarithmic scale, standard is linear
	private void logDataSet() {
		//create temporary hashtable and replace it with the old one after transformation
		Hashtable<BiologicalNodeAbstract,Double> tmptable = new Hashtable<BiologicalNodeAbstract,Double>();

		it = nodevalues.entrySet().iterator();
		while (it.hasNext()) {
			nodevaluesentry = it.next();
			currentnode = nodevaluesentry.getKey();
			currentvalue = nodevaluesentry.getValue();
			
			tmpvalue = ((imagewidth-1)/range)*(currentvalue-min);
		

			tmptable.put(currentnode, Math.log10(currentvalue));		
		}		
		
		nodevalues = tmptable;		
	}

	private void evaluateData() {
		min = Double.MAX_VALUE;
		max = Double.MIN_NORMAL;
 it = nodevalues.entrySet().iterator();
		
		while (it.hasNext()) {
			nodevaluesentry = it.next();
			currentvalue = nodevaluesentry.getValue();
			if (currentvalue > max)
				max = currentvalue;
			if (currentvalue < min)
				min = currentvalue;
		}	
		
		range = max-min;
	}


	private void assignNodeColoring() {
		it = nodevalues.entrySet().iterator();
				
		while (it.hasNext()) {
			nodevaluesentry = it.next();
			currentnode = nodevaluesentry.getKey();
			currentvalue = nodevaluesentry.getValue();
			
			tmpvalue = ((imagewidth-1)/range)*(currentvalue-min);
			imageindex = (int) tmpvalue;
				
			currentnode.setColor(new Color(rangesource.getRGB(imageindex, 0)));			
		}		
		
		
	}
}
