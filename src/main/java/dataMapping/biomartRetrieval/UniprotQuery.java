package dataMapping.biomartRetrieval;

import java.util.ArrayList;
import java.util.List;

/**
 * This class extends Query in order to get results with the UniProt filter
 * @author dborck
 *
 */
public class UniprotQuery extends Query {
	
	// the attribute which is used in the BioMart query
	final String ATTRIBUTE = "uniprot_swissprot_accession";

	/**
	 * Fill the query variables with values
	 * 
	 * @param dataset used in the mart 
	 * @param filterName according to the UNIPROT accessions (e.g. "P19838")
	 * @param fValues the request values as a list for the BioMart query  
	 */
	public UniprotQuery(String dataset, String filterName, List<String> fValues) {
		setDataset(dataset);
		setFilterName(filterName);
		setAttributeName(ATTRIBUTE);
		setFilterValues(fValues);
	}
	
	/**
	 * add together the filtername and the attributename to the attributes which are
	 * responded from the BioMart query
	 * @return list of attributes
	 */
	public List<String> getAttributes() {
		List<String> attributes = new ArrayList<String>();
		attributes.addAll(getAttributeName());
		attributes.add(getFilterName());
		
		
		return attributes;
	}
}
