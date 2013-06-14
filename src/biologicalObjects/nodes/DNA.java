package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;


public class DNA extends BiologicalNodeAbstract {
	
	private String ntSequence = "";

	public DNA(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.dna);
		shapes = new VertexShapes();	
		setShape(shapes.getRoundRectangle());
		setAbstract(false);
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(vs.getRoundRectangle(getVertex()));
	}
	
	public String getNtSequence() {
		return ntSequence;
	}

	public void setNtSequence(String ntSequence) {
		this.ntSequence = ntSequence;
	}

}
