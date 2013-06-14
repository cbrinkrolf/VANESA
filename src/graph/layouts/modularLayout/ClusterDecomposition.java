/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.layouts.modularLayout;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dao
 */
public class ClusterDecomposition {

    private static void setConnections(Set<MDNode> nodes, Map<MDNode, Set<MDNode>> connections) {
        for (MDNode node : nodes) {
            Set<MDNode> neighbours = connections.get(node);
            if (neighbours != null) {
                if (node.getConnections() == null) {
                    node.setConnections(new HashSet(neighbours));
                } else {
                    node.getConnections().addAll(neighbours);
                }
            }
        }
    }

    private static void setConnections(
            Collection<MDNode> cuts,
            Collection<MDNode> clusters) {
        for (MDNode cut : cuts) {
            Collection<MDNode> vertices = new HashSet();
            cut.getAllVertices(vertices);
            for (MDNode cluster : clusters) {
                Set<MDNode> children = cluster.getChildren();
                for (MDNode c : children) {
                    if (vertices.contains(c)) {
                        cluster.addConnection(cut);
                        cut.addConnection(cluster);
                        break;
                    }
                }
            }
        }
    }

    public static MDNode createTree(
            Collection<MDNode> vertices, Map<MDNode, Set<MDNode>> connections) {
        if (vertices.size() == 0) {
            return null;
        } else if (vertices.size() == 1) {
            MDNode treeRoot = vertices.iterator().next();
            if (treeRoot.getSubTrees() == null) {
                return treeRoot;
            } else {
                MDNode root = new MDNode();
                root.addChild(treeRoot);
                return root;
            }
        }
        Map<MDNode, Set<MDNode>> leavesMap = new HashMap();
        Collection<MDNode> currentLevel = new HashSet(vertices);
        //remove all vertices with edge
        currentLevel.removeAll(connections.keySet());
        //then add all vertices with single edge
        for (MDNode v : connections.keySet()) {
            Set<MDNode> neighbours = connections.get(v);
            if (neighbours.size() == 1) {
                MDNode n = neighbours.iterator().next();
                if (connections.get(n).size() > 1) {
                    currentLevel.add(v);
                    ClusterDecomposition.addEdgeInMap(leavesMap, n, v);
                }
            }
        }
        //now in currentLevel leaves only vertices with no or single edge
        if (currentLevel.size() == 0) {//when only a 2-connected graph left
            MDNode root = new MDNode();
//            Set<MDNode> trees=new HashSet();
//            for(MDNode v:vertices){
//                if(v.getNodeType()==MDNode.TREE){
//                    trees.add(v);
//                }
//            }
////            vertices.removeAll(trees);
//            root.setChildren(new HashSet(vertices));
            List<Set<MDNode>> allClusters = new ArrayList();
            List<MDNode> cuts = findCuts(
                    vertices,
                    connections,
                    allClusters);
            List<MDNode> clusterNodes = new ArrayList();
            if (cuts.size() > 0) {
                for (MDNode cut : cuts) {
                    root.addChild(cut);
                    root.addChildrenInTree(cut);                    
                }
                Set<MDNode> allCuts=new HashSet(root.getChildren());
                for (Set<MDNode> cluster : allClusters) {
                    MDNode n = new MDNode();
                    n.setNodeType(MDNode.CLUSTER);
                    cluster.removeAll(allCuts);
                    n.setChildren(cluster);
                    for (MDNode cl : new HashSet<MDNode>(cluster)) {
                        if (cl.getNodeType() == MDNode.TREE) {
                            n.addChildrenInTree(cl);
                        }
                    }
                    root.addChild(n);
                    Map<MDNode, Set<MDNode>> innerConnections =
                            ModularDecomposition.filterMap(connections, cluster, cluster);
                    setConnections(cluster, innerConnections);
                    clusterNodes.add(n);
                }
                setConnections(cuts, clusterNodes);
            } else {
                root.setChildren(new HashSet(vertices));
                for (MDNode cl : vertices) {
                    if (cl.getNodeType() == MDNode.TREE) {
                        root.addChildrenInTree(cl);
                    }
                }
                setConnections(root.getChildren(), connections);
            }

            return root;
        }
        for (MDNode node : leavesMap.keySet()) {
//            Set<MDNode> children = new HashSet();
//            children.add(node);
//            children.addAll(leavesMap.get(node));
            if (node.getSubTrees() == null) {
                node.setSubTrees(new HashSet());
            }
            Set<MDNode> children = leavesMap.get(node);
            node.getSubTrees().addAll(children);
            for(MDNode n:children){
                n.addConnection(node);
                node.addConnection(n);
            }
            node.setNodeType(MDNode.TREE);
//            for(MDNode c:node.getChildren()){
//                c.setParent(node);
//            }
//            MDNode parent = createNode(children, branchMap);
//            branchMap.put(node, parent);
        }
        Collection<MDNode> nextLevel = new HashSet(vertices);
        nextLevel.removeAll(currentLevel);
        Map<MDNode, Set<MDNode>> nextLevelConnections =
                ModularDecomposition.filterMap(connections, nextLevel, nextLevel);
        return createTree(nextLevel, nextLevelConnections);
    }

