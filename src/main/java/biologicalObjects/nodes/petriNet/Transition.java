package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.List;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class Transition extends PNNode {
	private boolean simulationActive;
	private String firingCondition = "true";// "time>9.8";
	private boolean knockedOut = false;

	public Transition(String label, String name) {
		super(label, name);
		if (label.equals(""))
			setLabel(name);
		if (name.equals(""))
			setName(label);
		AffineTransform transform2 = new AffineTransform();
		transform2.translate(1, 1);
		transform2.scale(1, 2);
		setDefaultShape(transform2.createTransformedShape(new VertexShapes().getRectangle()));
		setDefaultColor(Color.white);
		setBiologicalElement(Elementdeclerations.transition);
	}

	public boolean isSimulationActive() {
		return simulationActive;
	}

	public void setSimulationActive(boolean simulationActive) {
		this.simulationActive = simulationActive;
	}

	public String getFiringCondition() {
		return firingCondition;
	}

	public void setFiringCondition(String firingCondition) {
		this.firingCondition = firingCondition.trim();
	}

	public boolean isKnockedOut() {
		return knockedOut;
	}

	public void setKnockedOut(boolean knockedOut) {
		this.knockedOut = knockedOut;
	}

	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("firingCondition");
		return list;
	}
}
