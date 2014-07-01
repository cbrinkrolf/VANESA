package biologicalObjects.nodes;

import cluster.graphdb.GraphDBTransportNode;
import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

/**
 * 
 * @author mlewinsk
 * June 2014
 */
public class GraphNode extends BiologicalNodeAbstract {
	
	
	private GraphDBTransportNode supernode;
	
	
	
	
	/**
	 * Placeholder for Nodes containing information from the graph DB.
	 * @param label
	 * @param name
	 */
	public GraphNode(GraphDBTransportNode supernode){
		super(supernode.commonName,supernode.fullName);
		this.supernode = supernode;
		setBiologicalElement(Elementdeclerations.graphdbnode);
		shapes = new VertexShapes();	
		setShape(shapes.getEllipse());
	}
	
	
	public GraphDBTransportNode getSuperNode(){
		return supernode;
	}

}
