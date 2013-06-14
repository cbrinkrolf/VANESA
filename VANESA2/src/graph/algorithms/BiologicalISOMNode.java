package graph.algorithms;

import java.util.Vector;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import edu.uci.ics.jung.graph.Vertex;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class BiologicalISOMNode {
	
	private BiologicalNodeAbstract node;
	private Vector3f pos;
//	int pos;
	
	public BiologicalISOMNode(BiologicalNodeAbstract node){
		this.node = node;
		pos = new Vector3f();
	}
	
	public BiologicalNodeAbstract getNode(){
		return node;
	}
	
	public Vector3f getPos(){
		return this.pos;
	}
	
	public void setPos(Vector3f pos){
		this.pos = pos;
	}
	
}
