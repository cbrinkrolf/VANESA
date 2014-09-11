package dataMapping;

import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingleton;
import gui.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import cluster.IJobServer;
import cluster.JobTypes;
import cluster.MappingCallback;
import biologicalElements.Pathway;
import dataMapping.biomartRetrieval.AgilentQueryRetrieval;
import dataMapping.biomartRetrieval.BiomartQueryRetrieval;
import dataMapping.biomartRetrieval.HPRDQueryRetrieval;
import dataMapping.biomartRetrieval.IntActQueryRetrieval;
import dataMapping.dataImport.ImportExcelxData;
import database.unid.UNIDSearch;


/**
 * This class manages the data flow between the GUI, the BioMart information retrieval and the
 * DataMappingModel, notifies the GUI if data has changes
 * @author dborck
 *
 */
public class DataMappingModelController extends Observable {

	private DataMappingModel dataMappingModel;
	private ImportExcelxData importData;

	private String identifierType;
	private String species;
	private List<String> labels;
	private List<String> identifiers;
	private ArrayList<ArrayList<String>> multiValues;

	public static final int NETWORK = 0;
	public static final int IDENTIFIER_TYPE = 1;
	public static final int IDENTIFIER = 2;
	public static final int VALUES = 3;
	public static final int SPECIES = 4;

	private boolean[] checks = {false, false, false, false, false};

	private BiomartQueryRetrieval query;
	private Map<String, String> resultMap;
	private boolean withPathway;
	private int headerIndex;
	public static ProgressBar progressBarExport;

	/**
	 * constructs the DataMappingModelController and instantiates a new DataMappingModel
	 */
	public DataMappingModelController(boolean withPathway) {
		super();
		this.dataMappingModel = new DataMappingModel();
		this.withPathway = withPathway;
	}

	/**
	 * open an Excel file and import the data
	 * @param file
	 * @throws Exception
	 */
	public void openFile(File file) throws Exception {
		importData = new ImportExcelxData(file);
		setChanged();
		notifyObservers(importData);
	}

	/**
	 * set the source of the identifiers, e.g. Agilent, Affymetrix, EMBL or UniProt
	 * @param identifier
	 */
	public void setIdentifierType(String identifier) {
		this.identifierType = identifier.split(" ")[0];
		doCheck(IDENTIFIER_TYPE);
	}
	
	public void setSpecies(String species) {
		this.species = species.split(" ")[0];
		doCheck(SPECIES);
	}
	
	
	/**
	 * sets the checks[index] to true and does check if all parameters for
	 * the dataMapping were set, or if called with "-1" only checks,
	 * if true the view is notified and the "OK" button will be enable
	 * @param isSet - one out of four parameters
	 */
	private void doCheck(int isSet) {
		boolean isReady = false;
		switch (isSet) {
		case NETWORK:
			checks[NETWORK] = true;
			break;
		case IDENTIFIER_TYPE:
			checks[IDENTIFIER_TYPE] = true;
			break;
		case IDENTIFIER:
			checks[IDENTIFIER] = true;
			break;
		case VALUES:
			checks[VALUES] = true;
			break;
		case SPECIES:
			checks[SPECIES] = true;
			break;
		default:
			break;		
		}
		//network is only need for colaration/ species is only needed for mapping into db
		if(withPathway && checks[NETWORK] && checks[IDENTIFIER_TYPE] && checks[IDENTIFIER] && checks[VALUES]
			|| !withPathway && checks[IDENTIFIER_TYPE] && checks[IDENTIFIER] && checks[VALUES] && checks[SPECIES]) {
			isReady = true;
		}
		setChanged();
		notifyObservers(isReady);
	}

	/**
	 * gets the values out of the row[rowIndex] with the new values for the JTable header
	 * @param rowIndex
	 * @return - the new values as a Vector<String>
	 */
	public Vector<String> getHeaderData(int rowIndex) {
		Vector<Vector<String>> allData = importData.getDataVector();
		Vector<String> rowData = allData.get(rowIndex);
		return rowData;
	}

	/**
	 * extracts the pathway labels out of the selected pathway,
	 * the labels are used for the BioMart query
	 * @param pw - the selected pathway
	 */	
	@SuppressWarnings("unchecked")
	public void setPathwayLabels(Pathway pw) {
		labels = new ArrayList<String>();
		Iterator<String> it = pw.getAllNodeLabels().iterator();
		while (it.hasNext()) {
			labels.add((String) it.next());
		}
		doCheck(NETWORK);
	}
	
	

