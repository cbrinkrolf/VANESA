package graph.algorithms.alignment;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
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
import biologicalObjects.edges.Repression;
import biologicalObjects.edges.StateChange;
import biologicalObjects.edges.Ubiquitination;
import biologicalObjects.nodes.BRENDANode;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Complex;
import biologicalObjects.nodes.CompoundNode;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Degraded;
import biologicalObjects.nodes.Disease;
import biologicalObjects.nodes.Domain;
import biologicalObjects.nodes.Drug;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Glycan;
import biologicalObjects.nodes.HomodimerFormation;
import biologicalObjects.nodes.KEGGNode;
import biologicalObjects.nodes.LigandBinding;
import biologicalObjects.nodes.MRNA;
import biologicalObjects.nodes.MembraneChannel;
import biologicalObjects.nodes.OrthologGroup;
import biologicalObjects.nodes.Other;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;
import biologicalObjects.nodes.Receptor;
import biologicalObjects.nodes.SRNA;
import biologicalObjects.nodes.SmallMolecule;
import biologicalObjects.nodes.SolubleReceptor;
import biologicalObjects.nodes.TranscriptionFactor;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.utils.UserData;
import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.RangeSelector;

public class SimpleAlignmentGraph {

	private double edgeThreshold;
	private GraphAlignmentAlgorithms alignment;
	private AdjacencyMatrix graphA, graphB;
	private HashMap<String, Integer> graphA_id2position, graphB_id2position;

	private Pathway pw_one;
	private Pathway pw_two;
	private Pathway pw_new;
	private double global_shiftX = 0;
	private Hashtable<String, Vertex> verticesPW1 = new Hashtable();
	private Hashtable<String, Vertex> verticesPW2 = new Hashtable();
	private Hashtable<String, Vertex> namesPW1 = new Hashtable();
	private Hashtable<String, Vertex> namesPW2 = new Hashtable();
	private Hashtable<String, Vertex> allNodes = new Hashtable();

	private HashSet<Edge> alignmentEdgeSet = new HashSet<Edge>();

	private MyGraph graph;

	private String alignmentName;
	private Double spaceBetweenGraphs;

	private double xMax1, yMax1, xMax2, yMax2;
	private double xMin1, yMin1, xMin2, yMin2;
	private Map<String, String> backgroundAttributes1, backgroundAttributes2;

	public SimpleAlignmentGraph(Pathway pwA, Pathway pwB, String name) {

		this.pw_one = pwA;
		this.pw_two = pwB;
		this.alignmentName = name;
		spaceBetweenGraphs = 100.0;
		initNewGraph();

	}

	public SimpleAlignmentGraph(Pathway pwA, Pathway pwB, String name,
			GraphAlignmentAlgorithms ali, double threshold) {

		this.edgeThreshold = threshold;
		this.pw_one = pwA;
		this.pw_two = pwB;
		this.alignmentName = name;
		this.alignment = ali;
		this.graphA = ali.getGraphA();
		this.graphB = ali.getGraphB();
		this.graphA_id2position = graphA.getId2position();
		this.graphB_id2position = graphB.getId2position();

	}

	private void addAlignmentEdges() {

		for (String v1ID : graphA_id2position.keySet()) {

			int posV1 = graphA_id2position.get(v1ID).intValue();

			for (String v2ID : graphB_id2position.keySet()) {

				int posV2 = graphB_id2position.get(v2ID).intValue();

				// System.out.print(v1ID + " - " + v2ID + " = ");
				double aliScore = alignment.getSolutionMatrix().get(posV1,
						posV2);
				double eValue = alignment.getSimilarity().getMatrix()
						.get(posV1, posV2);
				// eValue = Math.pow(10, -eValue);
				// System.out.println(aliScore);

				if (aliScore >= edgeThreshold) {

					Vertex v1 = verticesPW1.get(v1ID);
					Vertex v2 = verticesPW2.get(v2ID);
					Edge edge = new UndirectedSparseEdge(v1, v2);
					edge.addUserDatum("alignment", new Double(aliScore),
							UserData.CLONE);
					edge.addUserDatum("eValue", new Double(eValue),
							UserData.CLONE);

					// pw_new.getGraphRepresentation().addEdge(v1, v2, edge);
					AlignmentEdge aliEdge = new AlignmentEdge(edge);
					pw_new.getGraph().addEdge(aliEdge);
					alignmentEdgeSet.add(edge);
					// System.out.println("ADDED");
				}

			}

		}

	}

