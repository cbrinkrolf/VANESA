package database.brenda;

public class BRENDAQueries {

	// public static final String getBRENDAenzyms =
	// "SELECT * FROM db_brenda.enzyme e where ";
	public static final String getBRENDAenzyms = "SELECT * FROM brenda_enzyme e where ?;";

	// public static final String getPossibleBRENDAenzymeNames =
	// "SELECT e.ec,e.recommend_name FROM db_brenda.enzyme e where ec like ? or recommend_name like ?;";
	public static final String getPossibleBRENDAenzymeNames = "SELECT e.ec_number,e.recomment_name FROM brenda_enzyme e where ec_number like ? or recomment_name like ?";

	// public static final String getBRENDAenzymeDetails =
	// "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary "
	// +
	// "FROM db_brenda.enzyme e Left outer join db_brenda.reaction r on e.ec=r.enzyme where e.ec=?;";
	public static final String getBRENDAenzymeDetails = "SELECT e.ec_number,e.recomment_name,e.systematic_name,r.reaction "
			+ "FROM brenda_enzyme e Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number "
			+ "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id where e.ec_number=?;";

	// public static final String getBRENDAenzymeDetails2 =
	// "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary "
	// +
	// "FROM db_brenda.enzyme e Left outer join db_brenda.reaction r on e.ec=r.enzyme where e.ec=?;";
	public static final String getBRENDAenzymeDetails2 = "SELECT e.ec_number,e.recomment_name,e.systematic_name,r.reaction "
			+ "FROM brenda_enzyme e Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number "
			+ "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id where e.ec_number=?;";

	// public static final String getAllBRENDAenzymeNames =
	// "SELECT e.ec,e.recommend_name FROM db_brenda.enzyme e;";
	public static final String getAllBRENDAenzymeNames = "SELECT e.ec_number,e.recomment_name FROM brenda_enzyme e;";

	// public static final String getAllBRENDAenzymeDetails =
	// "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary "
	// +
	// "FROM db_brenda.enzyme e Left outer join db_brenda.reaction r on e.ec=r.enzyme;";
	public static final String getAllBRENDAenzymeDetails = "SELECT e.ec_number,e.recomment_name,e.systematic_name,r.reaction "
			+ "FROM brenda_enzyme e Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number "
			+ "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id;";

	// public static final String getPossibleEnzymeDetails =
	// "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary "
	// +
	// "FROM db_brenda.enzyme e Left outer join db_brenda.reaction r on e.ec=r.enzyme where r.reaction like ";
	// public static final String getPossibleEnzymeDetails =
	// "SELECT e.ec_number,e.recomment_name,e.systematic_name,r.reaction "
	// +
	// "FROM brenda_enzyme e Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number "
	// +
	// "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id where r.reaction like ?";
	public static final String getPossibleEnzymeDetails = "SELECT e.ec_number,e.recomment_name,e.systematic_name,r.reaction "
			+ "FROM brenda_enzyme e Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number "
			+ "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id where r.reaction like ";

	// public static final String searchBrendaEnzyms =
	// "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction, r.commentary, org.name "
	// +
	// "FROM db_brenda.enzyme e Left Outer Join db_brenda.reaction r on e.ec=r.enzyme "
	// + "Left Outer Join db_brenda.ec2organism o on e.ec=o.ec "
	// + "Left Outer Join db_brenda.organism org on org.id=o.org_id where ";
	public static final String searchBrendaEnzyms = "SELECT e.ec_number,e.recomment_name,e.systematic_name,r.reaction, org.org_name "
			+ "FROM brenda_enzyme e Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number "
			+ "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id "
			+ "Left Outer Join brenda_enzyme2organism o on e.ec_number=o.ec_number "
			+ "Left Outer Join brenda_organism org on org.org_id=o.org_id where ";

	// public static final String getPossibleEnzymeDetailsWithOrganism =
	// "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary "
	// + "FROM db_brenda.enzyme e "
	// + "Left outer join db_brenda.reaction r on e.ec=r.enzyme "
	// + "Left outer join ec2organism org on e.ec=org.ec "
	// + "Left outer join organism o on org.org_id=o.id "
	// + "where r.reaction like ? and o.name like ?";
	public static final String getPossibleEnzymeDetailsWithOrganism = "SELECT e.ec_number,e.recomment_name,e.systematic_name,r.reaction "
			+ "FROM brenda_enzyme e Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number "
			+ "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id "
			+ "Left Outer Join brenda_enzyme2organism o on e.ec_number=o.ec_number "
			+ "Left Outer Join brenda_organism org on org.org_id=o.org_id where "
			+ "r.reaction like ? and org.org_name like ?";

