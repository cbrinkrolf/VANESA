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
 * @author Besitzer
 */
public class ModularDecomposition {

    public final static String MD_KEY = "ModularDecomposition";
    private static int hit = 1;
//    private final static Logger logger =
//            Logger.getLogger(ModularDecomposition.class);


    static {
//        logger.setLevel(Level.ALL);
    }

    public static void addEdgeInMap(Map<MDNode, Set<MDNode>> neighboursMap, Vertex source, Vertex target) {
        MDNode node = (MDNode) source.getUserDatum(MD_KEY);
        Set<MDNode> neighbours = neighboursMap.get(node);
        if (neighbours == null) {
            neighbours = new HashSet();
            neighboursMap.put(node, neighbours);
        }
        neighbours.add((MDNode) target.getUserDatum(MD_KEY));
    }

    public MDNode decomposition(Set<Vertex> visibleVertices, Set<Edge> visibleEdges) {
        Map nodes = new LinkedHashMap();
        for (Vertex v : visibleVertices) {
            MDNode node = (MDNode) v.getUserDatum(MD_KEY);
            nodes.put(v, node);
        }
        Map<MDNode, Set<MDNode>> edges = new LinkedHashMap();
        for (Edge edge : visibleEdges) {
            Pair endpoints = edge.getEndpoints();
            Vertex v1 = (Vertex) endpoints.getFirst();
            Vertex v2 = (Vertex) endpoints.getSecond();
            if (visibleVertices.contains(v1) && visibleVertices.contains(v2)) {
                addEdgeInMap(edges, v1, v2);
                addEdgeInMap(edges, v2, v1);
            }
        }
        return decomposition(new ArrayList(nodes.values()), edges);
    }

    private static <E> MDNode buildSimpleTree(List<MDNode> vertices, Map<MDNode, Set<MDNode>> edges) {
        int size = vertices.size();
        MDNode root = null;
        switch (size) {
            case 0:
                break;
            case 1:
                root = vertices.get(0);
                break;
            case 2:
                MDNode n0 = vertices.get(0),
                 n1 = vertices.get(1);
                root = new MDNode();
                Set c = edges.get(n0);
                if (c != null && c.contains(n1)) {
                    root.setNodeType(MDNode.SERIES);
                } else {
                    root.setNodeType(MDNode.PARALLEL);
                }
                root.addChild(n0);
                root.addChild(n1);
                break;
            default:
                throw new IllegalArgumentException("this method can only build a tree with maximal 3 children");
        }
        return root;
    }

    public static LinkedHashSet<Component> buildComponent(MDNode tree) {
        LinkedHashSet<Component> components = new LinkedHashSet<Component>();
        int partition = tree.getPartition();
        if ((tree.getNodeType() == MDNode.SERIES && partition > 0) ||
                (tree.getNodeType() == MDNode.PARALLEL && partition == 0) ||
                (tree.getNodeType() == MDNode.PRIME)) {
            Component c = new Component(tree, partition == 0);
            c.setPartition(partition);
            components.add(c);
        } else {
            Set<MDNode> subTrees = tree.getChildren();
            for (MDNode s : subTrees) {
                Component c = new Component(s, partition == 0);
                c.setPartition(partition);
                components.add(c);
            }
        }
        return components;
    }