    public static void addEdgeInMap(Map<MDNode, Set<MDNode>> neighboursMap, MDNode source, MDNode target) {
        Set<MDNode> neighbours = neighboursMap.get(source);
        if (neighbours == null) {
            neighbours = new HashSet();
            neighboursMap.put(source, neighbours);
        }
        neighbours.add(target);
    }

    public static MDNode decomposition(Set<Vertex> visibleVertices, Set<Edge> visibleEdges) {
        Map nodes = new LinkedHashMap();
        for (Vertex v : visibleVertices) {
            MDNode node = (MDNode) v.getUserDatum(ModularDecomposition.MD_KEY);
            nodes.put(v, node);
        }
        Map<MDNode, Set<MDNode>> edges = new LinkedHashMap();
        for (Edge edge : visibleEdges) {
            Pair endpoints = edge.getEndpoints();
            Vertex v1 = (Vertex) endpoints.getFirst();
            Vertex v2 = (Vertex) endpoints.getSecond();
            if (visibleVertices.contains(v1) && visibleVertices.contains(v2)) {
                ModularDecomposition.addEdgeInMap(edges, v1, v2);
                ModularDecomposition.addEdgeInMap(edges, v2, v1);
            }
        }
        MDNode root = decomposition(new ArrayList(nodes.values()), edges);
//        if (root.getChildren() != null) {
//            for (MDNode c : root.getChildren()) {
//                if (c.getChildren() != null && c.getChildren().size() > 0) {
//                    Collection<MDNode> leaves = c.getAllLeaves();
//                    c.setChildren(new HashSet(leaves));
//                    for (MDNode l : leaves) {
//                        l.setParent(c);
//                    }
//                }
//            }
//        }
        return root;
    }

    public static MDNode decomposition(Collection<MDNode> vertices, Map<MDNode, Set<MDNode>> edges) {
        MDNode root = null;
        if (vertices.size() > 0) {
            MDNode first = vertices.iterator().next();
            Set<MDNode> connected = new HashSet();
            Set<MDNode> rest =
                    divide(new LinkedHashSet(vertices), edges,
                    first, connected);
            Map<MDNode, Set<MDNode>> connectedEdges =
                    ModularDecomposition.filterMap(edges, connected, connected);
            if (connected.size() > 0) {
                root = createTree(connected, connectedEdges);
                root.setEdges(connectedEdges);
            }
            if (rest != null && rest.size() > 0) {
                Map<MDNode, Set<MDNode>> restEdges =
                        ModularDecomposition.filterMap(edges, rest, rest);
                MDNode branch2 = decomposition(rest, restEdges);
                MDNode branch1 = root;
                root = new MDNode(MDNode.PARALLEL);
                root.addChild(branch1);
                root.addChild(branch2);
            }
        }
        root.setEdges(edges);
        return root;
    }

