package dataMapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;

import javax.swing.JRadioButton;
import javax.swing.JTable;
import biologicalElements.Pathway;

import dataMapping.biomartRetrieval.BiomartQueryRetrieval;
import dataMapping.biomartRetrieval.HPRDQueryRetrieval;
import dataMapping.biomartRetrieval.IntActQueryRetrieval;
import dataMapping.dataImport.ImportExcelxData;


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
	private List<String> labels;
	private List<String> identifiers;
	private List<String> values;

	public static final int NETWORK = 0;
	public static final int IDENTIFIER_TYPE = 1;
	public static final int IDENTIFIER = 2;
	public static final int VALUES = 3;

	private boolean[] checks = {false, false, false, false};

	private BiomartQueryRetrieval query;
	private Map<String, String> resultMap;

	/**
	 * constructs the DataMappingModelController and instantiates a new DataMappingModel
	 */
	public DataMappingModelController() {
		super();
		this.dataMappingModel = new DataMappingModel();
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
		default:
			break;		
		}
		if(checks[NETWORK] && checks[IDENTIFIER_TYPE] && checks[IDENTIFIER] && checks[VALUES]) {
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
	public void startMapping() {
		createIdentifiervalueMap();
		if(detectPathwayOrigin().equals("HPRD")) {
			query = new HPRDQueryRetrieval("homo sapiens");
		} else if(detectPathwayOrigin().equals("IntAct")) {
			//query = new IntActQueryRetrieval(detectPathwaySpeciesHeuristic());
			query = new IntActQueryRetrieval(detectPathwaySpeciesAll());
		} else if(detectPathwayOrigin() == null) {
			// TODO: throw Exception
			System.out.println("Cannot determine the origin source of the pathway");
		}
		try {
			query.retrieveQueryResults(identifierType, labels);
		} catch (IOException e) {
			// TODO: manage exceptions if for example the BioMart server is down
			e.printStackTrace();
		}
		resultMap = query.getResultMap();
		dataMappingModel.setQueryResultMap(resultMap);
		dataMappingModel.setColoringParameters();
		dataMappingModel.mergeAndColor();
		setChanged();
		notifyObservers(dataMappingModel);		
	}

	/**
	 * creates the HashMap of the data of the input file
	 */
	private void createIdentifiervalueMap() {
		Map<String, String> map = new HashMap<String, String>();
		for (String id : identifiers) {
			int index = identifiers.indexOf(id);
			map.put(id, values.get(index));
		}
		dataMappingModel.setIdentifiersValuesMap(map);
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
			Vector<String> rowData = allData.get(i);
			identifiers.add(rowData.get(identifierColumnIndex));
		}
		doCheck(IDENTIFIER);
	}

	/**
	 * extracts the values (stored in one column) out of the imported Data (stored row wise)
	 * @param valueColumnIndex
	 */
	public void setValues(int valueColumnIndex) {
		Vector<Vector<String>> allData = importData.getDataVector();
		values = new ArrayList<String>();
		for(int i = 0; i<allData.size(); i++) {
			Vector<String> rowData = allData.get(i);
			values.add(rowData.get(valueColumnIndex));
		}
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
	 * extracts the new data for the coloring from the JTable, invokes the coloring and notifies the View
	 * @param dmt
	 */
	public void setNewMergeMap(JTable dmt) {
		Map<String, List<String>> newMergeMap = new HashMap<String, List<String>>();
		Map<String, List<String>> newDupMap = new HashMap<String, List<String>>();
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
	}
}
