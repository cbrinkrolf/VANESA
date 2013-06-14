package hqllayer;

public class HQLKEGGQueries {
	
	public static final String getHQLKEGGpathwayByName = 		
		"SELECT name,title,org,number,image,link FROM kegg_kgml_pathway where name = ?";

	public static final String getHQLPossibleKeggEntry = 
		"SELECT e.entryId,n.id.name,e.entryType,e.link,r.id.reaction " +
		"FROM kegg_kgml_entry e LEFT JOIN e.keggKgmlEntryNames n LEFT JOIN e.keggKgmlEntryReactions r " +
		"WHERE n.id.name like ? AND e.entryType=?";
/*		"SELECT e.entry_id,name.entry_name,e.type,e.link,e.reaction,e.map "
			+ "FROM db_kegg.entry e natural join db_kegg.entry_name "
			+ "name where name.entry_name like ? and e.type=? limit 1;";
*/
	public static final String getHQLKEGGentriesByPathwayName = 
		"SELECT e.entryId,e.link,e.entryType,n.id.name,g.bgcolor,g.fgcolor,g.name,g.graphicsType,g.x,g.y "
		+ "FROM kegg_kgml_pathway p LEFT JOIN p.keggKgmlEntries e LEFT JOIN e.keggKgmlEntryNames n LEFT JOIN e.keggKgmlGraphicses g "
		+ "WHERE p.id.name=? Order by e.entryId";	
/*		"SELECT k.entry_id, k.link, k.type, n.entry_name, g.background, "
			+ "g.foreground, g.graphicsName, g.graphicsType, g.x, g.y FROM dawis_md.kegg_entry k left outer join dawis_md.kegg_entry_name n "
			+ "on k.entry_id=n.entry_ID Inner join dawis_md.kegg_graphics g on k.entry_id=g.entry_ID "
			+ "where k.pathway_name=? and n.pathway_name=? and g.pathway_name=? Order by k.entry_id;";
*/
	public static final String getHQLKEGGGRelations = 
		"SELECT s.keggKgmlRelation.keggKgmlPathway.name,s.id.name,s.id.subtypeValue,s.keggKgmlRelation.entry1,s.keggKgmlRelation.entry2,"
			+ "s.keggKgmlRelation.relationType "
			+ "FROM kegg_kgml_subtype s where s.keggKgmlRelation.keggKgmlPathway.name=? ";

	// ------------------------------------------------------------------------------------------------

	// public static final String getHQLAllInvolvedEnzymes
	// ="SELECT * FROM enzyme_pathway e Inner Join enzyme en on e.entry=en.entry where e.nummer=?;";
	public static final String getHQLAllInvolvedEnzymes = 
		"SELECT e.entry,r.id.reaction,e.comment,ref.id.refNumber,e.sysname,"
			+ "c.id.enzymeClass,cf.id.cofactor,f.id.effector,p.id.product,s.id.substrate,o.id.orthology "			
			+ "FROM kegg_enzyme e "			
			+ "JOIN e.keggEnzymeReactions r "
			+ "JOIN e.keggEnzymeReferences ref "
			+ "JOIN e.keggEnzymeClasses c "
			+ "JOIN e.keggEnzymeCofactors cf "
			+ "JOIN e.keggEnzymeEffectors f "
			+ "JOIN e.keggEnzymeProducts p " 
			+ "JOIN e.keggEnzymeSubstrates s "
			+ "JOIN e.keggEnzymeOrthologies o "
			+ "JOIN e.keggEnzymePathwaies pw "
			+ "WHERE pw.id.number =?";
	
	// public static final String getHQLAllEnzymNamesInPathway =
	// "SELECT e.entry,en.name FROM enzyme_pathway e Inner Join enzyme_name en on e.entry=en.entry where e.nummer=?;";
	public static final String getHQLAllEnzymNamesInPathway = 
		"SELECT e.entry,e.sysname FROM kegg_enzyme e join e.keggEnzymePathwaies as p where p.id.number=?";

	// public static final String getHQLAllEnzymeDBLinks
	// ="SELECT e.entry,en.dbname, en.identifier FROM enzyme_pathway e Inner Join enzyme_dblinks en on e.entry=en.entry where e.nummer=?;";
	public static final String getHQLAllEnzymeDBLinks = 
		"SELECT k.id.entry,k.id.dbname,k.id.identifier FROM kegg_enzyme_dblinks k JOIN k.keggEnzyme.keggEnzymePathwaies p where p.id.number=?";

