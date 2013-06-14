package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
import edu.uci.ics.jung.graph.Edge;

/**
 * 
 * @author Olga
 *
 */

/**
 * create Feature
 */
public class Feature extends BiologicalEdgeAbstract {

	/**
	 * create Feature on
	 * 
	 * @param edge
	 * @param label
	 * @param name
	 */
	public Feature(Edge edge, String label, String name) {
		super(edge, label, name);
		setBiologicalElement(Elementdeclerations.feature);
		setAbstract(false);
	}

}
