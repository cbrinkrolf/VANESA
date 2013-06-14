package hqllayer;

/**
 * @author Martin Lewinski
 * Copy of S. Janowski's BRENDAQueries, translated to HQL
 */

public class HQLBRENDAQueries {

//		public static final String getBRENDAenzyms = "SELECT * FROM enzyme e WHERE ";
		public static final String getHQLBRENDAenzyms = 
			"FROM brenda_enzyme e WHERE ";
		
//		public static final String getPossibleBRENDAenzymeNames = "SELECT e.ec,e.recommend_name FROM enzyme e WHERE ec LIKE ? or recommend_name LIKE ?;";
		public static final String getHQLPossibleBRENDAenzymeNames = 
			"SELECT e.ecNumber,e.recommentName FROM brenda_enzyme e WHERE e.ecNumber LIKE ? or e.recommentName LIKE ?";
			
//		public static final String getBRENDAenzymeDetails = "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary "
//			+ "FROM enzyme e LEFT outer JOIN reaction r on e.ec=r.enzyme WHERE e.ec=?;";
		public static final String getHQLBRENDAenzymeDetails = 
			"SELECT e.ecNumber,e.recommentName,e.systematicName,r.reaction FROM brenda_enzyme e "
			+ "LEFT OUTER JOIN e.reaction r WHERE e.ecNumber=?";
		
//		public static final String getBRENDAenzymeDetails2 = "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary "
//			+ "FROM enzyme e LEFT outer JOIN reaction r on e.ec=r.enzyme WHERE e.ec=?;";
		public static final String getHQLBRENDAenzymeDetails2 = 
			"SELECT e.ecNumber,e.recommentName,e.systematicName,e.reaction FROM Enzyme e  WHERE e.ecNumber=?;";
/**	TODO Sebastian fragen: Warum 2x die selbe query?*/
		
//		public static final String getAllBRENDAenzymeNames = "SELECT e.ec,e.recommend_name FROM enzyme e;";
		public static final String getHQLAllBRENDAenzymeNames = 
			"SELECT e.ecNumber,e.recommentName FROM brenda_enzyme e";
		
//		public static final String getAllBRENDAenzymeDetails = "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary "
//			+ "FROM enzyme e LEFT outer JOIN reaction r on e.ec=r.enzyme;";
		public static final String getHQLAllBRENDAenzymeDetails = 
			"SELECT e.ecNumber,e.recommentName,e.systematicName,r.reaction FROM brenda_enzyme e LEFT JOIN e.reaction r";
		
//		public static final String getPossibleEnzymeDetails = "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary "
//				+ "FROM enzyme e LEFT outer JOIN reaction r on e.ec=r.enzyme WHERE r.reaction LIKE ";
		public static final String getHQLPossibleEnzymeDetails = 
			"SELECT e.ecNumber,e.recommentName,e.systematicName,r.reaction FROM brenda_enzyme e LEFT JOIN e.reaction r WHERE r.reaction LIKE ?";

//		public static final String searchBrendaEnzyms = "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction, r.commentary, org.name "
//				+ "FROM enzyme e LEFT Outer JOIN reaction r on e.ec=r.enzyme "
//				+ "LEFT Outer JOIN ec2organism o on e.ec=o.ec "
//				+ "LEFT Outer JOIN organism org on org.id=o.org_id WHERE ";
		public static final String searchBrendaEnzyms = 
			"SELECT e.ecNumber,e.recommentName,e.systematicName,r.reaction,o.organismName FROM brenda_enzyme e "
			+ "LEFT JOIN e.reaction r LEFT JOIN e.organism o WHERE ";
		
//		public static final String getPossibleEnzymeDetailsWithOrganism = "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary "
//				+ "FROM enzyme e "
//				+ "LEFT outer JOIN reaction r on e.ec=r.enzyme "
//				+ "LEFT outer JOIN ec2organism org on e.ec=org.ec "
//				+ "LEFT outer JOIN organism o on org.org_id=o.id "
//				+ "WHERE r.reaction LIKE ? and o.name LIKE ?";
		public static final String getHQLPossibleEnzymeDetailsWithOrganism = 
			"SELECT e.ecNumber,e.recommentName,e.systematicName,r.reaction FROM brenda_enzyme e "
			+ "LEFT JOIN e.reaction r LEFT JOIN e.organism o "
			+ "WHERE r.reaction LIKE ? and o.organismName LIKE ?";
		
		
//		public static final String getPossibleEnzymeDetailsWithOrganismAndLocalisation = "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary,loc.localisation "
//				+ "FROM enzyme e "
//				+ "LEFT outer JOIN reaction r on e.ec=r.enzyme "
//				+ "LEFT outer JOIN ec2organism org on e.ec=org.ec "
//				+ "LEFT outer JOIN organism o on org.org_id=o.id "
//				+ "LEFT outer JOIN localisation2ec l on l.enzyme=e.ec "
//				+ "LEFT outer JOIN localisation loc on loc.localisation_id=l.localisation_id "
//				+ "WHERE r.reaction LIKE ? and o.name LIKE ? and loc.localisation LIKE ?";
		public static final String getHQLPossibleEnzymeDetailsWithOrganismAndLocalisation = 			
			"SELECT l.enzyme.ecNumber, l.enzyme.recommentName, l.enzyme.systematicName, r.reaction, l.entry "
			+ "FROM brenda_localization l LEFT JOIN l.enzyme.reaction r LEFT JOIN l.enzyme.organism o "
			+ "WHERE r.reaction LIKE ? AND o.organismName LIKE ? AND l.entry LIKE ?";

		
//		public static final String getPossibleEnzymeDetailsWithLocalisation = "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary,loc.localisation,o.name "
//			+ "FROM enzyme e "
//			+ "LEFT outer JOIN reaction r on e.ec=r.enzyme "
//			+ "LEFT outer JOIN ec2organism org on e.ec=org.ec "
//			+ "LEFT outer JOIN organism o on org.org_id=o.id "
//			+ "LEFT outer JOIN localisation2ec l on l.enzyme=e.ec "
//			+ "LEFT outer JOIN localisation loc on loc.localisation_id=l.localisation_id "
//			+ "WHERE e.ec=?";
		public static final String getHQLPossibleEnzymeDetailsWithLocalisation = 
			"SELECT l.enzyme.ecNumber,l.enzyme.recommentName,l.enzyme.systematicName,r.reaction,l.entry,o.organismName "
			+ "FROM brenda_localization l LEFT JOIN l.enzyme.reaction r LEFT JOIN l.enzyme.organism o "
			+ "WHERE l.enzyme.ecNumber=?";
			
