package biologicalObjects.nodes;

import java.util.List;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;

public class Reaction extends BiologicalNodeAbstract implements DynamicNode {
	private String maximalSpeed = "1";
	private boolean knockedOut = false;

	public Reaction(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.reaction, pathway);
		attributeSetter();
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

	public List<String> getTransformationParameters() {
		final List<String> list = super.getTransformationParameters();
		list.add("maximalSpeed");
		list.add("isKnockedOut");
		return list;
	}

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
