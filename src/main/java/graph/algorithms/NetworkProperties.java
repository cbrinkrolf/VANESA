package graph.algorithms;

import java.awt.Color;
import java.util.*;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;

public class NetworkProperties {
	private final MainWindow w = MainWindow.getInstance();
	private final Pathway pw;
	private final MyGraph mg;

	private final int nodes;
	private final int edges;

	private final int[] nodei;
	private final int[] nodej;

	private final short[][] adjacency;
	private int[] nodedepths;
	private int maxpath;
	private boolean[] nodemarked;

	private List<Integer> nodedegrees = new ArrayList<>();
	private List<Integer> nodedegreessingle = new ArrayList<>();
	private final Hashtable<BiologicalNodeAbstract, Integer> nodeassings = new Hashtable<>();
	private final Hashtable<Integer, BiologicalNodeAbstract> nodeassignsback = new Hashtable<>();
	private final Hashtable<Integer, Integer> nodedegreetable = new Hashtable<>();
	private final Hashtable<Integer, Double> neighbordegreetable = new Hashtable<>();

	private int nodeincrement = 0;

	public NetworkProperties() {
		this(GraphInstance.getPathway());
	}

	public NetworkProperties(final Pathway pw) {
		this.pw = pw;
		mg = pw.getGraph();

		nodes = mg.getAllVertices().size();
		edges = mg.getAllEdges().size();

		nodei = new int[edges + 1];
		nodej = new int[edges + 1];

		adjacency = new short[nodes][nodes];

		// Induce mapping to local adjacency matrix and data structures (BNA.ID)
		for (final BiologicalNodeAbstract bna : mg.getAllVertices()) {
			reassignNodeBNA(bna);
		}

		fillAdjacencyData();
		countNodeDegrees();
		averageNeighbourDegreeTable();
	}

	public Map<Integer, Integer> getNodeDegreeDistribution() {
		Map<Integer, Integer> occurrenceMap = new HashMap<>();
		Set<BiologicalNodeAbstract> pickedbnas = mg.getVisualizationViewer().getPickedVertexState().getPicked();
		if (pickedbnas.isEmpty()) {
			// Count occurrences, all nodes
			for (int degree : nodedegrees) {
				if (occurrenceMap.containsKey(degree)) {
					// not first
					occurrenceMap.put(degree, occurrenceMap.get(degree) + 1);
				} else {
					// first
					occurrenceMap.put(degree, 1);
				}
			}
		} else {
			System.out.println(pickedbnas.size());
			// Count occurrences, just picked nodes
			for (BiologicalNodeAbstract bna : pickedbnas) {
				int degree = nodedegreetable.get(nodeassings.get(bna));
				if (occurrenceMap.containsKey(degree)) {
					// not first
					occurrenceMap.put(degree, occurrenceMap.get(degree) + 1);
				} else {
					// first
					occurrenceMap.put(degree, 1);
				}
			}
		}

		return occurrenceMap;
	}

	public short[][] getAdjacencyMatrix() {
		return adjacency;
	}

	public int getNodeDegree(int nodeID) {
		return nodedegreetable.get(nodeID);
	}

	public double getNeighborDegree(int nodeID) {
		return neighbordegreetable.get(nodeID);
	}

	public int getNodeAssignment(BiologicalNodeAbstract bna) {
		return nodeassings.get(bna);
	}

	public BiologicalNodeAbstract getNodeAssignmentBackwards(int nodeid) {
		return nodeassignsback.get(nodeid);
	}

	public Pathway getPathway() {
		return pw;
	}

	private void reassignNodeBNA(BiologicalNodeAbstract nodeBNA) {
		if (!nodeassings.containsKey(nodeBNA)) {
			nodeassings.put(nodeBNA, nodeincrement);
			nodeassignsback.put(nodeincrement, nodeBNA);
			nodeincrement++;
		}
	}

	private void fillAdjacencyData() {
		for (BiologicalEdgeAbstract bne : mg.getAllEdges()) {
			int fromid = nodeassings.get(bne.getFrom());
			int toid = nodeassings.get(bne.getTo());
			// Adjacency matrix is constructed undirected for analysis
			adjacency[fromid][toid] = 1;
			adjacency[toid][fromid] = 1;
		}

		// adjacency arrays
		int nodecounter = 1;
		for (int i = 0; i < nodes; i++) {
			for (int j = i; j < nodes; j++) {
				if (adjacency[i][j] == 1) {
					nodei[nodecounter] = i + 1;
					nodej[nodecounter] = j + 1;
					nodecounter++;
				}
			}
		}
	}

