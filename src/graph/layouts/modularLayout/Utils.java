/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.layouts.modularLayout;

import java.io.File;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLFile;



/**
 *
 * @author Besitzer
 */
public class Utils {
//    public static void main(String[] args) {
//        ModularDecomposition md = new ModularDecomposition();
////        Graph graph = md.loadGraph();
//        Graph graph = loadGraph();
//        MDNode root=md.decomposition(graph);
//        System.out.println(root);
//
//    }
    public static Graph loadGraph(File file){
        GraphMLFile loader=createGraphMLFile();
        Graph graph=loader.load(file.getAbsolutePath());
        return graph;
    }
            
    public static Graph loadGraph() {
        String filename = "resources/simple12.graphml";
        GraphMLFile loader=createGraphMLFile();
        // create a simple graph for the demo
        Graph graph = loader.load(filename);
//graph=TestGraphs.createTestGraph(false);
        return graph;
    }
    
    public static  GraphMLFile createGraphMLFile(){
        return new GraphMLFile();
    }
}
