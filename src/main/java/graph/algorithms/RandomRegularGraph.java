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

	public RandomRegularGraph() {
	}

	public void generateRandomRegularGraph(int numberOfNodes, int degree) {

		int k;
		int n = numberOfNodes;
		long seed = 1;
		int edges = (n * degree) / 2;
		int nodei[] = new int[edges + 1];
		int nodej[] = new int[edges + 1];

		k = GraphTheoryAlgorithms.randomRegularGraph(n, degree, seed, nodei,
				nodej);

		Pathway pw = new CreatePathway("Random Regular Graph").getPathway();
		MyGraph myGraph = pw.getGraph();
		HashSet<Integer> set = new HashSet<Integer>();
		Map<Integer, BiologicalNodeAbstract> nodes = new HashMap<Integer, BiologicalNodeAbstract>();

		if (k != 0) {

		} else {
			for (k = 1; k <= edges; k++) {
				if (!set.contains(nodei[k])) {
					set.add(nodei[k]);
					Other node = new Other(nodei[k] + "", nodei[k] + "", pw);
					pw.addVertex(node, new Point(150, 100));
					nodes.put(nodei[k], node);
					// myGraph.moveVertex(node.getVertex(), 150, 100);
				}

				if (!set.contains(nodej[k])) {
					set.add(nodej[k]);
					Other node = new Other(nodej[k] + "", nodej[k] + "", pw);
					pw.addVertex(node, new Point(150, 100));
					nodes.put(nodej[k], node);
					// myGraph.moveVertex(node.getVertex(), 150, 100);
				}

				ReactionEdge r = new ReactionEdge("", "", nodes.get(nodei[k]),
						nodes.get(nodej[k]));

				r.setDirected(false);
				r.setVisible(true);
				pw.addEdge(r);

			}
		}
		myGraph.restartVisualizationModel();

		myGraph.normalCentering();

		MainWindow window = MainWindow.getInstance();
		window.updateOptionPanel();
		// window.enable(true);
		pw.getGraph().changeToGEMLayout();
	}
}
