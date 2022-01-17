package biologicalObjects.nodes.petriNet;

import java.awt.Color;

import biologicalElements.Elementdeclerations;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscreteTransition extends Transition {

	private double delay = 1;

	public DiscreteTransition(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.discreteTransition);
		setColor(Color.WHITE);
	}
}
