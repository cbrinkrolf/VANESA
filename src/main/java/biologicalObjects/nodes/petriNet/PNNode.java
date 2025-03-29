package biologicalObjects.nodes.petriNet;

import java.util.ArrayList;
import java.util.List;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import gui.PopUpDialog;
import util.FormulaSafety;

public abstract class PNNode extends BiologicalNodeAbstract {
	protected PNNode(final String label, final String name, final String biologicalElement, final Pathway parent) {
		super(label, name, biologicalElement, parent);
		setLabel(label);
		setName(name);
	}

	@Override
	public void setName(String name) {
		final Pathway pw = getParent();
		if (pw != null) {
			final BiologicalNodeAbstract node = pw.getNodeByName(name);
			if (pw.containsVertex(this) && node != null && node != this) {
				if (node.getClass().equals(getClass())) {
					setLogicalReference(node);
					PopUpDialog.getInstance().show("Name already exists!", "Created logical node instead!");
				} else {
					System.out.println(pw.getName());
					PopUpDialog.getInstance().show("Type mismatch",
							"Node with same name already exists. Cannot create logical place because of type mismatch: "
									+ pw.getNodeByName(name).getClass().getSimpleName() + " versus " + this.getClass()
									.getSimpleName());
				}
				return;
			}
		}
		super.setName(FormulaSafety.replace(name));
	}

	@Override
	public void setLabel(String label) {
		super.setLabel(FormulaSafety.replace(label));
	}

	// defines parameters which are available in during transformation
	@Override
	public List<String> getTransformationParameters() {
		List<String> list = new ArrayList<>();
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
