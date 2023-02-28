package database.brenda;

public class BRENDAQueries {
    public static final String getBRENDAenzymeDetails =
            "SELECT e.ec_number,e.recomment_name,r.reaction " +
                    "FROM brenda_enzyme e Left " +
                    "Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number " +
                    "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id " +
                    "where e.ec_number=?;";

    public static final String searchBrendaEnzyms =
            "SELECT e.ec_number, e.recomment_name, r.reaction, org.org_name " +
                    "FROM brenda_enzyme e " +
                    "Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number " +
                    "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id " +
                    "Left Outer Join brenda_enzyme2organism o on e.ec_number=o.ec_number " +
                    "Left Outer Join brenda_organism org on org.org_id=o.org_id " +
                    "where ";

    public static final String getPossibleEnzymeDetails =
            "SELECT e.ec_number,e.recomment_name,r.reaction " +
                    "FROM brenda_enzyme e " +
                    "Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number " +
                    "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id " +
                    "where r.reaction like ";

    public static final String getPossibleEnzymeDetailsWithOrganism =
            "SELECT e.ec_number,e.recomment_name,r.reaction " +
                    "FROM brenda_enzyme e " +
                    "Left Outer Join brenda_enzyme2reaction er on e.ec_number=er.ec_number " +
                    "Left Outer Join brenda_reaction r on er.reaction_id=r.reaction_id " +
                    "Left Outer Join brenda_enzyme2organism o on e.ec_number=o.ec_number " +
                    "Left Outer Join brenda_organism org on org.org_id=o.org_id " +
                    "where r.reaction like ? and org.org_name like ?";
}