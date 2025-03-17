package biologicalElements;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.BiologicalEdgeAbstractFactory;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstractFactory;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import graph.ChangedFlags;
import graph.VanesaGraph;
import graph.compartment.CompartmentManager;
import graph.groups.Group;
import graph.gui.Boundary;
import graph.gui.CoarseNodeDeleteDialog;
import graph.gui.EdgeDeleteDialog;
import graph.gui.Parameter;
import graph.jung.classes.MyGraph;
import graph.jung.classes.MyVisualizationViewer;
import graph.layouts.Circle;
import graph.rendering.VanesaGraphRendererPanel;
import gui.GraphTab;
import gui.MainWindow;
import gui.PopUpDialog;
import petriNet.PetriNetProperties;
import petriNet.PetriNetSimulation;
import transformation.TransformationInformation;
import util.FormulaSafety;
import util.VanesaUtility;

public class Pathway implements Cloneable {
	private File file = null;
	private String name;
	private String version = "";
	private String date = "";
	private String title;
	private String author = "";
	private String link = "";
	private String organism = "";
	private boolean orgSpecification;
	private String description = "";

	private PathwayType type = PathwayType.BiologicalNetwork;
	private PetriNetProperties petriNetProperties = null;
	private PetriNetSimulation petriNetSimulation = null;

	private final VanesaGraph graph2 = new VanesaGraph();
	private VanesaGraphRendererPanel rendererPanel;
	private GraphTab tab;
	private Pathway parent;
	private final SortedSet<Integer> ids = new TreeSet<>();
	private final Set<BiologicalNodeAbstract> closedSubPathways = new HashSet<>();

	private final Map<String, ChangedFlags> changedFlags = new HashMap<>();
	private Map<Parameter, GraphElementAbstract> changedParameters = new HashMap<>();
	private Map<Place, Double> changedInitialValues = new HashMap<>();
	private Map<Place, Boundary> changedBoundaries = new HashMap<>();
	private BiologicalNodeAbstract rootNode;

	// no graph tab is assigned / created. this is used for rule editing window
	private final boolean headless;
	private List<Group> groups = new ArrayList<>();
	private TransformationInformation transformationInformation = null;
	private final CompartmentManager compartmentManager = new CompartmentManager();

	public Pathway(final String name) {
		this(name, false, PathwayType.BiologicalNetwork);
		rendererPanel = new VanesaGraphRendererPanel(graph2);
		tab = new GraphTab(this.name, rendererPanel);
		tab.setIcon(type);
	}

	public Pathway(final String name, final PathwayType type) {
		this(name, false, type);
		rendererPanel = new VanesaGraphRendererPanel(graph2);
		tab = new GraphTab(this.name, rendererPanel);
		tab.setIcon(type);
	}

	public Pathway(final String name, final boolean headless, final PathwayType type) {
		this.name = name.trim();
		this.title = this.name;
		this.headless = headless;
		this.type = type;
		if (type == PathwayType.PetriNet) {
			petriNetProperties = new PetriNetProperties(this);
		}
	}

	/**
	 * Constructor for BNA
	 */
	protected Pathway(final String name, final Pathway parent) {
		headless = false;
		this.name = name.trim();
		this.title = this.name;
		this.parent = parent;
		type = parent != null ? parent.getType() : PathwayType.BiologicalNetwork;
	}

	protected void cleanVertices() {
		// TODO
	}

	public Set<BiologicalEdgeAbstract> getEdges() {
		Set<BiologicalEdgeAbstract> edges = new HashSet<>();
		for (BiologicalNodeAbstract n : getVertices().keySet()) {
			edges.addAll(n.getConnectingEdges());
		}
		return edges;
	}

	public void changeBackground(final String color) {
		if (color.equals("black")) {
			rendererPanel.setBackground(Color.BLACK);
		} else if (color.equals("white")) {
			rendererPanel.setBackground(Color.WHITE);
		}
	}