    public static MDNode decomposition(List<MDNode> vertices, Map<MDNode, Set<MDNode>> edges) {
        int size = vertices.size();
        if (size < 3) {
            return buildSimpleTree(vertices, edges);
        }
        MDNode middle = vertices.remove(0);
        List<Set<MDNode>> partitions = new ArrayList();
        Set<MDNode> rest = divide(new LinkedHashSet(vertices), edges, middle, partitions);

        int count = partitions.size();
        List<List<MDNode>> allforests = new ArrayList();
        int counter = 0;
        Map<MDNode, Set<MDNode>> edgesForN1 = null;
        for (Set<MDNode> partition : partitions) {
            List<MDNode> forest = new ArrayList();
            if (partition.size() > 0) {
                MDNode tree = null;
                if (partition.size() == 1) {
                    tree = partition.iterator().next();
                } else {
                    Map<MDNode, Set<MDNode>> filtered = filterMap(edges, partition, partition);
                    if (counter == 1) {
                        edgesForN1 = filtered;
                    }
                    tree = decomposition(
                            new ArrayList(partition), filtered);
                }
                tree.setPartition(counter);
                counter++;
                if (tree != null) {
                    forest.add(tree);
                }
            }
            allforests.add(forest);
        }
        refreshActiveEdges(vertices, middle, edges);
        List<MDNode> allTrees = new ArrayList();
        LinkedHashSet<Component> allComponents = new LinkedHashSet();
        for (int i = 0; i < count; i++) {
            List<MDNode> forest = allforests.get(i);
            if (forest.size() > 0) {
                MDNode tree = forest.get(0);
                LinkedHashSet<Component> components = buildComponent(tree);
                allComponents.addAll(components);
//                logger.debug("before refine:" + forest);
                if (i > 0) {
                    refine(partitions.get(i - 1), forest, false, i == 1);
                }
                if (i < count - 1) {
                    refine(partitions.get(i + 1), forest, true, i == 0);
                }
//                logger.debug("after refine:" + forest);
//                allTrees.addAll(forest);
            }
//            if (i == 0) {
//                allTrees.add(middle);
//            }
        }
        for (int i = 0; i < count; i++) {
            List<MDNode> forest = allforests.get(i);
            if (forest.size() > 0) {
//                logger.debug("before promote:" + forest);                
                promote(forest);
//                logger.debug("after promote:" + forest);                
                allTrees.addAll(forest);
            }
            if (i == 0) {
                allTrees.add(middle);
            }
        }
        for (int i = 0; i < allTrees.size(); i++) {
            allTrees.get(i).setOrder(i);
        }

        Map<MDNode, List<Component>> commonParents = new HashMap();
        for (Component c : allComponents) {
            MDNode common = c.getCommonParent();
            if (common != null) {
                List<Component> siblings = commonParents.get(common);
                if (siblings == null) {
                    siblings = new ArrayList();
                    commonParents.put(common, siblings);
                }
                siblings.add(c);
            }
        }
        for (MDNode common : commonParents.keySet()) {
            List<Component> siblings = commonParents.get(common);
            if (siblings != null && siblings.size() > 1 && siblings.size() == common.getChildren().size()) {
                Component parentComponent =
                        new Component(common, siblings.get(0).isCoComponent());
                parentComponent.setPartition(common.getPartition());
                allComponents.removeAll(siblings);
                allComponents.add(parentComponent);
            }
        }
        for (Component c : allComponents) {
            c.refresh();
        }
//        logger.debug("allTrees:" + allTrees);
        Set<Component> left = new LinkedHashSet(), rightSet =
                new LinkedHashSet();
        for (MDNode node : allTrees) {
            if (node == middle) {
                continue;
            }
            LinkedHashSet<Component> all = new LinkedHashSet();
            Component c = node.getComponent();
            if (c != null) {
                all.add(c);
            } else {
                Collection<MDNode> subTrees = node.getChildren();
                for (MDNode subtree : subTrees) {
                    all.add(subtree.getComponent());
                }
            }
            if (node.getOrder() < middle.getOrder()) {
                left.addAll(all);
            } else if (node.getOrder() > middle.getOrder()) {
                rightSet.addAll(all);
            }
        }
        Component[] leftArray = left.toArray(new Component[0]);
        LinkedHashSet<Component> leftSet = new LinkedHashSet();
        for (int i = leftArray.length - 1; i >= 0; i--) {
            leftArray[i].setOrder(i - leftArray.length);
            leftSet.add(leftArray[i]);
        }
//        int leftSize = left.size();
//        int i = 0;
//
//        for (Component c : left) {
//            c.setOrder(i - leftSize);
//            i++;
//        }
//        for (int i = 0; i < left.size(); i++) {
//            left.get(i).setOrder(i - leftSize);
//        }
        Component middleComponent = new Component(middle, true);
        middleComponent.refresh();
//        for (int i = 0; i < right.size(); i++) {
//            right.get(i).setOrder(i + 1);
//        }
        int i = 1;
        for (Component c : rightSet) {
            c.setOrder(i);
            i++;
        }
//        logger.debug("before assemble :left components:" + left);
//        logger.debug("before assemble :right components:" + rightSet);
//        logger.debug("before assemble :middle component:" + middleComponent);

        Map<Component, Component> connections =
                findConnections(leftSet, rightSet, middleComponent);
//        logger.debug("connections:" + connections);
        Map<Component, Component> unavilableForPrallel = new HashMap();
        if (partitions.size() > 1) {
            unavilableForPrallel = findComponentsUnavilableForPrallel(
                    partitions.get(1), edgesForN1);
        }
//        logger.debug("unavilableForPrallel:" + unavilableForPrallel);
        Map<String, Object> result = new HashMap();
        result.put("middle", middle);
        MDNode assembled =
                assemble(leftSet, result, rightSet,
                connections, unavilableForPrallel, edges);
        if (rest != null) {
            MDNode root = new MDNode(MDNode.PARALLEL);
            Map<MDNode, Set<MDNode>> restEdges = filterMap(edges, rest, rest);
            MDNode r = decomposition(new ArrayList(rest), restEdges);
            root.addChild(assembled);
            root.addChild(r);
            assembled = root;
        }
//        logger.debug("buildtree:-------------->\n" + assembled);
        return assembled;
    }

