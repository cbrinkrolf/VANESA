package biologicalObjects.nodes;

import java.util.List;

import biologicalElements.Elementdeclerations;

public class Reaction extends BiologicalNodeAbstract implements DynamicNode {
	private String maximalSpeed = "1";
	private boolean knockedOut = false;

	public Reaction(final String label, final String name) {
		super(label, name, Elementdeclerations.reaction);
		attributeSetter(getClass().getSimpleName(), this);
	}

	@Override
	public String getMaximalSpeed() {
		return maximalSpeed;
	}

	@Override
	public void setMaximalSpeed(String maximalSpeed) {
		this.maximalSpeed = maximalSpeed;
	}

	@Override
	public boolean isKnockedOut() {
		return knockedOut;
	}

	@Override
	public void setKnockedOut(Boolean knockedOut) {
		this.knockedOut = knockedOut;
	}

	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("maximalSpeed");
		list.add("isKnockedOut");
		return list;
	}

	@Override
	public String getTransformationParameterValue(String parameter) {
		switch (parameter) {
		case "maximalSpeed":
			return getMaximalSpeed();
		case "isKnockedOut":
			return String.valueOf(isKnockedOut());
		}
		return super.getTransformationParameterValue(parameter);
	}
}
