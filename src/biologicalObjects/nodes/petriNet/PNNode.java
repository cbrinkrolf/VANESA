package biologicalObjects.nodes.petriNet;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import util.FormularSafety;

public class PNNode extends BiologicalNodeAbstract{
	

	public PNNode(String label, String name) {
		super(label, name);
		this.setLabel(label);
		this.setName(name);
	}
	
	@Override
	public void setName(String name){
		super.setName(FormularSafety.replace(name));
	}
	
	@Override
	public void setLabel(String label){
		super.setLabel(FormularSafety.replace(label));
	}
}
