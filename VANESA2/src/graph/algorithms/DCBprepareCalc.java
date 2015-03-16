/**
 * 
 */
package graph.algorithms;

import graph.algorithms.gui.DenselyConnectedBiclusteringGUI;
import gui.MainWindowSingleton;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import cluster.clientimpl.ClusterComputeThread2;
import cluster.clientimpl.ComputeCallback2;
import cluster.slave.JobTypes;
import dataMapping.DataMappingModelController;

/**
 * @author Britta Niemann
 *
 */
public class DCBprepareCalc extends SwingWorker<Void, Void>{
	
	private double density;
	private double attrdim;
	
	private ArrayList<Double> ranges;
	private ArrayList<String> attrTyps;
	private ArrayList<String> attrNames;
	private ArrayList<JFormattedTextField> rangeField;
	private int nodeType = DenselyConnectedBiclusteringGUI.TYPE_BNA_NR;
	private ArrayList<JComboBox<String>> attrTypList;
	private ArrayList<JComboBox<String>> attrList;
	
	private HashMap<BiologicalNodeAbstract, Double> cyclesMap;
	private HashMap<BiologicalNodeAbstract, Double> cliquesMap;
	private boolean successComputData= true;
	private int numOfServerJobs = 0;
	
	private JTable table;
	private LinkedList<DCBresultSet> results;
	
	private DenselyConnectedBiclusteringGUI gui;
	
	private NetworkProperties np;
	
	public DCBprepareCalc(DenselyConnectedBiclusteringGUI gui, double density, ArrayList<JFormattedTextField> rangeField, double attrdim,
			ArrayList<JComboBox<String>> attrTypList, ArrayList<JComboBox<String>> attrList){
		this.gui = gui;
		this.density = density;
		this.rangeField = rangeField;
		this.attrdim = attrdim;
		this.attrTypList = attrTypList;
		this.attrList = attrList;
		
	}

	


	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		
		boolean noMinMax = false;
		boolean toManyTypes = false;

		if (density < DenselyConnectedBiclusteringGUI.DENSITY_MIN || density > DenselyConnectedBiclusteringGUI.DENSITY_MAX) {
			noMinMax = true;
		}

		ranges = new ArrayList<Double>();
		attrTyps = new ArrayList<String>();
		attrNames = new ArrayList<String>();
		
//			LinkedHashMap<String, ArrayList<String>> attrNames2 = new LinkedHashMap <String, ArrayList<String>>();

		
//			int typeCounter = 0;
		
		for (int i = 0; i < rangeField.size(); i++) {
			ranges.add(((Number) rangeField.get(i).getValue())
					.doubleValue());
			// ranges.add(((Number)rangeField.get(i).getValue()).doubleValue());
			if (ranges.get(i) < DenselyConnectedBiclusteringGUI.ATTR_MIN || ranges.get(i) > DenselyConnectedBiclusteringGUI.ATTR_MAX) {
				noMinMax = true;
				break;
//				}else if(typeCounter > 1){
//					toManyTypes = true;
//					break;
			}else {
				String type = (String) attrTypList.get(i).getSelectedItem();
				attrTyps.add(type);
				attrNames.add((String) attrList.get(i).getSelectedItem());
				
				switch(type){
				case DenselyConnectedBiclusteringGUI.TYPE_GRAPHNODE: 
					if((nodeType != DenselyConnectedBiclusteringGUI.TYPE_BNA_NR)&&(nodeType != DenselyConnectedBiclusteringGUI.TYPE_GRAPHNODE_NR)){
						toManyTypes = true;
						break;
					}else{
						nodeType = DenselyConnectedBiclusteringGUI.TYPE_GRAPHNODE_NR;
					}
					break;
				case DenselyConnectedBiclusteringGUI.TYPE_PROTEIN: 
					if((nodeType != DenselyConnectedBiclusteringGUI.TYPE_BNA_NR)&&(nodeType != DenselyConnectedBiclusteringGUI.TYPE_PROTEIN_NR)){
						toManyTypes = true;
						break;
					}else{
						nodeType = DenselyConnectedBiclusteringGUI.TYPE_PROTEIN_NR;
					}
					break;
				case DenselyConnectedBiclusteringGUI.TYPE_DNA:
					if((nodeType != DenselyConnectedBiclusteringGUI.TYPE_BNA_NR)&&(nodeType != DenselyConnectedBiclusteringGUI.TYPE_DNA_NR)){
						toManyTypes = true;
						break;
					}else{
						nodeType = DenselyConnectedBiclusteringGUI.TYPE_DNA_NR;
					}
					break;
				case DenselyConnectedBiclusteringGUI.TYPE_RNA: 
					if((nodeType != DenselyConnectedBiclusteringGUI.TYPE_BNA_NR)&&(nodeType != DenselyConnectedBiclusteringGUI.TYPE_RNA_NR)){
						toManyTypes = true;
						break;
					}else{
						nodeType = DenselyConnectedBiclusteringGUI.TYPE_RNA_NR;
					}
					break;
				default:
					break;
				}
				
			}

		}
		

		

