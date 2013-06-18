package graph.algorithms;

import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JOptionPane;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Other;

public class RandomGraph {

	public RandomGraph() {
		// TODO Auto-generated constructor stub
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

			myGraph.lockVertices();
			myGraph.stopVisualizationModel();
			HashSet<Integer> set = new HashSet<Integer>();

			int nodeNumberCounter = 0;

			for (k = 1; k <= m; k++) {

				if (!set.contains(nodei[k])) {
					set.add(nodei[k]);
					Other node = new Other(nodei[k] + "", nodei[k] + "");
					node.setReference(false);
					pw.addVertex(node, new Point(150, 100));
					nodes.put(nodei[k], node);
					// myGraph.moveVertex(node, 150, 100);
					nodeNumberCounter++;

				}

				if (!set.contains(nodej[k])) {
					set.add(nodej[k]);
					Other node = new Other(nodej[k] + "", nodej[k] + "");
					node.setReference(false);
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
						Other node = new Other(k + "", k + "");
						node.setReference(false);
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
					r.setReference(false);
					r.setHidden(false);
					r.setVisible(true);

					if (weightedGraph) {
						r.setWeighted(true);
						r.setWeight(weight[k]);
					}

					pw.addEdge(r);

				} else {
					ReactionEdge r = new ReactionEdge("", "",
							nodes.get(nodei[k]), nodes.get(nodej[k]));
					// ReactionEdge r = new ReactionEdge(myGraph.createEdge(pw
					// .getNodeByName(nodei[k] + "").getVertex(), pw
					// .getNodeByName(nodej[k] + "").getVertex(), false),
					// "", "");

					r.setDirected(false);
					r.setReference(false);
					r.setHidden(false);
					r.setVisible(true);
					r.setAbstract(true);

					if (weightedGraph) {
						r.setWeighted(true);
						r.setWeight(weight[k]);
					}

					pw.addEdge(r);
				}
			}

			// myGraph.unlockVertices();
			// myGraph.restartVisualizationModel();

			MainWindow window = MainWindowSingelton.getInstance();
			// TODO
			 window.updateOptionPanel();
			window.enable(true);
			pw.getGraph().changeToGEMLayout();
			// myGraph.normalCentering();
		}
	}
}
