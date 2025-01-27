package graph.algorithms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;

public class ShortestPath {
	private final HashMap<BiologicalNodeAbstract, Pair> vertices = new HashMap<>();
	private final Vector<Pair> priorityQueue = new Vector<>();
	private final boolean mindMaps;
	private final Pathway pw;
	private final BiologicalNodeAbstract startNode;
	private final BiologicalNodeAbstract endNode;

	public ShortestPath(BiologicalNodeAbstract start, BiologicalNodeAbstract end, Boolean mindMaps) {
		pw = GraphInstance.getPathway();
		startNode = start;
		endNode = end;
		this.mindMaps = mindMaps;
	}

	private Vector<BiologicalNodeAbstract> reconstructShortestPath() {
		Vector<BiologicalNodeAbstract> v = new Vector<>();
		BiologicalNodeAbstract backVertexString = endNode;
		while (backVertexString != null) {
			Pair p = vertices.get(backVertexString);
			v.add(p.getName());
			backVertexString = p.getPreviousVertex();
		}
		return v;
	}

	private void relax(BiologicalNodeAbstract fromVertex, BiologicalNodeAbstract toVertex, int weight) {
		Pair toVertexPair = vertices.get(toVertex);
		Pair fromVertexPair = vertices.get(fromVertex);
		if (toVertexPair.getAmount() > (fromVertexPair.getAmount() + weight)) {
			toVertexPair.setAmount(fromVertexPair.getAmount() + weight);
			toVertexPair.setPreviousVertex(fromVertexPair.getName());
		}
	}

	private void initStart() {
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
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
			}
			for (BiologicalNodeAbstract node : pw.getGraph().getJungGraph().getNeighbors(p.getName())) {
				if (mindMaps) {
					if (node.getBiologicalElement().equals(Elementdeclerations.pathwayMap)) {
						relax(p.getName(), node, 10);
					} else {
						relax(p.getName(), node, 1);
					}
				} else {
					relax(p.getName(), node, 1);
				}
			}
		}
		return null;
	}

	private static class Pair implements Comparable<Pair> {
		private final BiologicalNodeAbstract name;
		private BiologicalNodeAbstract previousVertex;
		private int amount;

		public Pair(BiologicalNodeAbstract name, int amount) {
			this.name = name;
			this.amount = amount;
		}

		public int compareTo(Pair anotherPair) {
			return this.amount - anotherPair.getAmount();
		}

		public BiologicalNodeAbstract getName() {
			return name;
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
}