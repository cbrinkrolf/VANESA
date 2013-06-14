package graph.algorithms;

import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.util.HashSet;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.Other;

public class RandomRegularGraph {

	public RandomRegularGraph() {
		// TODO Auto-generated constructor stub
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
		HashSet set = new HashSet();

		myGraph.lockVertices();
		myGraph.stopVisualizationModel();

		if (k != 0) {

		} else {
			for (k = 1; k <= edges; k++) {
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
						.getNodeByName(nodej[k] + "").getVertex(), false), "",
						"");

				r.setDirected(false);
				r.setReference(false);
				r.setHidden(false);
				r.setVisible(true);
				pw.addElement(r);

			}
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
