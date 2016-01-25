package graph.algorithms;

import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingleton;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Other;

public class RandomBipartiteGraph {

	public RandomBipartiteGraph() {
		// TODO Auto-generated constructor stub
	}

	public static void generateRandomGraph(int numberOfNodesfirstSet,
			int numberOfNodesSecondSet, int numberOfEdges) {

		int n1 = numberOfNodesfirstSet;
		int n2 = numberOfNodesSecondSet;
		int m = numberOfEdges;
		long seed = 1;
		int nodei[] = new int[m + 1];
		int nodej[] = new int[m + 1];
		Map<Integer, BiologicalNodeAbstract> nodes = new HashMap<Integer, BiologicalNodeAbstract>();

		GraphTheoryAlgorithms.randomBipartiteGraph(n1, n2, m, seed, nodei,
				nodej);

		Pathway pw = new CreatePathway("Random Bipartite Graph").getPathway();
		MyGraph myGraph = pw.getGraph();

		HashSet<Integer> set = new HashSet<Integer>();

		for (int k = 1; k <= nodei[0]; k++) {

			if (!set.contains(nodei[k])) {
				set.add(nodei[k]);
				Other node = new Other(nodei[k] + "", nodei[k] + "");
				node.setReference(false);
				pw.addVertex(node, new Point(150, 100));
				nodes.put(nodei[k], node);
				// myGraph.moveVertex(node.getVertex(), 150, 100);
			}

			if (!set.contains(nodej[k])) {
				set.add(nodej[k]);
				Other node = new Other(nodej[k] + "", nodej[k] + "");
				node.setReference(false);
				pw.addVertex(node, new Point(150, 100));
				nodes.put(nodej[k], node);
				// myGraph.moveVertex(node.getVertex(), 150, 100);

			}

			ReactionEdge r = new ReactionEdge("", "", nodes.get(nodei[k]),
					nodes.get(nodej[k]));
			// System.out.println(nodes.get(nodei[k]).getID());
			// System.out.println(r.getID());
			r.setDirected(false);
			r.setReference(false);
			r.setHidden(false);
			r.setVisible(true);
			pw.addEdge(r);

		}

		myGraph.restartVisualizationModel();
		pw.getGraph().normalCentering();

		 MainWindow window = MainWindowSingleton.getInstance();
		 window.updateOptionPanel();
		// window.enable(true);
		pw.getGraph().changeToGEMLayout();

	}
}