    private static Map<Component, Component> findComponentsUnavilableForPrallel(
            Collection<MDNode> partition1, Map<MDNode, Set<MDNode>> edges) {
        Map<Component, Component> unavilableForPrallel = new HashMap();
        Set<MDNode> range = new HashSet(partition1);
        for (MDNode node : range) {
            int order = node.getComponent().getOrder();
            if (node.getRightNeighbours() == null) {
                if (edges != null) {
                    Set<MDNode> neighbours = edges.get(node);
                    Component oldMax = unavilableForPrallel.get(node.getComponent());
                    for (MDNode n : neighbours) {
                        if (n.getComponent().getOrder() > order) {
                            if (oldMax == null) {
                                oldMax = n.getComponent();
                            } else if (oldMax.getOrder() < n.getComponent().getOrder()) {
                                oldMax = n.getComponent();
                            }
                        }
                    }
                    if (oldMax != null) {
                        unavilableForPrallel.put(node.getComponent(), oldMax);
                    }
                }
            } else {
                unavilableForPrallel.put(
                        node.getComponent(),
                        node.getRightNeighbours().iterator().next().getComponent());
            }
        }
        return unavilableForPrallel;
    }

    public static Map<Component, Component> findConnections(
            Set<Component> leftSet, Set<Component> rightSet,
            Component middleComponent) {
        Set<MDNode> allRightLeaves = new LinkedHashSet();
        Set<MDNode> allLeftLeaves = new LinkedHashSet();
        Map<Integer, Component> leftMap = new HashMap();
        for (Component c : rightSet) {
            Set<MDNode> leaves = c.getLeaves();
            allRightLeaves.addAll(leaves);
        }
        for (Component c : leftSet) {
            leftMap.put(c.getOrder(), c);
            Set<MDNode> leaves = c.getLeaves();
            allLeftLeaves.addAll(leaves);
        }
//        edges = filterMap(edges, allLeftLeaves, allRightLeaves);
        Map<Component, Component> connections = new HashMap();
        Map<Component, Map<Component, Integer>> rightConnections = new HashMap();
        for (MDNode leaf : allLeftLeaves) {
            Component c0 = leaf.getComponent();
            int o0 = c0.getOrder();
            Set<MDNode> neighbours = leaf.getRightNeighbours();
            if (neighbours == null) {
                continue;
            }
            Component limit = connections.get(c0);
//            Set<MDNode> neighbours = edges.get(leaf);            
            for (MDNode neighbour : neighbours) {
                Component nc = neighbour.getComponent();
                if (limit == null || limit.compareTo(nc) < 0) {
                    limit = nc;
                }
                Map<Component, Integer> counter = rightConnections.get(nc);
                if (counter == null) {
                    counter = new HashMap();
                    rightConnections.put(nc, counter);
                }
                Integer old = counter.get(c0);
                if (old == null) {
                    old = 0;
                }
                counter.put(c0, old + 1);
            }
            connections.put(c0, limit);
        }
        for (Component c : rightConnections.keySet()) {
            Map<Component, Integer> counter = rightConnections.get(c);
            int nodeCount = c.getLeaves().size();
            for (Component l : counter.keySet()) {
                int count = counter.get(l);
                if (l.getLeaves().size() * nodeCount == count) {
                    Component limit = connections.get(c);
                    if (limit == null || limit.compareTo(l) < 0) {
                        limit = l;
                        connections.put(c, l);
                    }
                }
            }
        }
        Component lDefault = middleComponent;
        if (leftSet.size() > 0) {
            lDefault = leftSet.iterator().next();
        }
        for (Component c : rightSet) {
            Component limit = connections.get(c);
            if (limit == null) {
                connections.put(c, lDefault);
            } else {
                Component next = leftMap.get(limit.getOrder() + 1);
                if (next == null) {
                    next = middleComponent;
                }
                connections.put(c, next);
            }
        }
        for (Component c : leftSet) {
            if (connections.get(c) == null) {
                connections.put(c, middleComponent);
            }
        }
        return connections;
    }

