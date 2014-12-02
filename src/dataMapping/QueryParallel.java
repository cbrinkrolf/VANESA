/**
 * 
 */
package dataMapping;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import dataMapping.biomartRetrieval.BiomartQueryRetrieval;

/**
 * @author Britta Niemann
 *
 */
public class QueryParallel implements Callable<Map<String,String>>{
	

	private BiomartQueryRetrieval query;
	private List<String> ids;
	private String identifierType;
	
	public QueryParallel(BiomartQueryRetrieval query, List<String> list, String identifierType){
		this.query = query;
		this.ids = list;
		this.identifierType = identifierType;
	}
	

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Map<String, String> call() throws Exception {
		
		

		query.retrieveQueryResults(identifierType, ids);
		
		Map<String, String> resultMap = query.getResultMap();
		
		return resultMap;
	}

}
