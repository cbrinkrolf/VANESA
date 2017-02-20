package biologicalObjects.nodes.petriNet;

import java.awt.Color;

import biologicalElements.Elementdeclerations;

public class DiscreteTransition extends Transition{

	private double delay = 1;
	public DiscreteTransition(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.discreteTransition);
		setColor(Color.WHITE);
	}
	public double getDelay() {
		return delay;
	}
	public void setDelay(double delay) {
		this.delay = delay;
	}

}