	public Integer connectedComponents() {
		int[] component = new int[nodes + 1];
		GraphTheoryAlgorithms.connectedComponents(nodes, edges, nodei, nodej, component);
		return component[0];
	}

	public boolean isGraphConnected() {
		return GraphTheoryAlgorithms.connected(nodes, edges, nodei, nodej);
	}

	public boolean isGraphPlanar() {
		return GraphTheoryAlgorithms.planarityTesting(nodes, edges, nodei, nodej);
	}

	public float averageShortestPathLength() {
		// compute all shortest Path lengths
		int[] weight, mindistance = new int[nodes + 1];
		boolean[] directed = new boolean[edges + 1];
		weight = new int[edges + 1];
		weight[0] = 0;
		for (int i = 1; i < weight.length; i++) {
			weight[i] = 1;
		}
		GraphTheoryAlgorithms.allShortestPathLength(nodes, edges, nodei, nodej, directed, weight, 1, mindistance);

		// compute average shortest Path length
		int pathsum = 0;
		for (int i = 1; i < mindistance.length; i++) {
			pathsum += mindistance[i];
		}

		return pathsum / (nodes * 1.0f);
	}

	public int countNodeDegrees() {
		// Count of different Node degrees
		nodedegrees = new LinkedList<>();
		// count node degrees per node and save them
		int degree = 0;
		for (int i = 0; i < nodes; i++) {
			// row-wise addition
			for (int j = 0; j < nodes; j++) {
				if (adjacency[i][j] == 1)
					degree++;
			}
			// organize lists and tables
			if (!nodedegreessingle.contains(degree) && degree > 0)
				nodedegreessingle.add(degree);
			nodedegrees.add(degree);
			nodedegreetable.put(i, degree);
			// reset of degree
			degree = 0;
		}

		Collections.sort(nodedegreessingle);
		return nodedegreessingle.size();
	}