		//public static final String getHQLEnzymeSynonyms = "SELECT Distinct e.* FROM synonym s INNER JOIN enzyme e " +
		//		"on e.ec=s.ec WHERE s.name LIKE ? or s.ec LIKE ? or e.recommend_name LIKE ? or e.systematic_name LIKE ? order by e.ec;";
		public static final String getHQLEnzymeSynonyms =
			"SELECT distinct e.ecNumber,e.idComment,e.registryNumber,e.recommentName,e.systematicName FROM brenda_synonyms s INNER JOIN s.enzyme e "
			+ "WHERE s.synonym LIKE ? or s.enzyme LIKE ? or e.recommentName LIKE ? or e.systematicName LIKE ? order by e.ecNumber";
		
		//public static final String getHQLCoFactor ="SELECT c.enzyme,co.cofactor FROM cofactor2ec c LEFT outer JOIN cofactor co on c.cofactor_id=co.cofactor_id WHERE c.enzyme IN ";
		public static final String getHQLCoFactor =
			"SELECT c.enzyme.ecNumber,c.entry FROM brenda_cofactor c WHERE c.enzyme.ecNumber IN ";		
			
		//public static final String getHQLComment = "SELECT e.ec,co.comment_text FROM ec2commentary e LEFT outer JOIN commentary co on e.comment_id=co.id WHERE e.ec IN ";
		public static final String getHQLComment = 
			"SELECT e.ecNumber,e.idComment FROM brenda_enzyme e WHERE e.ecNumber IN ";
		
		//public static final String getHQLInhibitor="SELECT i.enzyme,ih.inhibitor FROM inhibitor2ec i LEFT outer JOIN inhibitor ih on i.inhibitor_id=ih.inhibitor_id WHERE i.enzyme IN ";
		public static final String getHQLInhibitor=
			"SELECT i.enzyme.ecNumber,i.entry FROM brenda_inhibitor i WHERE i.enzyme.ecNumber IN ";
}


