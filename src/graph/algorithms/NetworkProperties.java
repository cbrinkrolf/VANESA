package graph.algorithms;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Protein;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;

public class NetworkProperties extends Object {

	MainWindow w = MainWindow.getInstance();
	GraphContainer con = GraphContainer.getInstance();
	GraphInstance graphInstance = new GraphInstance();
	Pathway pw;
	public MyGraph mg;

	BiologicalNodeAbstract from, to;

	int nodes = 0;
	int edges = 0;

	int nodei[];
	int nodej[];

	short adjacency[][];
	short distances[][];
	int nodedepths[];
	int maxpath;
	boolean nodemarked[];
	int cutnodes[];
	int cliques[][];

	// lists
	LinkedList<Integer> nodedegrees = new LinkedList<Integer>();
	LinkedList<Integer> nodedegreessingle = new LinkedList<Integer>();
	Hashtable<BiologicalNodeAbstract, Integer> nodeassings = new Hashtable<BiologicalNodeAbstract, Integer>();
	Hashtable<Integer, BiologicalNodeAbstract> nodeassignsback = new Hashtable<Integer, BiologicalNodeAbstract>();
	Hashtable<Integer, Integer> nodedegreetable = new Hashtable<Integer, Integer>();
	Hashtable<Integer, Double> neighbordegreetable = new Hashtable<Integer, Double>();
	

	private int nodeincrement = 0;

	public NetworkProperties() {
		pw = con.getPathway(w.getCurrentPathway());
		mg = pw.getGraph();

		nodes = mg.getAllVertices().size();
		edges = mg.getAllEdges().size();

		nodei = new int[edges + 1];
		nodej = new int[edges + 1];

		adjacency = new short[nodes][nodes];

		// Induce mapping to local adjacency matrix and data structures (BNA.ID)
		Iterator<BiologicalNodeAbstract> it = mg.getAllVertices().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = it.next();
			reassignNodeBNA(bna);
		}


