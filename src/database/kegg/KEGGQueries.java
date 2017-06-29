package database.kegg;

import java.util.ArrayList;

import pojos.DBColumn;
import configurations.Wrapper;

public class KEGGQueries {

	// KEGG

	public static final String getKEGGpathwayByName = "SELECT pathway_name,title,org,number,image,link FROM kegg_pathway p where pathway_name = ?;";

	public static final String getPossibleKeggEntry = "SELECT e.entry_id,name.entry_name,e.type,e.link,e.reaction,e.map "
			+ "FROM db_kegg.entry e natural join db_kegg.entry_name "
			+ "name where name.entry_name like ? and e.type=? limit 1;";

	public static final String getKEGGentriesByPathwayName = "SELECT k.entry_id, k.link, k.type, n.ec, g.background, "
			+ "g.foreground, g.graphicsName, g.graphicsType, g.x, g.y FROM dawis_md.kegg_entry k left outer join dawis_md.kegg_entry_name n "
			+ "on k.entry_id=n.entry_ID Inner join dawis_md.kegg_graphics g on k.entry_id=g.entry_ID "
			+ "where k.pathway_name=? and n.pathway_name=? and g.pathway_name=? Order by k.entry_id;";

	public static final String getKEGGGRelations = "SELECT relation.pathway_name, subtype.subtype_name,subtype.subtype_value,"
			+ "relation.entry1,relation.entry2,relation.relation_type FROM "
			+ "kegg_subtype as subtype Inner join (SELECT * FROM kegg_relation where pathway_name=?) "
			+ "as relation on subtype.relation_id=relation.realtion_id;";

	// ------------------------------------------------------------------------------------------------

	// public static final String getAllInvolvedEnzymes
	// ="SELECT * FROM enzyme_pathway e Inner Join enzyme en on e.entry=en.entry where e.nummer=?;";
	public static final String getAllInvolvedEnzymes = "SELECT n.entry_id, n.entry_name, e.reaction, e.enzyme_comment, e.reference,e.sysname, "
			+ "c.class, k.cofactor, f.effector,p.product,s.substrate,o.orthology "
			+ "FROM kegg_entry_name as n natural join kegg_enzyme as e "
			+ "natural join kegg_enzyme_class as c "
			+ "natural join kegg_enzyme_cofactor as k "
			+ "natural join kegg_enzyme_effector as f "
			+ "natural join kegg_enzyme_product as p "
			+ "natural join kegg_enzyme_substrate as s "
			+ "natural join kegg_enzyme_orthology as o "
			+ "natural join kegg_enzyme_pathway as pa where pa.kegg_number=?;";

	// public static final String getAllEnzymNamesInPathway =
	// "SELECT e.entry,en.name FROM enzyme_pathway e Inner Join enzyme_name en on e.entry=en.entry where e.nummer=?;";
	public static final String getAllEnzymNamesInPathway = "SELECT k.entry,k.name FROM kegg_enzyme_name k natural join kegg_enzyme_pathway as p where p.kegg_number=?;";

	// public static final String getAllEnzymeDBLinks
	// ="SELECT e.entry,en.dbname, en.identifier FROM enzyme_pathway e Inner Join enzyme_dblinks en on e.entry=en.entry where e.nummer=?;";
	public static final String getAllEnzymeDBLinks = "SELECT k.entry,k.dbname,k.identifier FROM kegg_enzyme_dblinks k natural join kegg_enzyme_pathway as p where p.kegg_number=?;";

	// public static final String getAllEnzymeStructures =
	// "SELECT e.entry,en.structures FROM enzyme_pathway e Inner Join enzyme_structures en on e.entry=en.entry where e.nummer=?;";
	public static final String getAllEnzymeStructures = "SELECT k.entry, k.structures FROM kegg_enzyme_structures k natural join kegg_enzyme_pathway as p where p.kegg_number=?;";

	// public static final String getKEGGGroups
	// ="SELECT c.entry_id, c.component_id FROM component c where pathway_name=? order by component_id;";
	public static final String getKEGGGroups = "SELECT c.entry_id, c.component_id FROM kegg_component c where pathway_name=? order by component_id;";

	public static final String getSpecificKEGGGRelations = "SELECT r.entry1,r.entry2 FROM relation r where pathway_name =? and r.entry1=? and r.entry2=?;";

