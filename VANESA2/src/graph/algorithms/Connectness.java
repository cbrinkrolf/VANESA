package graph.algorithms;

import graph.GraphContainer;
import graph.ContainerSingelton;
import graph.GraphInstance;
import graph.algorithms.gui.GraphColorizer;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.Color;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Connectness extends Object {

	MainWindow w = MainWindowSingelton.getInstance();
	GraphContainer con = ContainerSingelton.getInstance();
	GraphInstance graphInstance = new GraphInstance();
	Pathway pw;
	MyGraph mg;
	
	int from, to;

	int nodes = 0;
	int edges = 0;

	int nodei[];
	int nodej[];

	int adjacency[][];
	int nodedepths[];
	int maxpath;
	boolean nodemarked[];

	// lists
	LinkedList<Integer> nodedegrees = new LinkedList<Integer>();
	LinkedList<Integer> nodedegreessingle = new LinkedList<Integer>();
	Hashtable<Integer, Integer> nodeassings = new Hashtable<Integer, Integer>();
	Hashtable<Integer, Integer> nodeassignsback = new Hashtable<Integer,Integer>();
	Hashtable<Integer, Integer> nodedegreetable = new Hashtable<Integer, Integer>();	
	
	private int nodeincrement = 0;

	public Connectness() {
		pw = con.getPathway(w.getCurrentPathway());
		mg = pw.getGraph();
		
		nodes = mg.getAllVertices().size();//pw.countNodes();
		edges = mg.getAllEdges().size();//pw.countEdges();

		nodei = new int[edges + 1];
		nodej = new int[edges + 1];

		adjacency = new int[nodes][nodes];
		
		//Induce mapping to local adjacency matrix and data structures (BNA.ID)
		Iterator<BiologicalNodeAbstract> it = mg.getAllVertices().iterator();
		while(it.hasNext()){
			BiologicalNodeAbstract bna = it.next();	
			reassignNode(bna.getID());			
		}
	
		fillAdjacencyData();
		//countNodeDegrees();
	}

	
// TODO: CALL WITH PATHWAY NAME
//	public Connectness(String pathwayname) {
//		pw = con.getPathway(pathwayname);
//
//		nodes = pw.countNodes();
//		edges = pw.countEdges();
//
//		nodei = new int[edges + 1];
//		nodej = new int[edges + 1];
//
//		adjacency = new int[nodes][nodes];
//		
//		//Induce mapping to local adjacency matrix and data structures (BNA.ID)
//		Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();
//		while(it.hasNext()){
//			BiologicalNodeAbstract bna = it.next();			
//			reassignNode(bna.getID());			
//		}
//
//		fillAdjacencyData();
//		countNodeDegrees();
//	}
	
	public int getNodeDegree(int nodeID){
		return nodedegreetable.get(nodeID);
	}
	
	public int getNodeAssignment(int bnaID){
		return nodeassings.get(bnaID);
	}
	
	public Pathway getPathway(){
		return pw;
	}

	private void reassignNode(int nodeBNAid) {
		if (!nodeassings.containsKey(nodeBNAid)) {
			nodeassings.put(nodeBNAid, nodeincrement);
			nodeassignsback.put(nodeincrement,nodeBNAid);
			System.out.println(nodeBNAid+" assigned to "+nodeincrement);
			nodeincrement++;
		}
	}

	private void fillAdjacencyData() {
		

		Iterator<BiologicalEdgeAbstract> it = mg.getAllEdges().iterator();
		while (it.hasNext()) {
			
			//get Connected Nodes
			BiologicalEdgeAbstract bne = (BiologicalEdgeAbstract) it.next();
			from = ((BiologicalNodeAbstract) bne.getFrom()).getID();
			to = ((BiologicalNodeAbstract) bne.getTo()).getID();
			
			from = nodeassings.get(from);
			to = nodeassings.get(to);
				
			//TODO: EDGES undirected (so far)
			adjacency[from][to] = 1;
			adjacency[to][from] = 1;
		}

		//adjacency arrays
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
	
	@Deprecated
	public Integer numberofCliques() {

		int clique[][] = new int[nodes][nodes + 1];
		GraphTheoryAlgorithms.allCliques(nodes, edges, nodei, nodej, clique);

		return clique[0][0];
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
	
	public Hashtable<Integer,Double> averageNeighbourDegreeTable() {
		Hashtable<Integer,Double> ht = new Hashtable<Integer,Double>(nodes);
				
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
		return (2 * edges) / (1.0f * nodes * (nodes - 1));
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
}