package graph.algorithms.alignment;

import java.util.HashMap;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;

public class AdjacencyMatrix {
	
	private Pathway pathway;
	
	private DoubleMatrix2D matrix;
	
	private HashMap<String, Integer> id2position;
	private HashMap<Integer, String> position2id;
	
	private HashMap<String, Integer> name2position;
	private HashMap<Integer, String> position2name;
	
//	private HashMap<String, Integer> vertexID2position;
//	private HashMap<Integer, String> position2vertexID;
	
	private int noNodes, noEdges;
	
	public AdjacencyMatrix(Pathway pw){
		
		this.pathway = pw;
		noNodes = pathway.getAllNodes().size();
		noEdges = pathway.getAllEdges().size();
		
		id2position = new HashMap<String, Integer>();
		position2id = new HashMap<Integer, String>();
		
		name2position = new HashMap<String, Integer>();
		position2name = new HashMap<Integer, String>();
		
		this.matrix = new DenseDoubleMatrix2D(noNodes, noNodes);
		buildMatrix();
		
	}

	private void buildMatrix() {
		
		int position = 0;
		String nodeID = "";
		HashMap<Vertex, Integer> node2position = new HashMap<Vertex, Integer>(); 
		for (Object element : pathway.getAllNodes()) {
			
			BiologicalNodeAbstract node = (BiologicalNodeAbstract) element;
			Integer i = new Integer(position);
			
			node2position.put(node.getVertex(), i);
			
			nodeID = node.getVertex().toString();
			//System.out.println(nodeID);
			id2position.put(nodeID, i);
			position2id.put(i, nodeID);
			
			name2position.put(node.getName(), i);
			position2name.put(i, node.getName());
			
			position++;
		}
		
		for (Object element : pathway.getAllEdges()) {
			BiologicalEdgeAbstract bioEdge = (BiologicalEdgeAbstract) element;
			Edge edge = bioEdge.getEdge();
			Pair pair = edge.getEndpoints();
			Vertex first = (Vertex) pair.getFirst();
			Vertex second = (Vertex) pair.getSecond();
			int x = (node2position.get(first)).intValue();
			int y = (node2position.get(second)).intValue();
			matrix.set(x, y, 1);
			if (!bioEdge.isDirected()) {
				matrix.set(y, x, 1);
			}
		}
		
	}
	
	public DoubleMatrix2D getMatrix(){
		
		return matrix;
		
	}

	public HashMap<String, Integer> getId2position() {
		return id2position;
	}

	public HashMap<Integer, String> getPosition2id() {
		return position2id;
	}

	public HashMap<Integer, String> getPosition2name() {
		return position2name;
	}

	public void setPosition2name(HashMap<Integer, String> position2name) {
		this.position2name = position2name;
	}

	public HashMap<String, Integer> getName2position() {
		return name2position;
	}

	public void setName2position(HashMap<String, Integer> name2position) {
		this.name2position = name2position;
	}

}
