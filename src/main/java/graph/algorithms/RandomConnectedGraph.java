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

public class RandomConnectedGraph {
	public static void generateRandomGraph(final int numberOfNodes, final int numberOfEdges,
			final boolean weightedGraph, final int minimumWeight, final int maximumWeight) {
		final long seed = 1;
		final int[] nodei = new int[numberOfEdges + 1];
		final int[] nodej = new int[numberOfEdges + 1];
		final int[] weight = new int[numberOfEdges + 1];
		final Map<Integer, BiologicalNodeAbstract> nodes = new HashMap<>();
		int k = GraphTheoryAlgorithms.randomConnectedGraph(numberOfNodes, numberOfEdges, seed, weightedGraph,
				minimumWeight, maximumWeight, nodei, nodej, weight);
		if (k != 0)
			JOptionPane.showMessageDialog(null, "Error during initialising random graph. Parameters are wrong!",
					"Error Message", JOptionPane.INFORMATION_MESSAGE);
		else {
			final Pathway pw = CreatePathway.create("Random Connected Graph");
			final MyGraph myGraph = pw.getGraph();
			final HashSet<Integer> set = new HashSet<>();
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
				final ReactionEdge r = new ReactionEdge("", "", nodes.get(nodei[k]), nodes.get(nodej[k]));
				r.setDirected(false);
				r.setVisible(true);
				if (weightedGraph) {
					r.setFunction(weight[k] + "");
				}
				pw.addEdge(r);
			}
			myGraph.restartVisualizationModel();
			myGraph.normalCentering();
			MainWindow.getInstance().updateOptionPanel();
			pw.getGraph().changeToGEMLayout();
		}
	}
}