    public static Set<MDNode> divide(
            Set<MDNode> vertices,
            Map<MDNode, Set<MDNode>> edges,
            MDNode middle, List<Set<MDNode>> partitions) {
//        logger.debug("divide vertices:" + vertices + "with vertex at middle:" + middle);
        Set<MDNode> rest = divide(vertices, Arrays.asList(middle), edges, partitions);
//        logger.debug("result partitions:" + partitions);
//        logger.debug("rest vertices:" + rest);
        return rest;
    }

    public static Set<MDNode> divide(Set<MDNode> vertices,
            Collection<MDNode> last,
            Map<MDNode, Set<MDNode>> edges,
            List<Set<MDNode>> partitions) {
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
        partitions.add(neighbours);
//        neighbours.retainAll(vertices);
        last = neighbours;
        vertices.removeAll(neighbours);
        Map<MDNode, Set<MDNode>> rightEdges =
                filterMap(edges, null, vertices);
        return divide(vertices, last, rightEdges, partitions);
    }

    public static <E, V> Map<E, Set<V>> filterMap(Map<E, Set<V>> map, Collection<E> keyRange, Collection<V> valueRange) {
        map = new HashMap(map);
        if (keyRange != null) {
            map.keySet().retainAll(keyRange);
        }
        for (Map.Entry<E, Set<V>> entry : map.entrySet()) {
            Set<V> value = new HashSet(entry.getValue());
            value.retainAll(valueRange);
            entry.setValue(value);
        }
        return map;
    }

    public static void refreshActiveEdges(Collection<MDNode> vertices, MDNode middle, Map<MDNode, Set<MDNode>> allEdges) {
        for (MDNode node : vertices) {
            node.setLeftNeighbours(null);
            node.setRightNeighbours(null);
        }
        for (MDNode node : vertices) {
            Set<MDNode> neighbours = allEdges.get(node);
            if (neighbours == null) {
                continue;
            }
            int p0 = node.getPartition();
            for (MDNode neighbour : neighbours) {
                if (neighbour == middle) {
                    continue;
                }
                int p1 = neighbour.getPartition();
                if (p1 < p0) {
                    node.addLeftNeighbours(neighbour);
                } else if (p1 > p0) {
                    node.addRightNeighbours(neighbour);
                }
            }
        }
    }

