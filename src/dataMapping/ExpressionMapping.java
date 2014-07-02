package dataMapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import dataMapping.biomartRetrieval.AgilentQueryRetrieval;
import dataMapping.biomartRetrieval.BiomartQueryRetrieval;
import dataMapping.dataImport.ImportExcelxData;

public class ExpressionMapping {

//	private static BiomartQueryRetrieval query;

//	public static void main(String[] args) {
//		query = new AgilentQueryRetrieval("homo sapiens");
//		Map<String, String> results = null;
//		List<String> agilentIds = new LinkedList<String>();
//		
//		ImportExcelxData importData = null;
//		File file = new File("C:\\Users\\Britta\\Documents\\Uni Bielefeld\\Bio-Informatik\\10. Semester\\Masterarbeit\\VanesaData\\test1.xls");
////		File file = new File("C:\\Users\\Britta\\Documents\\Uni Bielefeld\\Bio-Informatik\\10. Semester\\Masterarbeit\\VanesaData\\auswahl_summary_all_median_seven_2011-12-01.xls");
//
//		try {
//			importData = new ImportExcelxData(file);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		int[] columns = {5, 6, 7};
//		Vector<Vector<String>> allData = importData.getDataVector();
//		Map<String, List<String>> expressionMap = new HashMap<String, List<String>>();
//		for(int i = 0; i<allData.size(); i++) {
//			Vector<String> rowData = allData.get(i);
//			agilentIds.add(rowData.get(1));
//			List<String> expresionValues = new LinkedList<String>();
//			for(int column : columns){
//				expresionValues.add(rowData.get(column));
//			}
//			
//			expressionMap.put(rowData.get(1), expresionValues);
////			System.out.println(rowData.get(1));
//		}
//		
////		agilentIds.add("A_23_P98183");
////		agilentIds.add("A_23_P84596");
//		try {
//			query.retrieveQueryResults("UniProt", agilentIds);
//			results = query.getResultMap();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		for(String key : results.keySet()){
//			if(!key.isEmpty()){
//				System.out.print(key + " (");
//				System.out.print(results.get(key) + "): ");
//				for(String value : expressionMap.get((results.get(key)))){
//					System.out.print(value + " ");
//				}
//				System.out.println();
////				System.out.println(key + ": " + results.get(key));
//			}
//			
//		}
//
//		
//	}

}
