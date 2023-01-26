package biologicalObjects.nodes.petriNet;

import java.util.ArrayList;
import java.util.List;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import gui.MyPopUp;
import util.FormularSafety;

public class PNNode extends BiologicalNodeAbstract {
	public PNNode(String label, String name) {
		super(label, name);
		this.setLabel(label);
		this.setName(name);
	}

	@Override
	public void setName(String name) {
		/*if(name == null || name.trim().length() < 1){
			MyPopUp.getInstance().show("Empty name!", "Name must not be empty!");
			return;
		}*/
		Pathway pw = new GraphInstance().getPathway();
		if (pw != null && pw.containsVertex(this) && pw.getAllNodeNames().contains(name)) {
			if (pw.getNodeByName(name).getClass().equals(this.getClass())) {
				BiologicalNodeAbstract node = pw.getNodeByName(name);
				this.setLogicalReference(node);
				MyPopUp.getInstance().show("Name already exists!", "Created logical node instead!");
			} else {
				System.out.println(pw.getName());
				MyPopUp.getInstance().show("Type mismatch",
						"Node with same name already exists. Cannot create logical place because of type mismatch: "
								+ pw.getNodeByName(name).getClass().getSimpleName() + " versus "
								+ this.getClass().getSimpleName());
			}
			return;
		}
		super.setName(FormularSafety.replace(name));
	}

	@Override
	public void setLabel(String label) {
		super.setLabel(FormularSafety.replace(label));
	}

	// defines parameters which are available in during transformation
	public List<String> getTransformationParameters() {
		List<String> list = new ArrayList<String>();
		list.add("name");
		// set.add("label");
		// set.add("ID");
		return list;
	}

	public <T> T setTransformationParameterValue(String parameter, Class<T> type) {
		switch (parameter) {
		case "name":
			return type.cast(getName());
		case "label":
			return type.cast(getLabel());
		case "concentrationStart":
			return type.cast(getConcentrationStart());
		case "concentrationMin":
			return type.cast(getConcentrationMin());
		case "concentrationMax":
			return type.cast(getConcentrationMax());
		case "ID":
			return type.cast(getID());
		}
		return null;
	}
}
