package cluster.clientimpl;

import graph.algorithms.DCBprepareCalc;
import graph.algorithms.gui.DenselyConnectedBiclusteringGUI;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.SwingUtilities;

import cluster.slave.LayoutPoint2D;
import cluster.slave.IComputeCallback;

public class ComputeCallback2 extends UnicastRemoteObject implements Serializable, IComputeCallback {

	private static final long serialVersionUID = -5452379957017610971L;

	private DCBprepareCalc dcbPrepareCalc;
	
	public ComputeCallback2(DCBprepareCalc dcBprepareCalc) throws RemoteException {
		super();
		this.dcbPrepareCalc = dcBprepareCalc;
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
		
				dcbPrepareCalc.returnComputeData(table, jobtype);
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