	/**
	 * starts the BioMart query, sets the query results in the datamappingModel
	 * and starts the main work of the dataMappingModel
	 */
	public void startMapping(){
		createIdentifierMultiValueMap();
		//TODO numOfThreads
		int numOfThreads = 2;
		System.out.println("Anzahl Threads: " + numOfThreads);
	
		if(withPathway){ // = color the shown network
//			createIdentifierMultiValueMap();
			List<Future<Map<String, String>>> resultMapPart = new LinkedList<>();
			ExecutorService executeQuerys = Executors.newFixedThreadPool(numOfThreads);
			HashSet<QueryParallel> tasksQuery = new HashSet<>();
		
			
			int labelsCounter = 0;
			int labelsPartLength = labels.size()/numOfThreads;
			int labelsPartMod = labels.size()%numOfThreads;
		
			
			if(detectPathwayOrigin().equals("HPRD")) {
//				query = new HPRDQueryRetrieval("homo sapiens");
				
				for(int i = 0; i < numOfThreads; i++){
					if(i < labelsPartMod){
						tasksQuery.add(new QueryParallel(new HPRDQueryRetrieval("homo sapiens"), labels.subList(labelsCounter, (labelsCounter+labelsPartLength+1)), identifierType));
						labelsCounter = labelsCounter+labelsPartLength+1;

					}else{ //letzter Block
						tasksQuery.add(new QueryParallel(new HPRDQueryRetrieval("homo sapiens"), labels.subList(labelsCounter, (labelsCounter+labelsPartLength)), identifierType));
						labelsCounter = labelsCounter+labelsPartLength;
					}	
				}
				
			} else if(detectPathwayOrigin().equals("IntAct")) {
				//query = new IntActQueryRetrieval(detectPathwaySpeciesHeuristic());
//				query = new IntActQueryRetrieval(detectPathwaySpeciesAll());
				
				for(int i = 0; i < numOfThreads; i++){
					if(i < labelsPartMod){
						tasksQuery.add(new QueryParallel(new IntActQueryRetrieval(detectPathwaySpeciesAll()), labels.subList(labelsCounter, (labelsCounter+labelsPartLength+1)), identifierType));
						labelsCounter = labelsCounter+labelsPartLength+1;

					}else{ //letzter Block
						tasksQuery.add(new QueryParallel(new IntActQueryRetrieval(detectPathwaySpeciesAll()), labels.subList(labelsCounter, (labelsCounter+labelsPartLength)), identifierType));
						labelsCounter = labelsCounter+labelsPartLength;
					}	
				}
			} else if(detectPathwayOrigin() == null) {
				// TODO: throw Exception
				System.out.println("Cannot determine the origin source of the pathway");
			}
			
			
//			try {
//				query.retrieveQueryResults(identifierType, labels);
//			} catch (IOException e) {
//				// TODO: manage exceptions if for example the BioMart server is down
//				e.printStackTrace();
//			}
//			
			try {
				resultMapPart = executeQuerys.invokeAll(tasksQuery);
			} catch (InterruptedException e1) {
				JOptionPane.showMessageDialog(
						null,
						"Could not execute query.",
						"Error", JOptionPane.ERROR_MESSAGE);

				e1.printStackTrace();

			}
				
			executeQuerys.shutdown();
			resultMap = new HashMap<String, String>();
			
			for(Future<Map<String, String>> res : resultMapPart){
				try {
					resultMap.putAll(res.get());

				} catch (InterruptedException | ExecutionException e) {
					JOptionPane.showMessageDialog(
							null,
							"Query dosen't work (no results).",
							"Error", JOptionPane.ERROR_MESSAGE);

					e.printStackTrace();
				}
			}
			
//			resultMap = query.getResultMap();
			dataMappingModel.setQueryResultMap(resultMap);
			dataMappingModel.setColoringParameters();
			dataMappingModel.merge();
			dataMappingModel.coloringPathway();
		}else{ // = store data in db
//			createIdentifierMultiValueMap();
			List<Future<Map<String, String>>> resultMapPart = new LinkedList<>();
			ExecutorService executeQuerys = Executors.newFixedThreadPool(numOfThreads);
			HashSet<QueryParallel> tasksQuery = new HashSet<>();
		
			
			ArrayList<String> ids = new ArrayList<String>();
			ids.addAll(dataMappingModel.getIdentifiersMultiValuesMap().keySet());
			String speciesString = "";
			
			int idsCounter = 0;
			int idsPartLength = ids.size()/numOfThreads;
			int idsPartMod = ids.size()%numOfThreads;
		

			
			if(identifierType.equals("Agilent")){
				if(species.equals("Homo")){
					speciesString = "homo sapiens";
//					query = new AgilentQueryRetrieval("homo sapiens");
				}else if(species.equals("Mus")){
					speciesString = "mus musculus";
//					query = new AgilentQueryRetrieval("mus musculus");
				}else if(species.equals("Saccharomyces")){
					speciesString = "yeast";
//					query = new AgilentQueryRetrieval("yeast");
				}
			}
			

			
			for(int i = 0; i < numOfThreads; i++){
				if(i < idsPartMod){
					tasksQuery.add(new QueryParallel(new AgilentQueryRetrieval(speciesString), ids.subList(idsCounter, (idsCounter+idsPartLength+1)), "UniProtWithoutPW"));
					idsCounter = idsCounter+idsPartLength+1;

				}else{ //letzter Block
					tasksQuery.add(new QueryParallel(new AgilentQueryRetrieval(speciesString), ids.subList(idsCounter, (idsCounter+idsPartLength)), "UniProtWithoutPW"));
					idsCounter = idsCounter+idsPartLength;
				}	
			}
			
			try {
				resultMapPart = executeQuerys.invokeAll(tasksQuery);
			}catch (InterruptedException e1) {
//				JOptionPane.showMessageDialog(
//						null,
//						"Could not execute query.",
//						"Error", JOptionPane.ERROR_MESSAGE);

				e1.printStackTrace();
			}
				
			executeQuerys.shutdown();
			resultMap = new HashMap<String, String>();
			
			try{
				for(Future<Map<String, String>> res : resultMapPart){
						resultMap.putAll(res.get());
				}
				
			} catch (ExecutionException | InterruptedException e) {
				SwingUtilities.invokeLater(new Runnable() {
				     public void run() {
							JOptionPane.showMessageDialog(
									MainWindowSingleton.getInstance(),
							"Query dosen't work (no results).",
							"Error", JOptionPane.ERROR_MESSAGE);
				     }
				   });
				e.printStackTrace();
				
				setChanged();
				notifyObservers(e);
			}
						
			
			
//			resultMap = query.getResultMap();
			dataMappingModel.setQueryResultMap(resultMap);
			dataMappingModel.merge();
			
		}
		
		setChanged();
		notifyObservers(dataMappingModel);
		GraphInstance.getMyGraph().getVisualizationViewer().repaint();
	}
	
	
	/**
	 * creates the HashMap of the data of the input file
	 */
	private void createIdentifierMultiValueMap() {
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
//		ArrayList<String> header = new ArrayList<String>();
//		for(ArrayList<String> multiValue : multiValues){
//			header.add(multiValue.get(headerIndex));
//		}
//		
//		dataMappingModel.setHeader(header);
		for (String id : identifiers) {
			int index = identifiers.indexOf(id);
			ArrayList<String> valueRow = new ArrayList<String>();
			for(ArrayList<String> multiValue : multiValues){
				valueRow.add(multiValue.get(index));
			}
			map.put(id, valueRow);
			
		}
		dataMappingModel.setIdentifiersMultiValuesMap(map);
	}
	

