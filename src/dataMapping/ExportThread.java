package dataMapping;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import cluster.clientimpl.MappingCallback;
import cluster.master.IClusterJobs;
import cluster.slave.JobTypes;
import gui.MainWindow;

/**
 * @author Britta Niemann
 *
 */
public class ExportThread extends SwingWorker<Integer, Void>{
	
	Map<String, List<String>> newMergeMap;
	ArrayList<String> header;
	
	public ExportThread(Map<String, List<String>> newMergeMap, ArrayList<String> header){
		this.newMergeMap = newMergeMap;
		this.header = header;
	}
	


	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Integer doInBackground() throws Exception {
		String url = "rmi://cassiopeidae/ClusterJobs";
		IClusterJobs server;

//		HashMap<String, HashMap<String, Double>> experiments = new HashMap<String, HashMap<String, Double>>();
		
		String[] experimentArray = new String[header.size()];
		@SuppressWarnings("unchecked")
		HashMap<String, Double> mappings[] = new HashMap[header.size()]; 
		for(int i = 0; i < header.size(); i++){
			experimentArray[i] = header.get(i);
			
			mappings[i] = new HashMap<String, Double>();
			for(String id : newMergeMap.keySet()){
			

				mappings[i].put(id, Double.parseDouble(newMergeMap.get(id).get(i+1)));
				

				
//				if(experiments.containsKey(header.get(i))){
//					experiments.get(header.get(i)).put(id, Double.parseDouble(newMergeMap.get(id).get(i+1)));
//				}else{
//					HashMap<String, Double> tmpMap = new HashMap<String, Double>();
//					tmpMap.put(id, Double.parseDouble(newMergeMap.get(id).get(i+1)));
//					experiments.put(header.get(i), tmpMap);
//				}
				
			}
			
			
		}
		
		

		

		
		MappingCallback helper;
		try {
			
			server = (IClusterJobs) Naming.lookup(url);

			
			helper = new MappingCallback();
			if (!server.submitMapping(JobTypes.MAPPING_UNIPROT, experimentArray, mappings, helper)) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						DataMappingModelController.reactivateUI();
						JOptionPane.showMessageDialog(
								MainWindow.getInstance(), "Queue is at maximum capacity!");
					}
					
				});
			}

			
			
		}catch (NotBoundException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DataMappingModelController.reactivateUI();
					JOptionPane.showMessageDialog(MainWindow
							.getInstance().returnFrame(),
							"RMI Interface could not be established.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
			e.printStackTrace();

		} catch (RemoteException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DataMappingModelController.reactivateUI();
					JOptionPane.showMessageDialog(MainWindow
							.getInstance().returnFrame(),
							"Cluster not reachable.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
			e.printStackTrace();

		} catch (MalformedURLException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DataMappingModelController.reactivateUI();
					JOptionPane.showMessageDialog(MainWindow
							.getInstance().returnFrame(),
							"Clusteradress could not be resolved.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
			e.printStackTrace();
			
		}catch(Exception e){
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DataMappingModelController.reactivateUI();
					JOptionPane.showMessageDialog(
							MainWindow.getInstance(), "Data export could not be executed.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				
			});
			e.printStackTrace();
		}
		
//		DataMappingModelController.progressBarExport.closeWindow();
		return null;
	}

}
