package biologicalElements;

import java.util.HashMap;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

//import edu.uci.ics.jung.graph.Edge;
//import edu.uci.ics.jung.graph.Vertex;

public class InternalGraphRepresentation {

	private final HashMap<BiologicalNodeAbstract, HashMap<BiologicalNodeAbstract, BiologicalEdgeAbstract>> vertices = new HashMap<BiologicalNodeAbstract, HashMap<BiologicalNodeAbstract, BiologicalEdgeAbstract>>();

	public InternalGraphRepresentation() {

	}

	public void addVertex(BiologicalNodeAbstract v) {
		// System.out.println("nodes: "+this.vertices.size());
		// String vertexLabel = v.toString();

		if (!vertices.containsKey(v)) {
			vertices.put(
					v,
					new HashMap<BiologicalNodeAbstract, BiologicalEdgeAbstract>());
			// vertices.put(vertexLabel, new Vector());
		}
	}

	public void addEdge(BiologicalEdgeAbstract bea) {

		BiologicalNodeAbstract from = bea.getFrom();
		BiologicalNodeAbstract to = bea.getTo();

		if (!vertices.containsKey(from)) {
			vertices.put(
					from,
					new HashMap<BiologicalNodeAbstract, BiologicalEdgeAbstract>());
		}
		vertices.get(from).put(to, bea);
	}

	public void removeVertex(BiologicalNodeAbstract v) {

		if (vertices.containsKey(v)) {
			vertices.remove(v);
		} else {
			System.err.println("Vertex does not exist!");
		}
	}

	public void removeEdge(BiologicalEdgeAbstract bea) {

		BiologicalNodeAbstract from = bea.getFrom();
		BiologicalNodeAbstract to = bea.getTo();

		if (vertices.containsKey(from)) {
			if (vertices.get(from).get(to) != null) {
				vertices.get(from).remove(to);
			} else {
				System.err.println("Edge doese not exist!");
			}
		}
	}
	
	public boolean doesEdgeExist(BiologicalNodeAbstract from, BiologicalNodeAbstract to){
		if(!vertices.containsKey(from)){
			return false;
		}
		if(!vertices.get(from).containsKey(to)){
			return false;
		}
		return true;
	}
	
	public BiologicalEdgeAbstract getEdge(BiologicalNodeAbstract from, BiologicalNodeAbstract to){
		if(!vertices.containsKey(from)){
			return null;
		}
		if(!vertices.get(from).containsKey(to)){
			return null;
		}
		return vertices.get(from).get(to);
	}
}
