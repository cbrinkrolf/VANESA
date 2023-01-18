package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Factor extends BiologicalNodeAbstract{
	public Factor(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.factor);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
