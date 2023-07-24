package biologicalElements;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.BiologicalEdgeAbstractFactory;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstractFactory;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import graph.ChangedFlags;
import graph.compartment.CompartmentManager;
import graph.groups.Group;
import graph.gui.Boundary;
import graph.gui.CoarseNodeDeleteDialog;
import graph.gui.EdgeDeleteDialog;
import graph.gui.Parameter;
import graph.jung.classes.MyGraph;
import graph.jung.classes.MyVisualizationViewer;
import graph.layouts.Circle;
import gui.GraphTab;
import gui.MainWindow;
import gui.PopUpDialog;
import petriNet.PetriNetProperties;
import petriNet.PetriNetSimulation;
import transformation.TransformationInformation;
import util.FormulaSafety;

public class Pathway implements Cloneable {
	private File file = null;
	private String name;
	private String version = "";
	private String date = "";
	private String title = "";
	private String author = "";
	private String referenceNumber = "";
	private String link = "";
	private String organism = "";
	private boolean orgSpecification;
	private String description = "";

	private boolean isPetriNet = false;
	private PetriNetProperties petriNetProperties = null;
	private PetriNetSimulation petriNetSimulation = null;

	private MyGraph graph;
	private GraphTab tab;
	private Pathway parent;
	private final SortedSet<Integer> ids = new TreeSet<>();
	private final Set<BiologicalNodeAbstract> closedSubPathways = new HashSet<>();

	private final HashMap<String, ChangedFlags> changedFlags = new HashMap<>();
	private HashMap<Parameter, GraphElementAbstract> changedParameters = new HashMap<>();
	private HashMap<Place, Double> changedInitialValues = new HashMap<>();
	private HashMap<Place, Boundary> changedBoundaries = new HashMap<>();
	private BiologicalNodeAbstract rootNode;
	private HashMap<BiologicalNodeAbstract, Point2D> vertices = new HashMap<>();

	// no graph tab is assigned / created. this is used for rule editing window
	private final boolean headless;
	private ArrayList<Group> groups = new ArrayList<>();
	private TransformationInformation transformationInformation = null;
	private final CompartmentManager compartmentManager = new CompartmentManager();

	public Pathway(String name, boolean headless) {
		this.headless = headless;
		this.name = name.trim();
		// no graph tab is created, used for editing rules window
		if (headless) {
			this.title = this.name;
			graph = new MyGraph(this);
		}
	}

	public Pathway(String name) {
		this.headless = false;
		this.name = name.trim();
		this.title = this.name;
		graph = new MyGraph(this);
		tab = new GraphTab(this.name, graph.getGraphVisualization());
	}

	public Pathway(String name, Pathway parent) {
		this.headless = false;
		this.name = name.trim();
		this.title = this.name;
		this.parent = parent;
	}

	protected void cleanVertices() {
		vertices = new HashMap<>();
	}

	public Set<BiologicalEdgeAbstract> getEdges() {
		Set<BiologicalEdgeAbstract> edges = new HashSet<>();
		for (BiologicalNodeAbstract n : getVertices().keySet()) {
			edges.addAll(n.getConnectingEdges());
		}
		return edges;
	}

	public void changeBackground(String color) {
		if (color.equals("black")) {
			getGraph().getVisualizationViewer().setBackground(Color.BLACK);
			getGraph().getVisualizationViewer().repaint();
			getGraph().getSatelliteView().setBackground(Color.WHITE);
			getGraph().getSatelliteView().repaint();
		} else if (color.equals("white")) {
			getGraph().getVisualizationViewer().setBackground(Color.WHITE);
			getGraph().getSatelliteView().setBackground(Color.WHITE);
			getGraph().getVisualizationViewer().repaint();
			getGraph().getSatelliteView().repaint();
		}
	}

	public BiologicalNodeAbstract addVertex(String name, String label, String elementDeclaration, String compartment,
			Point2D p) {
		BiologicalNodeAbstract bna = BiologicalNodeAbstractFactory.create(elementDeclaration, null);
		bna.setName(name);
		bna.setLabel(label);
		this.getCompartmentManager().setCompartment(bna, this.getCompartmentManager().getCompartment(compartment));
		if (isBNA()) {
			Pathway parent = ((BiologicalNodeAbstract) this).getParentNode() == null ? getRootPathway()
					: ((BiologicalNodeAbstract) this).getParentNode();
			bna.setParentNodeDistance(
					Circle.get2Ddistance(parent.getGraph().getVertexLocation((BiologicalNodeAbstract) this), p));
		}
		return addVertex(bna, p);
	}

	public BiologicalNodeAbstract addVertex(BiologicalNodeAbstract bna, Point2D p) {
		if (graph.getJungGraph().containsVertex(bna)) {
			return bna;
		}
		bna.setLabel(bna.getLabel().trim());
		bna.setName(bna.getName().trim());
		if (isPetriNet) {
			boolean createdRef = false;
			for (BiologicalNodeAbstract node : getAllGraphNodes()) {
				if (node.getName().equals(bna.getName())) {
					if (node.getClass().equals(bna.getClass())) {
						bna.setLogicalReference(node);
						createdRef = true;
					} else {
						PopUpDialog.getInstance().show("Type mismatch",
								"Cannot create logical node with the name: " + bna.getName() + ". Type mismatch of "
										+ bna.getClass() + " and " + node.getClass() + "!");
						System.err.println("Cannot create logical node with the name: " + bna.getName()
								+ ". Type mismatch of " + bna.getClass() + " and " + node.getClass() + "!");
						return null;
					}
				}
			}
			int i = 1;
			if (createdRef) {
				while (true) {
					if (!getAllNodeNames().contains(bna.getName() + "_" + i)) {
						bna.setName(bna.getName() + "_" + i);
						bna.setLabel(bna.getName());
						break;
					}
					i++;
				}
			}
		}
		vertices.put(bna, p);
		addVertexToView(bna, p);
		bna.setID(this);
		this.handleChangeFlags(ChangedFlags.NODE_CHANGED);
		return bna;
	}

