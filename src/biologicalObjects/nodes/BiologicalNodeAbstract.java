package biologicalObjects.nodes;

import edu.uci.ics.jung.graph.util.Pair;
import graph.GraphInstance;
import graph.gui.Parameter;
import graph.jung.classes.MyGraph;
import graph.jung.graphDrawing.VertexShapes;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import petriNet.Place;
import sun.security.action.GetLongAction;
import configurations.NetworkSettings;
import configurations.NetworkSettingsSingelton;
import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;
import biologicalElements.NodeStateChanged;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;

public abstract class BiologicalNodeAbstract extends Pathway implements
		GraphElementAbstract {

	// ---Fields---

	// private Vertex vertex;

	private KEGGNode KEGGnode;

	private DAWISNode dawisNode;

	private String compartment = Elementdeclerations.cytoplasma;

	// private HashMap<Integer, Integer> values = new HashMap<Integer,
	// Integer>();

	private double nodesize = 1;

	private double defaultNodesize = 1;

	private Vector<CollectorNode> collectorNodes = new Vector<CollectorNode>();

	private BiologicalNodeAbstract parentNode;

	// contains information on microarray data for this element

	private Vector<Double> petriNetSimulationData;

	public VertexShapes shapes;

	private String organism = "";

	String db = "";

	private DefaultMutableTreeNode treeNode;

	private Vector<String> elementsVector;

	private SBMLNode sbml = new SBMLNode();

	// private int original_graph;

	private MicroArrayAttributes microarrayAttributes = null;

	private BiologicalNodeAbstract ref = null;

	private Set<BiologicalNodeAbstract> refs = new HashSet<BiologicalNodeAbstract>();

	private boolean isReference = true;

	private boolean isVisible = true;

	// private String name = "not mentioned";

	private String label = "???";

	// private String networklabel = "";

	private int ID = 0;

	private SortedSet<Integer> set;

	NetworkSettings settings = NetworkSettingsSingelton.getInstance();

	private String comments = "";

	private Color color = Color.LIGHT_GRAY;

	private Color defaultColor = Color.LIGHT_GRAY;

	private String BiologicalElement = "";

	private Shape shape = new VertexShapes().getEllipse();

	private Shape defaultShape = new VertexShapes().getEllipse();

	private boolean hidden = false;

	private boolean hasKEGGNode = false;

	private boolean hasKEGGEdge = false;

	private boolean hasFeatureEdge = false;

	private boolean hasDAWISNode = false;

	private boolean hasReactionPairEdge = false;

	private boolean hasBrendaNode = false;

	private HashSet<String> labelSet = new HashSet<String>();

	private Collection<Integer> originalGraphs;

	private ArrayList<Parameter> parameters = new ArrayList<Parameter>();

	private Set<BiologicalNodeAbstract> border = new HashSet<BiologicalNodeAbstract>();

	private Set<BiologicalNodeAbstract> environment = new HashSet<BiologicalNodeAbstract>();

	private Set<BiologicalEdgeAbstract> connectingEdges = new HashSet<BiologicalEdgeAbstract>();

	private Pathway rootNode;

	private NodeStateChanged state = NodeStateChanged.UNCHANGED;

	public void setStateChanged(NodeStateChanged state) {
		this.state = state;
	}

	public NodeStateChanged getStateChanged() {
		return state;
	}

	// ---Functional Methods---

	public BiologicalNodeAbstract(String label, String name) {
		super(name, new GraphInstance().getPathway());
		super.setName(name);
		this.label = label;
		this.labelSet.add(label);
		rootNode = new GraphInstance().getPathway().getRootPathway();
		//
		// setLabel(label.toLowerCase());
		// setName(name.toLowerCase());
		// setVertex(vertex);
		setPetriNet(new GraphInstance().getPathway().isPetriNet());

		// values.put(1, 0);
		shapes = new VertexShapes();
		// setShape(shapes.getEllipse());

		// initialize microarray data vector
		petriNetSimulationData = new Vector<Double>();

		sbml.setName(name.toLowerCase());
		sbml.setLabel(label.toLowerCase());
		// if (vertex != null) {
		// sbml.setVertex(vertex.toString());
		// }
		sbml.setBiologicalNodeDescription(Elementdeclerations.transcriptionFactor);

	}

	/*
	 * private boolean stringsEqualAndAreNotEmpty(String s1, String s2) { return
	 * s1.length() > 0 && s2.length() > 0 && s1.equalsIgnoreCase(s2); }
	 */

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
				// if (getLabel().equals("1") && this instanceof
				// BiologicalEdgeAbstract) {
				// return "";
				// }
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

	/**
	 * Returns a node that can be cloned to create a coarse node of the given
	 * set of nodes.
	 * 
	 * @param vertices
	 *            Nodes selected for coarsing operation
	 * @return Node that is instance of the correct type of node. Returns null
	 *         if coarsing operation is not possible on this set of nodes (e.g.
	 *         different node types in Petri-Net border).
	 * @author tloka
	 */
	public BiologicalNodeAbstract computeCoarseType(
			Set<BiologicalNodeAbstract> vertices) {
		Set<BiologicalNodeAbstract> testBorder = computeBorder(vertices);
		if (testBorder.size() <= 0)
			return vertices.iterator().next();
		if (this.isPetriNet()) {
			boolean isPlace = testBorder.iterator().next() instanceof Place;
			for (BiologicalNodeAbstract node : testBorder) {
				if (node instanceof Place != isPlace) {
					return null;
				}
			}
		}
		// TOBI: Check for continuous nodes in vertices(!) to clone this object.
		return testBorder.iterator().next();
	}

	/**
	 * Method to coarse a Set of nodes to a single hierarchical node. Can also
	 * be used for updating after change of nodes or edges.
	 * 
	 * @param vertices
	 *            Nodes to coarse.
	 * @author tloka
	 */
	public void coarse(Set<BiologicalNodeAbstract> vertices) {
		MyGraph activeGraph = getActiveGraph();

		// Set ParentNode of all input nodes to this node and add them to the
		// Pathway.
		for (BiologicalNodeAbstract node : vertices) {
			node.setParentNode(this);
			addVertex(node, activeGraph.getVertexLocation(node));
		}

		// Compute the border of the given set of nodes.
		border = computeBorder(vertices);

		// Compute the environment of the given set of nodes.
		environment = computeEnvironment(vertices, border);

		// Save the edges containing the nodes inside the coarse node.
		saveSubnetEdges(vertices);

		// Set state changed of all non-environment nodes to COARSED.
		for (BiologicalNodeAbstract node : getAllNodes()) {
			if (!environment.contains(node)) {
				node.setStateChanged(NodeStateChanged.COARSED);
			}
		}

		// Update current MyGraph
		rootNode.updateMyGraph();

		// Reset state changed of all non-environment nodes
		for (BiologicalNodeAbstract node : getAllNodes()) {
			if (!environment.contains(node)) {
				node.setStateChanged(NodeStateChanged.UNCHANGED);
			}
		}
	}

	/**
	 * Computes the border for a given set of nodes using the actual shown
	 * graph.
	 * 
	 * @param vertices
	 *            Set of nodes that were selected for coarse operation.
	 * @return The border of the input nodes for the actually shown graph.
	 * @author tloka
	 */
	private Set<BiologicalNodeAbstract> computeBorder(
			Set<BiologicalNodeAbstract> vertices) {

		MyGraph activeGraph = getActiveGraph();
		Set<BiologicalNodeAbstract> newBorder = new HashSet<BiologicalNodeAbstract>();

		// If input list is empty, return empty list.
		if (vertices == null | vertices.size() == 0) {
			return newBorder;
		}

		for (BiologicalNodeAbstract node : vertices) {

			for (BiologicalEdgeAbstract edge : activeGraph.getJungGraph()
					.getInEdges(node)) {

				if (!vertices.contains(edge.getFrom())) {
					newBorder.add(edge.getTo());
				}
			}

			for (BiologicalEdgeAbstract edge : activeGraph.getJungGraph()
					.getOutEdges(node)) {

				if (!vertices.contains(edge.getTo())) {
					newBorder.add(edge.getFrom());
				}
			}
		}
		return newBorder;
	}

	/**
	 * Computes the environment of a (hierarchical) node using a given set of
	 * nodes that were selected for coarsing operation and the already computed
	 * border.
	 * 
	 * @param vertices
	 *            Set of coarsed nodes.
	 * @param border
	 *            Subset of coarsed nodes representing the border.
	 * @return The environment of the input nodes for the actually shown graph.
	 * @author tloka
	 */
	private Set<BiologicalNodeAbstract> computeEnvironment(
			Set<BiologicalNodeAbstract> vertices,
			Set<BiologicalNodeAbstract> border) {

		MyGraph activeGraph = getActiveGraph();

		Set<BiologicalNodeAbstract> newEnvironment = new HashSet<BiologicalNodeAbstract>();

		for (BiologicalEdgeAbstract edge : activeGraph.getAllEdges()) {

			if (border.contains(edge.getTo())
					&& !vertices.contains(edge.getFrom())) {
				newEnvironment.add(edge.getFrom());
			} else if (border.contains(edge.getFrom())
					&& !vertices.contains(edge.getTo())) {
				newEnvironment.add(edge.getTo());
			}

		}
		for (BiologicalNodeAbstract envNode : newEnvironment) {
			addVertex(envNode, activeGraph.getVertexLocation(envNode));
		}
		return newEnvironment;
	}

	/**
	 * Internal edges (between two nodes inside the coarse node) are added to
	 * the pathway graph, connecting edges (between a border node and an
	 * environment node) are only added to a Set of edges without any influence
	 * on the pathway graph.
	 * 
	 * @param vertices
	 *            Set of (coarse) nodes.
	 * @author tloka
	 */
	private void saveSubnetEdges(Set<BiologicalNodeAbstract> vertices) {

		// Copy the connecting edges concerning the border of the subpathway
		// from the environment nodes.
		for (BiologicalNodeAbstract envNode : environment) {
			Set<BiologicalEdgeAbstract> conEdges = envNode.getConnectingEdges();
			if (!conEdges.isEmpty()) {
				for (BiologicalEdgeAbstract edge : conEdges) {
					if (border.contains(edge.getTo().getCurrentShownParentNode(
							this.getGraph()))
							| border.contains(edge.getFrom()
									.getCurrentShownParentNode(this.getGraph()))) {
						connectingEdges.add(edge);
					}
				}
			}
		}

		// Draw the "inner" edges of the subpathway.
		for (BiologicalEdgeAbstract edge : getActiveGraph().getAllEdges()) {
			BiologicalEdgeAbstract e = edge.clone();
			if (getAllNodes().contains(edge.getTo())
					&& !getEnvironment().contains(edge.getTo())) {
				addEdge(e);
			} else if (getAllNodes().contains(edge.getFrom())
					&& !getEnvironment().contains(edge.getFrom())) {
				addEdge(e);
			}
		}
	}

	/**
	 * Method to flat the coarse node back into the original subpathway.
	 * 
	 * @author tloka
	 */
	public void flat() {

		// Stop if node is not a coarse node.
		if (getAllNodes().size() <= 0)
			return;

		// Delete Parent node.
		this.setParentNode(this);

		// Set state changed of all non-environment nodes to FLATTED.
		this.setStateChanged(NodeStateChanged.FLATTED);

		// Update current MyGraph
		rootNode.updateMyGraph();

		// Reset state changed of all non-environment nodes
		this.setStateChanged(NodeStateChanged.UNCHANGED);
	}

	@Override
	public BiologicalNodeAbstract clone() {
		return (BiologicalNodeAbstract) super.clone();
	}

	// ---Getter/Setter---

	public MicroArrayAttributes getMicroarrayAttributes() {
		return microarrayAttributes;
	}

	public void setMicroarrayAttributes(
			MicroArrayAttributes microarrayAttributes) {
		this.microarrayAttributes = microarrayAttributes;
	}

	public SBMLNode getSbml() {
		sbml.setVertex(this.getID() + "");
		return sbml;
	}

	public void setSbml(SBMLNode sbml) {
		this.sbml = sbml;
	}

	public KEGGNode getKEGGnode() {
		return KEGGnode;
	}

	public void setKEGGnode(KEGGNode gnode) {
		hasKEGGNode(true);
		KEGGnode = gnode;
	}

	/**
	 * Returns the vector of this biological element's microarray data.
	 * 
	 * @author tschoeni
	 * @return microArrayData
	 */
	public Vector<Double> getPetriNetSimulationData() {
		return petriNetSimulationData;
	}

	/**
	 * Setter for this element's microarray data. Expects a vector containing
	 * Doubles.
	 * 
	 * @author tschoeni
	 * @param newmicroArrayData
	 */
	public void setPetriNetSimulationData(Vector<Double> petriNetSimulationData) {
		this.petriNetSimulationData = petriNetSimulationData;
	}

	/**
	 * This method expects an index and returns the corresponding value of the
	 * microarray data. It's return value is null, when the index exceeds the
	 * vectors size.
	 * 
	 * @author tschoeni
	 * @param index
	 * @return Double
	 */
	public Double getMicroArrayValue(int index) {
		if (index > petriNetSimulationData.size()) {
			return null;
		} else {
			return petriNetSimulationData.get(index);
		}
	}

	/**
	 * Removes a single value from the microarray data vector.
	 * 
	 * @author tschoeni
	 * @param index
	 *            The index to remove
	 */
	public void removeMicroArrayValue(int index) {
		petriNetSimulationData.remove(index);
	}

	/**
	 * Adds a new value to the microarray data vector.
	 * 
	 * @author tschoeni
	 * @param index
	 * @param value
	 */
	public void addMicroArrayValue(int index, Double value) {
		petriNetSimulationData.add(index, value);
	}

	/*
	 * public Vertex getVertex() { return vertex; }
	 * 
	 * public void setVertex(Vertex vertex) { this.vertex = vertex; if (vertex
	 * != null) { sbml.setVertex(vertex.toString()); } }
	 */

	public Color getColor() {

		if (isReference() | isHidden()) {
			return Color.WHITE;
		} else {
			return color;
		}
	}

	public String getCompartment() {
		return compartment;
	}

	public void setCompartment(String compartment) {
		this.compartment = compartment;
	}

	/*
	 * public int getAnimationValue(int time) { return values.get(time); }
	 * 
	 * public void setAnimationValue(int time, int value) { values.put(time,
	 * value); }
	 * 
	 * public void removeAnimationValue(int time) { values.remove(time); }
	 * 
	 * public int getAnimationSteps() { return values.size(); }
	 */

	/**
	 * get DAWIS OMIM node
	 * 
	 * @return dawisNode
	 */
	public DAWISNode getDAWISNode() {
		return dawisNode;
	}

	/**
	 * set DAWIS OMIM node
	 * 
	 * @param dawisNode
	 */
	public void setDAWISNode(DAWISNode dawisNode) {
		this.dawisNode = dawisNode;
		hasDAWISNode(true);
	}

	public static void setShapes(VertexShapes s) {
		// shapes = s;
	}

	public void rebuildShape(VertexShapes vs) {
		// setShape(vs.getEllipse(getVertex()));
	}

	public double getNodesize() {
		return nodesize;
	}

	public void setNodesize(double nodesize) {
		this.nodesize = nodesize;
	}

	public void setOrganism(String org) {
		this.organism = org;
	}

	public String getOrganism() {
		return this.organism;
	}

	public void setDefaultMutableTreeNode(DefaultMutableTreeNode newNode) {
		this.treeNode = newNode;
	}

	public DefaultMutableTreeNode getDefaultMutableTreeNode() {
		return this.treeNode;
	}

	public void setElementsVector(Vector<String> v) {
		this.elementsVector = v;
	}

	public Vector<String> getElementsVector() {
		return this.elementsVector;
	}

	public void addCollectorNode(CollectorNode collectorNode) {
		collectorNodes.add(collectorNode);
	}

	public Vector<CollectorNode> getCollectorNodes() {
		return collectorNodes;
	}

	public void setParentNode(BiologicalNodeAbstract parent) {
		parentNode = parent;
	}

	public BiologicalNodeAbstract getParentNode() {
		return parentNode;
	}

	public Set<BiologicalNodeAbstract> getAllParentNodes() {
		BiologicalNodeAbstract node = this;
		HashSet<BiologicalNodeAbstract> ret = new HashSet<BiologicalNodeAbstract>();
		if (node.getParentNode() == null) {
			return ret;
		}
		while (node.getParentNode() != node && node.getParentNode() != null) {
			node = node.getParentNode();
			ret.add(node);
		}
		return ret;
	}

	/**
	 * Returns the ParentNode (or the node itself respectively), that is
	 * currently shown in the graph.
	 * 
	 * @author tloka
	 * @return ParentNode currently existing in the graph. Returns null, if no
	 *         parent node currently exists in the graph (e.g. if a
	 *         (parent-)node was deleted or a child is currently represented in
	 *         the graph.
	 */
	public BiologicalNodeAbstract getCurrentShownParentNode(MyGraph graph) {
		if (graph == null) {
			graph = getActiveGraph();
		}

		if (graph.getAllVertices().contains(this)) {
			return this;
		}
		// Can result in null, if node was deleted from the graph.
		try {
			if (getParentNode() != this) {
				return getParentNode().getCurrentShownParentNode(graph);
			}
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	private MyGraph getActiveGraph() {
		return new GraphInstance().getPathway().getGraph();
	}

	public void setDB(String database) {
		this.db = database;
	}

	public String getDB() {
		return this.db;
	}

	public boolean hasRef() {
		if (this.ref != null) {
			return true;
		}
		return false;
	}

	public void setRef(BiologicalNodeAbstract ref) {
		this.ref = ref;
		ref.getRefs().add(this);
	}

	public BiologicalNodeAbstract getRef() {
		return this.ref;
	}

	public void deleteRef() {
		ref.getRefs().remove(this);
		this.ref = null;
	}

	/*
	 * public int getOriginal_graph() { return original_graph; }
	 */

	/*
	 * public void setOriginal_graph(int originalGraph) { original_graph =
	 * originalGraph; }
	 */

	public int getID() {
		return ID;
	}

	// should only be used when loading a file with a network
	public void setID(int id) {
		if (this.ID == id) {
			System.out.println("return");
			return;
		} else {
			set = new GraphInstance().getPathway().getIdSet();
			// System.out.println("size: " + set.size());

			if (set.contains(id)) {
				System.err.println("Error: Id " + id + " is already existing!");
			} else {
				if (this.ID > 0) {
					set.remove(ID);
					// System.out.println("removed: " + ID);
				}
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
		 * ids.size()); if (ids.contains(id)) { System.err.println("Error: Id "
		 * + id + " is already existing!"); ID = counter++; } else { if (id <
		 * counter) { ID = id; } else { counter = id; this.ID = counter++; }
		 * 
		 * } //System.out.println("added: " + ID); ids.add(ID);
		 */
	}

	public void setID() {
		set = new GraphInstance().getPathway().getIdSet();
		// System.out.println(new GraphInstance().getPathway().getName());
		// set id to highest current id+1;
		if (ID <= 0) {
			// System.out.println("neue ID");
			if (set.size() > 0) {
				// System.out.println("last: " + set.last());
				setID(set.last() + 1);
				// System.out.println("size: " + set.size());
				// System.out.println("groesster: " + set.last());
				// System.out.println("kleinster: " + set.first());
			} else {
				setID(100);
			}
		}
	}

	public Collection<Integer> getOriginalGraphs() {
		if (this.originalGraphs == null) {
			this.setOriginalGraphs(new ArrayList<Integer>());
		}
		return this.originalGraphs;
	}

	public void setOriginalGraphs(Collection<Integer> graphs) {
		this.originalGraphs = graphs;
	}

	public String getNetworklabel() {
		return getCorrectLabel(settings.getNodeLabel());
	}

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		labelSet.remove(this.label);
		this.label = label;
		labelSet.add(label);
		// this.networklabel = label;
		// System.out.println("gestezt");
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public boolean isEdge() {
		return false;
	}

	public boolean isVertex() {
		return true;
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
		// System.out.println(shape);
		this.shape = shape;
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

	public ArrayList<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Parameter> parameters) {
		this.parameters = parameters;
	}

	public HashSet<String> getLabelSet() {
		return labelSet;
	}

	public void setLabelSet(HashSet<String> labelSet) {
		this.labelSet = labelSet;
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

	public Set<BiologicalNodeAbstract> getBorder() {
		return border;
	}

	public Set<BiologicalNodeAbstract> getEnvironment() {
		return environment;
	}

	public Set<BiologicalNodeAbstract> getRefs() {
		return refs;
	}

	public void setRefs(Set<BiologicalNodeAbstract> refs) {
		this.refs = refs;
	}

	public Set<BiologicalEdgeAbstract> getConnectingEdges() {
		return connectingEdges;
	}

	public double getDefaultNodesize() {
		return defaultNodesize;
	}

	public void setDefaultNodesize(double defaultNodesize) {
		this.defaultNodesize = defaultNodesize;
		this.setNodesize(defaultNodesize);
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
		this.setColor(defaultColor);
	}

	public Shape getDefaultShape() {
		return defaultShape;
	}

	public void setDefaultShape(Shape defaultShape) {
		this.defaultShape = defaultShape;
		this.setShape(defaultShape);
	}

	public void resetAppearance() {
		this.shape = defaultShape;
		this.color = defaultColor;
		this.nodesize = defaultNodesize;
	}

}
