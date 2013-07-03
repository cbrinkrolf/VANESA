package biologicalElements;

import graph.GraphInstance;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import configurations.NetworkSettings;
import configurations.NetworkSettingsSingelton;

public class GraphElementAbstract {

	private boolean isEdge = false;
	private boolean isVertex = false;
	private boolean isAbstract = true;
	private boolean isReference = true;
	private boolean isVisible = true;

	private String name = "not mentioned";
	private String label = "???";
	private String networklabel = "";
	private static int counter = 1000;
	private static Set<Integer> ids = new HashSet<Integer>();
	private int ID;

	public int getID() {
		return ID;
	}

	// should only be used when loading a file with a network
	public void setID(int id) {
		// TODO muss Pathway handeln
		//System.out.println("size: " + ids.size());
		if (ids.contains(id)) {
			System.err.println("Error: Id " + id + " is already existing!");
			ID = counter++;
		} else {
			if (id < counter) {
				ID = id;
			} else {
				counter = id;
				this.ID = counter++;
			}

		}
		//System.out.println("added: " + ID);
		ids.add(ID);
	}

	public GraphElementAbstract() {
		// find current highest id in the pathway
		int highest_id=1000;
		for (Iterator it=new GraphInstance().getPathway().getAllNodes().iterator(); it.hasNext();){
			int current=((GraphElementAbstract)it.next()).getID();
			if (current>highest_id) highest_id=current;		
		}
			//set id to highest current id+1;
		setID(highest_id+1);

		// set id to highest current id+1;
		//ids.add(counter);
		//setID(counter++);
	}

	NetworkSettings settings = NetworkSettingsSingelton.getInstance();

	private Collection<Integer> originalGraphs;

	private boolean stringsEqualAndAreNotEmpty(String s1, String s2) {
		return s1.length() > 0 && s2.length() > 0 && s1.equalsIgnoreCase(s2);
	}

	/**
	 * checks if the given BiologicalNodeAbstract is equal to this one nodes are
	 * equal if name OR label match (also when name matches the label of the
	 * other node)
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

	public Collection<Integer> getOriginalGraphs() {
		if (this.originalGraphs == null) {
			this.setOriginalGraphs(new ArrayList<Integer>());
		}
		return this.originalGraphs;
	}

	public void setOriginalGraphs(Collection<Integer> graphs) {
		this.originalGraphs = graphs;
	}

	public void addOriginalGraph(int g) {

		this.getOriginalGraphs().add(g);
	}

	public boolean containedInAllOriginalGraphs(Pathway[] pathways) {
		boolean contained = true;
		for (int i = 1; i < pathways.length; i++)
			contained = contained && this.getOriginalGraphs().contains(i);
		return contained;
	}

	private String getCorrectLabel(Integer type) {

		if ((getLabel().length() == 0 || getLabel().equals(" "))
				&& (getName().length() == 0 || getName().equals(" "))) {
			return "";
		} else {

			if (type == 1) {

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

	public String getNetworklabel() {

		if (isVertex) {
			return getCorrectLabel(settings.getNodeLabel());
		} else {
			return getCorrectLabel(settings.getEdgeLabel());
		}
	}

	private String description = "";
	private String comments = "";
	private Color color = Color.LIGHT_GRAY;
	private String BiologicalElement = "";
	private Shape shape;
	private boolean hidden = false;

	private boolean hasKEGGNode = false;
	private boolean hasKEGGEdge = false;
	private boolean hasFeatureEdge = false;
	private boolean hasDAWISNode = false;
	private boolean hasReactionPairEdge = false;

	private boolean hasBrendaNode = false;

	public boolean hasFeatureEdge() {
		return hasFeatureEdge;
	}

	public void hasFeatureEdge(boolean hasFeatureEdge) {
		this.hasFeatureEdge = hasFeatureEdge;
	}

	public boolean hasKEGGEdge() {
		return hasKEGGEdge;
	}

	public void hasKEGGEdge(boolean hasKEGGEdge) {
		this.hasKEGGEdge = hasKEGGEdge;
	}

	public boolean hasReactionPairEdge() {
		return hasReactionPairEdge;
	}

	public void hasReactionPairEdge(boolean hasReactionPEdge) {
		this.hasReactionPairEdge = hasReactionPEdge;
	}

	public boolean hasKEGGNode() {
		return hasKEGGNode;
	}

	public void hasKEGGNode(boolean hasKEGGNode) {
		this.hasKEGGNode = hasKEGGNode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
		this.networklabel = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public boolean isEdge() {
		return isEdge;
	}

	public void setIsEdge(boolean isEdge) {
		this.isEdge = isEdge;

	}

	public boolean isVertex() {
		return isVertex;
	}

	public void setIsVertex(boolean isVertex) {
		this.isVertex = isVertex;
	}

	public Color getColor() {

		if (hidden) {
			if (isVertex) {
				return Color.WHITE;
			} else {
				return color;
			}
		} else {
			return color;
		}
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getBiologicalElement() {
		return BiologicalElement;
	}

	public void setBiologicalElement(String biologicalElement) {
		BiologicalElement = biologicalElement;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean isReference() {
		return isReference;
	}

	public void setReference(boolean isReference) {
		this.isReference = isReference;

	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean hasBrendaNode() {
		return hasBrendaNode;
	}

	public void hasBrendaNode(boolean hasBrendaNode) {
		this.hasBrendaNode = hasBrendaNode;
	}

	public boolean hasDAWISNode() {
		return hasDAWISNode;
	}

	public void hasDAWISNode(boolean node) {
		hasDAWISNode = node;
	}

}
