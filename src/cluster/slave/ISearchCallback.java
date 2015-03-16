package cluster.slave;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;

import cluster.graphdb.GraphDBTransportNode;

public interface ISearchCallback extends Remote, INotifyable {

	
	/**
	 * Return a adjacency list containing String labels of the nodes.
	 * @param adjacencylist
	 * @throws RemoteException
	 */
	public void setResultAdjacencyList(HashMap<GraphDBTransportNode, HashSet<GraphDBTransportNode>> adjacencylist) throws RemoteException;
}
