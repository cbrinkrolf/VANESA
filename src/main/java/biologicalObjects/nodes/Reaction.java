package biologicalObjects.nodes;

import java.util.List;

import biologicalElements.Elementdeclerations;

public class Reaction extends BiologicalNodeAbstract implements DynamicNode {

	private String maximalSpeed = "1";
	private boolean knockedOut = false;

	public Reaction(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.reaction);
		attributeSetter(this.getClass().getSimpleName(), this);
	}

	@Override
	public String getMaximalSpeed() {
		return this.maximalSpeed;
	}

	@Override
	public void setMaximalSpeed(String maximalSpeed) {
		this.maximalSpeed = maximalSpeed;
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