	public BiologicalNodeAbstract addVertex(final String name, final String label, final String type,
			final String compartment, final Point2D p) {
		final BiologicalNodeAbstract bna = BiologicalNodeAbstractFactory.create(type, null, label, name);
		getCompartmentManager().setCompartment(bna, this.getCompartmentManager().getCompartment(compartment));
		if (isBNA()) {
			final BiologicalNodeAbstract thisBna = (BiologicalNodeAbstract) this;
			final Pathway parent = thisBna.getParentNode() == null ? getRootPathway() : thisBna.getParentNode();
			bna.setParentNodeDistance(Circle.get2Ddistance(parent.graph2.getNodePosition(thisBna), p));
		}
		return addVertex(bna, p);
	}

	public BiologicalNodeAbstract addVertex(final BiologicalNodeAbstract bna, final Point2D p) {
		if (graph2.contains(bna)) {
			return bna;
		}
		bna.setLabel(bna.getLabel());
		bna.setName(bna.getName());
		if (type == PathwayType.PetriNet) {
			boolean createdRef = false;
			for (BiologicalNodeAbstract node : graph2.getNodes()) {
				if (node.getName().equals(bna.getName())) {
					if (node.getClass().equals(bna.getClass())) {
						bna.setLogicalReference(node);
						createdRef = true;
					} else {
						PopUpDialog.getInstance().show("Type mismatch",
								"Cannot create logical node with the name: " + bna.getName() + ". Type mismatch of "
										+ bna.getClass() + " and " + node.getClass() + "!");
						System.err.println(
								"Cannot create logical node with the name: " + bna.getName() + ". Type mismatch of "
										+ bna.getClass() + " and " + node.getClass() + "!");
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
		graph2.add(bna);
		graph2.setNodePosition(bna, p);
		bna.setID(this);
		handleChangeFlags(ChangedFlags.NODE_CHANGED);
		return bna;
	}

	public BiologicalEdgeAbstract addEdge(String label, String name, BiologicalNodeAbstract from,
			BiologicalNodeAbstract to, String element, boolean directed) {
		final BiologicalEdgeAbstract bea = BiologicalEdgeAbstractFactory.create(element, label, name, from, to);
		bea.setDirected(directed);
		if (element.equals(Elementdeclerations.pnArc) || element.equals(Elementdeclerations.pnInhibitorArc)) {
			((PNArc) bea).setProbability(1);
		}
		return addEdge(bea);
	}

	public BiologicalEdgeAbstract addEdge(final BiologicalEdgeAbstract bea) {
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

	/**
	 * Add an edge to the current view (MyGraph)
	 *
	 * @param bea   The edge
	 * @param force If true, missing nodes are added before to guarantee that the edge will be added
	 */
	public void addEdgeToView(BiologicalEdgeAbstract bea, boolean force) {
		if (graph2.contains(bea.getFrom()) && graph2.contains(bea.getTo())) {
			graph2.add(bea);
		} else if (force) {
			graph2.add(bea.getFrom());
			graph2.setNodePosition(bea.getFrom(), getRootPathway().getVertices().get(bea.getFrom()));
			graph2.add(bea.getTo());
			graph2.setNodePosition(bea.getTo(), getRootPathway().getVertices().get(bea.getTo()));
			graph2.add(bea);
		}
	}

	public void removeElement(final GraphElementAbstract element) {
		if (element == null) {
			return;
		}
		if (element.isVertex()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) element;
			getRootPathway().getVertices().remove(bna);
			for (BiologicalNodeAbstract parent : bna.getAllParentNodes()) {
				parent.getVertices().remove(bna);
			}
			bna.delete();
			handleChangeFlags(ChangedFlags.NODE_CHANGED);
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

	public int edgeGrade(final BiologicalEdgeAbstract bea) {
		final Set<BiologicalEdgeAbstract> conEdgesTo = new HashSet<>();
		for (final BiologicalEdgeAbstract edge : bea.getFrom().getConnectingEdges()) {
			if (edge.isDirected() == bea.isDirected()) {
				if (edge.isDirected()) {
					if (bea.getTo().getConnectingEdges().contains(edge) && edge.getFrom().getCurrentShownParentNode(
							graph2) == bea.getFrom()) {
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

	public BiologicalEdgeAbstract getEdge(final BiologicalNodeAbstract from, final BiologicalNodeAbstract to) {
		return graph2.findEdge(from, to);
	}

	public boolean contains(final BiologicalNodeAbstract bna) {
		return graph2.contains(bna);
	}

	public boolean contains(final BiologicalEdgeAbstract bea) {
		return graph2.contains(bea);
	}

	public boolean containsEdge(final BiologicalNodeAbstract from, final BiologicalNodeAbstract to) {
		return graph2.findEdge(from, to) != null;
	}

	public Set<String> getAllNodeLabels() {
		final Set<String> set = new HashSet<>();
		for (final BiologicalNodeAbstract biologicalNodeAbstract : graph2.getNodes()) {
			set.add(biologicalNodeAbstract.getLabel());
		}
		return set;
	}

	public Set<String> getAllNodeNames() {
		final Set<String> set = new HashSet<>();
		for (final BiologicalNodeAbstract biologicalNodeAbstract : graph2.getNodes()) {
			set.add(biologicalNodeAbstract.getName());
		}
		return set;
	}

	public Set<String> getAllEdgeNames() {
		final Set<String> set = new HashSet<>();
		for (final BiologicalEdgeAbstract bea : graph2.getEdges()) {
			set.add(bea.getName());
		}
		return set;
	}

	public boolean hasGotAtLeastOneElement() {
		return graph2.getNodeCount() > 0;
	}

	public BiologicalNodeAbstract getNodeByLabel(final String label) {
		for (final BiologicalNodeAbstract bna : graph2.getNodes()) {
			if (bna.getLabel().equals(label)) {
				return bna;
			}
		}
		return null;
	}

	public BiologicalNodeAbstract getNodeByName(final String name) {
		for (final BiologicalNodeAbstract bna : graph2.getNodes()) {
			if (bna.getName().equals(name)) {
				return bna;
			}
		}
		return null;
	}

	public Collection<BiologicalEdgeAbstract> getAllEdges() {
		return graph2.getEdges();
	}

	public List<BiologicalEdgeAbstract> getAllEdgesSortedByID() {
		return VanesaUtility.getEdgesSortedByID(graph2.getEdges());
	}

	public Collection<BiologicalNodeAbstract> getAllGraphNodes() {
		return graph2.getNodes();
	}

	public List<BiologicalNodeAbstract> getAllGraphNodesSorted() {
		return graph2.getNodes().stream().sorted(Comparator.comparingInt(BiologicalNodeAbstract::getID)).collect(
				Collectors.toList());
	}

	public List<BiologicalNodeAbstract> getAllGraphNodesSortedAlphabetically() {
		HashMap<String, ArrayList<BiologicalNodeAbstract>> map = new HashMap<>();
		for (BiologicalNodeAbstract bna : this.graph2.getNodes()) {
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

	public Collection<BiologicalNodeAbstract> getSelectedNodes() {
		return graph2.getSelectedNodes();
	}

	public Collection<BiologicalEdgeAbstract> getSelectedEdges() {
		return graph2.getSelectedEdges();
	}

	public int getNodeCount() {
		return graph2.getNodeCount();
	}

	public int getEdgeCount() {
		return graph2.getEdgeCount();
	}

	public void mergeNodes(Set<BiologicalNodeAbstract> nodes) {
		if (nodes.isEmpty()) {
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
			if (bna.getRefs().isEmpty()) {
				n.add(bna);
			} else {
				System.err.printf("Node with id: %d and name: %s cannot be merged due to unresolved references!",
						bna.getID(), bna.getName());
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
		}
	}

	private void mergeNodes(final BiologicalNodeAbstract first, final Set<BiologicalNodeAbstract> nodes) {
		for (final BiologicalNodeAbstract bna : nodes) {
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
				for (final BiologicalNodeAbstract v : graph2.getNodes()) {
					v.updateConnectingEdges();
				}
				removeElement(bna);
			}
		}
		updateMyGraph();
		graph2.clearSelection();
		graph2.selectNodes(true, first);
	}

	public void splitNode(final Set<BiologicalNodeAbstract> nodes) {
		for (final BiologicalNodeAbstract bna : new HashSet<>(nodes)) {
			if (graph2.getNeighborCount(bna) > 1) {
				final Set<BiologicalEdgeAbstract> edges = new HashSet<>(bna.getConnectingEdges());
				final Iterator<BiologicalEdgeAbstract> itEdges = edges.iterator();
				int count = 1;
				while (itEdges.hasNext() && graph2.getNeighborCount(bna) > 1) {
					final BiologicalEdgeAbstract bea = itEdges.next();
					removeElement(bea);
					updateMyGraph();
					final BiologicalNodeAbstract newBNA = BiologicalNodeAbstractFactory.create(
							bna.getBiologicalElement(), bna);
					newBNA.setID(this);
					newBNA.setRefs(new HashSet<>());
					newBNA.setLogicalReference(bna);
					while (getAllNodeNames().contains(bna.getName() + "_" + count)) {
						count++;
					}
					newBNA.setName(bna.getName() + "_" + count);
					newBNA.setLabel(newBNA.getName());
					final Point2D p;
					if (bea.getTo() == bna) {
						bea.setTo(newBNA);
						p = graph2.getNodePosition(bea.getFrom());
					} else if (bea.getFrom() == bna) {
						bea.setFrom(newBNA);
						p = graph2.getNodePosition(bea.getTo());
					} else {
						p = new Point2D.Double();
					}
					addVertex(newBNA, new Point2D.Double(p.getX() + 20, p.getY() + 20));
					addEdge(bea);
					graph2.selectNodes(true, newBNA);
					updateMyGraph();
				}
				updateMyGraph();
			}
		}
		MainWindow mw = MainWindow.getInstance();
		mw.updateElementTree();
		mw.updateElementProperties();
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

	public void setFile(final File file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
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

	public void setVersion(final String version) {
		this.version = version;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	public String getLink() {
		return link;
	}

	public void setLink(final String link) {
		this.link = link;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(final String organism) {
		this.organism = organism;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public PathwayType getType() {
		return type;
	}

	public boolean isPetriNet() {
		return type == PathwayType.PetriNet;
	}

	public void setIsPetriNet(final boolean isPetriNet) {
		this.type = isPetriNet ? PathwayType.PetriNet : PathwayType.BiologicalNetwork;
		if (isPetriNet) {
			petriNetProperties = new PetriNetProperties(this);
		}
		updateTabIcon();
	}

	private void updateTabIcon() {
		if (tab != null) {
			tab.setIcon(type);
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

	public Map<Parameter, GraphElementAbstract> getChangedParameters() {
		return changedParameters;
	}

	public void setChangedParameters(Map<Parameter, GraphElementAbstract> changedParameters) {
		this.changedParameters = changedParameters;
	}

	public Map<Place, Double> getChangedInitialValues() {
		return changedInitialValues;
	}

	public void setChangedInitialValues(Map<Place, Double> changedInitialValues) {
		this.changedInitialValues = changedInitialValues;
	}

	public Map<Place, Boundary> getChangedBoundaries() {
		return changedBoundaries;
	}

	public void setChangedBoundaries(Map<Place, Boundary> changedBoundaries) {
		this.changedBoundaries = changedBoundaries;
	}

	public BiologicalNodeAbstract getRootNode() {
		return rootNode;
	}

	public void setRootNode(BiologicalNodeAbstract rootNode) {
		this.rootNode = rootNode;
	}

	public Map<BiologicalNodeAbstract, Point2D> getVertices() {
		return new HashMap<>();
	}

	public boolean isHeadless() {
		return headless;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
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
		if (type == PathwayType.PetriNet) {
			return petriNetProperties;
		} else if (transformationInformation != null && transformationInformation.getPetriNet() != null) {
			return transformationInformation.getPetriNet().getPetriPropertiesNet();
		}
		return null;
	}

	public VanesaGraph getGraph2() {
		return graph2;
	}

	public VanesaGraphRendererPanel getGraphRenderer() {
		if (rendererPanel == null) {
			rendererPanel = new VanesaGraphRendererPanel(graph2);
		}
		return rendererPanel;
	}

	public MyGraph getGraph() {
		return null;
	}

	public void setGraph(final MyGraph graph) {
		// this.graph2 = graph;
	}

	public GraphTab getTab() {
		if (tab == null) {
			tab = new GraphTab(name, new VanesaGraphRendererPanel(graph2));
			tab.setTitle(name);
			updateTabIcon();
		}
		return tab;
	}

	public String getOrganismSpecificationAsString() {
		return String.valueOf(orgSpecification);
	}

	public void setOrganismSpecification(String orgSpecification) {
		this.orgSpecification = "true".equalsIgnoreCase(orgSpecification);
	}

	public Pathway getRootPathway() {
		if (getParent() == null)
			return this;
		return parent.getRootPathway();
	}

	public ArrayList<Pathway> getChildren() {
		ArrayList<Pathway> result = new ArrayList<>();
		for (BiologicalNodeAbstract bna : graph2.getNodes()) {
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
	 * Updates the current Graph after coarsing of nodes. The node that calls the method is added, all nodes contained
	 * in this node are removed. Edges are updated respectively (changed from/to for border-environment edges, removed
	 * automatically for all 'inner' edges)
	 */
	public void updateMyGraph() {
		/* TODO: revalidate
		graph2.clear();
		// Set of all edges of the finest abstraction level
		Set<BiologicalEdgeAbstract> flattenedEdges = new HashSet<>();
		// HashMap for abstracted nodes
		HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> abstractedNodes = new HashMap<>();
		for (BiologicalNodeAbstract n : graph2.getNodes()) {
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
			for (BiologicalNodeAbstract child : n.getGraph2().getNodes()) {
				abstractedNodes.put(child, n);
			}
		}
		// Add visible nodes to view
		for (BiologicalNodeAbstract n : abstractedNodes.values()) {
			graph2.add(n);
			graph2.setNodePosition(n, getNodePosition(n));
		}
		// Abstract edges and add them to view (includes drawing environment nodes implicitly)
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
		*/
	}

	private Point2D getNodePosition(final BiologicalNodeAbstract node) {
		if (graph2.contains(node)) {
			return graph2.getNodePosition(node);
		}
		if (node.isCoarseNode() || node.getNodeCount() > 0) {
			if (node.getRootNode() != null) {
				return getNodePosition(node.getRootNode());
			} else {
				final Set<Point2D> childrenCoordinates = new HashSet<>();
				for (BiologicalNodeAbstract child : node.getVertices().keySet()) {
					childrenCoordinates.add(node.getVertices().get(child));
				}
				return Circle.averagePoint(childrenCoordinates);
			}
		}
		return new Point2D.Double();
	}

	public boolean isRootPathway() {
		return getRootPathway() == this;
	}

	public boolean isBNA() {
		return this instanceof BiologicalNodeAbstract;
	}

	/**
	 * Opens a coarse node in the Pathway without flattening it in data structure.
	 *
	 * @param subPathway Node to be opened
	 * @return false, if opening action is not possible. true, if node was opened.
	 */
	public boolean openSubPathway(BiologicalNodeAbstract subPathway) {
		if (!subPathway.isCoarseNode() || subPathway.getChildrenNodes().isEmpty()) {
			return false;
		}
		if ((this instanceof BiologicalNodeAbstract && ((BiologicalNodeAbstract) this).getEnvironment().contains(
				subPathway))) {
			return false;
		}
		closedSubPathways.remove(subPathway);
		return true;
	}

	/**
	 * Opens all coarse nodes (including sub-coarsenodes) in the Pathway without flattening it in data structure.
	 */
	public void openAllSubPathways() {
		closedSubPathways.clear();
	}

	/**
	 * Closes a coarse node in the Pathway.
	 *
	 * @param subPathway The node to be closed.
	 */
	public void closeSubPathway(BiologicalNodeAbstract subPathway) {
		closedSubPathways.add(subPathway);
	}

	/**
	 * Closes all coarse nodes in the Pathway.
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
		for (final BiologicalNodeAbstract bna : graph2.getNodes()) {
			if (graph2.contains(bna)) {
				final Point2D p = graph2.getNodePosition(bna);
				p.setLocation(p.getX() * factor, p.getY() * factor);
			} else {
				for (BiologicalNodeAbstract child : bna.getVertices().keySet()) {
					final Point2D p = graph2.getNodePosition(child);
					p.setLocation(p.getX() * factor, p.getY() * factor);
				}
			}
		}
	}

	public void removeSelection() {
		removeSelectedEdges();
		removeSelectedVertices();
		graph2.clearSelection();
		updateMyGraph();
		if (!headless) {
			MainWindow.getInstance().updateAllGuiElements();
		}
	}

	/**
	 * Moves selected vertices in pixel by given offset.
	 */
	public void moveSelection(double x, double y) {
		graph2.translateSelectedNodes(x, y);
	}

	/**
	 * Add all selected Nodes to one Group
	 */
	public void groupSelectedNodes() {
		final Collection<BiologicalNodeAbstract> nodes = graph2.getSelectedNodes();
		if (nodes.size() > 1) {
			final Group group = new Group(nodes);
			groups.add(group);
			for (final BiologicalNodeAbstract nextNode : nodes) {
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
		final Collection<BiologicalNodeAbstract> nodes = new ArrayList<>(graph2.getSelectedNodes());
		if (!nodes.isEmpty()) {
			Iterator<BiologicalNodeAbstract> it = nodes.iterator();
			BiologicalNodeAbstract nextNode = it.next();
			if (nextNode.isInGroup()) {
				for (BiologicalNodeAbstract node : nextNode.getbiggestGroup()) {
					graph2.selectNodes(true, node);
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
		graph2.clearNodeSelection();
	}

	private void removeSelectedEdges() {
		if (graph2.getEdgeCount() > 0) {
			final var selectedEdges = new ArrayList<>(graph2.getSelectedEdges());
			for (final BiologicalEdgeAbstract edge : selectedEdges) {
				removeElement(edge);
			}
		}
	}

	private void removeSelectedVertices() {
		if (graph2.getNodeCount() <= 0) {
			return;
		}
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = getGraph().getVisualizationViewer();
		int savedAnswer = -1;
		Iterator<BiologicalNodeAbstract> it = vv.getPickedVertexState().getPicked().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = it.next();
			if (this instanceof BiologicalNodeAbstract) {
				BiologicalNodeAbstract pwNode = (BiologicalNodeAbstract) this;
				if (pwNode.getEnvironment().contains(bna)) {
					if (pwNode.getGraph2().getNeighborCount(bna) != 0) {
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
			for (BiologicalEdgeAbstract bea : getGraph2().getIncidentEdges(bna)) {
				removeElement(bea);
			}
			removeElement(bna);
		}
	}

	public PetriNetSimulation getPetriNetSimulation() {
		if (type == PathwayType.PetriNet) {
			if (petriNetSimulation == null) {
				petriNetSimulation = new PetriNetSimulation(this);
			}
			return petriNetSimulation;
		}
		return transformationInformation.getPetriNet().getPetriNetSimulation();
	}

	public int getPlaceCount() {
		int count = 0;
		for (final BiologicalNodeAbstract bna : graph2.getNodes()) {
			if (bna instanceof Place && !bna.isLogical()) {
				count++;
			}
		}
		return count;
	}

	public int getTransitionCount() {
		int count = 0;
		for (final BiologicalNodeAbstract bna : graph2.getNodes()) {
			if (bna instanceof Transition && !bna.isLogical()) {
				count++;
			}
		}
		return count;
	}

	public int getIncomingDirectedEdgeCount(BiologicalNodeAbstract bna) {
		int count = 0;
		Collection<BiologicalEdgeAbstract> edges = getGraph2().getIncidentEdges(bna);
		for (BiologicalEdgeAbstract e : edges) {
			if (bna == e.getTo() && e.isDirected()) {
				count++;
			}
		}
		return count;
	}

	public int getOutgoingDirectedEdgeCount(BiologicalNodeAbstract bna) {
		int count = 0;
		Collection<BiologicalEdgeAbstract> edges = getGraph2().getIncidentEdges(bna);
		for (BiologicalEdgeAbstract e : edges) {
			if (bna == e.getFrom() && e.isDirected()) {
				count++;
			}
		}
		return count;
	}

	public int getUndirectedEdgeCount(BiologicalNodeAbstract bna) {
		int count = 0;
		Collection<BiologicalEdgeAbstract> edges = getGraph2().getIncidentEdges(bna);
		for (BiologicalEdgeAbstract e : edges) {
			if (!e.isDirected()) {
				count++;
			}
		}
		return count;
	}

	public void setPlotColorPlacesTransitions(boolean override) {
		final float placeCountFactor = 1f / getPlaceCount();
		final float transitionCountFactor = 1f / getTransitionCount();
		int places = 0;
		int transitions = 0;
		for (final BiologicalNodeAbstract bna : getAllGraphNodesSortedAlphabetically()) {
			if (bna instanceof Place && !bna.isLogical()) {
				final Place p = (Place) bna;
				if (override || p.getPlotColor() == null) {
					p.setPlotColor(Color.getHSBColor(places * placeCountFactor, 1, 1));
				}
				places++;
			} else if (bna instanceof Transition && !bna.isLogical()) {
				final Transition t = (Transition) bna;
				if (override || t.getPlotColor() == null) {
					t.setPlotColor(Color.getHSBColor(transitions * transitionCountFactor, 1, 1));
				}
				transitions++;
			}
		}
	}

	public void adjustDown(Collection<BiologicalNodeAbstract> nodes) {
		if (nodes.size() < 2) {
			return;
		}
		double max = Double.MIN_VALUE;
		for (final BiologicalNodeAbstract node : nodes) {
			max = Math.max(graph2.getNodePosition(node).getY(), max);
		}
		for (final BiologicalNodeAbstract node : nodes) {
			final Point2D point = graph2.getNodePosition(node);
			graph2.setNodePosition(node, new Point2D.Double(point.getX(), max));
		}
	}

	public void adjustLeft(Collection<BiologicalNodeAbstract> nodes) {
		if (nodes.size() < 2) {
			return;
		}
		double min = Double.MAX_VALUE;
		for (final BiologicalNodeAbstract node : nodes) {
			min = Math.min(graph2.getNodePosition(node).getX(), min);
		}
		for (final BiologicalNodeAbstract node : nodes) {
			final Point2D point = graph2.getNodePosition(node);
			graph2.setNodePosition(node, new Point2D.Double(min, point.getY()));
		}
	}

	public void adjustHorizontalSpace(final Collection<BiologicalNodeAbstract> nodes) {
		if (nodes.size() < 3) {
			return;
		}
		final List<BiologicalNodeAbstract> sortedNodes = new ArrayList<>(nodes);
		sortedNodes.sort(Comparator.comparingDouble(n -> graph2.getNodePosition(n).getX()));
		final double min = graph2.getNodePosition(sortedNodes.get(0)).getX();
		final double max = graph2.getNodePosition(sortedNodes.get(sortedNodes.size() - 1)).getX();
		final double factor = Math.abs(max - min) / (nodes.size() - 1);
		for (int i = 1; i < sortedNodes.size() - 1; i++) {
			final Point2D p = graph2.getNodePosition(sortedNodes.get(i));
			graph2.setNodePosition(sortedNodes.get(i), new Point2D.Double(min + i * factor, p.getY()));
		}
	}

	public void adjustVerticalSpace(final Collection<BiologicalNodeAbstract> nodes) {
		if (nodes.size() < 3) {
			return;
		}
		final List<BiologicalNodeAbstract> sortedNodes = new ArrayList<>(nodes);
		sortedNodes.sort(Comparator.comparingDouble(n -> graph2.getNodePosition(n).getY()));
		final double min = graph2.getNodePosition(sortedNodes.get(0)).getY();
		final double max = graph2.getNodePosition(sortedNodes.get(sortedNodes.size() - 1)).getY();
		final double factor = Math.abs(max - min) / (nodes.size() - 1);
		for (int i = 1; i < sortedNodes.size() - 1; i++) {
			final Point2D p = graph2.getNodePosition(sortedNodes.get(i));
			graph2.setNodePosition(sortedNodes.get(i), new Point2D.Double(p.getX(), min + i * factor));
		}
	}
}