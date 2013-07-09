package graph.algorithms;

import edu.uci.ics.jung.graph.Vertex;
import graph.GraphInstance;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import biologicalElements.Elementdeclerations;
import biologicalElements.InternalGraphRepresentation;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ShortestPath {

	GraphInstance graphInstance = new GraphInstance();
	InternalGraphRepresentation graphRepresentation;
	HashMap<String, Pair> vertices = new HashMap<String, Pair>();
	Vector<Pair> priorityQueue = new Vector<Pair>();
	HashMap vertexNames = new HashMap();
	private boolean mindMaps = true;

	String startNode = "";
	String endNode = "";

	public ShortestPath(String start, String end, Boolean mindMaps) {

		graphRepresentation = graphInstance.getPathway()
				.getGraphRepresentation();
		startNode = start;
		endNode = end;
		this.mindMaps = mindMaps;

	}

	private void initNames() {

		Iterator i = GraphInstance.getMyGraph().getAllVertices().iterator();
		while (i.hasNext()) {
			Vertex v = (Vertex) i.next();
			vertexNames.put(v.toString(), v);
		}
	}

	private Vector reconstructShortestPath() {
		Vector<Vertex> v = new Vector<Vertex>();
		String backVertexString = endNode;

		while (!backVertexString.equals("-")) {
			Pair p = vertices.get(backVertexString);
			v.add((Vertex) vertexNames.get(p.getName()));
			backVertexString = p.getPreviousVertex();
		}

		return v;
	}

	private void relax(String fromVertex, String toVertex, int weigth) {

		Pair toVertexPair = vertices.get(toVertex);
		Pair fromVertexPair = vertices.get(fromVertex);

		if (toVertexPair.getAmount() > (fromVertexPair.getAmount() + weigth)) {
			toVertexPair.setAmount(fromVertexPair.getAmount() + weigth);
			toVertexPair.setPreviousVertex(fromVertexPair.getName());
		}

	}

	private void printTableValues() {
		Iterator it = vertices.values().iterator();
		while (it.hasNext()) {
			Pair p = (Pair) it.next();

		}
	}

	private void initStart() {

		Enumeration<String> e = graphRepresentation.getAllVertices();
		while (e.hasMoreElements()) {
			String vertexStr = e.nextElement().toString();

			Pair p = new Pair(vertexStr, 99999);
			vertices.put(p.getName(), p);
			priorityQueue.add(p);
		}

		vertices.get(startNode).setAmount(0);
	}

	public Vector calculateShortestPath() {

		initStart();
		initNames();

		while (!priorityQueue.isEmpty()) {

			Collections.sort(priorityQueue);
			Pair p = priorityQueue.elementAt(0);
			priorityQueue.remove(0);

			if (p.getName().equals(endNode)) {
				return reconstructShortestPath();
			} else {

				Iterator it = graphRepresentation.getVertexNeighbours(
						p.getName()).iterator();
				while (it.hasNext()) {
					String node = it.next().toString();
					// System.out.println("------" + p.getName());
					if (mindMaps) {
						BiologicalNodeAbstract bna = (BiologicalNodeAbstract) graphInstance
								.getPathwayElement(vertexNames.get(p.getName()));
						// System.out.println(bna.getLabel());
						if (bna.getBiologicalElement().equals(
								Elementdeclerations.pathwayMap)) {
							relax(p.getName(), node, 10);
						} else {
							relax(p.getName(), node, 1);
						}
					} else {
						relax(p.getName(), node, 1);
					}
				}
			}
		}
		printTableValues();
		return null;
	}
}

class Pair implements Comparable {

	private String name;
	private String previousVertex;
	private int amount;

	public Pair(String name, int amount) {
		this.name = name;
		this.amount = amount;
		previousVertex = "-";
	}

	public int compareTo(Object anotherPair) {
		if (!(anotherPair instanceof Pair))
			throw new ClassCastException("A Pair object expected.");
		int anotherPairAmount = ((Pair) anotherPair).getAmount();
		return this.amount - anotherPairAmount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getPreviousVertex() {
		return previousVertex;
	}

	public void setPreviousVertex(String previousVertex) {
		this.previousVertex = previousVertex;
	}
}