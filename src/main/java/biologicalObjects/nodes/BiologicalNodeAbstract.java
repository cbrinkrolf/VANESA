package biologicalObjects.nodes;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import biologicalElements.GraphElementAbstract;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import configurations.GraphSettings;
import graph.GraphInstance;
import graph.GraphNode;
import graph.VanesaGraph;
import graph.algorithms.NodeAttributeType;
import graph.groups.Group;
import graph.gui.Parameter;
import graph.jung.classes.MyGraph;
import graph.jung.graphDrawing.VertexShapes;
import graph.layouts.Circle;
import graph.rendering.shapes.CoarseShape;
import graph.rendering.shapes.NodeShape;
import graph.rendering.shapes.PlaceShape;
import gui.MainWindow;

public abstract class BiologicalNodeAbstract extends Pathway implements GraphNode, GraphElementAbstract {
	private KEGGNode KEGGnode;
	private double size = 1;
	private double defaultSize = 1;
	private BiologicalNodeAbstract parentNode;
	private String organism = "";
	private DefaultMutableTreeNode treeNode;
	private BiologicalNodeAbstract logicalReference = null;
	private Set<BiologicalNodeAbstract> refs = new HashSet<>();
	private boolean isVisible = true;
	private String label = "???";
	private int ID = 0;
	private SortedSet<Integer> set;
	private String comments = "";
	private Color color = Color.LIGHT_GRAY;
	private Color defaultColor = Color.LIGHT_GRAY;
	private String BiologicalElement = "";
	private Shape shape = VertexShapes.getEllipse();
	private Shape defaultShape = VertexShapes.getEllipse();
	private NodeShape nodeShape = new PlaceShape();
	private boolean hasKEGGNode = false;
	private boolean hasBrendaNode = false;
	private Set<String> labelSet = new HashSet<>();
	private List<Parameter> parameters = new ArrayList<>();
	private Set<BiologicalEdgeAbstract> connectingEdges = new HashSet<>();
	private final Set<NodeAttribute> nodeAttributes = new HashSet<>();
	private boolean markedAsEnvironment = false;
	private boolean markedAsCoarseNode = false;
	private Point2D parentNodeDistance = new Point2D.Double(0, 0);
	private boolean deleted = false;

	private boolean inGroup = false;

	private List<Group> groups = new ArrayList<>();

	private Color plotColor = null;
	private boolean discrete = false;

	// BNA has constant value
	private boolean constant = false;
	private double concentration = 1;
	private double concentrationMin = 0.0;
	private double concentrationMax = Double.MAX_VALUE;
	private double concentrationStart = 1;

	public BiologicalNodeAbstract(final String label, final String name) {
		this(label, name, GraphInstance.getPathway());
	}

	public BiologicalNodeAbstract(final String label, final String name, final Pathway pathway) {
		super(name, pathway);
		super.setName(name);
		setLabel(label);
		labelSet.add(label);
	}

