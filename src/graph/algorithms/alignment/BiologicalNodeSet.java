package graph.algorithms.alignment;

import java.util.HashMap;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class BiologicalNodeSet {

	public String elementsDeclaration;
	public String graphName;
	
	public HashMap<String, Integer> id2Position;
	public HashMap<Integer, String> position2ID;
	public HashMap<String, BiologicalNodeAbstract> id2Node;
	public HashMap<BiologicalNodeAbstract, String> node2ID;
	
	private int size;
	
			
	public BiologicalNodeSet(String declaration, String graph){
		
		elementsDeclaration = declaration;
		graphName = graph;
		id2Position = new HashMap<String, Integer>();
		position2ID = new HashMap<Integer, String>();
		id2Node  = new HashMap<String, BiologicalNodeAbstract>();
		node2ID = new HashMap<BiologicalNodeAbstract, String>();
		size = 0;
	}
	
	public void addNode(BiologicalNodeAbstract node){
		
		String id = node.getName();
		Integer position = new Integer(size);
		id2Node.put(id, node);
		node2ID.put(node, id);
		position2ID.put(position, id);
		id2Position.put(id, position);
		size++;
		
	}
	
	public int getSize(){
		return this.size;
	}
	
}
