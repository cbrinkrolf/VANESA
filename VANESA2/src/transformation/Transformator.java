package transformation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousPlace;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.PNNode;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import graph.CreatePathway;
import graph.GraphInstance;
import gui.MainWindow;

// Restrictions / limitations:
// biological network needs to be directed (each edge)
// All edges of one applied rule will be "deleted"
// no parameter mapping, default will be applied
// will stop if all edges and nodes got replaced/considered
// if no type is given (discrete / continuous), it is inferred from mapped node, default fall back: continuous

// TODO parameter mapping
// TODO test if mapping node types matches
// TODO syso -> log file
// TODO handling references / logical nodes
// TODO consider considered edges
// TODO try hierarchical network (maybe first flatten?)
public class Transformator {

	public static final String place = "Place";
	public static final String discretePlace = Elementdeclerations.discretePlace;
	public static final String continuousPlace = Elementdeclerations.continuousPlace;

	public static final String transition = "Transition";
	public static final String discreteTransition = Elementdeclerations.discreteTransition;
	public static final String continuousTransition = Elementdeclerations.continuousTransition;

	public static final String pnArc =  Elementdeclerations.pnEdge;
	public static final String pnTestArc = Elementdeclerations.pnTestEdge;
	public static final String pnInhibitoryArc = Elementdeclerations.pnInhibitionEdge;

	public static final Set<String> places = new HashSet<String>(Arrays.asList(place, discretePlace, continuousPlace));
	public static final Set<String> transitions = new HashSet<String>(
			Arrays.asList(transition, discreteTransition, continuousTransition));
	public static final Set<String> pnArcs = new HashSet<String>(Arrays.asList(pnArc, pnTestArc, pnInhibitoryArc));

	private Pathway pw;
	private Pathway petriNet;

	private List<Rule> rules = new ArrayList<Rule>();
	private HashMap<Rule, Integer> ruleToNextPermIndex = new HashMap<Rule, Integer>();

	private HashMap<Integer, BiologicalNodeAbstract> id2bna = new HashMap<>();

	private HashMap<String, ArrayList<BiologicalNodeAbstract>> nodeType2bna = new HashMap<String, ArrayList<BiologicalNodeAbstract>>();
	private HashMap<String, ArrayList<BiologicalEdgeAbstract>> edgeType2bea = new HashMap<String, ArrayList<BiologicalEdgeAbstract>>();

	private HashMap<BiologicalNodeAbstract, PNNode> nodeReplace = new HashMap<BiologicalNodeAbstract, PNNode>();

	private HashSet<BiologicalEdgeAbstract> availableEdges = new HashSet<BiologicalEdgeAbstract>();
	private HashSet<BiologicalNodeAbstract> availableNodes = new HashSet<BiologicalNodeAbstract>();

	public void transform(Pathway pw, List<Rule> rules) {
		this.pw = pw;
		this.rules = rules;

		MainWindow w = MainWindow.getInstance();
		new CreatePathway();
		GraphInstance graphInstance = new GraphInstance();
		graphInstance.getPathway().setPetriNet(true);
		// w.getBar().paintToolbar(option == JOptionPane.NO_OPTION);
		w.updateAllGuiElements();
		petriNet = graphInstance.getPathway();

		BiologicalNodeAbstract bna;
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();
			id2bna.put(bna.getID(), bna);
			availableNodes.add(bna);
		}

		Iterator<BiologicalEdgeAbstract> it2 = pw.getAllEdges().iterator();
		while (it2.hasNext()) {
			availableEdges.add(it2.next());
		}

		// createRules();
		this.createBuckets();
		applyRules();