	/**
	 * detects of which origin is the selected pathway,
	 * depending on the labels you have to call different BioMart queries
	 * @return - a String either "HPRD" or "IntAct" which determines the origin of the source
	 */
	private String detectPathwayOrigin() {
		// TODO: test for mixed variants..., only makes sense if after a merge
		// there will be a connected graph with these two different identifiers
		String check = labels.get(labels.size()/2);
		String result = null;
		if(check.contains("_")){
			result = "IntAct";
		} else  if (!check.contains("_")){
			result = "HPRD";
		}
		return result;
	}

	//	/**
	//	 * detects of which species is the selected pathway,
	//	 * depending on the labels you have to call different BioMart queries
	//	 * @return - a String representing the species of the pathway, it is used for the query construction
	//	 */
	//	private String detectPathwaySpeciesHeuristic() {
	//		int max = labels.size()-1;
	//		System.out.println(max/2);
	//		int middle = max/2;
	//		String[] check = {labels.get(0),labels.get(middle),labels.get(max)};
	//		String[] split = new String[3];
	//		for (int i = 0; i<check.length; i++) {
	//			split[i] = check[i].split("_")[1];
	//		}
	//		String result = null;
	//		// TODO: detect all common species!!!
	//		// TODO: detect whether the network contains labels from different species, up to now I do
	//		// not know how to handle this situation!!
	//		if(split[0].equalsIgnoreCase("mouse") && split[1].equalsIgnoreCase("mouse") && split[2].equalsIgnoreCase("mouse")){
	//			result = "mus musculus";
	//		} else  if (split[0].equalsIgnoreCase("human") && split[1].equalsIgnoreCase("human") && split[2].equalsIgnoreCase("human")){
	//			result = "homo sapiens";
	//		} else  if (split[0].equalsIgnoreCase("yeast") && split[1].equalsIgnoreCase("yeast") && split[2].equalsIgnoreCase("yeast")){
	//			result = "yeast";
	//		} else {
	//			System.out.println("Can not determine the source of species");
	//			result = "mus musculus";
	//		}
	//		System.out.println("Species: " + result);
	//		return result;
	//	}

