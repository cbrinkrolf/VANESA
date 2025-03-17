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
	public static void generateRandomGraph(int numberOfNodes, int numberOfEdges, boolean simpleGraph,
			boolean directedGraph, boolean acyclicGraph, boolean weightedGraph, int minimumWeight, int maximumWeigth) {
		long seed = 1;
		boolean directed = directedGraph;
		if (acyclicGraph) {
			directed = true;
		}
		int[] nodei = new int[numberOfEdges + 1];
		int[] nodej = new int[numberOfEdges + 1];
		int[] weight = new int[numberOfEdges + 1];
		Map<Integer, BiologicalNodeAbstract> nodes = new HashMap<>();
		int k = GraphTheoryAlgorithms.randomGraph(numberOfNodes, numberOfEdges, seed, simpleGraph, directed,
				acyclicGraph, weightedGraph, minimumWeight, maximumWeigth, nodei, nodej, weight);
		if (k != 0)
			JOptionPane.showMessageDialog(null, "Error during initialising random graph. Parameters are wrong!",
					"Error Message", JOptionPane.INFORMATION_MESSAGE);
		else {
			Pathway pw = CreatePathway.create("Random Graph");
			MyGraph myGraph = pw.getGraph();
			HashSet<Integer> set = new HashSet<>();
			int nodeNumberCounter = 0;
			Other node;
			for (k = 1; k <= numberOfEdges; k++) {
				if (!set.contains(nodei[k])) {
					set.add(nodei[k]);
					node = new Other(nodei[k] + "", nodei[k] + "");
					pw.addVertex(node, new Point(150, 100));
					nodes.put(nodei[k], node);
					nodeNumberCounter++;
				}
				if (!set.contains(nodej[k])) {
					set.add(nodej[k]);
					node = new Other(nodej[k] + "", nodej[k] + "");
					pw.addVertex(node, new Point(150, 100));
					nodes.put(nodej[k], node);
					nodeNumberCounter++;
				}
			}
			for (k = 0; k < numberOfNodes; k++) {
				if (nodeNumberCounter < numberOfNodes) {
					if (!set.contains(k)) {
						set.add(k);
						node = new Other(k + "", k + "");
						pw.addVertex(node, new Point(150, 100));
						nodes.put(k, node);
						nodeNumberCounter++;
					}
				}
			}
			for (k = 1; k <= numberOfEdges; k++) {
				if (directed) {
					ReactionEdge r = new ReactionEdge("", "", nodes.get(nodei[k]), nodes.get(nodej[k]));
					r.setDirected(true);
					r.setVisible(true);
					if (weightedGraph) {
						r.setFunction(weight[k] + "");
					}
					pw.addEdge(r);
				} else {
					ReactionEdge r = new ReactionEdge("", "", nodes.get(nodei[k]), nodes.get(nodej[k]));
					r.setDirected(false);
					r.setVisible(true);
					if (weightedGraph) {
						r.setFunction(weight[k] + "");
					}
					pw.addEdge(r);
				}
			}
			myGraph.restartVisualizationModel();
			pw.getGraph().changeToGEMLayout();
			pw.getGraph().normalCentering();
			MainWindow.getInstance().updateAllGuiElements();
			MainWindow.getInstance().getFrame().setEnabled(true);
		}
	}
}