	public static final String getKEGGGEntryMaps = "SELECT Distinct entry_id FROM entry e where pathway_name=? and type=?;";

	// public static final String KEGGPathwayQuery
	// ="SELECT * FROM pathway p where ";

	public static final String KEGGPathwayQueryWithOrganism = "SELECT * FROM db_kegg.pathway p LEFT OUTER JOIN db_kegg.taxonomy as t on p.org=t.code where ";

	// public static final String KEGGPathwayQuery
	// ="SELECT p.pathway_name,p.title,p.org FROM db_kegg.pathway p where ";
	public static final String KEGGPathwayQuery = "SELECT p.pathway_name,p.title,p.org FROM kegg_pathway p where ";

	// public static final String
	// KEGGGeneQuery="SELECT Distinct Concat(p.organismus,p.nummer) as pathway_name FROM genes g inner join genes_pathway as p on g.entry=p.entry where ";
	public static final String KEGGGeneQuery = "SELECT Distinct Concat(p.org,p.kegg_number) as pathway_name FROM kegg_genes_pathway as p Inner join kegg_genes_name as n on p.entry=n.entry where ";

	// public static final String KEGGEnzymeQuery=
	// "SELECT Distinct CONCAT(e.organismus,e.nummer) as pathway_name FROM enzyme_pathway e Inner Join enzyme_name n on e.entry=n.entry where ";
	public static final String KEGGEnzymeQuery = "SELECT Distinct CONCAT('map',e.kegg_number) as pathway_name FROM kegg_enzyme_pathway e Inner Join kegg_enzyme_name n on e.entry=n.entry where ";

	public static final String KEGGQuery = "SELECT Distinct Concat(p.organismus,p.nummer) as pathway_name FROM genes g inner join genes_pathway as p on g.entry=p.entry where ";

	// public static final String
	// KEGGCompoundQuery="SELECT Distinct Concat(p.organismus,p.nummer) as pathway_name FROM compound_name c inner join pathway_cpd as p on c.entry=p.entry where ";
	public static final String KEGGCompoundQuery = "SELECT Distinct Concat(p.organismus,p.kegg_number) as pathway_name FROM kegg_compound_name c inner join kegg_pathway_compound as p on c.entry=p.entry where ";

	public static final String getEnzymeDetails = "SELECT * FROM enzyme e where entry=?;";

	public static final String getPathwayEnzymeDetails = "SELECT * FROM enzyme_pathway e Inner Join enzyme en on e.entry=en.entry where en.entry=?;";

	public static final String getEnzymeNames = "SELECT * FROM enzyme_name e where entry=?;";

	public static final String getEnzymeDbLinks = "SELECT * FROM enzyme_dblinks e where entry=?;";

	// public static final String getEnzymePathways =
	// "SELECT * FROM enzyme_pathway e where entry=?;";
	public static final String getEnzymePathways = "SELECT k.entry, k.kegg_number, k.organismus FROM kegg_enzyme_pathway k where k.entry =? ;";

	public static final String getEnzymeStructures = "SELECT * FROM enzyme_structures e where entry=?;";

	public static final String getCompoundDetails = "SELECT * FROM compound c where entry=?;";

	// public static final String getAllCompoundDetails =
	// "SELECT * FROM compound c where entry In";
	public static final String getAllCompoundDetails = "SELECT k.entry, k.formula, k.mass, k.c_comment, "
			+ "k.remark, k.atom_nr, k.atom, k.bond_nr, k.bond, k.c_sequence, k.module,k.organism "
			+ "FROM kegg_compound k where k.entry in";

	public static final String getCompoundNames = "SELECT * FROM compound_name c where entry=?;";

	// public static final String getAllCompoundNames =
	// "SELECT * FROM compound_name c where entry In";
	public static final String getAllCompoundNames = "SELECT k.entry,k.name FROM kegg_compound_name k where k.entry in";

	public static final String getCompoundDbLinks = "SELECT * FROM compound_dblinks c where entry=?;";

	// public static final String getAllCompoundDbLinks =
	// "SELECT * FROM compound_dblinks c where entry In";
	public static final String getAllCompoundDbLinks = "SELECT k.entry,k.dbname,k.identifier from "
			+ "kegg_compound_dblinks k where k.entry in";

