package cluster;

import java.rmi.RemoteException;

public interface IMappingCallback {
	/**
	 * Notify via message string.
	 * 
	 * @param message
	 * @throws RemoteException
	 */
	public void progressNotify(String message) throws RemoteException;

	/**
	 * Callback after mapping. Deliver the amount of mapped nodes.
	 * @param mappedNodes
	 * @param experiment
	 * @throws RemoteException
	 */
	public void setMappingProgress(int mappedNodes, String experiment) throws RemoteException;

}