	/**
	 * detects which species exist in the selected pathway,
	 * depending on the labels you have to call different BioMart queries
	 * @return - a String representing the species with the most occurrences in the pathway, it is used for the query construction
	 */
	private String detectPathwaySpeciesAll() {
		List<String> spec = Arrays.asList("homo sapiens", "mus musculus", "yeast");
		String result = null;
		Integer[] amountSpec = {0, 0, 0};
		String[] split = new String[labels.size()];
		// store all suffixes in an array
		for (int i = 0; i<labels.size(); i++) {
			String[] splitStr = labels.get(i).split("_");
			if(splitStr.length>1) {
				split[i] = splitStr[1];
			} else {
				split[i] = "NA";
			}
		}
		List<String> species = new ArrayList<String>();
		// count the occurrences of the three species
		for(String str : split) {
			if(str.equalsIgnoreCase("human")) {
				amountSpec[0] += 1;
			} else if (str.equalsIgnoreCase("mouse")) {
				amountSpec[1] += 1;
			} else if (str.equalsIgnoreCase("yeast")) {
				amountSpec[2] += 1;
			}
			// add the species to the list (unique)
			if (!species.contains(str)) {
				species.add(str);
			}
		}
		// the result is either one string corresponding to the only species, or a List of up to three
		// species (hs, mmus and/or yeast)
		List<String> tmpResult = new ArrayList<String>();
		if(species.size()>1) {
			for (String str : species) {
				if(str.equalsIgnoreCase("human")) {
					tmpResult.add(spec.get(0));
				} else if (str.equalsIgnoreCase("mouse")) {
					tmpResult.add(spec.get(1));
				} else if (str.equalsIgnoreCase("yeast")) {
					tmpResult.add(spec.get(2));
				}
			}
			int max = (Integer) Collections. max(Arrays.asList(amountSpec));
			List<Integer> amount = Arrays.asList(amountSpec);
			result = spec.get(amount.indexOf(max));
		} else if (species.size()==1){
			if(species.get(0).equalsIgnoreCase("human")) {
				result = spec.get(0);
			} else if (species.get(0).equalsIgnoreCase("mouse")) {
				result = spec.get(1);
			} else if (species.get(0).equalsIgnoreCase("yeast")) {
				result = spec.get(2);
			}
		}
		return result;
	}

	/**
	 * extracts the identifier (stored in one column) out of the imported Data (stored row wise)
	 * @param identifierColumnIndex
	 */
	public void setIdentifiers(int identifierColumnIndex) {
		Vector<Vector<String>> allData = importData.getDataVector();
		identifiers = new ArrayList<String>();
		for(int i = 0; i<allData.size(); i++) {
			if(i != headerIndex){
				Vector<String> rowData = allData.get(i);
				identifiers.add(rowData.get(identifierColumnIndex));
			}
		}
		doCheck(IDENTIFIER);
	}	
	
	/**
	 * extracts the values (stored in some columns) out of the imported Data (stored row wise)
	 * @param columnIndices
	 */
	public void setMultiValues(HashSet<Integer> columnIndices) {
		multiValues = new ArrayList<ArrayList<String>>();
		Vector<Vector<String>> allData = importData.getDataVector();
		
		ArrayList<String> header = new ArrayList<String>();

		
		for(int columnIndex : columnIndices){
			ArrayList<String> valueList = new ArrayList<String>();
			for(int i = 0; i<allData.size(); i++) {
				Vector<String> rowData = allData.get(i);
				if(i != headerIndex){
					valueList.add(rowData.get(columnIndex));
				}else{
					header.add(rowData.get(columnIndex));
				}
			}
			multiValues.add(valueList);
		}
		
		dataMappingModel.setHeader(header);
		
	}
	
