package dataMapping.biomartRetrieval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This abstract class manages the BioMart information retrieval and stores the results in a map
 * The results should only contains two columns otherwise no mapping is possible (the first column
 * is the key, the second one is the value for the map)
 * 
 * @author dborck
 *
 */
public abstract class BiomartQueryRetrieval {
	
	// the mart which is used in BioMart
	final String MART = "ensembl";
	// the dataset of the mart which is used
	public static String DATASET = null;
	// the possible datasets
	final static String DATASET_HS = "hsapiens_gene_ensembl";
	final static String DATASET_MUS = "mmusculus_gene_ensembl";
	final static String DATASET_YEAST = "scerevisiae_gene_ensembl";
// TODO: perhaps add the following datasets to be able to deal with more species
//	final static String DATASET_RAT = "rnorvegicus_gene_ensembl";
//	final static String DATASET_DROME = "dmelanogaster_gene_ensembl";
	
	// all possible sources from the input data
	final static String EMBL = "EMBL";
	final static String UNIPROT = "UniProt";
	final static String AGILENT = "Agilent";
	final static String AFFYMETRIX = "Affymetrix";
	
	private Map<String, String> resultMap;
	
	/**
	 * constructs the BiomartQueryRetrieval and sets the corresponding dataset for the given species
	 * @param species
	 */
	public BiomartQueryRetrieval(String species) {
		if(species.equals("homo sapiens")) {
			DATASET = DATASET_HS;
		} else if(species.equals("mus musculus")) {
			DATASET = DATASET_MUS;
		} else if(species.equals("yeast")) {
			DATASET = DATASET_YEAST;
		}
	}

	/**
	 * Constructs a query dependent on the data source of the input data
	 * Build the query XML string and invokes the further request process
	 * 
	 * @param database sources from the input data
	 * @param fValues all values for the request 
	 * @throws IOException
	 */
	public void retrieveQueryResults(String database, List<String> fValues) throws IOException {		
		Query query = makeQuery(database, fValues);
		query.buildQueryString();
		proceedRequest(query.getQuery());
	}
	
	/**
	 * Open the connection to the BioMart server and streams the request to the server
	 * after finishing call a method to stor the results in a Map
	 * 
	 * @param request the BioMart XML query as a String
	 * @throws IOException
	 */
	private void proceedRequest(String request) throws IOException {
		// URL to the BioMart service
		URL url = new URL("http://www.biomart.org/biomart/martservice");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		
		String data = URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(request, "UTF-8");
		
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(data);
		out.close();
		//displayResults(conn);//instead of mapResults(), only for testing!
		mapResults(conn);
	}

	/**
	 * Instantiate a new query depending on the given database
	 * 
	 * @param database the source of the values of the input data  
	 * @param fValues the request values as a list for the BioMart query
	 * @return an abstract Query
	 */
	protected abstract Query makeQuery(String database, List<String> fValues);
	
	/**
	 * Retrieve all datasets which are available at the mart
	 * 
	 * @throws Exception
	 */
	public void getDatasets() throws Exception {
		// URL which retrieves the datasets
		URL url = new URL("http://www.biomart.org/biomart/martservice?type=datasets&mart="
						+ MART.replace(" ", "%20"));
		URLConnection urlConnection = url.openConnection();
		urlConnection.setDoOutput(true);
		
		displayResults((HttpURLConnection) urlConnection);
	}

	/**
	 * Retrieve all attributes for a dataset
	 * 
	 * @throws Exception
	 */
	public void getAttributes() throws Exception {
		// URL which retrieves the datasets
		URL url = new URL("http://www.biomart.org/biomart/martservice?type=attributes&dataset="
				+ DATASET.replace(" ", "%20"));
		URLConnection urlConnection = url.openConnection();
		urlConnection.setDoOutput(true);

		displayResults((HttpURLConnection) urlConnection);
	}
	
	/**
	 * Retrieve all filters for a dataset
	 * 
	 * @throws Exception
	 */
	public void getFilters() throws Exception {
		// URL which retrieves the datasets
		URL url = new URL("http://www.biomart.org/biomart/martservice?type=filters&dataset="
				+ DATASET.replace(" ", "%20"));
		URLConnection urlConnection = url.openConnection();
		urlConnection.setDoOutput(true);

		displayResults((HttpURLConnection) urlConnection);
	}

	/**
	 * Print out (to console) the results from BioMart server
	 * 
	 * @param conn the connection to the BioMart server
	 * @throws IOException
	 */
	private void displayResults(HttpURLConnection conn) throws IOException {
		// Open input stream to receive results from BioMart server
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		String results;
		while ((results = in.readLine()) != null) {
			System.out.println(results);
		}
		in.close();	
	}
	
	/**
	 * Stores the results in a map
	 * 
	 * @param conn the connection to the BioMart server
	 * @throws IOException
	 */
	private void mapResults(HttpURLConnection conn) throws IOException {
		// Open input stream to receive results from BioMart server
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		Map<String, String> map = new HashMap<String, String>();
		String results;
		while ((results = in.readLine()) != null) {
			String[] split = results.split("\t");
			if(split.length == 2) {
				String key = split[0];
				String value = split[1];
				map.put(key, value);
			}			
		}
		in.close();
		this.resultMap = map;	
	}
	
	/**
	 * @return  a Map with the complete results form the BioMart query
	 */
	public Map<String, String> getResultMap() {
		return resultMap;
	}

}
