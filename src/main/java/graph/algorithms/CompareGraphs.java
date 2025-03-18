package graph.algorithms;

import java.util.Vector;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.VanesaGraph;
import gui.MainWindow;

/**
 * This class is for comparison or merging of two or more graphs Also it is possible to summarize (combine equal
 * elements) a graph containing elements from two or more graphs with some equal elements
 */
public class CompareGraphs {
    private static void createEdges(Pathway pw, BiologicalNodeAbstract one, BiologicalNodeAbstract two) {
        for (BiologicalNodeAbstract bna : pw.getGraph2().getNeighbors(one)) {
            if (pw.getGraph2().findEdge(bna, two) == null) {
                if (pw.getGraph2().findEdge(two, one) == null) {
                    ReactionEdge e = new ReactionEdge("", "", two, bna);
                    e.setDirected(true);
                    pw.addEdge(e);
                }
            }
        }
    }

    public static void mergeGraph(Pathway pathway) {
        final VanesaGraph graph = pathway.getGraph2();
        graph.enableGraphTheory();
        Vector<BiologicalNodeAbstract> checked = new Vector<>();
        for (BiologicalNodeAbstract bna : pathway.getAllGraphNodes()) {
            for (BiologicalNodeAbstract bna2 : pathway.getAllGraphNodes()) {
                if ((bna2 != bna) && !checked.contains(bna2)) {
                    if (areNodesEqualLabeled(bna, bna2)) {
                        createEdges(pathway, bna, bna2);
                        createEdges(pathway, bna2, bna);
                        graph.selectNodes(true, bna2);
                    }
                }
            }
            checked.add(bna);
        }
        pathway.removeSelection();
    }

    private static boolean areNodesEqualLabeled(BiologicalNodeAbstract one, BiologicalNodeAbstract two) {
        return one.getLabel().equals(two.getLabel());
    }

    public static void compareGraphs(Pathway one, Pathway two) {
        MainWindow.getInstance().enableOptionPanelUpdate(false);
        final VanesaGraph graph1 = one.getGraph2();
        graph1.enableGraphTheory();
        final VanesaGraph graph2 = two.getGraph2();
        graph2.enableGraphTheory();
        for (final BiologicalNodeAbstract bna : one.getAllGraphNodes()) {
            for (final BiologicalNodeAbstract bna2 : two.getAllGraphNodes()) {
                if (areNodesEqualLabeled(bna, bna2)) {
                    graph1.selectNodes(true, bna);
                    graph2.selectNodes(true, bna2);
                }
            }
        }
        MainWindow.getInstance().enableOptionPanelUpdate(true);
    }
}
