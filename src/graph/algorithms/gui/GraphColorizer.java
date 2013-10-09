package graph.algorithms.gui;

import graph.ContainerSingelton;
import graph.GraphContainer;
import gui.MainWindow;
import gui.MainWindowSingelton;

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
	
	public GraphColorizer(Hashtable<BiologicalNodeAbstract,Double> nodevalues, String rangeimagepath, boolean logarithmic){
		this.nodevalues = nodevalues;
		
		try {//Get image and assign the rangewidth
			rangesource = ImageIO.read(new File(rangeimagepath));
			
//			rangesource = ImageIO.read(new File("pictures/colorrange_bluesea.png"));
//			rangesource = ImageIO.read(new File("pictures/colorrange_skyline.png"));
//			rangesource = ImageIO.read(new File("pictures/colorrange_darkmiddle.png"));
//			rangesource = ImageIO.read(new File("pictures/colorrange_dark.png"));
						
			imagewidth = rangesource.getWidth();			
			
			
		} catch (IOException e) {
			System.out.println("Error in "+getClass()+" \nreading File.");
		}

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
