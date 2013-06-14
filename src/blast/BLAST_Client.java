package blast;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

import cern.colt.matrix.DoubleMatrix2D;

public class BLAST_Client {

	private DoubleMatrix2D simMatrix;
	private HashMap<Integer, String> position2QueryID;
	private HashMap<Integer, String> position2referenceID;
	private HashMap<String, Integer> queryID2Position;
	private HashMap<String, Integer> referenceID2Position;

	public BLAST_Client() {
//		try {
//			InetAddress addr = InetAddress
//					.getByName("agbi.techfak.uni-bielefeld.de");
//			System.out.println(addr.getHostName());
//			System.out.println(addr.getHostAddress());
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
	}

	@SuppressWarnings("unchecked")
	public void allAgainstAll(HashMap<String, String> mapRef,
			HashMap<String, String> mapQuery, String mode) {

		// if (System.getSecurityManager() == null) {
		// System.setSecurityManager(new SecurityManager());
		// }

		try {
			String name = "BLAST_Server";
			Registry registry = LocateRegistry.getRegistry();
			BLAST_Interface blast = (BLAST_Interface) registry.lookup(name);
			Object[] result = blast.startAllAgainstAll(mapRef, mapQuery, mode);
			simMatrix = (DoubleMatrix2D) result[0];
			position2QueryID = (HashMap<Integer, String>) result[1];
			position2referenceID = (HashMap<Integer, String>) result[2];
			queryID2Position = (HashMap<String, Integer>) result[3];
			referenceID2Position = (HashMap<String, Integer>) result[4];
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DoubleMatrix2D getSimMatrix() {
		return simMatrix;
	}

	public HashMap<Integer, String> getPosition2QueryID() {
		return position2QueryID;
	}

	public HashMap<Integer, String> getPosition2referenceID() {
		return position2referenceID;
	}

	public HashMap<String, Integer> getQueryID2Position() {
		return queryID2Position;
	}

	public HashMap<String, Integer> getReferenceID2Position() {
		return referenceID2Position;
	}

	// TEST
	public static void main(String[] args) {

		HashMap<String, String> mapRef = new HashMap<String, String>();
//		mapRef
//				.put(
//						"protein1",
//						"DFGHWJTZJETZKZUKRHADFHGSRJTTZJDTJTZJHAFDVDCBSFGNSFGNAEJSTADFHGSGFSDGFADGFADFLDZLFUKJSG");
//		mapRef.put("protein2", "SDHRTHSTHSFGSGS");
//		mapRef.put("protein3", "ASADFWEGDHGFUZFEINNFA");
//		mapRef.put("protein4", "RIHRHKAJDKAEKFHHF");
//		mapRef.put("protein5", "HILHFJMDGFNSBGSFBSDFBYDV");
		mapRef.put("DNA1", "atccggcgcagtcagctagctagtgtacgtgcatcgtactttttttttgggggggggggggaaaaaacccccccccccccacttattagccggcatatgcgcggcattcattataggagactctggagcggatcggcgcta");
		mapRef.put("DNA2", "atccggcgtacgtgcatcgtacttaaaaaaccccccccccggcattcattataaattttttttttttttttttttccccccggagactctggagcggatcggcgcta");

		HashMap<String, String> mapQuery = new HashMap<String, String>();
//		mapQuery
//				.put(
//						"p1",
//						"DFGHWJTZJETZKZUKRHADFHGSRJTTZJDTJTZJHAFDVDCBSFGNSFGNAEJSTADFHGSGFSDGFADGFADFLDZLFUKJSG");
//		mapQuery.put("p2", "SDHRTHSTHSFGSGS");
//		mapQuery.put("p3", "ASADFWEGDHGFUZFEINNFA");
//		mapQuery.put("p4", "RIHRHKAJDKAEKFHHF");
		mapQuery.put("dna1", "atccggcgcagtcagctagctagtgtacgtgcatcgtactttttttttgggggggggggggaaaaaacccccccccccccacttattagccggcatatgcgcggcattcattataggagactctggagcggatcggcgcta");

		String mode = "blastn";

		BLAST_Client client = new BLAST_Client();
		client.allAgainstAll(mapRef, mapQuery, mode);
		
		//System.out.println(client.getSimMatrix());
	}

}