	// public static final String getHQLAllEnzymeStructures =
	// "SELECT e.entry,en.structures FROM enzyme_pathway e Inner Join enzyme_structures en on e.entry=en.entry where e.nummer=?;";
	public static final String getHQLAllEnzymeStructures = 
		"SELECT k.entry, k.structures FROM kegg_enzyme_structures k natural join kegg_enzyme_pathway as p where p.id.number=?;";
	//TODO: Enzyme structures nur in dawis_md vorhanden! Query rausnehmen?
	
	// public static final String getHQLKEGGGroups
	// ="SELECT c.entry_id, c.component_id FROM component c where pathway_name=? order by component_id;";
	public static final String getHQLKEGGGroups = 
		"SELECT c.id.entryId, c.id.id FROM kegg_kgml_component c where c.keggKgmlEntry.keggKgmlPathway.name=? order by c.id.id";

	public static final String getHQLSpecificKEGGGRelations = 
		"SELECT r.entry1,r.entry2 FROM kegg_kgml_relation r where r.keggKgmlPathway.name =? and r.entry1=? and r.entry2=?";

	public static final String getHQLKEGGGEntryMaps = 
		"SELECT DISTINCT e.entryId FROM kegg_kgml_entry e where e.keggKgmlPathway.name=? and e.entryType=?";

	//"SELECT * FROM db_kegg.pathway p LEFT OUTER JOIN db_kegg.taxonomy as t on p.org=t.code where ";
	public static final String HQLKEGGPathwayQueryWithOrganism = 
		"FROM kegg_kgml_pathway p,kegg_taxonomy t " +
		"WHERE p.org=t.org AND ";

	// public static final String KEGGPathwayQuery
	// ="SELECT p.pathway_name,p.title,p.org FROM db_kegg.pathway p where ";
	public static final String HQLKEGGPathwayQuery = 
		"SELECT p.name,p.title,p.org FROM kegg_kgml_pathway p where ";

	// public static final String
	// KEGGGeneQuery="SELECT Distinct Concat(p.organismus,p.nummer) as pathway_name FROM genes g inner join genes_pathway as p on g.entry=p.entry where ";
	public static final String HQLKEGGGeneQuery = 
		"SELECT DISTINCT concat(p.id.org,p.id.number) " +
		"FROM kegg_genes_pathway p " +
		"WHERE ";
	//Join nicht mehr notwendig, da p.keggGenes als Attribut existiert
	
	// public static final String KEGGEnzymeQuery=
	// "SELECT Distinct CONCAT(e.organismus,e.nummer) as pathway_name FROM enzyme_pathway e Inner Join enzyme_name n on e.entry=n.entry where ";
	public static final String HQLKEGGEnzymeQuery = 
		"SELECT Distinct e.name FROM kegg_enzyme_pathway e where ";

	public static final String HQLKEGGQuery = 
		"SELECT p.name FROM kegg_genes g INNER JOIN g.keggGenesPathwaies p where ";

	// public static final String
	// KEGGCompoundQuery="SELECT Distinct Concat(p.organismus,p.nummer) as pathway_name FROM compound_name c inner join pathway_cpd as p on c.entry=p.entry where ";
	public static final String HQLKEGGCompoundQuery = 
		"SELECT DISTINCT p.name FROM kegg_compound_pathway p where ";
		//"SELECT Distinct Concat(p.organismus,p.kegg_number) as pathway_name FROM kegg_compound_name c inner join kegg_pathway_compound as p on c.entry=p.entry where ";

	public static final String getHQLEnzymeDetails = 
		"FROM kegg_enzyme where entry=?";

	public static final String getHQLPathwayEnzymeDetails = 
		"FROM kegg_enzyme_pathway p WHERE p.keggEnzyme.entry=?";

	public static final String getHQLEnzymeNames = 
		"FROM kegg_enzyme_name e where e.keggEnzyme.entry=?";

	public static final String getHQLEnzymeDbLinks = 
		"FROM kegg_enzyme_dblinks e where entry=?";

	// public static final String getHQLEnzymePathways =
	// "SELECT * FROM enzyme_pathway e where entry=?;";
	public static final String getHQLEnzymePathways = 
		"SELECT p.id.entry,p.id.number,p.id.org FROM kegg_enzyme_pathway p where p.id.entry =? ";

