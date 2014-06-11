package cluster;

import graph.algorithms.gui.GraphColoringGUI;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Hashtable;

public class ComputeCallback extends UnicastRemoteObject implements Serializable, IComputeCallback {

	private static final long serialVersionUID = -5452379957017610971L;

	private GraphColoringGUI gui;

	public ComputeCallback(GraphColoringGUI gui) throws RemoteException {
		super();
		this.gui = gui;
	}

	@Override
	public void progressNotify(String message) throws RemoteException {
		if (GraphColoringGUI.progressbar != null) {
			GraphColoringGUI.progressbar.setProgressBarString(message);
		}
	}

	@Override
	public void setResultTable(Hashtable<Integer, Double> table, int jobtype)
			throws RemoteException {
		gui.returnComputeData(table, jobtype);
		gui.reactiveateUI();
	}

	@Override
	public void setResultSet(HashSet<HashSet<Integer>> set, int jobtype)
			throws RemoteException {
		gui.returnComputeData(set, jobtype);
		gui.reactiveateUI();
	}

	@Override
	public void setResultMatrix(int[][] matrix) throws RemoteException {

	}
}
