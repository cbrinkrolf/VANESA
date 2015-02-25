package biologicalElements;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.PNEdge;
import petriNet.Place;
import petriNet.StochasticTransition;
import petriNet.Transition;
import util.MyIntComparable;
import biologicalObjects.edges.Activation;
import biologicalObjects.edges.BindingAssociation;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.Compound;
import biologicalObjects.edges.Dephosphorylation;
import biologicalObjects.edges.Dissociation;
import biologicalObjects.edges.Expression;
import biologicalObjects.edges.Glycosylation;
import biologicalObjects.edges.HiddenCompound;
import biologicalObjects.edges.IndirectEffect;
import biologicalObjects.edges.Inhibition;
import biologicalObjects.edges.Methylation;
import biologicalObjects.edges.Phosphorylation;
import biologicalObjects.edges.PhysicalInteraction;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.edges.ReactionPair;
import biologicalObjects.edges.Repression;
import biologicalObjects.edges.StateChange;
import biologicalObjects.edges.Ubiquitination;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.CollectorNode;
import biologicalObjects.nodes.Complex;
import biologicalObjects.nodes.CompoundNode;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Degraded;
import biologicalObjects.nodes.Disease;
import biologicalObjects.nodes.Drug;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.GeneOntology;
import biologicalObjects.nodes.Glycan;
import biologicalObjects.nodes.HomodimerFormation;
import biologicalObjects.nodes.LigandBinding;
import biologicalObjects.nodes.MRNA;
import biologicalObjects.nodes.MembraneChannel;
import biologicalObjects.nodes.MembraneReceptor;
import biologicalObjects.nodes.OrthologGroup;
import biologicalObjects.nodes.Other;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.Reaction;
import biologicalObjects.nodes.Receptor;
import biologicalObjects.nodes.SRNA;
import biologicalObjects.nodes.SmallMolecule;
import biologicalObjects.nodes.SolubleReceptor;
import biologicalObjects.nodes.TranscriptionFactor;
import edu.uci.ics.jung.graph.util.Pair;
import graph.ChangedFlags;
import graph.filter.FilterSettings;
import graph.gui.Boundary;
import graph.gui.EdgeDeleteDialog;
import graph.gui.Parameter;
import graph.jung.classes.MyGraph;
import graph.layouts.hebLayout.HEBLayout;
import gui.GraphTab;
import gui.MainWindow;
import gui.MainWindowSingleton;
//import edu.uci.ics.jung.graph.Vertex;
//import edu.uci.ics.jung.utils.Pair;

public class Pathway implements Cloneable {

	// ---Fields---

	private File filename = null;

	private String name = "";

	private String version = "";

	private String date = "";

	private String title = "";

	private String author = "";

	private String referenceNumber = "";

	private String link = "";

	private String ImagePath = "";

	private String organism = "";

	private boolean orgSpecification;

	private String description = "";

	private String number = "";

	private Hashtable<String, Integer> nodeDescription = new Hashtable<String, Integer>();

	private boolean[] settings = new boolean[11];

	private boolean isPetriNet = false;

	private boolean isPetriNetSimulation = false;

	private final PetriNet petriNet = new PetriNet();

	private final HashMap<String, GraphElementAbstract> biologicalElements = new HashMap<String, GraphElementAbstract>();

	// private HashSet <Vertex> set = new HashSet <Vertex> ();

	private HashSet<BiologicalNodeAbstract> set = new HashSet<BiologicalNodeAbstract>();

	private HashMap<Pair<BiologicalNodeAbstract>, BiologicalEdgeAbstract> edges = new HashMap<Pair<BiologicalNodeAbstract>, BiologicalEdgeAbstract>();

	private MyGraph graph;

	private GraphTab tab;

	private final FilterSettings filterSettings;

	private final InternalGraphRepresentation graphRepresentation = new InternalGraphRepresentation();

	private boolean isDAWISProject = false;

	private Pathway parent;

	private SortedSet<Integer> ids = new TreeSet<Integer>();

	private HashMap<BiologicalNodeAbstract, Point2D> openedSubPathways = new HashMap<BiologicalNodeAbstract,Point2D>();

	private HashMap<String, ChangedFlags> changedFlags = new HashMap<String, ChangedFlags>();

	private HashMap<Parameter, GraphElementAbstract> changedParameters = new HashMap<Parameter, GraphElementAbstract>();

	private HashMap<Place, Double> changedInitialValues = new HashMap<Place, Double>();

	private HashMap<Place, Boundary> changedBoundaries = new HashMap<Place, Boundary>();
	
	private BiologicalNodeAbstract rootNode;

	// ---Functional Methods---

	public Pathway(String name) {
		this.title = name;
		graph = new MyGraph(this);
		tab = new GraphTab(name, graph.getGraphVisualization());
		filterSettings = new FilterSettings();
	}