	public static final String getHQLEnzymeStructures = 
		"SELECT * FROM enzyme_structures e where entry=?;";
	//TODO: Enzyme structures?
	
	public static final String getHQLCompoundDetails = 
		"FROM kegg_compound c where c.entry=?";

	// public static final String getHQLAllCompoundDetails =
	// "SELECT * FROM compound c where entry In";
	public static final String getHQLAllCompoundDetails = 
		"SELECT k.entry,k.formula,k.mass,k.comment,k.remark,k.atom,k.bond,k.sequence,k.module,k.organism "
			+ "FROM kegg_compound k where k.entry in ";

	public static final String getHQLCompoundNames = 
		"FROM kegg_compound_name c where c.id.entry=? ";

	// public static final String getHQLAllCompoundNames =
	// "SELECT * FROM compound_name c where entry In";
	public static final String getHQLAllCompoundNames = 
		"SELECT k.id.entry,k.id.name FROM kegg_compound_name k WHERE k.id.entry IN ";

	public static final String getHQLCompoundDbLinks = 
		"FROM kegg_compound_dblinks c where c.id.entry=? ";

	// public static final String getHQLAllCompoundDbLinks =
	// "SELECT * FROM compound_dblinks c where entry In";
	public static final String getHQLAllCompoundDbLinks = 
		"SELECT k.id.entry,k.id.dbname,k.id.identifier FROM kegg_compound_dblinks k where k.id.entry in ";

	public static final String getHQLGeneNames = 
		"SELECT Distinct gs.name FROM kegg_genes_name gs where gs.name like ?";

	public static final String getHQLGeneDetails = 
		"FROM kegg_genes g where g.id=?";

	public static final String getHQLGeneEnzyms = 
		"FROM kegg_enzyme_genes g where g.id.entry=?";

	public static final String getHQLGeneDbLinks = 
		"FROM kegg_genes_dblinks g where g.id.identifier=?";

	public static final String getHQLGeneMotifs = 
		"FROM kegg_genes_motif g where g.id.identifier=?";

	public static final String getHQLGeneOrthology = 
		"FROM kegg_genes_orthology g where g.id.orthology=?";

	public static final String getHQLGenePathways = 
		"FROM kegg_genes_pathway g where g.id.number=?";

	// public static final String getHQLAllGeneDetails =
	// "SELECT * FROM genes g where entry In";
	public static final String getHQLAllGeneDetails = 
		"SELECT g.geneId, gn.name, g.definition,g.position,aas.id.aaseqLength,aas.id.aaseq,ns.id.ntseqLength,ns.id.ntseq "
			+ "FROM kegg_genes g "
			+ "LEFT JOIN g.keggGenesNames gn "
			+ "LEFT JOIN g.keggGenesAaseqs aas "
			+ "LEFT JOIN g.keggGenesNtseqs ns "
			+ "WHERE g.geneId in ";
	//kegg_genes_name.entry == Keggkegg_genes.geneId angenommen

	// public static final String getHQLAllGeneEnzyms =
	// "SELECT * FROM genes_enzyme g where entry In";
	public static final String getHQLAllGeneEnzyms = 
		"SELECT k.id.geneId,k.keggEnzyme.entry " +
		"FROM kegg_enzyme_genes k " +
		"WHERE k.id.geneId IN ";
	
	// public static final String getHQLAllGeneDbLinks =
	// "SELECT * FROM genes_dblinks g where entry In";
	public static final String getHQLAllGeneDbLinks = 
		"SELECT k.keggGenes.geneId,k.id.dbname,k.id.identifier FROM kegg_genes_dblinks k where k.keggGenes.geneId in";

	// public static final String getHQLAllGeneMotifs =
	// "SELECT * FROM genes_motif g where entry= In";
	public static final String getHQLAllGeneMotifs = 
		"SELECT k.keggGenes.geneId,k.id.dbname,k.id.identifier FROM kegg_genes_motif k where k.keggGenes.geneId in";

	// public static final String getHQLAllGeneOrthology =
	// "SELECT * FROM genes_orthology g where entry In";
	public static final String getHQLAllGeneOrthology = 
		"SELECT k.keggGenes.geneId,k.id.orthology FROM kegg_genes_orthology k where k.keggGenes.geneId in";