	private void initNewGraph() {

		pw_new = new CreatePathway(alignmentName).getPathway();
		pw_new.setOrganism("");
		pw_new.setLink("");

		pw_new.getGraph().lockVertices();
		pw_new.getGraph().stopVisualizationModel();

		graph = pw_new.getGraph();

		xMin1 = yMin1 = xMin2 = yMin2 = Double.MAX_VALUE;
		xMax1 = yMax1 = xMax2 = yMax2 = 0;

		Iterator it = pw_one.getAllNodes().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) it.next();
			// BiologicalNodeAbstract newBNA = bna.deepCopy();
			addNode(bna, pw_one, false);
		}
		this.backgroundAttributes1 = new HashMap<String, String>();
		this.backgroundAttributes1.put("title", pw_one.getTitle());
		this.backgroundAttributes1.put("titlePos", "100");
		this.backgroundAttributes1.put("fillColor", "16777113");
		this.backgroundAttributes1.put("outlineColor", "1");
		this.backgroundAttributes1.put("textColor", "1");
		this.backgroundAttributes1.put("outlineType", "0");
		this.backgroundAttributes1.put("isEllipse", "false");
		this.backgroundAttributes1.put("minX", xMin1 - 50 + "");
		this.backgroundAttributes1.put("maxX", xMax1 + 50 + "");
		this.backgroundAttributes1.put("minY", yMin1 - 50 + "");
		this.backgroundAttributes1.put("maxY", yMax1 + 50 + "");

		it = pw_one.getAllEdges().iterator();
		while (it.hasNext()) {
			BiologicalEdgeAbstract bna = (BiologicalEdgeAbstract) it.next();
			addEdge(bna, true);
		}

		it = pw_two.getAllNodes().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) it.next();
			addNode(bna, pw_two, true);
		}
		this.backgroundAttributes2 = new HashMap<String, String>();
		this.backgroundAttributes2.put("title", pw_two.getTitle());
		this.backgroundAttributes2.put("titlePos", "100");
		this.backgroundAttributes2.put("fillColor", "13434777");
		this.backgroundAttributes2.put("outlineColor", "1");
		this.backgroundAttributes2.put("textColor", "1");
		this.backgroundAttributes2.put("outlineType", "0");
		this.backgroundAttributes2.put("isEllipse", "false");
		this.backgroundAttributes2.put("minX", xMin2 - 50 + "");
		this.backgroundAttributes2.put("maxX", xMax2 + 50 + "");
		this.backgroundAttributes2.put("minY", yMin2 - 50 + "");
		this.backgroundAttributes2.put("maxY", yMax2 + 50 + "");

		it = pw_two.getAllEdges().iterator();
		while (it.hasNext()) {
			BiologicalEdgeAbstract bna = (BiologicalEdgeAbstract) it.next();
			addEdge(bna, false);
		}

	}

	private void addNode(BiologicalNodeAbstract bna, Pathway pw,
			Boolean secondPathway) {

		String biologicalElement = bna.getBiologicalElement();
		String label = bna.getLabel();
		String name = bna.getName();
		String comment = bna.getComments();

		String sequence = "";
		if (bna instanceof Protein) {
			Protein e = (Protein) bna;
			sequence = e.getAaSequence();
		} else if (bna instanceof DNA) {
			DNA dna = (DNA) bna;
			sequence = dna.getNtSequence();
		} else if (bna instanceof RNA) {
			RNA rna = (RNA) bna;
			sequence = rna.getNtSequence();
		}

		Color color = bna.getColor();

		Point2D p = pw.getGraph().getClusteringLayout()
				.getLocation(bna.getVertex());
		Double x_coord = p.getX();
		Double y_coord = p.getY();

		Boolean isReference = bna.isReference();
		Object obj = null;
		BRENDANode brendaNode = null;
		KEGGNode keggNode = null;

		if (secondPathway) {
			x_coord = x_coord + global_shiftX + spaceBetweenGraphs;

			if (x_coord > xMax2)
				xMax2 = x_coord;
			if (x_coord < xMin2)
				xMin2 = x_coord;
			if (y_coord > yMax2)
				yMax2 = y_coord;
			if (y_coord < yMin2)
				yMin2 = y_coord;
		} else {
			if (global_shiftX < x_coord) {
				global_shiftX = x_coord;
			}
			if (x_coord > xMax1)
				xMax1 = x_coord;
			if (x_coord < xMin1)
				xMin1 = x_coord;
			if (y_coord > yMax1)
				yMax1 = y_coord;
			if (y_coord < yMin1)
				yMin1 = y_coord;
		}

		if (biologicalElement.equals(Elementdeclerations.enzyme)) {
			Enzyme e = new Enzyme(label, name, pw_new.getGraph()
					.createNewVertex());
			e.setAaSequence(sequence);
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.others)) {
			Other e = new Other(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.complex)) {
			Complex e = new Complex(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.degraded)) {
			Degraded e = new Degraded(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.dna)) {
			DNA e = new DNA(label, name, pw_new.getGraph().createNewVertex());
			e.setNtSequence(sequence);
			obj = e;

		} else if (biologicalElement
				.equals(Elementdeclerations.homodimerFormation)) {
			HomodimerFormation e = new HomodimerFormation(label, name, pw_new
					.getGraph().createNewVertex());
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.ligandBinding)) {
			LigandBinding e = new LigandBinding(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;

		} else if (biologicalElement
				.equals(Elementdeclerations.membraneChannel)) {
			MembraneChannel e = new MembraneChannel(label, name, pw_new
					.getGraph().createNewVertex());
			e.setAaSequence(sequence);
			obj = e;

		} else if (biologicalElement
				.equals(Elementdeclerations.membraneReceptor)) {
			Receptor e = new Receptor(label, name, pw_new.getGraph()
					.createNewVertex());
			e.setAaSequence(sequence);
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.mRNA)) {
			MRNA e = new MRNA(label, name, pw_new.getGraph().createNewVertex());
			e.setNtSequence(sequence);
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.orthologGroup)) {
			OrthologGroup e = new OrthologGroup(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.pathwayMap)) {
			PathwayMap e = new PathwayMap(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.protein)) {
			Protein e = new Protein(label, name, pw_new.getGraph()
					.createNewVertex());
			e.setAaSequence(sequence);
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.receptor)) {
			Receptor e = new Receptor(label, name, pw_new.getGraph()
					.createNewVertex());
			e.setAaSequence(sequence);
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.sRNA)) {
			SRNA e = new SRNA(label, name, pw_new.getGraph().createNewVertex());
			e.setNtSequence(sequence);
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.smallMolecule)) {
			SmallMolecule e = new SmallMolecule(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;

		} else if (biologicalElement
				.equals(Elementdeclerations.solubleReceptor)) {
			SolubleReceptor e = new SolubleReceptor(label, name, pw_new
					.getGraph().createNewVertex());
			e.setAaSequence(sequence);
			obj = e;

		} else if (biologicalElement
				.equals(Elementdeclerations.transcriptionFactor)) {
			TranscriptionFactor e = new TranscriptionFactor(label, name, pw_new
					.getGraph().createNewVertex());
			e.setAaSequence(sequence);
			obj = e;

		} else if (biologicalElement.equals(Elementdeclerations.glycan)) {
			Glycan e = new Glycan(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;
		} else if (biologicalElement.equals(Elementdeclerations.disease)) {
			Disease e = new Disease(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;
		} else if (biologicalElement.equals(Elementdeclerations.drug)) {
			Drug e = new Drug(label, name, pw_new.getGraph().createNewVertex());
			obj = e;
		} else if (biologicalElement.equals(Elementdeclerations.compound)) {
			CompoundNode e = new CompoundNode(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;
		} else if (biologicalElement.equals(Elementdeclerations.domain)) {
			Domain e = new Domain(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;
		} else if (biologicalElement.equals(Elementdeclerations.pathwayNode)) {
			PathwayMap e = new PathwayMap(label, name, pw_new.getGraph()
					.createNewVertex());
			obj = e;
		}

		BiologicalNodeAbstract bna_new = (BiologicalNodeAbstract) obj;

		bna_new.setCompartment(bna.getCompartment());
		bna_new.setComments(comment);
		bna_new.setColor(color);
		bna_new.setIsVertex(true);
		bna_new.setAbstract(false);
		bna_new.setReference(isReference);

		if (keggNode != null) {
			bna_new.setKEGGnode(keggNode);
		}

		pw_new.addElement(obj);
		pw_new.getGraph().moveVertex(bna_new.getVertex(), x_coord, y_coord);
		allNodes.put(bna_new.getName(), bna_new.getVertex());

		if (secondPathway) {
			verticesPW2.put(bna.getVertex().toString(), bna_new.getVertex());
			namesPW2.put(bna_new.getName(), bna_new.getVertex());
		} else {
			verticesPW1.put(bna.getVertex().toString(), bna_new.getVertex());
			namesPW1.put(bna_new.getName(), bna_new.getVertex());
		}
	}

	private void addEdge(BiologicalEdgeAbstract bna, boolean firstPathway) {

		String elementSpecification = bna.getBiologicalElement();
		String name = bna.getName();
		String label = bna.getLabel();
		String from = bna.getEdge().getEndpoints().getFirst().toString();
		String to = bna.getEdge().getEndpoints().getSecond().toString();

		boolean reference = bna.isReference();
		boolean directed = bna.isDirected();

		String comment = bna.getComments();
		Color color = bna.getColor();
		Object graphElement = null;

		Vertex fromVertex = null;
		Vertex toVertex = null;

		if (firstPathway) {
			fromVertex = verticesPW1.get(from);
			toVertex = verticesPW1.get(to);
		} else {
			fromVertex = verticesPW2.get(from);
			toVertex = verticesPW2.get(to);
		}

		if (elementSpecification.equals(Elementdeclerations.compoundEdge)) {

			graphElement = new Compound(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.hiddenCompoundEdge)) {

			graphElement = new HiddenCompound(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);
		} else if (elementSpecification
				.equals(Elementdeclerations.reactionEdge)) {

			graphElement = new ReactionEdge(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.activationEdge)) {

			graphElement = new Activation(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.inhibitionEdge)) {

			graphElement = new Inhibition(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.expressionEdge)) {

			graphElement = new Expression(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.repressionEdge)) {

			graphElement = new Repression(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.indirectEffectEdge)) {

			graphElement = new IndirectEffect(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.stateChangeEdge)) {

			graphElement = new StateChange(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification.equals(Elementdeclerations.bindingEdge)) {

			graphElement = new BindingAssociation(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.dissociationEdge)) {

			graphElement = new Dissociation(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.phosphorylationEdge)) {

			graphElement = new Phosphorylation(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);
		} else if (elementSpecification
				.equals(Elementdeclerations.dephosphorylationEdge)) {

			graphElement = new Dephosphorylation(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.glycosylationEdge)) {

			graphElement = new Glycosylation(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.ubiquitinationEdge)) {

			graphElement = new Ubiquitination(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.methylationEdge)) {

			graphElement = new Methylation(pw_new.getGraph().createEdge(
					fromVertex, toVertex, directed), label, name);

		} else if (elementSpecification
				.equals(Elementdeclerations.physicalInteraction)) {

			graphElement = new PhysicalInteraction(pw_new.getGraph()
					.createEdge(fromVertex, toVertex, directed), label, name);

		} else {

			graphElement = new PhysicalInteraction(pw_new.getGraph()
					.createEdge(fromVertex, toVertex, directed), label, name);

		}

		((BiologicalEdgeAbstract) graphElement).setDirected(directed);
		((BiologicalEdgeAbstract) graphElement).setColor(color);
		((BiologicalEdgeAbstract) graphElement).setComments(comment);
		((BiologicalEdgeAbstract) graphElement).setReference(reference);

		pw_new.addElement(graphElement);
	}

	private void drawGraph() {

		MainWindow w = MainWindowSingelton.getInstance();

		pw_new.getGraph().unlockVertices();
		pw_new.getGraph().restartVisualizationModel();
		MainWindowSingelton.getInstance().updateProjectProperties();
		MainWindowSingelton.getInstance().updateOptionPanel();

		graph = pw_new.getGraph();
		graph.updateGraph();

		w.updateElementTree();
		w.updateSatelliteView();
		w.updateFilterView();
		w.updatePathwayTree();
		w.updateTheoryProperties();
		w.setEnable(true);

	}

	public void drawAlignmentGraph() {
		// initNewGraph();
		addAlignmentEdges();
		drawGraph();
	}

	public void drawBasicGraphs() {
		// initNewGraph();
		addBackgroundRanges();
		drawGraph();
	}

	public void removeAlignmentEdges() {

		pw_new.getGraph().stopVisualizationModel();

		pw_new.getGraph().getJungGraph().removeEdges(alignmentEdgeSet);
		alignmentEdgeSet.clear();

		drawGraph();

	}

	public void setThreshold(double val) {
		this.edgeThreshold = val;
	}

	public void setAlignment(GraphAlignmentAlgorithms ali) {
		this.alignment = ali;
		this.graphA = ali.getGraphA();
		this.graphB = ali.getGraphB();
		this.graphA_id2position = graphA.getId2position();
		this.graphB_id2position = graphB.getId2position();
	}

	private void addBackgroundRanges() {

		RangeSelector.getInstance().addRangesInMyGraph(pw_new.getGraph(),
				backgroundAttributes1);
		RangeSelector.getInstance().addRangesInMyGraph(pw_new.getGraph(),
				backgroundAttributes2);

	}

}
