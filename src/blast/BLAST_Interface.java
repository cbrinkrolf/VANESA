package blast;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface BLAST_Interface extends Remote {

	public Object[] startAllAgainstAll(HashMap<String, String> mapReference,
			HashMap<String, String> mapQuery, String mode)
			throws RemoteException;

}
