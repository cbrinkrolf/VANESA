package graph.algorithms;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Other;
import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;

public class RandomRegularGraph {
	public void generateRandomRegularGraph(int numberOfNodes, int degree) {
		long seed = 1;
		int edges = (numberOfNodes * degree) / 2;
		int[] nodei = new int[edges + 1];
		int[] nodej = new int[edges + 1];
		int k = GraphTheoryAlgorithms.randomRegularGraph(numberOfNodes, degree, seed, nodei, nodej);
		Pathway pw = CreatePathway.create("Random Regular Graph");
		MyGraph myGraph = pw.getGraph();
		HashSet<Integer> set = new HashSet<>();
		Map<Integer, BiologicalNodeAbstract> nodes = new HashMap<>();
		if (k == 0) {
			for (k = 1; k <= edges; k++) {
				if (!set.contains(nodei[k])) {
					set.add(nodei[k]);
					Other node = new Other(nodei[k] + "", nodei[k] + "");
					pw.addVertex(node, new Point(150, 100));
					nodes.put(nodei[k], node);
				}
				if (!set.contains(nodej[k])) {
					set.add(nodej[k]);
					Other node = new Other(nodej[k] + "", nodej[k] + "");
					pw.addVertex(node, new Point(150, 100));
					nodes.put(nodej[k], node);
				}
				ReactionEdge r = new ReactionEdge("", "", nodes.get(nodei[k]), nodes.get(nodej[k]));
				r.setDirected(false);
				r.setVisible(true);
				pw.addEdge(r);
			}
		}
		myGraph.restartVisualizationModel();
		myGraph.normalCentering();
		MainWindow window = MainWindow.getInstance();
		window.updateOptionPanel();
		pw.getGraph().changeToGEMLayout();
	}
}
