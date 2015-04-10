package cluster.clientimpl;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.algorithms.gui.GraphColoringGUI;
import gui.MainWindow;
import gui.MainWindowSingleton;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import cluster.master.IClusterJobs;
import cluster.slave.JobTypes;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ClusterComputeThread extends Thread {

	private Hashtable<Integer, Double> resulttable;
	private HashSet<HashSet<Integer>> resultset;
	private short[][] shortestdistances, adjmatrix;
	private int[] edgearray, nodearray;
	private int nodes;
	private int job;
	private IClusterJobs jobinterface;
	private ComputeCallback helper;
	private MainWindow mw;

	public ClusterComputeThread(int job, ComputeCallback helper) {
		this.job = job;
		this.helper = helper;
		// setupArrays();
	}

	@Override
	public void run() {
		// compute job on server
		computeInBackground();
//		new TestClusterMapping();
			// RMI Error
		// Else is done by ClientHelper
	}

	public boolean computeInBackground() {

		// Catch if any input Data is given
//		if (adjmatrix == null) {
//			System.out.println("Please set adjacency data.");
//			return false;
//		}

		// MARTIN: set server by job type
		String url = "rmi://cassiopeidae/ClusterJobs";
		// System.setProperty("java.rmi.server.hostname", "cassiopeidae");
		// String url = "rmi://nero/Server";
		// System.setProperty("java.rmi.server.hostname", "nero");
		resulttable = new Hashtable<Integer, Double>();
		try {
			jobinterface = (IClusterJobs) Naming.lookup(url);

			switch (job) {
			case JobTypes.LAYOUT_FR_JOB:
				if (!jobinterface.submitJob(job, edgearray, nodes,helper)) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(
									MainWindowSingleton.getInstance(), "Queue is at maximum capacity!");
						}
					});
					
				}
				break;
				
			case JobTypes.LAYOUT_MULTILEVEL_JOB:
				if (!jobinterface.submitJob(job, edgearray, nodes,helper)) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(
									MainWindowSingleton.getInstance(), "Queue is at maximum capacity!");
						}
					});
					
				}
				break;
				
			case JobTypes.LAYOUT_MDS_FR_JOB:
				if (!jobinterface.submitJob(job, edgearray, nodes,helper)) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(
									MainWindowSingleton.getInstance(), "Queue is at maximum capacity!");
						}
					});
					
				}
				break;

			default:
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(
								MainWindowSingleton.getInstance(),
								"Jobtype not pecified in ComputeThread!");
					}
				});
				break;
			}
			

		}catch (NotBoundException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingleton
							.getInstance().returnFrame(),
							"RMI Interface could not be established.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
			GraphColoringGUI.progressbar.closeWindow();
			mw = MainWindowSingleton.getInstance();
			mw.setLockedPane(false);
			return false;

		} catch (RemoteException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingleton
							.getInstance().returnFrame(),
							"Cluster not reachable.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
			GraphColoringGUI.progressbar.closeWindow();
			mw = MainWindowSingleton.getInstance();
			mw.setLockedPane(false);
			return false;

		} catch (MalformedURLException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingleton
							.getInstance().returnFrame(),
							"Clusteradress could not be resolved.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
			GraphColoringGUI.progressbar.closeWindow();
			mw = MainWindowSingleton.getInstance();
			mw.setLockedPane(false);
			return false;
		}

		return true;
	}
	
	public void setNodes(int nodes){
		this.nodes = nodes;
	}

	public void setAdjMatrix(short adjmatrix[][]) {
		this.adjmatrix = adjmatrix;
	}

	public void setAdjLists(int[] nodearray, int[] edgearray) {
		this.nodearray = nodearray;
		this.edgearray = edgearray;

	}
	
	public void setEdgeArray(int[] edgearray){
		this.edgearray = edgearray;
	}

	//Not used yet,
	//maybe done on server in future
	private void setupArrays() {

		// get Graph hashmap
		MainWindow w = MainWindowSingleton.getInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		Iterator<BiologicalNodeAbstract> it = con
				.getPathway(w.getCurrentPathway()).getAllNodes().iterator();
		HashMap<BiologicalNodeAbstract, HashMap<BiologicalNodeAbstract, BiologicalEdgeAbstract>> vertices = con
				.getPathway(w.getCurrentPathway()).getGraphRepresentation()
				.getAdjacencyList();

		HashMap<BiologicalNodeAbstract, BiologicalEdgeAbstract> innermap;
		Iterator<Entry<BiologicalNodeAbstract, BiologicalEdgeAbstract>> itinner;

		HashMap<BiologicalNodeAbstract, Integer> assignments = new HashMap<BiologicalNodeAbstract, Integer>();
		HashMap<Integer, BiologicalNodeAbstract> assignmentsback = new HashMap<Integer, BiologicalNodeAbstract>();

		BiologicalNodeAbstract node;
		int counter = 0;
		while (it.hasNext()) {
			node = it.next();
			assignments.put(node, counter);
			assignmentsback.put(counter, node);
			counter++;
		}

		// initialize arrays
		int eindex = 0, nodes = vertices.size(), edges = con
				.getPathway(w.getCurrentPathway()).getAllEdges().size(), tmpnodeid;

		nodearray = new int[nodes];
		edgearray = new int[2 * edges]; // undirected

		// Iterate outer map, starting on zero to n-1
		for (int i = 0; i < nodes; i++) {
			// Set current starting position of the adjacency list in edge array
			nodearray[i] = eindex;
			innermap = vertices.get(assignmentsback.get(i));
			itinner = innermap.entrySet().iterator();
			// Iterate inner map
			while (itinner.hasNext()) {
				Entry<BiologicalNodeAbstract, BiologicalEdgeAbstract> connection = itinner
						.next();
				tmpnodeid = assignments.get(connection.getKey());
				// System.out.println(connection.getValue().isDirected());
				edgearray[eindex] = tmpnodeid;
				eindex++;
			}
		}
	}

	public Hashtable<Integer, Double> getResultTable() {
		return resulttable;
	}

	public HashSet<HashSet<Integer>> getResultSet() {
		return resultset;
	}
}
