package graph.algorithms;

//import edu.uci.ics.jung.graph.Vertex;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindowSingleton;

import java.util.Iterator;
import java.util.Vector;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * This class is for comparison or merging of two or more graphs Also it is
 * possible to summarize (combine equal elements) a graph containing elements
 * from two or more graphs with some equal elements
 */

public class CompareGraphs {

	static GraphInstance graphInstance = new GraphInstance();

	private static void createEdges(Pathway pw, BiologicalNodeAbstract one,
			BiologicalNodeAbstract two) {

		Iterator<BiologicalNodeAbstract> neighbours = pw.getGraph()
				.getJungGraph().getNeighbors(one).iterator();// one.getNeighbors().iterator();
		BiologicalNodeAbstract bna;
		while (neighbours.hasNext()) {
			bna = neighbours.next();

			if (pw.getGraph().getJungGraph().findEdge(bna, two) == null
					&& pw.getGraph().getJungGraph().findEdge(two, one) == null) {
				ReactionEdge e = new ReactionEdge("", "", two, bna);
				pw.addEdge(e);

			}
		}
	}

	public static void mergeGraph(Pathway pathway) {

		Vector<BiologicalNodeAbstract> pathwayOneNodes = pathway.getAllNodesAsVector();
		MyGraph graph1 = pathway.getGraph();

		graph1.enableGraphTheory();
		Vector<BiologicalNodeAbstract> checked = new Vector<BiologicalNodeAbstract>();

		Vector<BiologicalNodeAbstract> pathwayTwoNodes = pathway.getAllNodesAsVector();

		Iterator<BiologicalNodeAbstract> it = pathwayOneNodes.iterator();
		BiologicalNodeAbstract bna;
		BiologicalNodeAbstract bna2;
		Iterator<BiologicalNodeAbstract> it2;
		
		while (it.hasNext()) {
			bna = it.next();

			it2 = pathwayTwoNodes.iterator();

			while (it2.hasNext()) {
				bna2 = it2.next();

				if ((bna2 != bna) && !checked.contains(bna2)) {

					if (areNodesEqualLabeled(bna, bna2)) {
						createEdges(pathway, bna, bna2);
						createEdges(pathway, bna2, bna);
						graph1.getVisualizationViewer().getPickedVertexState()
								.pick(bna2, true);

					}
				}
			}
			checked.add(bna);
		}

		graph1.removeSelection();
		graph1.updateGraph();
	}

	private static boolean areNodesEqualLabeled(BiologicalNodeAbstract one,
			BiologicalNodeAbstract two) {

		if (one.getLabel().equals(two.getLabel()))
			return true;
		else
			return false;
	}

	public static void compareGraphs(Pathway one, Pathway two) {

		MainWindowSingleton.getInstance().enableOptionPanelUpdate(false);

		Vector<BiologicalNodeAbstract> pathwayOneNodes = one.getAllNodesAsVector();
		MyGraph graph1 = one.getGraph();
		graph1.enableGraphTheory();

		Vector<BiologicalNodeAbstract> pathwayTwoNodes = two.getAllNodesAsVector();
		MyGraph graph2 = two.getGraph();
		graph2.enableGraphTheory();

		Iterator<BiologicalNodeAbstract> it = pathwayOneNodes.iterator();
		BiologicalNodeAbstract bna;
		Iterator<BiologicalNodeAbstract> it2;
		BiologicalNodeAbstract bna2;
		
		while (it.hasNext()) {

			bna = it.next();
			it2 = pathwayTwoNodes.iterator();
			while (it2.hasNext()) {

				bna2 = it2.next();
				if (areNodesEqualLabeled(bna, bna2)) {
					graph1.getVisualizationViewer().getPickedVertexState()
							.pick(bna, true);
					graph2.getVisualizationViewer().getPickedVertexState()
							.pick(bna2, true);
				}
			}
		}

		graph1.updateGraph();
		graph2.updateGraph();
		MainWindowSingleton.getInstance().enableOptionPanelUpdate(true);
	}
}
