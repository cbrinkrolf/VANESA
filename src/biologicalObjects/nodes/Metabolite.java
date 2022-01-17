package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;


public class Metabolite extends BiologicalNodeAbstract{
	
	public Metabolite(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.metabolite);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
