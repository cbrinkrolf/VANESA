package biologicalObjects.edges;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;

import biologicalElements.GraphElementAbstract;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.NetworkSettingsSingelton;
import graph.gui.Parameter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BiologicalEdgeAbstract implements GraphElementAbstract, Cloneable {

	// ---Fields---
	private ReactionPairEdge reactionPairEdge;

	private boolean directed;
	private boolean visible = true;
	@Setter(AccessLevel.NONE)
	private String name = "not mentioned";
	@Setter(AccessLevel.NONE)
	private String label = "???";
	@Setter(AccessLevel.NONE)
	private int ID = 0;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private SortedSet<Integer> set;

	private String description = "";
	private String comments = "";
	private Color defaultColor = Color.GRAY;
	private Color color = Color.GRAY;
	private String biologicalElement = "";
	private Shape shape;
	private boolean hasKEGGNode = false;
	private boolean hasDAWISNode = false;
	private boolean hasBrendaNode = false;
	private HashSet<String> labelSet = new HashSet<String>();
	private ArrayList<Parameter> parameters = new ArrayList<Parameter>();
	@Setter(AccessLevel.NONE)
	private String function = "1";

	private BiologicalNodeAbstract from;
	private BiologicalNodeAbstract to;

	// ---Functional Methods---

	public BiologicalEdgeAbstract(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		// this.edge=edge;
		this.label = label;
		this.name = name;
		if (label.trim().length() == 0) {
			this.label = name;
		}
		if (name.trim().length() == 0) {
			this.name = label;
		}
		this.labelSet.add(label);
		// setName(name.toLowerCase());
		// setLabel(label.toLowerCase());
		this.from = from;
		this.to = to;
	}

	@Override
	public BiologicalEdgeAbstract clone() {
		try {
			BiologicalEdgeAbstract cloneEdge = (BiologicalEdgeAbstract) super.clone();
			return cloneEdge;
		} catch (CloneNotSupportedException e) {
			// Kann eigentlich nicht passieren, da Cloneable
			throw new InternalError();
		}
	}

	/*
	 * private boolean stringsEqualAndAreNotEmpty(String s1, String s2) { return
	 * s1.length() > 0 && s2.length() > 0 && s1.equalsIgnoreCase(s2); }
	 */

	/**
	 * checks if the given BiologicalNodeAbstract is equal to this one nodes are
	 * equal if name OR label match (also when name matches the label of the other
	 * node)
	 */
	/*
	 * public boolean equals(Object o) {
	 * 
	 * if (!(o instanceof BiologicalNodeAbstract)) { return super.equals(o); }
	 * 
	 * BiologicalNodeAbstract bna = (BiologicalNodeAbstract) o;
	 * 
	 * String name = this.getName(); String label = this.getLabel();
	 * 
	 * String name2 = bna.getName(); String label2 = bna.getLabel();
	 * 
	 * return stringsEqualAndAreNotEmpty(name,name2) //||
	 * stringsEqualAndAreNotEmpty(name,label2) //||
	 * stringsEqualAndAreNotEmpty(label,name2) ||
	 * stringsEqualAndAreNotEmpty(label,label2); }
	 */

	private String getCorrectLabel(Integer type) {

		if ((getLabel().length() == 0 || getLabel().equals(" "))
				&& (getName().length() == 0 || getName().equals(" "))) {
			return "";
		} else {

			if (type == 1) {
				if (getLabel().equals("1") && this instanceof BiologicalEdgeAbstract) {
					return "";
				}
				if (getLabel().length() == 0 || getLabel().equals(" ")) {
					return getName();
				} else {
					return getLabel();
				}
			} else if (type == 2) {
				if (getName().length() == 0 || getName().equals(" ")) {
					return getLabel();
				} else {
					return getName();
				}
			} else if (type == 3) {
				if (getName().length() == 0 || getName().equals(" ")) {
					return getLabel();
				} else if (getLabel().length() == 0 || getLabel().equals(" ")) {
					return getName();
				} else {
					return getLabel() + "  -|-  " + getName();
				}
			} else if (type == 4) {
				return "";
			}
		}
		return "";
	}

	// ---Getter/Setter---

	public void setFunction(String function) {
		this.function = function;
		setLabel(this.function);
	}

	// should only be used when loading a file with a network
	public void setID(int id, Pathway pw) throws IDAlreadyExistException {
		if (this.ID == id) {
			return;
		} else {
			set = pw.getIdSet();
			// System.out.println("size: " + set.size());
			if (set.contains(id)) {
				// System.err.println("Error: Id " + id + " is already
				// existing!");
				throw new IDAlreadyExistException("ID " + id + " is already existing.");
			} else {
				// if (this.ID > 0) {
				// set.remove(ID);
				// // System.out.println("removed: " + ID);
				// }
				// System.out.println("id added: " + id);
				set.add(id);
				this.ID = id;
				// System.out.println("added: " + id);
				// System.out.println("id: "+id);
			}
			// System.out.println("size: " + set.size());
		}

		/*
		 * System.out.println("id: "+id); // //System.out.println("size: " +
		 * ids.size()); if (ids.contains(id)) { System.err.println("Error: Id " + id +
		 * " is already existing!"); ID = counter++; } else { if (id < counter) { ID =
		 * id; } else { counter = id; this.ID = counter++; }
		 * 
		 * } //System.out.println("added: " + ID); ids.add(ID);
		 */
	}

	public void setID(Pathway pw) {

		set = pw.getIdSet();
		// System.out.println(new GraphInstance().getPathway().getName());
		// set id to highest current id+1;
		if (ID <= 0) {
			// System.out.println("neue ID");
			if (set.size() > 0) {
				// System.out.println("last: " + set.last());
				try {
					setID(set.last() + 1, pw);
				} catch (IDAlreadyExistException ex) {
					// cannot occur if program working fine.
					ex.printStackTrace();
				}
				// System.out.println("size: " + set.size());
				// System.out.println("groesster: " + set.last());
				// System.out.println("kleinster: " + set.first());
			} else {
				try {
					setID(100, pw);
				} catch (IDAlreadyExistException ex) {
					// cannot occur if program working fine.
					ex.printStackTrace();
				}
			}
		}
	}

	public String getNetworklabel() {
		return getCorrectLabel(NetworkSettingsSingelton.getInstance().getEdgeLabel());
	}

	public void setName(String name) {
		this.name = name.trim();
		if (label.length() == 0) {
			label = name;
		}
	}

	public void setLabel(String label) {
		labelSet.remove(this.label);
		this.label = label.trim();
		labelSet.add(this.label);
		if (name.length() == 0) {
			name = label;
		}
		// this.networklabel = label;
		// System.out.println("gestezt");
	}

	public boolean isEdge() {
		return true;
	}

	public boolean isVertex() {
		return false;
	}

	public void addLabel(String label) {
		this.labelSet.add(label);
	}

	public void addLabel(HashSet<String> labels) {
		this.labelSet.addAll(labels);
	}

	public void removeLabel(String label) {
		this.labelSet.remove(label);
	}

	public void resetAppearance() {

	}

	public boolean isValid(boolean allowFromEqualsTo) {
		if (to == null || from == null)
			return false;
		if (from == to && !allowFromEqualsTo)
			return false;
		return true;
	}

	// defines parameters which are available in during transformation
	public List<String> getTransformationParameters() {
		List<String> list = new ArrayList<String>();
		list.add("name");
		list.add("label");
		list.add("function");
		list.add("ID");
		return list;
	}

	public String getTransformationParameterValue(String parameter) {
		switch (parameter) {
		case "name":
			return getName();
		case "label":
			return getLabel();
		case "function":
			return getFunction();
		case "ID":
			return getID() + "";
		}
		return null;
	}
}
