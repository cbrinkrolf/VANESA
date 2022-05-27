package biologicalObjects.nodes;

import java.util.List;

import biologicalElements.Elementdeclerations;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Enzyme extends Protein implements DynamicNode {

	private String maximalSpeed = "1";
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean knockedOut = false;

	public Enzyme(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.enzyme);
		attributeSetter(this.getClass().getSimpleName(), this);
	}

	@Override
	public boolean isKnockedOut() {
		return this.knockedOut;
	}

	@Override
	public void setKnockedOut(Boolean knockedOut) {
		this.knockedOut = knockedOut;
	}

	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("maximalSpeed");
		return list;
	}

	public String getTransformationPaameterValue(String parameter) {
		switch (parameter) {
		case "maximalSpeed":
			return getMaximalSpeed();
		}
		return super.getTransformationParameterValue(parameter);
	}
}
