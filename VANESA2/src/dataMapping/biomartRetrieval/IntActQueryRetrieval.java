package dataMapping.biomartRetrieval;

import java.util.List;

/**
 * This class is the factory to build queries from HPRD network labels. It distinguishes between
 * the different queries by the given database of the input data
 *
 * @author dborck
 * 
 */
public class IntActQueryRetrieval extends BiomartQueryRetrieval{
	
	/**
	 * constructs the retrieval query and sets (with super()) the dataset corresponding to th
	 * given species
	 * @param species
	 */
	public IntActQueryRetrieval(String species) {
		super(species);
	}

	// the filter which is used in the BioMart query
	final String FILTERNAME = "uniprot_swissprot";
		
	/* (non-Javadoc)
	 * @see microarrayDataMapping.biomartRetrieval.BiomartQueryRetrieval#makeQuery(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	protected Query makeQuery(String database, List<String> fValues) {
		if (database.equals(BiomartQueryRetrieval.EMBL)) {
			return new EMBLQuery(BiomartQueryRetrieval.DATASET, FILTERNAME, fValues);
		} else if (database.equals(BiomartQueryRetrieval.UNIPROT)) {
			return new UniprotQuery(BiomartQueryRetrieval.DATASET, FILTERNAME, fValues);
		} else if (database.equals(BiomartQueryRetrieval.AGILENT)) {
			return new AgilentQuery(BiomartQueryRetrieval.DATASET, FILTERNAME, fValues);
		} else if (database.equals(BiomartQueryRetrieval.AFFYMETRIX)) {
			return new AffymetrixQuery(BiomartQueryRetrieval.DATASET, FILTERNAME, fValues);
		} else if (database.equals(BiomartQueryRetrieval.ILLUMINA)) {
			return new IlluminaQuery(BiomartQueryRetrieval.DATASET, FILTERNAME, fValues);
		}else return null;
	}
}
