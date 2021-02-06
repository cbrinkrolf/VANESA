package transformation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import util.MyIntComparable;

// Restrictions / limitations:
// biological network needs to be directed (each edge)
// no parameter mapping, default will be applied
// will stop if all edges and nodes got replaced/considered
// if no type is given (discrete / continuous), it is inferred from mapped node, default fall back: continuous

// TODO check undirected edges
// TODO parameter mapping
// TODO syso -> log file
// TODO try hierarchical network (maybe first flatten?)
// TODO interactive GUI while transforming (ability to abort)
public class Transformator {

	public static final String place = "Place";
	public static final String discretePlace = Elementdeclerations.discretePlace;
	public static final String continuousPlace = Elementdeclerations.continuousPlace;

	public static final String transition = "Transition";
	public static final String discreteTransition = Elementdeclerations.discreteTransition;
	public static final String continuousTransition = Elementdeclerations.continuousTransition;

	public static final String pnArc = Elementdeclerations.pnEdge;
	public static final String pnTestArc = Elementdeclerations.pnTestEdge;
	public static final String pnInhibitoryArc = Elementdeclerations.pnInhibitionEdge;

	public static final Set<String> places = new HashSet<String>(Arrays.asList(place, discretePlace, continuousPlace));
	public static final Set<String> transitions = new HashSet<String>(
			Arrays.asList(transition, discreteTransition, continuousTransition));
	public static final Set<String> pnArcs = new HashSet<String>(Arrays.asList(pnArc, pnTestArc, pnInhibitoryArc));

	private Pathway pw;
	private Pathway petriNet;

	private List<Rule> rules;
	private HashMap<Rule, Integer> ruleToNextPermIndex = new HashMap<Rule, Integer>();

	private HashMap<Integer, BiologicalNodeAbstract> id2bna = new HashMap<>();

	private HashMap<String, ArrayList<BiologicalNodeAbstract>> nodeType2bna = new HashMap<String, ArrayList<BiologicalNodeAbstract>>();
	private HashMap<String, ArrayList<BiologicalEdgeAbstract>> edgeType2bea = new HashMap<String, ArrayList<BiologicalEdgeAbstract>>();

	private HashMap<BiologicalNodeAbstract, PNNode> bn2pnMap = new HashMap<BiologicalNodeAbstract, PNNode>();

	private HashSet<BiologicalEdgeAbstract> remainingEdges = new HashSet<BiologicalEdgeAbstract>();
	private Map<Integer, BiologicalEdgeAbstract> id2Edge = new HashMap<Integer, BiologicalEdgeAbstract>();

	private int evaluatedPerms = 0;
	private int evaluatedEdges = 0;
	private boolean executed = false;
	private List<List<Integer>> permutations = null;
	private Graph<Integer, Integer> tmpGraph;

	private Set<BiologicalNodeAbstract> subGraphNodes;
	private Set<BiologicalEdgeAbstract> subGraphEdges;

	private List<BiologicalEdgeAbstract> usedEdges = new ArrayList<>();

	public Pathway transform(Pathway pw, List<Rule> rules) {
		this.pw = pw;
		this.rules = rules;
		tmpGraph = new UndirectedSparseGraph<>();

		
		
		petriNet = new Pathway("PN_"+pw.getName());
		petriNet.setPetriNet(true);

		BiologicalNodeAbstract bna;
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();
			if (!bna.hasRef()) {
				id2bna.put(bna.getID(), bna);
				tmpGraph.addVertex(bna.getID());
			}
		}

		Iterator<BiologicalEdgeAbstract> it2 = pw.getAllEdges().iterator();
		BiologicalEdgeAbstract bea;
		//System.out.println("tmpgraph edges:");
		while (it2.hasNext()) {
			bea = it2.next();
			remainingEdges.add(bea);
			tmpGraph.addEdge(bea.getID(), this.getBNARef(bea.getFrom()).getID(), this.getBNARef(bea.getTo()).getID());
			id2Edge.put(bea.getID(), bea);
			//System.out.println(getBNARef(bea.getFrom()).getName()+" -> "+getBNARef(bea.getTo()).getName());
		}
		System.out.println(this.maxAllShortestPath(pw.getGraph().getJungGraph()));
		System.out.println(this.maxAllShortestPath(tmpGraph));

		// createRules();
		applyRules();

