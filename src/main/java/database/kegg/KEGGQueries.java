package database.kegg;

import java.util.ArrayList;

import configurations.Wrapper;
import pojos.DBColumn;

public class KEGGQueries {
    public static final String KEGGPathwayQuery = "SELECT p.pathway_name,p.title,p.org FROM kegg_pathway p where ";
    public static final String KEGGGeneQuery = "SELECT Distinct Concat(p.org,p.kegg_number) as pathway_name FROM kegg_genes_pathway as p Inner join kegg_genes_name as n on p.entry=n.entry where ";
    public static final String KEGGEnzymeQuery = "SELECT Distinct CONCAT('map',e.kegg_number) as pathway_name FROM kegg_enzyme_pathway e Inner Join kegg_enzyme_name n on e.entry=n.entry where ";
    public static final String KEGGCompoundQuery = "SELECT Distinct Concat(p.organismus,p.kegg_number) as pathway_name FROM kegg_compound_name c inner join kegg_pathway_compound as p on c.entry=p.entry where ";

    public static ArrayList<DBColumn> getPathwayElements(String pathwayID) {
        String query = "SELECT k.id, k.link, k.entry_type, n.name, g.bgcolor, g.fgcolor, g.name, g.graphics_type, g.x, g.y, c.name, p.name, g.width, g.height, k.pathway_name "
                + "FROM kegg_kgml_entry k left outer join kegg_kgml_entry_name n "
                + "    on k.entry_id=n.entry_ID "
                + "Inner join kegg_kgml_graphics g "
                + "    on k.entry_id=g.entry_ID "
                + "left outer join kegg_compound_name  c"
                + "    on n.name=c.entry "
                + "left outer join kegg_pathway p"
                + "    on n.name=p.entry "
                + "where k.pathway_name='" + pathwayID + "' "
                + "AND (length(c.name)=(Select min(length(d.name)) from kegg_compound_name d where n.name=d.entry) OR c.name is NULL) "
                + "group by k.entry_id;";
        return new Wrapper().requestDbContent(2, query);
    }

    public static ArrayList<DBColumn> requestDbContent(String pathway, String organism, String gene, String compound, String enzyme) {
        String query = "Select distinct p.entry,p.name,p.organism "
                + "from kegg_pathway p ";
        if (!gene.equals(""))
            query += "left join (kegg_pathway_gene pg inner join kegg_genes g inner join kegg_genes_name gn) "
                    + "on p.entry=pg.entry AND pg.gene_id=g.gene_id AND g.id=gn.id ";
        if (!compound.equals("")) query += "left join (kegg_pathway_compound pc inner join kegg_compound_name cn) "
                + "on p.entry=pc.entry AND pc.compound=cn.entry ";
        if (!enzyme.equals(""))
            query += "left join (kegg_kgml_pathway kp inner join kegg_enzyme_pathway ep inner join kegg_enzyme_name en) "
                    + "on p.entry=kp.name AND kp.number=ep.number AND ep.entry=en.entry ";
        query += "where p.name like '%" + pathway + "%' AND p.organism like '%" + organism + "%' ";
        if (!gene.equals("")) query += "AND gn.name like '%" + gene + "%' ";
        if (!compound.equals("")) query += "AND cn.name like '%" + compound + "%' ";
        if (!enzyme.equals("")) query += "AND en.entry like '%" + enzyme + "%' ";
        query += "limit 0,1000;";
        return new Wrapper().requestDbContent(2, query);
    }

    public static ArrayList<DBColumn> getPathway(String pathwayID) {
        String query = "SELECT name,title,org,number,image,link FROM kegg_kgml_pathway p where name = '"
                + pathwayID + "';";
        return new Wrapper().requestDbContent(2, query);
    }

    public static ArrayList<DBColumn> getRelations(String pathwayID) {
        String query = "SELECT relation.pathway_name, subtype.name,subtype.subtype_value,relation.entry1,relation.entry2,relation.relation_type "
                + "FROM kegg_kgml_subtype subtype natural join kegg_kgml_relation relation "
                + " where pathway_name='" + pathwayID + "'order by relation_id;";
        return new Wrapper().requestDbContent(2, query);
    }

    public static ArrayList<DBColumn> getAllReactions(String pathwayID) {
        String query = "SELECT s.id,e.id,p.id,r.reaction_type, e.pathway_name " +
                "FROM kegg_kgml_reaction r " +
                "inner join kegg_kgml_substrate s on r.reaction_id=s.reaction_id " +
                "inner join kegg_kgml_product p on r.reaction_id=p.reaction_id " +
                "inner join kegg_kgml_entry_reaction er on er.reaction=r.name " +
                "inner join kegg_kgml_entry e on er.entry_id=e.entry_id " +
                "inner join kegg_kgml_entry_name en on e.entry_id=en.entry_id " +
                "where r.pathway_name='" + pathwayID + "' and e.pathway_name='" + pathwayID + "'; ";
        return new Wrapper().requestDbContent(2, query);
    }
}
