package dataMapping;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * This class holds the structures for the color coded mapping of a VANESA PPI network
 * @author dborck
 *
 */
public class DataMappingModel {

//	private Map<String, String> identifiersValuesMap;
	private Map<String, ArrayList<String>> identifiersMultiValuesMap;


	private Map<String, String> queryResultMap;
	private Pathway pw;

	private Map<String, List<String>> mergeMap;
	private Map<String, List<String>> duplicated;

	private double[] coloringMinMax = {0,0};
	private boolean isLogarithmic = true;
	private double average = 0;
	private double maxAmplitude = 0;
	// a integer to calculate lower and upper borders around the average value
	// the higher the value, the minor nodes are colored in black, which means
	// there values are near by the average value, recommended is STEPS between 10 and 30
	private static final double STEPS = 20; 
	private double lowerBound = 0;
	private double upperBound = 0;
	private ArrayList<String> header;

	/**
	 * creates a new DataMappingModel and instantiates the mergeMap and the duplicatedMap for the duplicated
	 */
	public DataMappingModel() {
		mergeMap = new HashMap<String, List<String>>();
		duplicated = new HashMap<String, List<String>>();
	}

	/**
	 * merges the two maps (queryResultMap and identifiersValuesMap),
	 * stores the mergeMap and duplicated entries in a Map
	 * invokes the coloring of the pathway
	 */
	public void merge() {
		Map<String, List<String>> mergeMapL = new HashMap<String, List<String>>();
		Map<String, List<String>> duplicatedL = new HashMap<String, List<String>>();
		for (Entry<String, String> entry : queryResultMap.entrySet()) {
			String id = entry.getKey();
			if(identifiersMultiValuesMap.containsKey(id)) {
				// a new list for the input data
				List<String> mergeIdVal = new ArrayList<String>();
				// add the identifier and the values
				mergeIdVal.add(id);
				mergeIdVal.addAll(identifiersMultiValuesMap.get(id));
				
				List<String> duplicatedtmp = mergeMapL.put(entry.getValue(), mergeIdVal);
				// if duplicatedtmp is not null, there has already been inserted an object with the id
				if(duplicatedtmp != null) {
					if(duplicatedL.containsKey(entry.getValue())) {
						List<String> tmp = duplicatedL.get(entry.getValue());
						for(String duplic : duplicatedtmp){
							tmp.add(duplic);
						}
//						tmp.add(duplicatedtmp.get(0));
//						tmp.add(duplicatedtmp.get(1));
						duplicatedL.remove(entry.getValue());
						duplicatedL.put(entry.getValue(), tmp);
					} else {
						List<String> tmp = new ArrayList<String>();
						for(String duplic : duplicatedtmp){
							tmp.add(duplic);
						}
//						tmp.add(duplicatedtmp.get(0));
//						tmp.add(duplicatedtmp.get(1));
						duplicatedL.put(entry.getValue(), tmp);
					}
				}
			}
		}
		setMergeMap(mergeMapL);
		setDupMap(duplicatedL);
	}
	
	/**
	 * sets the Map with duplicated entries
	 * @param duplicated
	 */
	private void setDupMap(Map<String, List<String>> duplicated) {
		this.duplicated = duplicated;
	}

	/**
	 * updates the coloring of the pathway with a new mergeMap
	 * @param mergeMap - the new mergeMap
	 * @param newDupMap 
	 */
	public void updateColor(Map<String, List<String>> mergeMap, Map<String, List<String>> newDupMap) {
		setDupMap(newDupMap);
		setMergeMap(mergeMap);
		coloringPathway();
	}