		//petriNet.getGraph().restartVisualizationModel();
		//MainWindow.getInstance().updateProjectProperties();
		//MainWindow.getInstance().updateOptionPanel();
		petriNet.updateMyGraph();
		return petriNet;
	}

	private void applyRules() {
		System.out.println("ruleset size: " + rules.size());
		long tic = System.currentTimeMillis();
		for (int i = 0; i < rules.size(); i++) {
			if (this.done()) {
				System.out.println("done, skipping rule #"+i+" and further rules");
				break;
			}
			System.out.println("apply rule #" + i);
			this.applyRule(rules.get(i), true);
			System.out.println("done with rule #" + i);
		}
		long tac = System.currentTimeMillis();
		System.out.println("done with all rules: " + (tac - tic) + "millis");
		System.out.println("evaluated Permutations: " + evaluatedPerms);
		System.out.println("evaluated Edges: " + evaluatedEdges);

		System.out.println("used edges: " + usedEdges.size());
		HashSet<BiologicalEdgeAbstract> set = new HashSet<>(usedEdges);
		System.out.println(
				"used edges set: " + set.size() + " edges used multipe times: " + (usedEdges.size() - set.size()));
	}

	private void applyRule(Rule r, boolean multipleExecution) {
		executed = false;

		// List<String> nodeNames = r.getBNodeNames();

		Graph<String, String> ruleGraph = new UndirectedSparseGraph<>();
		for (int i = 0; i < r.getAllBiologicalNodes().size(); i++) {
			ruleGraph.addVertex(r.getAllBiologicalNodes().get(i).getName());
		}
		RuleEdge re;
		for (int i = 0; i < r.getAllBiologicalEdges().size(); i++) {
			re = r.getAllBiologicalEdges().get(i);
			ruleGraph.addEdge(re.getName(), re.getFrom().getName(), re.getTo().getName());
		}

		int lengthRule = this.maxAllShortestPath(ruleGraph);

		System.out.println("max length rule: " + lengthRule);

		// for each node of pathway, create undirected subgraph (based on tempGraph,
		// containing only edges which are not considered yet)
		// around that node with max. length of rule length. Then test permutations on
		// subgraph
		BiologicalNodeAbstract startNode;

		// heuristic to start with the nodes having the smallest edge count
		List<Integer> sortedList = getAllGraphNodesSortedByEdgeCount(tmpGraph);
		System.out.println("sorted nodes of pathway: " + sortedList.size());
		List<Integer> perm;
		for (int i = 0; i < sortedList.size(); i++) {
			// System.out.println("nodeIdx: " + i);
			ruleToNextPermIndex.clear();
			startNode = id2bna.get(sortedList.get(i));
			if (!tmpGraph.containsVertex(startNode.getID())) {
				// node was already handled
				continue;
			}

			this.setSubgraphNodesAndEdges(startNode, lengthRule);
			System.out.println(startNode.getName() + " subGraphNodes: " + subGraphNodes.size() + " subGraphEdges, "
					+ subGraphEdges.size());
			if (subGraphNodes.size() < 1) {
				// skip
				continue;
			}
			this.createBuckets(subGraphNodes);

			this.createAndSetPermutation(r);

			if (permutations != null && permutations.size() < 1) {
				// skip
				continue;
			}
			System.out.println("size of permutation set: " + permutations.size());

			do {
				executed = false;
				/*
				 * if (nodesChanged) { this.nodesChanged = false;
				 * this.ruleToNextPermIndex.remove(r); createAndSetPermutation(r);
				 * System.out.println("size of permutation set: " + permutations.size()); }
				 */
				perm = this.getNextMatchingPermutation(r, permutations);

				if (perm != null) {
					this.executeRule(r, perm);
				}

			} while (executed && !this.done());
		}

	}

	private void createAndSetPermutation(Rule r) {
		ArrayList<List<Integer>> list = new ArrayList<List<Integer>>();
		ArrayList<Integer> l;
		BiologicalNodeAbstract bna;
		String type;
		Iterator<BiologicalNodeAbstract> it;
		for (int i = 0; i < r.getAllBiologicalNodes().size(); i++) {

			type = r.getAllBiologicalNodes().get(i).getType();

			// consider ANY node
			if (type.equals(Elementdeclerations.anyBNA)) {
				type = BiologicalNodeAbstract.class.getSimpleName();
			}
			// System.out.println(type);

			// node type of rule does not exist in graph -> skip rule
			if (nodeType2bna.get(type) == null) {
				// System.out.println("types do not exist");
				return;
			}
			it = nodeType2bna.get(type).iterator();
			l = new ArrayList<Integer>();
			// System.out.println("T: "+type);
			while (it.hasNext()) {
				bna = it.next();
				// System.out.println("bna id: "+bna.getID());
				// if (availableNodes.contains(bna)) {
				l.add(bna.getID());
				// System.out.println(bna.getID() + " added to perm");
				// System.out.println(bna.getName() + " added to perm");
				// }
			}
			// System.out.println(l.size());
			list.add(l);
			// System.out.println(list);
		}
		permutations = Permutator.permutations(list, false);
	}

	// tries to find one matching permutation
	private List<Integer> getNextMatchingPermutation(Rule r, List<List<Integer>> permutations) {

		// test each permutation
		System.out.println("available edges: " + remainingEdges.size() + " and nodes: " + tmpGraph.getVertexCount());

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

		RuleEdge bEdge;
		String edgeType;
		nextPerm: for (int i = start; i < permutations.size(); i++) {
			evaluatedPerms++;
			// System.out.println("Permutation #" + i);

			perm = permutations.get(i);
			if (evaluatedPerms % 100000 == 0) {
				System.out.println("perms: " + evaluatedPerms);
				// System.out.println("new perm");
			}

			boolean test = true;
			// check all edges
			for (int j = 0; j < r.getAllBiologicalEdges().size(); j++) {
				if (test) {
					bEdge = r.getAllBiologicalEdges().get(j);
					boolean test1 = false;
					id1 = perm.get(r.getAllBiologicalNodes().indexOf(bEdge.getFrom()));

					id2 = perm.get(r.getAllBiologicalNodes().indexOf(bEdge.getTo()));
					n1 = id2bna.get(id1);
					n2 = id2bna.get(id2);

					
					if (tmpGraph.findEdge(id1, id2) != null) {
						bea = id2Edge.get(tmpGraph.findEdge(id1, id2));
						edgeType = bEdge.getType();
						if (edgeType.equals(Elementdeclerations.anyBEA)) {
							edgeType = BiologicalEdgeAbstract.class.getSimpleName();
						}
						// System.out.println("eType="+edgeType);
						// System.out.println(edgeType2bea.get(edgeType).size());
						// System.out.println(n1.getName() + " -> "+n2.getName());
						if (getBNARef(bea.getFrom()) == n1 && getBNARef(bea.getTo()) == n2 && edgeType2bea.get(edgeType).contains(bea)) {
							//System.out.println("true");
							test1 = true;
						} else {
							continue nextPerm;
						}
					} else {
						continue nextPerm;
					}
					test = test && test1;
				} else {
					continue nextPerm;
				}
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

	private void executeRule(Rule r, List<Integer> perm) {
		executed = false;
		System.out.println("execute rule");
		HashMap<RuleNode, BiologicalNodeAbstract> ruleBNodeToBNA = new HashMap<RuleNode, BiologicalNodeAbstract>();
		HashMap<RuleNode, PNNode> rulePNodeToBNA = new HashMap<RuleNode, PNNode>();
		List<BiologicalNodeAbstract> toDeleteBNA = new ArrayList<BiologicalNodeAbstract>();

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
				if (bn2pnMap.containsKey(bna)) {
					// node already exists
					pnBNA = bn2pnMap.get(bna);
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
					toDeleteBNA.add(bna);
				} else {
					// node needs to be created
					rulePNodeToBNA.put(pnNode, createPNNode(r, pnNode, bna));
					toDeleteBNA.add(bna);
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
			case Elementdeclerations.pnEdge:
				edge = new PNEdge(from, to, "1", "1", "PNEdge", "1");

				break;
			case Elementdeclerations.pnTestEdge:

				break;
			case Elementdeclerations.pnInhibitionEdge:
				edge = new PNEdge(from, to, "1", "1", Elementdeclerations.inhibitor, "1");
				break;
			}
			if (edge != null) {
				edge.setDirected(true);
				petriNet.addEdge(edge);
			} else {
				System.out.println("Error: Petri net edge couldnt be created!");
				executed = false;
				return;
			}
		}

		// remove BEA from set of available edges
		// String edgeName = "";
		BiologicalNodeAbstract fromBNA = null;
		BiologicalNodeAbstract toBNA = null;

		RuleEdge bEdge;
		BiologicalEdgeAbstract bea;
		for (int i = 0; i < r.getConsideredEdges().size(); i++) {

			bEdge = r.getConsideredEdges().get(i);

			fromBNA = ruleBNodeToBNA.get(bEdge.getFrom());
			toBNA = ruleBNodeToBNA.get(bEdge.getTo());
			if (tmpGraph.findEdge(fromBNA.getID(), toBNA.getID()) != null) {
				bea = id2Edge.get(tmpGraph.findEdge(fromBNA.getID(), toBNA.getID()));
				this.remainingEdges.remove(bea);
				this.tmpGraph.removeEdge(bea.getID());
				subGraphEdges.remove(bea);
				this.usedEdges.add(bea);
			} else {
				System.out.println("Removing edge failed");
				executed = false;
				return;
			}
		}

		// removing nodes from set of potential nodes, if all of their incident edges
		// were considered already

		for (int i = 0; i < toDeleteBNA.size(); i++) {
			checkAndDelete(toDeleteBNA.get(i));
			// this.permutations = Permutator.removePermsByElement(permutations,
			// ruleToNextPermIndex.get(r), toDeleteBNA.get(i).getID());
			// this.ruleToNextPermIndex.remove(r);
			// System.out.println("new permutation size: " + permutations.size());
		}

		// this.ruleToNextPermIndex.remove(r);
		// this.applyRule(r, true);
		// return;
		return;

	}

	private PNNode createPNNode(Rule r, RuleNode pnNode, BiologicalNodeAbstract bna) {
		PNNode pn = null;
		String type = pnNode.getType();
		// no type given
		// System.out.println("rule node type: "+ type);
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
				bn2pnMap.put(bna, pn);
			}
			petriNet.addVertex(pn, new Point2D.Double(x, y));

		} else {
			System.out.println("Error, Petri net node could not be created!");
			return null;
		}

		return pn;
	}

	private void createBuckets(Collection<BiologicalNodeAbstract> bnas) {
		nodeType2bna.clear();
		edgeType2bea.clear();

		BiologicalNodeAbstract bna;
		BiologicalEdgeAbstract bea;
		String name;
		Class<?> c;
		// all nodes
		Iterator<BiologicalNodeAbstract> it = bnas.iterator();
		while (it.hasNext()) {

			bna = it.next();
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
		if (this.bn2pnMap.containsKey(bna)) {
			return this.bn2pnMap.get(bna);
		} else {
			return bna;
		}
	}

	private void checkAndDelete(BiologicalNodeAbstract bna) {
		if(tmpGraph.getIncidentEdges(bna.getID()).isEmpty()){
		tmpGraph.removeVertex(bna.getID());
		}
	}

	private boolean done() {
		// System.out.println("done check: "+availableEdges.size()+"
		// "+availableNodes.size());
		if (tmpGraph.getEdges().isEmpty() && tmpGraph.getVertices().isEmpty()) {
			System.out.println("done");
			return true;
		}
		return false;
	}

	private <V, E> int maxAllShortestPath(Graph<V, E> g) {
		int max = 0;
		Iterator<V> it = g.getVertices().iterator();
		V n1;
		V n2;
		int d;
		UnweightedShortestPath<V, E> sp = new UnweightedShortestPath<V, E>(g);
		Number dist;
		Iterator<V> it2;
		while (it.hasNext()) {
			n1 = it.next();
			it2 = g.getVertices().iterator();
			while (it2.hasNext()) {
				n2 = it2.next();
				dist = sp.getDistance(n1, n2);
				if (dist != null) {
					d = dist.intValue();
					if (d > max) {
						max = d;
					}
				}
			}
		}
		return max;
	}

	private void setSubgraphNodesAndEdges(BiologicalNodeAbstract startNode, int maxDist) {
		subGraphNodes = new HashSet<BiologicalNodeAbstract>();
		subGraphEdges = new HashSet<BiologicalEdgeAbstract>();
		UnweightedShortestPath<Integer, Integer> sp = new UnweightedShortestPath<>(tmpGraph);

		Map<Integer, Number> distances = sp.getDistanceMap(startNode.getID());

		Iterator<Integer> it = distances.keySet().iterator();
		int k;
		while (it.hasNext()) {
			k = it.next();
			if (distances.get(k) != null && distances.get(k).intValue() <= maxDist) {
				subGraphNodes.add(id2bna.get(k));
			}
		}
		Iterator<BiologicalEdgeAbstract> it2 = remainingEdges.iterator();
		BiologicalEdgeAbstract bea;
		while (it2.hasNext()) {
			bea = it2.next();
			if (subGraphNodes.contains(getBNARef(bea.getFrom())) && subGraphNodes.contains(getBNARef(bea.getTo()))) {
				subGraphEdges.add(bea);
			}
		}
	}

	private <V, E> List<V> getAllGraphNodesSortedByEdgeCount(Graph<V, E> g) {

		HashMap<Integer, Collection<V>> map = new HashMap<Integer, Collection<V>>();
		int size;
		for (V bna : g.getVertices()) {
			size = g.getInEdges(bna).size();
			if (!map.containsKey(size)) {
				map.put(size, new ArrayList<V>());
			}
			map.get(size).add(bna);
		}

		ArrayList<Integer> ids = new ArrayList<Integer>(map.keySet());
		Collections.sort(ids, new MyIntComparable());

		List<V> sortedList = new ArrayList<V>();
		for (int i = 0; i < ids.size(); i++) {
			sortedList.addAll(map.get(ids.get(i)));
		}
		return sortedList;
	}

	private BiologicalNodeAbstract getBNARef(BiologicalNodeAbstract bna) {
		if (bna.hasRef()) {
			return bna.getRef();
		}
		return bna;
	}
	
	public HashMap<BiologicalNodeAbstract, PNNode> getBnToPN(){
		return this.bn2pnMap;
	}
}
