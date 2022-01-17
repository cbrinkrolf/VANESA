package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;


public class SmallMolecule extends BiologicalNodeAbstract{
	
	public SmallMolecule(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.smallMolecule);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
