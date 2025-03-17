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
import graph.operations.layout.gem.GEMLayoutOperation;
import gui.MainWindow;

public class RandomBipartiteGraph {
	public static void generateRandomGraph(int numberOfNodesfirstSet, int numberOfNodesSecondSet, int numberOfEdges) {
		long seed = 1;
		int[] nodei = new int[numberOfEdges + 1];
		int[] nodej = new int[numberOfEdges + 1];
		Map<Integer, BiologicalNodeAbstract> nodes = new HashMap<>();
		GraphTheoryAlgorithms.randomBipartiteGraph(numberOfNodesfirstSet, numberOfNodesSecondSet, numberOfEdges, seed,
				nodei, nodej);
		Pathway pw = CreatePathway.create("Random Bipartite Graph");
		HashSet<Integer> set = new HashSet<>();
		for (int k = 1; k <= nodei[0]; k++) {
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
		pw.updateMyGraph();
		pw.getGraph2().apply(new GEMLayoutOperation());
		pw.getGraphRenderer().zoomAndCenterGraph();
		MainWindow window = MainWindow.getInstance();
		window.updateOptionPanel();
	}
}
