package biologicalObjects.nodes;

public class RNA extends BiologicalNodeAbstract{

	private String ntSequence = "";
	
	public RNA(String label, String name) {
		super(label, name);
		attributeSetter(this.getClass().getSimpleName(), this);
	}

	public String getNtSequence() {
		return ntSequence;
	}

	public void setNtSequence(String ntSequence) {
		this.ntSequence = ntSequence;
	}

}
