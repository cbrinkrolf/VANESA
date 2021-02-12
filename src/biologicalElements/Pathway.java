package biologicalElements;

import java.awt.Color;
import java.awt.Graphics;
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
import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstractFactory;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.petriNet.PNNode;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import graph.ChangedFlags;
import graph.groups.Group;
import graph.gui.Boundary;
import graph.gui.CoarseNodeDeleteDialog;
import graph.gui.EdgeDeleteDialog;
import graph.gui.Parameter;
import graph.jung.classes.MyGraph;
import graph.jung.classes.MyVisualizationViewer;
import graph.layouts.Circle;
import graph.layouts.GraphCenter;
import gui.GraphTab;
import gui.MainWindow;
import gui.MyPopUp;
import petriNet.PetriNetProperties;
import petriNet.PetriNetSimulation;
import util.FormularSafety;
import util.MyIntComparable;

public class Pathway implements Cloneable {

	private File file = null;
	private String name = "";
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

	private HashSet<BiologicalNodeAbstract> set = new HashSet<BiologicalNodeAbstract>();
	private MyGraph graph;
	private GraphTab tab;
	private Pathway parent;
	private SortedSet<Integer> ids = new TreeSet<Integer>();
	private Set<BiologicalNodeAbstract> closedSubPathways = new HashSet<BiologicalNodeAbstract>();

	private HashMap<String, ChangedFlags> changedFlags = new HashMap<String, ChangedFlags>();
	private HashMap<Parameter, GraphElementAbstract> changedParameters = new HashMap<Parameter, GraphElementAbstract>();
	private HashMap<Place, Double> changedInitialValues = new HashMap<Place, Double>();
	private HashMap<Place, Boundary> changedBoundaries = new HashMap<Place, Boundary>();

	private BiologicalNodeAbstract rootNode;
	private HashMap<BiologicalNodeAbstract, Point2D> vertices = new HashMap<BiologicalNodeAbstract, Point2D>();

	// no graph tab is assigned / created. this is used for rule editing window
	private boolean headless = false;
	private ArrayList<Group> groupes = new ArrayList<>();
	private Pathway petriNet = null;
	private HashMap<BiologicalNodeAbstract, PNNode> bnToPN = null;
	