		if (noMinMax) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DenselyConnectedBiclusteringGUI.reactivateUI();
					JOptionPane.showMessageDialog(null,
							"Please consider minimum and maximum Values.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
			

		} else if (attrdim > ranges.size()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DenselyConnectedBiclusteringGUI.reactivateUI();
					JOptionPane
							.showMessageDialog(
									null,
									"Number of similar attributes must not be greater than number of attributes.",
									"Error", JOptionPane.ERROR_MESSAGE);
				}
			});
			
		} else if (toManyTypes) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DenselyConnectedBiclusteringGUI.reactivateUI();
					JOptionPane
							.showMessageDialog(
									null,
									"These types of attributes could not be combined.",
									"Error", JOptionPane.ERROR_MESSAGE);
				}
			});
			

		} else {
			if(!(attrNames.contains(DenselyConnectedBiclusteringGUI.GC_CYCLES)||attrNames.contains(DenselyConnectedBiclusteringGUI.GC_CLIQUES))){
				cyclesMap = null;
				cliquesMap = null;
				startDcb();
				
				
				
			
			}else{
				np = new NetworkProperties();
				
				
				if(attrNames.contains(DenselyConnectedBiclusteringGUI.GC_CYCLES)){
					numOfServerJobs++;
		    		ComputeCallback2 helper;
						try {
							helper = new ComputeCallback2(this);

							ClusterComputeThread2 rmicycles = new ClusterComputeThread2(
									JobTypes.CYCLE_JOB_OCCURRENCE, helper);
							rmicycles.setAdjMatrix(np.getAdjacencyMatrix());
							rmicycles.start();
						} catch (RemoteException e1) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									DataMappingModelController.reactivateUI();
									JOptionPane.showMessageDialog(MainWindowSingleton
											.getInstance().returnFrame(),
											"Cluster not reachable.", "Error",
											JOptionPane.ERROR_MESSAGE);
								}
							});
							e1.printStackTrace();
						}
				}
				
				if(attrNames.contains(DenselyConnectedBiclusteringGUI.GC_CLIQUES)){
					numOfServerJobs++;
		    		ComputeCallback2 helper;
						try {
							helper = new ComputeCallback2(this);

							ClusterComputeThread2 rmicycles = new ClusterComputeThread2(
									JobTypes.CLIQUE_JOB_OCCURRENCE, helper);
							rmicycles.setAdjMatrix(np.getAdjacencyMatrix());
							rmicycles.start();
						} catch (RemoteException e1) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									DataMappingModelController.reactivateUI();
									JOptionPane.showMessageDialog(MainWindowSingleton
											.getInstance().returnFrame(),
											"Cluster not reachable.", "Error",
											JOptionPane.ERROR_MESSAGE);
								}
							});
							e1.printStackTrace();
						}
				}
				
			}

		}
		return null;
	}
	
	
	
	/**
	 * @param table
	 * @param jobtype
	 */
	public void returnComputeData(Hashtable<Integer, Double> table, int jobtype) {
		
		// Determine jobtype and behaviour
		switch (jobtype) {
		case JobTypes.CYCLE_JOB_OCCURRENCE:
			cyclesMap = new HashMap<BiologicalNodeAbstract, Double>();
			
			Hashtable<Integer, Double> cycledata = table;
			numOfServerJobs--;

			if (!cycledata.isEmpty()) {
				// Map ids to BNAs
				Iterator<Entry<Integer, Double>> it = cycledata.entrySet()
						.iterator();
				int key;
				double value;

				while (it.hasNext()) {
					Entry<Integer, Double> entry = it.next();
					key = entry.getKey();
					value = entry.getValue();
					// debug
					// System.out.println(key + " " + value);
					
					cyclesMap.put(np.getNodeAssignmentbackwards(key), value);

				}
				
			}else{
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						successComputData = false;
						DenselyConnectedBiclusteringGUI.reactivateUI();
						JOptionPane.showMessageDialog(null,
								"No cycles found, please use different attributes.", "No cycles",
								JOptionPane.INFORMATION_MESSAGE);
					}
				});
				
			}

			break;

		case JobTypes.CLIQUE_JOB_OCCURRENCE:
			cliquesMap = new HashMap<BiologicalNodeAbstract, Double>();
			
			Hashtable<Integer, Double> cliquesdata = table;
			numOfServerJobs--;
			


			if (!cliquesdata.isEmpty()) {
				// Map ids to BNAs
				Iterator<Entry<Integer, Double>> it = cliquesdata.entrySet()
						.iterator();
				int key;
				double value;

				while (it.hasNext()) {
					Entry<Integer, Double> entry = it.next();
					key = entry.getKey();
					value = entry.getValue();
					// debug
//					System.out.println(key + " " + value);
					
					cliquesMap.put(np.getNodeAssignmentbackwards(key), value);

				}
				
			}else{
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						successComputData = false;
						DenselyConnectedBiclusteringGUI.reactivateUI();
						JOptionPane.showMessageDialog(null,
								"No cliques found, please use different attributes.", "No cliques",
								JOptionPane.INFORMATION_MESSAGE);
					}
				});
				
			}
			
			break;

		default:
			System.out.println("Wrong Job Type: returnComputeData - "
					+ toString());
			break;
		}
		
		if(successComputData&&(numOfServerJobs == 0)){
			startDcb();
		}

		
	}
	
	public void startDcb() {
		DenselyConnectedBiclustering dcb = new DenselyConnectedBiclustering(
				density, ranges, nodeType, attrTyps, attrNames, attrdim, cyclesMap, cliquesMap);

		results = dcb.start();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try{
					if (results != null) {
						table = gui.initTable(results);
					} else {
						table = null;
					}
					
					DenselyConnectedBiclusteringGUI.reactivateUI();
					
					gui.openResultDialog(table);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		

		

	}
	


	

}
