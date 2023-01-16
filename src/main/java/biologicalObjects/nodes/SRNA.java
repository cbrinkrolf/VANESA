package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SRNA extends RNA {

	private String tarbase_DS = "";
	private String tarbase_IS = "";
	private String tarbase_ensemble = "";
	private String tarbase_accession = "";

	public SRNA(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.sRNA);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