	// public static final String
	// getPossibleEnzymeDetailsWithOrganismAndLocalisation =
	// "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary,loc.localisation "
	// + "FROM db_brenda.enzyme e "
	// + "Left outer join db_brenda.reaction r on e.ec=r.enzyme "
	// + "Left outer join ec2organism org on e.ec=org.ec "
	// + "Left outer join organism o on org.org_id=o.id "
	// + "Left outer join localisation2ec l on l.enzyme=e.ec "
	// +
	// "Left outer join localisation loc on loc.localisation_id=l.localisation_id "
	// +
	// "where r.reaction like ? and o.name like ? and loc.localisation like ?";
	public static final String getPossibleEnzymeDetailsWithOrganismAndLocalisation = "SELECT e.ec_number, e.recomment_name, e.systematic_name, r.reaction, l.localisation "
			+ "FROM brenda_enzyme e Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number "
			+ "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id "
			+ "Left Outer Join brenda_enzyme2organism o on e.ec_number=o.ec_number "
			+ "Left Outer Join brenda_organism org on org.org_id=o.org_id "
			+ "Left outer join brenda_localization l on l.enzyme=e.ec_number "
			+ "where r.reaction like ? and o.org_name like ? and l.localization like ?";

	// public static final String getPossibleEnzymeDetailsWithLocalisation =
	// "SELECT e.ec,e.recommend_name,e.systematic_name,r.reaction,r.commentary,loc.localisation,o.name "
	// + "FROM db_brenda.enzyme e "
	// + "Left outer join db_brenda.reaction r on e.ec=r.enzyme "
	// + "Left outer join ec2organism org on e.ec=org.ec "
	// + "Left outer join organism o on org.org_id=o.id "
	// + "Left outer join localisation2ec l on l.enzyme=e.ec "
	// +
	// "Left outer join localisation loc on loc.localisation_id=l.localisation_id "
	// + "where e.ec=?";
	public static final String getPossibleEnzymeDetailsWithLocalisation = "SELECT e.ec_number,e.recomment_name,e.systematic_name,r.reaction,l.localisation,o.org_name "
			+ "FROM brenda_enzyme e Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number "
			+ "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id "
			+ "Left Outer Join brenda_enzyme2organism o on e.ec_number=o.ec_number "
			+ "Left Outer Join brenda_organism org on org.org_id=o.org_id "
			+ "Left outer join brenda_localization l on l.enzyme=e.ec_number "
			+ "where e.ec_number=?";

	// public static final String getEnzymeSynonyms =
	// "SELECT Distinct e.* FROM db_brenda.synonym s Inner JOIN db_brenda.enzyme e "
	// +
	// "on e.ec=s.ec where s.name like ? or s.ec like ? or e.recommend_name like ? or e.systematic_name like ? order by e.ec;";
	public static final String getEnzymeSynonyms = "SELECT Distinct e.* FROM brenda_synonyms s Inner JOIN brenda_enzyme e "
			+ "on e.ec_number=s.enzyme where s.bsynonym like ? or s.enzyme like ? or e.recomment_name like ? or e.systematic_name like ? order by e.ec_number;";

	public static final String getCoFactor = "SELECT c.enzyme,c.cofactor FROM brenda_cofactor c where c.enzyme In ";

	public static final String getSpecificCoFactor = "SELECT c.enzyme,c.cofactor FROM (brenda_cofactor c natural join brenda_cofactor2organism b) natural join brenda_organism o where o.org_name=? and c.enzyme In ";

	// public static final String getComment =
	// "SELECT e.ec_number,e.id_comment FROM brenda_enzyme e where e.ec_number In ";

	public static final String getInhibitor = "SELECT i.enzyme,i.inhibitor FROM brenda_inhibitor i where i.enzyme In ";

	public static final String getSpecificInhibitor = "SELECT i.enzyme,i.inhibitor FROM (brenda_inhibitor i natural join brenda_inhibitor2organismb) natural join brenda_organism o where o.org_name=? where i.enzyme In ";
}
