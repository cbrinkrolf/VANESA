package dataMapping.biomartRetrieval;

import java.util.List;

/**
 * This class is the factory to build queries from HPRD network labels. It distinguishes between
 * the different queries by the given database of the input data
 *
 * @author dborck
 * 
 */
public class AgilentQueryRetrieval extends BiomartQueryRetrieval{
	
	/**
	 * constructs the retrieval query and sets (with super()) the dataset corresponding to th
	 * given species
	 * @param species
	 */
	public AgilentQueryRetrieval(String species) {
		super(species);
	}
	
	// the filter which is used in the BioMart query
	final String FILTERNAME = "efg_agilent_wholegenome_4x44k_v2";
		
	/* (non-Javadoc)
	 * @see microarrayDataMapping.biomartRetrieval.BiomartQueryRetrieval#makeQuery(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	protected Query makeQuery(String database, List<String> fValues) {	
		if (database.equals(BiomartQueryRetrieval.UNIPROT_WITHOUT_PW)) {
			return new UniprotQuery2(BiomartQueryRetrieval.DATASET, FILTERNAME, fValues);
		} else return null;
	}
}
