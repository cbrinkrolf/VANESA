package cluster.slave;

import java.rmi.RemoteException;

public interface IMappingCallback extends INotifyable {


	/**
	 * Callback after mapping. Deliver the amount of mapped nodes.
	 * @param mappedNodes
	 * @param experiment
	 * @throws RemoteException
	 */
	public void setMappingProgress(int mappedNodes, String experiment) throws RemoteException;

}