	public float averageNeighbourDegree() {
		float avgneighdegree = 0.0f, oneavgdegree = 0.0f;
		int verticedegrees[] = new int[nodes], degree = 0;
		// Count Vertice Degrees and store them in an Array
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				if (adjacency[i][j] == 1)
					degree++;
			}
			verticedegrees[i] = degree;
			degree = 0;
		}
		// Add Neighbour Degrees and divide
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				if (adjacency[i][j] == 1)
					oneavgdegree += verticedegrees[j];
			}
			if (verticedegrees[i] != 0)// if current node not connected
				avgneighdegree += (oneavgdegree / verticedegrees[i]);
			oneavgdegree = 0f;
		}
		// Divide by N (Number of Nodes)
		return avgneighdegree /= (nodes * 1.0f);
	}

	public Hashtable<BiologicalNodeAbstract, Double> averageNeighbourDegreeTable() {
		Hashtable<BiologicalNodeAbstract, Double> ht = new Hashtable<>(nodes);
		double oneavgdegree = 0.0f;
		int[] verticedegrees = new int[nodes];
		int degree = 0;
		// Count vertex degrees and store them in an Array
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				if (adjacency[i][j] == 1)
					degree++;
			}
			verticedegrees[i] = degree;
			degree = 0;
		}
		// Add Neighbor Degrees and divide
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				if (adjacency[i][j] == 1)
					oneavgdegree += verticedegrees[j];
			}
			if (verticedegrees[i] != 0)// if current node is connected
				oneavgdegree = (oneavgdegree / verticedegrees[i]);

			ht.put(nodeassignsback.get(i), oneavgdegree);
			// save neighbordegree
			nodeassignsback.get(i).addAttribute(NodeAttributeType.GRAPH_PROPERTY, NodeAttributeNames.NEIGHBOR_DEGREE, oneavgdegree);
			oneavgdegree = 0f;
		}
		return ht;
	}

	// Version for Filtering
	public Hashtable<BiologicalNodeAbstract, Double> averageNeighbourDegreeTable(double min, double max) {
		Hashtable<BiologicalNodeAbstract, Double> ht = new Hashtable<>(nodes);
		double oneavgdegree = 0.0f;
		int[] verticedegrees = new int[nodes];
		int degree = 0;
		// Count vertex degrees and store them in an Array
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				if (adjacency[i][j] == 1)
					degree++;
			}
			verticedegrees[i] = degree;
			degree = 0;
		}
		// Add Neighbour Degrees and divide
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				if (adjacency[i][j] == 1)
					oneavgdegree += verticedegrees[j];
			}
			if (verticedegrees[i] != 0)// if current node not connected
				oneavgdegree += (oneavgdegree / verticedegrees[i]);
			if (oneavgdegree >= min && oneavgdegree <= max)
				ht.put(nodeassignsback.get(i), oneavgdegree);
			oneavgdegree = 0f;
		}
		return ht;
	}

	public int maxPathLength() {
		// With Deep first search on all nodes
		nodemarked = new boolean[nodes + 1];
		nodedepths = new int[nodes + 1];
		nodemarked[0] = true;
		// start at first node
		for (int i = 1; i <= nodes; i++) {
			deepfirst(i, 0);
			for (final int nodedepth : nodedepths) {
				if (maxpath < nodedepth) {
					maxpath = nodedepth;
				}
			}
			// reset marking
			nodemarked = new boolean[nodes + 1];
			nodemarked[0] = true;
		}
		return maxpath;
	}

	public double getDensity() {
		return (2 * edges) / (nodes * (nodes - 1f));
	}

	public double getCentralization() {
		// n/n-2 * ( max(k)/n-1 - density )
		return (nodes / (nodes - 2f)) * ((Collections.max(nodedegrees) / (nodes - 1f)) - getDensity());
	}

	public int getMinDegree() {
		return Collections.min(nodedegrees);
	}

	public int getMaxDegree() {
		return Collections.max(nodedegrees);
	}

	public double getAvgNodeDegree() {
		double avgdegree = 0f;
		for (final Integer degree : nodedegrees) {
			avgdegree += degree;
		}
		return avgdegree / nodes;
	}

	public double getGlobalMatchingIndex() {
		// global matching index
		double matchingindex = 0.0f;

		// make nodesets for each node
		Set<Integer> nodeset1 = new HashSet<>();
		Set<Integer> nodeset2 = new HashSet<>();
		int similarnodes = 0, paircounter = 0;
		float allnodes = 0f;

		// traverse Nodepairs
		for (int i = 0; i < (nodes - 1); i++) {
			for (int j = i + 1; j < nodes; j++) {
				for (int k = 0; k < nodes; k++) {
					if (adjacency[i][k] == 1)
						nodeset1.add(k);
					if (adjacency[j][k] == 1)
						nodeset2.add(k);
				}
				// count similars
				for (Iterator<Integer> ns1 = nodeset1.iterator(); ns1.hasNext();) {
					int s = ns1.next();
					if (nodeset2.contains(s))
						similarnodes++;
				}

				// if similars = 0 skip
				if (similarnodes == 0) {
					// clear Lists and similars
					nodeset1.clear();
					nodeset2.clear();
					similarnodes = 0;
					paircounter++;
					continue;
				} else {
					// count degrees and substract 2xsimilars
					allnodes = (nodedegreetable.get(i) + nodedegreetable.get(j) - (2.0f * similarnodes));
					if (allnodes > 0)
						matchingindex += (similarnodes / allnodes);
				}
				// clear Lists and similars
				nodeset1.clear();
				nodeset2.clear();
				similarnodes = 0;
				paircounter++;
			}
		}
		matchingindex /= (paircounter * 1.0f);

		return matchingindex;
	}

	// Deep-First Search Algorithm used in maxPathLength()
	public void deepfirst(int node, int depth) {
		// mark node
		nodemarked[node] = true;
		nodedepths[node] = depth;
		// recursesive child call
		for (int i = 0; i < nodei.length; i++) {
			if (nodei[i] == node && !nodemarked[nodej[i]]) {
				deepfirst(nodej[i], depth + 1);
			} else if (nodej[i] == node && !nodemarked[nodei[i]]) {
				deepfirst(nodei[i], depth + 1);
			}
		}
	}

	public int getNodeCount() {
		return nodes;
	}

	public int getEdgeCount() {
		return edges;
	}

	// return all nodes that would disconnect the Network, works only with
	// connected networks
	public int[] getCutNodes() {
		if (!isGraphConnected()) {
			return new int[1];
		}
		final int[] cutnodes = new int[nodes + 1];
		GraphTheoryAlgorithms.cutNodes(nodes, edges, nodei, nodej, cutnodes);
		return cutnodes;
	}

	// directed only
	public int[] getStronglyConnectedComponents() {
		int[] scc = new int[nodes + 1];
		GraphTheoryAlgorithms.stronglyConnectedComponents(nodes, edges, nodei, nodej, scc);
		return scc;
	}

	// directed only
	public void getMinimalEqGraph() {
		boolean[] links = new boolean[edges + 1];
		GraphTheoryAlgorithms.minimalEquivalentGraph(nodes, edges, nodei, nodej, links);
	}

	/**
	 * Calculate the number of edges that would disconnect the network. Works only with connected networks.
	 */
	public int getEdgeConnectivity() {
		return GraphTheoryAlgorithms.edgeConnectivity(nodes, edges, nodei, nodej);
	}

	public int getFundamentalCycles() {
		// k is the expected number of independent cycles, here automatically
		// calculated (could be set by user)
		int k = (int) Math.min((double) edges - 2, ((double) (nodes - 1) * (nodes - 2)) / 2.0d);
		int fundcyc[][] = new int[k + 1][nodes + 1];

		GraphTheoryAlgorithms.fundamentalCycles(nodes, edges, nodei, nodej, fundcyc);

		return fundcyc[0][0];
	}

	public short[][] AllPairShortestPaths() {
		final short[][] distances = new short[nodes][nodes];
		// FloydWarshall
		// initialize distance matrix
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < nodes; j++) {
				if (i == j)
					distances[i][j] = 0;
				else if (adjacency[i][j] == 0)
					distances[i][j] = Short.MAX_VALUE / 2;
				else
					distances[i][j] = adjacency[i][j];
			}
		}
		// run the floyd warshall
		for (int k = 0; k < nodes; k++) {
			for (int i = 0; i < nodes; i++) {
				for (int j = 0; j < nodes; j++) {
					short dist = (short) (distances[i][k] + distances[k][j]);
					if (distances[i][j] > dist) {
						distances[i][j] = dist;
					}
				}
			}
		}
		return distances;
	}

	public void removeGreyNodes() {
		final Set<BiologicalNodeAbstract> removals = new HashSet<>();
		for (final BiologicalNodeAbstract bna : mg.getAllVertices()) {
			if (bna.getColor().getRGB() == -4144960)
				removals.add(bna);
		}
		for (final BiologicalNodeAbstract bna : removals) {
			mg.removeVertex(bna);
		}
		w.updateElementTree();
		w.updatePathwayTree();

	}

	public void getSpanningTree() {
		int[] weight = new int[edges + 1], treearc1 = new int[nodes], treearc2 = new int[nodes];
		Arrays.fill(weight, 10);

		GraphTheoryAlgorithms.minimumSpanningTreeKruskal(nodes, edges, nodei, nodej, weight, treearc1, treearc2);
		System.out.println(Arrays.toString(treearc1));
		System.out.println(Arrays.toString(treearc2));

		BiologicalNodeAbstract bnax, bnay;
		for (int i = 1; i <= treearc1[0]; i++) {
			bnax = nodeassignsback.get(treearc1[i] - 1);
			bnay = nodeassignsback.get(treearc2[i] - 1);

			pw.getEdge(bnax, bnay).setColor(Color.red);
			System.out.println(bnax.getLabel() + " - " + bnay.getLabel());
		}
	}

	public int[] getNodeI() {
		return nodei;
	}

	public int[] getNodeJ() {
		return nodej;
	}

	// public int getNumberOfCliques(){
	//
	// if(isGraphConnected()){
	// cliques = new int[nodes+1][nodes+1];
	// GraphTheoryAlgorithms.allCliques(nodes, edges, nodei, nodej, cliques);
	//
	// for (int i = 1; i < cliques.length; i++) {
	// for (int j = 1; j < cliques[0].length; j++) {
	// if(cliques[i][j]>0){
	// BiologicalNodeAbstract tmpnode = nodeassignsback.get(cliques[i][j]-1);
	// System.out.print(tmpnode.getLabel()+" ");
	// }
	//
	// }
	// System.out.println();
	// }
	//
	// return cliques[0][0];
	// }else
	// return -1;
	//
	// }
}