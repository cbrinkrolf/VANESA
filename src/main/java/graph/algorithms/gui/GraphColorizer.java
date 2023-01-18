package graph.algorithms.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import gui.images.ImagePath;
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
		
		// Get image and assign the rangewidth
		ImagePath imagepath = ImagePath.getInstance();
		ImageIcon ii = new ImageIcon();
		Image img;

		switch (rangeimage) {
		case 0:
			ii = new ImageIcon(imagepath.getPath("colorrange_bluesea.png"));
			break;
		case 1:
			ii = new ImageIcon(imagepath.getPath("colorrange_skyline.png"));
			break;
		case 2:
			ii = new ImageIcon(imagepath.getPath("colorrange_darkmiddle.png"));
			break;
		case 3:
			ii = new ImageIcon(imagepath.getPath("colorrange_dark.png"));
			break;
		case 4:
			ii = new ImageIcon(imagepath.getPath("colorrange_rainbow.png"));
			break;
		default:
			ii = new ImageIcon(imagepath.getPath("colorrange_bluesea.png"));
			break;
		}

		//convert to image and transfer the buffered Image
		//This step is needed to access the RGB code at a specified position
		//The Image Icon class does not support this feature
		img = ii.getImage();
		rangesource = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		// Draw the image on to the buffered image
		Graphics2D bGr = rangesource.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		
		
		
		imagewidth = rangesource.getWidth(null);			


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
