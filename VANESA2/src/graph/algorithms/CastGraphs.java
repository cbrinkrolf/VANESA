package graph.algorithms;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.Pair;
import edu.uci.ics.jung.utils.PredicateUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.collections.Predicate;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;


public class CastGraphs {
	
	public static ResultGraph toDirected(Pathway pathway){
		
		DirectedSparseGraph outGraph = new DirectedSparseGraph();
		HashMap<Vertex,Vertex> mapping = new HashMap<Vertex,Vertex>();
		HashMap<Vertex,Vertex> mapping2 = new HashMap<Vertex,Vertex>();
		
		HashSet<BiologicalNodeAbstract> allVertices = pathway.getAllNodes();
		
		for (BiologicalNodeAbstract bna : allVertices) {
			
//			outGraph.addVertex(bna.getVertex());
//			bna.getVertex().copy(outGraph);
//			Vertex newV = (DirectedSparseVertex) bna.getVertex().copy(outGraph);
//			Vertex newVertex = new DirectedSparseVertex();
			Vertex newVertex = outGraph.addVertex(new DirectedSparseVertex());
			
			mapping.put(newVertex, bna.getVertex());
			mapping2.put(bna.getVertex(), newVertex);
		
		}
		
		HashSet<BiologicalEdgeAbstract> allEdges = pathway.getAllEdges();
		
		for (BiologicalEdgeAbstract bea : allEdges) {
			Vertex v1, v2;
			Edge e = bea.getEdge();
			Pair p = e.getEndpoints();
			
			v1 = mapping2.get(p.getFirst());
			v2 = mapping2.get(p.getSecond());
			
			DirectedSparseEdge newEdge;

			if(bea.isDirected()){
//				outGraph.addEdge(bea.getEdge());
//				bea.getEdge().copy(outGraph);
				newEdge = new DirectedSparseEdge(v1, v2);
				outGraph.addEdge(newEdge);
				
			}else{
			
				newEdge = new DirectedSparseEdge(v1, v2);
				try {
					outGraph.addEdge(newEdge);
				} catch (edu.uci.ics.jung.exceptions.ConstraintViolationException ee) {
					System.err.println("ex. catched!");
					Predicate pred = ee.getViolatedConstraint();
					//in the most cases its this: [ParallelEdgePredicate=true]
					Map m = PredicateUtils.evaluateNestedPredicates(pred, newEdge);
					ee.printStackTrace();
				}
				
				if (!v2.equals(v1)){
					newEdge = new DirectedSparseEdge(v2, v1);	
					try {
						outGraph.addEdge(newEdge);
					} catch (edu.uci.ics.jung.exceptions.ConstraintViolationException ee) {
						System.err.println("ex. catched!");
						ee.printStackTrace();
					}
				}
				
			}
		}
		
		return new ResultGraph(outGraph, mapping);
	}
}



