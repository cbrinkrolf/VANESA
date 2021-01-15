package graph.algorithms;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JOptionPane;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Other;
import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;

public class RandomGraph {

	public RandomGraph() {
	}

	public static void generateRandomGraph(int numberOfNodes,
			int numberOfEdges, boolean simpleGraph, boolean directedGraph,
			boolean acyclicGraph, boolean weightedGraph, int minimumWeight,
			int maximumWeigth) {

		int k;
		int n = numberOfNodes;
		int m = numberOfEdges;
		long seed = 1;
		boolean simple = simpleGraph, directed = directedGraph, acyclic = acyclicGraph, weighted = weightedGraph;
		if (acyclic)
			directed = true;
		int minweight = minimumWeight;
		int maxweight = maximumWeigth;
		int nodei[] = new int[m + 1];
		int nodej[] = new int[m + 1];
		int weight[] = new int[m + 1];
		Map<Integer, BiologicalNodeAbstract> nodes = new HashMap<Integer, BiologicalNodeAbstract>();

		k = GraphTheoryAlgorithms.randomGraph(n, m, seed, simple, directed,
				acyclic, weighted, minweight, maxweight, nodei, nodej, weight);
		if (k != 0)
			JOptionPane
					.showMessageDialog(
							null,
							"Error during initialising random graph. Parameters are wrong!",
							"Error Message", 1);
		else {
			Pathway pw = new CreatePathway("Random Graph").getPathway();
			MyGraph myGraph = pw.getGraph();

			HashSet<Integer> set = new HashSet<Integer>();

			int nodeNumberCounter = 0;

			Other node;
			
			for (k = 1; k <= m; k++) {

				if (!set.contains(nodei[k])) {
					set.add(nodei[k]);
					node = new Other(nodei[k] + "", nodei[k] + "");
					pw.addVertex(node, new Point(150, 100));
					nodes.put(nodei[k], node);
					// myGraph.moveVertex(node, 150, 100);
					nodeNumberCounter++;

				}

				if (!set.contains(nodej[k])) {
					set.add(nodej[k]);
					node = new Other(nodej[k] + "", nodej[k] + "");
					pw.addVertex(node, new Point(150, 100));
					nodes.put(nodej[k], node);
					// myGraph.moveVertex(node.getVertex(), 150, 100);
					nodeNumberCounter++;
				}

			}
			for (k = 0; k < n; k++) {
				if (nodeNumberCounter < n) {
					if (!set.contains(k)) {
						set.add(k);
						node = new Other(k + "", k + "");
						pw.addVertex(node, new Point(150, 100));
						nodes.put(k, node);
						// myGraph.moveVertex(node.getVertex(), 150, 100);
						nodeNumberCounter++;
					}
				}
			}
			for (k = 1; k <= m; k++) {

				if (directed) {
					ReactionEdge r = new ReactionEdge("", "",
							nodes.get(nodei[k]), nodes.get(nodej[k]));// myGraph.createEdge(pw
					// .getNodeByName(nodei[k] + "").getVertex(), pw
					// .getNodeByName(nodej[k] + "").getVertex(), true),
					// "", "");

					r.setDirected(true);
					r.setVisible(true);

					if (weightedGraph) {
						//r.setWeighted(true);
						r.setFunction(weight[k]+"");
					}

					pw.addEdge(r);

				} else {
					//System.out.println("test");
					ReactionEdge r = new ReactionEdge("", "",
							nodes.get(nodei[k]), nodes.get(nodej[k]));
					// ReactionEdge r = new ReactionEdge(myGraph.createEdge(pw
					// .getNodeByName(nodei[k] + "").getVertex(), pw
					// .getNodeByName(nodej[k] + "").getVertex(), false),
					// "", "");

					r.setDirected(false);
					r.setVisible(true);

					if (weightedGraph) {
						//r.setWeighted(true);
						r.setFunction(weight[k]+"");
					}

					pw.addEdge(r);
				}
			}

			 myGraph.restartVisualizationModel();

			//MainWindow window = MainWindowSingelton.getInstance();
			//window.updateOptionPanel();
			//window.enable(true);
			 
			pw.getGraph().changeToGEMLayout();
			//System.out.println("drin");
			pw.getGraph().normalCentering();
			
			MainWindow.getInstance().updateAllGuiElements();
			MainWindow.getInstance().enable(true);
			//myGraph.getVisualizationViewer().repaint();
		}
	}
}
