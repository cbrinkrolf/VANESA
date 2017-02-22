package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class Reaction extends BiologicalNodeAbstract implements DynamicNode {
	
	private String maximumSpeed = "1";
	private boolean knockedOut = false;

	public Reaction(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.reaction);
		shapes = new VertexShapes();
		attributeSetter(this.getClass().getSimpleName(), this);
	}

	@Override
	public String getMaximumSpeed() {
		return this.maximumSpeed;
	}

	@Override
	public void setMaximumSpeed(String maximumSpeed) {
		this.maximumSpeed = maximumSpeed;
	}

	@Override
	public boolean isKnockedOut() {
		return this.knockedOut;
	}

	@Override
	public void setKnockedOut(Boolean knockedOut) {
		this.knockedOut = knockedOut;
	}
}
