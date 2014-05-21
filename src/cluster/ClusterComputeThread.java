package cluster;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.algorithms.gui.GraphColoringGUI;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ClusterComputeThread extends Thread {

	private Hashtable<Integer, Double> resulttable;
	private HashSet<HashSet<Integer>> resultset;
	private int[][] shortestdistances, adjmatrix;
	private int[] edgearray, nodearray;
	private int job;
	private IJobServer server;
	private ClientHelper helper;
	private MainWindow mw;

	public ClusterComputeThread(int job, ClientHelper helper) {
		this.job = job;
		this.helper = helper;
		// setupArrays();
	}

	@Override
	public void run() {
		// compute job on server
		if (!computeInBackground()) {
			// RMI Error
		}// Else is done by ClientHelper
	}

	public boolean computeInBackground() {

		// Catch if any input Data is given
		if (adjmatrix == null) {
			System.out.println("Please set adjacency data.");
			return false;
		}

		// MARTIN: set server by job type
		String url = "rmi://cassiopeidae/ClusterJobs";
		// System.setProperty("java.rmi.server.hostname", "cassiopeidae");
		// String url = "rmi://nero/Server";
		// System.setProperty("java.rmi.server.hostname", "nero");
		resulttable = new Hashtable<Integer, Double>();
		try {
			server = (IJobServer) Naming.lookup(url);

			if (!server.submitJob(job, adjmatrix, helper)) {
				JOptionPane.showMessageDialog(
						MainWindowSingelton.getInstance(), "MESSAGE!");
			}

		}

		// MARTIN link rmi exceptions in UI, not just in print stream
		catch (NotBoundException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingelton
							.getInstance().returnFrame(),
							"RMI Interface could not be established.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
			GraphColoringGUI.progressbar.closeWindow();
			mw = MainWindowSingelton.getInstance();
			mw.setEnable(true);
			mw.setLockedPane(false);
			//System.out.println("RMI Interface could not be established.");
			return false;

		} catch (RemoteException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingelton
							.getInstance().returnFrame(),
							"Cluster not reachable.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
			GraphColoringGUI.progressbar.closeWindow();
			mw = MainWindowSingelton.getInstance();
			mw.setEnable(true);
			mw.setLockedPane(false);
			//System.out.println("Cluster not reachable.");
			return false;

		} catch (MalformedURLException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingelton
							.getInstance().returnFrame(),
							"Clusteradress could not be resolved.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
			GraphColoringGUI.progressbar.closeWindow();
			mw = MainWindowSingelton.getInstance();
			mw.setEnable(true);
			mw.setLockedPane(false);
			//System.out.println("Clusteradress could not be resolved.");
			return false;
		}

		return true;
	}

	public void setAdjMatrix(int adjmatrix[][]) {
		this.adjmatrix = adjmatrix;
	}

	public void setAdjLists(int[] nodearray, int[] edgearray) {
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
