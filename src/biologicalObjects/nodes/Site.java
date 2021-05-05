package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Site extends BiologicalNodeAbstract{
	public Site(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.site);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
