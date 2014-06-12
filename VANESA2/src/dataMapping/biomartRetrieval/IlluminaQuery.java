package dataMapping.biomartRetrieval;

import java.util.List;

/**
 * This class extends Query in order to get results with the EMBL filter
 * @author dborck
 *
 */
public class IlluminaQuery extends Query {
	
	// the attribute which is used in the BioMart query
	final String ATTRIBUTE = "illumina_humanwg_6_v2";
	
	/**
	 * Fill the query variables with values
	 * 
	 * @param dataset used in the mart 
	 * @param filterName according to the EMBL accessions (e.g. "M58603")
	 * @param fValues the request values as a list for the BioMart query  
	 */
	public IlluminaQuery(String dataset,String filterName, List<String> fValues) {
		setDataset(dataset);
		setFilterName(filterName);
		setAttributeName(ATTRIBUTE);
		setFilterValues(fValues);
	}
}