	public void delete() {
		if (isCoarseNode()) {
			Set<BiologicalNodeAbstract> nodes = new HashSet<>(getVertices().keySet());
			for (BiologicalNodeAbstract n : nodes) {
				n.delete();
			}
		} else {
			getRootPathway().getVertices().remove(this);
			for (BiologicalNodeAbstract parent : getAllParentNodes()) {
				parent.getVertices().remove(this);
			}
			Set<BiologicalEdgeAbstract> conEdges = new HashSet<>(getConnectingEdges());
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
		// MainWindow.getInstance().nodeAttributeChanger(bna, false);
	}

	private String getCorrectLabel(Integer type) {
		if ((getLabel().length() == 0 || getLabel().equals(" "))
				&& (getName().length() == 0 || getName().equals(" "))) {
			return "";
		} else {
			if (type == GraphSettings.SHOW_LABEL) {
				// if (getLabel().equals("1") && this instanceof BiologicalEdgeAbstract) {
				// return "";
				// }
				if (getLabel().length() == 0 || getLabel().equals(" ")) {
					return getName();
				} else {
					return getLabel();
				}
			} else if (type == GraphSettings.SHOW_NAME) {
				if (getName().length() == 0 || getName().equals(" ")) {
					return getLabel();
				} else {
					return getName();
				}
			} else if (type == GraphSettings.SHOW_LABEL_AND_NAME) {
				if (getName().length() == 0 || getName().equals(" ")) {
					return getLabel();
				} else if (getLabel().length() == 0 || getLabel().equals(" ")) {
					return getName();
				} else {
					return getLabel() + "  -|-  " + getName();
				}
			} else if (type == GraphSettings.SHOW_NONE) {
				return "";
			}
		}
		return "";
	}

	/**
	 * Method to coarse a set of nodes to a hierarchical node with automatic
	 * generated id.
	 *
	 * @param nodes Nodes to coarse.
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
			if (e.getFrom() != node) {
				Pathway neighborParent = e.getFrom().getParentNode() == null ? node.getRootPathway()
						: e.getFrom().getParentNode();
				node.addVertex(e.getFrom(), neighborParent.getGraph().getVertexLocation(e.getFrom()));
				// node.getPredefinedEnvironment().add(e.getFrom());
				e.getFrom().removeConnectingEdge(e);
			} else if (e.getTo() != node) {
				Pathway neighborParent = e.getTo().getParentNode() == null ? node.getRootPathway()
						: e.getTo().getParentNode();
				node.addVertex(e.getTo(), neighborParent.getGraph().getVertexLocation(e.getTo()));
				// node.getPredefinedEnvironment().add(e.getTo());
				e.getTo().removeConnectingEdge(e);
			}
		}
		node.makeCoarseShape();
		node.setMarkedAsCoarseNode(true);
		node.updateConnectingEdges();
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
		if (nodes == null || nodes.isEmpty()) {
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
			coarseNode.setID(true, GraphInstance.getPathway());
		} else {
			try {
				coarseNode.setID(id, GraphInstance.getPathway());
			} catch (IDAlreadyExistException ex) {
				coarseNode.setID(true, GraphInstance.getPathway());
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
		coarseNode.updateConnectingEdges();
		coarseNode.makeCoarseShape();
		GraphInstance.getPathway().getClosedSubPathways().add(coarseNode);
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
			if (GraphInstance.getPathway().isBNA()
					&& ((BiologicalNodeAbstract) GraphInstance.getPathway()).getEnvironment().contains(vertex)) {
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
		Set<BiologicalNodeAbstract> leafVertices = new HashSet<>();
		for (BiologicalNodeAbstract v : vertices) {
			leafVertices.addAll(v.getLeafNodes());
		}
		Set<BiologicalNodeAbstract> borderNodes = new HashSet<>();
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
	 * @param isPetriNet  True if Network is a Petri-Net.
	 * @param borderNodes Set of nodes.
	 * @return True, if border candidate set is valid.
	 * @author tloka
	 */
	private static boolean isValidBorder(boolean isPetriNet, Set<BiologicalNodeAbstract> borderNodes) {
		// if coarse node is part of a petri net, check if all border nodes are the same
		// tape of node (place or transition)
		if (isPetriNet && borderNodes.size() >= 2) {
			Iterator<BiologicalNodeAbstract> iterator = borderNodes.iterator();
			BiologicalNodeAbstract node1 = iterator.next();
			while (iterator.hasNext()) {
				BiologicalNodeAbstract node2 = iterator.next();
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
	private static BiologicalNodeAbstract computeCoarseType(final Set<BiologicalNodeAbstract> vertices) {
		// Stop if no input nodes
		if (vertices == null || vertices.isEmpty()) {
			return null;
		}
		VanesaGraph activeGraph = GraphInstance.getPathway().getGraph2();
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

	public void updateConnectingEdges() {
		if (!isCoarseNode()) {
			return;
		}
		connectingEdges.clear();
		for (BiologicalNodeAbstract ln : getLeafNodes()) {
			for (BiologicalEdgeAbstract ce : ln.getConnectingEdges()) {
				if (!getLeafNodes().contains(ce.getTo()) || !getLeafNodes().contains(ce.getFrom())) {
					connectingEdges.add(ce);
				}
			}
		}
	}

	private void makeCoarseShape() {
		final Shape coarseShape = VertexShapes.makeCoarse(getRootNode() == null ? shape : getRootNode().shape);
		setDefaultShape(coarseShape);
		setShape(coarseShape);
		final NodeShape shape = new CoarseShape(getRootNode() == null ? nodeShape : getRootNode().nodeShape);
		setNodeShape(shape);
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
			Map<BiologicalNodeAbstract, Point2D> vertexLocations) {
		Set<BiologicalNodeAbstract> ln = new HashSet<>(getLeafNodes());
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
			Set<BiologicalNodeAbstract> innerNodes = new HashSet<>(getChildrenNodes());
			flat();
			MainWindow.getInstance().removeTab(false, getTab(), this);
			BiologicalNodeAbstract bna = BiologicalNodeAbstract.coarse(innerNodes, getID(), getLabel());
			setGraph(bna.getGraph());
			GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
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
		Set<BiologicalEdgeAbstract> conEdges = new HashSet<>(getConnectingEdges());
		for (BiologicalEdgeAbstract conEdge : conEdges) {
			if (conEdge.getTo() == edge.getTo() && conEdge.getFrom() == edge.getFrom()
					&& conEdge.isDirected() == edge.isDirected()) {
				getConnectingEdges().remove(conEdge);
				getIdSet().remove(conEdge.getID());
			}
		}
	}

	public void removeAllConnectionEdges() {
		Set<BiologicalEdgeAbstract> conEdges = new HashSet<>(getConnectingEdges());
		for (BiologicalEdgeAbstract edge : conEdges) {
			removeConnectingEdge(edge);
		}
	}

	/**
	 * Re-Initialise all hierarchy elements and sets for providing
	 * pointer-conflicts.
	 */
	public void cleanUpHierarchyElements() {
		connectingEdges = new HashSet<>();
		setGraph(new MyGraph(this));
		cleanVertices();
	}

	public Collection<BiologicalNodeAbstract> getChildrenNodes() {
		Set<BiologicalNodeAbstract> childrenNodes = new HashSet<>();
		Set<BiologicalNodeAbstract> done = new HashSet<>();
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
	 * Checks if node is a coarse node
	 *
	 * @return true, if coarse-node.
	 */
	public boolean isCoarseNode() {
		return getNodeCount() > 0;
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

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public BiologicalNodeAbstract getParentNode() {
		return parentNode;
	}

	public void setParentNode(BiologicalNodeAbstract parentNode) {
		this.parentNode = parentNode;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public DefaultMutableTreeNode getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(DefaultMutableTreeNode treeNode) {
		this.treeNode = treeNode;
	}

	public Set<BiologicalNodeAbstract> getRefs() {
		return refs;
	}

	public void setRefs(Set<BiologicalNodeAbstract> refs) {
		this.refs = refs;
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	@Override
	public String getComments() {
		return comments;
	}

	@Override
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String getBiologicalElement() {
		return BiologicalElement;
	}

	protected void setBiologicalElement(String biologicalElement) {
		BiologicalElement = biologicalElement;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	@Override
	public NodeShape getNodeShape() {
		return nodeShape;
	}

	public void setNodeShape(NodeShape nodeShape) {
		this.nodeShape = nodeShape;
	}

	@Override
	public boolean hasKEGGNode() {
		return hasKEGGNode;
	}

	@Override
	public void setHasKEGGNode(boolean hasKEGGNode) {
		this.hasKEGGNode = hasKEGGNode;
	}

	@Override
	public boolean hasBrendaNode() {
		return hasBrendaNode;
	}

	@Override
	public void setHasBrendaNode(boolean hasBrendaNode) {
		this.hasBrendaNode = hasBrendaNode;
	}

	@Override
	public Set<String> getLabelSet() {
		return labelSet;
	}

	@Override
	public void setLabelSet(Set<String> labelSet) {
		this.labelSet = labelSet;
	}

	@Override
	public List<Parameter> getParameters() {
		return parameters;
	}

	@Override
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

	public boolean isMarkedAsEnvironment() {
		return markedAsEnvironment;
	}

	public void setMarkedAsEnvironment(boolean markedAsEnvironment) {
		this.markedAsEnvironment = markedAsEnvironment;
	}

	public boolean isMarkedAsCoarseNode() {
		return markedAsCoarseNode;
	}

	public void setMarkedAsCoarseNode(boolean markedAsCoarseNode) {
		this.markedAsCoarseNode = markedAsCoarseNode;
	}

	public Point2D getParentNodeDistance() {
		return parentNodeDistance;
	}

	public void setParentNodeDistance(Point2D parentNodeDistance) {
		this.parentNodeDistance = parentNodeDistance;
	}

	public boolean isInGroup() {
		return inGroup;
	}

	public void setInGroup(boolean inGroup) {
		this.inGroup = inGroup;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public Color getPlotColor() {
		return plotColor;
	}

	public void setPlotColor(Color plotColor) {
		this.plotColor = plotColor;
	}

	public boolean isDiscrete() {
		return discrete;
	}

	public void setDiscrete(boolean discrete) {
		this.discrete = discrete;
	}

	/**
	 * Whether this node is constant in terms of tokens for Places or concentrations
	 * in a biological network. TODO: used for tokenMin and tokenMax but not for
	 * token and concentrations. Evaluate status!
	 */
	public boolean isConstant() {
		return constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}

	public double getConcentration() {
		return concentration;
	}

	public void setConcentration(double concentration) {
		if (concentration < 0) {
			return;
		}
		this.concentration = concentration;
	}

	public double getConcentrationMin() {
		return concentrationMin;
	}

	public void setConcentrationMin(double concentrationMin) {
		if (concentrationMin < 0) {
			return;
		}
		this.concentrationMin = concentrationMin;
	}

	public double getConcentrationMax() {
		return concentrationMax;
	}

	public void setConcentrationMax(double concentrationMax) {
		if (concentrationMax < 0) {
			return;
		}
		this.concentrationMax = concentrationMax;
	}

	public double getConcentrationStart() {
		return concentrationStart;
	}

	public void setConcentrationStart(double concentrationStart) {
		if (concentrationStart < 0) {
			return;
		}
		this.concentrationStart = concentrationStart;
	}

	public KEGGNode getKEGGnode() {
		return KEGGnode;
	}

	public void setKEGGnode(KEGGNode node) {
		setHasKEGGNode(true);
		KEGGnode = node;
	}

	public double getDefaultSize() {
		return defaultSize;
	}

	public void setDefaultSize(double defaultSize) {
		this.defaultSize = defaultSize;
		this.setSize(defaultSize);
	}

	public Shape getDefaultShape() {
		return defaultShape;
	}

	public void setDefaultShape(Shape defaultShape) {
		this.defaultShape = defaultShape;
		this.setShape(defaultShape);
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
		this.setColor(defaultColor);
	}

	public Set<NodeAttribute> getNodeAttributes() {
		return nodeAttributes;
	}

	public boolean isDeleted() {
		return deleted;
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
			setSize(defaultSize + Math.log(getLeafNodes().size()) / Math.log(100));
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
			for (BiologicalNodeAbstract childNode : lastCommonParentNode.getChildrenNodes()) {
				if (getAllParentNodes().contains(childNode) && otherNode.getAllParentNodes().contains(childNode)) {
					lastCommonParentNode = childNode;
					break;
				}
			}
			return lastCommonParentNode;
		}
		return null;
	}

	public Set<BiologicalNodeAbstract> getAllParentNodes() {
		BiologicalNodeAbstract node = this;
		HashSet<BiologicalNodeAbstract> ret = new HashSet<>();
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
		ArrayList<BiologicalNodeAbstract> ret = new ArrayList<>();
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
	public BiologicalNodeAbstract getCurrentShownParentNode(VanesaGraph graph) {
		if (graph == null) {
			graph = getActiveGraph();
		}
		if (graph.contains(this)) {
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

	public Set<BiologicalNodeAbstract> getCurrentShownChildrenNodes(VanesaGraph graph) {
		Set<BiologicalNodeAbstract> cscn = new HashSet<>();
		for (BiologicalNodeAbstract n : getChildrenNodes()) {
			if (graph.contains(n)) {
				cscn.add(n);
			} else if (n.isCoarseNode()) {
				cscn.addAll(n.getCurrentShownChildrenNodes(graph));
			}
		}
		return cscn;
	}

	private VanesaGraph getActiveGraph() {
		return GraphInstance.getPathway().getGraph2();
	}

	public boolean isLogical() {
		return logicalReference != null;
	}

	public BiologicalNodeAbstract getLogicalReference() {
		return logicalReference;
	}

	public void setLogicalReference(BiologicalNodeAbstract ref) {
		this.logicalReference = ref;
		ref.getRefs().add(this);
	}

	public void deleteLogicalReference() {
		logicalReference.getRefs().remove(this);
		this.logicalReference = null;
	}

	@Override
	public int getID() {
		return ID;
	}

	// should only be used when loading a file with a network
	@Override
	public void setID(int id, Pathway pw) throws IDAlreadyExistException {
		if (ID != id) {
			set = pw.getIdSet();
			if (set.contains(id)) {
				throw new IDAlreadyExistException("ID " + id + " already exists.");
			} else {
				set.add(id);
				this.ID = id;
			}
		}
	}

	@Override
	public void setID(Pathway pw) {
		setID(false, pw);
	}

	public void setID(boolean overwriteOldID, Pathway pw) {
		set = pw.getIdSet();
		// set id to the highest current id plus one
		if (overwriteOldID || ID <= 0) {
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

	@Override
	public String getNetworkLabel() {
		return logicalReference != null ? logicalReference.getNetworkLabel() : getCorrectLabel(
				GraphSettings.getInstance().getNodeLabel());
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		// this.label = FormularSafety.replace(label);
		labelSet.remove(this.label);
		this.label = label.trim();
		labelSet.add(this.label);
		if (getName().length() == 0) {
			setName(this.label);
		}
		// this.networklabel = label;
	}

	@Override
	public boolean isEdge() {
		return false;
	}

	@Override
	public boolean isVertex() {
		return true;
	}

	public Parameter getParameter(String name) {
		if (name != null) {
			for (Parameter parameter : parameters) {
				if (name.trim().equals(parameter.getName())) {
					return parameter;
				}
			}
		}
		return null;
	}

	@Override
	public void addLabel(String label) {
		labelSet.add(label);
	}

	@Override
	public void addLabel(Set<String> labels) {
		labelSet.addAll(labels);
	}

	public void removeLabel(String label) {
		labelSet.remove(label);
	}

	public Set<BiologicalNodeAbstract> getBorder() {
		Set<BiologicalNodeAbstract> border = new HashSet<>();
		// If node is no coarse node and has connections, return itself
		if (!isCoarseNode()) {
			if (getConnectingEdges().isEmpty())
				border.add(this);
			return border;
		}
		// Otherwise, go through connecting edges to find border nodes
		for (BiologicalEdgeAbstract e : getConnectingEdges()) {
			if (!getVertices().containsKey(e.getFrom())) {
				border.add(e.getTo());
			} else if (!getVertices().containsKey(e.getTo())) {
				border.add(e.getFrom());
			}
		}
		return border;
	}

	public Set<BiologicalNodeAbstract> getEnvironment() {
		Set<BiologicalNodeAbstract> env = new HashSet<>();
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
			if (!getVertices().containsKey(e.getFrom())) {
				env.add(e.getFrom());
			} else if (!getVertices().containsKey(e.getTo())) {
				env.add(e.getTo());
			}
		}
		return env;
	}

	public Set<BiologicalEdgeAbstract> getConnectingEdges() {
		if (isCoarseNode()) {
			Set<BiologicalEdgeAbstract> conEdges = new HashSet<>();
			for (BiologicalNodeAbstract n : getVertices().keySet()) {
				for (BiologicalEdgeAbstract e : n.getConnectingEdges()) {
					if (getVertices().containsKey(e.getFrom()) != getVertices().containsKey(e.getTo())) {
						conEdges.add(e);
					}
				}
			}
			connectingEdges = conEdges;
			return conEdges;
		}
		return connectingEdges;
	}

	public void resetAppearance() {
		this.shape = defaultShape;
		this.color = defaultColor;
		this.size = defaultSize;
	}

	public Set<BiologicalNodeAbstract> getLeafNodes() {
		if (isCoarseNode()) {
			return getVertices().keySet();
		}
		Set<BiologicalNodeAbstract> ln = new HashSet<>();
		ln.add(this);
		return ln;
	}

	public boolean isEnvironmentNodeOf(Pathway parentNodeOfInterest) {
		if (isMarkedAsEnvironment()) {
			return true;
		}
		return parentNodeOfInterest instanceof BiologicalNodeAbstract
				&& ((BiologicalNodeAbstract) parentNodeOfInterest).getEnvironment().contains(this);
	}

	public void addAttribute(NodeAttributeType nodeAttributeType, String name, double value) {
		nodeAttributes.add(new NodeAttribute(nodeAttributeType, name, value));
	}

	public void addAttribute(NodeAttributeType nodeAttributeType, String name, String value) {
		this.nodeAttributes.add(new NodeAttribute(nodeAttributeType, name, value));
	}

	/**
	 * @return Attribute of the node with given name if not found, returns null!
	 */
	public NodeAttribute getNodeAttributeByName(String name) {
		if (name != null) {
			for (NodeAttribute na : nodeAttributes) {
				if (name.equals(na.getName())) {
					return na;
				}
			}
		}
		return null;
	}

	public boolean hasAttributeByName(String name) {
		if (name != null) {
			for (NodeAttribute na : nodeAttributes) {
				if (name.equals(na.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public Set<NodeAttribute> getNodeAttributesByType(NodeAttributeType nodeAttributeType) {
		Set<NodeAttribute> result = new HashSet<>(nodeAttributes.size());
		for (NodeAttribute na : nodeAttributes) {
			if (na.getType() == nodeAttributeType)
				result.add(na);
		}
		return result;
	}

	public class NodeAttribute {
		private NodeAttributeType type;
		private String name;
		private double doublevalue;
		private String stringvalue;

		public NodeAttribute(NodeAttributeType nodeAttributeType, String name, double value) {
			type = nodeAttributeType;
			this.name = name;
			doublevalue = value;
			stringvalue = "";
		}

		public NodeAttribute(NodeAttributeType nodeAttributeType, String name, String value) {
			type = nodeAttributeType;
			this.name = name;
			doublevalue = -1d;
			stringvalue = value;
		}

		public NodeAttributeType getType() {
			return type;
		}

		public void setType(NodeAttributeType type) {
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
			result = prime * result + type.getId();
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
			if (bigG == null || g.size() > bigG.size()) {
				bigG = g;
			}
		}
		return bigG;
	}

	// defines parameters which are available in during transformation
	public List<String> getTransformationParameters() {
		List<String> list = new ArrayList<>();
		list.add("name");
		list.add("label");
		list.add("concentrationStart");
		list.add("concentrationMin");
		list.add("concentrationMax");
		list.add("ID");
		list.add("isConstant");
		return list;
	}

	public String getTransformationParameterValue(String parameter) {
		switch (parameter) {
		case "name":
			return getName();
		case "label":
			return getLabel();
		case "concentrationStart":
			return String.valueOf(getConcentrationStart());
		case "concentrationMin":
			return String.valueOf(getConcentrationMin());
		case "concentrationMax":
			return String.valueOf(getConcentrationMax());
		case "ID":
			return String.valueOf(getID());
		case "isConstant":
			return String.valueOf(isConstant());
		}
		return null;
	}
}