    public static void refine(Collection<MDNode> vertices,
            List<MDNode> forest,
            boolean refineLeft, boolean markLeft) {
//        logger.debug("refine forest:" + forest);
//        logger.debug("with vertices:" + vertices);        
        for (MDNode node : vertices) {
            Set<MDNode> neighbours = node.getLeftNeighbours();
            if (!refineLeft) {
                neighbours = node.getRightNeighbours();
            }
            if (neighbours == null || neighbours.size() == 0) {
                continue;
            }
            Collection<Group> groups = group(neighbours);
//            logger.debug("refined partitions:"+groups);
            for (Group group : groups) {
                if (!group.isAll()) {//&& part.getParent().getNodeType() > 0
                    refine(group, forest, markLeft);
                }
            }
        }
//        logger.debug("refine result:" + forest);        
    }

    public static void refine(Group group, List<MDNode> forest, boolean markLeft) {
        Set<MDNode> nodes = group.getChildren();
        MDNode parent = group.getParent();
        if (parent != null && parent.getNodeType() == MDNode.PRIME) {
            parent.setMark(markLeft ? MDNode.LEFT_MARK : MDNode.RIGHT_MARK);
            return;
        }
        MDNode n1 = new MDNode(parent),
                n2 = new MDNode(parent);
        if (nodes.size() == 1) {
            n1 = nodes.iterator().next();
        } else {
            n1.setChildren(nodes);
        }
        Set<MDNode> children = new LinkedHashSet(parent.getChildren());
        children.removeAll(nodes);
        if (children.size() == 1) {
            n2 = children.iterator().next();
        } else {
            n2.setChildren(children);
        }
        children = parent.getChildren();
        children.clear();
        children.add(n1);
        children.add(n2);
//        if (parent.getConnections() != null) {
//            Set<MDNode> connections = parent.getConnections();
//            for (MDNode c : connections) {
//                c.getConnections().remove(parent);
//                Collections.addAll(c.getConnections(), n1, n2);
//            }
//        }
        n1.setMark(markLeft ? MDNode.LEFT_MARK : MDNode.RIGHT_MARK);
//        promote(n1, forest, markLeft, new int[]{0});
    }

    public static void addInComponent(Component c, MDNode node) {
        if (c != null) {
            node.setComponent(c);
            c.addTree(node);
        }
    }

    public static void promote(List<MDNode> forest) {
        List<MDNode> all = new ArrayList<MDNode>(forest);
        int counter = 0;
        for (int i = 0; i < all.size(); i++) {
            counter += addNodeIntoForest(all.get(i), forest, i + counter, 0);
        }
        all = new ArrayList<MDNode>(forest);
        for (MDNode node : all) {
            verifyNode(forest, node);
        }
    }

    private static void verifyNode(Collection<MDNode> nodes, MDNode node) {
        Set<MDNode> children = node.getChildren();
        if (children != null) {
            for (MDNode n : new ArrayList<MDNode>(children)) {
                verifyNode(children, n);
            }
            Component c = node.getComponent();
            if (children.size() == 0) {
                nodes.remove(node);
                if (c != null) {
                    c.removeTree(node);
                    node.setComponent(null);
                }
            } else if (children.size() == 1) {
                MDNode m = children.iterator().next();
                if (nodes instanceof List) {
                    List<MDNode> list = (List<MDNode>) nodes;
                    int idx = list.indexOf(node);
                    list.remove(idx);
                    list.add(idx, m);
                    if (c != null) {
                        addInComponent(c, m);
                    }
                } else {
                    nodes.remove(node);
                    nodes.add(m);
                }
                if (c != null) {
                    c.removeTree(node);
                    node.setComponent(null);
                }
            }
        }
    }
//
    public static int addNodeIntoForest(MDNode node, List<MDNode> forest, int idx, int counter) {
        MDNode parent = node.getParent();
        if (parent != null) {
            parent.removeChild(node);
//            int idx = forest.indexOf(parent);
//            if ((node.getMark() & MDNode.LEFT_MARK) > 0) {
//                forest.add(idx, node);
//            } else if ((node.getMark() & MDNode.RIGHT_MARK) > 0) {
//                forest.add(idx + 1, node);
//            }
            forest.add(idx, node);
            Component pc = parent.getComponent();
            addInComponent(pc, node);
        }

        Set<MDNode> children = node.getChildren();
        if (node.getMark() > 0 && children != null) {
            List<MDNode> left =
                    new ArrayList(), right = new ArrayList();
            if (node.getNodeType() == MDNode.PRIME) {
                left.addAll(children);
            } else {
                for (MDNode child : children) {
                    if ((child.getMark() & MDNode.LEFT_MARK) > 0) {
                        left.add(child);
                    } else if ((child.getMark() & MDNode.RIGHT_MARK) > 0) {
                        right.add(child);
                    }

                }
            }
            for (MDNode n : right) {
                counter += addNodeIntoForest(n, forest, idx + 1, 1);
            }

            for (MDNode n : left) {
                counter += addNodeIntoForest(n, forest, idx, 1);
            }

        }
        return counter;
    }