		petriNet.getGraph().restartVisualizationModel();
		MainWindow.getInstance().updateProjectProperties();
		MainWindow.getInstance().updateOptionPanel();
	}

	private void applyRules() {
		System.out.println("ruleset size: " + rules.size());
		for (int i = 0; i < rules.size(); i++) {
			if (this.done()) {
				return;
			}
			System.out.println("apply rule #" + i);
			this.applyRule(rules.get(i), true);
		}
	}

	private void applyRule(Rule r, boolean multipleExecution) {
		boolean executed = false;

		// List<String> nodeNames = r.getBNodeNames();

		ArrayList<List<Integer>> list = new ArrayList<List<Integer>>();
		ArrayList<Integer> l;
		BiologicalNodeAbstract bna;
		String type;
		Iterator<BiologicalNodeAbstract> it;
		for (int i = 0; i < r.getAllBiologicalNodes().size(); i++) {

			type = r.getAllBiologicalNodes().get(i).getType();

			//TODO consider ANY node and ANY BEA
			
			// node type of rule does not exist in graph -> skip rule
			if (nodeType2bna.get(type) == null) {
				// System.out.println("types do not exist");
				return;
			}
			it = nodeType2bna.get(type).iterator();
			l = new ArrayList<Integer>();

			while (it.hasNext()) {
				bna = it.next();
				l.add(bna.getID());
			}
			list.add(l);
		}

		List<List<Integer>> permutations = Permutator.permutations(list, false);
		List<Integer> perm;

		do {
			executed = false;
			perm = this.getNextMatchingPermutation(r, permutations);

			if (perm != null) {
				executed = this.executeRule(r, perm);
			}

		} while (executed && !this.done());

		// System.out.println(permutations.size());
	}

	// tries to find one matching permutation
	private List<Integer> getNextMatchingPermutation(Rule r, List<List<Integer>> permutations) {

		// test each permutation
		System.out.println(permutations.size());

		List<Integer> perm;

		BiologicalEdgeAbstract bea;

		int id1;
		int id2;
		BiologicalNodeAbstract n1;
		BiologicalNodeAbstract n2;

		// for each permutation
		int start = 0;
		if (ruleToNextPermIndex.containsKey(r)) {
			start = ruleToNextPermIndex.get(r) + 1;
		}

		Iterator<BiologicalEdgeAbstract> it;
		RuleEdge bEdge;
		for (int i = start; i < permutations.size(); i++) {
			// System.out.println("Permutation #"+i);
			perm = permutations.get(i);

			// System.out.println("new perm");
			boolean test = true;
			// check all edges
			for (int j = 0; j < r.getAllBiologicalEdges().size(); j++) {
				bEdge = r.getAllBiologicalEdges().get(j);
				it = availableEdges.iterator();
				boolean test1 = false;
				while (it.hasNext() && !test1) {
					bea = it.next();
					id1 = perm.get(r.getAllBiologicalNodes().indexOf(bEdge.getFrom()));

					id2 = perm.get(r.getAllBiologicalNodes().indexOf(bEdge.getTo()));
					n1 = id2bna.get(id1);
					n2 = id2bna.get(id2);
					if (bea.getFrom() == n1 && bea.getTo() == n2 && edgeType2bea.get(bEdge.getType()).contains(bea)) {
						test1 = true;
						// System.out.println(r.getNodeNames().indexOf(r.getEdgeFrom().get(i)));

						// System.out.println(n1.getLabel() + "->" + n2.getLabel());
						// if(test){
						// System.out.println(perm);
						// }
					} else {
						// test = false;
					}

				}
				test = test && test1;

			}
			if (test) {
				for (int k = 0; k < perm.size(); k++) {
					// System.out.print(" " + id2bna.get(perm.get(k)).getLabel() + " ");
				}
				// System.out.println();
				this.ruleToNextPermIndex.put(r, i);
				return perm;
			}
		}

		return null;
	}

	private boolean executeRule(Rule r, List<Integer> perm) {
		boolean executed = false;
		System.out.println("execute rule");
		HashMap<RuleNode, BiologicalNodeAbstract> ruleBNodeToBNA = new HashMap<RuleNode, BiologicalNodeAbstract>();
		HashMap<RuleNode, PNNode> rulePNodeToBNA = new HashMap<RuleNode, PNNode>();

		for (int i = 0; i < r.getAllBiologicalNodes().size(); i++) {
			// System.out.println(r.getAllBiologicalNodes().get(i) + "->"+
			// id2bna.get(perm.get(i)));
			ruleBNodeToBNA.put(r.getAllBiologicalNodes().get(i), id2bna.get(perm.get(i)));
		}

		// create or fetch nodes
		RuleNode pnNode;
		RuleNode bNode;
		BiologicalNodeAbstract bna;
		PNNode pnBNA;
		for (int i = 0; i < r.getAllPetriNodes().size(); i++) {
			pnNode = r.getAllPetriNodes().get(i);
			// System.out.println(pnNode);
			// Petri net node has mapping to biological node
			if (r.getMappedBnode(pnNode) != null) {
				// System.out.println(pnNode);
				bNode = r.getMappedBnode(pnNode);
				bna = ruleBNodeToBNA.get(bNode);
				// System.out.println(bna);
				if (nodeReplace.containsKey(bna)) {
					// node already exists
					pnBNA = nodeReplace.get(bna);
					rulePNodeToBNA.put(pnNode, pnBNA);
					// check if node types matches
					if (pnNode.getType().equals(Transformator.transition) && pnBNA instanceof Place) {
						System.out.println("node type mismatch");
					} else if (pnNode.getType().equals(Transformator.continuousTransition)
							&& !(pnBNA instanceof ContinuousTransition)) {
						System.out.println("node type mismatch");
					} else if (pnNode.getType().equals(Transformator.discreteTransition)
							&& !(pnBNA instanceof DiscreteTransition)) {
						System.out.println("node type mismatch");
					} else if (pnNode.getType().equals(Transformator.place) && pnBNA instanceof Transition) {
						System.out.println("node type mismatch");
					} else if (pnNode.getType().equals(Transformator.continuousPlace)
							&& !(pnBNA instanceof ContinuousPlace)) {
						System.out.println("node type mismatch");
					} else if (pnNode.getType().equals(Transformator.discretePlace)
							&& !(pnBNA instanceof DiscretePlace)) {
						System.out.println("node type mismatch");
					}
				} else {
					// node needs to be created
					rulePNodeToBNA.put(pnNode, createPNNode(r, pnNode, bna));
					this.availableNodes.remove(bna);
					executed = true;
				}
			} else {
				// no mapping, PNNode needs to be created
				rulePNodeToBNA.put(pnNode, createPNNode(r, pnNode, null));
				executed = true;
			}
		}

		// creating Petri net edges
		RuleEdge pnEdge;
		PNNode from;
		PNNode to;
		PNEdge edge;
		for (int i = 0; i < r.getAllPetriEdges().size(); i++) {
			executed = true;
			pnEdge = r.getAllPetriEdges().get(i);

			from = rulePNodeToBNA.get(pnEdge.getFrom());
			to = rulePNodeToBNA.get(pnEdge.getTo());

			edge = null;
			switch (pnEdge.getType()) {
			case "PNArc":
				edge = new PNEdge(from, to, "1", "1", "PNEdge", "1");

				break;
			case "PNTestArc":

				break;
			case "PNInhibitoryArc":
				edge = new PNEdge(from, to, "1", "1", Elementdeclerations.inhibitor, "1");
				break;
			}
			if (edge != null) {
				edge.setDirected(true);
				petriNet.addEdge(edge);
			} else {
				System.out.println("Error: Petri net edge couldnt be created!");
				return false;
			}

		}

		// remove BEA from set of available edges
		//String edgeName = "";
		BiologicalNodeAbstract fromBNA = null;
		BiologicalNodeAbstract toBNA = null;

		RuleEdge bEdge;
		for (int i = 0; i < r.getAllBiologicalEdges().size(); i++) {

			bEdge = r.getAllBiologicalEdges().get(i);

			fromBNA = ruleBNodeToBNA.get(bEdge.getFrom());
			toBNA = ruleBNodeToBNA.get(bEdge.getTo());
			if (pw.existEdge(fromBNA, toBNA)) {
				this.availableEdges.remove(pw.getEdge(fromBNA, toBNA));
			} else {

				System.out.println("Removing edge failed");
				return false;
			}
		}

		return executed;

	}

	private PNNode createPNNode(Rule r, RuleNode pnNode, BiologicalNodeAbstract bna) {
		PNNode pn = null;
		String type = pnNode.getType();
		// no type given
		if (type.equals(place)) {
			// infer type from matching node
			if (bna != null) {
				if (bna.isDiscrete()) {
					type = discretePlace;
				} else {
					type = continuousPlace;
				}
			} else {
				type = continuousPlace;
			}
		} else if (type.equals(transition)) {
			if (bna != null) {
				if (bna.isDiscrete()) {
					type = discreteTransition;
				} else {
					type = continuousTransition;
				}
			} else {
				type = continuousTransition;
			}
		}

		switch (type) {
		case continuousPlace:
			pn = new ContinuousPlace("", "");
			break;
		case continuousTransition:
			pn = new ContinuousTransition("", "");
			break;
		case discretePlace:
			pn = new DiscretePlace("", "");
			break;
		case discreteTransition:
			pn = new DiscreteTransition("", "");
			break;
		}
		if (pn != null) {
			double x = 0;
			double y = 0;

			if (bna != null) {
				pn.setLabel(bna.getLabel());
				pn.setName(bna.getName());
				pn.setColor(bna.getColor());
				// System.out.println("Vertex: "+p.getName());
				// System.out.println("x: "+locations.getLocation(bna.getVertex()).getX());
				x = pw.getGraph().getVertexLocation(bna).getX();// locations.getLocation(bna.getVertex()).getX();
				y = pw.getGraph().getVertexLocation(bna).getY();// locations.getLocation(bna.getVertex()).getY();
				// System.out.println("x: "+x+" y: "+y);
				// pw.getGraph().moveVertex(p.getVertex(), scaleFactor*
				// x,scaleFactor* y);
				nodeReplace.put(bna, pn);
			}
			petriNet.addVertex(pn, new Point2D.Double(x, y));

		} else {
			System.out.println("Error, Petri net node could not be created!");
			return null;
		}

		return pn;
	}

	private void createBuckets() {
		nodeType2bna.clear();
		edgeType2bea.clear();

		BiologicalNodeAbstract bna;
		BiologicalEdgeAbstract bea;
		String name;
		Class<?> c;
		// all nodes
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();
			bna = this.getNodeReplace(bna);
			c = bna.getClass();
			name = bna.getClass().getSimpleName();

			while (!name.equals("Object")) {
				// System.out.println(name);

				if (!nodeType2bna.containsKey(name)) {
					nodeType2bna.put(name, new ArrayList<BiologicalNodeAbstract>());
				}
				nodeType2bna.get(name).add(bna);

				c = c.getSuperclass();
				name = c.getSimpleName();
			}
		}
		// all edges
		Iterator<BiologicalEdgeAbstract> it2 = pw.getAllEdges().iterator();
		while (it2.hasNext()) {
			bea = it2.next();
			c = bea.getClass();
			name = bea.getClass().getSimpleName();

			while (!name.equals("Object")) {
				// System.out.println(name);

				if (!edgeType2bea.containsKey(name)) {
					edgeType2bea.put(name, new ArrayList<BiologicalEdgeAbstract>());
				}
				edgeType2bea.get(name).add(bea);

				c = c.getSuperclass();
				name = c.getSimpleName();
			}
		}
	}

	private BiologicalNodeAbstract getNodeReplace(BiologicalNodeAbstract bna) {
		if (this.nodeReplace.containsKey(bna)) {
			return this.nodeReplace.get(bna);
		} else {
			return bna;
		}
	}

	private boolean done() {
		if (this.availableEdges.isEmpty() && this.availableNodes.isEmpty()) {
			return true;
		}
		return false;
	}
}
