package graph.algorithms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
//import edu.uci.ics.jung.graph.Vertex;
import graph.GraphInstance;

public class ShortestPath {

	GraphInstance graphInstance = new GraphInstance();
	HashMap<BiologicalNodeAbstract, Pair> vertices = new HashMap<BiologicalNodeAbstract, Pair>();
	Vector<Pair> priorityQueue = new Vector<Pair>();
	//HashMap vertexNames = new HashMap();
	private boolean mindMaps = true;
	Pathway pw;

	BiologicalNodeAbstract startNode;
	BiologicalNodeAbstract endNode;

	public ShortestPath(BiologicalNodeAbstract start, BiologicalNodeAbstract end, Boolean mindMaps) {

		pw = graphInstance.getPathway();
		startNode = start;
		endNode = end;
		this.mindMaps = mindMaps;

	}

	private Vector<BiologicalNodeAbstract> reconstructShortestPath() {
		Vector<BiologicalNodeAbstract> v = new Vector<BiologicalNodeAbstract>();
		BiologicalNodeAbstract backVertexString = endNode;

		while (backVertexString != null) {
			//System.out.println("back: "+backVertexString);
			Pair p = vertices.get(backVertexString);
			v.add(p.getName());
			backVertexString = p.getPreviousVertex();
		}

		return v;
	}

	private void relax(BiologicalNodeAbstract fromVertex, BiologicalNodeAbstract toVertex, int weigth) {

		Pair toVertexPair = vertices.get(toVertex);
		Pair fromVertexPair = vertices.get(fromVertex);

		if (toVertexPair.getAmount() > (fromVertexPair.getAmount() + weigth)) {
			toVertexPair.setAmount(fromVertexPair.getAmount() + weigth);
			toVertexPair.setPreviousVertex(fromVertexPair.getName());
		}

	}

	/*private void printTableValues() {
		Iterator it = vertices.values().iterator();
		while (it.hasNext()) {
			Pair p = (Pair) it.next();

		}
	}*/

	private void initStart() {

		//Enumeration<String> e = graphRepresentation.getAllVertices();
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			//String vertexStr = e.nextElement().toString();

			Pair p = new Pair(bna, 99999);
			vertices.put(p.getName(), p);
			priorityQueue.add(p);
		}

		vertices.get(startNode).setAmount(0);
	}

	public Vector<BiologicalNodeAbstract> calculateShortestPath() {

		initStart();
		
		while (!priorityQueue.isEmpty()) {

			Collections.sort(priorityQueue);
			Pair p = priorityQueue.elementAt(0);
			priorityQueue.remove(0);

			if (p.getName().equals(endNode)) {
				return reconstructShortestPath();
			} else {

				Iterator<BiologicalNodeAbstract> it = pw.getGraph().getJungGraph().getNeighbors(p.getName()).iterator();//graphRepresentation.getVertexNeighbours(
						//p.getName()).iterator();
				BiologicalNodeAbstract node;
				while (it.hasNext()) {
					node = it.next();
					// System.out.println("------" + p.getName());
					if (mindMaps) {
						//BiologicalNodeAbstract bna = (BiologicalNodeAbstract) graphInstance
						//		.getPathwayElement(vertexNames.get(p.getName()));
						// System.out.println(bna.getLabel());
						if (node.getBiologicalElement().equals(
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
		//printTableValues();
		return null;
	}
}

class Pair implements Comparable<Object> {

	private BiologicalNodeAbstract name;
	private BiologicalNodeAbstract previousVertex;
	private int amount;

	public Pair(BiologicalNodeAbstract name, int amount) {
		this.name = name;
		this.amount = amount;
		previousVertex = null;
	}

	public int compareTo(Object anotherPair) {
		if (!(anotherPair instanceof Pair))
			throw new ClassCastException("A Pair object expected.");
		int anotherPairAmount = ((Pair) anotherPair).getAmount();
		return this.amount - anotherPairAmount;
	}

	public BiologicalNodeAbstract getName() {
		return name;
	}

	public void setName(BiologicalNodeAbstract name) {
		this.name = name;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public BiologicalNodeAbstract getPreviousVertex() {
		return previousVertex;
	}

	public void setPreviousVertex(BiologicalNodeAbstract previousVertex) {
		this.previousVertex = previousVertex;
	}
}