    public static void promote(MDNode node, List<MDNode> forest, boolean toLeft, int[] pos) {
        MDNode parent = node.getParent();
        if (parent == null) {
            pos[0] = forest.indexOf(node);
            return;//node is already a root;

        }


        promote(parent, forest, toLeft, pos);
        parent.removeChild(node);
        addInComponent(parent.getComponent(), node);
        int idx = pos[0];
        if (parent.getChildren().size() == 1) {
            MDNode child = parent.getChildren().iterator().next();
            parent.removeChild(child);
            forest.remove(parent);
            forest.add(idx, child);
            Component pc = parent.getComponent();
            if (pc != null) {
                addInComponent(pc, child);
                pc.removeTree(parent);
            }

        }
        if (toLeft) {
            forest.add(idx, node);
        } else {
            forest.add(idx + 1, node);
            ++pos[0];
        }

    }

    public static void addNodeIntoPartitions(Map<MDNode, Group> partitions, MDNode node) {
        if (node == null) {
            return;
        }

        MDNode parent = node.getParent();
        if (parent == null) {
            return;//node is root
        }

        Group last = partitions.get(parent);
        if (last == null) {
            last = new Group(parent);
            partitions.put(parent, last);
        }
//        logger.debug("add node :"+node+" in group:"+last);
        last.addChild(node);
        if (last.isAll()) {
            partitions.remove(parent);
            addNodeIntoPartitions(partitions, parent);
        }
//        logger.debug("result:"+partitions);
    }

    public static Collection<Group> group(Collection<MDNode> nodes) {
        if (nodes.size() == 0) {
            return new ArrayList();
        }

        Map<MDNode, Group> partitions = new HashMap();
        for (MDNode node : nodes) {
            addNodeIntoPartitions(partitions, node);
        }

        return partitions.values();
    }

    public static MDNode assemble(
            Set<Component> left, Map<String, Object> result,
            Set<Component> right,
            Map<Component, Component> connections,
            Map<Component, Component> unavilableForPrallel,
            Map<MDNode, Set<MDNode>> edges) {
        MDNode middle = (MDNode) result.get("middle");
        MDNode tree = null;

//        logger.debug("to assemble :left components:\n" + left);
//        logger.debug("to assemble :right components:\n" + right);

        if (left.size() == 0 && right.size() == 0) {
            tree = middle;
        } else {
            Component lastLeft = (Component) result.get("left"), lastRight = (Component) result.get("right");
            if (lastLeft == null) {
                lastLeft = middle.getComponent();
            }

            if (lastRight == null) {
                lastRight = middle.getComponent();
            }

//            logger.debug("to assemble: middle:" + middle + "\n lastleft:" + lastLeft + " \nlastright:" + lastRight);

            if (!assemble(left, lastRight, middle,
                    connections, null, MDNode.SERIES, result)) {
                if (!assemble(right, lastLeft, middle,
                        connections, unavilableForPrallel, MDNode.PARALLEL, result)) {
                    assemble(left, right, middle,
                            connections, unavilableForPrallel, edges, result);
                }

            }
            tree = assemble(left, result, right, connections,
                    unavilableForPrallel, edges);
        }
//        logger.debug("assembled tree:" + tree);
        return tree;
    }

    private static void buildPrimeNode(MDNode node,
            Component toAdd) {
        Set<MDNode> child = toAdd.getTrees();
        for (MDNode c : child) {
            node.addChild(c);
        }

    }

