package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Other extends BiologicalNodeAbstract {

	public Other(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.others);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
