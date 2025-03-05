package biologicalObjects.edges;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import biologicalElements.GraphElementAbstract;
import biologicalElements.GraphicalElementAbstract;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.GraphSettings;
import graph.gui.Parameter;
import org.apache.commons.lang3.StringUtils;

public abstract class BiologicalEdgeAbstract extends GraphicalElementAbstract implements GraphElementAbstract, Cloneable {
	private boolean directed;
	private boolean visible = true;
	private String name;
	private String label;
	private int ID = 0;
	private SortedSet<Integer> set;

	private String description = "";
	private String comments = "";
	private Color defaultColor = Color.GRAY;
	private Color color = Color.GRAY;
	private String biologicalElement = "";
	private Shape shape;
	private boolean hasKEGGNode = false;
	private boolean hasBrendaNode = false;
	private Set<String> labelSet = new HashSet<>();
	private List<Parameter> parameters = new ArrayList<>();
	private String function = "1";

	private BiologicalNodeAbstract from;
	private BiologicalNodeAbstract to;

	public BiologicalEdgeAbstract(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		this.label = label;
		this.name = name;
		if (StringUtils.isBlank(label)) {
			this.label = name;
		}
		if (StringUtils.isBlank(name)) {
			this.name = label;
		}
		labelSet.add(label);
		// setName(name.toLowerCase());
		// setLabel(label.toLowerCase());
		this.from = from;
		this.to = to;
	}

	public boolean isDirected() {
		return directed;
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	public String getComments() {
		return comments;
	}

	@Override
	public void setComments(String comments) {
		this.comments = comments;
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	public String getBiologicalElement() {
		return biologicalElement;
	}

	protected void setBiologicalElement(String biologicalElement) {
		this.biologicalElement = biologicalElement;
	}

	public Shape getShape() {
		return shape;
	}

	@Override
	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public boolean hasKEGGNode() {
		return hasKEGGNode;
	}

	@Override
	public void setHasKEGGNode(boolean hasKEGGNode) {
		this.hasKEGGNode = hasKEGGNode;
	}

	public boolean hasBrendaNode() {
		return hasBrendaNode;
	}

	@Override
	public void setHasBrendaNode(boolean hasBrendaNode) {
		this.hasBrendaNode = hasBrendaNode;
	}

	public Set<String> getLabelSet() {
		return labelSet;
	}

	@Override
	public void setLabelSet(Set<String> labelSet) {
		this.labelSet = labelSet;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public List<Parameter> getParametersSortedAlphabetically() {
		Map<String, Parameter> map = new HashMap<>();
		for (Parameter p : getParameters()) {
			String name = p.getName();
			map.put(name, p);
		}
		List<String> names = new ArrayList<>(map.keySet());
		Collections.sort(names);
		List<Parameter> sortedList = new ArrayList<>();
		for (String name : names) {
			sortedList.add(map.get(name));
		}
		return sortedList;
	}

	@Override
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public BiologicalNodeAbstract getFrom() {
		return from;
	}

	public void setFrom(BiologicalNodeAbstract from) {
		this.from = from;
	}

	public BiologicalNodeAbstract getTo() {
		return to;
	}

	public void setTo(BiologicalNodeAbstract to) {
		this.to = to;
	}

	@Override
	public BiologicalEdgeAbstract clone() {
		try {
			return (BiologicalEdgeAbstract) super.clone();
		} catch (CloneNotSupportedException e) {
			// Should not happen as it's Cloneable
			throw new InternalError();
		}
	}

	private String getCorrectLabel(Integer type) {
		final boolean labelEmpty = StringUtils.isEmpty(label);
		final boolean nameEmpty = StringUtils.isEmpty(name);
		if (labelEmpty && nameEmpty) {
			return "";
		}
		if (type == GraphSettings.SHOW_LABEL) {
			if (label.equals("1")) { // always true:  && this instanceof BiologicalEdgeAbstract
				return "";
			}
			return labelEmpty ? name : label;
		}
		if (type == GraphSettings.SHOW_NAME) {
			return nameEmpty ? label : name;
		}
		if (type == GraphSettings.SHOW_LABEL_AND_NAME) {
			if (nameEmpty) {
				return label;
			}
			if (labelEmpty) {
				return name;
			}
			return label + "  -|-  " + name;
		}
		return "";
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
		setLabel(this.function);
	}

	public int getID() {
		return ID;
	}

	// should only be used when loading a file with a network
	public void setID(int id, Pathway pw) throws IDAlreadyExistException {
		if (ID != id) {
			set = pw.getIdSet();
			if (set.contains(id)) {
				throw new IDAlreadyExistException("ID " + id + " already exists.");
			}
			set.add(id);
			this.ID = id;
		}
	}

	public void setID(Pathway pw) {
		set = pw.getIdSet();
		// set id to the highest current id plus one
		if (ID <= 0) {
			if (set.size() > 0) {
				try {
					setID(set.last() + 1, pw);
				} catch (IDAlreadyExistException ex) {
					// cannot occur if program working fine.
					ex.printStackTrace();
				}
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
		return getCorrectLabel(GraphSettings.getInstance().getEdgeLabel());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
		if (StringUtils.isEmpty(label)) {
			label = name;
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		labelSet.remove(this.label);
		this.label = label.trim();
		labelSet.add(this.label);
		if (StringUtils.isEmpty(name)) {
			name = label;
		}
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

	public void addLabel(Set<String> labels) {
		this.labelSet.addAll(labels);
	}

	public void removeLabel(String label) {
		this.labelSet.remove(label);
	}

	public void resetAppearance() {
	}

	public boolean isValid(boolean allowFromEqualsTo) {
		return to != null && from != null && (from != to || allowFromEqualsTo);
	}

	// defines parameters which are available in during transformation
	public List<String> getTransformationParameters() {
		List<String> list = new ArrayList<>();
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
			return String.valueOf(getID());
		}
		return null;
	}
}
