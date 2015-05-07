package cluster.slave;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public interface IComputeCallback extends Remote, INotifyable {	
	
	/**
	 * Set a predefined result table.
	 * @param table
	 * @param jobtype
	 * @throws RemoteException
	 */
	public void setResultTable(Hashtable<Integer, Double> table, int jobtype) throws RemoteException;
	
	/**
	 * Set a predefined result set.
	 * @param set
	 * @param jobtype
	 * @throws RemoteException
	 */
	public void setResultSet(HashSet<HashSet<Integer>> set, int jobtype) throws RemoteException;
	
	/**
	 * Set a predefined result set.
	 * @param set
	 * @param jobtype
	 * @throws RemoteException
	 */
	public void setResultMap(HashMap<Double,HashSet<Integer>> map, int jobtype) throws RemoteException;
	
	
	/**
	 * Set a modified matrix.
	 * @param matrix
	 * @throws RemoteException
	 */
	public void setResultMatrix(int[][] matrix) throws RemoteException;
	
	/**
	 * Set a point cloud.
	 * @param matrix
	 * @throws RemoteException
	 */
	public void setResultCoordinates(HashMap<Integer,LayoutPoint2D> coords) throws RemoteException;
}
