package graph.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.algorithms.gui.NodeAttributeBarChart;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import mdsj.MDSJ;

public class MultidimensionalScaling {
	private MainWindow w = MainWindow.getInstance();
	private GraphContainer con = ContainerSingelton.getInstance();
	private Pathway pw;
	private MyGraph mg;
	private int nodes, dimensions = 2, scaling = 1024; // scaling in pixels x*y
														// / 1000*1000
	double[][] dissim, output;
	double[] mins, maxes;
	ArrayList<Double> expvalues;
	ArrayList<String> txtvalues;

	Hashtable<BiologicalNodeAbstract, Integer> nodeassings = new Hashtable<BiologicalNodeAbstract, Integer>();
	Hashtable<Integer, BiologicalNodeAbstract> nodeassignsback = new Hashtable<Integer, BiologicalNodeAbstract>();
	private int nodeincrement = 0;

	/**
	 * Constructor for Node-attribute data
	 * 
	 * @param nodeAttributeName
	 *            - Name of the attribute Name having double value attributes
	 * @param string - true -> nodeattributes are text based, false -> attributes are numbers(doubles) 
	 */
	public MultidimensionalScaling(String nodeAttributeName, boolean string) {

		pw = con.getPathway(w.getCurrentPathway());
		mg = pw.getGraph();

		nodes = mg.getAllVertices().size();

		dissim = new double[nodes][nodes];

		if (!string) {
			expvalues = new ArrayList<>(nodes);

			NodeAttribute attribute;

			// Induce mapping to local adjacency matrix and data structures
			// (BNA.ID)
			Iterator<BiologicalNodeAbstract> it = mg.getAllVertices()
					.iterator();
			while (it.hasNext()) {
				BiologicalNodeAbstract bna = it.next();
				reassignNodeBNA(bna);
				attribute = bna.getNodeAttributeByName(nodeAttributeName);
				if (attribute != null) {
					expvalues.add(attribute.getDoublevalue());
				} else {
					expvalues.add(Math.random() / 33.0d);
				}
			}

			// System.out.println(expvalues);
			DoubleSummaryStatistics stats = expvalues.stream().collect(
					Collectors.summarizingDouble(Double::doubleValue));
			// System.out.println(stats);
			// calcdissim
			for (int i = 0; i < dissim.length; i++) {
				for (int j = i + 1; j < dissim.length; j++) {
					dissim[i][j] = Math.sqrt(getDissimilarity(expvalues.get(i),
							expvalues.get(j), stats.getMin()));
					dissim[j][i] = dissim[i][j];
				}
			}
			// DEBUG
			// printDissimilarityMatrix();
			// output = MDSJ.classicalScaling(dissim, dimensions);
			output = MDSJ.stressMinimization(dissim, dimensions);
			// printOutputMatrix();

			mins = new double[dimensions];
			maxes = new double[dimensions];
			Arrays.setAll(mins, d -> Double.MAX_VALUE);
			Arrays.setAll(maxes, d -> Double.MIN_VALUE);
			double currentdouble;
			// get min and max dimensions
			for (int x = 0; x < dimensions; x++) {
				for (int y = 0; y < nodes; y++) {
					currentdouble = output[x][y];
					if (currentdouble > maxes[x]) {
						maxes[x] = currentdouble;
					}
					if (currentdouble < mins[x]) {
						mins[x] = currentdouble;
					}
				}
			}

			// DEBUG
			// System.out.println(Arrays.toString(mins));
			// System.out.println(Arrays.toString(maxes));

			
			//MDS is textbased
		} else {
			
			//determine statistics
			txtvalues = new ArrayList<>(nodes);
			HashMap<String,Integer> occur = new HashMap<>(nodes);

			Iterator<BiologicalNodeAbstract> it = mg.getAllVertices()
					.iterator();
			while (it.hasNext()) {
				BiologicalNodeAbstract bna = it.next();
				reassignNodeBNA(bna);
				for(NodeAttribute na : bna.getNodeAttributes()){

					if(na.getName().equals(nodeAttributeName)){
						if(!occur.containsKey(na.getStringvalue()))
							occur.put(na.getStringvalue(), 1);
						else
							occur.put(na.getStringvalue(), occur.get(na.getStringvalue())+1);
					}
				}
			}
			
			//filter by occurrence
			HashMap<String,Integer> occurfilter = new HashMap<>();
			
			for(Entry<String, Integer> e : occur.entrySet()){
				if(e.getValue()>100)
					occurfilter.put(e.getKey(), e.getValue());
			}
			
			
			
			
			
//			System.out.println(entriesSortedByValues(occur));

			//display sorted values
			
			new NodeAttributeBarChart("Statistics",nodeAttributeName,"X","Y",occurfilter);
			
			
			ArrayList<BiologicalNodeAbstract> nodes = new ArrayList<>();
			nodes.addAll(mg.getAllVertices());
			for (int i = 0; i < nodes.size(); i++) {
				BiologicalNodeAbstract bnai = nodes.get(i);
				for (int j = i+1; j < nodes.size(); j++) {
					BiologicalNodeAbstract bnaj = nodes.get(j);
					for(NodeAttribute na : bnai.getNodeAttributes()){
						for (NodeAttribute nb : bnaj.getNodeAttributes()) {
							if(na.getName().equals(nodeAttributeName)){
								if(nb.getName().equals(nodeAttributeName)){
									if(na.getStringvalue().equals(nb.getStringvalue())){
										dissim[i][j]+= (Math.random()/10.0d)+2; 
										dissim[j][i] = dissim[i][j]; 
									}
								}	
							}	
						}					
					}
				}
			}

//			output = MDSJ.stressMinimization(dissim, dimensions);
					

		}
		realignNetwork();
	}
	

