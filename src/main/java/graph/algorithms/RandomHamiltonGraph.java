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
import graph.operations.layout.gem.GEMLayoutOperation;
import gui.MainWindow;

public class RandomHamiltonGraph {

	public RandomHamiltonGraph() {
	}

	public static void generateRandomGraph(int numberOfNodes, int numberOfEdges, boolean directedGraph,
			boolean weightedGraph, int minimumWeight, int maximumWeight) {
		long seed = 1;
		int[] nodei = new int[numberOfEdges + 1];
		int[] nodej = new int[numberOfEdges + 1];
		int[] weight = new int[numberOfEdges + 1];
		int k = GraphTheoryAlgorithms.randomHamiltonGraph(numberOfNodes, numberOfEdges, seed, directedGraph,
				weightedGraph, minimumWeight, maximumWeight, nodei, nodej, weight);
		if (k != 0)
			JOptionPane.showMessageDialog(null, "Error during initialising random graph. Parameters are wrong!",
					"Error Message", JOptionPane.INFORMATION_MESSAGE);
		else {
			Pathway pw = CreatePathway.create("Random Hamilton Graph");
			HashSet<Integer> set = new HashSet<>();
			Map<Integer, BiologicalNodeAbstract> nodes = new HashMap<>();
			int nodeNumberCounter = 0;
			for (k = 1; k <= numberOfEdges; k++) {
				if (!set.contains(nodei[k])) {
					set.add(nodei[k]);
					Other node = new Other(nodei[k] + "", nodei[k] + "");
					pw.addVertex(node, new Point(150, 100));
					nodes.put(nodei[k], node);
					nodeNumberCounter++;
				}
				if (!set.contains(nodej[k])) {
					set.add(nodej[k]);
					Other node = new Other(nodej[k] + "", nodej[k] + "");
					pw.addVertex(node, new Point(150, 100));
					nodes.put(nodej[k], node);
					nodeNumberCounter++;
				}
			}
			for (k = 0; k < numberOfNodes; k++) {
				if (nodeNumberCounter < numberOfNodes) {
					if (!set.contains(k)) {
						set.add(k);
						Other node = new Other(k + "", k + "");
						pw.addVertex(node, new Point(150, 100));
						nodes.put(k, node);
						nodeNumberCounter++;
					}
				}
			}
			for (k = 1; k <= numberOfEdges; k++) {
				ReactionEdge r = new ReactionEdge("", "", nodes.get(nodei[k]), nodes.get(nodej[k]));
				r.setDirected(directedGraph);
				r.setVisible(true);
				if (weightedGraph) {
					r.setFunction(weight[k] + "");
				}
				pw.addEdge(r);
			}
			pw.updateMyGraph();
			pw.getGraph2().apply(new GEMLayoutOperation());
			pw.getGraphRenderer().zoomAndCenterGraph();
			MainWindow window = MainWindow.getInstance();
			window.updateOptionPanel();
		}
	}
}
