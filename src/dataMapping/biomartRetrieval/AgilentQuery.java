package dataMapping.biomartRetrieval;

import java.util.ArrayList;
import java.util.List;

/**
 * This class extends Query in order to get results with the Agilent filter
 * @author dborck
 *
 */
public class AgilentQuery extends Query {
	
	// the attribute which is used in the BioMart query
	final String ATTRIBUTE = "efg_agilent_wholegenome_4x44k_v2";
	// only data how have values with this attribute are returned
	final String FILTEREXCLUSION = "with_efg_agilent_wholegenome_4x44k_v2";

	/**
	 * Fill the query variables with values
	 * 
	 * @param dataset used in the mart 
	 * @param filterName according to the Agilent microarray accessions (e.g. "A_23_P202156")
	 * @param fValues the request values as a list for the BioMart query  
	 */
	public AgilentQuery(String dataset, String filterName, List<String> fValues) {
		setDataset(dataset);
		setFilterName(filterName);
		setFilterExclusionName(FILTEREXCLUSION);
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