	/**
	 * iterates over the nodes in the pathway and colors the matching nodes according to there values
	 * @param mergeMap - the merged map from the BioMart query and the data input
	 */
	public void coloringPathway() {
		Iterator<BiologicalNodeAbstract> allNodes = pw.getAllNodes().iterator();
		while (allNodes.hasNext()) {
			BiologicalNodeAbstract bna = allNodes.next();
			// decolor the nodes if they were highlighted before
			bna.setColor(Color.LIGHT_GRAY);
			// set Reference to false to be able to color KEGG Pathways
			bna.setReference(false);
			String labelTmp = bna.getLabel().toUpperCase();
			if (mergeMap.containsKey(labelTmp)) {// ignore nodes which are not part of the data input
				if((!mergeMap.get(labelTmp).get(1).equals("")) && (!mergeMap.get(labelTmp).get(1).equals("{#VALUE!}"))) {// ignore empty strings in the data input
					double value = 0;
					try {
						value = Double.parseDouble(mergeMap.get(labelTmp).get(1));
					} catch (NumberFormatException e) {
						// ignore the value if it is not in a correct format and do not color the node
						continue;	
					}
					int red = 0;
					int green = 0;
					int blue = 0;
					
					// linear color coding means, that maxAmplitude defines that value which have the brightest color,
					// if a value is higher(red) or lower(green) than maxAmplitude, the color will not change,
					// below(red) or above(green) the maxAmplitude the colors are graduated to dark(black)
					if (value >= lowerBound && value <= upperBound) {
						//do nothing, the nodes will be black, which means there is no change in FC
						//(black is the corresponding coding color of a non changing expression)
					} else if (value > upperBound) {
						//color coding from upperBound to maximum from dark red to light red
						if(!isLogarithmic) {
							red = (int) (255/(maxAmplitude - upperBound) * Math.abs(value) - (255*upperBound)/(maxAmplitude - upperBound)); // linear color coding between Bound and maxAmplitude
						} else if(isLogarithmic) {
							red = (int) (((Math.pow(2, Math.abs(value))-Math.pow(2, upperBound))*255)/(Math.pow(2, maxAmplitude)-Math.pow(2, upperBound)));
						}
						if (red > 255)
							red = 255;
					} else if (value < lowerBound) {
						//color coding from minimum to lowerBound from dark green to light green
						if (!isLogarithmic) {
							green = (int) (255/(maxAmplitude - 1/lowerBound) * 1/Math.abs(value) - (255*1/lowerBound)/(maxAmplitude - 1/lowerBound)); // linear color coding between Bound and maxAmplitude
						} else if (isLogarithmic) {
							green = (int) (((Math.pow(2, Math.abs(value))-Math.pow(2, upperBound))*255)/(Math.pow(2, maxAmplitude)-Math.pow(2, upperBound)));
						}
						if (green > 255)
							green = 255;
					}
					bna.setColor(new Color(red, green, blue));
					//System.out.println(bna.getLabel() + ";" + value + " Red: " + red + " Green: " + green + " Blue: " + blue);
				}
			}
		}
	}

	/**
	 * sets the map for the data input identifiers and values (e.g. accessions and foldchanges)
	 * @param map - the map of the data input
	 */
	public void setIdentifiersMultiValuesMap(Map<String, ArrayList<String>> map) {
		this.identifiersMultiValuesMap = map;
	}

	/**
	 * sets the map for the BioMart retrieval results (e.g. accesions and labels)
	 * @param resultMap - the map of the query retrieval
	 */
	public void setQueryResultMap(Map<String, String> resultMap) {
		this.queryResultMap = resultMap;
	}

	/**
	 * sets the selected pathway for the mapping
	 * @param selectedPathway - the selected VANESA pathway (e.g. from the user selected)
	 */
	public void setPathway(Pathway selectedPathway) {
		this.pw = selectedPathway;
	}

