package graph.algorithms;

import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.util.HashSet;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
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

		GraphTheoryAlgorithms.randomBipartiteGraph(n1, n2, m, seed, nodei,
				nodej);

		Pathway pw = new CreatePathway("Random Bipartite Graph").getPathway();
		MyGraph myGraph = pw.getGraph();

		myGraph.lockVertices();
		myGraph.stopVisualizationModel();

		HashSet set = new HashSet();

		for (int k = 1; k <= nodei[0]; k++) {

			if (!set.contains(nodei[k])) {
				set.add(nodei[k]);
				Other node = new Other(nodei[k] + "", nodei[k] + "",
						myGraph.createNewVertex());
				node.setReference(false);
				pw.addElement(node);
				myGraph.moveVertex(node.getVertex(), 150, 100);
			}

			if (!set.contains(nodej[k])) {
				set.add(nodej[k]);
				Other node = new Other(nodej[k] + "", nodej[k] + "",
						myGraph.createNewVertex());
				node.setReference(false);
				pw.addElement(node);
				myGraph.moveVertex(node.getVertex(), 150, 100);

			}

			ReactionEdge r = new ReactionEdge(myGraph.createEdge(pw
					.getNodeByName(nodei[k] + "").getVertex(), pw
					.getNodeByName(nodej[k] + "").getVertex(), false), "", "");

			r.setDirected(false);
			r.setReference(false);
			r.setHidden(false);
			r.setVisible(true);

			pw.addElement(r);

		}

		myGraph.unlockVertices();
		myGraph.restartVisualizationModel();
		pw.getGraph().normalCentering();

		MainWindow window = MainWindowSingelton.getInstance();
		window.updateOptionPanel();
		window.enable(true);
		pw.getGraph().changeToGEMLayout();

	}
}
