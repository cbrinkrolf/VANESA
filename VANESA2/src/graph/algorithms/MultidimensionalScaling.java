package graph.algorithms;

import java.awt.Point;
import java.util.*;
import java.util.stream.Collectors;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingleton;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import mdsj.MDSJ;

public class MultidimensionalScaling {
	private MainWindow w = MainWindowSingleton.getInstance();
	private GraphContainer con = ContainerSingelton.getInstance();
	private Pathway pw;
	private MyGraph mg;
	private int nodes, dimensions = 2, scaling = 1024; //scaling in pixels x*y / 1000*1000
	double[][] dissim, output;
	double[] mins, maxes;
	ArrayList<Double> expvalues;

	Hashtable<BiologicalNodeAbstract, Integer> nodeassings = new Hashtable<BiologicalNodeAbstract, Integer>();
	Hashtable<Integer, BiologicalNodeAbstract> nodeassignsback = new Hashtable<Integer, BiologicalNodeAbstract>();
	private int nodeincrement = 0;

	public MultidimensionalScaling(String nodeAttributeName) {

		pw = con.getPathway(w.getCurrentPathway());
		mg = pw.getGraph();

		nodes = mg.getAllVertices().size();

		dissim = new double[nodes][nodes];
		expvalues = new ArrayList<>(nodes);

		NodeAttribute attribute;

		// Induce mapping to local adjacency matrix and data structures (BNA.ID)
		Iterator<BiologicalNodeAbstract> it = mg.getAllVertices().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = it.next();
			reassignNodeBNA(bna);
			attribute = bna.getNodeAttributeByName(nodeAttributeName);
			if (attribute != null) {
				expvalues.add(attribute.getDoublevalue());
			} else {
				expvalues.add(Math.random()/33.0d);
			}
		}

//		System.out.println(expvalues);

		DoubleSummaryStatistics stats = expvalues.stream().collect(
				Collectors.summarizingDouble(Double::doubleValue));

//		System.out.println(stats);

		// calcdissim
		for (int i = 0; i < dissim.length; i++) {
			for (int j = i + 1; j < dissim.length; j++) {
				dissim[i][j] = Math.sqrt(getDissimilarity(expvalues.get(i),
						expvalues.get(j), stats.getMin()));
				dissim[j][i] = dissim[i][j];
			}
		}

		// DEBUG
//		printDissimilarityMatrix();
		System.out.println();

//		output = MDSJ.classicalScaling(dissim, dimensions);
		output = MDSJ.stressMinimization(dissim, dimensions);

		
//		printOutputMatrix();

		
		mins = new double[dimensions];
		maxes = new double[dimensions];
		
		Arrays.setAll(mins, d -> Double.MAX_VALUE);
		Arrays.setAll(maxes, d -> Double.MIN_VALUE);
		
		double currentdouble;
		//get min and max dimensions
		for(int x = 0; x < dimensions; x++){
			for(int y = 0; y < nodes; y++){
				currentdouble = output[x][y] ;
				if(currentdouble > maxes[x]){
					maxes[x] = currentdouble  ;
				}
				if(currentdouble  < mins[x]){
					mins[x] = currentdouble  ;
				}
			}
		}
		
		
		
		System.out.println(Arrays.toString(mins));
		System.out.println(Arrays.toString(maxes));
		
		
		realignNetwork();
	}

	private void realignNetwork() {
		
		//scale
		for (int i = 0; i < nodes; i++) {
			output[0][i] = (output[0][i])*scaling;
			output[1][i]= (output[1][i])*scaling;
		}
//		System.out.println("--- trans ---");
//		printOutputMatrix();
		
		//check for multiple nodes on same coordinates
		int duplicatecoords = 0, x1, x2, y1, y2;
		for (int i = 0; i < nodes; i++) {
			for (int j = i+1; j < nodes; j++) {
				x1 = (int) output[0][i];
				x2 = (int) output[0][j];
				
				y1 = (int) output[1][i];
				y2 = (int) output[1][j];
				
				if(x1 == x2 && y1 == y2)
					duplicatecoords++;				
			}			
		}		
		System.out.println("piled nodes "+duplicatecoords);
		
		
		for (int node = 0; node < nodes; node++) {
			mg.getVisualizationViewer()
					.getModel()
					.getGraphLayout()
					.setLocation(
							nodeassignsback.get(node),
							new Point((int) (output[0][node]+1.5),
									(int) (output[1][node]+1.5)));
		}
	}

	private double getDissimilarity(Double d1, Double d2, Double min) {
//		return (Double.max(d1, d2) + min) - (Double.min(d1, d2) + min);
		//EUCL
		return Math.sqrt(Math.pow((Double.max(d1, d2) + min) - (Double.min(d1, d2) + min),2));
//System.out.println(Math.pow(8,1.0/3.0));
		
		
//		return Math.pow(Math.pow((Double.max(d1, d2) + min) - (Double.min(d1, d2) + min),12),1.0/12.0);
//		return Math.sqrt((Double.max(d1, d2) + min) - (Double.min(d1, d2) + min));

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
}