	public static final String getGeneNames = "SELECT Distinct g.name FROM db_kegg.genes g where g.name like ?;";

	public static final String getGeneDetails = "SELECT * FROM genes g where entry=?;";

	public static final String getGeneEnzyms = "SELECT * FROM genes_enzyme g where entry=?;";

	public static final String getGeneDbLinks = "SELECT * FROM genes_dblinks g where entry=?;";

	public static final String getGeneMotifs = "SELECT * FROM genes_motif g where entry=?;";

	public static final String getGeneOrthology = "SELECT * FROM genes_orthology g where entry=?;";

	public static final String getGenePathways = "SELECT * FROM genes_pathway g where entry=?;";

	// public static final String getAllGeneDetails =
	// "SELECT * FROM genes g where entry In";
	public static final String getAllGeneDetails = "SELECT k.entry, k.name, g.gene_definition, "
			+ "g.position,g.codon_usage, s.aaseq_length, s.aaseq,s.ntseq_length,s.ntseq "
			+ "FROM kegg_genes_name k "
			+ "natural join kegg_genes g "
			+ "natural join kegg_genes_sequence s " + "where k.entry in";

	// public static final String getAllGeneEnzyms =
	// "SELECT * FROM genes_enzyme g where entry In";
	public static final String getAllGeneEnzyms = "SELECT k.entry, k.enzyme FROM dawis_new.kegg_genes_enzyme k where k.entry in";

	// public static final String getAllGeneDbLinks =
	// "SELECT * FROM genes_dblinks g where entry In";
	public static final String getAllGeneDbLinks = "SELECT k.entry, k.dbname, k.identifier FROM kegg_genes_dblinks k where k.entry in";

	// public static final String getAllGeneMotifs =
	// "SELECT * FROM genes_motif g where entry= In";
	public static final String getAllGeneMotifs = "SELECT k.entry, k.dbname, k.identifier FROM kegg_genes_motif k where k.entry in";

	// public static final String getAllGeneOrthology =
	// "SELECT * FROM genes_orthology g where entry In";
	public static final String getAllGeneOrthology = "SELECT k.entry,k.o_name,k.o_orthology FROM kegg_genes_orthology k where k.entry in";

	// public static final String getAllGenePathways =
	// "SELECT * FROM genes_pathway g where entry In";
	public static final String getAllGenePathways = "SELECT k.entry, k.kegg_number, k.org FROM kegg_genes_pathway k "
			+ "where k.entry in";

	public static final String getGlycanDetails = "SELECT * FROM glycan g where entry=?;";

	public static final String getGlycanEnzyms = "SELECT * FROM glycan_enzyme g where entry=?;";

	public static final String getGlycanDbLinks = "SELECT * FROM glycan_dblinks g where entry=?;";

	public static final String getGlycanPathways = "SELECT * FROM glycan_pathway g where entry=?;";

	// public static final String getAllGlycanDetails =
	// "SELECT * FROM glycan g where entry In";
	public static final String getAllGlycanDetails = "SELECT n.entry, n.name, k.mass, k.remark, "
			+ "o.orthology, k.reference, k.bracket, k.composition, k.node, k.edge "
			+ "FROM dawis_new.kegg_glycan k "
			+ "natural join kegg_glycan_name n "
			+ "natural join kegg_glycan_orthology o where n.entry in";

	// public static final String getAllGlycanEnzyms =
	// "SELECT * FROM glycan_enzyme g where entry In";
	public static final String getAllGlycanEnzyms = "SELECT k.entry,k.enzyme FROM kegg_glycan_enzyme k where k.entry In";

	// public static final String getAllGlycanDbLinks =
	// "SELECT * FROM glycan_dblinks g where entry In";
	public static final String getAllGlycanDbLinks = "SELECT k.entry, k.dbname, k.identifier FROM kegg_glycan_dblinks k where k.entry In";

	// public static final String getAllGlycanPathways =
	// "SELECT * FROM glycan_pathway g where entry In";
	public static final String getAllGlycanPathways = "SELECT k.entry,k.kegg_number,k.organismus FROM kegg_glycan_pathway k where k.entry In";

	public static final String getReactions = "SELECT * FROM reaction r where entry=?;";

	public static final String getSubstrate = "SELECT * FROM substrate s where pathway_name=? and reaction_name=?;";

