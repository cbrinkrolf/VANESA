package biologicalObjects.nodes;

import java.util.List;

import biologicalElements.Elementdeclerations;

public class Enzyme extends Protein implements DynamicNode {
	private String maximalSpeed = "1";
	private boolean knockedOut = false;

	public Enzyme(final String label, final String name) {
		super(label, name, Elementdeclerations.enzyme);
		attributeSetter(getClass().getSimpleName(), this);
	}

	public String getMaximalSpeed() {
		return maximalSpeed;
	}

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
