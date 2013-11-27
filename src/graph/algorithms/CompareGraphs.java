package graph.algorithms;

//import edu.uci.ics.jung.graph.Vertex;
import graph.CreatePathway;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindowSingelton;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import xmlInput.sbml.VAMLInput;
import xmlOutput.sbml.VAMLoutput;
import biologicalElements.GraphElementAbstract;
import biologicalElements.InternalGraphRepresentation;
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

		Vector pathwayOneNodes = pathway.getAllNodesAsVector();
		MyGraph graph1 = pathway.getGraph();

		graph1.enableGraphTheory();
		Vector<BiologicalNodeAbstract> checked = new Vector<BiologicalNodeAbstract>();

		Vector pathwayTwoNodes = pathway.getAllNodesAsVector();

		Iterator it = pathwayOneNodes.iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) it.next();

			Iterator it2 = pathwayTwoNodes.iterator();

			while (it2.hasNext()) {
				BiologicalNodeAbstract bna2 = (BiologicalNodeAbstract) it2
						.next();

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

		MainWindowSingelton.getInstance().enableOptionPanelUpdate(false);

		Vector pathwayOneNodes = one.getAllNodesAsVector();
		MyGraph graph1 = one.getGraph();
		graph1.enableGraphTheory();

		Vector pathwayTwoNodes = two.getAllNodesAsVector();
		MyGraph graph2 = two.getGraph();
		graph2.enableGraphTheory();

		Iterator it = pathwayOneNodes.iterator();
		while (it.hasNext()) {

			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) it.next();
			Iterator it2 = pathwayTwoNodes.iterator();
			while (it2.hasNext()) {

				BiologicalNodeAbstract bna2 = (BiologicalNodeAbstract) it2
						.next();
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
		MainWindowSingelton.getInstance().enableOptionPanelUpdate(true);
	}
}
