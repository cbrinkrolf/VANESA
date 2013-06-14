package graph.algorithms;

import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.util.HashSet;

import javax.swing.JOptionPane;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.Other;

public class RandomConnectedGraph {

	public RandomConnectedGraph() {
		// TODO Auto-generated constructor stub
	}

	public static void generateRandomGraph(int numberOfNodes,
			int numberOfEdges, boolean weightedGraph, int minimumWeight,
			int maximumWeigth) {

		int k;
		int n = numberOfNodes;
		int m = numberOfEdges;
		long seed = 1;
		boolean weighted = weightedGraph;
		int minweight = minimumWeight;
		int maxweight = maximumWeigth;
		int nodei[] = new int[m + 1];
		int nodej[] = new int[m + 1];
		int weight[] = new int[m + 1];

		k = GraphTheoryAlgorithms.randomConnectedGraph(n, m, seed, weighted,
				minweight, maxweight, nodei, nodej, weight);
		if (k != 0)
			JOptionPane
					.showMessageDialog(
							null,
							"Error during initialising random graph. Parameters are wrong!",
							"Error Message", 1);
		else {

			Pathway pw = new CreatePathway("Random Connected Graph")
					.getPathway();
			MyGraph myGraph = pw.getGraph();

			myGraph.lockVertices();
			myGraph.stopVisualizationModel();
			HashSet set = new HashSet();

			int nodeNumberCounter = 0;

			for (k = 1; k <= m; k++) {

				if (!set.contains(nodei[k])) {
					set.add(nodei[k]);
					Other node = new Other(nodei[k] + "", nodei[k] + "",
							myGraph.createNewVertex());
					node.setReference(false);
					pw.addElement(node);
					myGraph.moveVertex(node.getVertex(), 150, 100);
					nodeNumberCounter++;

				}

				if (!set.contains(nodej[k])) {
					set.add(nodej[k]);
					Other node = new Other(nodej[k] + "", nodej[k] + "",
							myGraph.createNewVertex());
					node.setReference(false);
					pw.addElement(node);
					myGraph.moveVertex(node.getVertex(), 150, 100);
					nodeNumberCounter++;
				}

			}
			for (k = 0; k < n; k++) {
				if (nodeNumberCounter < n) {
					if (!set.contains(k)) {
						set.add(k);
						Other node = new Other(k + "", k + "",
								myGraph.createNewVertex());
						node.setReference(false);
						pw.addElement(node);
						myGraph.moveVertex(node.getVertex(), 150, 100);
						nodeNumberCounter++;
					}
				}
			}
			for (k = 1; k <= m; k++) {

				ReactionEdge r = new ReactionEdge(myGraph.createEdge(pw
						.getNodeByName(nodei[k] + "").getVertex(), pw
						.getNodeByName(nodej[k] + "").getVertex(), false), "",
						"");

				r.setDirected(false);
				r.setReference(false);
				r.setHidden(false);
				r.setVisible(true);

				if (weightedGraph) {
					r.setWeighted(true);
					r.setWeight(weight[k]);
				}

				pw.addElement(r);

			}

			myGraph.unlockVertices();
			myGraph.restartVisualizationModel();

			myGraph.normalCentering();

			MainWindow window = MainWindowSingelton.getInstance();
			window.updateOptionPanel();
			window.enable(true);
			pw.getGraph().changeToGEMLayout();
		}
	}
}
