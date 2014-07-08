package cluster;

public class JobTypes {
	/**
	 * Decorator for Cluster job types
	 */

	public static final int CYCLE_JOB_OCCURRENCE = 10;
	public static final int CYCLE_JOB_NEIGHBORS = 11;
	public static final int CLIQUE_JOB_OCCURRENCE = 20;
	public static final int CLIQUE_JOB_NEIGHBORS = 21;
	public static final int CLIQUE_JOB_PATHSLESS = 22;
	public static final int CLIQUE_JOB_CONNECTIVITY = 23;
	public static final int APSP_JOB = 30;
	public static final int SPECTRAL_CLUSTERING_JOB = 40;
	public static final int LAYOUT_FR_JOB = 50;
	
	
	/**
	 * Graphdb only jobs
	 */
	public static final int HPRD_MAPPING = 110,
			BRENDA_MAPPING = 111,
			INTACT_MAPPING = 112,
			KEGG_MAPPING = 113,
			MINT_MAPPING = 114,
			UNIPROT_MAPPING = 115,
			
			DEPTH_SEARCH = 100,
			MINIMAL_NETWORK_SEARCH = 101;
}