	public void doValueCheck(){
		doCheck(VALUES);
	}

	/**
	 * after a reset in the view, the values for the checks have to be reset
	 */
	public void setChecks() {
		for(int i = 0; i< checks.length; i++) {
			checks[i] = false;
		}
		setChanged();
		notifyObservers("reset");
	}

	/**
	 * after some choises from the user the mapping have to be disabled, e.g. if
	 * "none" pathway is selected, sets the checks[index] to false and invokes 
	 * with "-1" the doCheck
	 * @param index - 
	 */
	public void disableCheck(int index) {
		checks[index] = false;
		doCheck(-1);
	}

	/**
	 * invokes the setting of the pathway in the dataMappingModel
	 * @param selectedPathway
	 */
	public void setPathway(Pathway selectedPathway) {
		dataMappingModel.setPathway(selectedPathway);

	}

	/**
	 * resets the dataMappingModel
	 */
	public void resetModel() {
		this.dataMappingModel = new DataMappingModel();
		setPathway(null);
	}

	/**
	 * extracts the new data for the coloring/storing from the JTable, invokes the coloring and notifies the View
	 * @param dmt
	 */
	public void setNewMergeMap(JTable dmt) {
		Map<String, List<String>> newMergeMap = new HashMap<String, List<String>>();
		Map<String, List<String>> newDupMap = new HashMap<String, List<String>>();
		if(withPathway){
			for(int i = 0; i<dmt.getRowCount(); i++) {
				JRadioButton jButton = (JRadioButton) dmt.getValueAt(i, 3);
				if(jButton.isSelected()) {
					List<String> changedValue = new ArrayList<String>();
					changedValue.add((String) dmt.getValueAt(i, 1));
					changedValue.add((String) dmt.getValueAt(i, 2));
					newMergeMap.put((String) dmt.getValueAt(i, 0), changedValue);
				} else if(!jButton.isSelected()) {
					List<String> changedValue = new ArrayList<String>();
					changedValue.add((String) dmt.getValueAt(i, 1));
					changedValue.add((String) dmt.getValueAt(i, 2));
					newDupMap.put((String) dmt.getValueAt(i, 0), changedValue);
				}
			}
			dataMappingModel.updateColor(newMergeMap, newDupMap);
			
			setChanged();
			notifyObservers(dataMappingModel);
		}else{ //store data into db is diffrent because the ordering of columns is different
			for(int i = 0; i<dmt.getRowCount(); i++) {
				JRadioButton jButton = (JRadioButton) dmt.getValueAt(i, 2);
				if(jButton.isSelected()) {
					List<String> changedValue = new ArrayList<String>();
					changedValue.add((String) dmt.getValueAt(i, 1));
					for(int j = 3; j < dmt.getColumnCount(); j++){
						changedValue.add((String) dmt.getValueAt(i, j));
					}				
					newMergeMap.put((String) dmt.getValueAt(i, 0), changedValue);
				} else if(!jButton.isSelected()) {
					List<String> changedValue = new ArrayList<String>();
					changedValue.add((String) dmt.getValueAt(i, 1));
					for(int j = 3; j < dmt.getColumnCount(); j++){
						changedValue.add((String) dmt.getValueAt(i, j));
					}
					newDupMap.put((String) dmt.getValueAt(i, 0), changedValue);
				}
			}
			//TODO Methode f�r Daten Export
//			System.out.println(newMergeMap);
			
			export(newMergeMap);
			
			
			
		}

		
			
	}
	
	
	/**
	 * @param newMergeMap
	 */
	private void export(Map<String, List<String>> newMergeMap) {
		// TODO Auto-generated method stub
		//ExportThread erzeugen newM �bergeben
		
		
		progressBarExport = new ProgressBar();
		progressBarExport.init(100, "DATAMAPPING", true);
		

		ArrayList<String> header = dataMappingModel.getHeader();
		
		
		ExportThread dataExport = new ExportThread(newMergeMap, header);
		try{
		dataExport.execute();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void reactivateUI() {
		// close Progress bar and reactivate UI
		progressBarExport.closeWindow();
		MainWindow mw = MainWindowSingleton.getInstance();
		mw.setEnable(true);
		mw.setLockedPane(false);
	}


	
	public void setHeaderIndex(int headerIndex) {
		this.headerIndex = headerIndex;
	}
	
}