	public BiologicalEdgeAbstract addEdge(String label, String name, BiologicalNodeAbstract from,
			BiologicalNodeAbstract to, String element, boolean directed) {
		BiologicalEdgeAbstract bea = BiologicalEdgeAbstractFactory.create(element, null);
		bea.setLabel(label);
		bea.setName(name);
		bea.setFrom(from);
		bea.setTo(to);
		bea.setDirected(directed);
		if (element.equals(Elementdeclerations.pnArc)) {
			((PNArc) bea).setProbability(1);
		} else if (element.equals(Elementdeclerations.pnInhibitorArc)) {
			((PNArc) bea).setProbability(1);
		}
		return addEdge(bea);
	}

	public BiologicalEdgeAbstract addEdge(BiologicalEdgeAbstract bea) {
		if (bea != null) {
			if (!bea.getFrom().isCoarseNode() && !bea.getTo().isCoarseNode()) {
				BiologicalNodeAbstract.addConnectingEdge(bea);
				bea.setID(this);
			}
			handleChangeFlags(ChangedFlags.EDGE_CHANGED);
			return bea;
		} else {
			new NullPointerException().printStackTrace();
		}
		return null;
	}

	public void drawEdge(BiologicalEdgeAbstract bea) {
		boolean add = true;
		for (BiologicalEdgeAbstract edge : getGraph().getJungGraph().findEdgeSet(bea.getFrom(), bea.getTo())) {
			if (bea.isDirected() == edge.isDirected()) {
				add = false;
				break;
			}
		}
		if (add) {
			getGraph().addEdge(bea);
			bea.setID(this);
		}
	}

	/**
	 * Add a vertex to the current view (MyGraph)
	 *
	 * @param bna The node
	 * @param p   The location
	 */
	public void addVertexToView(BiologicalNodeAbstract bna, Point2D p) {
		if (!getGraph().getAllVertices().contains(bna))
			getGraph().addVertex(bna, p);
	}

	/**
	 * Add an edge to the current view (MyGraph)
	 *
	 * @param bea   The edge
	 * @param force If true, missing nodes are added before to guarantee that the
	 *              edge will be added
	 */
	public void addEdgeToView(BiologicalEdgeAbstract bea, boolean force) {
		if (getGraph().getAllVertices().contains(bea.getFrom()) && getGraph().getAllVertices().contains(bea.getTo())) {
			getGraph().addEdge(bea);
		} else if (force) {
			addVertexToView(bea.getFrom(), getRootPathway().getVertices().get(bea.getFrom()));
			addVertexToView(bea.getTo(), getRootPathway().getVertices().get(bea.getTo()));
			getGraph().addEdge(bea);
		}
	}

