package cluster.clientimpl;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.algorithms.gui.DenselyConnectedBiclusteringGUI;
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
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ClusterComputeThread2 extends Thread {

	private Hashtable<Integer, Double> resulttable;
	private HashSet<HashSet<Integer>> resultset;
	private short[][] adjmatrix;
//	private int[] edgearray, nodearray;
	private int job;
	private IClusterJobs server;
	private ComputeCallback2 helper;
	private MainWindow mw;

	public ClusterComputeThread2(int job, ComputeCallback2 helper2) {
		this.job = job;
		this.helper = helper2;
		// setupArrays();
	}

	@Override
	public void run() {
		// Catch if any input Data is given
		if (adjmatrix == null) {
			System.out.println("Please set adjacency data.");
		}

		// MARTIN: set server by job type
		String url = "rmi://cassiopeidae/ClusterJobs";
		// System.setProperty("java.rmi.server.hostname", "cassiopeidae");
		// String url = "rmi://nero/Server";
		// System.setProperty("java.rmi.server.hostname", "nero");
		resulttable = new Hashtable<Integer, Double>();
		try {
			server = (IClusterJobs) Naming.lookup(url);

			if (!server.submitJob(job, adjmatrix, helper)) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
					JOptionPane.showMessageDialog(
							MainWindowSingleton.getInstance(), "Queue is at maximum capacity!");
						}
				});
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


		} catch (RemoteException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingleton
							.getInstance().returnFrame(),
							"Cluster not reachable.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});

		} catch (MalformedURLException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingleton
							.getInstance().returnFrame(),
							"Clusteradress could not be resolved.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
		}

	}



	public void setAdjMatrix(short adjmatrix[][]) {
		this.adjmatrix = adjmatrix;
	}

}
