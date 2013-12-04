package biologicalObjects.nodes;

import javax.swing.tree.DefaultMutableTreeNode;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class CollectorNode extends BiologicalNodeAbstract{
	
	BiologicalNodeAbstract parent;
	DefaultMutableTreeNode parentTreeNode;
	String elementObject;
	
	public CollectorNode(String label, String count) {
		super(label, count+" Elemente");
		setBiologicalElement(Elementdeclerations.collector);
		shapes = new VertexShapes();	
		setShape(shapes.getEllipse());
		setReference(false);
	}
	
	public void setParent(BiologicalNodeAbstract p){
		parent = p;
	}
	
	public BiologicalNodeAbstract getParent(){
		return parent;
	}

	public void setParentTreeNode(DefaultMutableTreeNode t) {
		parentTreeNode = t;		
	}
	
	public DefaultMutableTreeNode getParentTreeNode(){
		return parentTreeNode;
	}

	public void setObject(String object) {
		elementObject = object;
	}
	
	public String getObject(){
		return elementObject;
	}

}
