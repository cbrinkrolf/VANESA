package biologicalObjects.nodes;

import graph.GraphInstance;
import graph.gui.Parameter;
import graph.jung.classes.MyGraph;
import graph.jung.graphDrawing.VertexShapes;
import graph.layouts.Circle;
import gui.MainWindow;
import gui.MainWindowSingleton;
import gui.visualization.VisualizationConfigBeans.Bean;

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
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import petriNet.Place;
import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.NodeStateChanged;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import configurations.NetworkSettings;
import configurations.NetworkSettingsSingelton;

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

	private Vector<Double> petriNetSimulationData = new Vector<Double>();

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
	
	private Set<BiologicalNodeAbstract> leafNodes = new HashSet<BiologicalNodeAbstract>();

	private Set<BiologicalNodeAbstract> border = new HashSet<BiologicalNodeAbstract>();

	private Set<BiologicalNodeAbstract> environment = new HashSet<BiologicalNodeAbstract>();
	
	private Set<BiologicalNodeAbstract> predefinedEnvironment = new HashSet<BiologicalNodeAbstract>();

	private Set<BiologicalEdgeAbstract> connectingEdges = new HashSet<BiologicalEdgeAbstract>();
	
	private NodeStateChanged state = NodeStateChanged.UNCHANGED;
	
	private HashSet<NodeAttribute> nodeAttributes = new HashSet<>();
	
	private boolean markedAsEnvironment = false;
	
	private boolean markedAsCoarseNode = false;
	
	private Point2D parentNodeDistance = new Point2D.Double(0,0);
	
	private MainWindow mainWindow = MainWindowSingleton.getInstance();
	
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

	public void attributeSetter(String className, BiologicalNodeAbstract bna){
		mainWindow.nodeAttributeChanger(bna, false);
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
	 * @author tloka
	 */
	public static BiologicalNodeAbstract coarse(Set<BiologicalNodeAbstract> nodes){
		return BiologicalNodeAbstract.coarse(nodes, null, null);
	}
	
	public static BiologicalNodeAbstract coarse(BiologicalNodeAbstract node){
		if(node.isCoarseNode()){
			return node;
		}
		for(BiologicalEdgeAbstract e : node.getConnectingEdges()){
			Pathway neighborParent = node.getRootPathway();
			if(e.getFrom()!=node){
				neighborParent = e.getFrom().getParentNode()==null ? node.getRootPathway() : e.getFrom().getParentNode();
				node.addVertex(e.getFrom(), neighborParent.getGraph().getVertexLocation(e.getFrom()));
				node.getPredefinedEnvironment().add(e.getFrom());
				e.getFrom().removeConnectingEdge(e);
			} else if(e.getTo()!=node){
				neighborParent = e.getTo().getParentNode()==null ? node.getRootPathway() : e.getTo().getParentNode();
				node.addVertex(e.getTo(), neighborParent.getGraph().getVertexLocation(e.getTo()));
				node.getPredefinedEnvironment().add(e.getTo());
				e.getTo().removeConnectingEdge(e);
			}
		}
		node.makeCoarseShape();
		node.markAsCoarseNode(true);
		node.updateHierarchicalAttributes();
		node.getRootPathway().updateMyGraph();
		return node;
	}
	
	public static BiologicalNodeAbstract coarse(Set<BiologicalNodeAbstract> nodes, Integer id, String label){
		return BiologicalNodeAbstract.coarse(nodes,id,label,null);
	}

	
	/**
	 * Method to coarse a set of nodes to a hierarchical node with a given id. 
	 * ONLY SET ID DURING IMPORTING PROCESS.
	 * 
	 * @param nodes Nodes to coarse.
	 * @param id Id for the new generated coarse node.			
	 * @return The created coarseNode. Null, if coarsing was not successful.
	 * @author tloka
	 */
	public static BiologicalNodeAbstract coarse(Set<BiologicalNodeAbstract> nodes, Integer id, String label, BiologicalNodeAbstract rootNode){
		
		return coarse(nodes, id, label, rootNode, true);
	}
	
	public static BiologicalNodeAbstract coarse(Set<BiologicalNodeAbstract> nodes, Integer id, String label, BiologicalNodeAbstract rootNode, boolean updatePathway){
		// Stop, if less than two nodes are selected.
				if(nodes == null  || nodes.size()<1){
					return null;
				}
				
				BiologicalNodeAbstract coarseNode = rootNode;
				// Get the node type
				if(rootNode == null || rootNode.getRootPathway().isPetriNet())
					coarseNode = computeCoarseType(nodes);
				
				// If coarsing is not valid, abort.
				if(coarseNode == null || !isCoarsingAllowed(coarseNode.isPetriNet(), nodes)){
					showCoarsingErrorMessage();
					return null;
				}
					
				// Create a clean node of the correct node type
				coarseNode = coarseNode.clone();
				coarseNode.cleanUpHierarchyElements();
				coarseNode.setRootNode(rootNode);
				
				// Set id
				if(id==null){
					coarseNode.setID(true);
				} else {
					try{
						coarseNode.setID(id);
					} catch(IDAlreadyExistException ex){
						coarseNode.setID(true);
					}
				}
				
				// Set label, name and title
				String answer = label;
				if(label==null){
					answer = JOptionPane.showInputDialog(MainWindowSingleton.getInstance(), null, 
							"Name of the coarse Node", JOptionPane.QUESTION_MESSAGE);
					if(answer==null){
						return null;
					} else if(answer.isEmpty()){
						answer = "id_" + coarseNode.getID();
					}
				}
				coarseNode.setLabel(answer);
				coarseNode.setName(answer);
				coarseNode.setTitle(answer);

				// set attributes of the child nodes
				for (BiologicalNodeAbstract node : nodes) {
					node.setStateChanged(NodeStateChanged.COARSED);
					if(node.getParentNode()!=null){
						coarseNode.addVertex(node, node.getParentNode().getGraph().getVertexLocation(node));
					} else {
						coarseNode.addVertex(node, node.getRootPathway().getGraph().getVertexLocation(node));
					}
					node.setParentNode(coarseNode);
					coarseNode.getLeafNodes().addAll(node.getLeafNodes());
				}
						
				//compute border, environment and connecting edges
				coarseNode.updateHierarchicalAttributes();
				
				coarseNode.updateEdges();
				coarseNode.makeCoarseShape();
//				if(updatePathway){
					coarseNode.getRootPathway().updateMyGraph();
//				}


				return coarseNode;
	}
	
	/**
	 * Checks, if the node selection is valid for coarsing operation.
	 * @param isPetriNet True if Network is a Petri-Net.
	 * @param vertices Set of nodes.
	 * @return true is coarsing is allowed for the given set of nodes.
	 */
	private static boolean isCoarsingAllowed(boolean isPetriNet, Set<BiologicalNodeAbstract> vertices){
		BiologicalNodeAbstract parentNode = null;
		
		if(vertices.iterator().hasNext()){
			parentNode = vertices.iterator().next().getParentNode();
		}
		for(BiologicalNodeAbstract vertex : vertices){
			// fail if at least 2 nodes don't have the same parent node (can also be null)
			if(vertex.getParentNode()!=parentNode){
				return false;
			}
			// fail if at least 1 selected node is environment node of the current shown graph
			if(GraphInstance.getPathwayStatic().isBNA() && ((BiologicalNodeAbstract) GraphInstance.getPathwayStatic()).getEnvironment().contains(vertex)){
				return false;
			}
		}
		return hasValidBorder(isPetriNet, vertices);
	}
	
	/**
	 * Checks, if a selection of nodes has a valid border in the given type of graph.
	 * @param isPetriNet True if Network is a Petri-Net.
	 * @param vertices Set of nodes.
	 * @return true, if set of nodes has a valid border.
	 */
	private static boolean hasValidBorder(boolean isPetriNet, Set<BiologicalNodeAbstract> vertices){
		if(!isPetriNet)
			return true;
		Set<BiologicalNodeAbstract> leafVertices = new HashSet<BiologicalNodeAbstract>();
		for(BiologicalNodeAbstract v : vertices){
			leafVertices.addAll(v.getLeafNodes());
		}
		Set<BiologicalNodeAbstract> borderNodes = new HashSet<BiologicalNodeAbstract>();
		for(BiologicalNodeAbstract v : leafVertices){
			for(BiologicalEdgeAbstract e : v.getConnectingEdges()){
				if(!leafVertices.contains(e.getTo()) || !leafVertices.contains(e.getFrom())){
					borderNodes.add(v);
					break;
				}
			}
		}
		return isValidBorder(isPetriNet,borderNodes);
	}
	
	/**
	 * Checks if given Set of nodes is a valid border in the given type of graph.
	 * @param isPetriNet True if Network is a Petri-Net.
	 * @param vertices Set of nodes.
	 * @return True, if border candidate set is valid.
	 * @author tloka
	 */
	private static boolean isValidBorder(boolean isPetriNet, Set<BiologicalNodeAbstract> borderNodes){
		
		// if coarse node is part of a petri net, check if all border nodes are the same
		// tape of node (place or transition)
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
	 * Returns a node that can be cloned to create a coarse node of the given
	 * set of nodes.
	 * 
	 * @param vertices Nodes selected for coarsing operation
	 * @return A node, that would be contained in the border if vertices were coarsed.
	 * @author tloka
	 */
	private static BiologicalNodeAbstract computeCoarseType(
			Set<BiologicalNodeAbstract> vertices) {
		
		// Stop if no input nodes
		if(vertices==null || vertices.size()==0){
			return null;
		}
		MyGraph activeGraph = new GraphInstance().getPathway().getGraph();
		
		// return the first found border node.
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
	
	public void updateHierarchicalAttributes(){
		updateConnectingEdges();
		updateBorderEnvironment();
	}
	
	private void updateConnectingEdges(){
		if(!isCoarseNode()){
			return;
		}
		connectingEdges.clear();
		for(BiologicalNodeAbstract ln : getLeafNodes()){
			for(BiologicalEdgeAbstract ce : ln.getConnectingEdges()){
				if(!getLeafNodes().contains(ce.getTo()) || !getLeafNodes().contains(ce.getFrom())){
					if(!connectingEdges.contains(ce)){
						connectingEdges.add(ce);
					}
				}
			}
		}
	}
	
	private void updateBorderEnvironment(){
		for(BiologicalNodeAbstract n : environment){
			removeElement(n);
		}
		environment.clear();
		border.clear();
		for(BiologicalNodeAbstract n : getLeafNodes()){
			for(BiologicalEdgeAbstract e : n.getConnectingEdges()){
				if(e.getTo().getCurrentShownParentNode(getGraph())==null && e.getFrom().getCurrentShownParentNode(getGraph())!=null){
					border.add(e.getFrom());
					if(e.getTo().getParentNode()!=null){
						addVertex(e.getTo(), e.getTo().getParentNode().getGraph().getVertexLocation(e.getTo()));
					} else {
						addVertex(e.getTo(), e.getTo().getRootPathway().getGraph().getVertexLocation(e.getTo()));
					}
					predefinedEnvironment.remove(e.getTo());
					environment.add(e.getTo());
				}
				if(e.getFrom().getCurrentShownParentNode(getGraph())==null && e.getTo().getCurrentShownParentNode(getGraph())!=null){
					border.add(e.getTo());
					if(e.getFrom().getParentNode()!=null){
						addVertex(e.getFrom(), e.getFrom().getParentNode().getGraph().getVertexLocation(e.getFrom()));
					} else {
						addVertex(e.getFrom(), e.getFrom().getRootPathway().getGraph().getVertexLocation(e.getFrom()));
					}
					predefinedEnvironment.remove(e.getFrom());
					environment.add(e.getFrom());
				}
			}
		}
		Pathway neighborParent = getRootPathway();
		for(BiologicalNodeAbstract pe : predefinedEnvironment){
			neighborParent = pe.getParentNode()==null ? getRootPathway() : pe.getParentNode();
			addVertex(pe, neighborParent.getGraph().getVertexLocation(pe));
			environment.add(pe);
		}
	}
	
	/**
	 * Changes current shape to shape in coarse node layout.
	 * @author tloka
	 */
	public void makeCoarseShape(){
		Shape coarseShape;
		if(getRootNode() == null)
			coarseShape = shape;
		else 
			coarseShape = getRootNode().getShape();
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
	
	public boolean addToCoarseNode(Set<BiologicalNodeAbstract> vertices, HashMap<BiologicalNodeAbstract, Point2D> vertexLocations){
		Set<BiologicalNodeAbstract> ln = new HashSet<BiologicalNodeAbstract>();
		ln.addAll(getLeafNodes());
		for(BiologicalNodeAbstract v : vertices){
			ln.addAll(v.getLeafNodes());
			if(v.getParentNode()!=this.getParentNode()){
				return false;
			}
		}
		if(hasValidBorder(getRootPathway().isPetriNet(),ln)){
			leafNodes = ln;
			for(BiologicalNodeAbstract n : vertices){
				n.setParentNode(this);
				n.setParentNodeDistance(Circle.get2Ddistance(GraphInstance.getMyGraph().getVertexLocation(this),vertexLocations.get(n)));
				n.setStateChanged(NodeStateChanged.ADDED);
				addVertex(n, vertexLocations.get(n));
				if(getEnvironment().contains(n)){
					getEnvironment().remove(n);
				}
			}
			updateHierarchicalAttributes();
			getRootPathway().updateMyGraph();
			updateNodeType();
			return true;
		}
		return false;
		
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
//			bna.printAllHierarchicalAttributes();
		}
	}
	
	public static void addConnectingEdge(BiologicalEdgeAbstract bea){
		if(bea.getFrom().isCoarseNode() || bea.getTo().isCoarseNode())
			return;
		bea.getFrom().addConEdge(bea);
		bea.getTo().addConEdge(bea);
	}
	
	private void addConEdge(BiologicalEdgeAbstract bea){
		boolean edgeAlreadyExist = false;
		for(BiologicalEdgeAbstract conEdge : getConnectingEdges()){
			if(conEdge.getFrom()==bea.getFrom() && conEdge.getTo()==bea.getTo() && conEdge.isDirected() == bea.isDirected()){
				edgeAlreadyExist = true;
				break;				
			}
		}
		if(!edgeAlreadyExist){
			getConnectingEdges().add(bea);
		}	
	}
	
	/**
	 * Development tool: method to print all relevant hierarchy sets.
	 */
	public void printAllHierarchicalAttributes(){
		System.out.println("Name:");
		System.out.println(getLabel());
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

		// Stop, if flattening is not possible.
		if (!isCoarseNode())
			return;
		
		if(this.getParentNode()!=null && this.getParentNode().getGraph()!=getActiveGraph()){
			return;
		}

		// Change node state.
		this.setStateChanged(NodeStateChanged.FLATTENED);
		
		// Update all graphs.
		getRootPathway().updateMyGraph();
		
		// Update the parent node of flattened node's children.
		// Must be called AFTER updateMyGraph()!
		for(BiologicalNodeAbstract node : getInnerNodes()){
			node.setParentNode(getParentNode());
		}
		
		//remove id from rootPathway id set.
		getIdSet().remove(ID);
		
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
				getIdSet().remove(conEdge.getID());
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
			if(!environment.contains(node.getCurrentShownParentNode(getGraph()))){
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
		if(getInnerNodes().isEmpty()){
			return false;
		}
		if(getGraph(false)==null){
			return false;
		}
		return true;
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

//		if (isReference() || isHidden()) {
		if (isHidden()){
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
	
	public void setCoarseNodesize(){
		if(isCoarseNode()){
			setNodesize(defaultNodesize + Math.log(getLeafNodes().size())/Math.log(100));
		}
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
	
	public int getHierarchyDistance(BiologicalNodeAbstract otherNode){
		BiologicalNodeAbstract commonParent = getLastCommonParentNode(otherNode);
		if(commonParent==null){
			return -1;
		}
		int distance = 0;
		BiologicalNodeAbstract currentParent = this;
		while(currentParent!=commonParent){
			distance += 1;
			currentParent = currentParent.getParentNode();
		}
		int newDistance = 0;
		currentParent = otherNode;
		while(currentParent!=commonParent){
			newDistance += 1;
			currentParent = currentParent.getParentNode();
		}
		return Math.max(distance, newDistance);
	}
	
	public BiologicalNodeAbstract getLastParentNode() {
		BiologicalNodeAbstract lastParent = getParentNode();
		if(lastParent==null){
			return this;
		}
		while(lastParent.getParentNode()!=null){
			lastParent = lastParent.getParentNode();
		}
		return lastParent;
	}
	
	public BiologicalNodeAbstract getLastCommonParentNode(BiologicalNodeAbstract otherNode){
		if(getLastParentNode() == otherNode.getLastParentNode() && getLastParentNode()!=null){
			BiologicalNodeAbstract lastCommonParentNode = getLastParentNode();
			while(true){
				for(BiologicalNodeAbstract childNode : lastCommonParentNode.getInnerNodes()){
					if(getAllParentNodes().contains(childNode) && otherNode.getAllParentNodes().contains(childNode)){
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
	
	public Set<BiologicalNodeAbstract> getCurrentShownChildrenNodes(MyGraph graph){
		Set<BiologicalNodeAbstract> cscn = new HashSet<BiologicalNodeAbstract>();
		for(BiologicalNodeAbstract n : getInnerNodes()){
			if(graph.getAllVertices().contains(n)){
				cscn.add(n);
			} else if (n.isCoarseNode()){
				cscn.addAll(n.getCurrentShownChildrenNodes(graph));
			}
		}
		return cscn;
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
	public void setID(int id) throws IDAlreadyExistException{
		if (this.ID == id) {
			System.out.println("return");
			return;
		} else {
			set = getIdSet();
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
		set = getIdSet();
		// System.out.println(new GraphInstance().getPathway().getName());
		// set id to highest current id+1;
		if (overwriteOldID || ID <= 0) {
			// System.out.println("neue ID");
			if (set.size() > 0) {
				// System.out.println("last: " + set.last());
				try{
				setID(set.last() + 1);
				} catch(IDAlreadyExistException ex){
					// cannot occur if program working fine.
					ex.printStackTrace();
				}
				// System.out.println("size: " + set.size());
				// System.out.println("groesster: " + set.last());
				// System.out.println("kleinster: " + set.first());
			} else {
				try{
				setID(100);
				} catch(IDAlreadyExistException ex){
					// cannot occur if program working fine.
					ex.printStackTrace();
				}
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
	
	public Set<BiologicalNodeAbstract> getPredefinedEnvironment() {
		return predefinedEnvironment;
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
	
	public Set<BiologicalNodeAbstract> getLeafNodes() {
		if(isCoarseNode()){
			return leafNodes;
		}
		Set<BiologicalNodeAbstract> ln = new HashSet<BiologicalNodeAbstract>();
		ln.add(this);
		return ln;
	}
	
	public void setStateChanged(NodeStateChanged state) {
		this.state = state;
	}

	public NodeStateChanged getStateChanged() {
		return state;
	}
	
	public void markAsEnvironment(boolean value){
		markedAsEnvironment = value;
	}
	
	protected void markAsCoarseNode(boolean value){
		markedAsCoarseNode = value;
	}
	
	public boolean isMarkedAsEnvironment(){
		return markedAsEnvironment;
	}
	
	public boolean isMarkedAsCoarseNode(){
		return markedAsCoarseNode;
	}
	
	public boolean isEnvironmentNodeOf(Pathway parentNodeOfInterest){
		if(isMarkedAsEnvironment()){
			return true;
		}
		if(parentNodeOfInterest instanceof BiologicalNodeAbstract && ((BiologicalNodeAbstract) parentNodeOfInterest).getEnvironment().contains(this)){
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
	 * @param nodeAttributeType
	 * @return Attributes of the node with given type, if none exist it returns empty.
	 */
	public HashSet<NodeAttribute> getNodeAttributesByType(int nodeAttributeType){
		HashSet<NodeAttribute> returnlist = new HashSet<>(nodeAttributes.size());
		for(NodeAttribute na : nodeAttributes){
			if(na.getType() == nodeAttributeType)
				returnlist.add(na);
		}
		return returnlist;
	}
	

	
	public Point2D getParentNodeDistance() {
		return parentNodeDistance;
	}

	public void setParentNodeDistance(Point2D parentNodeDistance) {
		this.parentNodeDistance = parentNodeDistance;
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
}