		fillAdjacencyData();
		countNodeDegrees();
		averageNeighbourDegreeTable();

	}

	public NetworkProperties(String pathwayname) {
		pw = con.getPathway(pathwayname);
		mg = pw.getGraph();

		nodes = mg.getAllVertices().size();
		edges = mg.getAllEdges().size();

		nodei = new int[edges + 1];
		nodej = new int[edges + 1];

		adjacency = new short[nodes][nodes];

		// Induce mapping to local adjacency matrix and data structures (BNA.ID)
		Iterator<BiologicalNodeAbstract> it = mg.getAllVertices().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = it.next();
			reassignNodeBNA(bna);
		}

		fillAdjacencyData();
		countNodeDegrees();
		averageNeighbourDegreeTable();
	}

	public HashMap<Integer, Integer> getNodeDegreeDistribution(){
		HashMap<Integer, Integer> occurrenceMap = new HashMap<Integer, Integer>();

		Set<BiologicalNodeAbstract> pickedbnas =  mg.getVisualizationViewer().getPickedVertexState().getPicked();
	 
		if(pickedbnas.size()>0){
			System.out.println(pickedbnas.size());
			int degree = 0;
			//Count occurrences, just picked nodes
			for(BiologicalNodeAbstract bna : pickedbnas){
				degree = nodedegreetable.get(nodeassings.get(bna));
				if (occurrenceMap.containsKey(degree)) {
					//not first
					occurrenceMap.put(degree, occurrenceMap.get(degree) + 1);
				} else {
					// first
					occurrenceMap.put(degree, 1);
				}			
			}	
		}else{
			//Count occurrences, all nodes
			for (int degree : nodedegrees) {				
				if (occurrenceMap.containsKey(degree)) {
					//not first
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
	
	public double getNeighborDegree(int nodeID){
		return neighbordegreetable.get(nodeID);
	}

	public int getNodeAssignment(BiologicalNodeAbstract bna) {
		return nodeassings.get(bna);
	}

	public BiologicalNodeAbstract getNodeAssignmentbackwards(int nodeid) {
		return nodeassignsback.get(nodeid);
	}

	public Pathway getPathway() {
		return pw;
	}

	private void reassignNodeBNA(BiologicalNodeAbstract nodeBNA) {
		if (!nodeassings.containsKey(nodeBNA)) {
			nodeassings.put(nodeBNA, nodeincrement);
			nodeassignsback.put(nodeincrement, nodeBNA);
			// debug
			// System.out.println(nodeBNA.getID()+" "+nodeBNA.getLabel()+" assigned to "+nodeincrement);
			nodeincrement++;
		}
	}

	private void fillAdjacencyData() {

		int fromid, toid;
		Iterator<BiologicalEdgeAbstract> it = mg.getAllEdges().iterator();
		while (it.hasNext()) {

			// get Connected Nodes
			BiologicalEdgeAbstract bne = (BiologicalEdgeAbstract) it.next();
			from = ((BiologicalNodeAbstract) bne.getFrom());
			to = ((BiologicalNodeAbstract) bne.getTo());

			fromid = nodeassings.get(from);
			toid = nodeassings.get(to);

			// EDGES for analysis undirected
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

		int component[] = new int[nodes + 1];
		GraphTheoryAlgorithms.connectedComponents(nodes, edges, nodei, nodej,
				component);
		return component[0];
	}

	public boolean isGraphConnected() {

		if (GraphTheoryAlgorithms.connected(nodes, edges, nodei, nodej))
			return true;
		else
			return false;
	}

	public boolean isGraphPlanar() {

		return GraphTheoryAlgorithms.planarityTesting(nodes, edges, nodei,
				nodej);

	}

	public float averageShortestPathLength() {
		// compute all shortest Path lengths
		int weight[], mindistance[] = new int[nodes + 1];
		;
		boolean directed[] = new boolean[edges + 1];
		weight = new int[edges + 1];
		weight[0] = 0;
		for (int i = 1; i < weight.length; i++) {
			weight[i] = 1;
		}
		GraphTheoryAlgorithms.allShortestPathLength(nodes, edges, nodei, nodej,
				directed, weight, 1, mindistance);

		// compute average shortest Path length
		int pathsum = 0;
		for (int i = 1; i < mindistance.length; i++) {
			pathsum += mindistance[i];
		}

		return pathsum / (nodes * 1.0f);
	}

	public int countNodeDegrees() {
		// Count of different Node degrees
		nodedegrees = new LinkedList<Integer>();
		// count node degrees per node and save them
		int degree = 0;
		for (int i = 0; i < nodes; i++) {
			// row-wise addition
			for (int j = 0; j < nodes; j++) {
				if (adjacency[i][j] == 1)
					degree++;
			}
			// ogranize lists and tables
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
		Hashtable<BiologicalNodeAbstract, Double> ht = new Hashtable<BiologicalNodeAbstract, Double>(
				nodes);

		double oneavgdegree = 0.0f;
		int verticedegrees[] = new int[nodes], degree = 0;
		// Count Vertex Degrees and store them in an Array
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
			//save neighbordegree
			nodeassignsback.get(i).addAttribute(NodeAttributeTypes.GRAPH_PROPERTY, NodeAttributeNames.NEIGHBOR_DEGREE, oneavgdegree);			
			oneavgdegree = 0f;
		}

		// debug
		// Iterator<Double> iit = ht.values().iterator();
		// while (iit.hasNext()) {
		// double d = iit.next();
		// System.out.println(d);
		//
		// }

		return ht;
	}

	// Version for Filtering
	public Hashtable<BiologicalNodeAbstract, Double> averageNeighbourDegreeTable(
			double min, double max) {
		Hashtable<BiologicalNodeAbstract, Double> ht = new Hashtable<BiologicalNodeAbstract, Double>(
				nodes);

		double oneavgdegree = 0.0f;
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
			for (int j = 0; j < nodedepths.length; j++) {
				if (maxpath < nodedepths[j])
					maxpath = nodedepths[j];
			}
			// reset marking
			nodemarked = new boolean[nodes + 1];
			nodemarked[0] = true;
		}

		return maxpath;
	}

	public double getDensity() {
		// Graph Density
		return (2 * edges) / (1.0d * nodes * (nodes - 1));
	}

	public double getCentralization() {
		// Centralization
		// n/n-2 * ( max(k)/n-1 - density )
		return ((nodes / (nodes - 2) * 1.0f))
				* (((Collections.max(nodedegrees)) / ((nodes - 1) * 1.0f)) - getDensity());
	}

	public int getMinDegree() {
		// Minimum Node degree
		return Collections.min(nodedegrees);
	}

	public int getMaxDegree() {
		// Maximum Node degree
		return Collections.max(nodedegrees);
	}

	public double getAvgNodeDegree() {
		// Average Node degree
		double avgdegree = 0f;
		for (int i = 0; i < nodedegrees.size(); i++) {
			avgdegree += nodedegrees.get(i);
		}
		avgdegree /= (nodes * 1.0f);
		return avgdegree;
	}

	public double getGlobalMatchingIndex() {
		// global matching index
		Set<Integer> nodeset1, nodeset2;
		double matchingindex = 0.0f;

		// make nodesets for each node
		nodeset1 = new HashSet<Integer>();
		nodeset2 = new HashSet<Integer>();
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

			cutnodes = new int[nodes + 1];
			
			System.out.println(nodes+" "+edges);
			System.out.println(Arrays.toString(nodei).replaceAll("\\[", "").replaceAll("\\]", ""));
			System.out.println(Arrays.toString(nodej).replaceAll("\\[", "").replaceAll("\\]", ""));
			
			GraphTheoryAlgorithms
					.cutNodes(nodes, edges, nodei, nodej, cutnodes);

			// Visualization
			// for (int i = 1; i < cutnodes.length; i++) {
			//
			// if(cutnodes[i]>0){
			// BiologicalNodeAbstract tmpnode =
			// nodeassignsback.get(cutnodes[i]-1);
			// tmpnode.setColor(Color.yellow);
			// tmpnode.setNodesize(2.0);
			// }
			//
			// }
			// GraphInstance.getMyGraph().getVisualizationViewer().repaint();

			return cutnodes;
	}

	// directed only
	public int[] getStronglyConnectedComponents() {
		int[] scc = new int[nodes + 1];

		GraphTheoryAlgorithms.stronglyConnectedComponents(nodes, edges, nodei,
				nodej, scc);

		// System.out.println("strongly connected components: "+scc[0]);

		return scc;

	}

	// directed only
	public void getMinimalEqGraph() {
		boolean links[] = new boolean[edges + 1];

		GraphTheoryAlgorithms.minimalEquivalentGraph(nodes, edges, nodei,
				nodej, links);

	}

	// return the number of edges that would disconnect the Network, works only
	// with connected networks
	public int getEdgeConnectivity() {
		int econ;

		econ = GraphTheoryAlgorithms.edgeConnectivity(nodes, edges, nodei,
				nodej);

		return econ;
	}

	public int getFundamentalCycles() {
		// k is the expected number of independent cycles, here automatically
		// calculated (could be set by user)
		int k = (int) Math.min((double) edges - 2,
				((double) (nodes - 1) * (nodes - 2)) / 2.0d);
		int fundcyc[][] = new int[k + 1][nodes + 1];

		GraphTheoryAlgorithms.fundamentalCycles(nodes, edges, nodei, nodej,
				fundcyc);

		return fundcyc[0][0];
	}

	public void saveAdjMatrix(String filename) {
		// adj matrix
		try {
			FileWriter fw = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fw);

			String line = "";
			for (int i = 0; i < nodes; i++) {
				for (int j = 0; j < nodes; j++) {
					line += adjacency[i][j];
				}
				out.write(line + "\n");
				line = "";
			}

			out.close();
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// attributes
		try {
			FileWriter fw = new FileWriter(filename + ".atr");
			BufferedWriter out = new BufferedWriter(fw);

			BiologicalNodeAbstract bnb;

			String line = "";
			for (int i = 0; i < nodes; i++) {
				line = i + "\t" + nodedegreetable.get(i) + "\t"
						+ ((int) (Math.random() * 5));

				bnb = nodeassignsback.get(i);
				if (bnb instanceof Protein) {
					Protein p = (Protein) bnb;
					line += "\t" + p.getAaSequence().length();
				}

				line += "\t" + ((int) (Math.random() * 10)) + "\t"
						+ ((int) (Math.random() * 20));
				out.write(line + "\n");
				line = "";
			}

			out.close();
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void savePackedAdjList(String filename) {
		// array of size(nodes): narray
		// array of size(edges - 2*edges): earray
		int narray[] = new int[nodes], earray[] = new int[2 * edges];
		int arraypointer = 0;

		// narray: each position of the array points to a position in earray
		// (begin of the adjacency list of this node)
		for (int i = 0; i < nodes; i++) {
			narray[i] = arraypointer;
			for (int j = 0; j < nodes; j++) {
				if (adjacency[i][j] == 1) {
					earray[arraypointer] = j;
					arraypointer++;
				}
			}
		}

		// write to File
		try {
			FileWriter fw = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fw);

			for (int i = 0; i < narray.length; i++) {// write node array
				if (i == narray.length - 1)
					out.write(narray[i] + "");
				else
					out.write(narray[i] + "\t");
			}
			out.write("\n");
			for (int i = 0; i < arraypointer; i++) {// write edge array
				if (i == arraypointer - 1)
					out.write(earray[i] + "");
				else
					out.write(earray[i] + "\t");
			}

			out.close();
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public short[][] AllPairShortestPaths(boolean writeToFile) {
		//Time
		//long start = System.currentTimeMillis();
		//System.out.println("starting apsp");
		

		distances = new short[nodes][nodes];		
		int i, j, k;		
		
		// FloydWarshall
		// initialize distance matrix

		for (i = 0; i < nodes; i++) {
			for (j = 0; j < nodes; j++) {	
				if(i == j)
					distances[i][j] = 0;
				else 
					if(adjacency[i][j] == 0)				
					distances[i][j] = Short.MAX_VALUE/2;
				else
					distances[i][j] = adjacency[i][j];
			}
		}

		short dist;
		// run the floyd warshall
		for (k = 0; k < nodes; k++) {
			for (i = 0; i < nodes; i++) {
				for (j = 0; j < nodes; j++) {				
					dist = (short) (distances[i][k]+distances[k][j]);
					if(distances[i][j] > dist){
						distances[i][j] = dist;						
					}
				}
			}
		}

		// write to File
		if (writeToFile)
			try {
				FileWriter fw = new FileWriter("distance");
				BufferedWriter out = new BufferedWriter(fw);

				for (i = 0; i < nodes; i++) {
					for (j = 0; j < nodes; j++) {
						if (j == nodes - 1)
							out.write(distances[i][j] + "");
						else
							out.write(distances[i][j] + "\t");
					}
					out.write("\n");
				}

				out.close();
				fw.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		
		//Time
		//System.out.println("apsp:"+ (System.currentTimeMillis()-start)/1000.0);
		
		return distances;
	}

	public void removeGreyNodes() {

		HashSet<BiologicalNodeAbstract> removals = new HashSet<BiologicalNodeAbstract>();
		Iterator<BiologicalNodeAbstract> itn = mg.getAllVertices().iterator();
		while (itn.hasNext()) {
			BiologicalNodeAbstract tmp = itn.next();
			if (tmp.getColor().getRGB() == -4144960)
				removals.add(tmp);
		}

		itn = removals.iterator();
		while (itn.hasNext()) {
			mg.removeVertex(itn.next());
		}
		w.updateElementTree();
		w.updatePathwayTree();

	}

	public void getSpanningTree() {
		
		int[] weight = new int[edges+1], treearc1 = new int[nodes], treearc2 = new int[nodes];
		
		Arrays.fill(weight, 10);
		
		
		GraphTheoryAlgorithms.minimumSpanningTreeKruskal(nodes, edges, nodei, nodej, weight, treearc1, treearc2);
		System.out.println(Arrays.toString(treearc1));
		System.out.println(Arrays.toString(treearc2));
		
		BiologicalNodeAbstract bnax,bnay;
		for(int i = 1; i<=treearc1[0]; i++){
			bnax = nodeassignsback.get(treearc1[i]-1);
			bnay = nodeassignsback.get(treearc2[i]-1);
			
			pw.getEdge(bnax, bnay).setColor(Color.red);
			System.out.println(bnax.getLabel()+" - "+ bnay.getLabel());
		}
		
		
		
	}
	
	public int[] getNodeI(){
		return nodei;
	}
	
	public int[] getNodeJ(){
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