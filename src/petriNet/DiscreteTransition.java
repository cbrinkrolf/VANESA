package petriNet;

import java.awt.Color;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;

public class DiscreteTransition extends Transition{

	private double delay = 1;
	public DiscreteTransition(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.discreteTransition);
		setModellicaString("PNlib.TD"); 
		setColor(Color.WHITE);
	}
	public double getDelay() {
		return delay;
	}
	public void setDelay(double delay) {
		this.delay = delay;
	}

}
