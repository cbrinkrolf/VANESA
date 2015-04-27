package cluster.clientimpl;

import graph.algorithms.gui.GraphColoringGUI;
import gui.MainWindow;
import gui.MainWindowSingleton;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import cluster.master.IClusterJobs;

public class ClusterComputeThread extends Thread {

	private Hashtable<Integer, Double> resulttable;
	private HashSet<HashSet<Integer>> resultset;
	private byte [] jobinformation; 
	private int job;
	private IClusterJobs jobinterface;
	private ComputeCallback helper;
	private MainWindow mw;

	public ClusterComputeThread(int job, byte[] jobinformation,ComputeCallback helper) {
		this.job = job;
		this.jobinformation = jobinformation;
		this.helper = helper;
	}

	@Override
	public void run() {
		// compute job on server
		submitJob();
	}

	private void submitJob() {

		//URL of Master server
		String url = "rmi://cassiopeidae/ClusterJobs";
		// String url = "rmi://nero/Server";
		resulttable = new Hashtable<Integer, Double>();
		try {
			jobinterface = (IClusterJobs) Naming.lookup(url);
		
			if (!jobinterface.submitJob(job,jobinformation,helper)) {
				displayNotice("Queue is at maximum capacity!");
			}
			

		}catch (NotBoundException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingleton
							.getInstance().returnFrame(),
							"RMI Interface could not be established.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});

			mw = MainWindowSingleton.getInstance();
			mw.closeProgressBar();

		} catch (RemoteException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingleton
							.getInstance().returnFrame(),
							"Cluster not reachable.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});

			mw = MainWindowSingleton.getInstance();
			mw.closeProgressBar();

		} catch (MalformedURLException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingleton
							.getInstance().returnFrame(),
							"Clusteradress could not be resolved.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});

			mw = MainWindowSingleton.getInstance();
			mw.closeProgressBar();
		}

	}
	
	private void displayNotice(String string) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(
						MainWindowSingleton.getInstance(), string);
			}
		});		
	}

	
	public Hashtable<Integer, Double> getResultTable() {
		return resulttable;
	}

	public HashSet<HashSet<Integer>> getResultSet() {
		return resultset;
	}
}