	/**
	 * sets the coloring parameters depending on minimal and maximal values and
	 * of the type of the values (foldchange (if values are greater or equal than null)
	 * or logarithmic foldchange values (if the values contain negative values)) 
	 */
	public void setColoringParameters() {
		double minValue = Double.MAX_VALUE;
		double maxValue = Double.MIN_VALUE;

		//filter null values
		ArrayList<String> toRemove = new ArrayList<>();
		for(Entry<String, ArrayList<String>> entry : identifiersMultiValuesMap.entrySet()){
			if(entry.getKey().length()<2)
				toRemove.add(entry.getKey());
		}
		
		for(String removekey : toRemove){			
			identifiersMultiValuesMap.remove(removekey);
		}		
		
		for (Entry<String, ArrayList<String>> entry : identifiersMultiValuesMap.entrySet()) {
		
			if(entry.getValue() != null) {
				double tmpValue = 0;
				try {
					tmpValue = Double.parseDouble(entry.getValue().get(0));
				} catch (Exception e) {
					// do nothing if the entry is not a number
					e.printStackTrace();
					continue;
				}
				if(tmpValue < minValue) {
					minValue = tmpValue;
				}
				if(tmpValue > maxValue) {
					maxValue = tmpValue;
				}
			}
		}
		coloringMinMax[0] = minValue;
		coloringMinMax[1] = maxValue;

		if(coloringMinMax[0] < 0) {
			// the values are logFC values
			isLogarithmic = true;
			average = 0;
			double minLog = coloringMinMax[0];
			double maxLog = coloringMinMax[1];
			maxAmplitude = Math.min(Math.abs(maxLog), Math.abs(minLog))/2; //for the brightest color
			// add to the average the difference between 2fold (logFC=1, FC=2) and the maxAmplitude, and take one "STEPS" from it
			upperBound = Math.log((STEPS + Math.pow(2, maxAmplitude) - 2)/STEPS)/Math.log(2);
			if (upperBound < average) {
				upperBound = (Math.log(2)/STEPS)/Math.log(2);
			}
			lowerBound = -1*upperBound;
		}
		if(coloringMinMax[0] >= 0) {
			// the values are FC values
			isLogarithmic = false;
			average = 1;
			double min = coloringMinMax[0];
			double max = coloringMinMax[1];
			maxAmplitude = Math.min(max, 1/min)/2; //for the brightest color
			// add to the average the difference between 2fold (logFC=1, FC=2) and the maxAmplitude, and take one "STEPS" from it
			upperBound = average + (maxAmplitude - 2)/STEPS;
			if (upperBound < average) {
				upperBound = average + 2/STEPS;
			}
			lowerBound = 1/upperBound;
		}
		//System.out.println("Lower: " + lowerBound + " Upper: " + upperBound + " maxAmp: " + maxAmplitude);
		
	}

	/**
	 * @return - the "average" of the input data, either 0 (for logFC data) or 1 (for FC data)
	 */
	public double getAverage() {
		return average;
	}

	/**
	 * @return - a maximum number beetween the "average" and the minimal or maximal value
	 */
	public double getMaxAmplitude() {
		return maxAmplitude;
	}

	/**
	 * @return - below this bound is the color coding of lower values,
	 * above (until upperBound) the color is BLACK
	 */
	public double getLowerBound() {
		return lowerBound;
	}

	/**
	 * @return - above this bound is the color coding of higher values,
	 * below (until lowerBound) the color is BLACK
	 */
	public double getUpperBound() {
		return upperBound;
	}

	/**
	 * @return - the merged map with the data for the color mapping
	 */
	public Map<String, List<String>> getMergeMap() {
		return mergeMap;
	}

	/**
	 * @return  - the minimal and maximal values of the data input
	 */
	public double[] getColoringMinMax() {
		return coloringMinMax;
	}
	
	/**
	 * @return - the Map with duplicated or uncolored entries
	 */
	public Map<String, List<String>> getDuplicatedList() {
		return duplicated;
	}
	
	/**
	 * sets the mergeMap from which the coloring of the pathway will be chosen
	 * @param mergeMap
	 */
	private void setMergeMap(Map<String, List<String>> mergeMap) {
		this.mergeMap = mergeMap;
	}
	
	public Map<String, ArrayList<String>>  getIdentifiersMultiValuesMap() {
		return identifiersMultiValuesMap;
	}

	public void setHeader(ArrayList<String> header) {
		this.header = header;	
	}

	public ArrayList<String> getHeader() {
		return header;
	}
	
}