	public Pathway(String name, Pathway parent) {
		// this(name);
		this.title = name;
		filterSettings = new FilterSettings();
		this.parent = parent;
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

	public BiologicalNodeAbstract addVertex(String name, String label,
			String elementDecleration, String compartment, Point2D p) {
		BiologicalNodeAbstract bna = null;

		if (elementDecleration.equals(Elementdeclerations.protein))
			bna = new Protein(label, name);
		else if (elementDecleration.equals(Elementdeclerations.enzyme))
			bna = new Enzyme(label, name);
		else if (elementDecleration
				.equals(Elementdeclerations.homodimerFormation))
			bna = new HomodimerFormation(label, name);
		else if (elementDecleration.equals(Elementdeclerations.degraded))
			bna = new Degraded(label, name);
		else if (elementDecleration.equals(Elementdeclerations.pathwayMap))
			bna = new PathwayMap(label, name);
		else if (elementDecleration.equals(Elementdeclerations.smallMolecule))
			bna = new SmallMolecule(label, name);
		else if (elementDecleration.equals(Elementdeclerations.solubleReceptor))
			bna = new SolubleReceptor(label, name);
		else if (elementDecleration.equals(Elementdeclerations.sRNA))
			bna = new SRNA(label, name);
		else if (elementDecleration.equals(Elementdeclerations.dna))
			bna = new DNA(label, name);
		else if (elementDecleration.equals(Elementdeclerations.complex))
			bna = new Complex(label, name);
		else if (elementDecleration.equals(Elementdeclerations.ligandBinding))
			bna = new LigandBinding(label, name);
		else if (elementDecleration.equals(Elementdeclerations.membraneChannel))
			bna = new MembraneChannel(label, name);
		else if (elementDecleration
				.equals(Elementdeclerations.membraneReceptor))
			bna = new MembraneReceptor(label, name);
		else if (elementDecleration.equals(Elementdeclerations.mRNA))
			bna = new MRNA(label, name);
		else if (elementDecleration.equals(Elementdeclerations.receptor))
			bna = new Receptor(label, name);
		else if (elementDecleration.equals(Elementdeclerations.place))
			bna = new Place(label, name, 0, true);
		else if (elementDecleration.equals(Elementdeclerations.s_place))
			bna = new Place(label, name, 0, false);
		// Transkription Factor node will be generated here
		else if (elementDecleration
				.equals(Elementdeclerations.transcriptionFactor)) {
			bna = new TranscriptionFactor(label, name);
		} else if (elementDecleration.equals(Elementdeclerations.glycan))
			bna = new Glycan(label, name);
		else if (elementDecleration.equals(Elementdeclerations.others))
			bna = new Other(label, name);
		else if (elementDecleration.equals(Elementdeclerations.orthologGroup))
			bna = new OrthologGroup(label, name);
		else if (elementDecleration.equals(Elementdeclerations.compound))
			bna = new CompoundNode(label, name);
		else if (elementDecleration.equals(Elementdeclerations.disease))
			bna = new Disease(label, name);
		else if (elementDecleration.equals(Elementdeclerations.drug))
			bna = new Drug(label, name);
		else if (elementDecleration.equals(Elementdeclerations.go))
			bna = new GeneOntology(label, name);
		else if (elementDecleration.equals(Elementdeclerations.gene))
			bna = new Gene(label, name);
		else if (elementDecleration.equals(Elementdeclerations.reaction))
			bna = new Reaction(label, name);
		else if (elementDecleration.equals(Elementdeclerations.collector))
			bna = new CollectorNode(label, name);
		else if (elementDecleration
				.equals(Elementdeclerations.stochasticTransition))
			bna = new StochasticTransition(label, name);
		else if (elementDecleration
				.equals(Elementdeclerations.discreteTransition))
			bna = new DiscreteTransition(label, name);
		else if (elementDecleration
				.equals(Elementdeclerations.continuousTransition))
			bna = new ContinuousTransition(label, name);
		else if (elementDecleration.equals(Elementdeclerations.graphdbnode))
			bna = new Protein(label + "FAILSAFE", name + "FAILSAFE");
		if (bna != null) {
			bna.setCompartment(compartment);
			return addVertex(bna, p);
		} else
			try {
				throw new NullPointerException();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}

	public BiologicalNodeAbstract addVertex(BiologicalNodeAbstract bna,
			Point2D p) {
		// System.out.println("add");
		// System.out.println(bna.getClass().getName());
		// Object graphElement = element;
		// GraphElementAbstract gea = (GraphElementAbstract) element;
		if (getAllNodes().contains(bna)) {
			return bna;
		}
		bna.setLabel(bna.getLabel().trim());
		bna.setName(bna.getName().trim());

		// System.out.println(biologicalElements.size());
		graphRepresentation.addVertex(bna);
		getGraph().addVertex(bna, p);
		if (bna instanceof Place) {
			this.petriNet.setPlaces(this.petriNet.getPlaces() + 1);
		}
		if (bna instanceof Transition) {
			this.petriNet.setTransitions(this.petriNet.getTransitions() + 1);
		}
		// System.out.println("node eingefuegt");

		if (!nodeDescription.containsKey(bna.getBiologicalElement())) {
			nodeDescription.put(bna.getBiologicalElement(), 1);
		} else {
			Integer temp = nodeDescription.get(bna.getBiologicalElement()) + 1;
			nodeDescription.remove(bna.getBiologicalElement());
			nodeDescription.put(bna.getBiologicalElement(), temp);
		}
		// System.out.println("new id: "+bna.getID());
		bna.setID();
		biologicalElements.put(bna.getID() + "", bna);
		//
		// System.out.println(this.graph.getAllVertices().size());
		// bna.setNodesize(this.graph.getAllVertices().size());
		this.handleChangeFlags(ChangedFlags.NODE_CHANGED);
		return bna;
	}

	public BiologicalEdgeAbstract addEdge(String label, String name,
			BiologicalNodeAbstract from, BiologicalNodeAbstract to,
			String element, boolean directed) {

		BiologicalEdgeAbstract bea = null;

		if (element.equals(Elementdeclerations.compoundEdge)) {

			bea = new Compound(label, name, from, to);

		} else if (element.equals(Elementdeclerations.hiddenCompoundEdge)) {

			bea = new HiddenCompound(label, name, from, to);

		} else if (element.equals(Elementdeclerations.reactionEdge)) {
			// System.out.println("drin");
			bea = new ReactionEdge(label, name, from, to);

		} else if (element.equals(Elementdeclerations.activationEdge)) {

			bea = new Activation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.inhibitionEdge)) {

			bea = new Inhibition(label, name, from, to);

		} else if (element.equals(Elementdeclerations.expressionEdge)) {

			bea = new Expression(label, name, from, to);

		} else if (element.equals(Elementdeclerations.repressionEdge)) {

			bea = new Repression(label, name, from, to);

		} else if (element.equals(Elementdeclerations.indirectEffectEdge)) {

			bea = new IndirectEffect(label, name, from, to);

		} else if (element.equals(Elementdeclerations.stateChangeEdge)) {

			bea = new StateChange(label, name, from, to);

		} else if (element.equals(Elementdeclerations.bindingEdge)) {

			bea = new BindingAssociation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.dissociationEdge)) {

			bea = new Dissociation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.phosphorylationEdge)) {

			bea = new Phosphorylation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.dephosphorylationEdge)) {

			bea = new Dephosphorylation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.glycosylationEdge)) {

			bea = new Glycosylation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.ubiquitinationEdge)) {

			bea = new Ubiquitination(label, name, from, to);

		} else if (element.equals(Elementdeclerations.methylationEdge)) {

			bea = new Methylation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.reactionPairEdge)) {

			bea = new ReactionPair(label, name, from, to);

		} else if (element.equals(Elementdeclerations.physicalInteraction)) {

			bea = new PhysicalInteraction(label, name, from, to);

		} else if (element.equals(Elementdeclerations.pnDiscreteEdge)) {
			boolean wasUndirected = false;
			double UpperBoundary = 0.0;
			double LowerBoundary = 0.0;
			double ActivationProbability = 1.0;
			/*
			 * if (bea instanceof PNEdge) { PNEdge e = (PNEdge) bea; tokens =
			 * e.getFunction(); wasUndirected = e.wasUndirected(); UpperBoundary
			 * = e.getUpperBoundary(); LowerBoundary = e.getLowerBoundary();
			 * ActivationProbability = e.getActivationProbability(); }
			 */
			bea = new PNEdge(from, to, label, name, "discrete", label);
			((PNEdge) bea).wasUndirected(wasUndirected);
			((PNEdge) bea).setLowerBoundary(LowerBoundary);
			((PNEdge) bea).setUpperBoundary(UpperBoundary);
			((PNEdge) bea).setActivationProbability(ActivationProbability);
			((PNEdge) bea).setBiologicalElement(bea.getBiologicalElement());
			// System.out.println("discrete kante pw hinzugefuegt");

		} else if (element.equals(Elementdeclerations.pnContinuousEdge)) {
			boolean wasUndirected = false;
			double UpperBoundary = 0.0;
			double LowerBoundary = 0.0;
			double ActivationProbability = 1.0;
			/*
			 * if (bea instanceof PNEdge) { PNEdge e = (PNEdge) bea; tokens =
			 * e.getFunction(); wasUndirected = e.wasUndirected(); UpperBoundary
			 * = e.getUpperBoundary(); LowerBoundary = e.getLowerBoundary();
			 * ActivationProbability = e.getActivationProbability(); }
			 */
			bea = new PNEdge(from, to, label, name,
					Elementdeclerations.pnContinuousEdge, label);
			((PNEdge) bea).wasUndirected(wasUndirected);
			((PNEdge) bea).setLowerBoundary(LowerBoundary);
			((PNEdge) bea).setUpperBoundary(UpperBoundary);
			((PNEdge) bea).setActivationProbability(ActivationProbability);
			((PNEdge) bea).setBiologicalElement(bea.getBiologicalElement());
			// System.out.println("continuous kante pw hinzugefuegt");

		} else if (element.equals(Elementdeclerations.pnInhibitionEdge)) {
			boolean wasUndirected = false;
			double UpperBoundary = 0.0;
			double LowerBoundary = 0.0;
			double ActivationProbability = 1.0;
			/*
			 * if (bea instanceof PNEdge) { PNEdge e = (PNEdge) bea; tokens =
			 * e.getFunction(); wasUndirected = e.wasUndirected(); UpperBoundary
			 * = e.getUpperBoundary(); LowerBoundary = e.getLowerBoundary();
			 * ActivationProbability = e.getActivationProbability(); }
			 */
			bea = new PNEdge(from, to, label, name,
					Elementdeclerations.pnInhibitionEdge, label);
			((PNEdge) bea).wasUndirected(wasUndirected);
			((PNEdge) bea).setLowerBoundary(LowerBoundary);
			((PNEdge) bea).setUpperBoundary(UpperBoundary);
			((PNEdge) bea).setActivationProbability(ActivationProbability);
			((PNEdge) bea).setBiologicalElement(bea.getBiologicalElement());
			// System.out.println("inhibition kante pw hinzugefuegt");

		}

		if (bea != null) {
			bea.setDirected(directed);
			return this.addEdge(bea);
		} else
			try {
				throw new NullPointerException();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}

	public BiologicalEdgeAbstract addEdge(BiologicalEdgeAbstract bea) {

		// System.out.println("id in pw: "+bea.getID());
		// System.out.println("edge hinzugefuegt");

		if (bea != null) {

//			edges.put(
//					new Pair<BiologicalNodeAbstract>(bea.getFrom(), bea.getTo()),
//					bea);
//			// System.out.println(biologicalElements.size());
//			// Pair p = bea.getEdge().getEndpoints();
//			graphRepresentation.addEdge(bea);
//			getGraph().addEdge(bea);
//			bea.setID();
//			biologicalElements.put(bea.getID() + "", bea);
//			if (!bea.getFrom().isCoarseNode() && !bea.getTo().isCoarseNode()
//					&& bea.getFrom().getParentNode() == null
//					&& bea.getTo().getParentNode() == null && !bea.isClone()) {
			if (!bea.getFrom().isCoarseNode() && !bea.getTo().isCoarseNode()
					&& !bea.isClone()) {
				BiologicalNodeAbstract.addConnectingEdge(bea);
				bea.setID();
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
	
	public void drawEdge(BiologicalEdgeAbstract bea){
		Collection<BiologicalEdgeAbstract> existingEdges = getGraph().getJungGraph().findEdgeSet(bea.getFrom(), bea.getTo());
		boolean add = true;
		for(BiologicalEdgeAbstract edge : existingEdges){
			if(bea.isDirected()==edge.isDirected()){
				add=false;
			}
		}
		if(add){
			edges.put(
				new Pair<BiologicalNodeAbstract>(bea.getFrom(), bea.getTo()),
				bea);
			// System.out.println(biologicalElements.size());
			// Pair p = bea.getEdge().getEndpoints();
			graphRepresentation.addEdge(bea);
			getGraph().addEdge(bea);
			bea.setID();
			biologicalElements.put(bea.getID() + "", bea);
		}
	}

	public void removeElement(GraphElementAbstract element) {
		if (element != null) {
			if (element.isVertex()) {
				// System.out.println(biologicalElements.size());
				// System.out.println("drin");
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) element;
				graphRepresentation.removeVertex(bna);
				getGraph().removeVertex(bna);

				// System.out.println("durch");
				if (!nodeDescription.containsKey(bna.getBiologicalElement())) {
				} else {
					Integer temp = nodeDescription.get(bna
							.getBiologicalElement());
					// System.out.println(temp);
					if (temp == 1) {
						nodeDescription.remove(bna.getBiologicalElement());
					} else {
						nodeDescription.remove(bna.getBiologicalElement());
						nodeDescription.put(bna.getBiologicalElement(),
								temp - 1);
					}
				}
				if (bna instanceof Place) {
					this.petriNet.setPlaces(this.petriNet.getPlaces() - 1);
				}
				if (bna instanceof Transition) {
					this.petriNet
							.setTransitions(this.petriNet.getTransitions() - 1);
				}
				this.handleChangeFlags(ChangedFlags.NODE_CHANGED);
			} else {
				BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) element;
				// Pair p = bea.getEdge().getEndpoints();
				// System.out.println(edges.size());
				if (bea.getTo().isCoarseNode() || bea.getFrom().isCoarseNode()) {
					EdgeDeleteDialog dialog = new EdgeDeleteDialog(bea);
					Set<BiologicalEdgeAbstract> delBeas = dialog.getAnswer();
					// aborted
					if (delBeas != null) {
						for (BiologicalEdgeAbstract delBea : delBeas) {
							getRootPathway().deleteSubEdge(delBea);
						}
					}
					return;
				}
				getRootPathway().deleteSubEdge(bea);
				return;
			}
			ids.remove(element.getID());
			// System.out.println(biologicalElements.size());
			biologicalElements.remove(element.getID() + "");
			this.handleChangeFlags(ChangedFlags.EDGE_CHANGED);
			// System.out.println(biologicalElements.size());
		}
	}

	public void removeEdge(BiologicalEdgeAbstract bea, boolean removeID) {
		edges.remove(new Pair<BiologicalNodeAbstract>(bea.getFrom(), bea
				.getTo()));
		graphRepresentation.removeEdge(bea);
		getGraph().removeEdge(bea);
		if (removeID) {
			getRootPathway().getIdSet().remove(bea.getID());
			biologicalElements.remove(bea.getID() + "");
			}
		this.handleChangeFlags(ChangedFlags.EDGE_CHANGED);
	}
	
	public void deleteSubEdge(BiologicalEdgeAbstract edge){
		BiologicalNodeAbstract from = edge.getFrom();
		BiologicalNodeAbstract to = edge.getTo();
		Set<BiologicalNodeAbstract> parentNodes = new HashSet<BiologicalNodeAbstract>();
		parentNodes.addAll(from.getAllParentNodes());
		parentNodes.addAll(to.getAllParentNodes());
		parentNodes.add(from);
		parentNodes.add(to);
		for(BiologicalNodeAbstract parent : parentNodes){
			parent.removeConnectingEdge(edge);
		}
		getRootPathway().updateMyGraph();
	}

	public int edgeGrade(BiologicalEdgeAbstract bea) {
		Set<BiologicalEdgeAbstract> conEdgesTo = new HashSet<BiologicalEdgeAbstract>();
		for(BiologicalEdgeAbstract edge : bea.getFrom().getConnectingEdges()){
			if(edge.isDirected() == bea.isDirected()){
				if(edge.isDirected()){
					if(bea.getTo().getConnectingEdges().contains(edge) && edge.getFrom().getCurrentShownParentNode(getGraph())==bea.getFrom()){
						conEdgesTo.add(edge);
					}
				} else {
					if(bea.getTo().getConnectingEdges().contains(edge)){
						conEdgesTo.add(edge);
					}
				}
			}
		}
//		System.out.println(conEdgesTo.size());
		return conEdgesTo.size();
	}

	/*
	 * public Object getElement(Object graphElement) {
	 * 
	 * if (biologicalElements.get(graphElement) != null) { return
	 * biologicalElements.get(graphElement); } else return null; }
	 */

	public boolean existEdge(BiologicalNodeAbstract from,
			BiologicalNodeAbstract to) {

		return edges.containsKey(new Pair<BiologicalNodeAbstract>(from, to));
	}

	public boolean containsElement(Object graphElement) {
		return biologicalElements.containsValue(graphElement);
	}

	public Set<String> getAllNodeDescriptions() {

		return nodeDescription.keySet();
	}

	public HashSet<String> getAllNodeLabels() {

		HashSet<String> set = new HashSet<String>();
		Iterator<BiologicalNodeAbstract> it = this.getAllNodes().iterator();// biologicalElements.values().iterator();

		while (it.hasNext()) {
			set.add(it.next().getLabel());
		}
		return set;
	}

	public boolean hasGotAtLeastOneElement() {
		if (biologicalElements.size() > 0)
			return true;
		else
			return false;
	}

	public BiologicalNodeAbstract getNodeByName(String name) {

		Iterator<GraphElementAbstract> it = biologicalElements.values()
				.iterator();
		GraphElementAbstract gea;
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			gea = it.next();
			if (gea.isVertex()) {
				bna = (BiologicalNodeAbstract) gea;
				if (bna.getName().equals(name)) {
					return bna;
				}
			}
		}
		return null;
	}

	/*
	 * public BiologicalNodeAbstract getNodeByVertexID(String vertexID) {
	 * 
	 * Iterator it = biologicalElements.values().iterator();
	 * 
	 * while (it.hasNext()) { Object obj = it.next(); GraphElementAbstract gea =
	 * (GraphElementAbstract) obj; if (gea.isVertex()) { BiologicalNodeAbstract
	 * bna = (BiologicalNodeAbstract) obj; if
	 * (bna.getVertex().toString().equals(vertexID)) { return bna; } } } return
	 * null; }
	 */

	public BiologicalNodeAbstract getNodeByLabel(String label) {

		Iterator<GraphElementAbstract> it = biologicalElements.values()
				.iterator();

		GraphElementAbstract gea;
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			gea = it.next();
			if (gea.isVertex()) {
				bna = (BiologicalNodeAbstract) gea;
				if (bna.getLabel().equals(label)) {
					return bna;
				}
			}
		}
		return null;
	}

	public Object getNodeByKEGGEntryID(String id) {

		Iterator<GraphElementAbstract> it = biologicalElements.values().iterator();

		GraphElementAbstract gea;
		while (it.hasNext()) {
			gea = it.next();
			if (gea instanceof BiologicalNodeAbstract) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) gea;
				if (bna.getKEGGnode().getKEGGentryID().equals(id)) {
					return gea;
				}
			}
		}
		return null;
	}

	public HashSet<BiologicalEdgeAbstract> getAllKEGGEdges() {

		Iterator<GraphElementAbstract> it = biologicalElements.values().iterator();
		HashSet<BiologicalEdgeAbstract> set = new HashSet<BiologicalEdgeAbstract>();

		GraphElementAbstract gea;
		while (it.hasNext()) {
			gea = it.next();
			if (gea instanceof BiologicalEdgeAbstract && gea.hasKEGGEdge()) {
				set.add((BiologicalEdgeAbstract)gea);
			}
		}
		return set;
	}

	public HashSet<BiologicalEdgeAbstract> getAllReactionPairEdges() {

		Iterator<GraphElementAbstract> it = biologicalElements.values().iterator();
		HashSet<BiologicalEdgeAbstract> set = new HashSet<BiologicalEdgeAbstract>();

		GraphElementAbstract gea;
		while (it.hasNext()) {
			gea = it.next();
			if (gea instanceof BiologicalEdgeAbstract && gea.hasReactionPairEdge()) {
				set.add((BiologicalEdgeAbstract)gea);
			}
		}
		return set;
	}

	/*
	 * public boolean existEdge(Vertex vertex1, Vertex vertex2) { for (Iterator
	 * i = getAllEdges().iterator(); i.hasNext();) { BiologicalEdgeAbstract bea
	 * = (BiologicalEdgeAbstract) i.next(); if
	 * (bea.getEdge().getEndpoints().getFirst().equals(vertex1) &&
	 * bea.getEdge().getEndpoints().getSecond().equals(vertex2)) return true; }
	 * return false; }
	 */

	public Collection<BiologicalEdgeAbstract> getAllEdges() {

		/*
		 * Iterator<GraphElementAbstract> it =
		 * biologicalElements.values().iterator();
		 * HashSet<BiologicalEdgeAbstract> set = new
		 * HashSet<BiologicalEdgeAbstract>();
		 * 
		 * GraphElementAbstract gea; while (it.hasNext()) { gea = it.next(); if
		 * (gea instanceof BiologicalEdgeAbstract) {
		 * set.add((BiologicalEdgeAbstract)gea); } } return set;
		 */
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

	public Collection<BiologicalNodeAbstract> getAllNodes() {

		/*
		 * Iterator<GraphElementAbstract> it =
		 * biologicalElements.values().iterator();
		 * HashSet<BiologicalNodeAbstract> set = new
		 * HashSet<BiologicalNodeAbstract>();
		 * 
		 * while (it.hasNext()) { Object obj = it.next(); GraphElementAbstract
		 * gea = (GraphElementAbstract) obj; if (gea.isVertex()) {
		 * set.add((BiologicalNodeAbstract)obj); } }
		 * 
		 * return set;
		 */
		if (getGraph(false) == null) {
			return Collections.emptyList();
		}
		return getGraph().getAllVertices();
	}

	public List<BiologicalNodeAbstract> getAllNodesSorted() {

		HashMap<Integer, BiologicalNodeAbstract> map = new HashMap<Integer, BiologicalNodeAbstract>();

		for (BiologicalNodeAbstract bna : this.getAllNodes()) {
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
	
	public List<BiologicalNodeAbstract> getAllNodesSortedAlphabetically() {

		HashMap<String, BiologicalNodeAbstract> map = new HashMap<String, BiologicalNodeAbstract>();

		for (BiologicalNodeAbstract bna : this.getAllNodes()) {
			map.put(bna.getName(), bna);
		}

		ArrayList<String> ids = new ArrayList<String>(map.keySet());
		Collections.sort(ids);

		List<BiologicalNodeAbstract> sortedList = new ArrayList<BiologicalNodeAbstract>();
		for (int i = 0; i < ids.size(); i++) {
			sortedList.add(map.get(ids.get(i)));
		}

		return sortedList;
	}

	public Vector<BiologicalNodeAbstract> getAllNodesAsVector() {

		Iterator<GraphElementAbstract> it = biologicalElements.values().iterator();
		Vector<BiologicalNodeAbstract> set = new Vector<BiologicalNodeAbstract>();

		GraphElementAbstract gea;
		while (it.hasNext()) {
			gea = it.next();
			if (gea instanceof BiologicalNodeAbstract) {
				set.add((BiologicalNodeAbstract)gea);
			}
		}

		return set;
	}

	public Vector<BiologicalEdgeAbstract> getAllEdgesAsVector() {

		Iterator<GraphElementAbstract> it = biologicalElements.values().iterator();
		Vector<BiologicalEdgeAbstract> set = new Vector<BiologicalEdgeAbstract>();

		GraphElementAbstract gea;
		while (it.hasNext()) {
			gea = it.next();
			if (gea instanceof BiologicalEdgeAbstract) {
				set.add((BiologicalEdgeAbstract)gea);
			}
		}

		return set;
	}

	public Vector<BiologicalNodeAbstract> getSelectedNodes() {
		Vector<BiologicalNodeAbstract> ve = new Vector<BiologicalNodeAbstract>();
		Iterator<BiologicalNodeAbstract> it = getGraph()
				.getVisualizationViewer().getPickedVertexState().getPicked()
				.iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract v = it.next();
			ve.add(v);
		}
		return ve;
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
							node2Refs.put(bna.getRef(),
									new HashSet<BiologicalNodeAbstract>());
						}
						node2Refs.get(bna.getRef()).add(bna);
					}
				} else {
					newNodes.add(bna);
				}
			}
			Iterator<BiologicalNodeAbstract> refs = node2Refs.keySet()
					.iterator();

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
					System.err
							.print("Node with id: "
									+ bna.getID()
									+ " and name: "
									+ bna.getName()
									+ " cannot be merged due to unresolved references!");
				}
			}

			if (n.size() > 1) {
				this.mergeNodes(n.iterator().next(), n);
				merged = true;

			}
			if (merged) {
				MainWindow mw = MainWindowSingleton.getInstance();
				mw.updateElementTree();
				mw.updateElementProperties();

				this.graph.getVisualizationViewer().repaint();
			}

		}

	}

	private void mergeNodes(BiologicalNodeAbstract first,
			Set<BiologicalNodeAbstract> nodes) {
		Iterator<BiologicalNodeAbstract> it = nodes.iterator();
		BiologicalNodeAbstract bna;
		BiologicalEdgeAbstract bea;
		Iterator<BiologicalEdgeAbstract> itEdges;

		while (it.hasNext()) {

			bna = it.next();
			if (bna != first && bna.getParentNode()==first.getParentNode()) {

				Set<BiologicalEdgeAbstract> edges = new HashSet<BiologicalEdgeAbstract>();
				edges.addAll(bna.getConnectingEdges());
				itEdges = edges.iterator();
				// System.out.println(graph.getJungGraph().getInEdges(bna).size());

				while (itEdges.hasNext()) {
					bea = itEdges.next();
					if(bea.getFrom()==bna || bea.getTo()==bna){
						deleteSubEdge(bea);
						if(bea.getTo()==bna){
							bea.setTo(first);
						} else if(bea.getFrom()==bna){
							bea.setFrom(first);
						}
						this.addEdge(bea);
					}
				}
				first.addLabel(bna.getLabelSet());
				if (bna.hasRef() && bna.getRef() == first) {
					first.getRefs().remove(bna);
				}
				this.removeElement(bna);
				// System.out.println("merged:");
				// Iterator<String> itString =
				// first.getLabelSet().iterator();
				// while(itString.hasNext()){
				// System.out.println(itString.next());
			}
		}
		updateMyGraph();
		this.graph.getVisualizationViewer().getPickedVertexState().clear();
		this.graph.getVisualizationViewer().getPickedEdgeState().clear();
		this.graph.getVisualizationViewer().getPickedVertexState()
				.pick(first, true);
		// System.out.println("labels: " + first.getLabelSet().size());
	}

	public void splitNode(Set<BiologicalNodeAbstract> nodes) {
		BiologicalNodeAbstract bna;
		Iterator<BiologicalNodeAbstract> it = nodes.iterator();
		BiologicalEdgeAbstract bea;
		Iterator<BiologicalEdgeAbstract> itEdges;
		BiologicalNodeAbstract newBNA;
		Point2D p = new Point2D.Double(0,0);

		while (it.hasNext()) {
			bna = it.next();

			if (this.graph.getJungGraph().getNeighborCount(bna) > 1) {

				// edges = this.graph.getJungGraph().getInEdges(bna);
				Set<BiologicalEdgeAbstract> edges = new HashSet<BiologicalEdgeAbstract>();
				edges.addAll(bna.getConnectingEdges());
				itEdges = edges.iterator();
				// System.out.println(graph.getJungGraph().getInEdges(bna).size());

				while (itEdges.hasNext()
						&& this.graph.getJungGraph().getNeighborCount(bna) > 1) {
					bea = itEdges.next();
						deleteSubEdge(bea);
						newBNA = bna.clone();
						newBNA.setID();
						newBNA.setRefs(new HashSet<BiologicalNodeAbstract>());
						newBNA.setRef(bna);
						if(bea.getTo()==bna){
							bea.setTo(newBNA);
							p = this.getGraph().getVertexLocation(bea.getFrom());
						} else if(bea.getFrom()==bna){
							bea.setFrom(newBNA);
							p = this.getGraph().getVertexLocation(bea.getTo());
						}
						this.addVertex(newBNA, new Point2D.Double(
								p.getX() + 20, p.getY() + 20));
						this.addEdge(bea);
						graph.getVisualizationViewer().getPickedVertexState()
								.pick(newBNA, true);
				}
				// System.out.println(graph.getJungGraph().getOutEdges(bna).size());
			}
		}
		updateMyGraph();
		MainWindow mw = MainWindowSingleton.getInstance();
		mw.updateElementTree();
		mw.updateElementProperties();

		this.graph.getVisualizationViewer().repaint();
	}

	/**
	 * Reset Element lists.
	 */
	public void clearElements() {
		biologicalElements.clear();
		set.clear();
		edges.clear();
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

	public PetriNet getPetriNet() {
		return petriNet;
	}

	public boolean isPetriNet() {
		return isPetriNet;
	}

	public void setPetriNet(boolean isPetriNet) {
		this.isPetriNet = isPetriNet;
	}

	public void setGraph(MyGraph graph) {
		this.graph = graph;
	}

	public void setDAWISProject() {
		isDAWISProject = true;
	}

	public boolean isDAWISProject() {
		return isDAWISProject;
	}

	public String getImagePath() {
		return ImagePath;
	}

	public void setImagePath(String imagePath) {
		ImagePath = imagePath;
	}

	public File getFilename() {
		return filename;
	}

	public void setFilename(File filename) {
		this.filename = filename;
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
		this.name = name;
		if (tab != null) {
			tab.setTitle(name);
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

	public HashMap<String, GraphElementAbstract> getBiologicalElements() {
		return biologicalElements;
	}

	public FilterSettings getFilterSettings() {
		return filterSettings;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
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

	public InternalGraphRepresentation getGraphRepresentation() {
		return graphRepresentation;
	}

	public void setSettings(boolean[] set) {
		settings = set;
	}

	public String[] getSettingsAsString() {
		String[] set = new String[11];
		for (int i = 0; i < 11; i++) {
			set[i] = settings[i] + "";
		}
		return set;
	}

	public boolean[] getSettings() {
		return settings;
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

	public void setPetriNetSimulation(boolean isPetriNetSimulation) {
		this.isPetriNetSimulation = isPetriNetSimulation;
	}

	public boolean isPetriNetSimulation() {
		return isPetriNetSimulation;
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
		Iterator<BiologicalNodeAbstract> it = getAllNodes().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = it.next();
			if (bna instanceof PathwayMap
					&& ((PathwayMap) bna).getPathwayLink() != null)
				result.add(((PathwayMap) bna).getPathwayLink());
		}
		return result;
	}

	public SortedSet<Integer> getIdSet() {
		if(getRootPathway()==this)
			return this.ids;
		return getRootPathway().getIdSet();
	}

	/**
	 * Updates the current Graph after coarsing of nodes. The node that calls
	 * the method is added, all nodes contained in this node are removed. Edges
	 * are updated respectively (changed from/to for border-environment edges,
	 * removed automatically for all 'inner' edges)
	 * 
	 * @author tloka
	 */
	public void updateMyGraph() {

		// to differ between bna and pathway objects.
		BiologicalNodeAbstract thisNode = null;
		if (this.isBNA()) {
			thisNode = (BiologicalNodeAbstract) this;
//			System.out.println(thisNode.getLabel());
		}

		// go through all nodes in the current Pathway
		Set<BiologicalNodeAbstract> nodeSet = new HashSet<BiologicalNodeAbstract>();
		if (this.hasGraph()) {
			nodeSet.addAll(openedSubPathways.keySet());
			nodeSet.addAll(getGraph().getAllVertices());
		}

		Iterator<BiologicalNodeAbstract> it = nodeSet.iterator();
		BiologicalNodeAbstract node;
		BiologicalNodeAbstract parentNode;
		while (it.hasNext()) {
			node = it.next();
			
			switch (node.getStateChanged()) {

			case UNCHANGED:
				break;

			case FLATTENED:
				this.removeElement(node);
				// root Pathway and non-environment nodes add complete
				// sub-pathway.
				if (!isBNA() || !thisNode.getEnvironment().contains(node)) {
					for (BiologicalNodeAbstract n : node.getInnerNodes()) {
						this.addVertex(n, node.getGraph().getVertexLocation(n));
					}
				}

				// environment nodes add only correct Subnodes and connecting
				// edges.
				else if (thisNode.getEnvironment().contains(node)) {
					for (BiologicalEdgeAbstract edge : node
							.getConnectingEdges()) {
						BiologicalEdgeAbstract newEdge = edge.clone();
						BiologicalNodeAbstract newVertex;
						if (thisNode.getBorder().contains(
								edge.getTo().getCurrentShownParentNode(
										getGraph()))) {
							newEdge.setTo(newEdge.getTo()
									.getCurrentShownParentNode(getGraph()));
							newEdge.setFrom(newEdge.getFrom()
									.getCurrentShownParentNode(node.getGraph()));
							newVertex = newEdge.getFrom();
						} else if (thisNode.getBorder().contains(
								edge.getFrom().getCurrentShownParentNode(
										getGraph()))) {
							newEdge.setFrom(newEdge.getFrom()
									.getCurrentShownParentNode(getGraph()));
							newEdge.setTo(newEdge.getTo()
									.getCurrentShownParentNode(node.getGraph()));
							newVertex = newEdge.getTo();
						} else {
							continue;
						}
						if (newEdge.isValid(false)) {
							thisNode.addVertex(newVertex, node.getGraph()
									.getVertexLocation(newVertex));
							thisNode.getEnvironment().add(newVertex);
						}
					}
					thisNode.getEnvironment().remove(node);
				}
				
				break;

			case COARSED:
				parentNode = node.getParentNode();
				// if already added in correct graph, break.
				if (parentNode == this) {
					if(getGraph().getLayout() instanceof HEBLayout){
						((HEBLayout) getGraph().getLayout()).addToOrder(parentNode);
					}
					break;
				}
				// Coarse nodes. Location is copied by a border node if possible.
				if ((this.isBNA() && !thisNode.getEnvironment().contains(node)) || !this.isBNA()) {
					Point2D loc;
					if(parentNode.getRootNode()!=null && getGraph().getAllVertices().contains(parentNode.getRootNode())){
						loc = getGraph().getVertexLocation(parentNode.getRootNode());
					} else {
						loc = parentNode.getBorder().isEmpty() ? getGraph().getVertexLocation(node)
								: getGraph().getVertexLocation(parentNode.getBorder().iterator().next());
					}
					addVertex(parentNode, loc);
					removeElement(node);
				}

				if(getGraph().getLayout() instanceof HEBLayout){
					((HEBLayout) getGraph().getLayout()).addToOrder(parentNode);
				}
				parentNode.setCoarseNodesize();
				break;

			case CONNECTIONMODIFIED:

				break;

			case DELETED:
				removeElement(node);
				break;

			default:
				break;
			}
			
			// update all included subpathways
			if (thisNode == null) {
				node.updateMyGraph();
			} else if (!thisNode.getEnvironment().contains(node)) {
				node.updateMyGraph();
			}

		}

		if(getGraph(false)!=null){
			if (isBNA() && thisNode.isCoarseNode()) {
				thisNode.updateHierarchicalAttributes();
			}
			updateEdges();
			if (isRootPathway()) {
				markPathwayUnchanged();
				MainWindowSingleton.getInstance().updateElementTree();
			}
			getGraph().updateLayout();
		}
	}

	/**
	 * Add a Set of Edges to the Pathway. Used to draw connecting Edges of
	 * coarsed Subnodes.
	 */
	protected void updateEdges() {
		
		BiologicalNodeAbstract thisNode = this instanceof BiologicalNodeAbstract ? (BiologicalNodeAbstract) this : null;
		Set<BiologicalEdgeAbstract> edges = new HashSet<BiologicalEdgeAbstract>();
		edges.addAll(getAllEdges());
		for(BiologicalEdgeAbstract edge : edges){
			removeEdge(edge, true);
		}
		Set<BiologicalNodeAbstract> innerNodes = new HashSet<BiologicalNodeAbstract>();
		if(thisNode==null){
			innerNodes.addAll(getAllNodes());
		} else {
			innerNodes.addAll(thisNode.getInnerNodes());
		}
		
		for(BiologicalNodeAbstract node : innerNodes){
			for (BiologicalEdgeAbstract connectingEdge : node.getConnectingEdges()) {
				BiologicalEdgeAbstract newEdge = connectingEdge.clone();
				newEdge.setTo(newEdge.getTo().getCurrentShownParentNode(getGraph()));
				newEdge.setFrom(newEdge.getFrom().getCurrentShownParentNode(getGraph()));
				
				if (newEdge.isValid(false))
					drawEdge(newEdge);
			}
		}
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

	public void markPathwayUnchanged() {
		Collection<BiologicalNodeAbstract> nodes = getAllNodes();
		if (isBNA()) {
			nodes = ((BiologicalNodeAbstract) this).getInnerNodes();
		}
		for (BiologicalNodeAbstract node : nodes) {
			node.setStateChanged(NodeStateChanged.UNCHANGED);
			if (node.isCoarseNode()) {
				node.markPathwayUnchanged();
			}
		}
	}
	
	/**
	 * Opens a coarse node in the Pathway without flattening it in data structure.
	 * @param subPathway Node to be opened
	 * @return false, if opening action is not possible. true, if node was opened.
	 * @author tloka
	 */
	public boolean openSubPathway(BiologicalNodeAbstract subPathway){
		if(!subPathway.isCoarseNode() || subPathway.getInnerNodes().size()==0){
			return false;
		}
		if((this instanceof BiologicalNodeAbstract && 
				((BiologicalNodeAbstract) this).getEnvironment().contains(subPathway))){
			return false;
		}
		Point2D location = getGraph().getVertexLocation(subPathway);
		removeElement(subPathway);
		Color color;
		for(BiologicalNodeAbstract node : subPathway.getInnerNodes()){
			addVertex(node, this.getGraph().getVertexLocation(node));
			color = node.getColor();
			node.setColor(new Color((int) Math.min(255,color.getRed()*1.2f),(int) Math.min(255,(int) color.getGreen()*1.2f),(int) Math.min(255,(int) color.getBlue()*1.2f)));
		}
//		for(BiologicalEdgeAbstract edge : subPathway.getConnectingEdges()){
//			BiologicalEdgeAbstract e = edge.clone();
//			e.setTo(e.getTo().getCurrentShownParentNode(getGraph()));
//			e.setFrom(e.getFrom().getCurrentShownParentNode(getGraph()));
//			if(e.isValid(false)){
//				addEdge(e);
//			}
//		}
//		for(BiologicalEdgeAbstract edge : subPathway.getAllEdges()){
//			if(!subPathway.getEnvironment().contains(edge.getTo()) && 
//					!subPathway.getEnvironment().contains(edge.getFrom())){
//				addEdge(edge);
//			}
//		}
		updateEdges();
		openedSubPathways.put(subPathway,location);
		return true;
	}
	
	/**
	 * Opens all coarse nodes (including sub-coarsenodes) in the Pathway 
	 * without flattening it in data structure.
	 * @author tloka
	 */
	public void openAllSubPathways(){
		
		Set<BiologicalNodeAbstract> nodes = new HashSet<BiologicalNodeAbstract>();
		boolean repeat = true;
		
		while(repeat){
			repeat = false;
			nodes.clear();
			nodes.addAll(getAllNodes());
			for(BiologicalNodeAbstract n : nodes){
				if(openSubPathway(n))
					repeat = true;
			}
		}
	}
	
	/**
	 * Closes a coarse node in the Pathway.
	 * @param subPathway The node to be closed.
	 * @author tloka
	 */
	public void closeSubPathway(BiologicalNodeAbstract subPathway){
		if(!openedSubPathways.keySet().contains(subPathway)){
			return;
		}
		
		addVertex(subPathway, openedSubPathways.get(subPathway));

		Color color;
		for(BiologicalNodeAbstract node : subPathway.getInnerNodes()){
			closeSubPathway(node);
			color = node.getColor();
			node.setColor(new Color((int) Math.min(255,(int) color.getRed()/1.2f),(int) Math.min(255,(int) color.getGreen()/1.2f),(int) Math.min(255,(int) color.getBlue()/1.2f)));
			removeElement(node);
		}
//		for(BiologicalEdgeAbstract edge : subPathway.getConnectingEdges()){
//			BiologicalEdgeAbstract e = edge.clone();
//			e.setTo(e.getTo().getCurrentShownParentNode(getGraph()));
//			e.setFrom(e.getFrom().getCurrentShownParentNode(getGraph()));
//			if(e.isValid(false)){
//				addEdge(e);
//			}
//		}
		updateEdges();
		openedSubPathways.remove(subPathway);
	}
	
	/**
	 * Closes all coarse nodes in the Pathway.
	 * @author tloka
	 */
	public void closeAllSubPathways(){
		HashSet<BiologicalNodeAbstract> osp = new HashSet<BiologicalNodeAbstract>();
		while(!openedSubPathways.isEmpty()){
			osp.addAll(openedSubPathways.keySet());
			HashSet<BiologicalNodeAbstract> innerNodes = new HashSet<BiologicalNodeAbstract>();
			for(BiologicalNodeAbstract n : osp){
				innerNodes.addAll(n.getInnerNodes());
				innerNodes.retainAll(openedSubPathways.keySet());
				if(innerNodes.isEmpty()){
					closeSubPathway(n);
				}
				innerNodes.clear();
			}
			if(openedSubPathways.size()==osp.size()){
				break;
			}
		}
	}

	public Set<BiologicalNodeAbstract> getOpenedSubPathways() {
		return openedSubPathways.keySet();
	}
	
	public Point2D getOpenedSubPathwayLocation(BiologicalNodeAbstract n){
		if(getAllNodes().contains(n))
			return graph.getVertexLocation(n);
		if(getOpenedSubPathways().contains(n))
			return openedSubPathways.get(n);
		return null;
	}
	
	public BiologicalNodeAbstract getRootNode(){
		if(rootNode != null && getAllNodes().contains(rootNode))
			return rootNode;
		if(getRootPathway()==this && getAllNodes().contains(rootNode)){
			return rootNode;
		} else if(getRootPathway()==this){
			return null;
		}
		return getRootPathway().getRootNode();
	}
	
	public void setRootNode(BiologicalNodeAbstract node){
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

	public void setChangedParameters(
			HashMap<Parameter, GraphElementAbstract> changedParameters) {
		this.changedParameters = changedParameters;
	}

	public HashMap<Place, Double> getChangedInitialValues() {
		return changedInitialValues;
	}

	public void setChangedInitialValues(
			HashMap<Place, Double> changedInitialValues) {
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
		Iterator<BiologicalNodeAbstract> it = getAllNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();

			p = getGraph().getVertexLocation(bna);

			graph.getVisualizationViewer()
					.getModel()
					.getGraphLayout()
					.setLocation(
							bna,
							new Point2D.Double(p.getX() * factor, p.getY()
									* factor));
		}
	}
}
