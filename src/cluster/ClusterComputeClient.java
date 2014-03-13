package cluster;

import gui.MainWindowSingelton;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.swing.JOptionPane;

public class ClusterComputeClient {
	
	public static final int 
	CYCLE_JOB_OCCURRENCE 	= 10,
	CYCLE_JOB_NEIGHBORS 	= 11,
	CLIQUE_JOB_OCCURRENCE 	= 20,
	CLIQUE_JOB_NEIGHBORS 	= 21,
	CLIQUE_JOB_PATHSLESS 	= 22,
	CLIQUE_JOB_CONNECTIVITY = 23,
	APSP_JOB				= 30;

	Hashtable<Integer, Double> result;

	public ClusterComputeClient(int adjmatrix[][], int job) {

		
		//TODO: set server by job type
		String url = "rmi://cassiopeidae/Server";
		System.setProperty("java.rmi.server.hostname", "cassiopeidae");
		result = new Hashtable<Integer, Double>();
		try {
			JobServer server = (JobServer) Naming.lookup(url);

			//determine job
			switch (job) {
			case CYCLE_JOB_OCCURRENCE:
				result = server.getCycleValues(adjmatrix);
				break;
			case CLIQUE_JOB_OCCURRENCE:
				result = server.getCliqueValues(adjmatrix);
				break;			

			default:
				System.err.println("ERROR! JobType not found.");
				break;
			}
			

		}
		// debug
		// System.out.println("Cycle computation Result: "+result);
		catch (NotBoundException e) {
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance()
					.returnFrame(), "RMI Interface could not be established.", "Error", 1);
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance()
					.returnFrame(), "Cluster not reachable.", "Error", 1);
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(MainWindowSingelton.getInstance()
					.returnFrame(), "Clusteradress could not be resolved.", "Error", 1);
		}
	}

	public Hashtable<Integer, Double> getResultTable() {
		return result;
	}

}
