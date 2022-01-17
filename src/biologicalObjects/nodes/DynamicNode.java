package biologicalObjects.nodes;

public interface DynamicNode {
	
	public String getMaximalSpeed();
	public void setMaximalSpeed(String maximumSpeed);
	
	public boolean isKnockedOut();
	public void setKnockedOut(Boolean knockedOut);
}
