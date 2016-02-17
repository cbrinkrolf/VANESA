package cluster.clientimpl;

import graph.algorithms.gui.GraphColoringGUI;
import graph.algorithms.gui.smacof.view.SmacofView;
import gui.MainWindow;
import gui.MainWindowSingleton;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import cluster.slave.Cluster;
import cluster.slave.IComputeCallback;
import cluster.slave.LayoutPoint2D;

public class ComputeCallback extends UnicastRemoteObject implements Serializable, IComputeCallback {

	private static final long serialVersionUID = -5452379957017610971L;

	private GraphColoringGUI gui = null;
	private SmacofView sv = null;

	public ComputeCallback(GraphColoringGUI gui) throws RemoteException {
		super();
		this.gui = gui;
	}
	
	public ComputeCallback(SmacofView sv) throws RemoteException {
		super();
		this.sv = sv;
	}

	@Override
	public void progressNotify(String message) throws RemoteException {
		if (MainWindow.progressbar != null) {
			MainWindow.progressbar.setProgressBarString(message);
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
	public void setResultMap(HashMap<Double,HashSet<Integer>> map, int jobtype)
			throws RemoteException {
		gui.returnComputeData(map, jobtype);
		gui.reactiveateUI();
	}

	@Override
	public void setResultMatrix(int[][] matrix) throws RemoteException {

	}

	@Override
	public void setResultCoordinates(HashMap<Integer, LayoutPoint2D> coords)
			throws RemoteException {
		if(gui!=null){
			gui.realignNetwork(coords);
			gui.reactiveateUI();
		}else{
			sv.realignNetwork(coords);
		}
		
		
	}

	@Override
	public void setResultClusters(ArrayList<Cluster> clusters)
			throws RemoteException {
		gui.createNewPathway(clusters);
		gui.reactiveateUI();
	}
}
