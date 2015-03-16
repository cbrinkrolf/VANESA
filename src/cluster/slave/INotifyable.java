package cluster.slave;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INotifyable extends Remote {
	/**
	 * Notify via message string.
	 * @param message
	 * @throws RemoteException
	 */
	public void progressNotify(String message) throws RemoteException;
}
