package cluster;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import database.unid.UNIDSearch;

public class SearchCallback extends UnicastRemoteObject implements ISearchCallback, Serializable {

	private static final long serialVersionUID = 7891201262766307950L;

	private UNIDSearch usearch;

	public SearchCallback(UNIDSearch usearch) throws RemoteException{
		super();
		this.usearch = usearch;
	}

	@Override
	public void setResultAdjacencyList(HashMap<String, HashSet<String>> adjacencylist)
			throws RemoteException {
		// Preset adjacency list in Search object
		usearch.setAdjacencyList(adjacencylist);
		UNIDSearch.progressBar.setProgressBarString("Applying Layout..");
		
		//DEBUG
//		HashSet<String> allnodes = new HashSet<String>();
//		for (Entry<String, HashSet<String>> e : adjacencylist.entrySet()) {
//			allnodes.add(e.getKey());
//			allnodes.addAll(e.getValue());
//		}
//		System.out.println("Found " + allnodes.size()+" Nodes.");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				usearch.createNetworkFromSearch();
				usearch.reactivateUI();
			}
		});

	}

	@Override
	public void progressNotify(String message) throws RemoteException {
		if (UNIDSearch.progressBar != null) {
			UNIDSearch.progressBar.setProgressBarString(message);
		}
	}

}
