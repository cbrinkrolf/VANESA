package cluster;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MappingCallback extends UnicastRemoteObject implements
		IMappingCallback, Serializable {

	private static final long serialVersionUID = -4445566937473044196L;

	public MappingCallback() throws RemoteException {
		super();
	}

	@Override
	public void progressNotify(String message) throws RemoteException {
System.out.println(message);

	}

	@Override
	public void setMappingProgress(int mappedNodes, String experiment)
			throws RemoteException {
System.out.println(mappedNodes +" "+experiment);

	}
}