	private int placeCount = 0;
	private int transitionCount = 0;

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
		this.name = name.trim();
		this.title = this.name;
		graph = new MyGraph(this);
		tab = new GraphTab(this.name, graph.getGraphVisualization());
	}

	public Pathway(String name, Pathway parent) {
		// this(name);
		this.name = name.trim();
		this.title = this.name;
		this.parent = parent;
	}

	public Set<BiologicalNodeAbstract> getClosedSubPathways() {
		return closedSubPathways;
	}

	public HashMap<BiologicalNodeAbstract, Point2D> getVertices() {
		return vertices;
	}

	protected void cleanVertices() {
		vertices = new HashMap<BiologicalNodeAbstract, Point2D>();
	}

	public Set<BiologicalEdgeAbstract> getEdges() {
		Set<BiologicalEdgeAbstract> edges = new HashSet<BiologicalEdgeAbstract>();
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
		bna.setCompartment(compartment);
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
			Iterator<BiologicalNodeAbstract> it = getAllGraphNodes().iterator();
			BiologicalNodeAbstract node;
			while (it.hasNext()) {
				node = it.next();
				if (node.getName().equals(bna.getName())) {
					if (node.getClass().equals(bna.getClass())) {
						bna.setRef(node);
						createdRef = true;
					} else {
						MyPopUp.getInstance().show("Type mismatch",
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
		if (bna instanceof Place) {
			this.placeCount++;
		}else if (bna instanceof Transition) {
			this.transitionCount++;
		}
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

		if (element.equals(Elementdeclerations.pnEdge)) {
			boolean wasUndirected = false;
			double ActivationProbability = 1.0;

			((PNEdge) bea).wasUndirected(wasUndirected);
			((PNEdge) bea).setProbability(ActivationProbability);

		} else if (element.equals(Elementdeclerations.pnInhibitionEdge)) {
			boolean wasUndirected = false;
			double ActivationProbability = 1.0;
			((PNEdge) bea).wasUndirected(wasUndirected);
			((PNEdge) bea).setProbability(ActivationProbability);
		}
		return addEdge(bea);
	}

	public BiologicalEdgeAbstract addEdge(BiologicalEdgeAbstract bea) {

		// System.out.println("edge hinzugefuegt");
		if (bea != null) {
			if (!bea.getFrom().isCoarseNode() && !bea.getTo().isCoarseNode()) {
				BiologicalNodeAbstract.addConnectingEdge(bea);
				bea.setID(this);
			}
			this.handleChangeFlags(ChangedFlags.EDGE_CHANGED);
			return bea;
		} else
			try {
				throw new NullPointerException();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}

	public void drawEdge(BiologicalEdgeAbstract bea) {
		Collection<BiologicalEdgeAbstract> existingEdges = getGraph().getJungGraph().findEdgeSet(bea.getFrom(),
				bea.getTo());
		boolean add = true;
		for (BiologicalEdgeAbstract edge : existingEdges) {
			if (bea.isDirected() == edge.isDirected()) {
				add = false;
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
		if (element != null) {
			// removeElementFromView(element);
			if (element.isVertex()) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) element;
				getRootPathway().getVertices().remove(bna);
				for (BiologicalNodeAbstract parent : bna.getAllParentNodes()) {
					parent.getVertices().remove(bna);
				}
				if (bna instanceof Place) {
					this.placeCount--;
				}else if (bna instanceof Transition) {
					this.transitionCount--;
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
				this.handleChangeFlags(ChangedFlags.EDGE_CHANGED);
				return;
			}
		}
	}

	public void removeElementWithID(GraphElementAbstract element) {
		removeElement(element);
		ids.remove(element.getID());
	}

	public int edgeGrade(BiologicalEdgeAbstract bea) {
		Set<BiologicalEdgeAbstract> conEdgesTo = new HashSet<BiologicalEdgeAbstract>();
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
		return null != this.graph.getJungGraph().findEdge(from, to);
	}

	public BiologicalEdgeAbstract getEdge(BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		return this.graph.getJungGraph().findEdge(from, to);
	}

	public boolean containsVertex(BiologicalNodeAbstract bna) {
		return this.graph.getJungGraph().containsVertex(bna);
	}

	public boolean containsEdge(BiologicalEdgeAbstract bea) {
		return this.graph.getJungGraph().containsEdge(bea);
	}

	public HashSet<String> getAllNodeLabels() {

		HashSet<String> set = new HashSet<String>();
		Iterator<BiologicalNodeAbstract> it = this.getAllGraphNodes().iterator();// biologicalElements.values().iterator();

		while (it.hasNext()) {
			set.add(it.next().getLabel());
		}
		return set;
	}

	public HashSet<String> getAllNodeNames() {

		HashSet<String> set = new HashSet<String>();
		Iterator<BiologicalNodeAbstract> it = this.getAllGraphNodes().iterator();// biologicalElements.values().iterator();

		while (it.hasNext()) {
			set.add(it.next().getName());
		}
		return set;
	}

	public boolean hasGotAtLeastOneElement() {
		if (!this.getAllGraphNodes().isEmpty())
			return true;
		else
			return false;
	}

	public BiologicalNodeAbstract getNodeByLabel(String label) {

		Iterator<BiologicalNodeAbstract> it = this.getAllGraphNodes().iterator();

		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			if (bna.getLabel().equals(label)) {
				return bna;
			}
		}
		return null;
	}

	public BiologicalNodeAbstract getNodeByName(String name) {
		Iterator<BiologicalNodeAbstract> it = this.getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
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
		HashMap<Integer, BiologicalEdgeAbstract> map = new HashMap<Integer, BiologicalEdgeAbstract>();

		for (BiologicalEdgeAbstract bea : this.getAllEdges()) {
			map.put(bea.getID(), bea);
		}

		ArrayList<Integer> ids = new ArrayList<Integer>(map.keySet());
		Collections.sort(ids, new MyIntComparable());

		List<BiologicalEdgeAbstract> sortedList = new ArrayList<BiologicalEdgeAbstract>();
		for (int i = 0; i < ids.size(); i++) {
			sortedList.add(map.get(ids.get(i)));
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

		HashMap<Integer, BiologicalNodeAbstract> map = new HashMap<Integer, BiologicalNodeAbstract>();

		for (BiologicalNodeAbstract bna : this.getAllGraphNodes()) {
			map.put(bna.getID(), bna);
		}

		ArrayList<Integer> ids = new ArrayList<Integer>(map.keySet());
		Collections.sort(ids, new MyIntComparable());

		List<BiologicalNodeAbstract> sortedList = new ArrayList<BiologicalNodeAbstract>();
		for (int i = 0; i < ids.size(); i++) {
			sortedList.add(map.get(ids.get(i)));
		}

		return sortedList;
	}

	public List<BiologicalNodeAbstract> getAllGraphNodesSortedAlphabetically() {

		HashMap<String, ArrayList<BiologicalNodeAbstract>> map = new HashMap<String, ArrayList<BiologicalNodeAbstract>>();
		String name;
		ArrayList<BiologicalNodeAbstract> list;

		for (BiologicalNodeAbstract bna : this.getAllGraphNodes()) {
			name = bna.getName().toLowerCase();

			if (!map.containsKey(name)) {
				map.put(name, new ArrayList<BiologicalNodeAbstract>());
			}

			map.get(name).add(bna);
		}

		ArrayList<String> ids = new ArrayList<String>(map.keySet());
		Collections.sort(ids);

		List<BiologicalNodeAbstract> sortedList = new ArrayList<BiologicalNodeAbstract>();
		for (int i = 0; i < ids.size(); i++) {
			list = map.get(ids.get(i));
			for (int j = 0; j < list.size(); j++) {
				sortedList.add(list.get(j));
			}
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
		// return getAllNodesAsVector().size();
	}

	public int countEdges() {
		return getGraph().getAllEdges().size();
		// return getAllEdgesAsVector().size();
	}

	public void mergeNodes(Set<BiologicalNodeAbstract> nodes) {
		if (nodes.size() > 1) {
			boolean merged = false;

			// BiologicalNodeAbstract[] array =
			// nodes.toArray(BiologicalNodeAbstract);
			// System.out.println(getGraph().getAllVertices().size());
			// System.out.println(getGraph().getAllEdges().size());

			Iterator<BiologicalNodeAbstract> it = nodes.iterator();

			HashMap<BiologicalNodeAbstract, Set<BiologicalNodeAbstract>> node2Refs = new HashMap<BiologicalNodeAbstract, Set<BiologicalNodeAbstract>>();
			Set<BiologicalNodeAbstract> newNodes = new HashSet<BiologicalNodeAbstract>();
			BiologicalNodeAbstract bna;

			while (it.hasNext()) {
				bna = it.next();
				if (bna.hasRef()) {
					if (nodes.contains(bna.getRef())) {
						if (!node2Refs.containsKey(bna.getRef())) {
							node2Refs.put(bna.getRef(), new HashSet<BiologicalNodeAbstract>());
						}
						node2Refs.get(bna.getRef()).add(bna);
					}
				} else {
					newNodes.add(bna);
				}
			}
			Iterator<BiologicalNodeAbstract> refs = node2Refs.keySet().iterator();

			while (refs.hasNext()) {
				bna = refs.next();
				this.mergeNodes(bna, node2Refs.get(bna));
				merged = true;
			}

			Set<BiologicalNodeAbstract> n = new HashSet<BiologicalNodeAbstract>();
			it = newNodes.iterator();
			while (it.hasNext()) {
				bna = it.next();
				if (bna.getRefs().size() == 0) {
					n.add(bna);
				} else {
					System.err.print("Node with id: " + bna.getID() + " and name: " + bna.getName()
							+ " cannot be merged due to unresolved references!");
				}
			}

			if (n.size() > 1) {
				this.mergeNodes(n.iterator().next(), n);
				merged = true;
			}
			if (merged) {
				MainWindow mw = MainWindow.getInstance();
				mw.updateElementTree();
				mw.updateElementProperties();

				this.graph.updateGraph();
				this.graph.getVisualizationViewer().repaint();

			}
		}
	}

	private void mergeNodes(BiologicalNodeAbstract first, Set<BiologicalNodeAbstract> nodes) {
		Iterator<BiologicalNodeAbstract> it = nodes.iterator();
		BiologicalNodeAbstract bna;
		BiologicalEdgeAbstract bea;
		Iterator<BiologicalEdgeAbstract> itEdges;
		while (it.hasNext()) {

			bna = it.next();
			if (bna != first && bna.getParentNode() == first.getParentNode()) {

				Set<BiologicalEdgeAbstract> edges = new HashSet<BiologicalEdgeAbstract>();
				edges.addAll(bna.getConnectingEdges());
				itEdges = edges.iterator();
				// System.out.println(graph.getJungGraph().getInEdges(bna).size());

				while (itEdges.hasNext()) {
					bea = itEdges.next();
					if (bea.getFrom() == bna || bea.getTo() == bna) {
						this.removeElement(bea);
						bea.getFrom().removeConnectingEdge(bea);
						bea.getTo().removeConnectingEdge(bea);
						if (bea.getTo() == bna) {
							bea.setTo(first);
						} else if (bea.getFrom() == bna) {
							bea.setFrom(first);
						}
						// System.out.println(bea.getFrom().getName());
						// System.out.println(bea.getTo().getName());
						// System.out.println(first.getName()+" "+first.getConnectingEdges().size());
						this.addEdge(bea);
						this.updateMyGraph();
						// BiologicalNodeAbstract.addConnectingEdge(bea);
						// System.out.println(first.getName() + " " +
						// first.getConnectingEdges().size());
						// System.out.println("added");
					}

				}
				first.addLabel(bna.getLabelSet());

				if (bna.hasRef() && bna.getRef() == first) {
					// System.out.println("ref geloescht");
					first.getRefs().remove(bna);
				}
				for (BiologicalNodeAbstract v : this.vertices.keySet()) {
					v.updateHierarchicalAttributes();
					// System.out.println(v + "_" +
					// v.getConnectingEdges().size());
				}
				// System.out.println("all edges:");
				for (BiologicalEdgeAbstract v : this.getAllEdges()) {
					// System.out.println(v.getFrom().getName() + "->" +
					// v.getTo().getName());
				}
				// System.out.println("remove: " + bna);
				// bna.updateHierarchicalAttributes();
				for (BiologicalEdgeAbstract b : bna.getConnectingEdges()) {
					// b.getFrom().updateHierarchicalAttributes();
					// b.getTo().updateHierarchicalAttributes();
					// System.out.println(b.getFrom() + "->" + b.getTo());
				}
				this.removeElement(bna);
				// System.out.println("all edges:");
				for (BiologicalEdgeAbstract v : this.getAllEdges()) {
					// System.out.println(v.getFrom() + "->" + v.getTo());
				}
				// System.out.println();
				for (BiologicalNodeAbstract v : this.vertices.keySet()) {
					// System.out.println(v + "_" +
					// v.getConnectingEdges().size());
				}
				// System.out.println("merged:");
				// Iterator<String> itString =
				// first.getLabelSet().iterator();
				// while(itString.hasNext()){
				// System.out.println(itString.next());
			}
		}
		// System.out.println(graph.getJungGraph().getEdgeCount());
		// System.out.println(this.graph.getAllEdges().size());
		// System.out.println(this.getAllEdges().size());
		updateMyGraph();
		// System.out.println(this.getAllEdges().size());
		this.graph.getVisualizationViewer().getPickedVertexState().clear();
		this.graph.getVisualizationViewer().getPickedEdgeState().clear();
		this.graph.getVisualizationViewer().getPickedVertexState().pick(first, true);
		// System.out.println(graph.getJungGraph().getEdgeCount());
		// System.out.println(this.graph.getAllEdges().size());
		// System.out.println("labels: " + first.getLabelSet().size());
	}

	public void splitNode(Set<BiologicalNodeAbstract> nodes) {
		BiologicalNodeAbstract bna;
		Iterator<BiologicalNodeAbstract> it = nodes.iterator();
		BiologicalEdgeAbstract bea;
		Iterator<BiologicalEdgeAbstract> itEdges;
		BiologicalNodeAbstract newBNA;
		Point2D p = new Point2D.Double(0, 0);

		while (it.hasNext()) {
			bna = it.next();

			if (this.graph.getJungGraph().getNeighborCount(bna) > 1) {

				// edges = this.graph.getJungGraph().getInEdges(bna);
				Set<BiologicalEdgeAbstract> edges = new HashSet<BiologicalEdgeAbstract>();
				edges.addAll(bna.getConnectingEdges());
				itEdges = edges.iterator();
				// System.out.println(graph.getJungGraph().getInEdges(bna).size());
				int count = 1;
				while (itEdges.hasNext() && this.graph.getJungGraph().getNeighborCount(bna) > 1) {
					// System.out.println("while: "+bna.getName()+"
					// "+this.graph.getJungGraph().getNeighborCount(bna));
					bea = itEdges.next();
					// bea.getFrom().removeConnectingEdge(bea);
					// bea.getTo().removeConnectingEdge(bea);
					this.removeElement(bea);
					updateMyGraph();
					for (BiologicalNodeAbstract node : graph.getJungGraph().getVertices()) {
						if (!node.hasRef()) {
							// System.out.println(node.getName() + "v: " +
							// this.graph.getJungGraph().getNeighborCount(node));
						}
					}
					// newBNA = bna.clone();

					// newBNA = new Other(bna.getName(), bna.getLabel());
					newBNA = BiologicalNodeAbstractFactory.create(bna.getBiologicalElement(), bna);
					newBNA.setID(this);
					newBNA.setRefs(new HashSet<BiologicalNodeAbstract>());
					newBNA.setRef(bna);
					while (this.getAllNodeNames().contains(bna.getName() + "_" + count)) {
						count++;
					}
					newBNA.setName(bna.getName() + "_" + count);
					newBNA.setLabel(newBNA.getName());
					if (bea.getTo() == bna) {
						bea.setTo(newBNA);
						p = this.getGraph().getVertexLocation(bea.getFrom());
					} else if (bea.getFrom() == bna) {
						bea.setFrom(newBNA);
						p = this.getGraph().getVertexLocation(bea.getTo());
					}
					this.addVertex(newBNA, new Point2D.Double(p.getX() + 20, p.getY() + 20));
					this.addEdge(bea);
					graph.getVisualizationViewer().getPickedVertexState().pick(newBNA, true);
					// updateMyGraph();
					// System.out.println("count: " +
					// this.graph.getJungGraph().getNeighborCount(bna));
					for (BiologicalNodeAbstract node : graph.getJungGraph().getVertices()) {
						if (!node.hasRef()) {
							// System.out.println(node.getName() + "n: " +
							// this.graph.getJungGraph().getNeighborCount(node));
						}
					}
					updateMyGraph();
				}
				updateMyGraph();
				// System.out.println(this.graph.getJungGraph().getNeighborCount(bna));
				// System.out.println(graph.getJungGraph().getOutEdges(bna).size());
			}
		}

		for (BiologicalNodeAbstract node : graph.getJungGraph().getVertices()) {
			if (!node.hasRef()) {
				// System.out.println(node.getName() + ": " +
				// this.graph.getJungGraph().getNeighborCount(node));
			}
		}
		MainWindow mw = MainWindow.getInstance();
		mw.updateElementTree();
		mw.updateElementProperties();
		this.graph.getVisualizationViewer().repaint();

		// System.out.println(graph.getJungGraph().getEdgeCount());
		// System.out.println(this.graph.getAllEdges().size());
	}

	/**
	 * Reset Element lists.
	 */
	public void clearElements() {
		set.clear();
	}

	@Override
	public Pathway clone() {
		try {
			return (Pathway) super.clone();
		} catch (CloneNotSupportedException e) {
			// Kann eigentlich nicht passieren, da Cloneable
			throw new InternalError();
		}
	}

	// ---Getter/Setter---

	public PetriNetProperties getPetriPropertiesNet() {
		if (isPetriNet) {
			return petriNetProperties;
		} else if (petriNet != null) {
			return petriNet.getPetriPropertiesNet();
		}
		return null;
	}

	public boolean isPetriNet() {
		return isPetriNet;
	}

	public void setPetriNet(boolean isPetriNet) {
		this.isPetriNet = isPetriNet;
		if (isPetriNet) {
			petriNetProperties = new PetriNetProperties();
		}
	}

	public void setGraph(MyGraph graph) {
		this.graph = graph;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File f) {
		this.file = f;
		// System.out.println(filename);
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		// if PW is BNA
		this.name = FormularSafety.replace(name.trim());
		if (tab != null) {
			this.name = name.trim();
			tab.setTitle(this.name);
		}
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String name) {
		this.title = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
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

	public GraphTab getTab() {
		if (tab == null) {
			tab = new GraphTab(name, getGraph().getGraphVisualization());
			tab.setTitle(name);
		}
		return tab;
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

	public void setNewLoadedNodes(HashSet<BiologicalNodeAbstract> loadedElements) {
		set = loadedElements;
	}

	public HashSet<BiologicalNodeAbstract> getNewLoadedNodes() {
		return set;
	}

	public void setSpecification(boolean organismSpecific) {
		orgSpecification = organismSpecific;
	}

	public boolean getSpecification() {
		return orgSpecification;
	}

	public String getSpecificationAsString() {
		return orgSpecification + "";
	}

	public void setSpecification(String organismSpecific) {
		if (organismSpecific.equalsIgnoreCase("true")) {
			orgSpecification = true;
		} else {
			orgSpecification = false;
		}
	}

	public void setParent(Pathway parent) {
		this.parent = parent;
	}

	public Pathway getParent() {
		return parent;
	}

	public Pathway getRootPathway() {
		if (getParent() == null)
			return this;
		else
			return parent.getRootPathway();
	}

	public ArrayList<Pathway> getChilds() {
		ArrayList<Pathway> result = new ArrayList<Pathway>();
		Iterator<BiologicalNodeAbstract> it = getAllGraphNodes().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = it.next();
			if (bna instanceof PathwayMap && ((PathwayMap) bna).getPathwayLink() != null)
				result.add(((PathwayMap) bna).getPathwayLink());
		}
		return result;
	}

	public SortedSet<Integer> getIdSet() {
		if (getRootPathway() == this)
			return this.ids;
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

		// System.out.println("startlagg");

		// Clear myGraph
		getGraph().removeAllElements();

		// Set of all edges of the finest abstraction level
		Set<BiologicalEdgeAbstract> flattenedEdges = new HashSet<BiologicalEdgeAbstract>();

		// HashMap for abstracted nodes
		HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> abstractedNodes = new HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract>();
		for (BiologicalNodeAbstract n : vertices.keySet()) {
			// System.out.println("put + " + n + " " +
			// n.getConnectingEdges().size());
			abstractedNodes.put(n, n);
		}

		// Add all flattened edges to the set of flattened edges
		for (BiologicalNodeAbstract n : abstractedNodes.keySet()) {
			// System.out.println("a: "+this.getAllEdges().size());
			// System.out.println("bea: "+n.getConnectingEdges().iterator().next());
			flattenedEdges.addAll(n.getConnectingEdges());
			// System.out.println(n + " " + n.getConnectingEdges().size());
		}

		// Abstract nodes
		Set<BiologicalNodeAbstract> csp = new HashSet<BiologicalNodeAbstract>();
		csp.addAll(closedSubPathways);
		for (BiologicalNodeAbstract n : csp) {

			// If coarse node was flattened, remove it from the list of closed
			// nodes
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
		// System.out.println("endlagg");

	}

	public void saveVertexLocations() {
		Set<BiologicalNodeAbstract> nodes = new HashSet<BiologicalNodeAbstract>();
		nodes.addAll(vertices.keySet());
		for (BiologicalNodeAbstract n : nodes) {
			if (getGraph().getAllVertices().contains(n)) {
				// System.out.println(getGraph().getVertexLocation(n));
				vertices.put(n, getGraph().getVertexLocation(n));
			}
		}
	}

	private Point2D getVertexPosition(BiologicalNodeAbstract n) {
		if (vertices.keySet().contains(n)) {
			return vertices.get(n);
		}
		if (n.isCoarseNode() || n.getVertices().size() > 0) {
			if (n.getRootNode() != null) {
				return getVertexPosition(n.getRootNode());
			} else {
				Set<Point2D> childrenCoordinates = new HashSet<Point2D>();
				for (BiologicalNodeAbstract child : n.getVertices().keySet()) {
					childrenCoordinates.add(n.getVertices().get(child));
				}
				return Circle.averagePoint(childrenCoordinates);
			}
		}
		return new Point2D.Double(0.0, 0.0);
	}

	public boolean isRootPathway() {
		if (getRootPathway() == this) {
			return true;
		}
		return false;
	}

	public boolean isBNA() {
		if (this instanceof BiologicalNodeAbstract) {
			return true;
		}
		return false;
	}

	public boolean hasGraph() {
		if (getGraph(false) == null) {
			return false;
		}
		return true;
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

	public BiologicalNodeAbstract getRootNode() {
		return rootNode;
	}

	public void setRootNode(BiologicalNodeAbstract node) {
		rootNode = node;
	}

	public void handleChangeFlags(int flag) {
		Iterator<ChangedFlags> it = this.changedFlags.values().iterator();
		ChangedFlags cf;

		while (it.hasNext()) {
			cf = it.next();
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
		if (!this.changedFlags.containsKey(key)) {
			this.changedFlags.put(key, new ChangedFlags());
		}
		return changedFlags.get(key);
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

	public void stretchGraph(double factor) {
		BiologicalNodeAbstract bna;
		Point2D p;
		Iterator<BiologicalNodeAbstract> it = getAllGraphNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();
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
		this.getGraph().getVisualizationViewer().getPickedEdgeState().clear();
		this.getGraph().getVisualizationViewer().getPickedVertexState().clear();
		this.getGraph().updateGraph();
		updateMyGraph();
	}

	/**
	 * Add all selected Nodes to one Group
	 *
	 */
	public void groupSelectedNodes() {
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = getGraph().getVisualizationViewer();

		Set<BiologicalNodeAbstract> nodes = vv.getPickedVertexState().getPicked();
		if (nodes.size() > 1) {
			ArrayList<BiologicalNodeAbstract> nodeslist = new ArrayList<BiologicalNodeAbstract>();
			nodeslist.addAll(nodes);
			Group group = new Group(nodeslist);
			groupes.add(group);

			Iterator<BiologicalNodeAbstract> it = vv.getPickedVertexState().getPicked().iterator();
			while (it.hasNext()) {
				BiologicalNodeAbstract nextNode = it.next();
				nextNode.setisinGroup(true);
				nextNode.setGroup(group);
			}
		} else {
			MyPopUp.getInstance().show("Groupingerror", "This cannot be grouped.");
		}
	}

	/**
	 * If one node is selected, the group members will be picked too.
	 *
	 */
	public void pickGroup() {
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = getGraph().getVisualizationViewer();
		if (vv.getPickedVertexState().getPicked().size() != 0) {
			Iterator<BiologicalNodeAbstract> it = vv.getPickedVertexState().getPicked().iterator();
			BiologicalNodeAbstract nextNode = it.next();
			if (nextNode.getisinGroup()) {
				for (BiologicalNodeAbstract node : nextNode.getbiggestGroup().nodes) {
					graph.getVisualizationViewer().getPickedVertexState().pick(node, true);
				}
			}
		}
	}

	/**
	 * Delete selected group.
	 *
	 */
	public void deleteGroup() {
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = getGraph().getVisualizationViewer();
		Iterator<BiologicalNodeAbstract> iter = vv.getPickedVertexState().getPicked().iterator();
		Group groupToDelete = null;
		Boolean deletegroup = true;
		BiologicalNodeAbstract firstNode = iter.next();
		groupToDelete = firstNode.getbiggestGroup();

		// Checks if selected nodes are all of the same group
		for (BiologicalNodeAbstract bnaNode : vv.getPickedVertexState().getPicked()) {
			if (!bnaNode.getGroups().contains(groupToDelete)) {
				deletegroup = false;
				MyPopUp.getInstance().show("Deletion error", "This cannot be deleted.");
			}
		}
		// if all selected are from same group, delete group
		Iterator<BiologicalNodeAbstract> it = vv.getPickedVertexState().getPicked().iterator();
		while (it.hasNext() && deletegroup) {
			BiologicalNodeAbstract nextNode = it.next();
			if (nextNode.getGroups().size() == 1) {
				nextNode.setisinGroup(false);
			}
			nextNode.getGroups().remove(groupToDelete);
		}
		if (groupToDelete != null && deletegroup) {
			groupes.remove(groupToDelete);
		}
		graph.getVisualizationViewer().getPickedVertexState().clear();
	}

	private void removeSelectedEdges() {
		if (getGraph().getAllEdges().size() > 0) {
			// System.out.println("e:
			// "+vv.getPickedEdgeState().getSelectedObjects().length);

			Iterator<BiologicalEdgeAbstract> it = getGraph().getVisualizationViewer().getPickedEdgeState().getPicked()
					.iterator();
			// Iterator it = vv.getPickedState().getPickedEdges().iterator();
			while (it.hasNext()) {
				removeElement(it.next());
			}
		}
	}

	private void removeSelectedVertices() {
		// System.out.println(vv.getPickedVertexState().getPicked().size());
		if (getGraph().getAllVertices().size() > 0) {
			MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = getGraph()
					.getVisualizationViewer();
			Iterator<BiologicalNodeAbstract> it = vv.getPickedVertexState().getPicked().iterator();
			BiologicalNodeAbstract bna;
			Integer savedAnswer = -1;
			while (it.hasNext()) {
				bna = it.next();
				if (this instanceof BiologicalNodeAbstract) {
					BiologicalNodeAbstract pwNode = (BiologicalNodeAbstract) this;
					if (pwNode.getEnvironment().contains(bna)) {
						if (pwNode.getGraph().getJungGraph().getNeighborCount(bna) != 0) {
							JOptionPane.showMessageDialog(null, "Can't delete connected environment nodes.",
									"Deletion Error", JOptionPane.ERROR_MESSAGE);
							continue;
						} else {
							Object[] options = { "Yes", "No" };
							int answer = JOptionPane.showOptionDialog(vv,
									"Do you want to delete the predefined environment node " + bna.getLabel() + "?",
									"Delete predefined environment node", JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
							if (answer == JOptionPane.NO_OPTION) {
								continue;
							} else {
								removeElement(bna);
								getRootPathway().updateMyGraph();
								continue;
							}
						}
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

				Set<BiologicalEdgeAbstract> conEdges = new HashSet<BiologicalEdgeAbstract>();
				conEdges.addAll(bna.getConnectingEdges());
				for (BiologicalEdgeAbstract bea : conEdges) {
					bea.getFrom().removeConnectingEdge(bea);
					bea.getTo().removeConnectingEdge(bea);
				}
				removeElement(bna);
			}
		}
	}

	public boolean isHeadless() {
		return this.headless;
	}

	public VisualizationImageServer<BiologicalNodeAbstract, BiologicalEdgeAbstract> prepareGraphToPrint() {

		MyGraph mg = getGraph();
		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> v = getGraph().getVisualizationViewer();
		if (!(mg.getLayout() instanceof StaticLayout)) {
			mg.changeToStaticLayout();
		}
		VisualizationImageServer<BiologicalNodeAbstract, BiologicalEdgeAbstract> wvv = new VisualizationImageServer<BiologicalNodeAbstract, BiologicalEdgeAbstract>(
				mg.getLayout(), mg.getLayout().getSize());
		wvv.setBackground(Color.white);
		for (int i = 0; i < v.getPreRenderers().size(); i++) {
			wvv.addPreRenderPaintable(v.getPreRenderers().get(i));
		}

		wvv.addPostRenderPaintable(new Paintable() {
			@Override
			public boolean useTransform() {
				return false;
			}

			@Override
			public void paint(Graphics g) {
				Iterator<BiologicalNodeAbstract> it = getAllGraphNodes().iterator();
				Place p;
				BiologicalNodeAbstract bna;
				while (it.hasNext()) {
					bna = it.next();
					if (bna instanceof Place) {
						p = (Place) bna;
						int x1 = (int) (p.getShape().getBounds2D().getMaxX() - p.getShape().getBounds2D().getMinX());

						boolean discrete = false;
						String tokens = p.getToken() + "";
						if (p.isDiscrete()) {
							tokens = (int) p.getToken() + "";
							discrete = true;
						}

						if (p.hasRef() && p.getRef() instanceof Place) {
							tokens = ((Place) p.getRef()).getToken() + "";
							if (((Place) p.getRef()).isDiscrete()) {
								tokens = (int) ((Place) p.getRef()).getToken() + "";
								discrete = true;
							}
						}

						int xpos;
						Point2D point = v.getGraphLayout().apply(p);
						Point2D p1inv = v.getRenderContext().getMultiLayerTransformer().transform(point);
						if (discrete) {
							xpos = Double.valueOf(p1inv.getX() - x1 + 19 - 5 * ((double) tokens.length() / 2))
									.intValue();
						} else {
							xpos = Double.valueOf(p1inv.getX() - x1 + 21 - 5 * ((double) tokens.length() / 2))
									.intValue();
						}
						g.setColor(Color.BLACK);
						int y = (int) p1inv.getY();
						g.drawString(tokens, xpos, y + 7);
					}
				}
			}
		});

		wvv.setBackground(Color.white);
		wvv.setRenderContext(v.getRenderContext());

		double scaleV = wvv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
		double scaleL = wvv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
		double scale;
		if (scaleV < 1) {
			scale = scaleV;
		} else {
			scale = scaleL;
		}
		GraphCenter gc = new GraphCenter(mg);
		double width = gc.getWidth();
		double height = gc.getHeight();

		if (width > height) {
			width = width * scale;
		} else {
			height = height * scale;
		}

		//Point2D p1inv = v.getRenderContext().getMultiLayerTransformer().transform(new Point2D.Double(gc.getMinX() + gc.getWidth(), gc.getMinY() + gc.getHeight()));

		//wvv.setBounds(0, 0, (int) p1inv.getX() + 150, (int) p1inv.getY() + 50);
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

	public ArrayList<Group> getGroupes() {
		return groupes;
	}

	public void setGroupes(ArrayList<Group> groupes) {
		this.groupes = groupes;
	}

	public Pathway getPetriNet() {
		return petriNet;
	}

	public void setPetriNet(Pathway petriNet) {
		this.petriNet = petriNet;
	}

	public PetriNetSimulation getPetriNetSimulation() {
		if (this.isPetriNet) {
			if (this.petriNetSimulation == null) {
				this.petriNetSimulation = new PetriNetSimulation(this);
			}
			return petriNetSimulation;
		}else{
			return this.petriNet.getPetriNetSimulation();
		}
	}

	public HashMap<BiologicalNodeAbstract, PNNode> getBnToPN() {
		return bnToPN;
	}

	public void setBnToPN(HashMap<BiologicalNodeAbstract, PNNode> bnToPN) {
		this.bnToPN = bnToPN;
	}
	
	public int getPlaceCount(){
		return this.placeCount;
	}
	
	public int getTransitionCount(){
		return this.transitionCount;
	}
}