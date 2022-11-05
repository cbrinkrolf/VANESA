package biologicalObjects.nodes;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import biologicalElements.GraphElementAbstract;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.NodeStateChanged;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import configurations.NetworkSettingsSingelton;
import graph.GraphInstance;
import graph.groups.Group;
import graph.gui.Parameter;
import graph.jung.classes.MyGraph;
import graph.jung.graphDrawing.VertexShapes;
import graph.layouts.Circle;
import gui.MainWindow;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BiologicalNodeAbstract extends Pathway implements GraphElementAbstract {

	// ---Fields---
	@Setter(AccessLevel.NONE)
	private KEGGNode KEGGnode;
	private double nodesize = 1;
	@Setter(AccessLevel.NONE)
	private double defaultNodesize = 1;
	private BiologicalNodeAbstract parentNode;
	private String organism = "";
	private DefaultMutableTreeNode treeNode;
	@Setter(AccessLevel.NONE)
	private BiologicalNodeAbstract logicalReference = null;
	private Set<BiologicalNodeAbstract> refs = new HashSet<BiologicalNodeAbstract>();
	private boolean isVisible = true;
	@Setter(AccessLevel.NONE)
	private String label = "???";
	@Setter(AccessLevel.NONE)
	private int ID = 0;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private SortedSet<Integer> set;
	private String comments = "";
	private Color color = Color.LIGHT_GRAY;
	@Setter(AccessLevel.NONE)
	private Color defaultColor = Color.LIGHT_GRAY;
	private String BiologicalElement = "";
	private Shape shape = new VertexShapes().getEllipse();
	@Setter(AccessLevel.NONE)
	private Shape defaultShape = new VertexShapes().getEllipse();
	private boolean hasKEGGNode = false;
	private boolean hasBrendaNode = false;
	private HashSet<String> labelSet = new HashSet<String>();
	private ArrayList<Parameter> parameters = new ArrayList<Parameter>();
//	private Set<BiologicalNodeAbstract> border = new HashSet<BiologicalNodeAbstract>();
//	private Set<BiologicalNodeAbstract> environment = new HashSet<BiologicalNodeAbstract>();
	// private Set<BiologicalNodeAbstract> predefinedEnvironment = new
	// HashSet<BiologicalNodeAbstract>();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Set<BiologicalEdgeAbstract> connectingEdges = new HashSet<BiologicalEdgeAbstract>();
	private NodeStateChanged nodeStateChanged = NodeStateChanged.UNCHANGED;
	@Setter(AccessLevel.NONE)
	private HashSet<NodeAttribute> nodeAttributes = new HashSet<>();
	private boolean markedAsEnvironment = false;
	private boolean markedAsCoarseNode = false;
	private Point2D parentNodeDistance = new Point2D.Double(0, 0);
	@Setter(AccessLevel.NONE)
	private boolean deleted = false;

	private boolean inGroup = false;

	private ArrayList<Group> groups = new ArrayList<>();
	// private Set<Group> group = new HashSet<Group>();

	private Color plotColor = null;
	private boolean discrete = false;

	// BNA has constant value
	private boolean constant = false;
	private double concentration = 1;
	private double concentrationMin = 0.0;
	private double concentrationMax = Double.MAX_VALUE;
	private double concentrationStart = 1;

	public BiologicalNodeAbstract(String label, String name) {
		super(name, new GraphInstance().getPathway());
		super.setName(name);
		this.setLabel(label);
		this.labelSet.add(label);
		//
		// setLabel(label.toLowerCase());
		// setName(name.toLowerCase());
		// setVertex(vertex);
		if (new GraphInstance().getPathway() != null) {
			setIsPetriNet(new GraphInstance().getPathway().isPetriNet());
		}
		// values.put(1, 0);
		// setShape(shapes.getEllipse());
	}

	public void delete() {
		if (isCoarseNode()) {
			Set<BiologicalNodeAbstract> nodes = new HashSet<BiologicalNodeAbstract>();
			nodes.addAll(getVertices().keySet());
			for (BiologicalNodeAbstract n : nodes) {
				n.delete();
			}
		} else {
			getRootPathway().getVertices().remove(this);
			for (BiologicalNodeAbstract parent : getAllParentNodes()) {
				parent.getVertices().remove(this);
			}
			Set<BiologicalEdgeAbstract> conEdges = new HashSet<BiologicalEdgeAbstract>();
			conEdges.addAll(getConnectingEdges());
			for (BiologicalEdgeAbstract e : conEdges) {
				e.getFrom().removeConnectingEdge(e);
				e.getTo().removeConnectingEdge(e);
			}
			setParentNode(null);
			if (isLogical()) {
				getLogicalReference().getRefs().remove(this);
			}
		}
		deleted = true;
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

	public void attributeSetter(String className, BiologicalNodeAbstract bna) {
		MainWindow.getInstance().nodeAttributeChanger(bna, false);
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
	 * Method to coarse a set of nodes to a hierarchical node with automatic
	 * generated id.
	 * 
	 * @param vertices Nodes to coarse.
	 * @return The created coarseNode. Null, if coarsing was not successful.
	 * @author tloka
	 */
	public static BiologicalNodeAbstract coarse(Set<BiologicalNodeAbstract> nodes) {
		return BiologicalNodeAbstract.coarse(nodes, null, null);
	}

	public static BiologicalNodeAbstract coarse(BiologicalNodeAbstract node) {
		if (node.isCoarseNode()) {
			return node;
		}
		for (BiologicalEdgeAbstract e : node.getConnectingEdges()) {
			Pathway neighborParent = node.getRootPathway();
			if (e.getFrom() != node) {
				neighborParent = e.getFrom().getParentNode() == null ? node.getRootPathway()
						: e.getFrom().getParentNode();
				node.addVertex(e.getFrom(), neighborParent.getGraph().getVertexLocation(e.getFrom()));
				// node.getPredefinedEnvironment().add(e.getFrom());
				e.getFrom().removeConnectingEdge(e);
			} else if (e.getTo() != node) {
				neighborParent = e.getTo().getParentNode() == null ? node.getRootPathway() : e.getTo().getParentNode();
				node.addVertex(e.getTo(), neighborParent.getGraph().getVertexLocation(e.getTo()));
				// node.getPredefinedEnvironment().add(e.getTo());
				e.getTo().removeConnectingEdge(e);
			}
		}
		node.makeCoarseShape();
		node.setMarkedAsCoarseNode(true);
		node.updateHierarchicalAttributes();
		node.getRootPathway().updateMyGraph();
		return node;
	}

	public static BiologicalNodeAbstract coarse(Set<BiologicalNodeAbstract> nodes, Integer id, String label) {
		return BiologicalNodeAbstract.coarse(nodes, id, label, null);
	}

	/**
	 * Method to coarse a set of nodes to a hierarchical node with a given id. ONLY
	 * SET ID DURING IMPORTING PROCESS.
	 * 
	 * @param nodes Nodes to coarse.
	 * @param id    Id for the new generated coarse node.
	 * @return The created coarseNode. Null, if coarsing was not successful.
	 * @author tloka
	 */
	public static BiologicalNodeAbstract coarse(Set<BiologicalNodeAbstract> nodes, Integer id, String label,
			BiologicalNodeAbstract rootNode) {

		return coarse(nodes, id, label, rootNode, true);
	}

	public static BiologicalNodeAbstract coarse(Set<BiologicalNodeAbstract> nodes, Integer id, String label,
			BiologicalNodeAbstract rootNode, boolean updatePathway) {
		// Stop, if less than two nodes are selected.
		if (nodes == null || nodes.size() < 1) {
			return null;
		}

		BiologicalNodeAbstract coarseNode = rootNode;
		// Get the node type
		if (rootNode == null || rootNode.getRootPathway().isPetriNet())
			coarseNode = computeCoarseType(nodes);

		// If coarsing is not valid, abort.
		if (coarseNode == null || !isCoarsingAllowed(coarseNode.isPetriNet(), nodes)) {
			showCoarsingErrorMessage();
			return null;
		}

		// Create a clean node of the correct node type
		coarseNode = coarseNode.clone();
		coarseNode.cleanUpHierarchyElements();
		coarseNode.setRootNode(rootNode);

		// Set id
		if (id == null) {
			// if pw==null, replace with GraphInstance.getPathwayStatic();
			coarseNode.setID(true, GraphInstance.getPathwayStatic());
		} else {
			try {
				coarseNode.setID(id, GraphInstance.getPathwayStatic());
			} catch (IDAlreadyExistException ex) {
				coarseNode.setID(true, GraphInstance.getPathwayStatic());
			}
		}

		// Set label, name and title
		String answer = label;
		if (label == null) {
			answer = JOptionPane.showInputDialog(MainWindow.getInstance().getFrame(), null, "Name of the coarse Node",
					JOptionPane.QUESTION_MESSAGE);
			if (answer == null) {
				return null;
			} else if (answer.isEmpty()) {
				answer = "id_" + coarseNode.getID();
			}
		}
		coarseNode.setLabel(answer);
		coarseNode.setName(answer);
		coarseNode.setTitle(answer);

		// set attributes of the child nodes
		for (BiologicalNodeAbstract node : nodes) {
			node.setParentNode(coarseNode);
			if (!node.isCoarseNode()) {
				coarseNode.getVertices().put(node, node.getRootPathway().getVertices().get(node));
			} else {
				coarseNode.getVertices().putAll(node.getVertices());
			}
		}

		// compute border, environment and connecting edges
		coarseNode.updateHierarchicalAttributes();

		coarseNode.makeCoarseShape();

		GraphInstance.getPathwayStatic().getClosedSubPathways().add(coarseNode);

		return coarseNode;
	}

	/**
	 * Checks, if the node selection is valid for coarsing operation.
	 * 
	 * @param isPetriNet True if Network is a Petri-Net.
	 * @param vertices   Set of nodes.
	 * @return true is coarsing is allowed for the given set of nodes.
	 */
	private static boolean isCoarsingAllowed(boolean isPetriNet, Set<BiologicalNodeAbstract> vertices) {
		BiologicalNodeAbstract parentNode = null;

		if (vertices.iterator().hasNext()) {
			parentNode = vertices.iterator().next().getParentNode();
		}
		for (BiologicalNodeAbstract vertex : vertices) {
			// fail if at least 2 nodes don't have the same parent node (can also be null)
			if (vertex.getParentNode() != parentNode) {
				return false;
			}
			// fail if at least 1 selected node is environment node of the current shown
			// graph
			if (GraphInstance.getPathwayStatic().isBNA()
					&& ((BiologicalNodeAbstract) GraphInstance.getPathwayStatic()).getEnvironment().contains(vertex)) {
				return false;
			}
		}
		return hasValidBorder(isPetriNet, vertices);
	}

	/**
	 * Checks, if a selection of nodes has a valid border in the given type of
	 * graph.
	 * 
	 * @param isPetriNet True if Network is a Petri-Net.
	 * @param vertices   Set of nodes.
	 * @return true, if set of nodes has a valid border.
	 */
	private static boolean hasValidBorder(boolean isPetriNet, Set<BiologicalNodeAbstract> vertices) {
		if (!isPetriNet)
			return true;
		Set<BiologicalNodeAbstract> leafVertices = new HashSet<BiologicalNodeAbstract>();
		for (BiologicalNodeAbstract v : vertices) {
			leafVertices.addAll(v.getLeafNodes());
		}
		Set<BiologicalNodeAbstract> borderNodes = new HashSet<BiologicalNodeAbstract>();
		for (BiologicalNodeAbstract v : leafVertices) {
			for (BiologicalEdgeAbstract e : v.getConnectingEdges()) {
				if (!leafVertices.contains(e.getTo()) || !leafVertices.contains(e.getFrom())) {
					borderNodes.add(v);
					break;
				}
			}
		}
		return isValidBorder(isPetriNet, borderNodes);
	}

	/**
	 * Checks if given Set of nodes is a valid border in the given type of graph.
	 * 
	 * @param isPetriNet True if Network is a Petri-Net.
	 * @param vertices   Set of nodes.
	 * @return True, if border candidate set is valid.
	 * @author tloka
	 */
	private static boolean isValidBorder(boolean isPetriNet, Set<BiologicalNodeAbstract> borderNodes) {

		// if coarse node is part of a petri net, check if all border nodes are the same
		// tape of node (place or transition)
		if (isPetriNet && borderNodes.size() >= 2) {
			Iterator<BiologicalNodeAbstract> iterator = borderNodes.iterator();
			BiologicalNodeAbstract node1 = iterator.next();
			BiologicalNodeAbstract node2;
			while (iterator.hasNext()) {
				node2 = iterator.next();
				if ((node1 instanceof Place) != (node2 instanceof Place)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns a node that can be cloned to create a coarse node of the given set of
	 * nodes.
	 * 
	 * @param vertices Nodes selected for coarsing operation
	 * @return A node, that would be contained in the border if vertices were
	 *         coarsed.
	 * @author tloka
	 */
	private static BiologicalNodeAbstract computeCoarseType(Set<BiologicalNodeAbstract> vertices) {

		// Stop if no input nodes
		if (vertices == null || vertices.size() == 0) {
			return null;
		}
		MyGraph activeGraph = new GraphInstance().getPathway().getGraph();

		// return the first found border node.
		for (BiologicalNodeAbstract node : vertices) {
			for (BiologicalEdgeAbstract conEdge : node.getConnectingEdges()) {
				BiologicalNodeAbstract to = conEdge.getTo().getCurrentShownParentNode(activeGraph);
				BiologicalNodeAbstract from = conEdge.getFrom().getCurrentShownParentNode(activeGraph);
				if (!(vertices.contains(to) && vertices.contains(from))) {
					return node;
				}
			}
		}
		return vertices.iterator().next();
	}

	public void updateHierarchicalAttributes() {
		updateConnectingEdges();
	}

	private void updateConnectingEdges() {
		if (!isCoarseNode()) {
			return;
		}
		connectingEdges.clear();
		for (BiologicalNodeAbstract ln : getLeafNodes()) {
			for (BiologicalEdgeAbstract ce : ln.getConnectingEdges()) {
				if (!getLeafNodes().contains(ce.getTo()) || !getLeafNodes().contains(ce.getFrom())) {
					if (!connectingEdges.contains(ce)) {
						connectingEdges.add(ce);
					}
				}
			}
		}
	}

	/**
	 * Changes current shape to shape in coarse node layout.
	 * 
	 * @author tloka
	 */
	public void makeCoarseShape() {
		Shape coarseShape;
		if (getRootNode() == null)
			coarseShape = shape;
		else
			coarseShape = getRootNode().getShape();
		coarseShape = new VertexShapes().makeCoarse(coarseShape);
		setDefaultShape(coarseShape);
		setShape(coarseShape);
	}

	/**
	 * Shows an error dialog, that coarsing the selected set of nodes is not
	 * possible.
	 * 
	 * @author tloka
	 */
	private static void showCoarsingErrorMessage() {
		JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(),
				"No coarsing possible with the given set of nodes.", "Coarsing Error!", JOptionPane.ERROR_MESSAGE);
	}

	public boolean addToCoarseNode(Set<BiologicalNodeAbstract> vertices,
			HashMap<BiologicalNodeAbstract, Point2D> vertexLocations) {
		Set<BiologicalNodeAbstract> ln = new HashSet<BiologicalNodeAbstract>();
		ln.addAll(getLeafNodes());
		for (BiologicalNodeAbstract v : vertices) {
			if (v.getParentNode() != this.getParentNode()) {
				return false;
			}
			ln.addAll(v.getLeafNodes());
		}
		if (hasValidBorder(getRootPathway().isPetriNet(), ln)) {
			getVertices().clear();
			for (BiologicalNodeAbstract leafNode : ln) {
				getVertices().put(leafNode, getRootPathway().getVertices().get(leafNode));
			}
			for (BiologicalNodeAbstract n : vertices) {
				n.setParentNode(this);
				n.setParentNodeDistance(Circle.get2Ddistance(GraphInstance.getMyGraph().getVertexLocation(this),
						vertexLocations.get(n)));
				n.setNodeStateChanged(NodeStateChanged.ADDED);
			}
			updateNodeType();
			return true;
		}
		return false;

	}

	/**
	 * If coarse node type is not identical in Petri Net, node is flattened and
	 * re-coarsed with the correct node type.
	 * 
	 * @author tloka
	 */
	public void updateNodeType() {
		Set<BiologicalNodeAbstract> border = getBorder();
		if (border.isEmpty()) {
			return;
		}
		if (border.iterator().next() instanceof Place != this instanceof Place) {
			Set<BiologicalNodeAbstract> innerNodes = new HashSet<BiologicalNodeAbstract>();
			innerNodes.addAll(getChildrenNodes());
			this.flat();
			MainWindow.getInstance().removeTab(false, getTab().getTitelTab(), this);
			BiologicalNodeAbstract bna = BiologicalNodeAbstract.coarse(innerNodes, getID(), getLabel());
			setGraph(bna.getGraph());
			new GraphInstance().getPathway().getGraph().getVisualizationViewer().repaint();
//			bna.printAllHierarchicalAttributes();
		}
	}

	public static void addConnectingEdge(BiologicalEdgeAbstract bea) {
		if (bea.getFrom().isCoarseNode() || bea.getTo().isCoarseNode())
			return;
		bea.getFrom().addConEdge(bea);
		bea.getTo().addConEdge(bea);
	}

	private void addConEdge(BiologicalEdgeAbstract bea) {
		boolean edgeAlreadyExist = false;
		for (BiologicalEdgeAbstract conEdge : getConnectingEdges()) {
			if (conEdge.getFrom() == bea.getFrom() && conEdge.getTo() == bea.getTo()
					&& conEdge.isDirected() == bea.isDirected()) {
				edgeAlreadyExist = true;
				break;
			}
		}
		if (!edgeAlreadyExist) {
			getConnectingEdges().add(bea);
		}
	}

	/**
	 * Development tool: method to print all relevant hierarchy sets.
	 */
	public void printAllHierarchicalAttributes() {
		System.out.println("Name:");
		System.out.println(getLabel());
		System.out.println("All Nodes:");
		for (BiologicalNodeAbstract node : getVertices().keySet()) {
			System.out.println(node.getLabel());
		}
		System.out.println("");
		System.out.println("All Edges:");
		for (BiologicalEdgeAbstract edge : getAllEdges()) {
			System.out.println(edge.getFrom().getLabel() + "->" + edge.getTo().getLabel());
		}
		System.out.println("");
		System.out.println("Border: ");
		for (BiologicalNodeAbstract bnode : getBorder()) {
			System.out.println(bnode.getLabel());
		}
		System.out.println("");
		System.out.println("Environment: ");
		for (BiologicalNodeAbstract bnode : getEnvironment()) {
			System.out.println(bnode.getLabel());
		}
		System.out.println("");
		System.out.println("Connecting Edges:");
		for (BiologicalEdgeAbstract edge : getConnectingEdges()) {
			System.out.println(edge.getFrom().getLabel() + "->" + edge.getTo().getLabel());
		}
		System.out.println("");
	}

	/**
	 * Method to flat the coarse node back into the original subpathway.
	 * 
	 * @author tloka
	 */
	public void flat() {

		if (!isCoarseNode()) {
			return;
		}

		// Update the parent node of flattened node's children.
		for (BiologicalNodeAbstract node : getChildrenNodes()) {
			node.setParentNode(getParentNode());
		}

		deleted = true;

		// remove id from rootPathway id set.
		getIdSet().remove(ID);

	}

	/**
	 * Removes a connecting edge with same from, to and directed properties as the
	 * input edge.
	 */
	public void removeConnectingEdge(BiologicalEdgeAbstract edge) {
		Set<BiologicalEdgeAbstract> conEdges = new HashSet<BiologicalEdgeAbstract>();
		conEdges.addAll(getConnectingEdges());
		for (BiologicalEdgeAbstract conEdge : conEdges) {
			if (conEdge.getTo() == edge.getTo() && conEdge.getFrom() == edge.getFrom()
					&& conEdge.isDirected() == edge.isDirected()) {
				getConnectingEdges().remove(conEdge);
				getIdSet().remove(conEdge.getID());
			}
		}
	}

	public void removeAllConnectionEdges() {
		Set<BiologicalEdgeAbstract> conEdges = new HashSet<BiologicalEdgeAbstract>();
		conEdges.addAll(getConnectingEdges());
		for (BiologicalEdgeAbstract edge : conEdges) {
			this.removeConnectingEdge(edge);
		}
	}

	/**
	 * Re-Initialise all hierarchy elements and sets for providing
	 * pointer-conflicts.
	 */
	public void cleanUpHierarchyElements() {
		connectingEdges = new HashSet<BiologicalEdgeAbstract>();
		setGraph(new MyGraph(this));
		setNodeStateChanged(NodeStateChanged.UNCHANGED);
		cleanVertices();
	}

	/**
	 * To get all internal non-environment nodes.
	 * 
	 * @return The internal nodes excluding environment nodes.
	 */
//	public Collection<BiologicalNodeAbstract> getInnerNodes(){
//		Collection<BiologicalNodeAbstract> innerNodes = new HashSet<BiologicalNodeAbstract>();
//		if(getVertices().isEmpty()){
//			return innerNodes;
//		}
//		for(BiologicalNodeAbstract node : getVertices().keySet()){
//			if(!getEnvironment().contains(node.getCurrentShownParentNode(getGraph()))){
//				innerNodes.add(node);
//			}
//		}
//		return innerNodes;
//	}

	public Collection<BiologicalNodeAbstract> getChildrenNodes() {
		Set<BiologicalNodeAbstract> childrenNodes = new HashSet<BiologicalNodeAbstract>();
		Set<BiologicalNodeAbstract> done = new HashSet<BiologicalNodeAbstract>();
		BiologicalNodeAbstract currentNode;
		for (BiologicalNodeAbstract n : getLeafNodes()) {
			if (done.contains(n)) {
				continue;
			}
			currentNode = n;
			while (currentNode.getParentNode() != null) {
				if (currentNode.getParentNode() == this) {
					childrenNodes.add(currentNode);
					done.addAll(currentNode.getLeafNodes());
					break;
				} else {
					currentNode = currentNode.getParentNode();
				}
			}
		}
		return childrenNodes;
	}

	/**
	 * Checks if node is a coarse node with a non-empty internal subgraph
	 * 
	 * @return true, if coarse-node.
	 */
	public boolean isCoarseNode() {
		if (!getVertices().isEmpty())
			return true;
//		if(getInnerNodes().isEmpty()){
//			return false;
//		}
//		if(getGraph(false)==null){
//			return false;
//		}
//		return true;
		return false;
	}

	// still buggy, do not use too frequent
	@Override
	public BiologicalNodeAbstract clone() {
		BiologicalNodeAbstract bna = (BiologicalNodeAbstract) super.clone();
		bna.removeAllConnectionEdges();
		bna.getRefs().clear();
		// bna.setID(null);
		return bna;
	}

	// ---Getter/Setter---

	public void setKEGGnode(KEGGNode gnode) {
		setHasKEGGNode(true);
		KEGGnode = gnode;
	}

	/*
	 * public Vertex getVertex() { return vertex; }
	 * 
	 * public void setVertex(Vertex vertex) { this.vertex = vertex; if (vertex !=
	 * null) { sbml.setVertex(vertex.toString()); } }
	 */

	/*
	 * public int getAnimationValue(int time) { return values.get(time); }
	 * 
	 * public void setAnimationValue(int time, int value) { values.put(time, value);
	 * }
	 * 
	 * public void removeAnimationValue(int time) { values.remove(time); }
	 * 
	 * public int getAnimationSteps() { return values.size(); }
	 */

	public void setCoarseNodesize() {
		if (isCoarseNode()) {
			setNodesize(defaultNodesize + Math.log(getLeafNodes().size()) / Math.log(100));
		}
	}

	public int getHierarchyDistance(BiologicalNodeAbstract otherNode) {
		BiologicalNodeAbstract commonParent = getLastCommonParentNode(otherNode);
		if (commonParent == null) {
			return -1;
		}
		int distance = 0;
		BiologicalNodeAbstract currentParent = this;
		while (currentParent != commonParent) {
			distance += 1;
			currentParent = currentParent.getParentNode();
		}
		int newDistance = 0;
		currentParent = otherNode;
		while (currentParent != commonParent) {
			newDistance += 1;
			currentParent = currentParent.getParentNode();
		}
		return Math.max(distance, newDistance);
	}

	public BiologicalNodeAbstract getLastParentNode() {
		BiologicalNodeAbstract lastParent = getParentNode();
		if (lastParent == null) {
			return this;
		}
		while (lastParent.getParentNode() != null) {
			lastParent = lastParent.getParentNode();
		}
		return lastParent;
	}

	public BiologicalNodeAbstract getLastCommonParentNode(BiologicalNodeAbstract otherNode) {
		if (getLastParentNode() == otherNode.getLastParentNode() && getLastParentNode() != null) {
			BiologicalNodeAbstract lastCommonParentNode = getLastParentNode();
			while (true) {
				for (BiologicalNodeAbstract childNode : lastCommonParentNode.getChildrenNodes()) {
					if (getAllParentNodes().contains(childNode) && otherNode.getAllParentNodes().contains(childNode)) {
						lastCommonParentNode = childNode;
						break;
					}
				}
				break;
			}
			return lastCommonParentNode;
		}
		return null;
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

	public List<BiologicalNodeAbstract> getAllParentNodesSorted() {
		BiologicalNodeAbstract node = this;
		ArrayList<BiologicalNodeAbstract> ret = new ArrayList<BiologicalNodeAbstract>();
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
	 * Returns the ParentNode (or the node itself respectively), that is currently
	 * shown in the graph.
	 * 
	 * @author tloka
	 * @return ParentNode currently existing in the graph. Returns null, if no
	 *         parent node currently exists in the graph (e.g. if a (parent-)node
	 *         was deleted or a child is currently represented in the graph.
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

	public Set<BiologicalNodeAbstract> getCurrentShownChildrenNodes(MyGraph graph) {
		Set<BiologicalNodeAbstract> cscn = new HashSet<BiologicalNodeAbstract>();
		for (BiologicalNodeAbstract n : getChildrenNodes()) {
			if (graph.getAllVertices().contains(n)) {
				cscn.add(n);
			} else if (n.isCoarseNode()) {
				cscn.addAll(n.getCurrentShownChildrenNodes(graph));
			}
		}
		return cscn;
	}

	private MyGraph getActiveGraph() {
		return new GraphInstance().getPathway().getGraph();
	}

	public boolean isLogical() {
		if (this.logicalReference != null) {
			return true;
		}
		return false;
	}

	public void setLogicalReference(BiologicalNodeAbstract ref) {
		this.logicalReference = ref;
		ref.getRefs().add(this);
	}

	public void deleteLogicalReference() {
		logicalReference.getRefs().remove(this);
		this.logicalReference = null;
	}

	// should only be used when loading a file with a network
	public void setID(int id, Pathway pw) throws IDAlreadyExistException {
		if (this.ID == id) {
			// System.out.println("return");
			return;
		} else {
			set = pw.getIdSet();
			// System.out.println("size: " + set.size());

			if (set.contains(id)) {
//				System.err.println("Error: Id " + id + " is already existing!");
				throw new IDAlreadyExistException("ID " + id + " is already existing.");
			} else {
//				if (this.ID > 0) {
//					set.remove(ID);
				// System.out.println("removed: " + ID);
//				}
				// System.out.println("id added: " + id);
				set.add(id);
				this.ID = id;
				// System.out.println("added: " + id);
				// System.out.println("id: "+id);
			}
			// System.out.println("size: " + set.size());
		}
	}

	public void setID(Pathway pw) {
		setID(false, pw);
	}

	public void setID(boolean overwriteOldID, Pathway pw) {
		set = pw.getIdSet();
		// System.out.println(new GraphInstance().getPathway().getName());
		// set id to highest current id+1;
		if (overwriteOldID || ID <= 0) {
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
		return getCorrectLabel(NetworkSettingsSingelton.getInstance().getNodeLabel());
	}

	public void setLabel(String label) {
		// this.label = FormularSafety.replace(label);
		this.label = label.trim();
		labelSet.remove(this.label);
		// this.label = label;
		labelSet.add(this.label);
		if (getName().length() == 0) {
			setName(this.label);
		}
		// this.networklabel = label;
		// System.out.println("gestezt");
	}

	public boolean isEdge() {
		return false;
	}

	public boolean isVertex() {
		return true;
	}

	public Parameter getParameter(String name) {
		for (int i = 0; i < parameters.size(); i++) {
			if (parameters.get(i).getName().equals(name.trim())) {
				return parameters.get(i);
			}
		}
		return null;
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

		Set<BiologicalNodeAbstract> bord = new HashSet<BiologicalNodeAbstract>();

		// If node is no coarse node and has connections, return itself
		if (!isCoarseNode()) {
			if (getConnectingEdges().isEmpty())
				bord.add(this);
			return bord;
		}

		// Otherwise, go through connecting edges to find border nodes
		for (BiologicalEdgeAbstract e : getConnectingEdges()) {
			if (!getVertices().keySet().contains(e.getFrom())) {
				bord.add(e.getTo());
			} else if (!getVertices().keySet().contains(e.getTo())) {
				bord.add(e.getFrom());
			}
		}
		return bord;
//		return border;
	}

	public Set<BiologicalNodeAbstract> getEnvironment() {

		Set<BiologicalNodeAbstract> env = new HashSet<BiologicalNodeAbstract>();

		// If node is no coarse node and has connections
		if (!isCoarseNode()) {
			for (BiologicalEdgeAbstract e : getConnectingEdges()) {
				if (e.getFrom() != this) {
					env.add(e.getFrom());
				}
				if (e.getTo() != this) {
					env.add(e.getTo());
				}
			}
			return env;
		}

		// Otherwise, go through connecting edges to find environment nodes
		for (BiologicalEdgeAbstract e : getConnectingEdges()) {
			if (!getVertices().keySet().contains(e.getFrom())) {
				env.add(e.getFrom());
			} else if (!getVertices().keySet().contains(e.getTo())) {
				env.add(e.getTo());
			}
		}
		return env;
//		return environment;
	}

	public Set<BiologicalEdgeAbstract> getConnectingEdges() {
		if (isCoarseNode()) {
			Set<BiologicalEdgeAbstract> conEdges = new HashSet<BiologicalEdgeAbstract>();
			for (BiologicalNodeAbstract n : getVertices().keySet()) {
				for (BiologicalEdgeAbstract e : n.getConnectingEdges()) {
					if (getVertices().keySet().contains(e.getFrom()) != getVertices().keySet().contains(e.getTo())) {
						conEdges.add(e);
					}
				}
			}
			connectingEdges = conEdges;
			return conEdges;
		} else {
			return connectingEdges;
		}
	}

	public void setDefaultNodesize(double defaultNodesize) {
		this.defaultNodesize = defaultNodesize;
		this.setNodesize(defaultNodesize);
	}

	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
		this.setColor(defaultColor);
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

	public Set<BiologicalNodeAbstract> getLeafNodes() {
		if (isCoarseNode()) {
			return getVertices().keySet();
		}
		Set<BiologicalNodeAbstract> ln = new HashSet<BiologicalNodeAbstract>();
		ln.add(this);
		return ln;
	}

	public boolean isEnvironmentNodeOf(Pathway parentNodeOfInterest) {
		if (isMarkedAsEnvironment()) {
			return true;
		}
		if (parentNodeOfInterest instanceof BiologicalNodeAbstract
				&& ((BiologicalNodeAbstract) parentNodeOfInterest).getEnvironment().contains(this)) {
			return true;
		}
		return false;
	}

	/**
	 * double value Attribute
	 * 
	 * @param nodeAttributeType
	 * @param name
	 * @param doublevalue
	 */
	public void addAttribute(int nodeAttributeType, String name, double doublevalue) {
		nodeAttributes.add(new NodeAttribute(nodeAttributeType, name, doublevalue));
	}

	/**
	 * string value attribute
	 * 
	 * @param nodeAttributeType
	 * @param name
	 * @param stringvalue
	 */
	public void addAttribute(int nodeAttributeType, String name, String stringvalue) {
		this.nodeAttributes.add(new NodeAttribute(nodeAttributeType, name, stringvalue));
	}

	/**
	 * 
	 * @param name
	 * @return Attribute of the node with given name if not found, returns null!
	 */
	public NodeAttribute getNodeAttributeByName(String name) {
		for (NodeAttribute na : nodeAttributes) {
			if (na.getName().equals(name))
				return na;
		}
		return null;
	}

	public boolean hasAttributeByName(String name) {
		for (NodeAttribute na : nodeAttributes) {
			if (na.getName().equals(name))
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param nodeAttributeType
	 * @return Attributes of the node with given type, if none exist it returns
	 *         empty.
	 */
	public HashSet<NodeAttribute> getNodeAttributesByType(int nodeAttributeType) {
		HashSet<NodeAttribute> returnlist = new HashSet<>(nodeAttributes.size());
		for (NodeAttribute na : nodeAttributes) {
			if (na.getType() == nodeAttributeType)
				returnlist.add(na);
		}
		return returnlist;
	}

	/**
	 * 
	 * @author mlewinsk
	 *
	 *         Node Attributes are used to save node specific properties which can
	 *         be used for coloring, analysis or other purposes.
	 * 
	 */
	public class NodeAttribute {
		private int type; // FROM NodeAttributeTypes
		private String name;
		private double doublevalue;
		private String stringvalue;

		/**
		 * @param nodeAttributeType
		 * @param doublevalue       Constructor for double attributes
		 */
		public NodeAttribute(int nodeAttributeType, String name, double doublevalue) {
			this.type = nodeAttributeType;
			this.name = name;
			this.doublevalue = doublevalue;
			this.stringvalue = "";
		}

		/**
		 * @param nodeAttributeType
		 * @param stringvalue       Constructor for string attributes
		 */
		public NodeAttribute(int nodeAttributeType, String name, String stringvalue) {
			this.type = nodeAttributeType;
			this.name = name;
			this.doublevalue = -1d;
			this.stringvalue = stringvalue;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public double getDoublevalue() {
			return doublevalue;
		}

		public void setDoublevalue(double doublevalue) {
			this.doublevalue = doublevalue;
		}

		public String getStringvalue() {
			return stringvalue;
		}

		public void setStringvalue(String stringvalue) {
			this.stringvalue = stringvalue;
		}

		private BiologicalNodeAbstract getOuterType() {
			return BiologicalNodeAbstract.this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			long temp;
			temp = Double.doubleToLongBits(doublevalue);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((stringvalue == null) ? 0 : stringvalue.hashCode());
			result = prime * result + type;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NodeAttribute other = (NodeAttribute) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (Double.doubleToLongBits(doublevalue) != Double.doubleToLongBits(other.doublevalue))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (stringvalue == null) {
				if (other.stringvalue != null)
					return false;
			} else if (!stringvalue.equals(other.stringvalue))
				return false;
			if (type != other.type)
				return false;
			return true;
		}
	}

	public void addGroup(Group group) {
		this.groups.add(group);
	}

	public Group getbiggestGroup() {
		Group bigG = null;
		for (Group g : groups) {
			if (bigG == null) {
				bigG = g;
			} else if (g.nodes.size() > bigG.nodes.size()) {
				bigG = g;
			}
		}
		return bigG;
	}

	// defines parameters which are available in during transformation
	public List<String> getTransformationParameters() {
		List<String> list = new ArrayList<String>();
		list.add("name");
		list.add("label");
		list.add("concentrationStart");
		list.add("concentrationMin");
		list.add("concentrationMax");
		list.add("ID");
		return list;
	}

	public String getTransformationParameterValue(String parameter) {
		switch (parameter) {
		case "name":
			return getName();
		case "label":
			return getLabel();
		case "concentrationStart":
			return getConcentrationStart()+"";
		case "concentrationMin":
			return getConcentrationMin()+"";
		case "concentrationMax":
			return getConcentrationMax()+"";
		case "ID":
			return getID()+"";
		}
		return null;
	}
}
