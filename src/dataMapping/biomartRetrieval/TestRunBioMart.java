package dataMapping.biomartRetrieval;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is a class to test the BioMart query information retrieval
 * @author dborck
 *
 */
public class TestRunBioMart {

	public static void main(String[] args) throws Exception {
		
		// all (in this program) up to date available marts of BioMart
		String[] databases = {"EMBL", "Agilent", "Affymetrix", "UniProt", "Illumina"};
		
		// some test values for the query, the values are labels of a VANESA PPI network
		List<String> testValues = new ArrayList<String>();
//		testValues.add("nfkb1_human");
//		testValues.add("nfkb2_human");
//		testValues.add("P19838");
//		testValues.add("nfkb1_mouse");
//		testValues.add("NFKB2");
//		testValues.add("nfkb1");
//		testValues.add("traf6");
//		testValues.add("trad1");
//		testValues.add("ripk1");
		testValues.add("Bnip3_mouse");
		testValues.add("pax3_human");
		testValues.add("dap1_mouse");
				
		// instantiate the query
		BiomartQueryRetrieval queryResults = null;
		
		// query factory depending on the origin of the values
		if(testValues.get(0).contains("_")){
			queryResults = new IntActQueryRetrieval("mus musculus");
		} else  if (!testValues.get(0).contains("_")){
			queryResults = new HPRDQueryRetrieval("homo sapiens");
		}
		
		/*
		 * information about the datasets used in BioMArt
		 * we use the following datasets:
		 * hsapiens_gene_ensembl	Homo sapiens genes (GRCh37.p8)	2012-10-02 10:58:23
		 * mmusculus_gene_ensembl	Mus musculus genes (GRCm38)	2012-10-02 11:02:05
		 * scerevisiae_gene_ensembl	Saccharomyces cerevisiae genes (EF4)	2012-10-02 11:02:02 
		 */
//		queryResults.getDatasets();
//		queryResults.getAttributes();
//		queryResults.getFilters();
		
		// test query for each database with a println output 
		for (String database : databases) {
			queryResults.retrieveQueryResults(database, testValues);
			Map<String, String> map = queryResults.getResultMap();
			System.out.println("Results for dataset: " + database);
			for(Entry<String, String> entry : map.entrySet()){
				System.out.println("accession: " + entry.getKey() + "\t label: " + entry.getValue().toString());
			}
		}
	}
}
