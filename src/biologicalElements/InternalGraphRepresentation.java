package biologicalElements;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

//import edu.uci.ics.jung.graph.Edge;
//import edu.uci.ics.jung.graph.Vertex;

public class InternalGraphRepresentation {

	private final HashMap vertices = new HashMap();

	public InternalGraphRepresentation() {

	}

	public void addVertex(BiologicalNodeAbstract v) {
		//System.out.println("nodes: "+this.vertices.size());
		String vertexLabel = v.toString();

		if (!vertices.containsKey(vertexLabel)) {
			vertices.put(vertexLabel, new Vector());
		}
	}

	public void addEdge(BiologicalEdgeAbstract bea) {

		String vertexLabelFrom = bea.getFrom().toString();
		String vertexLabelTo = bea.getTo().toString();

		if (!vertices.containsKey(vertexLabelFrom)) {

			Vector v = new Vector();
			Object[] elements = { vertexLabelTo, bea.toString() };
			v.add(elements);
			vertices.put(vertexLabelFrom, v);

		} else {
			Vector v = (Vector) vertices.get(vertexLabelFrom);
			Object[] elements = { vertexLabelTo, bea.toString() };
			v.add(elements);
		}
	}

	public void removeVertex(BiologicalNodeAbstract v) {

		String vertexLabel = v.toString();

		if (vertices.containsKey(vertexLabel)) {
			vertices.remove(vertexLabel);

		}
	}

	public void removeAllVertices() {
		vertices.clear();
	}

	public void removeEdge(BiologicalEdgeAbstract bea) {

		String vertexLabelFrom = bea.getFrom().toString();
		String vertexLabelTo = bea.getTo().toString();
		Vector<Object[]> deleteEdges = new Vector<Object[]>();

		if (vertices.containsKey(vertexLabelFrom)) {

			Vector v = (Vector) vertices.get(vertexLabelFrom);

			Vector vCopy = (Vector) v.clone();
			Iterator it = vCopy.iterator();
			while (it.hasNext()) {

				Object[] tempArray = (Object[]) it.next();
				if (tempArray[0].equals(vertexLabelTo)
						&& tempArray[1].equals(bea.toString())) {
					deleteEdges.add(tempArray);
					// v.remove(tempArray);
				}
			}

			Iterator it2 = deleteEdges.iterator();
			while (it2.hasNext()) {
				Object[] tempArray = (Object[]) it2.next();
				v.remove(tempArray);
			}
		}
	}

	/*public void removeAllEdges() {

		for (Enumeration e = vertices.elements(); e.hasMoreElements();) {
			((Vector) e.nextElement()).clear();
		}
	}*/

	public Set<String> getAllVertices() {

		return vertices.keySet();
	}

	public int getVerticesAmount() {
		return vertices.size();
	}

	/*public Vector getVertexNeighbours(String vertex) {

		Vector v = new Vector();

		Iterator it = ((Vector) vertices.get(vertex)).iterator();
		while (it.hasNext()) {
			String vertexNode = ((Object[]) it.next())[0].toString();
			v.add(vertexNode);
		}

		Enumeration e = vertices.keys();
		while (e.hasMoreElements()) {
			String vertexNode = e.nextElement().toString();

			Iterator it2 = ((Vector) vertices.get(vertexNode)).iterator();
			while (it2.hasNext()) {
				String newVertexNode = ((Object[]) it2.next())[0].toString();
				if (newVertexNode.equals(vertex))
					v.add(vertexNode);
			}
		}

		return v;
	}*/

	/*public boolean are2NodesConnectedThroughOneElement(Vertex one, Vertex two) {

		String vertexLabelFrom = one.toString();
		String vertexLabelTo = two.toString();

		if (vertices.containsKey(vertexLabelFrom)) {

			Vector otherNodes = (Vector) vertices.get(vertexLabelFrom);
			Iterator it = otherNodes.iterator();

			while (it.hasNext()) {

				Object[] elements = (Object[]) it.next();
				// Vertex newNode = (Vertex)elements[0];

				if (vertices.containsKey(elements[0].toString())) {

					Vector otherNodes2 = (Vector) vertices.get(elements[0]
							.toString());
					Iterator it2 = otherNodes2.iterator();

					while (it2.hasNext()) {

						Object[] elements2 = (Object[]) it2.next();
						if (vertexLabelTo.equals(elements2[0].toString())) {

							return true;
						}

					}
				}
			}

		} else if (vertices.containsKey(vertexLabelTo)) {

			Vector otherNodes = (Vector) vertices.get(vertexLabelTo);
			Iterator it = otherNodes.iterator();

			while (it.hasNext()) {

				Object[] elements = (Object[]) it.next();
				// Vertex newNode = (Vertex)elements[0];

				if (vertices.containsKey(elements[0])) {

					Vector otherNodes2 = (Vector) vertices.get(elements[0]);
					Iterator it2 = otherNodes2.iterator();

					while (it2.hasNext()) {

						Object[] elements2 = (Object[]) it2.next();
						if (vertexLabelFrom.equals(elements2[0].toString()))
							return true;
					}
				}
			}
		} else {
			return false;
		}

		return false;
	}*/

	/*public int getNodeDegree(Vertex v) {

		return v.inDegree() + v.outDegree();
	}*/

	public int getHeighestNodeDegree() {

		return 1;
	}

	public boolean doesEdgeExist(BiologicalNodeAbstract from, BiologicalNodeAbstract to) {

		String vertexLabelFrom = from.toString();
		String vertexLabelTo = to.toString();

		if (vertices.containsKey(vertexLabelFrom)) {
			Vector v = (Vector) vertices.get(vertexLabelFrom);
			Iterator it = v.iterator();
			while (it.hasNext()) {
				Object[] tempArray = (Object[]) it.next();
				if (tempArray[0].equals(vertexLabelTo)) {
					return true;
				}
			}
		}
		return false;
	}
}
