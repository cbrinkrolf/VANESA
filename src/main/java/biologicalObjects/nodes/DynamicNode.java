package biologicalObjects.nodes;

public interface DynamicNode {
	String getMaximalSpeed();

	void setMaximalSpeed(String maximalSpeed);

	boolean isKnockedOut();

	void setKnockedOut(Boolean knockedOut);
}
