package cluster;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JOptionPane;

import biologicalElements.InternalGraphRepresentation;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ClusterComputeClient {

	public static final int CYCLE_JOB_OCCURRENCE = 10,
			CYCLE_JOB_NEIGHBORS = 11, CLIQUE_JOB_OCCURRENCE = 20,
			CLIQUE_JOB_NEIGHBORS = 21, CLIQUE_JOB_PATHSLESS = 22,
			CLIQUE_JOB_CONNECTIVITY = 23, APSP_JOB = 30;

	Hashtable<Integer, Double> result;
	int[][] shortestdistances, adjmatrix;
	int[] edgearray, nodearray;
	int job;

	public ClusterComputeClient(int job) {
		this.job = job;
		//setupArrays();
	}
	
	
	public boolean start(){
		
		//Catch if any input Data is given
		if(adjmatrix == null ){
			System.out.println("Please set adjacency data.");
			return false;
		} 
		
		// MARTIN: set server by job type
		String url = "rmi://cassiopeidae/Server";
		System.setProperty("java.rmi.server.hostname", "cassiopeidae");
//		String url = "rmi://nero/Server";
//		System.setProperty("java.rmi.server.hostname", "nero");
		result = new Hashtable<Integer, Double>();
		try {
			JobServer server = (JobServer) Naming.lookup(url);

			// determine job
			switch (job) {
			case CYCLE_JOB_OCCURRENCE:
				result = server.getCycleValues(adjmatrix);
				break;
			case CLIQUE_JOB_OCCURRENCE:
				result = server.getCliqueValues(adjmatrix);
				break;
			case APSP_JOB:
				shortestdistances = server.getAllPairShortestPaths(nodearray,
						edgearray);
				
				//Test shortest distances by BNA Label			
				break;

			default:
				System.err.println("ERROR! JobType not found.");
				break;
			}

		}
		// debug
		// System.out.println("Cycle computation Result: "+result);
		catch (NotBoundException e) {
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance()
					.returnFrame(), "RMI Interface could not be established.",
					"Error", JOptionPane.ERROR_MESSAGE);
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance()
					.returnFrame(), "Cluster not reachable.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance()
					.returnFrame(), "Clusteradress could not be resolved.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return true;
	}
	
	public void setAdjMatrix(int adjmatrix[][]){
		this.adjmatrix = adjmatrix;
	}
	
	public void setAdjLists(int[] nodearray, int[] edgearray){
		this.nodearray = nodearray;
		this.edgearray = edgearray;
		
	}
	

	private void setupArrays() {

		// get Graph hashmap
		MainWindow w = MainWindowSingelton.getInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		Iterator<BiologicalNodeAbstract> it = con
				.getPathway(w.getCurrentPathway()).getAllNodes().iterator();
		HashMap<BiologicalNodeAbstract, HashMap<BiologicalNodeAbstract, BiologicalEdgeAbstract>> vertices = con
				.getPathway(w.getCurrentPathway()).getGraphRepresentation()
				.getAdjacencyList();
		
		HashMap<BiologicalNodeAbstract,BiologicalEdgeAbstract> innermap;
		Iterator<Entry<BiologicalNodeAbstract, BiologicalEdgeAbstract>> itinner;
						
		HashMap<BiologicalNodeAbstract, Integer> assignments = new HashMap<BiologicalNodeAbstract, Integer>();
		HashMap<Integer,BiologicalNodeAbstract> assignmentsback = new HashMap<Integer,BiologicalNodeAbstract>();

		BiologicalNodeAbstract node;
		int counter = 0;
		while (it.hasNext()) {
			node = it.next();
			assignments.put(node, counter);
			assignmentsback.put(counter,node);
			counter++;
		}	

		// initialize arrays
		int eindex=0,
				nodes = vertices.size(), 
				edges = con.getPathway(w.getCurrentPathway()).getAllEdges().size(),
				tmpnodeid;
		
		nodearray = new int[nodes];
		edgearray = new int[2*edges]; //undirected
		
		//Iterate outer map, starting on zero to n-1
		for (int i = 0; i < nodes; i++) {			
			//Set current starting position of the adjacency list in edge array
			nodearray[i] = eindex;			
			innermap = vertices.get(assignmentsback.get(i));
			itinner = innermap.entrySet().iterator();
			//Iterate inner map
			while (itinner.hasNext()) {
				Entry<BiologicalNodeAbstract, BiologicalEdgeAbstract> connection = itinner.next();
				tmpnodeid = assignments.get(connection.getKey());
				//System.out.println(connection.getValue().isDirected());
				edgearray[eindex] = tmpnodeid;
				eindex++;
			}
		}
	}

	public Hashtable<Integer, Double> getResultTable() {
		return result;
	}

}
