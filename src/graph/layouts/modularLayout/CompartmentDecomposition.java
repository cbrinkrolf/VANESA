/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.layouts.modularLayout;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Besitzer
 */
public class CompartmentDecomposition {

    public final static String MD_KEY = "ModularDecomposition";

    private void addEdgeInMap(Map<MDNode, Set<MDNode>> neighboursMap, Vertex source, Vertex target) {
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

    public static <E> MDNode decomposition(List<MDNode> vertices, Map<MDNode, Set<MDNode>> edges) {
        Map<String, Set<MDNode>> compartmentMap = new HashMap();
        for (MDNode v : vertices) {
            String c = v.getCompartment();
            Set<MDNode> others = compartmentMap.get(c);
            if (others == null) {
                others = new HashSet();
                compartmentMap.put(c, others);
            }
            others.add(v);
        }
        MDNode root = new MDNode();
        Map<String,MDNode> branchMap=new HashMap();
        for(String c:compartmentMap.keySet()){
            Set<MDNode> compartment=compartmentMap.get(c);
            Map<MDNode, Set<MDNode>> internEdges=
                    ModularDecomposition.filterMap(edges, compartment, compartment);
            MDNode parent=ModularDecomposition.decomposition(new ArrayList(compartment),internEdges);
            parent.setCompartment(c);
            branchMap.put(c, parent);
            root.addChild(parent);
        }
        for(MDNode n:edges.keySet()){
            Set<MDNode> neighbours=new HashSet(edges.get(n));
//            Set<MDNode> intern=new HashSet(neighbours);
            Set<MDNode> compartment=compartmentMap.get(n.getCompartment());
//            intern.retainAll(compartment);
            neighbours.removeAll(compartment);
//            if(intern.size()>0){
//                n.setConnections(intern);
//            }
            MDNode parent=branchMap.get(n.getCompartment());
            Set<MDNode> externConnections=parent.getConnections();
            if(externConnections==null){
                externConnections=new HashSet();
            }
            for(MDNode v:neighbours){
                externConnections.add(branchMap.get(v.getCompartment()));
            }
            if(externConnections.size()>0){
                parent.setConnections(externConnections);
            }
        }
        return root;
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
}

