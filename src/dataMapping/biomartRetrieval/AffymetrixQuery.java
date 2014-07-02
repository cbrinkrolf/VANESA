package dataMapping.biomartRetrieval;

import java.util.ArrayList;
import java.util.List;

/**
 * This class extends Query in order to get results with the Affymetrix filter
 * @author dborck
 *
 */
public class AffymetrixQuery extends Query {

	// the attribute which is used in the BioMart query
	final String ATTRIBUTE_HS = "affy_hg_u133_plus_2";
	final String ATTRIBUTE_MUS = "affy_mouse430a_2";
	final String ATTRIBUTE_YEAST = "affy_yeast_2";
	// only homo sapiens data which have values with this attribute are returned
	final String FILTEREXCLUSION = "with_affy_hg_u133_plus_2";

	/**
	 * Fill the query variables with values
	 * 
	 * @param dataset used in the mart 
	 * @param filterName according to the Affymetrix microarray accessions (e.g. "209636_at")
	 * @param fValues the request values as a list for the BioMart query  
	 */
	public AffymetrixQuery(String dataset, String filterName, List<String> fValues) {
		setDataset(dataset);
		setFilterName(filterName);	
		if(dataset.startsWith("hsapiens")) {
			setFilterExclusionName(FILTEREXCLUSION);
			setAttributeName(ATTRIBUTE_HS);
		} else if (dataset.startsWith("mmusculus")) {
			setAttributeName(ATTRIBUTE_MUS);
		} else if (dataset.startsWith("scerevisiae")) {
			setAttributeName(ATTRIBUTE_YEAST);
		}
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
