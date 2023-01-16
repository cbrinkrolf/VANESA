package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;


public class Collector extends BiologicalNodeAbstract{
	
	
	public Collector(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.collector);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
