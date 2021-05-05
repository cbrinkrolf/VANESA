package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;


public class SmallMolecule extends BiologicalNodeAbstract{
	
	private String formula="";
	private String mass="";
	
	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getMass() {
		return mass;
	}

	public void setMass(String mass) {
		this.mass = mass;
	}

	public SmallMolecule(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.smallMolecule);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
