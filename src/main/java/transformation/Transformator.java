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
import gui.PopUpDialog;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import util.StringLengthComparator;

// Restrictions / limitations:
// no parameter mapping, default will be applied
// will stop if all edges and nodes got replaced/considered
// if no type is given (discrete / continuous), it is inferred from mapped node, default fall back: continuous
// will not generate logical places/transitions. if there is collision of the same name, name will get altered

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

	public static final Set<String> places = new HashSet<>(Arrays.asList(place, discretePlace, continuousPlace));
	public static final Set<String> transitions = new HashSet<>(
			Arrays.asList(transition, discreteTransition, continuousTransition));
	public static final Set<String> pnArcs = new HashSet<>(Arrays.asList(pnArc, pnTestArc, pnInhibitorArc));

	private Pathway pw;
	private Pathway petriNet;

	private List<Rule> rules;
	private HashMap<Rule, Integer> ruleToNextPermIndex = new HashMap<>();

	private HashMap<Integer, BiologicalNodeAbstract> id2bna = new HashMap<>();

	private HashMap<String, ArrayList<BiologicalNodeAbstract>> nodeType2bna = new HashMap<>();
	private HashMap<String, ArrayList<BiologicalEdgeAbstract>> edgeType2bea = new HashMap<>();

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

	private Map<PNNode, String> initialPNNodeName = new HashMap<>();
	private Map<Match, Map<String, String>> match2Parameters = new HashMap<>();
	private Map<Match, Map<String, String>> match2ReplaceParameters = new HashMap<>();
	private Map<Match, List<String>> match2SortedParameterList = new HashMap<>();
	private Collection<Function> functions = null;
	private Collection<Function> functionsBool = null;

	private List<Match> matches;

	public Pathway transform(Pathway pw, List<Rule> rules) {
		// System.out.println("new transform");
		this.pw = pw;
		this.rules = rules;
		// multigraph is Necessary for loops
		tmpGraph = new UndirectedSparseMultigraph<>();

		petriNet = new Pathway("PN_" + pw.getName());
		petriNet.setIsPetriNet(true);

		matches = new ArrayList<>();
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (!bna.isLogical()) {
				id2bna.put(bna.getID(), bna);
				tmpGraph.addVertex(bna.getID());
			}
		}

		// System.out.println("tmpgraph edges:");
		for (BiologicalEdgeAbstract bea : pw.getAllEdges()) {
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
		PopUpDialog.getInstance().show("Transformation", "Biological network transformed without errors!");
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
		Match match = null;
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
				match = this.getNextMatchingPermutation(r, permutations);

				if (match != null) {
					this.executeRule(match);
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
		int inCount;
		int outCount;
		int unDirCount;

		for (int i = 0; i < r.getBiologicalNodes().size(); i++) {
			rn = r.getBiologicalNodes().get(i);

			inCount = r.getIncomingDirectedEdgeCount(rn);
			outCount = r.getOutgoingDirectedEdgeCount(rn);
			unDirCount = r.getUndirectedEdgeCount(rn);

			l = new ArrayList<Integer>();

			if (useBuckets) {
				// System.out.println("buckets");
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

				// System.out.println("T: "+type+ " count: "+nodeType2bna.get(type).size());
				while (it.hasNext()) {
					bna = it.next();
					// System.out.println(bna.getName());
					// System.out.println("bna id: "+bna.getID());
					// if (availableNodes.contains(bna)) {
					// System.out.println(bna.getName() + " in: " +
					// pw.getIncomingDirectedEdgeCount(bna));
					// System.out.println(bna.getName() + " out: " +
					// pw.getOutgoingDirectedEdgeCount(bna));
					// System.out.println(bna.getName() + " undir: " +
					// pw.getUndirectedEdgeCount(bna));
					// System.out.println(rn.getName() + " in: " +
					// r.getIncomingDirectedEdgeCount(rn));
					// System.out.println(rn.getName() + " out: " +
					// r.getOutgoingDirectedEdgeCount(rn));
					// System.out.println(rn.getName() + " undir.: " +
					// r.getUndirectedEdgeCount(rn));
					// check exact incdedences

					// TODO does not take numbers of edges of logical nodes into account!!!
					if (rn.isExactIncidence()) {
						if (inCount == pw.getIncomingDirectedEdgeCount(bna)
								&& outCount == pw.getOutgoingDirectedEdgeCount(bna)
								&& unDirCount == pw.getUndirectedEdgeCount(bna)) {
							l.add(bna.getID());
							// System.out.println("added: " + bna.getName());
						}
					} else {
						// System.out.println("edge count name: "+bna.getName());
						//// System.out.println("in: "+ pw.getIncomingDirectedEdgeCount(bna));
						// System.out.println("out: "+ pw.getOutgoingDirectedEdgeCount(bna));
						// System.out.println("undir: "+ pw.getUndirectedEdgeCount(bna));

						// TODO does not take numbers of edges of logical nodes into account!!!
						// if (inCount <= pw.getIncomingDirectedEdgeCount(bna)
						// && outCount <= pw.getOutgoingDirectedEdgeCount(bna)
						// && unDirCount <= pw.getUndirectedEdgeCount(bna)) {
						l.add(bna.getID());
						// }
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
						if (inCount == pw.getIncomingDirectedEdgeCount(node)
								&& outCount == pw.getOutgoingDirectedEdgeCount(node)
								&& unDirCount == pw.getUndirectedEdgeCount(node)) {
							l.add(node.getID());
						}
					} else {
						if (inCount <= pw.getIncomingDirectedEdgeCount(node)
								&& outCount <= pw.getOutgoingDirectedEdgeCount(node)
								&& unDirCount <= pw.getUndirectedEdgeCount(node)) {
							l.add(node.getID());
						}
					}
					// l.add(node.getID());
				}
			}
			if (l.size() < 1) {
				permutations = null;
				return;
			}
			list.add(l);
		}
		if (printLog) {
			System.out.println(list);
		}
		permutations = Permutator.permutations(list, false);
		totalGeneratedPerms += permutations.size();
	}

	// tries to find one matching permutation
	private Match getNextMatchingPermutation(Rule r, List<List<Integer>> permutations) {
		Match match = new Match(r);

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
			match.clearEdgeMapping();
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

						// for (Integer iteg : tmpGraph.getVertices()) {
						// System.out.println(id2bna.get(iteg).getName());
						// }
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

							// check directed or undirected
							if (bea.isDirected() != bEdge.isDirected()) {
								continue nextPerm;
							}

							// check biological type
							if (edgeType2bea.get(edgeType) == null || !edgeType2bea.get(edgeType).contains(bea)) {
								continue nextPerm;
							}
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
							if (getBNARef(bea.getFrom()) == n1 && getBNARef(bea.getTo()) == n2) {
								// System.out.println("true1");
								test1 = true;
								match.addMapping(bEdge, bea);
							}
							// check reverse edge for undirected edge
							if (!bEdge.isDirected()) {
								if (getBNARef(bea.getFrom()) == n2 && getBNARef(bea.getTo()) == n1) {
									// System.out.println("true2");
									test1 = true;
									match.addMapping(bEdge, bea);
								}
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
				if (r.getBiologicalNodes().size() != perm.size()) {
					System.err.println(
							"Size mismatch between permutation and number of biological nodes in the transformation rule!!!");
					return null;
				}
				for (int k = 0; k < perm.size(); k++) {
					match.addMapping(r.getBiologicalNodes().get(k), id2bna.get(perm.get(k)));
				}
				// System.out.println();
				this.ruleToNextPermIndex.put(r, i);
				return match;
			}
		}
		return null;
	}

	private void executeRule(Match match) {
		executed = false;
		Rule r = match.getRule();
		if (printLog) {
			System.out.println("execute rule: " + r.getName());
		}
		// HashMap<RuleNode, BiologicalNodeAbstract> ruleBNodeToBNA = new
		// HashMap<RuleNode, BiologicalNodeAbstract>();
		HashMap<RuleNode, PNNode> rulePNodeToBNA = new HashMap<RuleNode, PNNode>();
		List<BiologicalNodeAbstract> toDeleteBNA = new ArrayList<BiologicalNodeAbstract>();

		// for (int i = 0; i < r.getBiologicalNodes().size(); i++) {
		// System.out.println(r.getAllBiologicalNodes().get(i) + "->"+
		// id2bna.get(perm.get(i)));
		// System.out.println(id2bna.get(perm.get(i)).getName() + " expected type: "+
		// r.getBiologicalNodes().get(i).getType());
		// ruleBNodeToBNA.put(r.getBiologicalNodes().get(i), id2bna.get(perm.get(i)));
		// }

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
				bna = match.getMapping(bNode);
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
					newPNNode = createPNNode(pnNode, bna);
					rulePNodeToBNA.put(pnNode, newPNNode);
					setTransformationParameters(newPNNode, pnNode, match);
					toDeleteBNA.add(bna);
					executed = true;
				}
			} else {
				// no mapping, PNNode needs to be created
				newPNNode = createPNNode(pnNode, null);
				rulePNodeToBNA.put(pnNode, newPNNode);
				setTransformationParameters(newPNNode, pnNode, match);
				executed = true;
			}
		}

		// creating Petri net arcs
		RuleEdge pnEdge;
		PNNode from;
		PNNode to;
		PNArc arc;
		for (int i = 0; i < r.getPetriEdges().size(); i++) {
			executed = true;
			pnEdge = r.getPetriEdges().get(i);

			from = rulePNodeToBNA.get(pnEdge.getFrom());
			to = rulePNodeToBNA.get(pnEdge.getTo());
			arc = createPNArc(pnEdge, from, to);

			if (arc == null) {
				return;
			}
			setTransformationParameters(arc, pnEdge, match);
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

			fromBNA = match.getMapping(bEdge.getFrom());
			toBNA = match.getMapping(bEdge.getTo());
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
						if (!bea.isDirected()) {
							if (getBNARef(bea.getFrom()) == toBNA && getBNARef(bea.getTo()) == fromBNA) {
								this.remainingEdges.remove(bea);
								this.tmpGraph.removeEdge(bea.getID());
								subGraphEdges.remove(bea);
								this.usedEdges.add(bea);
								deleted = true;
							}
						}
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
		matches.add(match);
		// System.out.println("matches so far: " + matches.size());
		return;

	}

	private PNNode createPNNode(RuleNode pnNode, BiologicalNodeAbstract bna) {
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

		// avoiding duplicate names
		int i = 1;
		while (petriNet.getAllNodeNames().contains("p" + i)) {
			i++;
		}
		String defaultPName = "p" + i;
		i = 1;
		while (petriNet.getAllNodeNames().contains("t" + i)) {
			i++;
		}
		String defaultTName = "t" + i;

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
				x = pw.getGraph().getVertexLocation(bna).getX();// locations.getLocation(bna.getVertex()).getX();
				y = pw.getGraph().getVertexLocation(bna).getY();// locations.getLocation(bna.getVertex()).getY();
				bn2pnMap.put(bna, pn);
			}
			petriNet.addVertex(pn, new Point2D.Double(x, y));
		} else {
			System.out.println("Error, Petri net node could not be created!");
			return null;
		}
		return pn;
	}

	private PNArc createPNArc(RuleEdge re, PNNode from, PNNode to) {
		PNArc arc = null;
		String type = re.getType();
		switch (type) {
		case pnArc:
			arc = new PNArc(from, to, "1", "1", Elementdeclerations.pnArc, "1");
			break;
		case pnTestArc:
			arc = new PNArc(from, to, "1", "1", Elementdeclerations.pnTestArc, "1");
			break;
		case pnInhibitorArc:
			arc = new PNArc(from, to, "1", "1", Elementdeclerations.pnInhibitorArc, "1");
			break;
		}
		if (arc == null) {
			System.out.println("Error, Petri net node could not be created!");
			return null;
		}
		petriNet.addEdge(arc);
		return arc;
	}

	private void createBuckets(Collection<BiologicalNodeAbstract> bnas) {
		// System.out.println("calculate subgraph nodes: "+ bnas.size());
		nodeType2bna.clear();
		edgeType2bea.clear();

		BiologicalNodeAbstract bna1;
		BiologicalEdgeAbstract bea1;
		String name;
		Class<?> c;
		nodeType2bna.put(Elementdeclerations.anyBNA, new ArrayList<BiologicalNodeAbstract>());
		edgeType2bea.put(Elementdeclerations.anyBEA, new ArrayList<BiologicalEdgeAbstract>());

		// all nodes
		for (BiologicalNodeAbstract bna : bnas) {

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
		for (BiologicalEdgeAbstract bea : pw.getAllEdges()) {
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
		int d;
		UnweightedShortestPath<V, E> sp = new UnweightedShortestPath<V, E>(g);
		Number dist;
		for (V v1 : g.getVertices()) {
			for (V v2 : g.getVertices()) {
				dist = sp.getDistance(v1, v2);
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

			for (int k : distances.keySet()) {
				if (distances.get(k) != null && distances.get(k).intValue() <= maxDist) {
					subGraphNodes.add(id2bna.get(k));
				}
			}
			for (BiologicalEdgeAbstract bea : remainingEdges) {
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
			for (BiologicalEdgeAbstract bea : remainingEdges) {
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

	// set parameters to Petri net node
	private void setTransformationParameters(PNNode petriNode, RuleNode rn, Match match) {
		Rule r = match.getRule();

		// generation of possible parameters for current matching
		setCurrentPossibleParametersOfMatch(match);

		Place p;
		Transition t;
		String orgValue;
		String value;

		Double d;
		String string;
		BiologicalNodeAbstract node;
		for (String key : rn.getParameterMap().keySet()) {
			orgValue = rn.getParameterMap().get(key);
			if (orgValue == null || orgValue.trim().length() < 1) {
				continue;//orgValue = "";
			}
			// System.out.println("key: " + key + " value: " + orgValue);
			value = replaceParametersToValues(orgValue, match);
			switch (key) {
			case "name":
				String initialName = "";
				String name = "";
				// string = this.evalParameter(possibleParams, value, String.class, match);
				string = value;
				//System.out.println("string: " + value);
				if (string == null || string.trim().length() == 0) {
					// avoiding duplicate names
					int i = 1;
					if (petriNode instanceof Place) {
						//System.out.println(petriNet.getAllNodeNames());
						while (petriNet.getAllNodeNames().contains("p" + i)) {
							i++;
						}
						name = "p" + i;
						//System.out.println("name = " + name);
					} else {
						while (petriNet.getAllNodeNames().contains("t" + i)) {
							i++;
						}
						name = "t" + i;
					}
				} else {
					initialName = string;
					// name exists already in PN
					if (petriNet.getAllNodeNames().contains(string)) {
						// node is mapped to a BNA
						if (bn2pnMap.values().contains(petriNode)) {
							node = petriNet.getNodeByName(string);
							if (bn2pnMap.values().contains(node)) {
								int i = 1;
								while (petriNet.getAllNodeNames().contains(string + i)) {
									i++;
								}
								name = string + i;
							} else {
								// node with same name is not mapped
								int i = 1;
								String tmpName = initialPNNodeName.get(node) + i;
								while (petriNet.getAllNodeNames().contains(tmpName) || tmpName.equals(string)) {
									i++;
									tmpName = initialPNNodeName.get(node) + i;
								}
								node.setName(tmpName);
								node.setLabel(tmpName);
								name = string;
							}
						} else {
							// node is not mapped to a BNA
							int i = 1;
							while (petriNet.getAllNodeNames().contains(string + i)) {
								i++;
							}
							name = string + i;
						}
					} else {
						// name doesnt exist in PN, yet
						name = string;
					}
				}
				petriNode.setName(name);
				petriNode.setLabel(petriNode.getName());
				initialPNNodeName.put(petriNode, initialName);

				break;
			case "tokenStart":
				if (orgValue.length() > 0) {
					if (petriNode instanceof Place) {
						p = (Place) petriNode;
						d = evaluateDouble(value);
						if (p instanceof DiscretePlace) {
							d = Double.valueOf(Math.round(d));
						}
						p.setTokenStart(d);
						if (r.getMappedBnode(rn) != null) {
							// p.setConstant(match.getMapping(r.getMappedBnode(pnNode)).isConstant());
						}
					}
				}
				break;
			case "tokenMin":
				if (orgValue.length() > 0) {
					if (petriNode instanceof Place) {
						p = (Place) petriNode;
						d = evaluateDouble(value);
						if (p instanceof DiscretePlace) {
							d = Double.valueOf(Math.round(d));
						}
						p.setTokenMin(d);
					}
				}
				break;
			case "tokenMax":
				if (orgValue.length() > 0) {
					if (petriNode instanceof Place) {
						p = (Place) petriNode;
						d = evaluateDouble(value);
						if (p instanceof DiscretePlace) {
							d = Double.valueOf(Math.round(d));
						}
						p.setTokenMax(d);
					}
				}
				break;
			case "firingCondition":
				if (orgValue.length() > 0) {
					if (petriNode instanceof Transition) {
						t = (Transition) petriNode;
						t.setFiringCondition(value);
					}
				}
				break;
			case "maximalSpeed":
				if (orgValue.length() > 0) {
					if (petriNode instanceof ContinuousTransition) {
						ContinuousTransition ct = (ContinuousTransition) petriNode;
						ct.setMaximalSpeed(value);
					}
				}
				break;
			case "delay":
				if (orgValue.length() > 0) {
					if (petriNode instanceof DiscreteTransition) {
						// d = this.evalParameter(possibleParams, value, Double.class, match);
						// if (d != null) {
						// ((DiscreteTransition) gea).setDelay(d);
						// }
					}
				}
			case "isConstant":
				if (orgValue.length() > 0) {
					petriNode.setConstant(evaluateBoolean(value));
				}
				break;
			case "isKnockedOut":
				if (orgValue.length() > 0) {
					if (petriNode instanceof Transition) {
						t = (Transition) petriNode;

						t.setKnockedOut(evaluateBoolean(value));
					}
				}
				break;
			}

			// copy parameters of nodes that are matched in this value
			for (BiologicalNodeAbstract bna : this.getNodesOfReplacedParameter(orgValue, "maximalSpeed", match)) {
				this.copyParameters(bna, petriNode);
			}
			// copy parameters of edges that are matched in this value
			for (BiologicalEdgeAbstract bea : this.getEdgesOfReplacedParameter(orgValue, "function", match)) {
				this.copyParameters(bea, petriNode);
			}
		}

	}

	private void setTransformationParameters(PNArc arc, RuleEdge re, Match match) {
		// generation of possible parameters for current matching
		setCurrentPossibleParametersOfMatch(match);

		String orgValue;
		String value;

		// System.out.println("map size: "+re.getParameterMap().size());

		for (String key : re.getParameterMap().keySet()) {
			orgValue = re.getParameterMap().get(key);
			if (orgValue == null || orgValue.trim().length() < 1) {
				continue;
			}
			value = replaceParametersToValues(orgValue, match);
			//System.out.println("key: " + key + " value: " + value);
			switch (key) {
			// case "name":
			// break;
			case "function":
				arc.setFunction(value);
				break;
			}

			// copy parameters of nodes that are matched in this value
			for (BiologicalNodeAbstract bna : this.getNodesOfReplacedParameter(orgValue, "maximalSpeed", match)) {
				this.copyParameters(bna, arc);
			}
			// copy parameters of edges that are matched in this value
			for (BiologicalEdgeAbstract bea : this.getEdgesOfReplacedParameter(orgValue, "function", match)) {
				this.copyParameters(bea, arc);
			}
		}
	}

	private double evaluateDouble(String value) {
		if (this.functions == null) {
			this.createFunctions();
		}
		ExpressionBuilder eb = new ExpressionBuilder(value);
		for (Function f : functions) {
			eb.function(f);
		}
		return eb.build().evaluate();
	}

	private boolean evaluateBoolean(String value) {
		if (this.functionsBool == null) {
			this.createFunctionsBool();
		}
		value = value.toLowerCase().replaceAll("true", "1").replaceAll("false", "0");
		ExpressionBuilder eb = new ExpressionBuilder(value);
		for (Function f : functionsBool) {
			eb.function(f);
		}
		return eb.build().evaluate() > 0;
	}

	private void setCurrentPossibleParametersOfMatch(Match match) {

		if (match2Parameters.containsKey(match)) {
			return;
		}
		Rule r = match.getRule();
		Map<String, String> possibleParams = new HashMap<>();

		for (RuleNode rn : r.getBiologicalNodes()) {
			for (String parameter : match.getMapping(rn).getTransformationParameters()) {
				possibleParams.put(rn.getName() + "." + parameter,
						match.getMapping(rn).getTransformationParameterValue(parameter));
				// replace.put(rn.getName() + "." + parameter, rn.getName() + "_" + parameter);
				// System.out.println(rn.getName() + "." + parameter);
			}
		}

		for (RuleEdge re : r.getBiologicalEdges()) {
			for (String parameter : match.getMapping(re).getTransformationParameters()) {
				possibleParams.put(re.getName() + "." + parameter,
						match.getMapping(re).getTransformationParameterValue(parameter));
			}
		}

		List<String> names = new ArrayList<>(possibleParams.keySet());
		Collections.sort(names, new StringLengthComparator());
		match2SortedParameterList.put(match, names);
		// match2ReplaceParameters.put(match, replace);
		match2Parameters.put(match, possibleParams);
	}

	private String replaceParametersToValues(String s, Match match) {

		List<String> names = match2SortedParameterList.get(match);
		Map<String, String> possibleParams = match2Parameters.get(match);
		for (String name : names) {
			s = s.replaceAll(name, possibleParams.get(name));
		}
		return s;
	}

	private Set<BiologicalNodeAbstract> getNodesOfReplacedParameter(String s, String parameter, Match match) {
		Set<BiologicalNodeAbstract> nodes = new HashSet<>();
		Map<String, String> possibleParams = match2Parameters.get(match);
		String testParam;
		for (RuleNode rn : match.getRule().getBiologicalNodes()) {
			testParam = rn.getName() + "." + parameter;
			if (s.contains(testParam) && possibleParams.containsKey(testParam)) {
				nodes.add(match.getMapping(rn));
			}
		}
		return nodes;
	}

	private Set<BiologicalEdgeAbstract> getEdgesOfReplacedParameter(String s, String parameter, Match match) {
		Set<BiologicalEdgeAbstract> edges = new HashSet<>();
		Map<String, String> possibleParams = match2Parameters.get(match);
		String testParam;
		for (RuleEdge re : match.getRule().getBiologicalEdges()) {
			testParam = re.getName() + "." + parameter;
			if (s.contains(testParam) && possibleParams.containsKey(testParam)) {
				edges.add(match.getMapping(re));
				// System.out.println("contains: " + re.getName() + "." + parameter);
			}
		}
		return edges;
	}

	private void copyParameters(GraphElementAbstract from, GraphElementAbstract to) {
		for (Parameter p : from.getParameters()) {
			// TODO test if parameter with same name exists already, create
			// ParameterController class
			to.getParameters().add(new Parameter(p.getName(), p.getValue(), p.getUnit()));
		}
	}

	public List<Match> getMatches() {
		return this.matches;
	}

	private void createFunctions() {
		// already built in:
		// abs: absolute value
		// acos: arc cosine
		// asin: arc sine
		// atan: arc tangent
		// cbrt: cubic root
		// ceil: nearest upper integer
		// cos: cosine
		// cosh: hyperbolic cosine
		// exp: euler's number raised to the power (e^x)
		// floor: nearest lower integer
		// log: logarithmus naturalis (base e)
		// log10: logarithm (base 10)
		// log2: logarithm (base 2)
		// sin: sine
		// sinh: hyperbolic sine
		// sqrt: square root
		// tan: tangent
		// tanh: hyperbolic tangent
		// signum: signum function

		functions = new HashSet<>();
		Function min = new Function("min", 2) {
			@Override
			public double apply(double... args) {
				return Math.min(args[0], args[1]);
			}
		};
		functions.add(min);

		Function max = new Function("max", 2) {
			@Override
			public double apply(double... args) {
				return Math.max(args[0], args[1]);
			}
		};
		functions.add(max);

		Function random = new Function("random", 2) {
			@Override
			public double apply(double... args) {
				return Math.random() * (args[1] - args[0]) + args[0];
			}
		};
		functions.add(random);

		Function round = new Function("round", 2) {
			@Override
			public double apply(double... args) {
				return Math.rint(args[0]);
			}
		};
		functions.add(round);
	}

	private void createFunctionsBool() {
		functionsBool = new HashSet<>();
		Function and = new Function("and", 2) {
			@Override
			public double apply(double... args) {
				return Math.min(args[0], args[1]);
			}
		};
		functionsBool.add(and);

		Function or = new Function("or", 2) {
			@Override
			public double apply(double... args) {
				return Math.min(1, args[0] + args[1]);
			}
		};
		functionsBool.add(or);

		Function not = new Function("not", 1) {
			@Override
			public double apply(double... args) {
				return (args[0] + 1) % 2;
			}
		};
		functionsBool.add(not);
	}
}
