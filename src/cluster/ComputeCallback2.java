package cluster;

import graph.algorithms.DenselyConnectedBiclustering;
import graph.algorithms.gui.DenselyConnectedBiclusteringGUI;
import graph.algorithms.gui.GraphColoringGUI;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.SwingUtilities;

import dataMapping.DataMappingModelController;

public class ComputeCallback2 extends UnicastRemoteObject implements Serializable, IComputeCallback {

	private static final long serialVersionUID = -5452379957017610971L;

	private DenselyConnectedBiclusteringGUI gui;
	
	public ComputeCallback2(DenselyConnectedBiclusteringGUI gui) throws RemoteException {
		super();
		this.gui = gui;
	}

	@Override
	public void progressNotify(String message) throws RemoteException {
		DenselyConnectedBiclusteringGUI.progressBar.setProgressBarString(message);
	}

	@Override
	public void setResultTable(final Hashtable<Integer, Double> table, final int jobtype)
			throws RemoteException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		
				gui.returnComputeData(table, jobtype);
			}
		});
	}

	@Override
	public void setResultSet(HashSet<HashSet<Integer>> set, int jobtype)
			throws RemoteException {
		
	}

	@Override
	public void setResultMatrix(int[][] matrix) throws RemoteException {

	}

	@Override
	public void setResultCoordinates(HashMap<Integer, LayoutPoint2D> coords)
			throws RemoteException {
		// MARTIN Auto-generated method stub
		
	}
}
