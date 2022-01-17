package biologicalObjects.nodes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RNA extends BiologicalNodeAbstract {

	private String ntSequence = "";

	public RNA(String label, String name) {
		super(label, name);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
