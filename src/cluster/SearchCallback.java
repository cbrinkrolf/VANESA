package cluster;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import database.unid.UNIDSearch;

public class SearchCallback implements ISearchCallback,Serializable{

	private static final long serialVersionUID = 7891201262766307950L;

	private UNIDSearch usearch;
	
	
	public SearchCallback(UNIDSearch usearch){
		super();
		this.usearch = usearch;
	}
	
	@Override
	public void setResultAdjacencyList(HashMap<String, HashSet<String>> adjacencylist) throws RemoteException{
		//MARTIN Creating a new network on the GraphDB result (String hashmap, for starters)
				HashSet<String> allnodes = new HashSet<String>();
				int interactions = 0;
				for(Entry<String,HashSet<String>> e : adjacencylist.entrySet()){
					allnodes.add(e.getKey());
					allnodes.addAll(e.getValue());
					interactions+=e.getValue().size();
				}
				System.out.println("Found "+allnodes.size()+" Nodes with "+interactions+" interactions.");
				usearch.createNetworkFromSearch(adjacencylist);
				usearch.reactiveateUI();
	}
	
	@Override
	public void progressNotify(String message) throws RemoteException {
		if(UNIDSearch.progressBar!=null){
			UNIDSearch.progressBar.setProgressBarString(message);
		}
	}

}