	public static final String getProducts = "SELECT * FROM product p where pathway_name=? and reaction_name=?;";

	public static final String getReactionType = "SELECT * FROM reaction_kgml r where pathway_name=? and reaction_name=?;";

	public static final String getReactionEnzymes = "SELECT * FROM reaction_enzyme r where entry=?";

	public static final String getReactionsOutOfPathways = "SELECT Distinct entry FROM pathway_reaction p where p.organismus=? and p.nummer=?;";

	// public static final String getAllReactions =
	// "SELECT * FROM reaction r where entry In";
	public static final String getAllReactions = "SELECT k.entry, k.name, k.remark, o.orthology, "
			+ "k.reference, k.reaction_comment, k.reaction_definition, k.equation, k.rpair "
			+ "FROM kegg_reaction k inner join "
			+ "kegg_reaction_orthology o where k.entry In";

	// public static final String getAllSubstrate =
	// "SELECT * FROM substrate s where pathway_name=?;";
	public static final String getAllSubstrate = "SELECT * FROM substrate s where pathway_name=?;";

	public static final String getAllProducts = "SELECT * FROM product p where pathway_name=?;";

	public static final String getAllReactionType = "SELECT * FROM reaction_kgml r where pathway_name=?;";

	public static final String getAllReactionEnzymes = "SELECT * FROM reaction_enzyme r where entry In";

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

	public static ArrayList<DBColumn> requestDbContent(String pathway,
			String organismus, String gene, String compound, String enzyme) {
		String query = "Select distinct p.entry,p.name,p.organism "
				+ "from kegg_pathway p ";
				if (!gene.equals(""))query+= "left join (kegg_pathway_gene pg inner join kegg_genes g inner join kegg_genes_name gn) "
				+ "on p.entry=pg.entry AND pg.gene_id=g.gene_id AND g.id=gn.id ";
				if (!compound.equals(""))query+= "left join (kegg_pathway_compound pc inner join kegg_compound_name cn) "
				+ "on p.entry=pc.entry AND pc.compound=cn.entry ";
				if (!enzyme.equals("")) query+= "left join (kegg_kgml_pathway kp inner join kegg_enzyme_pathway ep inner join kegg_enzyme_name en) "
				+ "on p.entry=kp.name AND kp.number=ep.number AND ep.entry=en.entry ";
				query+= "where p.name like '%" + pathway + "%' AND p.organism like '%" + organismus + "%' ";
				if (!gene.equals(""))query+="AND gn.name ='" + gene + "' ";
				if (!compound.equals(""))query+="AND cn.name ='" + compound + "' ";
				if (!enzyme.equals("")) query+="AND en.entry='" + enzyme + "' ";
				query+="limit 0,1000;";
				//System.out.println(query);
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
		String query ="SELECT s.id,e.id,p.id,r.reaction_type, e.pathway_name "+
			"FROM kegg_kgml_reaction r "+
			"inner join kegg_kgml_substrate s on r.reaction_id=s.reaction_id "+
			"inner join kegg_kgml_product p on r.reaction_id=p.reaction_id "+
			"inner join kegg_kgml_entry_reaction er on er.reaction=r.name "+
			"inner join kegg_kgml_entry e on er.entry_id=e.entry_id "+
			"inner join kegg_kgml_entry_name en on e.entry_id=en.entry_id "+
			"where r.pathway_name='"+pathwayID+"' and e.pathway_name='"+pathwayID+"'; ";
			return new Wrapper().requestDbContent(2, query);
	}
	//query for gene list and number of pathways hit
	//select pw_name, count(pw_name) from (select distinct kegg_genes_name.name, kegg_genes_pathway.name as pw_name FROM dawismd.kegg_genes_name join dawismd.kegg_genes_pathway on kegg_genes_name.id = dawismd.kegg_genes_pathway.id where kegg_genes_name.name = "cdk2" or kegg_genes_name.name = "ANAPC2") as T group by pw_name;
	//select pw_name, count(pw_name) from (select distinct kegg_genes_name.name, kegg_genes_pathway.name as pw_name FROM dawismd.kegg_genes_name join dawismd.kegg_genes_pathway on kegg_genes_name.id = dawismd.kegg_genes_pathway.id where kegg_genes_name.name in ("cdk2", "ANAPC2")) as T group by pw_name;

}