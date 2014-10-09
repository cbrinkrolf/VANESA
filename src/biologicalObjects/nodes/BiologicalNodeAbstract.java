package biologicalObjects.nodes;

import graph.GraphInstance;
import graph.gui.Parameter;
import graph.jung.classes.MyGraph;
import graph.jung.graphDrawing.VertexShapes;
import gui.MainWindowSingleton;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import petriNet.Place;
import petriNet.Transition;
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
	
	private NodeStateChanged state = NodeStateChanged.UNCHANGED;
	
	private HashSet<NodeAttribute> nodeAttributes = new HashSet<>();

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
	 * Method to coarse a set of nodes to a hierarchical node with automatic generated id.
	 * @param vertices Nodes to coarse.
	 * @return The created coarseNode. Null, if coarsing was not successful.
	 */
	public static BiologicalNodeAbstract coarse(Set<BiologicalNodeAbstract> nodes){
		return BiologicalNodeAbstract.coarse(nodes, null, null);
	}
	
	/**
	 * Method to coarse a set of nodes to a hierarchical node with a given id. 
	 * ONLY TO BE USED DURING IMPORTING PROCESS, OTHERWISE USE METHOD WITHOUT ID PARAMETER!
	 * 
	 * @param nodes Nodes to coarse.
	 * @param id Id for the new generated coarse node.			
	 * @return The created coarseNode. Null, if coarsing was not successful.
	 * @author tloka
	 */
	public static BiologicalNodeAbstract coarse(Set<BiologicalNodeAbstract> nodes, Integer id, String label){
		
		if(nodes == null  || nodes.size()<1){
			return null;
		}
		BiologicalNodeAbstract coarseNode = computeCoarseType(nodes);
		
		if(coarseNode == null || !isCoarsingAllowed(nodes)){
//		if(coarseNode == null){
			showCoarsingErrorMessage();
			return null;
		}
			
		coarseNode = coarseNode.clone();
		coarseNode.cleanUpHierarchyElements();
		
		String lbl;
		if(label==null){
			lbl = JOptionPane.showInputDialog(null, null, 
					"Name of the coarse Node", JOptionPane.QUESTION_MESSAGE);
			if(lbl==null){
				return null;
			} else if(lbl.isEmpty()){
				lbl = "id_" + coarseNode.getID();
			}
		} else {
			lbl=label;
		}
		if(id==null){
			coarseNode.setID(true);
		} else {
			coarseNode.setID(id);
		}
		coarseNode.setLabel(lbl);
		coarseNode.setName(lbl);
		coarseNode.setTitle(lbl);
		
		for (BiologicalNodeAbstract node : nodes) {
			node.setStateChanged(NodeStateChanged.COARSED);
			node.setParentNode(coarseNode);
		}
		
		coarseNode.makeSubGraph(nodes);
		
		if(BiologicalNodeAbstract.isValidBorder(coarseNode.isPetriNet(), coarseNode.updateBorder())){
			
			coarseNode.updateEnvironment();	
			coarseNode.updateConnectingEdges();

			coarseNode.makeCoarseShape();
			coarseNode.getRootPathway().updateMyGraph();
			
			//coarseNode.printAllHierarchicalAttributes();

		} else {
			for (BiologicalNodeAbstract node : nodes) {
				node.setStateChanged(NodeStateChanged.UNCHANGED);
				node.setParentNode(coarseNode.getParentNode());
			}
			coarseNode = null;
			showCoarsingErrorMessage();
		}
		
		return coarseNode;
	}
	
	/**
	 * Checks, if the node selection is valid for coarsing operation.
	 * @param vertices
	 * @return true is coarsing is allowed for the given set of nodes, false otherwise
	 */
	private static boolean isCoarsingAllowed(Set<BiologicalNodeAbstract> vertices){
		BiologicalNodeAbstract parentNode = null;
		if(vertices.iterator().hasNext()){
			parentNode = vertices.iterator().next().getParentNode();
		}
		for(BiologicalNodeAbstract vertex : vertices){
			if(vertex.getParentNode()!=parentNode){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns a node that can be cloned to create a coarse node of the given
	 * set of nodes.
	 * 
	 * @param vertices
	 *            Nodes selected for coarsing operation
	 * @return A node, that would be contained in the border if vertices were coarsed.
	 * @author tloka
	 */
	private static BiologicalNodeAbstract computeCoarseType(
			Set<BiologicalNodeAbstract> vertices) {
		if(vertices==null || vertices.size()==0){
			return null;
		}
		MyGraph activeGraph = new GraphInstance().getPathway().getGraph();
		for(BiologicalNodeAbstract node : vertices){
			for(BiologicalEdgeAbstract conEdge : node.getConnectingEdges()){
				BiologicalNodeAbstract to = conEdge.getTo().getCurrentShownParentNode(activeGraph);
				BiologicalNodeAbstract from = conEdge.getFrom().getCurrentShownParentNode(activeGraph);
				if(!(vertices.contains(to) && vertices.contains(from))){
					return node;
				}
			}
		}
		return vertices.iterator().next();
	}
	
	/**
	 * Updates the connecting edges based on border and environment.
	 * Border and Environment must be updated before calling this method.
	 * @return The new set of connecting edges.
	 * @author tloka
	 */
	public Set<BiologicalEdgeAbstract> updateConnectingEdges(){
		connectingEdges.clear();
		for(BiologicalNodeAbstract node : border){
			for(BiologicalEdgeAbstract conEdge : node.getConnectingEdges()){
				if(environment.contains(conEdge.getTo().getCurrentShownParentNode(getGraph())) || 
						environment.contains(conEdge.getFrom().getCurrentShownParentNode(getGraph()))){
					connectingEdges.add(conEdge);
				}
			}
		}
		return connectingEdges;
	}
	
	/**
	 * Computes the set of border nodes based on the internal Graph.
	 * @return The new border.
	 * @author tloka
	 */
	public Set<BiologicalNodeAbstract> updateBorder(){
		border.clear();
		for(BiologicalEdgeAbstract edge : getAllEdges()){
			if(edge.getTo().getParentNode() != this){
				border.add(edge.getFrom());
			} else if(edge.getFrom().getParentNode() != this){
				border.add(edge.getTo());
			}
		}
		return border;
	}
	
	/**
	 * Checks if given Set of nodes is a valid border in the given type of graph.
	 * @param isPetriNet
	 * 			True to verify border candidate set for Petri Nets. False otherwise.
	 * @param borderNodes
	 * 			The border candidate set to verify.
	 * @return
	 * 			True, if border candidate set is valid, false otherwise.
	 * @author tloka
	 */
	private static boolean isValidBorder(boolean isPetriNet, Set<BiologicalNodeAbstract> borderNodes){
		if(isPetriNet && borderNodes.size()>=2){
			Iterator<BiologicalNodeAbstract> iterator = borderNodes.iterator();
			BiologicalNodeAbstract node1 = iterator.next();
			BiologicalNodeAbstract node2;
			while(iterator.hasNext()){
				node2 = iterator.next();
				if((node1 instanceof Place) != (node2 instanceof Place)){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Computes the set of environment nodes based on the internal graph.
	 * @return The new environment.
	 * @author tloka
	 */
	public Set<BiologicalNodeAbstract> updateEnvironment(){
		environment.clear();
		Set<BiologicalNodeAbstract> allNodes = new HashSet<BiologicalNodeAbstract>();
		allNodes.addAll(getAllNodes());
		for(BiologicalNodeAbstract node : allNodes){
			if(node.getParentNode() != this){
				if(getGraph().getJungGraph().getNeighborCount(node)==0){
					removeElement(node);
				} else {
					environment.add(node);
				}
			}
		}
		return environment;
	}
	
	/**
	 * Changes current shape to shape in coarse node layout.
	 * @author tloka
	 */
	public void makeCoarseShape(){
		Shape coarseShape = shape;
		if(!isPetriNet()){
			coarseShape = shapes.getEllipse();
		}
		coarseShape = shapes.makeCoarse(coarseShape);
		setDefaultShape(coarseShape);
		setShape(coarseShape);
	}
	
	/**
	 * Shows an error dialog, that coarsing the selected set of nodes is not possible.
	 * @author tloka
	 */
	private static void showCoarsingErrorMessage(){
		JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(), 
				"No coarsing possible with the given set of nodes.", 
				"Coarsing Error!", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Creates the Pathway (vertices and edges) of the new created 
	 * coarse node based on a given set of vertices and the active graph.
	 * @param vertices
	 * @author tloka
	 */
	private void makeSubGraph(Set<BiologicalNodeAbstract> vertices){
		MyGraph currentGraph = getActiveGraph();
		for(BiologicalNodeAbstract vertex : vertices){
			Set<BiologicalEdgeAbstract> conEdges = new HashSet<BiologicalEdgeAbstract>();
			conEdges.addAll(vertex.getConnectingEdges());
			for(BiologicalEdgeAbstract conEdge : conEdges){
				BiologicalNodeAbstract to = conEdge.getTo().getCurrentShownParentNode(currentGraph);
				BiologicalNodeAbstract from = conEdge.getFrom().getCurrentShownParentNode(currentGraph);
				if(to==this){
					to = conEdge.getTo().getCurrentShownParentNode(getGraph());
					addVertex(to, getGraph().getVertexLocation(to));
				} else {
					addVertex(to, currentGraph.getVertexLocation(to));
				}
				if(from==this){
					from = conEdge.getFrom().getCurrentShownParentNode(getGraph());
					addVertex(from, getGraph().getVertexLocation(from));
				} else {
					addVertex(from, currentGraph.getVertexLocation(from));
				}
				BiologicalEdgeAbstract newEdge = conEdge.clone();
				newEdge.setTo(to);
				newEdge.setFrom(from);
				if(newEdge.isValid(false)){
					addEdge(newEdge);
				}
			}
		}
	}
	
	/**
	 * Adds the set of nodes to the coarse node if possible.
	 * @param vertices Nodes to be added.
	 * @return true if nodes were successfully added, false otherwise.
	 * @author tloka
	 */
	public boolean tryAddToCoarseNode(Set<BiologicalNodeAbstract> vertices){
		MyGraph activeGraph = getActiveGraph();
		Set<BiologicalNodeAbstract> relevantNodes = new HashSet<BiologicalNodeAbstract>();
		relevantNodes.addAll(vertices);
		relevantNodes.add(this);
		Set<BiologicalNodeAbstract> borderCandidate = new HashSet<BiologicalNodeAbstract>();
		for(BiologicalNodeAbstract vertex : relevantNodes){
			for(BiologicalEdgeAbstract conEdge : vertex.getConnectingEdges()){
				BiologicalNodeAbstract from = conEdge.getFrom().getCurrentShownParentNode(activeGraph);
				BiologicalNodeAbstract to = conEdge.getTo().getCurrentShownParentNode(activeGraph);
				if(relevantNodes.contains(from) && relevantNodes.contains(to)){
					continue;
				} else {
					borderCandidate.add(vertex);
				}
			}
		}
		if(isValidBorder(isPetriNet(), borderCandidate)){
			return(addToCoarseNode(vertices));
		} else {
			return false;
		}
	}
	
	/**
	 * Adds the input set of nodes to the coarse node if possible.
	 * @param vertices Nodes to add.
	 * @return true, if adding nodes was possible and successful. false otherwise.
	 * @author tloka
	 */
	private boolean addToCoarseNode(Set<BiologicalNodeAbstract> vertices){
		
		for(BiologicalNodeAbstract vertex : vertices){
			if(vertex.getEnvironment().contains(this)){
				for(BiologicalEdgeAbstract conEdge : vertex.getConnectingEdges()){
					BiologicalEdgeAbstract newEdge = conEdge.clone();
					BiologicalNodeAbstract newNode = null;
					if(conEdge.getTo().getCurrentShownParentNode(vertex.getGraph())==this){
						newNode = conEdge.getTo().getCurrentShownParentNode(getGraph());
						newEdge.setFrom(conEdge.getFrom().getCurrentShownParentNode(vertex.getGraph()));
						newEdge.setTo(newNode);
					}
					else if(conEdge.getFrom().getCurrentShownParentNode(vertex.getGraph())==this){
						newNode = conEdge.getFrom().getCurrentShownParentNode(getGraph());
						newEdge.setTo(conEdge.getTo().getCurrentShownParentNode(vertex.getGraph()));
						newEdge.setFrom(newNode);
					}
					if(newNode!=null){
						vertex.addVertex(newNode, getGraph().getVertexLocation(newNode));
					}
					if(newEdge.isValid(false)){
						vertex.addEdge(newEdge);
					}
				}
				vertex.removeElement(this);
				vertex.updateBorder();
				vertex.updateEnvironment();
				vertex.updateConnectingEdges();
			}
			vertex.setStateChanged(NodeStateChanged.COARSED);
			vertex.setParentNode(this);
		}
		makeSubGraph(vertices);
		updateBorder();
		updateEnvironment();
		updateConnectingEdges();
//		printAllHierarchicalAttributes();
		getRootPathway().updateMyGraph();
		updateNodeType();
		return true;
	}
	
	/** If coarse node type is not identical in Petri Net, node is flattened and re-coarsed with the
	 *  correct node type.
	 *  @author tloka
	 */
	public void updateNodeType(){
		if(border.isEmpty()){
			return;
		}
		if(border.iterator().next() instanceof Place != this instanceof Place){
			Set<BiologicalNodeAbstract> innerNodes = new HashSet<BiologicalNodeAbstract>();
			innerNodes.addAll(getInnerNodes());
			this.flat();
			MainWindowSingleton.getInstance().removeTab(false, getTab().getTitelTab(), this);
			BiologicalNodeAbstract bna = BiologicalNodeAbstract.coarse(innerNodes, getID(), getLabel());
			setGraph(bna.getGraph());
			new GraphInstance().getPathway().getGraph().getVisualizationViewer().repaint();
			bna.printAllHierarchicalAttributes();
		}
	}
	
	/**
	 * Development tool: method to print all relevant hierarchy sets.
	 */
	public void printAllHierarchicalAttributes(){
		System.out.println("All Nodes:");
		for(BiologicalNodeAbstract node : getAllNodes()){
			System.out.println(node.getLabel());
		}
		System.out.println("");
		System.out.println("All Edges:");
		for(BiologicalEdgeAbstract edge : getAllEdges()){
			System.out.println(edge.getFrom().getLabel() + "->" + edge.getTo().getLabel());
		}
		System.out.println("");
		System.out.println("Border: ");
		for(BiologicalNodeAbstract bnode : border){
			System.out.println(bnode.getLabel());
		}
		System.out.println("");
		System.out.println("Environment: ");
		for(BiologicalNodeAbstract bnode : environment){
			System.out.println(bnode.getLabel());
		}
		System.out.println("");
		System.out.println("Connecting Edges:");
		for(BiologicalEdgeAbstract edge : getConnectingEdges()){
			System.out.println(edge.getFrom().getLabel() + "->" + edge.getTo().getLabel());
		}
		System.out.println("");
	}

	/**
	 * Method to flat the coarse node back into the original subpathway.
	 * @author tloka
	 */
	public void flat() {

		if (!isCoarseNode())
			return;
		
//		printAllHierarchicalAttributes();
		if(this.getParentNode()!=null && this.getParentNode().getGraph()!=getActiveGraph()){
			return;
		}

		for(BiologicalNodeAbstract node : getInnerNodes()){
			node.setParentNode(getParentNode());
		}

		this.setStateChanged(NodeStateChanged.FLATTENED);
		
		getRootPathway().updateMyGraph();
		
//		printAllHierarchicalAttributes();
	}
	
	/** 
	 * Removes a connecting edge with same from, to and directed properties as the input edge.
	 */
	public void removeConnectingEdge(BiologicalEdgeAbstract edge){
		Set<BiologicalEdgeAbstract> conEdges = new HashSet<BiologicalEdgeAbstract>();
		conEdges.addAll(getConnectingEdges());
		for(BiologicalEdgeAbstract conEdge : conEdges){
			if(conEdge.getTo()==edge.getTo() && conEdge.getFrom()==edge.getFrom() && conEdge.isDirected() == edge.isDirected()){
				getConnectingEdges().remove(conEdge);
			}
		}
	}
	
	/** 
	 * Re-Initialise all hierarchy elements and sets for providing pointer-conflicts.
	 */
	public void cleanUpHierarchyElements(){
		border = new HashSet<BiologicalNodeAbstract>();
		environment = new HashSet<BiologicalNodeAbstract>();
		connectingEdges = new HashSet<BiologicalEdgeAbstract>();
		setGraph(new MyGraph(this));
		setStateChanged(NodeStateChanged.UNCHANGED);
	}
	
	/**
	 * To get all internal non-environment nodes.
	 * @return The internal nodes excluding environment nodes.
	 */
	public Collection<BiologicalNodeAbstract> getInnerNodes(){
		Collection<BiologicalNodeAbstract> innerNodes = new HashSet<BiologicalNodeAbstract>();
		if(getAllNodes().isEmpty()){
			return innerNodes;
		}
		for(BiologicalNodeAbstract node : getAllNodes()){
			if(!environment.contains(node)){
				innerNodes.add(node);
			}
		}
		return innerNodes;
	}
	
	/**
	 * Checks if node is a coarse node with a non-empty internal subgraph 
	 * @return true, if coarse-node.
	 */
	public boolean isCoarseNode(){
		if(getAllNodes().isEmpty()){
			return false;
		}
		if(getGraph(false)==null){
			return false;
		}
		return true;
	}
	
	/**
	 * Opens the coarse-node in the actually opened window without flatting it permanently.
	 */
	public void showSubPathway(){
		if(!isCoarseNode()){
			return;
		}
		Pathway currentGraph = new GraphInstance().getPathway();
		currentGraph.removeElement(this);
		for(BiologicalNodeAbstract node : getInnerNodes()){
			currentGraph.addVertex(node, this.getGraph().getVertexLocation(node));
			node.setHidden(true);
		}
		for(BiologicalEdgeAbstract edge : getConnectingEdges()){
			BiologicalEdgeAbstract e = edge.clone();
			e.setTo(e.getTo().getCurrentShownParentNode(currentGraph.getGraph()));
			e.setFrom(e.getFrom().getCurrentShownParentNode(currentGraph.getGraph()));
			if(e.isValid(false)){
				currentGraph.addEdge(e);
			}
		}
		for(BiologicalEdgeAbstract edge : getAllEdges()){
			if(!environment.contains(edge.getTo()) && !environment.contains(edge.getFrom())){
				currentGraph.addEdge(edge);
			}
		}
		if(getParentNode()!=null){
			getParentNode().addToOpenedSubPathways(this);
		} else {
			getRootPathway().addToOpenedSubPathways(this);
		}
		
		currentGraph.getGraph().updateLayout();
	}
	
	/**
	 * Closes the coarse-node if it was opened in the actually opened window.
	 */
	public void hideSubPathway(){
		Pathway currentGraph = new GraphInstance().getPathway();
		if(!isCoarseNode() | currentGraph == this){
			return;
		}
		if(!getBorder().isEmpty()){
			currentGraph.addVertex(this, currentGraph.getGraph().getVertexLocation(getBorder().iterator().next()));
		} else {
			currentGraph.addVertex(this, currentGraph.getGraph().getVertexLocation(getInnerNodes().iterator().next()));
		}
		for(BiologicalNodeAbstract node : getInnerNodes()){
			node.hideSubPathway();
			node.setHidden(false);
			currentGraph.removeElement(node);
		}
		for(BiologicalEdgeAbstract edge : getConnectingEdges()){
			BiologicalEdgeAbstract e = edge.clone();
			e.setTo(e.getTo().getCurrentShownParentNode(currentGraph.getGraph()));
			e.setFrom(e.getFrom().getCurrentShownParentNode(currentGraph.getGraph()));
			if(e.isValid(false)){
				currentGraph.addEdge(e);
			}
		}
		if(getParentNode()!=null){
			getParentNode().removeFromOpenedSubPathways(this);
		} else {
			getRootPathway().removeFromOpenedSubPathways(this);
		}
		currentGraph.getGraph().updateLayout();
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
	
	public void setID(){
		setID(false);
	}

	public void setID(boolean overwriteOldID) {
		set = new GraphInstance().getPathway().getIdSet();
		// System.out.println(new GraphInstance().getPathway().getName());
		// set id to highest current id+1;
		if (overwriteOldID || ID <= 0) {
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

	public Set<BiologicalNodeAbstract> getAllRootNodes() {
		Set<BiologicalNodeAbstract> rootNodes = new HashSet<BiologicalNodeAbstract>();
		for(BiologicalNodeAbstract node : getInnerNodes()){
			if(!node.isCoarseNode()){
				rootNodes.add(node);
			} else {
				rootNodes.addAll(node.getAllRootNodes());
			}
		}
		return rootNodes;
	}
	
	/**
	 * double value Attribute
	 * 
	 * @param nodeAttributeType
	 * @param name
	 * @param doublevalue
	 */
	public void addAttribute(int nodeAttributeType, String name, double doublevalue){
		nodeAttributes.add(new NodeAttribute(nodeAttributeType, name, doublevalue));
	}
	
	/**
	 * string value attribute
	 * 
	 * @param nodeAttributeType
	 * @param name
	 * @param stringvalue
	 */
	public void addAttribute(int nodeAttributeType, String name, String stringvalue){
		this.nodeAttributes.add(new NodeAttribute(nodeAttributeType, name, stringvalue));
	}
	
	public HashSet<NodeAttribute> getNodeAttributes(){
		return this.nodeAttributes;
	}
	
	public void resetNodeAttributes(){
		this.nodeAttributes = new HashSet<BiologicalNodeAbstract.NodeAttribute>();
	}
	
	/**
	 * 
	 * @param name
	 * @return Attribute of the node with given name
	 * if not found, returns null!
	 */
	public NodeAttribute getNodeAttributeByName(String name){
		for(NodeAttribute na : nodeAttributes){
			if(na.getName().equals(name))
				return na;
		}
		return null;
	}
	
	public boolean hasAttributeByName(String name){
		for(NodeAttribute na : nodeAttributes){
			if(na.getName().equals(name))
				return true;
		}
		return false;
	}

	
	/**
	 * 
	 * @author mlewinsk
	 *
	 * Node Attributes are used to save node specific properties which can be used for coloring, analysis or other purposes.
	 * 
	 */
	public class NodeAttribute{
		private int type; //FROM NodeAttributeTypes
		private String name;
		private double doublevalue;
		private String stringvalue;
		
		/**
		 * @param nodeAttributeType
		 * @param doublevalue
		 *  Constructor for double attributes
		 */
		public NodeAttribute(int nodeAttributeType, String name, double doublevalue){
			this.type = nodeAttributeType;
			this.name = name;
			this.doublevalue = doublevalue;
			this.stringvalue = "";
		}
		
		/**
		 * @param nodeAttributeType
		 * @param stringvalue
		 *  Constructor for string attributes
		 */
		public NodeAttribute(int nodeAttributeType, String name, String stringvalue){
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
			result = prime * result
					+ ((stringvalue == null) ? 0 : stringvalue.hashCode());
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
			if (Double.doubleToLongBits(doublevalue) != Double
					.doubleToLongBits(other.doublevalue))
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
	/**
	 * 
	 * @author mlewinsk
	 *
	 * Attribute Types define the general category of the attribute
	 */	
}