	// public static final String getHQLAllGenePathways =
	// "SELECT * FROM genes_pathway g where entry In";
	public static final String getHQLAllGenePathways = 
		"SELECT k.keggGenes.geneId, k.id.number, k.id.org FROM kegg_genes_pathway k where k.keggGenes.geneId in";

	public static final String getHQLGlycanDetails = 
		"FROM kegg_glycan g where g.entry=?";

	public static final String getHQLGlycanEnzyms = 
		"FROM kegg_glycan_enzyme g where g.id.entry=?";

	public static final String getHQLGlycanDbLinks = 
		"FROM kegg_glycan_dblinks g where g.id.entry=?";

	public static final String getHQLGlycanPathways = 
		"FROM kegg_glycan_pathway g where g.id.entry=?";

	// public static final String getHQLAllGlycanDetails =
	// "SELECT * FROM glycan g where entry In";
	public static final String getHQLAllGlycanDetails = 
		"SELECT k.entry,n.id.name,k.mass,k.remark,o.id.orthology,k.reference,k.composition,k.node,k.edge "
			+ "FROM kegg_glycan k "
			+ "LEFT JOIN k.keggGlycanNames n "
			+ "LEFT JOIN k.keggGlycanOrthologies o where k.entry in";

	// public static final String getHQLAllGlycanEnzyms =
	// "SELECT * FROM glycan_enzyme g where entry In";
	public static final String getHQLAllGlycanEnzyms = 
		"SELECT k.id.entry,k.id.enzyme FROM kegg_glycan_enzyme k where k.id.entry in";

	// public static final String getHQLAllGlycanDbLinks =
	// "SELECT * FROM glycan_dblinks g where entry In";
	public static final String getHQLAllGlycanDbLinks = 
		"SELECT k.id.entry,k.id.dbname,k.id.identifier FROM kegg_glycan_dblinks k where k.id.entry in";

	// public static final String getHQLAllGlycanPathways =
	// "SELECT * FROM glycan_pathway g where entry In";
	public static final String getHQLAllGlycanPathways = 
		"SELECT k.id.entry,k.id.number,k.id.org FROM kegg_glycan_pathway k where k.id.entry in";

	public static final String getHQLReactions = 
		"FROM kegg_reaction r where r.entry=?";

	public static final String getHQLSubstrate = 
		"FROM kegg_kgml_substrate s " +
		"WHERE s.keggKgmlReaction.keggKgmlPathway.name=? " +
		"AND s.keggKgmlReaction.name=?";

	public static final String getHQLProducts = 
		"FROM kegg_kgml_product p where p.keggKgmlReaction.keggKgmlPathway.name=? and p.keggKgmlReaction.name=?";

	public static final String getHQLReactionType = 
		"FROM kegg_kgml_reaction r where r.keggKgmlPathway.name=? and r.name=?";

	public static final String getHQLReactionEnzymes = 
		"FROM kegg_enzyme_reaction r where r.id.entry=?";

	public static final String getHQLReactionsOutOfPathways = 
		"SELECT Distinct p.id.entry FROM kegg_enzyme_pathway p where p.id.org=? and p.id.number=?";

	// public static final String getHQLAllReactions =
	// "SELECT * FROM reaction r where entry In";
	public static final String getHQLAllReactions = 
		"SELECT k.entry,k.remark,o.id.orthology,k.comment, k.definition, k.equation, rp.id.rpair "
			+ "FROM kegg_reaction k "
			+ "INNER JOIN k.keggReactionOrthologies o "
			+ "JOIN k.keggReactionRpairs rp where k.entry in";

	// public static final String getHQLAllSubstrate =
	// "SELECT * FROM substrate s where pathway_name=?;";
	public static final String getHQLAllSubstrate = 
		"FROM kegg_kgml_substrate s where s.keggKgmlReaction.keggKgmlPathway.name=?";

	public static final String getHQLAllProducts = 
		"FROM kegg_kgml_product p where p.keggKgmlReaction.keggKgmlPathway.name=?";

	public static final String getHQLAllReactionType = 
		"FROM kegg_kgml_reaction r where r.keggKgmlPathway.name=?";

	public static final String getHQLAllReactionEnzymes = 
		"FROM kegg_reaction_enzyme r where r.id.entry in";

}
