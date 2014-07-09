package dataMapping;

import gui.MainWindowSingelton;

import java.rmi.Naming;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import cluster.IJobServer;
import cluster.JobTypes;
import cluster.MappingCallback;

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
		IJobServer server;

//		HashMap<String, HashMap<String, Double>> experiments = new HashMap<String, HashMap<String, Double>>();
		
		String[] experimentArray = new String[header.size()];
		HashMap<String, Double> mappings[] = new HashMap[header.size()]; 
		for(int i = 0; i < header.size(); i++){
			experimentArray[i] = header.get(i);
			
			System.out.println(experimentArray[i]);
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
			
			server = (IJobServer) Naming.lookup(url);
			
			System.out.println(server);
			
			
			
			helper = new MappingCallback();
			if (!server.submitMapping(JobTypes.MAPPING_UNIPROT, experimentArray, mappings, helper)) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(
								MainWindowSingelton.getInstance(), "Queue is at maximum capacity!");
					}
					
				});
			}

		}catch(Exception e){
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(
							MainWindowSingelton.getInstance(), "Data export could not be executed.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				
			});
			e.printStackTrace();
		}
		
//		DataMappingModelController.progressBarExport.closeWindow();
		return null;
	}

}
