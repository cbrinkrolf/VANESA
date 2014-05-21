package cluster;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Hashtable;

public interface IClientHelper extends Remote{	
	public void progressNotify(String message) throws RemoteException;
	public void setResultTable(Hashtable<Integer, Double> table, int jobtype) throws RemoteException;
	public void setResultSet(HashSet<HashSet<Integer>> set, int jobtype) throws RemoteException;
	public void setResultMatrix(int[][] matrix) throws RemoteException;
}
