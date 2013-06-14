package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

/**
 * 
 * @author Olga
 *
 */

/**
 * create domain
 */
public class Domain extends BiologicalNodeAbstract {

	String name = "";

	/**
	 * create domain on
	 * 
	 * @param label
	 * @param name
	 * @param vertex
	 */
	public Domain(String label, String name, Vertex vertex) {
		super(label, name, vertex);
		this.name = label;

		setBiologicalElement(Elementdeclerations.domain);
		VertexShapes shapes = new VertexShapes();

		setBiologicalElement(Elementdeclerations.feature);
		shapes = new VertexShapes();

		setShape(shapes.getRegularStar(vertex, 6));
		setAbstract(false);
	}

	/**
	 * get the name of the domain
	 * 
	 * @return name
	 */
	public String getDomainName() {
		return name;
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		setShape(vs.getRegularStar(getVertex(), 6));
	}
}
