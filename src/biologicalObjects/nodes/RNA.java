package biologicalObjects.nodes;

//import edu.uci.ics.jung.graph.Vertex;


public class RNA extends BiologicalNodeAbstract{

	private String ntSequence = "";
	
	public RNA(String label, String name) {
		super(label, name);
		// TODO Auto-generated constructor stub
	}

	public String getNtSequence() {
		return ntSequence;
	}

	public void setNtSequence(String ntSequence) {
		this.ntSequence = ntSequence;
	}

}
