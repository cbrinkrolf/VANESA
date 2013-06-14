package blast;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class BLAST_Server implements BLAST_Interface {

	public BLAST_Server() throws RemoteException {
		super();
	}

	public Object[] startAllAgainstAll(HashMap<String, String> mapReference,
			HashMap<String, String> mapQuery, String mode) {

		AllAgainstAll blast = new AllAgainstAll(mapReference, mapQuery, mode);

		try {
			Thread t = new Thread(blast);
			t.start();
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Object[] result = { blast.getSimMatrix(), blast.getPosition2QueryID(),
				blast.getPosition2ReferenceID(), blast.getQueryID2Position(),
				blast.getReferenceID2Position() };

		return result;
	}

	public static void main(String[] args) throws RemoteException {

		// if (System.getSecurityManager() == null) {
		// System.setSecurityManager(new SecurityManager());
		// }

		LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

		String name = "BLAST_Server";
		BLAST_Server server = new BLAST_Server();
		BLAST_Interface stub = (BLAST_Interface) UnicastRemoteObject
				.exportObject(server, 0);
		// RemoteServer.setLog( System.out );

		Registry registry = LocateRegistry.getRegistry();
		registry.rebind(name, stub);

	}

}