    private static void buildPrimeNode(MDNode node,
            Component toAdd, Set<Component> rest1,
            Set<Component> rest2,
            Map<Component, Component> connections, Map<Component, Component> unavilableForPrallel, Map<String, Object> result) {
        buildPrimeNode(node, toAdd);
        if (toAdd.getOrder() < 0) {
            result.put("left", toAdd);
        } else {
            result.put("right", toAdd);
        }

        if (rest1.size() == 0) {
            return;
        }

        Component limit = connections.get(toAdd);
        if (!rest1.contains(limit)) {
            return;
        }

        Set<Component> restToAdd = new LinkedHashSet();
        boolean addAll = false;
        boolean addRight = toAdd.getPartition() < 1;
        for (Component c : rest1) {
            restToAdd.add(c);
            if (addRight) {
                Component r = unavilableForPrallel.get(c);
                if (c.getPartition() > 1 ||
                        (r != null && r.getPartition() > 1)) {
                    addAll = true;
                    break;

                } else if (r != null && r.getOrder() > limit.getOrder()) {
                    limit = r;
                }

            }
            if (c == limit) {
                break;
            }

        }
        if (addAll) {
            for (Component c : rest1) {
                buildPrimeNode(node, c);
            }

            for (Component c : rest2) {
                buildPrimeNode(node, c);
            }

            rest1.clear();
            rest2.clear();
            return;

        }


        rest1.removeAll(restToAdd);
        for (Component c : restToAdd) {
            buildPrimeNode(node, c, rest2, rest1, connections, unavilableForPrallel, result);
        }

    }

    public static void assemble(
            Set<Component> left, Set<Component> right, MDNode middle,
            Map<Component, Component> connections,
            Map<Component, Component> unavilableForPrallel,
            Map<MDNode, Set<MDNode>> edges,
            Map<String, Object> result) {
        if (left.size() == 0 || right.size() == 0) {
            throw new IllegalArgumentException(
                    "unable to create prime node with empty left or right components");
        }

        Component leftLimit = left.iterator().next();
        left.remove(leftLimit);
        MDNode root = new MDNode(MDNode.PRIME);
        root.addChild(middle);
        buildPrimeNode(root, leftLimit, right, left, connections, unavilableForPrallel, result);
        result.put("middle", root);
        Collection<MDNode> subTrees = root.getChildren();
        if (subTrees == null || subTrees.size() == 0) {
            return;
        }

        Map<MDNode, MDNode> leafToTree = new HashMap();
        for (MDNode child : subTrees) {
            leafToTree.put(child.getRandeomLeaf(), child);
        }

        Set<MDNode> allLeaves = leafToTree.keySet();
        Map<MDNode, Set<MDNode>> restricted =
                filterMap(edges, allLeaves, allLeaves);
        for (MDNode leaf : allLeaves) {
            MDNode tree = leafToTree.get(leaf);
            Set<MDNode> neighbourTrees = new LinkedHashSet();
            Set<MDNode> neighbours = restricted.get(leaf);
            for (MDNode n : neighbours) {
                neighbourTrees.add(leafToTree.get(n));
            }

            tree.setConnections(neighbourTrees);
        }

    }

