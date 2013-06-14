/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.layouts.modularLayout;

import java.awt.Color;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserData;

/**
 *
 * @author dao
 */
public class MDRenderer {

    private static Color cutColor = Color.RED;
    private static Color treeColor = Color.YELLOW;
    private static Color clusterColor = Color.BLUE;

    public static void render(Graph g, Pathway pathway) {
        Set<Vertex> vertices = g.getVertices();
        Set<Edge> edges = g.getEdges();
        for (Vertex v : vertices) {
            MDNode node = new MDNode(v);
            v.setUserDatum(ModularDecomposition.MD_KEY, node, UserData.REMOVE);
        }
        MDNode root =
                ClusterDecomposition.decomposition(
                vertices, edges);
        setColor(root, pathway);
//        clusterColor=Color.BLUE;
//        if (root.getNodeType() == MDNode.PRIME) {
//            for (MDNode n : root.getChildren()) {
//                setColor(n, pathway);
//            }
//        } else if (root.getNodeType() == MDNode.TREE) {
//            setColor(root, pathway);
//        } else if (root.getNodeType() == MDNode.PARALLEL) {
//            for (MDNode c : root.getChildren()) {
//                if (c.getChildren() != null) {
//                    for (MDNode cc : c.getChildren()) {
//                        setColor(cc, pathway);
//                    }
//                }
//            }
//        }
    }

    public static void setColor(MDNode n, Pathway pathway) {
        if ((n.getNodeType() & MDNode.CUT) > 0) {
            BiologicalNodeAbstract ba =
                    (BiologicalNodeAbstract) pathway.getElement(n.getId());
//            ba.setMyColor(cutColor);
        }
        if (n.getChildren() != null) {
            if (n.getNodeType() == MDNode.CLUSTER) {
                float r=(float)Math.random()*0.5f,
                        g=(float)Math.random(),
                        b=(float)Math.random();
                Color color = new Color(r,g,b);
//                clusterColor=clusterColor.darker();
                for (MDNode c : n.getChildren()) {
                    if ((c.getNodeType() & MDNode.CUT) == 0) {
                        BiologicalNodeAbstract ba =
                                (BiologicalNodeAbstract) pathway.getElement(c.getId());
//                        ba.setMyColor(color);
                    }
                }
            } else if ((n.getNodeType()& MDNode.TREE) >0){
                for (MDNode c : n.getChildren()) {
                    BiologicalNodeAbstract ba =
                            (BiologicalNodeAbstract) pathway.getElement(c.getId());
//                    ba.setMyColor(treeColor);
                }
            }
            for (MDNode c : n.getChildren()) {
                setColor(c, pathway);
            }
        }
    }
}
