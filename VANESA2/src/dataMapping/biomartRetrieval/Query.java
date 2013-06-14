package dataMapping.biomartRetrieval;

import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class manages the building of the xml query for the BioMart information retrieval
 * @author dborck
 *
 */
public abstract class Query {

	// some strings which are used in the BioMart XML query
	final String VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	final String HEADER = "<Query  formatter = \"TSV\" header = \"0\" uniqueRows = \"1\">";
	final String DATASETHEAD = "<Dataset name = \"";
	final String DATASETTAIL = "\" >";
	final String TAIL = "</Dataset>" +	"</Query>";
	final String EXCLUSIONHEAD = "<Filter name = \"";
	final String EXCLUSIONTAIL = "\" excluded = \"0\"/>";

	// additional query variables
	private String filterName;
	private String filterExclusionName = "";
	private String dataset;
	private List<String> attributeName = new ArrayList<String>();
	private List<String> filterValues = new ArrayList<String>();

	// the BioMart XML query
	private String query = "";

	/**
	 * concatenates the whole query
	 */
	public void buildQueryString() {
		query = query.concat(buildQueryHead()).concat(buildQueryMiddle()).concat(buildQueryTail());
	}

	/**
	 * build the first part of the query 
	 * @return the first part of the query as a string
	 */
	private String buildQueryHead() {
		return query.concat(VERSION).concat(HEADER).concat(getDataset());
	}
	
	/**
	 * sets the BioMart dataset query part as a string
	 * @param dataset which specified the mart in the BioMart query
	 */
	public void setDataset(String dataset) {
		this.dataset = DATASETHEAD.concat(dataset).concat(DATASETTAIL);
	}
	
	/**
	 * @return dataset which specified the mart in the BioMart query
	 */
	private String getDataset() {
		return dataset;
	}

	/**
	 * build the middle part of the query. Filters, filterexclusions, attriutes and
	 * request values are added 
	 * @return the middle part of the query as a string
	 */
	private String buildQueryMiddle() {
		String values = "";
		for (int i = 0; i < getFilterValues().size(); i++) {
			if (i!=0) {
				values = values.concat(",");
			}
			values = values.concat(getFilterValues().get(i));		
		}
		String attribs = "";
		for (String att : this.getAttributes()) {
			attribs = attribs.concat("<Attribute name = \"" + att + "\" />");		
		}
		String query = "";
		if (!filterExclusionName.equals("")) {
			query = query.concat(EXCLUSIONHEAD).concat(filterExclusionName).concat(EXCLUSIONTAIL);
		}
		query = query.concat(
				"<Filter name = \"" + getFilterName() + "\" value = \"" + values + "\"/>" + attribs
				);
		return query;
	}

	/**
	 * build the last part of the query
	 * @return the last part of the query as a string
	 */
	private String buildQueryTail() {
		return query.concat(TAIL);
	}

	/**
	 * @return the completed BioMart XML query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @return filterName specifies the type of requested data in the query
	 */
	public String getFilterName() {
		return filterName;
	}

	/**
	 * @param filterName the name of the identification
	 * of the requested values in the BioMart query
	 */
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	
	/**
	 * @param filterExclusionName for reducing the retrieval work,
	 * if the filterExclusionName is set then the respond contains only
	 * data if there are corresponding data in this filter
	 */
	public void setFilterExclusionName(String filterExclusionName) {
		this.filterExclusionName = filterExclusionName;
	}
	
	/**
	 * @return A list of strings for each a request will be constructed
	 */
	public List<String> getFilterValues() {
		return filterValues;
	}

	/**
	 * A list of strings for each a request will be constructed
	 * @param filterValues
	 */
	public void setFilterValues(List<String> filterValues) {
		this.filterValues = filterValues;
	}

	/**
	 * @return the specified names of the response values of the query, these define the header
	 *  names of the response, but the AttributeName do not have to be equal to the header names
	 */
	public List<String> getAttributeName() {
		return attributeName;
	}

	/**
	 * sets the specified names of the response values of the query, these define the header
	 * names of the response, but the AttributeName do not have to be equal to the header names
	 * @param attributeName 
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName.add(attributeName);
	}

	/**
	 * add together the filtername and the attributename to the attributes which are
	 * responded from the BioMart query
	 * @return list of attributes
	 */
	public List<String> getAttributes() {
		List<String> attributes = getAttributeName();
		attributes.add(getFilterName());
		return attributes;
	}
}