	public void removeElement(GraphElementAbstract element) {
		if (element == null) {
			return;
		}
		// removeElementFromView(element);
		if (element.isVertex()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) element;
			getRootPathway().getVertices().remove(bna);
			for (BiologicalNodeAbstract parent : bna.getAllParentNodes()) {
				parent.getVertices().remove(bna);
			}
			bna.delete();

			this.handleChangeFlags(ChangedFlags.NODE_CHANGED);
		} else {
			BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) element;
			if (bea.getTo().isCoarseNode() || bea.getFrom().isCoarseNode()) {
				EdgeDeleteDialog dialog = new EdgeDeleteDialog(bea);
				Set<BiologicalEdgeAbstract> delBeas = dialog.getAnswer();
				// aborted
				if (delBeas != null) {
					for (BiologicalEdgeAbstract delBea : delBeas) {
						delBea.getFrom().removeConnectingEdge(delBea);
						delBea.getTo().removeConnectingEdge(delBea);
					}
				}
				return;
			}
			bea.getFrom().removeConnectingEdge(bea);
			bea.getTo().removeConnectingEdge(bea);
			handleChangeFlags(ChangedFlags.EDGE_CHANGED);
		}
	}

	public void removeElementWithID(GraphElementAbstract element) {
		removeElement(element);
		ids.remove(element.getID());
	}

	public int edgeGrade(BiologicalEdgeAbstract bea) {
		Set<BiologicalEdgeAbstract> conEdgesTo = new HashSet<>();
		for (BiologicalEdgeAbstract edge : bea.getFrom().getConnectingEdges()) {
			if (edge.isDirected() == bea.isDirected()) {
				if (edge.isDirected()) {
					if (bea.getTo().getConnectingEdges().contains(edge)
							&& edge.getFrom().getCurrentShownParentNode(getGraph()) == bea.getFrom()) {
						conEdgesTo.add(edge);
					}
				} else {
					if (bea.getTo().getConnectingEdges().contains(edge)) {
						conEdgesTo.add(edge);
					}
				}
			}
		}
		return conEdgesTo.size();
	}

	public boolean existEdge(BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		return graph.getJungGraph().findEdge(from, to) != null;
	}

	public BiologicalEdgeAbstract getEdge(BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		return graph.getJungGraph().findEdge(from, to);
	}

	public boolean containsVertex(BiologicalNodeAbstract bna) {
		return graph.getJungGraph().containsVertex(bna);
	}

	public boolean containsEdge(BiologicalEdgeAbstract bea) {
		return graph.getJungGraph().containsEdge(bea);
	}

	public Set<String> getAllNodeLabels() {
		Set<String> set = new HashSet<>();
		for (BiologicalNodeAbstract biologicalNodeAbstract : getAllGraphNodes()) {
			set.add(biologicalNodeAbstract.getLabel());
		}
		return set;
	}

	public Set<String> getAllNodeNames() {
		Set<String> set = new HashSet<>();
		for (BiologicalNodeAbstract biologicalNodeAbstract : this.getAllGraphNodes()) {
			set.add(biologicalNodeAbstract.getName());
		}
		return set;
	}
	
	public Set<String> getAllEdgeNames() {
		Set<String> set = new HashSet<>();
		for (BiologicalEdgeAbstract bea : this.getAllEdges()) {
			set.add(bea.getName());
		}
		return set;
	}

	public boolean hasGotAtLeastOneElement() {
		return !getAllGraphNodes().isEmpty();
	}

	public BiologicalNodeAbstract getNodeByLabel(String label) {
		for (BiologicalNodeAbstract bna : getAllGraphNodes()) {
			if (bna.getLabel().equals(label)) {
				return bna;
			}
		}
		return null;
	}

	public BiologicalNodeAbstract getNodeByName(String name) {
		for (BiologicalNodeAbstract bna : getAllGraphNodes()) {
			if (bna.getName().equals(name)) {
				return bna;
			}
		}
		return null;
	}

	public Collection<BiologicalEdgeAbstract> getAllEdges() {
		if (getGraph(false) == null) {
			return Collections.emptyList();
		}
		return getGraph().getAllEdges();
	}

	public List<BiologicalEdgeAbstract> getAllEdgesSorted() {
		Map<Integer, BiologicalEdgeAbstract> map = new HashMap<>();
		for (BiologicalEdgeAbstract bea : this.getAllEdges()) {
			map.put(bea.getID(), bea);
		}
		ArrayList<Integer> ids = new ArrayList<>(map.keySet());
		ids.sort(Integer::compare);
		List<BiologicalEdgeAbstract> sortedList = new ArrayList<>();
		for (Integer id : ids) {
			sortedList.add(map.get(id));
		}
		return sortedList;
	}

	public Collection<BiologicalNodeAbstract> getAllGraphNodes() {
		if (getGraph(false) == null) {
			return Collections.emptyList();
		}
		return getGraph().getAllVertices();
	}

	public List<BiologicalNodeAbstract> getAllGraphNodesSorted() {
		HashMap<Integer, BiologicalNodeAbstract> map = new HashMap<>();
		for (BiologicalNodeAbstract bna : this.getAllGraphNodes()) {
			map.put(bna.getID(), bna);
		}
		ArrayList<Integer> ids = new ArrayList<>(map.keySet());
		ids.sort(Integer::compare);
		List<BiologicalNodeAbstract> sortedList = new ArrayList<>();
		for (Integer id : ids) {
			sortedList.add(map.get(id));
		}
		return sortedList;
	}

	public List<BiologicalNodeAbstract> getAllGraphNodesSortedAlphabetically() {
		HashMap<String, ArrayList<BiologicalNodeAbstract>> map = new HashMap<>();
		for (BiologicalNodeAbstract bna : this.getAllGraphNodes()) {
			String name = bna.getName().toLowerCase();
			if (!map.containsKey(name)) {
				map.put(name, new ArrayList<>());
			}
			map.get(name).add(bna);
		}
		ArrayList<String> ids = new ArrayList<>(map.keySet());
		Collections.sort(ids);
		List<BiologicalNodeAbstract> sortedList = new ArrayList<>();
		for (String id : ids) {
			sortedList.addAll(map.get(id));
		}
		return sortedList;
	}

	public Set<BiologicalNodeAbstract> getSelectedNodes() {
		return getGraph().getVisualizationViewer().getPickedVertexState().getPicked();
	}

	public Set<BiologicalEdgeAbstract> getSelectedEdges() {
		return getGraph().getVisualizationViewer().getPickedEdgeState().getPicked();
	}

	public int countNodes() {
		return getGraph().getAllVertices().size();
	}

	public int countEdges() {
		return getGraph().getAllEdges().size();
	}

	public void mergeNodes(Set<BiologicalNodeAbstract> nodes) {
		if (nodes.size() == 0) {
			return;
		}
		HashMap<BiologicalNodeAbstract, Set<BiologicalNodeAbstract>> node2Refs = new HashMap<>();
		Set<BiologicalNodeAbstract> newNodes = new HashSet<>();
		for (BiologicalNodeAbstract bna : nodes) {
			if (bna.isLogical()) {
				if (nodes.contains(bna.getLogicalReference())) {
					if (!node2Refs.containsKey(bna.getLogicalReference())) {
						node2Refs.put(bna.getLogicalReference(), new HashSet<>());
					}
					node2Refs.get(bna.getLogicalReference()).add(bna);
				}
			} else {
				newNodes.add(bna);
			}
		}
		boolean merged = false;
		for (BiologicalNodeAbstract bna : node2Refs.keySet()) {
			mergeNodes(bna, node2Refs.get(bna));
			merged = true;
		}
		Set<BiologicalNodeAbstract> n = new HashSet<>();
		for (BiologicalNodeAbstract bna : newNodes) {
			if (bna.getRefs().size() == 0) {
				n.add(bna);
			} else {
				System.err.print("Node with id: " + bna.getID() + " and name: " + bna.getName()
						+ " cannot be merged due to unresolved references!");
			}
		}
		if (n.size() > 1) {
			mergeNodes(n.iterator().next(), n);
			merged = true;
		}
		if (merged) {
			MainWindow mw = MainWindow.getInstance();
			mw.updateElementTree();
			mw.updateElementProperties();
			graph.updateGraph();
			graph.getVisualizationViewer().repaint();
		}
	}

	private void mergeNodes(BiologicalNodeAbstract first, Set<BiologicalNodeAbstract> nodes) {
		for (BiologicalNodeAbstract bna : nodes) {
			if (bna != first && bna.getParentNode() == first.getParentNode()) {
				Set<BiologicalEdgeAbstract> edges = new HashSet<>(bna.getConnectingEdges());
				for (BiologicalEdgeAbstract bea : edges) {
					if (bea.getFrom() == bna || bea.getTo() == bna) {
						removeElement(bea);
						bea.getFrom().removeConnectingEdge(bea);
						bea.getTo().removeConnectingEdge(bea);
						if (bea.getTo() == bna) {
							bea.setTo(first);
						} else if (bea.getFrom() == bna) {
							bea.setFrom(first);
						}
						addEdge(bea);
						updateMyGraph();
					}

				}
				first.addLabel(bna.getLabelSet());
				if (bna.isLogical() && bna.getLogicalReference() == first) {
					first.getRefs().remove(bna);
				}
				for (BiologicalNodeAbstract v : this.vertices.keySet()) {
					v.updateHierarchicalAttributes();
				}
				// bna.updateHierarchicalAttributes();
				// for (BiologicalEdgeAbstract b : bna.getConnectingEdges()) {
				// b.getFrom().updateHierarchicalAttributes();
				// b.getTo().updateHierarchicalAttributes();
				// }
				removeElement(bna);
			}
		}
		updateMyGraph();
		graph.getVisualizationViewer().getPickedVertexState().clear();
		graph.getVisualizationViewer().getPickedEdgeState().clear();
		graph.getVisualizationViewer().getPickedVertexState().pick(first, true);
	}

	public void splitNode(Set<BiologicalNodeAbstract> nodes) {
		for (BiologicalNodeAbstract bna : new HashSet<>(nodes)) {
			if (graph.getJungGraph().getNeighborCount(bna) > 1) {
				// edges = this.graph.getJungGraph().getInEdges(bna);
				Set<BiologicalEdgeAbstract> edges = new HashSet<>(bna.getConnectingEdges());
				Iterator<BiologicalEdgeAbstract> itEdges = edges.iterator();
				int count = 1;
				while (itEdges.hasNext() && graph.getJungGraph().getNeighborCount(bna) > 1) {
					BiologicalEdgeAbstract bea = itEdges.next();
					// bea.getFrom().removeConnectingEdge(bea);
					// bea.getTo().removeConnectingEdge(bea);
					removeElement(bea);
					updateMyGraph();
					// newBNA = bna.clone();
					// newBNA = new Other(bna.getName(), bna.getLabel());
					BiologicalNodeAbstract newBNA = BiologicalNodeAbstractFactory.create(bna.getBiologicalElement(),
							bna);
					newBNA.setID(this);
					newBNA.setRefs(new HashSet<>());
					newBNA.setLogicalReference(bna);
					while (getAllNodeNames().contains(bna.getName() + "_" + count)) {
						count++;
					}
					newBNA.setName(bna.getName() + "_" + count);
					newBNA.setLabel(newBNA.getName());
					Point2D p = new Point2D.Double(0, 0);
					if (bea.getTo() == bna) {
						bea.setTo(newBNA);
						p = getGraph().getVertexLocation(bea.getFrom());
					} else if (bea.getFrom() == bna) {
						bea.setFrom(newBNA);
						p = getGraph().getVertexLocation(bea.getTo());
					}
					addVertex(newBNA, new Point2D.Double(p.getX() + 20, p.getY() + 20));
					addEdge(bea);
					graph.getVisualizationViewer().getPickedVertexState().pick(newBNA, true);
					updateMyGraph();
				}
				updateMyGraph();
			}
		}
		MainWindow mw = MainWindow.getInstance();
		mw.updateElementTree();
		mw.updateElementProperties();
		graph.getVisualizationViewer().repaint();
	}

	@Override
	public Pathway clone() {
		try {
			return (Pathway) super.clone();
		} catch (CloneNotSupportedException e) {
			// Should not happen as it's Cloneable
			throw new InternalError();
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		// if PW is BNA
		this.name = FormulaSafety.replace(name.trim());
		if (tab != null) {
			this.name = name.trim();
			tab.setTitle(this.name);
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPetriNet() {
		return isPetriNet;
	}

	public void setIsPetriNet(boolean isPetriNet) {
		this.isPetriNet = isPetriNet;
		if (isPetriNet) {
			petriNetProperties = new PetriNetProperties();
		}
	}

	public Pathway getParent() {
		return parent;
	}

	public void setParent(Pathway parent) {
		this.parent = parent;
	}

	public Set<BiologicalNodeAbstract> getClosedSubPathways() {
		return closedSubPathways;
	}

	public HashMap<Parameter, GraphElementAbstract> getChangedParameters() {
		return changedParameters;
	}

	public void setChangedParameters(HashMap<Parameter, GraphElementAbstract> changedParameters) {
		this.changedParameters = changedParameters;
	}

	public HashMap<Place, Double> getChangedInitialValues() {
		return changedInitialValues;
	}

	public void setChangedInitialValues(HashMap<Place, Double> changedInitialValues) {
		this.changedInitialValues = changedInitialValues;
	}

	public HashMap<Place, Boundary> getChangedBoundaries() {
		return changedBoundaries;
	}

	public void setChangedBoundaries(HashMap<Place, Boundary> changedBoundaries) {
		this.changedBoundaries = changedBoundaries;
	}

	public BiologicalNodeAbstract getRootNode() {
		return rootNode;
	}

	public void setRootNode(BiologicalNodeAbstract rootNode) {
		this.rootNode = rootNode;
	}

	public HashMap<BiologicalNodeAbstract, Point2D> getVertices() {
		return vertices;
	}

	public boolean isHeadless() {
		return headless;
	}

	public ArrayList<Group> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<Group> groups) {
		this.groups = groups;
	}

	public TransformationInformation getTransformationInformation() {
		return transformationInformation;
	}

	public void setTransformationInformation(TransformationInformation transformationInformation) {
		this.transformationInformation = transformationInformation;
	}

	public CompartmentManager getCompartmentManager() {
		return compartmentManager;
	}

	public PetriNetProperties getPetriPropertiesNet() {
		if (isPetriNet) {
			return petriNetProperties;
		} else if (transformationInformation != null && transformationInformation.getPetriNet() != null) {
			return transformationInformation.getPetriNet().getPetriPropertiesNet();
		}
		return null;
	}

	public MyGraph getGraph() {
		return getGraph(true);
	}

	public MyGraph getGraph(boolean createIfNull) {
		if (graph == null && createIfNull) {
			graph = new MyGraph(this);
			// tab = new GraphTab(name, graph.getGraphVisualization());
			// tab.setTitle(name);
		}
		return graph;
	}

	public void setGraph(MyGraph graph) {
		this.graph = graph;
	}

	public GraphTab getTab() {
		if (tab == null) {
			tab = new GraphTab(name, getGraph().getGraphVisualization());
			tab.setTitle(name);
		}
		return tab;
	}

	public String getOrganismSpecificationAsString() {
		return String.valueOf(orgSpecification);
	}

	public void setOrganismSpecification(String orgSpecification) {
		this.orgSpecification = orgSpecification.equalsIgnoreCase("true");
	}

	public Pathway getRootPathway() {
		if (getParent() == null)
			return this;
		else
			return parent.getRootPathway();
	}

	public ArrayList<Pathway> getChildren() {
		ArrayList<Pathway> result = new ArrayList<>();
		for (BiologicalNodeAbstract bna : getAllGraphNodes()) {
			if (bna instanceof PathwayMap && ((PathwayMap) bna).getPathwayLink() != null)
				result.add(((PathwayMap) bna).getPathwayLink());
		}
		return result;
	}

	public SortedSet<Integer> getIdSet() {
		if (getRootPathway() == this) {
			return this.ids;
		}
		return getRootPathway().getIdSet();
	}

	/**
	 * Updates the current Graph after coarsing of nodes. The node that calls the
	 * method is added, all nodes contained in this node are removed. Edges are
	 * updated respectively (changed from/to for border-environment edges, removed
	 * automatically for all 'inner' edges)
	 *
	 * @author tloka
	 */
	public void updateMyGraph() {
		// Clear myGraph
		getGraph().removeAllElements();
		// Set of all edges of the finest abstraction level
		Set<BiologicalEdgeAbstract> flattenedEdges = new HashSet<>();
		// HashMap for abstracted nodes
		HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> abstractedNodes = new HashMap<>();
		for (BiologicalNodeAbstract n : vertices.keySet()) {
			abstractedNodes.put(n, n);
		}
		// Add all flattened edges to the set of flattened edges
		for (BiologicalNodeAbstract n : abstractedNodes.keySet()) {
			flattenedEdges.addAll(n.getConnectingEdges());
		}
		// Abstract nodes
		Set<BiologicalNodeAbstract> csp = new HashSet<>(closedSubPathways);
		for (BiologicalNodeAbstract n : csp) {
			// If coarse node was flattened, remove it from the list of closed nodes
			if (n.isDeleted()) {
				closedSubPathways.remove(n);
				continue;
			}
			// If parent node is also closed, do nothing
			if (csp.contains(n.getParentNode())) {
				continue;
			}
			for (BiologicalNodeAbstract child : n.getVertices().keySet()) {
				abstractedNodes.put(child, n);
			}
		}
		// Add visible nodes to view
		for (BiologicalNodeAbstract n : abstractedNodes.values()) {
			addVertexToView(n, getVertexPosition(n));
		}
		// Abstract edges and add them to view (includes drawing environment
		// nodes implicitly)
		while (!flattenedEdges.isEmpty()) {
			BiologicalEdgeAbstract next = flattenedEdges.iterator().next();
			BiologicalEdgeAbstract clone = next.clone();
			BiologicalNodeAbstract newFrom = abstractedNodes.get(clone.getFrom());
			BiologicalNodeAbstract newTo = abstractedNodes.get(clone.getTo());
			clone.setFrom(newFrom == null ? clone.getFrom() : newFrom);
			clone.setTo(newTo == null ? clone.getTo() : newTo);
			if (clone.isValid(false)) {
				if (clone.getTo() == next.getTo() && clone.getFrom() == next.getFrom())
					addEdgeToView(next, true);
				else
					addEdgeToView(clone, true);
				flattenedEdges.removeIf(e -> abstractedNodes.get(e.getFrom()) == clone.getFrom()
						&& abstractedNodes.get(e.getTo()) == clone.getTo());
			}
			flattenedEdges.remove(next);
		}
	}

	public void saveVertexLocations() {
		Set<BiologicalNodeAbstract> nodes = new HashSet<>(vertices.keySet());
		for (BiologicalNodeAbstract n : nodes) {
			if (getGraph().getAllVertices().contains(n)) {
				vertices.put(n, getGraph().getVertexLocation(n));
			}
		}
	}

	private Point2D getVertexPosition(BiologicalNodeAbstract n) {
		if (vertices.containsKey(n)) {
			return vertices.get(n);
		}
		if (n.isCoarseNode() || n.getVertices().size() > 0) {
			if (n.getRootNode() != null) {
				return getVertexPosition(n.getRootNode());
			} else {
				Set<Point2D> childrenCoordinates = new HashSet<>();
				for (BiologicalNodeAbstract child : n.getVertices().keySet()) {
					childrenCoordinates.add(n.getVertices().get(child));
				}
				return Circle.averagePoint(childrenCoordinates);
			}
		}
		return new Point2D.Double(0.0, 0.0);
	}

	public boolean isRootPathway() {
		return getRootPathway() == this;
	}

	public boolean isBNA() {
		return this instanceof BiologicalNodeAbstract;
	}

	public boolean hasGraph() {
		return getGraph(false) != null;
	}

	/**
	 * Opens a coarse node in the Pathway without flattening it in data structure.
	 *
	 * @param subPathway Node to be opened
	 * @return false, if opening action is not possible. true, if node was opened.
	 * @author tloka
	 */
	public boolean openSubPathway(BiologicalNodeAbstract subPathway) {
		if (!subPathway.isCoarseNode() || subPathway.getChildrenNodes().size() == 0) {
			return false;
		}
		if ((this instanceof BiologicalNodeAbstract
				&& ((BiologicalNodeAbstract) this).getEnvironment().contains(subPathway))) {
			return false;
		}
		closedSubPathways.remove(subPathway);
		return true;
	}

	/**
	 * Opens all coarse nodes (including sub-coarsenodes) in the Pathway without
	 * flattening it in data structure.
	 *
	 * @author tloka
	 */
	public void openAllSubPathways() {
		closedSubPathways.clear();
	}

	/**
	 * Closes a coarse node in the Pathway.
	 *
	 * @param subPathway The node to be closed.
	 * @author tloka
	 */
	public void closeSubPathway(BiologicalNodeAbstract subPathway) {
		closedSubPathways.add(subPathway);
	}

	/**
	 * Closes all coarse nodes in the Pathway.
	 *
	 * @author tloka
	 */
	public void closeAllSubPathways() {
		for (BiologicalNodeAbstract v : getVertices().keySet()) {
			closedSubPathways.addAll(v.getAllParentNodes());
		}
	}

	public void handleChangeFlags(int flag) {
		for (ChangedFlags cf : changedFlags.values()) {
			switch (flag) {
			case ChangedFlags.EDGE_CHANGED:
				cf.setEdgeChanged(true);
				break;
			case ChangedFlags.NODE_CHANGED:
				cf.setNodeChanged(true);
				break;
			case ChangedFlags.PARAMETER_CHANGED:
				cf.setParameterChanged(true);
				break;
			case ChangedFlags.INITIALVALUE_CHANGED:
				cf.setInitialValueChanged(true);
				break;
			case ChangedFlags.EDGEWEIGHT_CHANGED:
				cf.setEdgeWeightChanged(true);
				break;
			case ChangedFlags.PNPROPERTIES_CHANGED:
				cf.setPnPropertiesChanged(true);
				break;
			case ChangedFlags.BOUNDARIES_CHANGED:
				cf.setBoundariesChanged(true);
				break;
			}
		}
	}

	public ChangedFlags getChangedFlags(String key) {
		if (!changedFlags.containsKey(key)) {
			changedFlags.put(key, new ChangedFlags());
		}
		return changedFlags.get(key);
	}

	public void stretchGraph(double factor) {
		Point2D p;
		for (BiologicalNodeAbstract bna : getAllGraphNodes()) {
			if (!vertices.containsKey(bna)) {
				for (BiologicalNodeAbstract child : bna.getVertices().keySet()) {
					p = vertices.get(child);
					p.setLocation(p.getX() * factor, p.getY() * factor);
				}
			} else {
				p = vertices.get(bna);
				p.setLocation(p.getX() * factor, p.getY() * factor);
			}
		}
		graph.getVisualizationViewer().repaint();
	}

	public void removeSelection() {
		removeSelectedEdges();
		removeSelectedVertices();
		getGraph().getVisualizationViewer().getPickedEdgeState().clear();
		getGraph().getVisualizationViewer().getPickedVertexState().clear();
		getGraph().updateGraph();
		updateMyGraph();
		if (!isHeadless()) {
			MainWindow mw = MainWindow.getInstance();
			mw.updateAllGuiElements();
		}
	}

	// moves selected vertices in pixel by given offset
	public void moveSelection(double x, double y) {
		for (BiologicalNodeAbstract bna : getGraph().getVisualizationViewer().getPickedVertexState().getPicked()) {
			Point2D pGraph = getVertexPosition(bna);
			Point2D pScreen = getGraph().getVisualizationViewer().getRenderContext().getMultiLayerTransformer()
					.transform(pGraph);
			pScreen.setLocation(pScreen.getX() + x, pScreen.getY() + y);
			Point2D pGraphNew = getGraph().getVisualizationViewer().getRenderContext().getMultiLayerTransformer()
					.inverseTransform(pScreen);
			getGraph().getVisualizationViewer().getModel().getGraphLayout().setLocation(bna, pGraphNew);
		}
		saveVertexLocations();
	}

	/**
	 * Add all selected Nodes to one Group
	 */
	public void groupSelectedNodes() {
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = getGraph().getVisualizationViewer();
		Set<BiologicalNodeAbstract> nodes = vv.getPickedVertexState().getPicked();
		if (nodes.size() > 1) {
			Group group = new Group(nodes);
			groups.add(group);
			for (BiologicalNodeAbstract nextNode : vv.getPickedVertexState().getPicked()) {
				nextNode.setInGroup(true);
				nextNode.addGroup(group);
			}
		} else {
			PopUpDialog.getInstance().show("Grouping Error", "This cannot be grouped.");
		}
	}

	/**
	 * If one node is selected, the group members will be picked too.
	 */
	public void pickGroup() {
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = getGraph().getVisualizationViewer();
		if (vv.getPickedVertexState().getPicked().size() != 0) {
			Iterator<BiologicalNodeAbstract> it = vv.getPickedVertexState().getPicked().iterator();
			BiologicalNodeAbstract nextNode = it.next();
			if (nextNode.isInGroup()) {
				for (BiologicalNodeAbstract node : nextNode.getbiggestGroup()) {
					graph.getVisualizationViewer().getPickedVertexState().pick(node, true);
				}
			}
		}
	}

	/**
	 * Delete selected group.
	 */
	public void deleteGroup() {
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = getGraph().getVisualizationViewer();
		Iterator<BiologicalNodeAbstract> iter = vv.getPickedVertexState().getPicked().iterator();
		boolean deletegroup = true;
		BiologicalNodeAbstract firstNode = iter.next();
		Group groupToDelete = firstNode.getbiggestGroup();
		// Checks if all selected nodes are of the same group
		for (BiologicalNodeAbstract bnaNode : vv.getPickedVertexState().getPicked()) {
			if (!bnaNode.getGroups().contains(groupToDelete)) {
				deletegroup = false;
				PopUpDialog.getInstance().show("Deletion error", "This cannot be deleted.");
			}
		}
		// if all selected are from same group, delete group
		Iterator<BiologicalNodeAbstract> it = vv.getPickedVertexState().getPicked().iterator();
		while (it.hasNext() && deletegroup) {
			BiologicalNodeAbstract nextNode = it.next();
			if (nextNode.getGroups().size() == 1) {
				nextNode.setInGroup(false);
			}
			nextNode.getGroups().remove(groupToDelete);
		}
		if (groupToDelete != null && deletegroup) {
			groups.remove(groupToDelete);
		}
		graph.getVisualizationViewer().getPickedVertexState().clear();
	}

	private void removeSelectedEdges() {
		if (getGraph().getAllEdges().size() > 0) {
			Iterator<BiologicalEdgeAbstract> it = getGraph().getVisualizationViewer().getPickedEdgeState().getPicked()
					.iterator();
			// Iterator it = vv.getPickedState().getPickedEdges().iterator();
			while (it.hasNext()) {
				removeElement(it.next());
			}
		}
	}

	private void removeSelectedVertices() {
		if (getGraph().getAllVertices().size() > 0) {
			MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = getGraph()
					.getVisualizationViewer();
			Iterator<BiologicalNodeAbstract> it = vv.getPickedVertexState().getPicked().iterator();
			BiologicalNodeAbstract bna;
			int savedAnswer = -1;
			while (it.hasNext()) {
				bna = it.next();
				if (this instanceof BiologicalNodeAbstract) {
					BiologicalNodeAbstract pwNode = (BiologicalNodeAbstract) this;
					if (pwNode.getEnvironment().contains(bna)) {
						if (pwNode.getGraph().getJungGraph().getNeighborCount(bna) != 0) {
							JOptionPane.showMessageDialog(null, "Can't delete connected environment nodes.",
									"Deletion Error", JOptionPane.ERROR_MESSAGE);
						} else {
							Object[] options = { "Yes", "No" };
							int answer = JOptionPane.showOptionDialog(vv,
									"Do you want to delete the predefined environment node " + bna.getLabel() + "?",
									"Delete predefined environment node", JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
							if (answer != JOptionPane.NO_OPTION) {
								removeElement(bna);
								getRootPathway().updateMyGraph();
							}
						}
						continue;
					}
				}
				if (bna.isCoarseNode() && savedAnswer == JOptionPane.NO_OPTION) {
					continue;
				}
				if (bna.isCoarseNode() && savedAnswer != JOptionPane.YES_OPTION) {
					CoarseNodeDeleteDialog dialog = new CoarseNodeDeleteDialog(bna);
					Integer[] del = dialog.getAnswer();
					if (del[0] == JOptionPane.NO_OPTION) {
						if (del[1] == 1) {
							savedAnswer = JOptionPane.NO_OPTION;
						}
						continue;
					} else if (del[0] == JOptionPane.YES_OPTION) {
						if (del[1] == 1) {
							savedAnswer = JOptionPane.YES_OPTION;
						}
					}
				}

				Set<BiologicalEdgeAbstract> conEdges = new HashSet<>(bna.getConnectingEdges());
				for (BiologicalEdgeAbstract bea : conEdges) {
					bea.getFrom().removeConnectingEdge(bea);
					bea.getTo().removeConnectingEdge(bea);
				}
				// delete refs
				if (bna.isLogical()) {
					bna.getLogicalReference().getRefs().remove(bna);
				}
				// delete incident edges
				for (BiologicalEdgeAbstract bea : getGraph().getJungGraph().getIncidentEdges(bna)) {
					removeElement(bea);
					// System.out.println("edge deleted");
				}
				removeElement(bna);
			}
		}
	}

	public VisualizationImageServer<BiologicalNodeAbstract, BiologicalEdgeAbstract> prepareGraphToPrint() {
		MyGraph mg = getGraph();
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> v = getGraph().getVisualizationViewer();
		if (!(mg.getLayout() instanceof StaticLayout)) {
			mg.changeToStaticLayout();
		}
		VisualizationImageServer<BiologicalNodeAbstract, BiologicalEdgeAbstract> wvv = new VisualizationImageServer<>(
				mg.getLayout(), mg.getLayout().getSize());
		wvv.setBackground(Color.white);
		for (Paintable renderer : v.getPreRenderers()) {
			wvv.addPreRenderPaintable(renderer);
		}
		for (Paintable renderer : v.getPostRenderers()) {
			wvv.addPostRenderPaintable(renderer);
		}
		wvv.setBackground(Color.white);
		wvv.setRenderContext(v.getRenderContext());
		/*
		 * double scaleV =
		 * wvv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).
		 * getScale(); double scaleL =
		 * wvv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT
		 * ).getScale(); double scale; if (scaleV < 1) { scale = scaleV; } else { scale
		 * = scaleL; } GraphCenter gc = new GraphCenter(mg); double width =
		 * gc.getWidth(); double height = gc.getHeight();
		 * 
		 * if (width > height) { width = width * scale; } else { height = height *
		 * scale; }
		 */

		// Point2D p1inv = v.getRenderContext().getMultiLayerTransformer().transform(new
		// Point2D.Double(gc.getMinX() + gc.getWidth(), gc.getMinY() + gc.getHeight()));

		// wvv.setBounds(0, 0, (int) p1inv.getX() + 150, (int) p1inv.getY() + 50);
		wvv.setBounds(v.getVisibleRect());

		Map<Key, Object> map = wvv.getRenderingHints();

		map.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		map.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		map.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		map.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		map.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		map.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		return wvv;
	}

	public PetriNetSimulation getPetriNetSimulation() {
		if (isPetriNet) {
			if (petriNetSimulation == null) {
				petriNetSimulation = new PetriNetSimulation(this);
			}
			return petriNetSimulation;
		} else {
			return transformationInformation.getPetriNet().getPetriNetSimulation();
		}
	}

	public int getPlaceCount() {
		int count = 0;
		for (BiologicalNodeAbstract bna : getAllGraphNodes()) {
			if (bna instanceof Place && !bna.isLogical()) {
				count++;
			}
		}
		return count;
	}

	public int getTransitionCount() {
		int count = 0;
		for (BiologicalNodeAbstract bna : getAllGraphNodes()) {
			if (bna instanceof Transition && !bna.isLogical()) {
				count++;
			}
		}
		return count;
	}

	public int getIncomingDirectedEdgeCount(BiologicalNodeAbstract bna) {
		int count = 0;
		Collection<BiologicalEdgeAbstract> edges = getGraph().getJungGraph().getIncidentEdges(bna);
		for (BiologicalEdgeAbstract e : edges) {
			if (bna == e.getTo() && e.isDirected()) {
				count++;
			}
		}
		return count;
	}

	public int getOutgoingDirectedEdgeCount(BiologicalNodeAbstract bna) {
		int count = 0;
		Collection<BiologicalEdgeAbstract> edges = getGraph().getJungGraph().getIncidentEdges(bna);
		for (BiologicalEdgeAbstract e : edges) {
			if (bna == e.getFrom() && e.isDirected()) {
				count++;
			}
		}
		return count;
	}

	public int getUndirectedEdgeCount(BiologicalNodeAbstract bna) {
		int count = 0;
		Collection<BiologicalEdgeAbstract> edges = getGraph().getJungGraph().getIncidentEdges(bna);
		for (BiologicalEdgeAbstract e : edges) {
			if (!e.isDirected()) {
				count++;
			}
		}
		return count;
	}

	public void setPlotColorPlacesTransitions(boolean override) {
		int i = 0;
		int j = 0;
		for (final BiologicalNodeAbstract bna : getAllGraphNodesSortedAlphabetically()) {
			if (bna instanceof Place && !bna.isLogical()) {
				Place p = (Place) bna;
				Color c = Color.getHSBColor(i * 1f / getPlaceCount(), 1, 1);
				if (override) {
					p.setPlotColor(c);
				} else if (p.getPlotColor() == null) {
					p.setPlotColor(c);
				}
				i++;
			} else if (bna instanceof Transition && !bna.isLogical()) {
				Transition t = (Transition) bna;
				Color c = Color.getHSBColor(j * 1f / getTransitionCount(), 1, 1);
				if (override) {
					t.setPlotColor(c);
				} else if (t.getPlotColor() == null) {
					t.setPlotColor(c);
				}
				j++;
			}
		}
	}

	public void adjustDown(Collection<BiologicalNodeAbstract> nodes) {
		double maxy = Double.MIN_VALUE;
		Point2D point;
		if (nodes.size() > 1) {
			for (BiologicalNodeAbstract bna : nodes) {
				point = getGraph().getVertexLocation(bna);
				if (point.getY() > maxy) {
					maxy = point.getY();
				}
			}
			for (BiologicalNodeAbstract bna : nodes) {
				point = getGraph().getVertexLocation(bna);
				point.setLocation(point.getX(), maxy);
				getGraph().getVisualizationViewer().getModel().getGraphLayout().setLocation(bna, point);
			}
		}
		saveVertexLocations();
	}

	public void adjustLeft(Collection<BiologicalNodeAbstract> nodes) {
		double minx = Double.MAX_VALUE;
		Point2D point;
		if (nodes.size() > 1) {
			for (BiologicalNodeAbstract bna : nodes) {
				point = getGraph().getVertexLocation(bna);
				if (point.getX() < minx) {
					minx = point.getX();
				}
			}
			for (BiologicalNodeAbstract bna : nodes) {
				point = getGraph().getVertexLocation(bna);
				point.setLocation(minx, point.getY());
				getGraph().getVisualizationViewer().getModel().getGraphLayout().setLocation(bna, point);
			}
		}
		saveVertexLocations();
	}

	public void adjustHorizontalSpace(Collection<BiologicalNodeAbstract> nodes) {
		double minx = Double.MAX_VALUE;
		double maxx = Double.MIN_VALUE;
		HashMap<BiologicalNodeAbstract, Double> map = new HashMap<>();
		Point2D point;
		if (nodes.size() > 2) {
			for (BiologicalNodeAbstract bna : nodes) {
				point = getGraph().getVertexLocation(bna);
				if (point.getX() < minx) {
					minx = point.getX();
				}
				if (point.getX() > maxx) {
					maxx = point.getX();
				}
				map.put(bna, point.getX());
			}
			List<Double> c = new ArrayList<>(map.values());
			Collections.sort(c);
			for (BiologicalNodeAbstract bna : nodes) {
				int d = c.indexOf(map.get(bna));
				double newx;
				if (d == 0) {
					newx = minx;
				} else if (d == nodes.size() - 1) {
					newx = maxx;
				} else {
					newx = minx + d * ((Math.abs(maxx - minx)) / (nodes.size() - 1));
				}
				point = getGraph().getVertexLocation(bna);
				point.setLocation(newx, point.getY());
				getGraph().getVisualizationViewer().getModel().getGraphLayout().setLocation(bna, point);
			}
			saveVertexLocations();
		}
	}

	public void adjustVerticalSpace(Collection<BiologicalNodeAbstract> nodes) {
		double miny = Double.MAX_VALUE;
		double maxy = Double.MIN_VALUE;
		HashMap<BiologicalNodeAbstract, Double> map = new HashMap<>();
		Point2D point;
		if (nodes.size() > 2) {
			for (BiologicalNodeAbstract bna : nodes) {
				point = getGraph().getVertexLocation(bna);
				if (point.getY() < miny) {
					miny = point.getY();
				}
				if (point.getY() > maxy) {
					maxy = point.getY();
				}
				map.put(bna, point.getY());
			}
			List<Double> c = new ArrayList<>(map.values());
			Collections.sort(c);
			for (BiologicalNodeAbstract bna : nodes) {
				int d = c.indexOf(map.get(bna));
				double newy;
				if (d == 0) {
					newy = miny;
				} else if (d == nodes.size() - 1) {
					newy = maxy;
				} else {
					newy = miny + d * ((Math.abs(maxy - miny)) / (nodes.size() - 1));
				}
				point = getGraph().getVertexLocation(bna);
				point.setLocation(point.getX(), newy);
				getGraph().getVisualizationViewer().getModel().getGraphLayout().setLocation(bna, point);
			}
			saveVertexLocations();
		}
	}
}