    public static List<MDNode> findCuts(Collection<MDNode> vertices,
            Map<MDNode, Set<MDNode>> edges, List<Set<MDNode>> allClusters) {
        List<MDNode> cuts = new ArrayList();
        int minDegree = 3;
        for (MDNode n : vertices) {
            if (edges.get(n).size() >= minDegree) {
                List<Set<MDNode>> clusters =
                        findClusters(n, new HashSet(vertices), new HashMap(edges));
                if (clusters.size() > 1) {
                    cuts.add(n);
//                    Set<MDNode> used = new HashSet();
                    for (Set<MDNode> cluster : clusters) {
                        findCutTree(n, edges, cluster);
                    }
//                    if (used.size() > 1) {
//                        for (Set<MDNode> cluster : clusters) {
//                            cluster.removeAll(used);
//                        }
//                    }
                    for (Set<MDNode> cluster : clusters) {
                        cuts.addAll(findCuts(cluster,
                                ModularDecomposition.filterMap(edges, cluster, cluster),
                                allClusters));
//                        cluster.remove(n);
//                        allClusters.add(cluster);
                    }
                    break;
                }
            }
        }
        if (cuts.size() == 0) {
            allClusters.add(new HashSet(vertices));
        }
        return cuts;
    }

    private static void findCutTree(MDNode cut,
            Map<MDNode, Set<MDNode>> edges, Set<MDNode> cluster) {
        cut.addNodeType(MDNode.CUT);
        Set<MDNode> neighbours = new HashSet(edges.get(cut));
        neighbours.retainAll(cluster);
        if (neighbours.size() == 1) {
            MDNode newCut = neighbours.iterator().next();
            if (cut.getSubTrees() == null) {
                cut.setSubTrees(new HashSet());                
            }
            cut.addNodeType(MDNode.TREE);
            cut.getSubTrees().add(newCut);
//            cut.addChild(newCut);
            cluster.remove(newCut);
            findCutTree(newCut, edges, cluster);
        } else {
            cluster.add(cut);
        }
    }

    public static List<Set<MDNode>> findClusters(MDNode cut, Set<MDNode> vertices, Map<MDNode, Set<MDNode>> edges) {
        vertices.remove(cut);
        edges.remove(cut);
        List<Set<MDNode>> disjunktions = findDisjunktions(vertices, edges);
        return disjunktions;
    }

    public static List<Set<MDNode>> findDisjunktions(
            Set<MDNode> vertices,
            Map<MDNode, Set<MDNode>> edges) {
        List<Set<MDNode>> disjuktions = new ArrayList();
        Set<MDNode> rest = null;
        do {
            if (vertices.size() > 0) {
                Set<MDNode> connected = new HashSet();
                MDNode first = vertices.iterator().next();
                rest = divide(vertices, edges,
                        first, connected);
                disjuktions.add(connected);
            } else {
                rest = null;
            }
        } while (rest != null);
        return disjuktions;
    }

    public static Set<MDNode> divide(
            Set<MDNode> vertices,
            Map<MDNode, Set<MDNode>> edges,
            MDNode middle, Set<MDNode> connected) {
        connected.add(middle);
        vertices.remove(middle);
        Set<MDNode> rest = divide(vertices, Arrays.asList(middle), edges, connected);
        return rest;
    }

    public static Set<MDNode> divide(Set<MDNode> vertices,
            Collection<MDNode> last,
            Map<MDNode, Set<MDNode>> edges,
            Set<MDNode> connected) {
        if (vertices.size() == 0) {
            return null;
        }
        Set<MDNode> neighbours = new LinkedHashSet();
        for (MDNode node : last) {
            Collection<MDNode> n = edges.get(node);
            if (n != null) {
                neighbours.addAll(n);
            }
        }
        if (neighbours.size() == 0) {
            return vertices;
        }
        connected.addAll(neighbours);
        last = neighbours;
        vertices.removeAll(neighbours);
        Map<MDNode, Set<MDNode>> restEdges =
                ModularDecomposition.filterMap(edges, null, vertices);
        return divide(vertices, last, restEdges, connected);
    }
}