    public static boolean assemble(Set<Component> components, Component compare, MDNode middle,
            Map<Component, Component> connections, Map<Component, Component> unavilableForPrallel, int type, Map<String, Object> result) {
//        logger.debug("try to assemble:" + type + " with components\n" + components);
//        logger.debug("compare component:" + compare);
        List<Component> candidates = new ArrayList();
        Component last = null;
        for (Component c : components) {
            if (connections.get(c) != compare ||
                    (type == MDNode.PARALLEL &&
                    (c.getPartition() != 1 ||
                    unavilableForPrallel.get(c) != null))) {
                break;
            }

            candidates.add(c);
            last =
                    c;
        }

        if (candidates.size() == 0) {
            return false;
        }
//        Component last = Collections.max(candidates, new Comparator<Component>() {
//
//            public int compare(Component o1, Component o2) {
//                return Math.abs(o1.getOrder()) - Math.abs(o2.getOrder());
//            }
//        });
//        logger.debug("find candidates:" + candidates);
        components.removeAll(candidates);
        MDNode root = new MDNode(type);

        for (Component c : candidates) {
            MDNode node = c.assemble();
//            logger.debug("c:"+c);
//            logger.debug("child:"+node);
//            logger.debug("before add child:"+root);
            root.addChild(node);
//            logger.debug("after add child:"+root);
        }

        root.addChild(middle);
        result.put("middle", root);

        if (type == MDNode.SERIES) {
            result.put("left", last);
            result.put("right", compare);
        } else {
            result.put("left", compare);
            result.put("right", last);
        }

//        logger.debug("assembled subtree:" + root);
        return true;
    }
}//            logger.debug("left components after refined:"+left);
//                logger.debug("right components after refined:"+right);
//            logger.debug("forest before promotion:" + forest);
//            promote(forest);
//            logger.debug("forest after promotion:" + forest);
//            int idx = forest.indexOf(parent);
//            forest.remove(parent);
//            forest.add(idx, n2);
//            if (markLeft) {
//                forest.add(idx, n1);
//            } else {
//                forest.add(idx + 1, n1);
//            }
//        } else {
//            children = parent.getChildren();
//            children.clear();
//            children.add(n1);
//            children.add(n2);
//            if (markLeft) {
//                n1.setMark(MDNode.LEFT_MARK);
//            } else {
//                n1.setMark(MDNode.RIGHT_MARK);
//            }
//        }
//public static void promote(List<MDNode> forest) {
//        for (MDNode node : new ArrayList<MDNode>(forest)) {
//            if (node.getMark() > 0) {
//                for (MDNode child : new ArrayList<MDNode>(node.getChildren())) {
//                    if (child.getMark() > 0) {
//                        addNodeIntoForest(child, forest);
//                    }
//                }
//            }
//        }
//    }
//
//    public static void addNodeIntoForest(MDNode node, List<MDNode> forest) {
//        MDNode parent = node.getParent();
//        parent.removeChild(node);
//        int idx = forest.indexOf(parent);
//        if ((node.getMark() & MDNode.LEFT_MARK) > 0) {
//            forest.add(idx, node);
//        } else if ((node.getMark() & MDNode.RIGHT_MARK) > 0) {
//            forest.add(idx + 1, node);
//        }
//        if (node.getChildren() != null) {
//            for (MDNode child : new ArrayList<MDNode>(node.getChildren())) {
//                if (child.getMark() > 0) {
//                    addNodeIntoForest(child, forest);
//                }
//            }
//        }
//    }
//public static <V> Set<V> removeAll(
//            Collection<V> values,
//            Collection<V> range) {
//        Set<V> removed = new LinkedHashSet();
//        if (values.size() > range.size()) {
//            for (V v : range) {
//                if (values.remove(v)) {
//                    removed.add(v);
//                }
//            }
//        } else {
//            for (V v : values) {
//                if (range.contains(v)) {
//                    values.remove(v);
//                    removed.add(v);
//                }
//            }
//        }
//        return removed;
//    }
//
//    public static <E, V> Map<E, Set<V>> splitMap(Map<E, Set<V>> fullMap,
//            Collection<E> leftRange,
//            Collection<V> rightRange,
//            Map<E, Set<V>> leftMap,
//            Map<E, Set<V>> leftRightMap) {
//        Map<E, Set<V>> rightMap = new HashMap(fullMap);
//        for (E e : leftRange) {
//            Set<V> retained = new HashSet(rightMap.get(e));
//            Set<V> removedValues = removeAll(retained, rightRange);
//            leftMap.put(e, retained);
//            leftRightMap.put(e, removedValues);
//            rightMap.remove(e);
//        }
//        for (Map.Entry<E, Set<V>> entry : rightMap.entrySet()) {
//            Set<V> retained = new HashSet(entry.getValue());
//            retained.retainAll(rightRange);
//            entry.setValue(retained);
//        }
//        return rightMap;
//    }