	private void realignNetwork() {

		// scale
		for (int i = 0; i < nodes; i++) {
			output[0][i] = (output[0][i]) * scaling;
			output[1][i] = (output[1][i]) * scaling;
		}
		// System.out.println("--- trans ---");
		// printOutputMatrix();

		// check for multiple nodes on same coordinates
		int duplicatecoords = 0, x1, x2, y1, y2;
		for (int i = 0; i < nodes; i++) {
			for (int j = i + 1; j < nodes; j++) {
				x1 = (int) output[0][i];
				x2 = (int) output[0][j];

				y1 = (int) output[1][i];
				y2 = (int) output[1][j];

				if (x1 == x2 && y1 == y2)
					duplicatecoords++;
			}
		}
		// System.out.println("piled nodes "+duplicatecoords);

		for (int node = 0; node < nodes; node++) {
			mg.getVisualizationViewer()
					.getModel()
					.getGraphLayout()
					.setLocation(
							nodeassignsback.get(node),
							new Point((int) (output[0][node] + 1.5),
									(int) (output[1][node] + 1.5)));
		}
	}

	private double getDissimilarity(Double d1, Double d2, Double min) {
		// return (Double.max(d1, d2) + min) - (Double.min(d1, d2) + min);
		// EUCL
		return Math.sqrt(Math.pow(
				(Double.max(d1, d2) + min) - (Double.min(d1, d2) + min), 2));
		// System.out.println(Math.pow(8,1.0/3.0));

		// return Math.pow(Math.pow((Double.max(d1, d2) + min) - (Double.min(d1,
		// d2) + min),12),1.0/12.0);
		// return Math.sqrt((Double.max(d1, d2) + min) - (Double.min(d1, d2) +
		// min));

	}

	private void printDissimilarityMatrix() {
		for (int i = 0; i < dissim.length; i++) {
			for (int j = 0; j < dissim.length; j++) {
				System.out.printf("%.3f\t", dissim[i][j]);
			}
			System.out.println();
		}
	}

	private void printOutputMatrix() {
		for (int i = 0; i < dimensions; i++) {
			for (int j = 0; j < nodes; j++) {
				System.out.printf("%.3f\t", output[i][j]);
			}
			System.out.println();
		}
	}

	private void reassignNodeBNA(BiologicalNodeAbstract nodeBNA) {
		if (!nodeassings.containsKey(nodeBNA)) {
			nodeassings.put(nodeBNA, nodeincrement);
			nodeassignsback.put(nodeincrement, nodeBNA);
			nodeincrement++;
		}
	}

	/**
	 * Sort method for values
	 * @param map
	 * @return
	 */
	static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(
			Map<K, V> map) {

		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(
				map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
	}

}
