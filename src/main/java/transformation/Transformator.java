package transformation;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
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
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Enzyme;
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
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import graph.gui.Parameter;
import gui.MyPopUp;

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

	public static final String pnArc = Elementdeclerations.pnArc;
	public static final String pnTestArc = Elementdeclerations.pnTestArc;
	public static final String pnInhibitorArc = Elementdeclerations.pnInhibitorArc;

	public static final Set<String> places = new HashSet<String>(Arrays.asList(place, discretePlace, continuousPlace));
	public static final Set<String> transitions = new HashSet<String>(
			Arrays.asList(transition, discreteTransition, continuousTransition));
	public static final Set<String> pnArcs = new HashSet<String>(Arrays.asList(pnArc, pnTestArc, pnInhibitorArc));

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
	private int totalGeneratedPerms = 0;
	private int evaluatedEdges = 0;
	private boolean executed = false;
	private List<List<Integer>> permutations = Collections.emptyList();
	private Graph<Integer, Integer> tmpGraph;

	private Set<BiologicalNodeAbstract> subGraphNodes;
	private Set<BiologicalEdgeAbstract> subGraphEdges;

	private List<BiologicalEdgeAbstract> usedEdges = new ArrayList<>();

	private boolean useSubgraph = true;
	private boolean useBuckets = true;
	private boolean useSmallestNodeDegree = true;

	private boolean printLog = !true;

	public Pathway transform(Pathway pw, List<Rule> rules) {
		this.pw = pw;
		this.rules = rules;
		// multigraph is Necessary for loops
		tmpGraph = new UndirectedSparseMultigraph<>();

		petriNet = new Pathway("PN_" + pw.getName());
		petriNet.setIsPetriNet(true);

		BiologicalNodeAbstract bna;
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();
			if (!bna.isLogical()) {
				id2bna.put(bna.getID(), bna);
				tmpGraph.addVertex(bna.getID());
			}
		}

		Iterator<BiologicalEdgeAbstract> it2 = pw.getAllEdges().iterator();
		BiologicalEdgeAbstract bea;
		// System.out.println("tmpgraph edges:");
		while (it2.hasNext()) {
			bea = it2.next();
			remainingEdges.add(bea);
			tmpGraph.addEdge(bea.getID(), this.getBNARef(bea.getFrom()).getID(), this.getBNARef(bea.getTo()).getID());
			id2Edge.put(bea.getID(), bea);
			// System.out.println(getBNARef(bea.getFrom()).getName()+" ->
			// "+getBNARef(bea.getTo()).getName());
		}
		// System.out.println(this.maxAllShortestPath(pw.getGraph().getJungGraph()));
		// System.out.println(this.maxAllShortestPath(tmpGraph));
		System.out.println("tmp Graph Edge count: " + tmpGraph.getEdgeCount());
		// createRules();
		applyRules();

		// petriNet.getGraph().restartVisualizationModel();
		// MainWindow.getInstance().updateProjectProperties();
		// MainWindow.getInstance().updateOptionPanel();
		petriNet.updateMyGraph();
		MyPopUp.getInstance().show("Transformation", "Biological network transformed without errors!");
		return petriNet;
	}

	private void applyRules() {
		System.out.println("ruleset size: " + rules.size());
		long tic = System.currentTimeMillis();
		for (int i = 0; i < rules.size(); i++) {
			if (this.done()) {
				System.out.println("done, skipping rule #" + i + " and further rules");
				break;
			}
			System.out.println("apply rule #" + i);
			this.applyRule(rules.get(i), true);
			System.out.println("done with rule #" + i);
		}
		long tac = System.currentTimeMillis();
		System.out.println("done with all rules: " + (tac - tic) + "millis");
		System.out.println("evaluated Permutations: " + evaluatedPerms);
		System.out.println("total generated Permutations: " + totalGeneratedPerms);
		System.out.println("evaluated Edges: " + evaluatedEdges);

		System.out.println("used edges: " + usedEdges.size());
		HashSet<BiologicalEdgeAbstract> set = new HashSet<>(usedEdges);
		System.out.println(
				"used edges set: " + set.size() + " edges used multipe times: " + (usedEdges.size() - set.size()));
	}

	private void applyRule(Rule r, boolean multipleExecution) {
		if (printLog) {
			System.out.println("apply rule: " + r.getName());
		}
		executed = false;

		// List<String> nodeNames = r.getBNodeNames();

		Graph<String, String> ruleGraph = new UndirectedSparseGraph<>();
		for (int i = 0; i < r.getBiologicalNodes().size(); i++) {
			ruleGraph.addVertex(r.getBiologicalNodes().get(i).getName());
		}
		RuleEdge re;
		for (int i = 0; i < r.getBiologicalEdges().size(); i++) {
			re = r.getBiologicalEdges().get(i);
			ruleGraph.addEdge(re.getName(), re.getFrom().getName(), re.getTo().getName());
		}

		int lengthRule = this.maxAllShortestPath(ruleGraph);

		System.out.println("max length rule: " + lengthRule);

		int neighborhoodSize = Math.floorDiv(lengthRule + 1, 2);
		System.out.println("neighborhood size: " + neighborhoodSize);

		// for each node of pathway, create undirected subgraph (based on tempGraph,
		// containing only edges which are not considered yet)
		// around that node with max. length of rule length. Then test permutations on
		// subgraph
		BiologicalNodeAbstract startNode;

		// heuristic to start with the nodes having the smallest edge count
		List<Integer> sortedList;
		if (useSmallestNodeDegree) {
			sortedList = getAllGraphNodesSortedByEdgeCount(tmpGraph);
		} else {
			sortedList = new ArrayList<Integer>(tmpGraph.getVertices());
		}

		System.out.println("sorted nodes of pathway: " + sortedList.size());
		System.out.println("number of edges in pathway: " + tmpGraph.getEdgeCount());
		List<Integer> perm;
		for (int i = 0; i < sortedList.size(); i++) {
			// System.out.println("nodeIdx: " + i);
			ruleToNextPermIndex.clear();
			startNode = id2bna.get(sortedList.get(i));

			if (!useSubgraph) {
				// only execute once
				i = sortedList.size();
			}

			if (!tmpGraph.containsVertex(startNode.getID())) {
				// node was already handled
				continue;
			}

			this.setSubgraphNodesAndEdges(startNode, neighborhoodSize);
			if (printLog) {
				System.out.println(startNode.getName() + " subGraphNodes: " + subGraphNodes.size() + " subGraphEdges, "
						+ subGraphEdges.size());
			}

			if (subGraphNodes.size() < 1) {
				// skip
				continue;
			}
			this.createBuckets(subGraphNodes);
			// System.out.println("done create buckets");
			this.createAndSetPermutation(r);

			// System.out.println(permutations.isEmpty());
			if (permutations == null || permutations.size() < 1) {
				// skip
				continue;
			}
			// if(permutations != null)
			// System.out.println("size of permutation set: " + permutations.size());
			if (!useSubgraph && !useBuckets) {
				return;
			}
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
		// System.out.println("createAndSetPermutation: " + r.getName());
		ArrayList<List<Integer>> list = new ArrayList<List<Integer>>();
		ArrayList<Integer> l;
		BiologicalNodeAbstract bna;
		String type;
		Iterator<BiologicalNodeAbstract> it;
		RuleNode rn;
		Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> jungGraph = pw.getGraph().getJungGraph();
		for (int i = 0; i < r.getBiologicalNodes().size(); i++) {
			rn = r.getBiologicalNodes().get(i);
			l = new ArrayList<Integer>();

			if (useBuckets) {
				type = rn.getType();

				// consider ANY node
				// if (type.equals(Elementdeclerations.anyBNA)) {
				// type = BiologicalNodeAbstract.class.getSimpleName();
				// }
				// System.out.println("type: " + type);

				// node type of rule does not exist in graph -> skip rule
				if (nodeType2bna.get(type) == null) {
					System.out.println("types do not exist");
					return;
				}
				it = nodeType2bna.get(type).iterator();

				// System.out.println("T: "+type);
				while (it.hasNext()) {
					bna = it.next();
					// System.out.println("bna id: "+bna.getID());
					// if (availableNodes.contains(bna)) {
					// System.out.println(bna.getName() + " in: " +
					// jungGraph.getInEdges(bna).size());
					// System.out.println(bna.getName() + " out: " +
					// jungGraph.getOutEdges(bna).size());
					// System.out.println(rn.getName() + " in: " + r.getIncomingEdgeCount(rn));
					// System.out.println(rn.getName() + " out: " + r.getOutgoingEdgeCount(rn));
					if (rn.isExactIncidence()) {
						if (r.getIncomingEdgeCount(rn) == jungGraph.getInEdges(bna).size()
								&& r.getOutgoingEdgeCount(rn) == jungGraph.getOutEdges(bna).size()) {
							l.add(bna.getID());
							// System.out.println("added: " + bna.getName());
						}
					} else {
						l.add(bna.getID());
					}
					// System.out.println(bna.getID() + " added to perm");
					// System.out.println(bna.getName() + " added to perm");
					// }
				}
				// System.out.println(l.size());
			} else {
				for (BiologicalNodeAbstract node : subGraphNodes) {
					// check exact incidences
					if (rn.isExactIncidence()) {
						if (r.getIncomingEdgeCount(rn) == jungGraph.getInEdges(node).size()
								&& r.getOutgoingEdgeCount(rn) == jungGraph.getOutEdges(node).size()) {
							l.add(node.getID());
						}
					} else {
						l.add(node.getID());
					}

					// l.add(node.getID());
				}
			}
			if (l.size() < 1) {
				permutations = null;
				return;
			}
			list.add(l);
			// System.out.println(list);
		}
		permutations = Permutator.permutations(list, false);
		totalGeneratedPerms += permutations.size();
	}

	// tries to find one matching permutation
	private List<Integer> getNextMatchingPermutation(Rule r, List<List<Integer>> permutations) {

		// test each permutation
		if (printLog) {
			System.out
					.println("available edges: " + remainingEdges.size() + " and nodes: " + tmpGraph.getVertexCount());
		}

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
		RuleNode rn;

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

			// TODO check all nodes if !useBuckets
			boolean test = true;
			if (!useBuckets) {
				for (int j = 0; j < r.getBiologicalNodes().size(); j++) {
					rn = r.getBiologicalNodes().get(j);
					id1 = perm.get(j);
					n1 = id2bna.get(id1);
					if (nodeType2bna.get(rn.getType()) != null && nodeType2bna.get(rn.getType()).contains(n1)) {
						// check all nodes, implement here
					} else {
						continue nextPerm;
					}
				}
			}

			// check all edges
			Collection<Integer> edgeSet;
			Iterator<Integer> edgeIt;
			// System.out.println("biological edges in pattern: " +
			// r.getBiologicalEdges().size());
			for (int j = 0; j < r.getBiologicalEdges().size(); j++) {
				// System.out.println("j: " + j);
				evaluatedEdges++;
				if (test) {
					bEdge = r.getBiologicalEdges().get(j);
					boolean test1 = false;
					id1 = perm.get(r.getBiologicalNodes().indexOf(bEdge.getFrom()));

					id2 = perm.get(r.getBiologicalNodes().indexOf(bEdge.getTo()));
					n1 = id2bna.get(id1);
					n2 = id2bna.get(id2);
					// System.out.println("edge count: " + tmpGraph.getEdgeCount());
					// System.out.println("try find edge: " + n1.getName() + " -> " + n2.getName());
					// System.out.println("set size: " + tmpGraph.findEdgeSet(id1, id2).size());
					try {
						tmpGraph.findEdgeSet(id1, id2);
					} catch (Exception e) {
						// System.out.println("contains "+id1+" :" + tmpGraph.containsVertex(id1)+"
						// "+id2bna.get(id1).getName());
						// System.out.println("contains "+id2+" :" + tmpGraph.containsVertex(id2)+"
						// "+id2bna.get(id2).getName());
						// e.printStackTrace();
						// System.out.println("tmpGraph nodes:");

						for (Integer iteg : tmpGraph.getVertices()) {
							// System.out.println(id2bna.get(iteg).getName());
						}
					}
					if (!tmpGraph.containsVertex(id1) || !tmpGraph.containsVertex(id2)) {
						continue nextPerm;
					}
					edgeSet = tmpGraph.findEdgeSet(id1, id2);
					if (edgeSet != null && !edgeSet.isEmpty()) {
						edgeIt = edgeSet.iterator();
						while (edgeIt.hasNext() && !test1) {
							// System.out.println("found");
							bea = id2Edge.get(edgeIt.next());
							edgeType = bEdge.getType();

							// System.out.println("eType=" + edgeType);
							// System.out.println(edgeType2bea.get(edgeType).size());
							// System.out.println(n1.getName() + " -> " + n2.getName());
							// System.out.println("if block:");
							// System.out.println(
							// "from: " + getBNARef(bea.getFrom()).getName() + " (expected: " + n1.getName()
							// + ")");
							// System.out.println(getBNARef(bea.getFrom()) == n1);
							// System.out.println(getBNARef(bea.getTo()) == n2);
							// System.out.println(edgeType2bea.get(edgeType) != null);
							// System.out.println(edgeType2bea.get(edgeType).contains(bea));

							// System.out.println("end if block");
							if (getBNARef(bea.getFrom()) == n1 && getBNARef(bea.getTo()) == n2
									&& edgeType2bea.get(edgeType) != null && edgeType2bea.get(edgeType).contains(bea)) {
								// System.out.println("true");
								test1 = true;
							}
						}
						if (!test1) {
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
		if (printLog) {
			System.out.println("execute rule: " + r.getName());
		}
		HashMap<RuleNode, BiologicalNodeAbstract> ruleBNodeToBNA = new HashMap<RuleNode, BiologicalNodeAbstract>();
		HashMap<RuleNode, PNNode> rulePNodeToBNA = new HashMap<RuleNode, PNNode>();
		List<BiologicalNodeAbstract> toDeleteBNA = new ArrayList<BiologicalNodeAbstract>();

		for (int i = 0; i < r.getBiologicalNodes().size(); i++) {
			// System.out.println(r.getAllBiologicalNodes().get(i) + "->"+
			// id2bna.get(perm.get(i)));
			// System.out.println(id2bna.get(perm.get(i)).getName() + " expected type: "+
			// r.getBiologicalNodes().get(i).getType());
			ruleBNodeToBNA.put(r.getBiologicalNodes().get(i), id2bna.get(perm.get(i)));
		}

		// create or fetch nodes
		RuleNode pnNode;
		RuleNode bNode;
		BiologicalNodeAbstract bna;
		PNNode pnBNA;
		PNNode newPNNode;
		for (int i = 0; i < r.getPetriNodes().size(); i++) {
			pnNode = r.getPetriNodes().get(i);
			// System.out.println(pnNode);
			// Petri net node has mapping to biological node
			if (r.getMappedBnode(pnNode) != null) {
				// System.out.println(pnNode);
				bNode = r.getMappedBnode(pnNode);
				bna = ruleBNodeToBNA.get(bNode);
				// System.out.println(bna);
				// System.out.println(bna.getName());
				if (bn2pnMap.containsKey(bna)) {
					// node already exists
					pnBNA = bn2pnMap.get(bna);
					rulePNodeToBNA.put(pnNode, pnBNA);
					// check if node types matches
					if (pnNode.getType().equals(Transformator.transition) && pnBNA instanceof Place) {
						System.out.println("node type mismatch 1");
					} else if (pnNode.getType().equals(Transformator.continuousTransition)
							&& !(pnBNA instanceof ContinuousTransition)) {
						System.out.println("node type mismatch 2");
					} else if (pnNode.getType().equals(Transformator.discreteTransition)
							&& !(pnBNA instanceof DiscreteTransition)) {
						System.out.println("node type mismatch 3");
					} else if (pnNode.getType().equals(Transformator.place) && pnBNA instanceof Transition) {
						System.out.println("node type mismatch 4");
					} else if (pnNode.getType().equals(Transformator.continuousPlace)
							&& !(pnBNA instanceof ContinuousPlace)) {
						System.out.println("node type mismatch 5");
					} else if (pnNode.getType().equals(Transformator.discretePlace)
							&& !(pnBNA instanceof DiscretePlace)) {
						System.out.println("node type mismatch 6");
					}
					toDeleteBNA.add(bna);
				} else {
					// node needs to be created
					if (bna.getName().equals("n2_7_1_69")) {
						// System.out.println("create "+pnNode.getType() +" "+ bna.getName());
					}
					newPNNode = createPNNode(r, pnNode, bna);
					rulePNodeToBNA.put(pnNode, newPNNode);
					setTransformationParameters(newPNNode, pnNode, ruleBNodeToBNA, r);
					toDeleteBNA.add(bna);
					executed = true;
				}
			} else {
				// no mapping, PNNode needs to be created
				newPNNode = createPNNode(r, pnNode, null);
				rulePNodeToBNA.put(pnNode, newPNNode);
				executed = true;
			}
		}

		// creating Petri net edges
		RuleEdge pnEdge;
		PNNode from;
		PNNode to;
		PNArc edge;
		for (int i = 0; i < r.getPetriEdges().size(); i++) {
			executed = true;
			pnEdge = r.getPetriEdges().get(i);

			from = rulePNodeToBNA.get(pnEdge.getFrom());
			to = rulePNodeToBNA.get(pnEdge.getTo());

			edge = null;
			switch (pnEdge.getType()) {
			case Elementdeclerations.pnArc:
				// TODO set parameters
				edge = new PNArc(from, to, "1", "1", Elementdeclerations.pnArc, "1");

				break;
			case Elementdeclerations.pnTestArc:
				// TODO set parameters

				break;
			case Elementdeclerations.pnInhibitorArc:
				// TODO set parameters
				edge = new PNArc(from, to, "1", "1", Elementdeclerations.inhibitor, "1");
				break;
			}
			if (edge != null) {
				edge.setDirected(true);
				petriNet.addEdge(edge);
			} else {
				System.out.println("Error: Petri net arc couldnt be created!");
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
		Collection<Integer> edgeColl;
		Iterator<Integer> it;
		boolean deleted = false;
		for (int i = 0; i < r.getConsideredEdges().size(); i++) {
			deleted = false;
			bEdge = r.getConsideredEdges().get(i);

			fromBNA = ruleBNodeToBNA.get(bEdge.getFrom());
			toBNA = ruleBNodeToBNA.get(bEdge.getTo());
			edgeColl = tmpGraph.findEdgeSet(fromBNA.getID(), toBNA.getID());
			// System.out.println(edgeColl.size());
			if (edgeColl != null && edgeColl.size() > 0) {

				it = edgeColl.iterator();
				while (it.hasNext() && !deleted) {
					bea = id2Edge.get(it.next());
					if (getBNARef(bea.getFrom()) == fromBNA && getBNARef(bea.getTo()) == toBNA) {
						this.remainingEdges.remove(bea);
						this.tmpGraph.removeEdge(bea.getID());
						subGraphEdges.remove(bea);
						this.usedEdges.add(bea);
						deleted = true;
					} else {
						// System.out.println("from: "+bea.getFrom().getName()+"expected:
						// "+fromBNA.getName());
					}
				}
			}
			if (!deleted) {
				System.err.println("Removing edge failed");
				executed = false;
				return;
			}
		}

		// removing nodes from set of potential nodes, if all of their incident edges
		// were considered already
		// System.out.println("number of nodes to be checked for deletion:
		// "+toDeleteBNA.size());
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
		// TODO parameters
		// TODO consider stochastic transitions
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
		String defaultPName = "p" + petriNet.getPlaceCount() + 1;
		String defaultTName = "t" + petriNet.getTransitionCount() + 1;
		switch (type) {
		case continuousPlace:
			pn = new ContinuousPlace(defaultPName, defaultPName);
			break;
		case continuousTransition:
			pn = new ContinuousTransition(defaultTName, defaultTName);
			break;
		case discretePlace:
			pn = new DiscretePlace(defaultPName, defaultPName);
			break;
		case discreteTransition:
			pn = new DiscreteTransition(defaultTName, defaultTName);
			break;
		}
		if (pn != null) {
			double x = 0;
			double y = 0;

			if (bna != null) {
				if (bna.getLabel().trim().length() > 0) {
					// pn.setLabel(bna.getLabel());
				}
				if (bna.getName().trim().length() > 0) {
					// pn.setName(bna.getName());
				}
				pn.setColor(bna.getColor());
				pn.setPlotColor(bna.getPlotColor());
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
		// System.out.println("calculate subgraph nodes: "+ bnas.size());
		nodeType2bna.clear();
		edgeType2bea.clear();

		BiologicalNodeAbstract bna;
		BiologicalNodeAbstract bna1;
		BiologicalEdgeAbstract bea;
		BiologicalEdgeAbstract bea1;
		String name;
		Class<?> c;
		nodeType2bna.put(Elementdeclerations.anyBNA, new ArrayList<BiologicalNodeAbstract>());
		edgeType2bea.put(Elementdeclerations.anyBEA, new ArrayList<BiologicalEdgeAbstract>());

		// all nodes
		Iterator<BiologicalNodeAbstract> it = bnas.iterator();
		while (it.hasNext()) {
			bna = it.next();

			c = bna.getClass();
			name = bna.getClass().getSimpleName();
			// Object o = c.getSuperclass().cast(bna);
			nodeType2bna.get(Elementdeclerations.anyBNA).add(bna);

			while (!name.equals("BiologicalNodeAbstract")) {
				// System.out.println("class: "+c.getSimpleName());
				// System.out.println(name);

				// System.out.println("name: "+name);
				// bna = (BiologicalNodeAbstract) c.cast(bna);
				try {
					bna1 = (BiologicalNodeAbstract) c.getConstructor(String.class, String.class).newInstance("", "");
					if (!nodeType2bna.containsKey(bna1.getBiologicalElement())) {
						nodeType2bna.put(bna1.getBiologicalElement(), new ArrayList<BiologicalNodeAbstract>());
					}
					nodeType2bna.get(bna1.getBiologicalElement()).add(bna);
					// System.out.println("type: " + bna1.getBiologicalElement() + " " +
					// bna1.getName());
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}

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

			edgeType2bea.get(Elementdeclerations.anyBEA).add(bea);
			while (!name.equals("BiologicalEdgeAbstract")) {

				try {
					bea1 = (BiologicalEdgeAbstract) c.getConstructor(String.class, String.class,
							BiologicalNodeAbstract.class, BiologicalNodeAbstract.class).newInstance("", "", null, null);
					if (!edgeType2bea.containsKey(bea1.getBiologicalElement())) {
						edgeType2bea.put(bea1.getBiologicalElement(), new ArrayList<BiologicalEdgeAbstract>());
					}
					edgeType2bea.get(bea1.getBiologicalElement()).add(bea);
					// System.out.println("type: "+bna1.getBiologicalElement());
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}

				// if (!edgeType2bea.containsKey(name)) {
				// edgeType2bea.put(name, new ArrayList<BiologicalEdgeAbstract>());
				// }
				// edgeType2bea.get(name).add(bea);
				// System.out.println(name);
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
		if (tmpGraph.getIncidentEdges(bna.getID()).isEmpty()) {
			tmpGraph.removeVertex(bna.getID());
			// System.out.println("removed from tmpGraph: "+bna.getName());
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
		if (useSubgraph) {

			// UndirectedSparseGraph g = new UndirectedSparseGraph<>();
			// DirectionTransformer.toUndirected(tmpGraph, null, null, executed)

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
				if (subGraphNodes.contains(getBNARef(bea.getFrom()))
						&& subGraphNodes.contains(getBNARef(bea.getTo()))) {
					subGraphEdges.add(bea);
					// System.out.println(
					// "added edge to subgraph: " + bea.getFrom().getName() + " -> " +
					// bea.getTo().getName());
				}
			}
		} else {
			for (Integer i : tmpGraph.getVertices()) {
				subGraphNodes.add(id2bna.get(i));
			}
			Iterator<BiologicalEdgeAbstract> it2 = remainingEdges.iterator();
			BiologicalEdgeAbstract bea;
			while (it2.hasNext()) {
				bea = it2.next();
				if (subGraphNodes.contains(getBNARef(bea.getFrom()))
						&& subGraphNodes.contains(getBNARef(bea.getTo()))) {
					subGraphEdges.add(bea);
				}
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
		ids.sort(Integer::compare);

		List<V> sortedList = new ArrayList<V>();
		for (int i = 0; i < ids.size(); i++) {
			sortedList.addAll(map.get(ids.get(i)));
		}
		return sortedList;
	}

	private BiologicalNodeAbstract getBNARef(BiologicalNodeAbstract bna) {
		if (bna.isLogical()) {
			return bna.getLogicalReference();
		}
		return bna;
	}

	public HashMap<BiologicalNodeAbstract, PNNode> getBnToPN() {
		return this.bn2pnMap;
	}

	// setting parameters to Petri net nodes and edges
	private void setTransformationParameters(GraphElementAbstract gea, RuleNode pnNode,
			HashMap<RuleNode, BiologicalNodeAbstract> ruleBNodeToBNA, Rule r) {

		// generation of possible parameters for current matching
		Set<String> possibleParams = new HashSet<>();
		for (RuleNode rn : r.getBiologicalNodes()) {
			for (String parameter : ruleBNodeToBNA.get(rn).getTransformationParameters()) {
				possibleParams.add(rn.getName() + "." + parameter);
				// System.out.println(rn.getName() + "." + parameter);
			}
		}
		for (RuleEdge re : r.getBiologicalEdges()) {
			possibleParams.add(re.getName() + ".label");
			possibleParams.add(re.getName() + ".name");
			possibleParams.add(re.getName() + ".function");
		}

		Place p;
		String value;
		PNNode petriNode;
		PNArc arc;
		Double d;
		String string;
		BiologicalNodeAbstract node;
		// for nodes
		if (gea instanceof PNNode) {
			petriNode = (PNNode) gea;
			for (String key : pnNode.getParameterMap().keySet()) {
				value = pnNode.getParameterMap().get(key);
				if (value == null || value.trim().length() < 1) {
					continue;
				}
				// System.out.println("key: " + key + " value: " + value);
				switch (key) {
				case "name":
					// System.out.println(gea.getName());
					string = this.evalParameter(possibleParams, value, String.class, ruleBNodeToBNA, r);
					if (string != null) {
						if (petriNet.getAllNodeNames().contains(string)) {
							// System.out.println("new String: " + string);
							// System.out.println(petriNet.getAllNodeNames());
							node = petriNet.getNodeByName(string);
							if (node != petriNode) {
								if (node.getClass().getName().equals(petriNode.getClass().getName())) {
									petriNode.setLogicalReference(node);
									MyPopUp.getInstance().show("Name already exists!",
											"Name: " + string + " exists already. Created logical node instead!");
								} else {
									MyPopUp.getInstance().show("Error: Name already exists!", "Name: " + string
											+ " exists already. Cannot Created logical node because of type mismatch: "
											+ petriNet.getClass().getSimpleName() + " versus "
											+ node.getClass().getSimpleName());
								}
								continue;
							}
						}
						gea.setName(string);
						gea.setLabel(gea.getName());
					}
					break;
				case "tokenStart":
					if (gea instanceof Place) {
						p = (Place) gea;
						BiologicalNodeAbstract bna = getRuleNodeOfParameter(possibleParams, value, ruleBNodeToBNA, r);
						d = this.evalParameter(possibleParams, value, Double.class, ruleBNodeToBNA, r);
						if (d != null) {
							p.setTokenStart(d);
							p.setConstant(bna.isConstant());
						}
					}
					break;
				case "tokenMin":
					if (gea instanceof Place) {
						p = (Place) gea;
						d = this.evalParameter(possibleParams, value, Double.class, ruleBNodeToBNA, r);
						if (d != null) {
							p.setTokenMin(d);
						}
					}
					break;
				case "tokenMax":
					if (gea instanceof Place) {
						p = (Place) gea;
						d = this.evalParameter(possibleParams, value, Double.class, ruleBNodeToBNA, r);
						if (d != null) {
							p.setTokenMax(d);
						}
					}
					break;
				case "firingCondition":
					if (gea instanceof Transition) {
						string = this.evalParameter(possibleParams, value, String.class, ruleBNodeToBNA, r);
						if (string != null) {
							((Transition) gea).setFiringCondition(string);
						}
					}
					break;
				case "maximalSpeed":
					if (gea instanceof ContinuousTransition) {
						ContinuousTransition ct = (ContinuousTransition) gea;
						string = this.evalParameter(possibleParams, value, String.class, ruleBNodeToBNA, r);
						if (string != null) {
							// System.out.println(string);
							ct.setMaximalSpeed(string);
							BiologicalNodeAbstract bna = getRuleNodeOfParameter(possibleParams, value, ruleBNodeToBNA,
									r);

							if (bna != null) {
								if (bna.getTransformationParameters().contains("isKnockedOut")) {
									ct.setKnockedOut(
											Boolean.parseBoolean(bna.getTransformationParameterValue("isKnockedOut")));
								}
								this.copyParameters(bna, (ContinuousTransition) gea);
							}
						}
					}
					break;
				case "delay":
					if (gea instanceof DiscreteTransition) {
						d = this.evalParameter(possibleParams, value, Double.class, ruleBNodeToBNA, r);
						if (d != null) {
							((DiscreteTransition) gea).setDelay(d);
						}
					}
				}
			}
		} else if (gea instanceof PNArc) {
			// for edges
			arc = (PNArc) gea;
			for (String key : pnNode.getParameterMap().keySet()) {
				value = pnNode.getParameterMap().get(key);
				if (value == null || value.trim().length() < 1) {
					continue;
				}
				// System.out.println("key: " + key + " value: " + value);
				switch (key) {
				case "name":

					break;

				case "function":

					break;
				}
			}
		}
	}

	private BiologicalNodeAbstract getRuleNodeOfParameter(Set<String> possibleParameters, String value,
			HashMap<RuleNode, BiologicalNodeAbstract> ruleBNodeToBNA, Rule r) {
		String[] split;
		BiologicalNodeAbstract bna = null;
		// String v;
		// System.out.println("type: "+type);
		// if (type.equals(String.class)) {
		// System.out.println("type match");
		if (possibleParameters.contains(value)) {
			// System.out.println("value: " + value);
			split = value.split("\\.");
			// System.out.println("split1: " + split[0]);
			// System.out.println("split2: " + split[1]);
			if (split.length == 2) {
				bna = ruleBNodeToBNA.get(r.getBiologicaNode(split[0]));
				// System.out.println("return:
				// "+type.cast(bna.getTransformationParameterValue(split[1])));

			}
		}
		return bna;
	}

	private <T> T evalParameter(Set<String> possibleParameters, String value, Class<T> type,
			HashMap<RuleNode, BiologicalNodeAbstract> ruleBNodeToBNA, Rule r) {
		String[] split;
		BiologicalNodeAbstract bna;
		// String v;
		// System.out.println("type: "+type);
		// if (type.equals(String.class)) {
		// System.out.println("type match");
		if (possibleParameters.contains(value)) {
			// System.out.println("value: " + value);
			split = value.split("\\.");
			// System.out.println("split1: " + split[0]);
			// System.out.println("split2: " + split[1]);
			if (split.length == 2) {
				bna = ruleBNodeToBNA.get(r.getBiologicaNode(split[0]));
				// System.out.println("return:
				// "+type.cast(bna.getTransformationParameterValue(split[1])));
				if (type == String.class) {
					if (bna instanceof Enzyme) {
						// System.out.println(bna.getTransformationParameters());
						// System.out.println(split[1]);
						// System.out.println(bna.getTransformationParameterValue(split[1]));
						// Enzyme e = (Enzyme) bna;
						// System.out.println(((Enzyme)bna).getTransformationParameterValue("maximalSpeed"));
					}
					return type.cast(bna.getTransformationParameterValue(split[1]));
				} else if (type == Double.class) {
					return type.cast(Double.valueOf(bna.getTransformationParameterValue(split[1])));
				}
			}
		} else {
			return type.cast(value);
		}
		// } else if (type == Double.class){
		// return type.cast(value);
		// }
		return null;
	}

	private void copyParameters(BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		for (Parameter p : from.getParameters()) {
			to.getParameters().add(new Parameter(p.getName(), p.getValue(), p.getUnit()));
		}

	}
}
