package cluster;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

public interface JobServer extends Remote {

	/**
	 * This method takes an adjacency matrix as an input and starts the cycle computation.
	 * In the resulting Hashtable contains all nodes from the adjmatrix mapped to a computed value.
	 * This computed value represents each node's occurrence in all cycles.
	 * @param adjmatrix
	 * @return
	 * @throws RemoteException
	 */
	public Hashtable<Integer, Double> getCycleValues(int[][] adjmatrix)
			throws RemoteException;

	public Hashtable<Integer, Double> getCliqueValues(int[][] adjmatrix)
			throws RemoteException;

	public int[][] getAllPairShortestPaths(int[] nodearray, int[] edgearray)
			throws RemoteException;

}