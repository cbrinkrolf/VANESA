package cluster.slave;

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
	public static final int LAYOUT_MULTILEVEL_JOB = 60;
	
	
	/**
	 * Graphdb only jobs
	 */
	public static final int MAPPING_HPRD = 110;
	public static final int MAPPING_BRENDA = 111;
	public static final int MAPPING_INTACT = 112;
	public static final int MAPPING_KEGG = 113;
	public static final int MAPPING_MINT = 114;
	public static final int MAPPING_UNIPROT = 115;

	public static final int SEARCH_DEPTH = 100;
	public static final int SEARCH_MINIMAL_NETWORK = 101;
	public static final int SEARCH_DATASET = 102;
}
