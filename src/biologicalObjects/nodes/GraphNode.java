package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

/**
 * 
 * @author mlewinsk
 * June 2014
 */
public class GraphNode extends BiologicalNodeAbstract {
	
	/**
	 * Placeholder for Nodes containing information from the graph DB.
	 * @param label
	 * @param name
	 */
	public GraphNode(String label, String name){
		super(label,name);
		
		setBiologicalElement(Elementdeclerations.others);
		shapes = new VertexShapes();	
		setShape(shapes.getEllipse());
	}

}
