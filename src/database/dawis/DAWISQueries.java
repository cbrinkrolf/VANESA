package database.dawis;

public class DAWISQueries {

	public static final String getRemoteControlSessionID = "SELECT distinct rc_session "
			+ "FROM dawis_md.remote_control " + "where rc_session = ?";
	
	/*
	 * get pathway information
	 */
	public static final String getKEGGPathwayStartQuery = "SELECT distinct p.pathway_name, p.title, t.name "
			+ "FROM dawis_md.kegg_pathway p "
			+ "INNER JOIN dawis_md.kegg_taxonomy t on p.org = t.org "
			+ "where ";

	public static final String getKEGGPathwayName = "SELECT distinct p.title "
			+ "FROM dawis_md.kegg_pathway p " + "where p.pathway_name = ?";

	public static final String getKEGGPathwayOrganismusIndependentStartQuery = "SELECT distinct p.number, p.title "
			+ "FROM dawis_md.kegg_pathway p " + "where ";

	public static final String getPathwayFromPathway = "SELECT distinct ken.entry_name, kp.title, kp.org "
			+ "FROM dawis_md.kegg_entry ke "
			+ "inner join dawis_md.kegg_entry_name ken on ken.entry_id = ke.entry_id and ke.pathway_name = ken.pathway_name "
			+ "left outer join dawis_md.kegg_pathway kp on kp.pathway_name = ken.entry_name "
			+ "where ke.type = 'map' and ke.pathway_name = ?";

	public static final String getTranspathPathwayStartQuery = "SELECT distinct tpp.pathway_id, tpp.pathway_name "
			+ "FROM dawis_md.tp_pathway tpp " + "where ";

	public static final String getTPPathwayFromTPPathway = "SELECT distinct tpl.pathway_id, tpp.pathway_name "
			+ "FROM dawis_md.tp_pathway_level tpl "
			+ "INNER JOIN dawis_md.tp_pathway tpp on tpp.pathway_id = tpl.pathway_id "
			+ "where tpl.super_id = ?";

	public static final String getPathwayFromPathway2 = "SELECT distinct ken.entry_name "
			+ "FROM dawis_md.kegg_entry ke "
			+ "inner join dawis_md.kegg_entry_name ken on ken.entry_id = ke.entry_id and ke.pathway_name = ken.pathway_name "
			+ "left outer join dawis_md.kegg_pathway kp on kp.pathway_name = ken.entry_name "
			+ "where ke.type = 'map' and ke.pathway_name = ";

	public static final String getTPPathwayFromTPPathway2 = "SELECT distinct tpp.super_id, tpp.super_orientated "
			+ "FROM dawis_md.tp_pathway_level tpp " + "where tpp.pathway_id = ";

	public static final String getTPPathwayComments = "SELECT distinct comment_id "
			+ "FROM dawis_md.tp_pathway_comments " + "where pathway_id = ?";

	public static final String getTPPathwayReference = "SELECT r.pubmed_id "
			+ "FROM dawis_md.tp_reference r "
			+ "INNER JOIN dawis_md.tp_pathway2reference pr on pr.reference_id = r.reference_id "
			+ "where pr.pathway_id = ?";

	public static final String getTPPathwayFromCompound = "SELECT tppm.pathway_id "
			+ "FROM dawis_md.tp_pathway_molecules_involved tppm "
			+ "where tppm.molecule_id = ?";

	public static final String getTPPathwayFromReaction = "SELECT tppr.pathway_id "
			+ "FROM dawis_md.tp_pathway_reactions_involved tppr "
			+ "where tppr.reaction_id = ?";

	public static final String getPathwayCountFromGene = "SELECT count(entry) "
			+ "FROM dawis_md.kegg_genes_pathway kgp "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.number = kgp.kegg_number AND kp.org = kgp.org "
			+ "where kgp.kegg_number = ?";

	public static final String getPathwayFromGene = "SELECT distinct kgp.kegg_number, kgp.name, kgp.org "
			+ "FROM dawis_md.kegg_genes_pathway kgp " + "where kgp.entry = ?";

	public static final String getPathwayFromGeneOrganismIndependent = "SELECT distinct kgp.kegg_number, kgp.name "
			+ "FROM dawis_md.kegg_genes_pathway kgp " + "where kgp.entry = ?";

	public static final String getPathwayFromEnzyme = "SELECT distinct kp.pathway_name, kp.title, kp.org "
			+ "FROM dawis_md.kegg_enzyme_pathway kep "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kep.kegg_number = kp.number AND kep.organismus = kp.org "
			+ "where kep.entry = ?";

	public static final String getPathwayFromEnzyme2 = "SELECT kp.pathway_name "
			+ "FROM dawis_md.kegg_enzyme_pathway kep "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kep.kegg_number = kp.number AND kep.organismus = kp.org "
			+ "where kep.entry = ";

	public static final String getPathwayNumberAndNameFromEnzyme = "SELECT kep.kegg_number "
			+ "FROM dawis_md.kegg_enzyme_pathway kep " + "where kep.entry = ?";

	public static final String getPathwayFromCompound = "SELECT distinct kpc.kegg_number, kp.title "
			+ "FROM dawis_md.kegg_pathway_compound kpc "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.number = kpc.kegg_number and kp.org = kpc.organismus "
			+ "where kpc.entry = ?";

	public static final String getPathwayFromCompound2 = "SELECT distinct kpc.kegg_number, kp.title "
			+ "FROM dawis_md.kegg_pathway_compound kpc "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.number = kpc.kegg_number and kp.org = kpc.organismus "
			+ "where kpc.entry = ";

	public static final String getPathwayFromTRANSPATHCompound = "SELECT distinct tpmi.pathway_id, tpp.pathway_name "
			+ "FROM dawis_md.tp_pathway_molecules_involved tpmi "
			+ "INNER JOIN dawis_md.tp_pathway tpp on tpp.pathway_id = tpmi.pathway_id "
			+ "where tpmi.molecule_id = ?";

	public static final String getTRANSPATHPathwayMoleculesInvolved = "SELECT distinct tpmi.molecule_id, tpmi.pathway_molecule "
			+ "FROM dawis_md.tp_pathway_molecules_involved tpmi "
			+ "where tpmi.pathway_id = ?";

	public static final String getPathwayFromCompoundOrganismSpecific = "SELECT kp.pathway_name, kp.title, kp.org "
			+ "FROM dawis_md.kegg_pathway_compound kpc "
			+ "inner join dawis_md.kegg_pathway kp on kp.org = kpc.organismus and kp.number = kpc.kegg_number "
			+ "where ";

	public static final String getPathwayFromReaction = "SELECT distinct kpr.kegg_number, kp.title "
			+ "FROM dawis_md.kegg_pathway_reaction kpr "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.number = kpr.kegg_number "
			+ "where kpr.entry = ?";

	public static final String getPathwayFromReactionOrganismSpecific = "SELECT kp.pathway_name, kp.title, kpr.organismus "
			+ "FROM dawis_md.kegg_pathway_reaction kpr "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.number = kpr.kegg_number and kp.org = kpr.organismus "
			+ "where  ";

	public static final String getPathwayFromGlycan = "SELECT distinct kpg.kegg_number, kp.title "
			+ "FROM dawis_md.kegg_pathway_glycan kpg "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.org = kpg.organismus and kp.number = kpg.kegg_number "
			+ "where kpg.entry = ?";

	public static final String getPathwayFromGlycanOrganismSpecific = "SELECT kp.pathway_name, kp.title, kp.org "
			+ "FROM dawis_md.kegg_pathway_glycan kpg "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.org = kpg.organismus and kp.number = kpg.kegg_number "
			+ "where ";

	public static final String getPathwayFromDrug = "SELECT kpd.kegg_number, kp.title, kpd.organismus "
			+ "FROM dawis_md.kegg_pathway_drug kpd "
			+ "LEFT OUTER JOIN dawis_md.kegg_pathway kp on kp.number = kpd.kegg_number "
			+ "where kpd.entry = ?";

	public static final String getPathwayFromDrug2 = "SELECT kpd.kegg_number, kp.title, kpd.organismus "
			+ "FROM dawis_md.kegg_pathway_drug kpd "
			+ "LEFT OUTER JOIN dawis_md.kegg_pathway kp on kp.number = kpd.kegg_number "
			+ "where kpd.entry = ";

	public static final String getPathwayNumber = "SELECT kp.number "
			+ "FROM dawis_md.kegg_pathway kp " + "where kp.pathway_name = ?";

	public static final String getPathwayOrganism = "SELECT kp.org "
			+ "FROM dawis_md.kegg_pathway kp " + "where kp.pathway_name = ?";

	public static final String getPathwayOrganism2 = "SELECT t.name "
			+ "FROM dawis_md.kegg_pathway kp "
			+ "INNER JOIN dawis_md.kegg_taxonomy t on t.org = kp.org "
			+ "where kp.pathway_name = ?";

	public static final String getPathwayMap = "SELECT kp.image "
			+ "FROM dawis_md.kegg_pathway kp " + "where kp.pathway_name = ?";

	public static final String getPathwayMapByNumber = "SELECT kp.image "
			+ "FROM dawis_md.kegg_pathway kp " + "where kp.number = ?";

	public static final String getTPPathway = "SELECT distinct t.pathway_id "
			+ "FROM tp_pathway t "
			+ "INNER JOIN kegg_pathway kp on kp.title like t.pathway_name "
			+ "where t.pathway_type like 'pathway' and t.pathway_name like ";

	public static final String getTPPathwayName = "SELECT t.pathway_name "
			+ "FROM tp_pathway t "
			+ "INNER JOIN kegg_pathway kp on t.pathway_name like kp.title "
			+ "where t.pathway_type like 'pathway' and t.pathway_id = ? ";

	public static final String getKEGGPathwayFromTPPathway = "SELECT distinct kp.number "
			+ "FROM tp_pathway t "
			+ "INNER JOIN kegg_pathway kp on t.pathway_name like kp.title "
			+ "where t.pathway_type like 'pathway' and t.pathway_id = ";

	public static final String getTPPathwayFromKEGGPathway = "SELECT distinct t.pathway_id "
			+ "FROM tp_pathway t "
			+ "INNER JOIN kegg_pathway kp on t.pathway_name like kp.title "
			+ "where t.pathway_type like 'pathway' and kp.number = ";

	public static final String getKEGGPathwayFromTPPathway2 = "SELECT distinct kp.number "
			+ "FROM tp_pathway t "
			+ "INNER JOIN kegg_pathway kp on t.pathway_name like kp.title "
			+ "where t.pathway_type like 'pathway' and t.pathway_id = ? ";

	public static final String getTPPathwayFromKEGGPathway2 = "SELECT distinct t.pathway_id "
			+ "FROM tp_pathway t "
			+ "INNER JOIN kegg_pathway kp on t.pathway_name like kp.title "
			+ "where t.pathway_type like 'pathway' and kp.number = ? ";

	public static final String getTranspathPathwayType = "SELECT tpp.pathway_type "
			+ "FROM dawis_md.tp_pathway tpp " + "where tpp.pathway_id = ?";

	public static final String getTranspathPathwayComment = "SELECT tpc.comment_id "
			+ "FROM dawis_md.tp_pathway_comments tpc "
			+ "where tpc.pathway_id = ?";

	public static final String getOrganismForPathway = "SELECT distinct p.org "
			+ "FROM dawis_md.kegg_pathway p " + "where ";

	/*
	 * get disease information
	 */
	public static final String getOMIMDiseaseStartQuery = "SELECT distinct d.MIM, d.title "
			+ "FROM dawis_md.omim_disease d "
			+ "INNER JOIN dawis_md.omim_osynonym os on os.MIM = d.MIM "
			+ "where ";

	public static final String getOMIMDiseaseName = "SELECT distinct d.title "
			+ "FROM dawis_md.omim_disease d " + "where d.MIM = ?";

	public static final String getDiseaseDiagnosisType = "SELECT dt.name "
			+ "FROM dawis_md.omim_diagnosis_type dt "
			+ "INNER JOIN dawis_md.omim_disease d on dt.id = d.type "
			+ "where d.MIM = ?";

	public static final String getDiseaseFromGene = "SELECT d.MIM, d.title "
			+ "FROM dawis_md.omim_disease d "
			+ "INNER JOIN dawis_md.kegg_genes_dblinks gdbl on gdbl.identifier = d.MIM "
			+ "where gdbl.dbname = 'OMIM' AND gdbl.entry = ?";

	public static final String getDiseaseFromGene2 = "SELECT d.MIM "
			+ "FROM dawis_md.omim_disease d "
			+ "INNER JOIN dawis_md.kegg_genes_dblinks gdbl on gdbl.identifier = d.MIM "
			+ "where gdbl.dbname = 'OMIM' AND gdbl.entry = ";

	public static final String getDiseaseFromEMBLGene = "SELECT ogm.mim, ogm.title "
			+ "FROM omim_gene_map ogm "
			+ "INNER JOIN omim_gene_symbol ogs on ogs.cm = ogm.cm "
			+ "INNER JOIN embl_features ef on ef.f_value = ogs.symbol "
			+ "where ef.f_key = 'gene' and ef.f_attribute = 'gene' and ef.primary_ac = ?";

	public static final String getDiseaseFromTRANSPATHGene = "SELECT tpedl.database_identifier, od.title "
			+ "FROM dawis_md.tp_external_database_links tpedl "
			+ "INNER JOIN dawis_md.tp_gene2db_link t on t.database_id = tpedl.accession_number "
			+ "INNER JOIN dawis_md.omim_disease od on od.MIM = tpedl.database_identifier "
			+ "where tpedl.database_name like '%MIM' and t.gene_id = ?";

	public static final String getDiseaseFromTRANSPATHGene2 = "SELECT tpedl.database_identifier "
			+ "FROM dawis_md.tp_external_database_links tpedl "
			+ "INNER JOIN dawis_md.tp_gene2db_link t on t.database_id = tpedl.accession_number "
			+ "where tpedl.database_name like '%MIM' and t.gene_id = ";

	public static final String getDiseaseFromTRANSFACGene2 = "SELECT tpedl.database_identifier "
			+ "FROM dawis_md.tp_external_database_links tpedl "
			+ "INNER JOIN dawis_md.tp_gene2db_link t on t.database_id = tpedl.accession_number "
			+ "where tpedl.database_name like '%MIM' and t.gene_id = ";

	public static final String getDiseaseFromHPRDProtein = "SELECT hm.omim_id, od.title "
			+ "FROM dawis_md.hprd_hprd_id_mapping hm "
			+ "INNER JOIN dawis_md.omim_disease od on od.MIM = hm.omim_id "
			+ "where hm.hprd_id = ?";

	public static final String getDiseaseFromTPProtein = "SELECT t.database_identifier "
			+ "FROM tp_external_database_links t "
			+ "inner join tp_molecule2db_link tml on tml.database_id = t.accession_number"
			+ "where database_name like '%mim' and tml.molecule_id = ?";

	// for further search delete ' ' from ud.primary_id
	public static final String getDiseaseIDFromProtein = "SELECT ud.primary_id "
			+ "FROM dawis_md.uniprot_dbxref ud " + "where ";

	public static final String getDiseaseFromProtein = "SELECT d.MIM, d.title "
			+ "FROM dawis_md.omim_disease d " + "where d.MIM = ?";

	public static final String getDiseaseFromCompound = "SELECT tpedl.database_identifier "
			+ "FROM dawis_md.tp_molecule2db_link tpml "
			+ "INNER JOIN dawis_md.tp_external_database_links tpedl on tpedl.accession_number = tpml.database_id "
			+ "where tpedl.database_name = 'MIM' and tpml.molecule_id = ?";

	public static final String getDiseaseFromCompound2 = "SELECT tpedl.database_identifier "
			+ "FROM dawis_md.tp_molecule2db_link tpml "
			+ "INNER JOIN dawis_md.tp_external_database_links tpedl on tpedl.accession_number = tpml.database_id "
			+ "where tpedl.database_name = 'MIM' and tpml.molecule_id = ";

	public static final String getDiseaseSymbolsAndLocations = "SELECT gs.symbol, gm.location "
			+ "From dawis_md.omim_gene_map gm "
			+ "Inner Join dawis_md.omim_gene_symbol gs on gm.cm = gs.cm "
			+ "where gm.MIM=?";

	public static final String getDiseaseLocations = "SELECT gm.location "
			+ "From dawis_md.omim_gene_map gm " + "where gm.MIM=?";

	public static final String getDiseaseReference = "SELECT entry "
			+ "From dawis_md.omim_reference " + "where MIM=?";

	public static final String getDiseaseSynonyms = "SELECT o.osynonym "
			+ "FROM dawis_md.omim_osynonym o " + "where o.MIM=?";

	public static final String getDiseaseDetails = "SELECT dt.id, gd.disorder "
			+ "FROM dawis_md.omim_diagnosis_type dt "
			+ "Inner Join dawis_md.omim_disease d on d.type = dt.id "
			+ "Inner Join dawis_md.omim_gene_map gm on d.mim = gm.MIM "
			+ "Inner Join dawis_md.omim_gene_disorders gd on gd.cm = gm.cm "
			+ "where d.MIM=?";

	public static final String getDiseaseDisorder = "SELECT disorder "
			+ "FROM dawis_md.omim_gene_disorders " + "where MIM=?";

	public static final String getDiseaseFeatures = "SELECT cs.odomain, cs.feature "
			+ "FROM dawis_md.omim_clinical_synopsis cs " + "where cs.MIM=?";

	/*
	 * get gene information
	 */

	public static final String getGeneStartQuery = "SELECT distinct g.entry, g.name, t.org "
			+ "FROM dawis_md.kegg_genes_name g "
			+ "INNER JOIN dawis_md.kegg_taxonomy t on g.org = t.latin_name "
			+ "where ";

	public static final String getGeneStartQueryOrganismIndependent = "SELECT distinct g.entry, g.name "
			+ "FROM dawis_md.kegg_genes_name g " + "where ";

	public static final String getEMBLGeneOrganismIndependentStartQuery = "SELECT *"
			+ "FROM dawis_md.embl_description ed " + "where  ";

	public static final String getEMBLGeneStartQuery = "SELECT distinct ed.primary_ac, description, eo.species "
			+ "FROM dawis_md.embl_description ed "
			+ "INNER JOIN dawis_md.embl_organismdata eo on eo.primary_ac = ed.primary_ac "
			+ "where ";

	public static final String getTransfacGeneOrganismIndependentStartQuery = "SELECT distinct tfg.gene_id, tfg.short_gene_term "
			+ "FROM dawis_md.tf_gene tfg "
			+ "INNER JOIN dawis_md.tf_gene_synonyms tfgs on tfgs.gene_id = tfg.gene_id "
			+ "where ";

	public static final String getTransfacGeneStartQuery = "SELECT distinct tfg.gene_id, tfg.short_gene_term, tfo.latin_name "
			+ "FROM dawis_md.tf_gene tfg "
			+ "INNER JOIN dawis_md.tf_organism tfo on tfo.organism_id = tfg.organism_Id "
			+ "INNER JOIN dawis_md.tf_gene_synonyms tfgs on tfgs.gene_id = tfg.gene_id "
			+ "where ";

	public static final String getTranspathGeneStartQuery = "SELECT distinct tpg.gene_id, tpg.gene_name, tpo.latin_name "
			+ "FROM dawis_md.tp_gene tpg "
			+ "INNER JOIN dawis_md.tp_organism tpo on tpo.organism_id = tpg.organism_Id "
			+ "LEFT OUTER JOIN dawis_md.tp_gene_synonyms tpgs on tpgs.gene_id = tpg.gene_id "
			+ "where ";

	public static final String getTranspathGeneOrganismIndependentStartQuery = "SELECT distinct tpg.gene_id, tpg.gene_name "
			+ "FROM dawis_md.tp_gene tpg "
			+ "INNER JOIN dawis_md.tp_gene_synonyms tpgs on tpgs.gene_id = tpg.gene_id "
			+ "where ";

	public static final String getKEGGGeneFromTranspathGene = "SELECT tpl.database_identifier "
			+ "FROM dawis_md.tp_external_database_links tpl "
			+ "INNER JOIN dawis_md.tp_gene2db_link tpgl on tpgl.database_id = tpl.accession_number "
			+ "where tpl.database_name = 'ENTREZGENE' and tpgl.gene_id = ?";

	public static final String getKEGGGeneFromTransfacGene = "SELECT tpl.database_identifier "
			+ "FROM dawis_md.tp_external_database_links tpl "
			+ "INNER JOIN dawis_md.tp_gene2db_link tpgl on tpgl.database_id = tpl.accession_number "
			+ "where tpl.database_name = 'ENTREZGENE' and tpgl.gene_id = ?";

	public static final String getGeneFromDisease = "SELECT g.entry, gs.symbol "
			+ "From dawis_md.omim_gene_map gm "
			+ "Inner Join dawis_md.omim_gene_symbol gs on gm.cm = gs.cm "
			+ "Inner Join dawis_md.kegg_genes_dblinks g on g.identifier = gm.MIM AND g.dbname = \"OMIM\" "
			+ "where gm.MIM =?";

	public static final String getTRANSPATHGeneFromDisease = "SELECT tpl.gene_id, tg.gene_name "
			+ "FROM tp_external_database_links t "
			+ "INNER JOIN tp_gene2db_link tpl on tpl.database_id = t.accession_number "
			+ "INNER JOIN tp_gene tg on tg.gene_id = tpl.gene_id "
			+ "where t.database_name like '%MIM' and t.database_identifier = ?";

	public static final String getTPGeneFromDisease = "SELECT tl.gene_id "
			+ "FROM tp_external_database_links t "
			+ "inner join tp_gene2db_link tl on t.accession_number = tl.database_id "
			+ "where database_name like '%mim' and database_identifier = ?";

	public static final String getTRANSFACGeneFromDisease = "SELECT tfl.gene_id, tg.short_gene_term "
			+ "FROM tf_external_database_links t "
			+ "INNER JOIN tf_gene2db_links tfl on tfl.database_id = t.accession_number "
			+ "INNER JOIN tf_gene tg on tg.gene_id = tfl.gene_id "
			+ "where t.database_name like '%MIM' and tfl.database_id = ?";

	public static final String getTPGeneFromTPProtein = "SELECT tpm.encoding_gene_id "
			+ "FROM dawis_md.tp_molecule tpm " + "where tpm.molecule_id = ?";

	public static final String getTFGeneFromTFProtein = "SELECT tff.encoding_Gene_Id "
			+ "FROM dawis_md.tf_factor tff " + "where tff.factor_id = ?";

	public static final String getTPGeneFromEnzyme = "SELECT tpgl.gene_id "
			+ "FROM dawis_md.kegg_enzyme ke "
			+ "INNER JOIN dawis_md.kegg_genes_enzyme ge on ke.entry = ge.enzyme "
			+ "INNER JOIN dawis_md.tp_external_database_links tpl on tpl.database_identifier = ge.entry "
			+ "INNER JOIN dawis_md.tp_gene2db_link tpgl on tpgl.database_id = tpl.accession_number "
			+ "where tpl.database_name = 'ENTREZGENE' and ge.enzyme = ?";

	public static final String getTPGeneFromEnzyme2 = "SELECT tpgl.gene_id "
			+ "FROM dawis_md.kegg_enzyme ke "
			+ "INNER JOIN dawis_md.kegg_genes_enzyme ge on ke.entry = ge.enzyme "
			+ "INNER JOIN dawis_md.tp_external_database_links tpl on tpl.database_identifier = ge.entry "
			+ "INNER JOIN dawis_md.tp_gene2db_link tpgl on tpgl.database_id = tpl.accession_number "
			+ "where tpl.database_name = 'ENTREZGENE' and ge.enzyme = ";

	public static final String getTPGeneFromTPCompound = "SELECT distinct tpgd.gene_id "
			+ "FROM dawis_md.tp_gene_product tpgd "
			+ "LEFT OUTER JOIN dawis_md.tp_molecule tpm on tpm.molecule_id = tpgd.product_id "
			+ "where tpgd.product_id = ?";

	public static final String getTPGeneFromTPCompound2 = "SELECT distinct tpgd.gene_id "
			+ "FROM dawis_md.tp_gene_product tpgd "
			+ "LEFT OUTER JOIN dawis_md.tp_molecule tpm on tpm.molecule_id = tpgd.product_id "
			+ "where tpgd.product_id = ";

	public static final String getTFGeneFromEnzyme = "SELECT t.gene_id "
			+ "FROM tf_gene2db_links t "
			+ "inner join tf_external_database_links tfl on tfl.accession_number = t.database_id "
			+ "where database_name = 'BRENDA' and database_id = ?";

	public static final String getTFGeneFromEnzyme2 = "SELECT t.gene_id "
			+ "FROM tf_gene2db_links t "
			+ "inner join tf_external_database_links tfl on tfl.accession_number = t.database_id "
			+ "where database_name = 'BRENDA' and database_id = ";

	public static final String getEMBLGeneType = "SELECT ei.molecular_type "
			+ "FROM dawis_md.embl_identification ei "
			+ "where ei.primary_ac = ?";

	public static final String getEMBLGeneDescription = "SELECT ed.description "
			+ "FROM dawis_md.embl_description ed " + "where ed.primary_ac = ?";

	public static final String getKEGGOrganismSynonyms = "SELECT t.org, t.latin_name, t.name "
			+ "FROM dawis_md.kegg_taxonomy t " + "where ";

	public static final String getGeneFromUniProtProtein = "SELECT distinct g.entry "
			+ "FROM dawis_md.kegg_genes g "
			+ "INNER JOIN dawis_md.kegg_genes_dblinks l on g.entry = l.entry AND g.org = l.org AND l.dbname = \"UniProt\" "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers a on a.accession_number = l.identifier "
			+ "where a.uniprot_id =?";

	public static final String getGeneFromUniProtProtein2 = "SELECT distinct g.entry "
			+ "FROM dawis_md.kegg_genes g "
			+ "INNER JOIN dawis_md.kegg_genes_dblinks l on g.entry = l.entry AND g.org = l.org AND l.dbname = \"UniProt\" "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers a on a.accession_number = l.identifier "
			+ "where (a.accession_number = ";

	public static final String getEMBLGeneFromUniProtProtein2 = "SELECT distinct u.primary_id "
			+ "FROM dawis_md.uniprot_dbxref u "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers ua on ua.uniprot_id = u.uniprot_id "
			+ "where u.db_name like 'EMBL' and (ua.accession_number = ";

	public static final String getGeneFromUniProtProteinOrganismSpecific = "SELECT distinct g.entry, g.org "
			+ "FROM dawis_md.kegg_genes g "
			+ "INNER JOIN dawis_md.kegg_genes_dblinks l on g.entry = l.entry AND g.org = l.org AND l.dbname = \"UniProt\" "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers a on a.accession_number = l.identifier "
			+ "where a.uniprot_id =?";

	public static final String getEMBLGeneFromUniProtProtein = "SELECT distinct ud.primary_id "
			+ "FROM dawis_md.uniprot_dbxref ud "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers ua on ua.uniprot_id = ud.uniprot_id "
			+ "where ud.db_name like 'EMBL' and ua.accession_number = ?";

	public static final String getEMBLGeneFromFactor = "SELECT tl.accession_number "
			+ "FROM dawis_md.tf_factor2db_links t "
			+ "inner join dawis_md.tf_external_database_links tl on tl.accession_number = t.database_id "
			+ "where tl.database_name like 'embl' and factor_id = ";

	public static final String getGeneFromHPRDProtein = "SELECT hm.entrezgene_id "
			+ "FROM dawis_md.hprd_hprd_id_mapping hm " + "where hm.hprd_id = ?";

	public static final String getGeneFromHPRDProtein2 = "SELECT hm.entrezgene_id "
			+ "FROM dawis_md.hprd_hprd_id_mapping hm " + "where hm.hprd_id = ";

	public static final String getTransfacGeneFromFactor = "SELECT tff.gene_id "
			+ "FROM dawis_md.tf_gene_encoded_factor tff "
			+ "where tff.factor_id = ?";

	public static final String getGeneFromEnzyme = "SELECT g.entry "
			+ "FROM dawis_md.kegg_genes_enzyme g " + "where g.enzyme = ?";

	public static final String getGeneFromEnzyme2 = "SELECT g.entry "
			+ "FROM dawis_md.kegg_genes_enzyme g " + "where g.enzyme = ";

	public static final String getGeneFromTRANSPATHCompound = "SELECT tpgp.gene_id, tpgp.gene_name "
			+ "FROM dawis_md.tp_gene_product tpgp "
			+ "where tpgp.product_id = ?";

	public static final String getTranspathGeneFromReaction = "SELECT molecule_id "
			+ "FROM (SELECT * FROM dawis_md.tp_reaction_molecule_up "
			+ "UNION SELECT * FROM dawis_md.tp_reaction_molecule_down) tabelle "
			+ "where reaction_id = ";

	public static final String getGeneFromEnzymeOrganismSpecific = "SELECT g.entry, g.org "
			+ "FROM dawis_md.kegg_genes_enzyme g " + "where g.enzyme = ?";

	public static final String getGeneFromPathway = "SELECT kgp.entry, kgp.org "
			+ "FROM dawis_md.kegg_genes_pathway kgp "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.number = kgp.kegg_number AND kp.org = kgp.org "
			+ "where kp.pathway_name = ?";

	public static final String getGeneFromPathway2 = "SELECT kgp.entry "
			+ "FROM dawis_md.kegg_genes_pathway kgp "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.number = kgp.kegg_number AND kp.org = kgp.org "
			+ "where kp.pathway_name = ";

	public static final String getGeneFromPathwayOrganismIndependent = "SELECT distinct kgp.entry "
			+ "FROM dawis_md.kegg_genes_pathway kgp "
			+ "where kgp.kegg_number = ?";

	public static final String getGeneName = "SELECT kgn.name "
			+ "FROM dawis_md.kegg_genes_name kgn " + "where kgn.entry = ?";

	public static final String getTPGeneSynonyms = "SELECT tps.gene_synonym "
			+ "FROM dawis_md.tp_gene_synonyms tps " + "where tps.gene_id = ?";

	public static final String getTFGeneSynonyms = "SELECT tfs.gene_synonym "
			+ "FROM dawis_md.tf_gene_synonyms tfs " + "where tfs.gene_id = ?";

	public static final String getEMBLGeneSynonyms = "SELECT ek.keywords "
			+ "FROM dawis_md.embl_keywords ek " + "where ek.primary_ac = ?";

	public static final String getTPGeneName = "SELECT gene_name "
			+ "FROM dawis_md.tp_gene " + "where gene_id = ?";

	public static final String getTFGeneName = "SELECT short_gene_term "
			+ "FROM dawis_md.tf_gene " + "where gene_id = ?";

	public static final String getEMBLGeneName = "SELECT description "
			+ "FROM dawis_md.embl_description " + "where primary_ac = ?";

	public static final String getGeneDetails = "SELECT * "
			+ "FROM dawis_md.kegg_genes kg " + "where kg.entry = ?";

	public static final String getKEGGGeneFromTFGene = "SELECT t.database_id "
			+ "FROM tf_gene2db_links t "
			+ "INNER JOIN tf_external_database_links tl on tl.accession_number = t.database_id "
			+ "where tl.database_name like 'ENTREZGENE' and t.gene_id = ?";

	public static final String getKEGGGeneFromTPGene = "SELECT database_identifier "
			+ "FROM tp_external_database_links t "
			+ "INNER JOIN tp_gene2db_link tl on tl.database_id = t.accession_number "
			+ "where database_name = 'ENTREZGENE' and tl.gene_id = ?";

	public static final String getTFGeneFromEMBLGene = "SELECT t.gene_id "
			+ "FROM tf_gene2db_links t "
			+ "INNER JOIN tf_external_database_links tl on tl.accession_number = t.database_id "
			+ "where tl.database_name like 'EMBL' and t.database_id = ?";

	public static final String getEMBLGeneFromTFGene = "SELECT t.database_id "
			+ "FROM tf_gene2db_links t "
			+ "INNER JOIN tf_external_database_links tl on tl.accession_number = t.database_id "
			+ "where tl.database_name like 'EMBL' and t.gene_id = ?";

	public static final String getTFGeneFromKEGGGene = "SELECT t.gene_id "
			+ "FROM tf_gene2db_links t "
			+ "INNER JOIN tf_external_database_links tl on tl.accession_number = t.database_id "
			+ "where tl.database_name like 'ENTREZGENE' and t.database_id = ?";

	public static final String getTPGeneFromKEGGGene = "SELECT tl.gene_id "
			+ "FROM tp_external_database_links t "
			+ "INNER JOIN tp_gene2db_link tl on tl.database_id = t.accession_number "
			+ "where database_name = 'ENTREZGENE' and database_identifier = ?";

	public static final String getGeneSequenceDetails = "SELECT * "
			+ "FROM dawis_md.kegg_genes_sequence kgs " + "where kgs.entry = ?";

	public static final String getEMBLGeneSequenceData = "SELECT * "
			+ "FROM dawis_md.embl_sequencedata es " + "where es.primary_ac = ?";

	public static final String getGeneOrthologyDetails = "SELECT kgo.org, kgo.o_orthology, kgo.o_name "
			+ "FROM dawis_md.kegg_genes_orthology kgo " + "where kgo.entry = ?";

	public static final String getMotif = "SELECT * "
			+ "FROM dawis_md.kegg_genes_motif kgm " + "where kgm.entry = ?";

	public static final String getEMBLGeneOrganismData = "SELECT * "
			+ "FROM dawis_md.embl_organismdata eo " + "where eo.primary_ac = ?";

	public static final String getEMBLGeneSequenceLength = "SELECT ei.sequence_length "
			+ "FROM dawis_md.embl_identification ei "
			+ "where ei.primary_ac = ?";

	public static final String getTransfacGeneDetails = "SELECT * "
			+ "FROM dawis_md.tf_gene tfg " + "where tfg.gene_id = ?";

	public static final String getTransfacGeneBindingRegion = "SELECT distinct * "
			+ "FROM dawis_md.tf_gene_binding_region ftgbr "
			+ "where tfgbr.gene_id = ?";

	public static final String getTFGeneSite = "SELECT distinct s.site_id "
			+ "FROM dawis_md.tf_site s " + "where s.gene_id = ";

	public static final String getTFGeneSiteAndFactor = "SELECT distinct t.binding_site_id, t.factor_id, tf.factor_Name "
			+ "FROM dawis_md.tf_factor_binding_sites t "
			+ "INNER JOIN dawis_md.tf_factor tf on tf.factor_id = t.factor_id "
			+ "where t.gene_id = ?";

	public static final String getTFGeneFactor = "SELECT t.factor_id, tf.factor_Name "
			+ "FROM dawis_md.tf_factor_binding_sites t "
			+ "INNER JOIN dawis_md.tf_factor tf on tf.factor_id = t.factor_id "
			+ "where t.gene_id = ";

	public static final String getTFGeneFragment = "SELECT distinct t.chip_id "
			+ "FROM dawis_md.tf_gene_binding_region t " + "where t.gene_id = ";

	public static final String getTFGeneFactor2 = "SELECT distinct t.factor_id, tf.factor_Name "
			+ "FROM dawis_md.tf_factor_binding_sites t "
			+ "INNER JOIN dawis_md.tf_factor tf on tf.factor_id = t.factor_id "
			+ "where t.gene_id = ?";

	public static final String getTFGeneFragment2 = "SELECT distinct t.chip_id "
			+ "FROM dawis_md.tf_gene_binding_region t " + "where t.gene_id = ?";

	public static final String getFactorDetails = "SELECT * "
			+ "FROM dawis_md.tf_factor tf  " + "where tf.factor_id = ?";

	public static final String getSiteDetails = "SELECT * "
			+ "FROM dawis_md.tf_site tf  " + "where tf.site_id = ?";

	public static final String getTransfacGeneEncodingFactor = "SELECT tfgef.factor_name, tfgef.additional_information "
			+ "FROM dawis_md.tf_gene_encoded_factor ftgef "
			+ "where tfgef.gene_id = ?";

	public static final String getTransfacGeneRegulation = "SELECT tfgr.reg_condition "
			+ "FROM dawis_md.tf_gene_regulation ftgr "
			+ "where tfgr.gene_id = ?";

	public static final String getTransfacGeneSynonyms = "SELECT tfgs.gene_synonym "
			+ "FROM dawis_md.tf_gene_synonyms ftgs " + "where tfgs.gene_id = ?";

	public static final String getTFGeneFromTPGene = "SELECT database_identifier "
			+ "FROM tp_external_database_links t "
			+ "INNER JOIN tp_gene2db_link tl on tl.database_id = t.accession_number "
			+ "where database_name = 'TRANSFAC' and tl.gene_id = ?";

	public static final String getOrganismForGene = "SELECT kg.org "
			+ "FROM dawis_md.kegg_genes kg " + "where kg.entry = ?";

	public static final String getOrganismForEMBLGene = "SELECT eo.species "
			+ "FROM dawis_md.embl_organismdata eo " + "where eo.primary_ac = ?";

	public static final String getOrganismForTransfacGene = "SELECT tfo.latin_name "
			+ "FROM dawis_md.tf_organism tfo "
			+ "INNER JOIN dawis_md.tf_gene tfg on tfg.organism_id = tfo.organism_id "
			+ "where tfg.gene_id = ?";

	public static final String getOrganismForTranspathGene = "SELECT tpo.latin_name "
			+ "FROM dawis_md.tp_organism tpo "
			+ "INNER JOIN dawis_md.tp_gene tpg on tpg.organism_Id = tpo.organism_id "
			+ "where tpg.gene_id = ?";

	public static final String getExonsForGene = "SELECT distinct ef.primary_ac "
			+ "FROM dawis_md.embl_features ef "
			+ "where ef.f_key = 'exon' and ";

	/*
	 * get protein information
	 */

	public static final String getUniProtProteinStartQuery = "SELECT p.uniprot_id, p.description, p.species "
			+ "FROM dawis_md.uniprot p " + "where ";

	public static final String getUniProtProteinStartQueryOrganismIndependent = "SELECT distinct p.uniprot_id, p.description "
			+ "FROM dawis_md.uniprot p " + "where ";

	public static final String getUniProtProteinNameByAccessionNumber = "SELECT distinct p.uniprot_id, p.description "
			+ "FROM dawis_md.uniprot p "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers ua on ua.accession_number = p.uniprot_id "
			+ "where ua.accession_number = ";

	public static final String getHPRDProteinStartQuery = "SELECT distinct hps.HPRD_ID, hps.protein_name "
			+ "FROM dawis_md.hprd_protein_sequences hps " + "where ";

	public static final String getTransfacProteinStartQuery = "SELECT distinct tff.factor_id, tff.factor_name, tfo.latin_name "
			+ "FROM dawis_md.tf_factor tff "
			+ "INNER JOIN dawis_md.tf_organism tfo on tfo.organism_id = tff.organism_Id "
			+ "where ";

	public static final String getTransfacProteinStartQueryOrganismIndependent = "SELECT distinct tff.factor_id, tff.factor_name "
			+ "FROM dawis_md.tf_factor tff " + "where ";

	public static final String getTFProteinFromTPProtein = "SELECT t.database_identifier "
			+ "FROM tp_external_database_links t "
			+ "inner join tp_molecule2db_link ml on ml.database_id = t.accession_number "
			+ "where t.database_name like 'transfac' and ml.molecule_id = ?";

	public static final String getTPProteinFromTFProtein = "SELECT ml.molecule_id "
			+ "FROM tp_external_database_links t "
			+ "inner join tp_molecule2db_link ml on ml.database_id = t.accession_number "
			+ "where t.database_name like 'transfac' and t.database_identifier = ?";
	//
	public static final String getUniprotProteinFromTransfacProtein = "SELECT u.uniprot_id "
			+ "FROM dawis_md.uniprot_dbxref u "
			+ "where u.db_name like 'TRANSFAC' and u.primary_id = ";

	public static final String getUniprotProteinFromTransfacProtein2 = "SELECT u.uniprot_id "
			+ "FROM dawis_md.uniprot_dbxref u "
			+ "where u.db_name like 'TRANSFAC' and u.primary_id = ? ";

	public static final String getTransfacProteinFromUniprotProtein2 = "SELECT u.primary_id "
			+ "FROM dawis_md.uniprot_dbxref u "
			+ "where u.db_name like 'TRANSFAC' and u.uniprot_id = ? ";

	public static final String getUniprotProteinFromHPRDProtein = "SELECT u.uniprot_id "
			+ "FROM dawis_md.uniprot_accessionnumbers u "
			+ "INNER JOIN dawis_md.hprd_hprd_id_mapping hhm on hhm.swissprot_id = u.accession_number "
			+ "where hhm.HPRD_ID = ?";

	public static final String getHPRDProteinFromUniprotProtein = "SELECT hhm.HPRD_ID "
			+ "FROM dawis_md.uniprot_accessionnumbers u "
			+ "INNER JOIN dawis_md.hprd_hprd_id_mapping hhm on hhm.swissprot_id = u.accession_number "
			+ "where u.uniprot_id = ";

	public static final String getHPRDProteinInteractor = "SELECT h.interactor_2_hprd_id, h.interactor_2_geneSymbol "
			+ "FROM dawis_md.hprd_protein_protein h "
			+ "where h.interactor_1_hprd_id = ?";

	public static final String getTFProteinInteractor = "SELECT t.factor_id, tff.factor_Name, t.subunit_complex_id, t.complex_name "
			+ "FROM dawis_md.tf_factor_complexes t "
			+ "INNER JOIN dawis_md.tf_factor tff on tff.factor_id = t.factor_id "
			+ "where t.subunit_complex_id = ";

	public static final String getProteinFromGene = "SELECT u.uniprot_id, u.description "
			+ "FROM dawis_md.kegg_genes_dblinks l "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers a on a.accession_number = l.identifier "
			+ "INNER JOIN  dawis_md.uniprot u ON u.uniprot_id = a.uniprot_id "
			+ "where l.entry = ?";

	// ud.primary_id is a gene id; write a ' ' befor it
	public static final String getProteinFromEMBLGene = "SELECT u.uniprot_id, u.description "
			+ "FROM dawis_md.uniprot u "
			+ "INNER JOIN  dawis_md.uniprot_dbxref ud ON u.uniprot_id = ud.uniprot_id "
			+ "where ud.db_name like 'EMBL' and ";

	public static final String getProteinFromTPCompound = "SELECT ua.uniprot_id "
			+ "FROM tp_molecule2db_link t "
			+ "inner join tp_external_database_links tpl on tpl.accession_number = t.database_id "
			+ "inner join uniprot_accessionnumbers ua on ua.accession_number = tpl.database_identifier "
			+ "where database_name like 'swissprot' and t.molecule_id = ?";

	public static final String getProteinFromTPCompound2 = "SELECT ua.uniprot_id "
			+ "FROM tp_molecule2db_link t "
			+ "inner join tp_external_database_links tpl on tpl.accession_number = t.database_id "
			+ "inner join uniprot_accessionnumbers ua on ua.accession_number = tpl.database_identifier "
			+ "where database_name like 'swissprot' and t.molecule_id = ";

	public static final String getProteinFromTransfacGene = "SELECT tff.factor_id, tff.factor_name "
			+ "FROM dawis_md.tf_gene_encoded_factor tff "
			+ "where tff.gene_id = ? ";

	public static final String getProteinFromTransfacGene2 = "SELECT tff.factor_id "
			+ "FROM dawis_md.tf_gene_encoded_factor tff "
			+ "where tff.gene_id = ";

	public static final String getProteinFromTranspathGene = "SELECT tff.factor_id, tff.factor_name "
			+ "FROM dawis_md.tf_gene_encoded_factor tff "
			+ "INNER JOIN dawis_md.tp_external_database_links tpl on tff.gene_id = tpl.database_identifier "
			+ "INNER JOIN dawis_md.tp_gene2db_link tpgl on tpgl.database_id = tpl.accession_number "
			+ "where tpl.database_name like 'TRANSFAC' and tff.gene_id = ? ";

	public static final String getProteinFromTranspathGene2 = "SELECT tff.factor_id "
			+ "FROM dawis_md.tf_gene_encoded_factor tff "
			+ "INNER JOIN dawis_md.tp_external_database_links tpl on tff.gene_id = tpl.database_identifier "
			+ "INNER JOIN dawis_md.tp_gene2db_link tpgl on tpgl.database_id = tpl.accession_number "
			+ "where tpl.database_name like 'TRANSFAC' and tff.gene_id = ";

	public static final String getHPRDProtein = "SELECT hhim.HPRD_ID "
			+ "FROM dawis_md.hprd_hprd_id_mapping hhim "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers ud on ud.accession_number = hhim.swissprot_id "
			+ "where ud.uniprot_id = ? ";

	public static final String getHPRDProteinIsoformen = "SELECT count(isoform_id) "
			+ "FROM dawis_md.hprd_sequence_information " + "where hprd_id = ? ";

	public static final String getHPRDProteinFromGene = "SELECT hhim.HPRD_ID, hhim.main_name "
			+ "FROM dawis_md.hprd_hprd_id_mapping hhim "
			+ "where hhim.entrezgene_id = ? ";

	public static final String getHPRDProteinFromGene2 = "SELECT hhim.HPRD_ID "
			+ "FROM dawis_md.hprd_hprd_id_mapping hhim "
			+ "where hhim.entrezgene_id = ";

	public static final String getHPRDProteinExpression = "SELECT expression_term "
			+ "FROM dawis_md.hprd_tissue_expressions " + "where hprd_id = ? ";

	public static final String getHPRDProteinProzess = "SELECT name "
			+ "FROM dawis_md.hprd_gene_ontology_2_bio_process_term "
			+ "where hprd_id = ? ";

	public static final String getHPRDProteinCellComponent = "SELECT name "
			+ "FROM dawis_md.hprd_gene_ontology_2_cell_component_term "
			+ "where hprd_id = ? ";

	public static final String getHPRDProteinMolecularFunktion = "SELECT name "
			+ "FROM dawis_md.hprd_gene_ontology_2_mol_funktion_term "
			+ "where hprd_id = ? ";

	// write ' ' in front of ud.primary_id
	public static final String getProteinFromDisease = "SELECT ud.uniprot_id, u.description "
			+ "FROM dawis_md.uniprot_dbxref ud "
			+ "INNER JOIN dawis_md.uniprot u on u.uniprot_id = ud.uniprot_id "
			+ "where db_name = \"MIM\" and ";

	public static final String getHPRDProteinFromDisease = "SELECT hhim.HPRD_ID, hhim.main_name "
			+ "FROM dawis_md.hprd_hprd_id_mapping hhim "
			+ "where hhim.omim_id = ?";

	public static final String getHPRDProteinFromDisease2 = "SELECT hhim.HPRD_ID "
			+ "FROM dawis_md.hprd_hprd_id_mapping hhim "
			+ "where hhim.omim_id = ";

	// write ' ' in front of ud.primary_id
	public static final String getProteinFromGO = "SELECT ud.uniprot_id, u.description "
			+ "FROM dawis_md.uniprot_dbxref ud "
			+ "INNER JOIN dawis_md.uniprot u on u.uniprot_id = ud.uniprot_id "
			+ "where ud.primary_id = ?";

	public static final String getHPRDProteinFromGO = "Select hprd_id "
			+ "FROM (SELECT * FROM hprd_gene_ontology_2_mol_funktion_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_cell_component_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_bio_process_term) tabelle "
			+ "where go_number = ?";

	public static final String getProteinFromEnzyme = "SELECT ua.uniprot_id "
			+ "FROM dawis_md.enzyme_enzyme2uniprot eeu "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers ua on ua.accession_number = eeu.uniprot_id "
			+ "where eeu.enzyme_id  = ?";

	public static final String getProteinFromEnzyme2 = "SELECT ua.uniprot_id "
			+ "FROM dawis_md.enzyme_enzyme2uniprot eeu "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers ua on ua.accession_number = eeu.uniprot_id "
			+ "where eeu.enzyme_id = ";

	public static final String getProteinDetails = "SELECT * "
			+ "FROM uniprot u " + "where u.uniprot_id = ?";

	public static final String getProteinDetailsByAccessionNumber = "SELECT * "
			+ "FROM uniprot u "
			+ "INNER JOIN uniprot_accessionnumbers uan on uan.uniprot_id = u.uniprot_id "
			+ "where uan.accession_number = ?";

	public static final String getProteinAccessionnumber = "SELECT * "
			+ "FROM uniprot_accessionnumbers u " + "where uniprot_id = ?";

	public static final String getUniProtProteinID = "SELECT * "
			+ "FROM uniprot_accessionnumbers u " + "where accession_number = ?";

	public static final String getProteinGeneName = "SELECT * "
			+ "FROM uniprot_genenames u " + "where u.uniprot_id = ?";

	public static final String getProteinGeneNameByAccessionNumber = "SELECT * "
			+ "FROM uniprot_genenames u "
			+ "INNER JOIN uniprot_accessionnumbers uan on uan.uniprot_id = u.uniprot_id "
			+ "where uan.accession_number = ?";

	public static final String getProteinGeneSynonyms = "SELECT * "
			+ "FROM uniprot_genesynonyms u " + "where uniprot_id = ?";

	public static final String getProteinGeneSynonymsByAccessionNumber = "SELECT * "
			+ "FROM uniprot_genesynonyms u "
			+ "INNER JOIN uniprot_accessionnumbers uan on uan.uniprot_id = u.uniprot_id "
			+ "where uan.accession_number = ?";

	public static final String getProteinFromFactor = "SELECT tl.accession_number "
			+ "FROM dawis_md.tf_factor2db_links t "
			+ "inner join dawis_md.tf_external_database_links tl on tl.accession_number = t.database_id "
			+ "where tl.database_name like 'swissprot' and factor_id = ";

	public static final String getProteinPDBs = "SELECT ud.primary_id "
			+ "FROM dawis_md.uniprot_dbxref ud "
			+ "where ud.db_name = 'PDB' and uniprot_id = ?";

	public static final String getProteinPDBsByAccessionNumber = "SELECT ud.primary_id "
			+ "FROM dawis_md.uniprot_dbxref ud "
			+ "INNER JOIN uniprot_accessionnumbers uan on uan.uniprot_id = ud.uniprot_id "
			+ "where ud.db_name = 'PDB' and uan.accession_number = ?";

	public static final String getTransfacProteinDetails = "SELECT * "
			+ "FROM dawis_md.tf_factor tff " + "where tff.factor_id = ?";

	public static final String getTFProteinSynonyms = "SELECT tf.factor_synonym "
			+ "FROM dawis_md.tf_factor_synonyms tf " + "where tf.factor_id = ?";

	public static final String getTPProteinComplexName = "SELECT tpmc.complex_name "
			+ "FROM dawis_md.tp_molecule_complex tpmc "
			+ "where tpmc.molecule_id = ?";

	public static final String getTPProteinComment = "SELECT tpmc.comment_id "
			+ "FROM dawis_md.tp_molecule_comments tpmc "
			+ "where tpmc.molecule_id = ?";

	public static final String getTPProteinSuperfamily = "SELECT tpms.molecule_superfamily_id, tpms.molecule_superfamily "
			+ "FROM dawis_md.tp_molecule_superfamalies tpms "
			+ "where tpms.molecule_id = ?";

	public static final String getTPProteinSubfamily = "SELECT tpms.molecule_subfamily_id, tpms.molecule_subfamily "
			+ "FROM dawis_md.tp_molecule_subfamilies tpms "
			+ "where tpms.molecule_id = ?";

	public static final String getTFProteinFeatures = "SELECT tf.feature "
			+ "FROM dawis_md.tf_factor_features tf " + "where tf.factor_id = ?";

	public static final String getFragmentsOfFactor = "SELECT tfbf.fragment_id "
			+ "FROM dawis_md.tf_fragment_binding_factor tfbf "
			+ "where tfbf.factor_id = ";

	public static final String getFragmentsOfFactor2 = "SELECT tfbf.fragment_id "
			+ "FROM dawis_md.tf_fragment_binding_factor tfbf "
			+ "where tfbf.factor_id = ?";

	public static final String getMatrixOfSite = "SELECT t.matrix_id "
			+ "FROM dawis_md.tf_site_matrices t " + "where t.site_id = ";

	public static final String getMatrixOfFactor = "SELECT t.matrix_id "
			+ "FROM dawis_md.tf_matrix_binding_factor t "
			+ "where t.factor_id = ";

	public static final String getSiteOfFactor = "SELECT t.site_id "
			+ "FROM dawis_md.tf_site_binding_factor t "
			+ "where t.factor_id = ";

	public static final String getSiteOfFactor2 = "SELECT distinct t.site_id "
			+ "FROM dawis_md.tf_site_binding_factor t "
			+ "where t.factor_id = ?";

	public static final String getOrganismForProtein = "SELECT u.species "
			+ "FROM dawis_md.uniprot u " + "where u.uniprot_id = ?";

	/*
	 * get enzyme information
	 */

	public static final String getEnzymeStartQuery = "SELECT distinct ke.entry, ke.sysname, bo.org_name  "
			+ "FROM kegg_enzyme ke "
			+ "INNER JOIN dawis_md.kegg_enzyme_name ken on ken.entry = ke.entry "
			+ "inner join dawis_md.brenda_synonyms bs on bs.enzyme = ke.entry "
			+ "INNER JOIN dawis_md.brenda_enzyme2organism eo on eo.ec_number = ke.entry "
			+ "INNER JOIN dawis_md.brenda_organism bo on bo.org_id = eo.org_id "
			+ "where";

	public static final String getEnzymeStartQueryOrganismIndependent = "SELECT distinct ke.entry, ke.sysname "
			+ "FROM dawis_md.kegg_enzyme ke "
			+ "INNER JOIN dawis_md.kegg_enzyme_name ken on ken.entry = ke.entry "
			+ "INNER JOIN dawis_md.brenda_synonyms bs on bs.enzyme = ke.entry "
			+ "where ";

	public static final String getTPEnzymeFromKEGGEnzyme = "SELECT tpm.molecule_id "
			+ "FROM tp_external_database_links t "
			+ "INNER JOIN tp_molecule2db_link tpm on tpm.database_id = t.accession_number "
			+ "where database_name like 'brenda' and t.database_identifier = ?";

	public static final String getKEGGEnzymeFromTPEnzyme = "SELECT t.database_identifier "
			+ "FROM tp_external_database_links t "
			+ "INNER JOIN tp_molecule2db_link tpm on tpm.database_id = t.accession_number "
			+ "where database_name like 'brenda' and tpm.molecule_id = ?";

	public static final String getEnzymeFromGene = "SELECT ge.enzyme, g.sysname "
			+ "FROM dawis_md.kegg_genes_enzyme ge "
			+ "INNER JOIN dawis_md.kegg_enzyme g on g.entry = ge.enzyme "
			+ "where ge.entry=?";

	public static final String getEnzymeFromTranspathGene = "SELECT ge.enzyme, ke.sysname "
			+ "FROM dawis_md.kegg_enzyme ke "
			+ "INNER JOIN dawis_md.kegg_genes_enzyme ge on ke.entry = ge.enzyme "
			+ "INNER JOIN dawis_md.tp_external_database_links tpl on tpl.database_identifier = ge.entry "
			+ "INNER JOIN dawis_md.tp_gene2db_link tpgl on tpgl.database_id = tpl.accession_number "
			+ "where tpl.database_name = 'ENTREZGENE' and tpgl.gene_id=?";

	public static final String getEnzymeFromTranspathGene2 = "SELECT ge.enzyme "
			+ "FROM dawis_md.kegg_genes_enzyme ge "
			+ "INNER JOIN dawis_md.tp_external_database_links tpl on tpl.database_identifier = ge.entry "
			+ "INNER JOIN dawis_md.tp_gene2db_link tpgl on tpgl.database_id = tpl.accession_number "
			+ "where tpl.database_name = 'ENTREZGENE' and tpgl.gene_id = ";

	public static final String getEnzymeFromTranspathCompound = "SELECT tpl.database_identifier "
			+ "FROM dawis_md.tp_external_database_links tpl "
			+ "INNER JOIN dawis_md.tp_molecule2db_link tpml on tpml.database_id = tpl.accession_number "
			+ "where tpl.database_name like 'BRENDA' and tpml.molecule_id = ";

	public static final String getEnzymeFromTranspathCompound2 = "SELECT tpl.database_identifier, tpm.molecule_name "
			+ "FROM dawis_md.tp_external_database_links tpl "
			+ "INNER JOIN dawis_md.tp_molecule2db_link tpml on tpml.database_id = tpl.accession_number "
			+ "INNER JOIN dawis_md.tp_molecule tpm on tpm.molecule_id = tpml.molecule_id "
			+ "where tpl.database_name like 'BRENDA' and tpml.molecule_id = ?";

	public static final String getEnzymeFromTransfacGene = "SELECT database_id "
			+ "FROM tf_gene2db_links t "
			+ "inner join tf_external_database_links tfl on tfl.accession_number = t.database_id "
			+ "where database_name = 'BRENDA' and t.gene_id = ?";

	public static final String getEnzymeFromTransfacGene2 = "SELECT database_id "
			+ "FROM tf_gene2db_links t "
			+ "inner join tf_external_database_links tfl on tfl.accession_number = t.database_id "
			+ "where database_name = 'BRENDA' and t.gene_id = ?";

	public static final String getEnzymeFromGeneOrganismSpecific = "SELECT ge.enzyme, g.sysname, ge.org "
			+ "FROM dawis_md.kegg_genes_enzyme ge "
			+ "INNER JOIN dawis_md.kegg_enzyme g on g.entry = ge.enzyme "
			+ "where ge.entry=?";

	public static final String getEnzymeFromProtein = "SELECT eeu.enzyme_id "
			+ "FROM dawis_md.enzyme_enzyme2uniprot eeu "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers ua on ua.accession_number = eeu.uniprot_id "
			+ "where ua.uniprot_id = ?";

	public static final String getEnzymeFromProtein2 = "SELECT eeu.enzyme_id "
			+ "FROM dawis_md.enzyme_enzyme2uniprot eeu "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers ua on ua.accession_number = eeu.uniprot_id "
			+ "where (ua.uniprot_id = ";

	public static final String getEnzymeFromPathway = "SELECT ke.entry, ke.sysname, kp.org "
			+ "FROM dawis_md.kegg_enzyme_pathway kep "
			+ "INNER JOIN dawis_md.kegg_enzyme ke on ke.entry = kep.entry "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.number = kep.kegg_number "
			+ "where kp.pathway_name = ?";

	public static final String getEnzymeFromPathway2 = "SELECT ke.entry "
			+ "FROM dawis_md.kegg_enzyme_pathway kep "
			+ "INNER JOIN dawis_md.kegg_enzyme ke on ke.entry = kep.entry "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.number = kep.kegg_number "
			+ "where kp.pathway_name = ";

	public static final String getEnzymeFromPathwayOrganismSpecific = "SELECT ke.entry, ke.sysname "
			+ "FROM dawis_md.kegg_enzyme_pathway kep "
			+ "INNER JOIN dawis_md.kegg_enzyme ke on ke.entry = kep.entry "
			+ "where kep.kegg_number = ?";

	public static final String getEnzymeFromGlycan = "SELECT kge.enzyme, ke.sysname "
			+ "FROM dawis_md.kegg_glycan_enzyme kge "
			+ "LEFT OUTER JOIN dawis_md.kegg_enzyme ke on ke.entry = kge.enzyme "
			+ "where kge.entry = ?";

	public static final String getEnzymeFromGlycanOrganismSpecific = "SELECT distinct kge.enzyme, ke.sysname, ge.org "
			+ "FROM dawis_md.kegg_glycan_enzyme kge "
			+ "INNER JOIN dawis_md.kegg_genes_enzyme ge on kge.enzyme = ge.enzyme "
			+ "LEFT OUTER JOIN dawis_md.kegg_enzyme ke on ke.entry = kge.enzyme "
			+ "where kge.entry = ?";

	public static final String getEnzymeFromReaction = "SELECT kre.enzyme, ke.sysname "
			+ "FROM dawis_md.kegg_reaction_enzyme kre "
			+ "LEFT OUTER JOIN dawis_md.kegg_enzyme ke on ke.entry = kre.enzyme "
			+ "where kre.entry = ?";

	public static final String getEnzymeFromReactionOrganismSpecific = "SELECT distinct kre.enzyme, ke.sysname, ge.org "
			+ "FROM dawis_md.kegg_reaction_enzyme kre "
			+ "INNER JOIN dawis_md.kegg_genes_enzyme ge on kre.enzyme = ge.enzyme "
			+ "LEFT OUTER JOIN dawis_md.kegg_enzyme ke on ke.entry = kre.enzyme "
			+ "where kre.entry = ?";

	public static final String getEnzymeFromGO = "SELECT distinct gd.xref_key, ke.sysname "
			+ " FROM dawis_md.go_term gt "
			+ "INNER JOIN dawis_md.go_term_dbxref gtd on gtd.term_id = gt.id "
			+ "INNER JOIN dawis_md.go_dbxref gd on gd.id = gtd.dbxref_id and gd.xref_dbname = 'EC' "
			+ "LEFT OUTER JOIN dawis_md.kegg_enzyme ke on ke.entry = gd.xref_key "
			+ "where gt.acc = ?";

	public static final String getEnzymeFromGOOrganismSpecific = "SELECT distinct gd.xref_key, ke.sysname, kge.org "
			+ " FROM dawis_md.go_term gt "
			+ "INNER JOIN dawis_md.go_term_dbxref gtd on gtd.term_id = gt.id "
			+ "INNER JOIN dawis_md.go_dbxref gd on gd.id = gtd.dbxref_id and gd.xref_dbname = 'EC' "
			+ "INNER JOIN dawis_md.kegg_genes_enzyme kge on kge.enzyme = gd.xref_key "
			+ "LEFT OUTER JOIN dawis_md.kegg_enzyme ke on ke.entry = gd.xref_key "
			+ "where gt.acc = ?";

	public static final String getEnzymeFromCompound = "SELECT kce.enzyme, ke.sysname "
			+ "FROM kegg_compound_enzyme kce "
			+ "LEFT OUTER JOIN kegg_enzyme ke on ke.entry = kce.enzyme "
			+ "where kce.entry = ?";

	public static final String getEnzymeFromCompound2 = "SELECT kce.enzyme "
			+ "FROM kegg_compound_enzyme kce "
			+ "LEFT OUTER JOIN kegg_enzyme ke on ke.entry = kce.enzyme "
			+ "where kce.entry = ";

	public static final String getEnzymeFromUniprotProtein = "SELECT eeu.enzyme_id "
			+ "FROM dawis_md.enzyme_enzyme2uniprot eeu "
			+ "INNER JOIN dawis_md.uniprot_accessionnumbers ua on ua.accession_number = eeu.uniprot_id "
			+ "where  ua.uniprot_id = ?";

	public static final String getEnzymeFromCompoundOrganismSpecific = "SELECT kce.enzyme, ke.sysname, kge.org "
			+ "FROM kegg_compound_enzyme kce "
			+ "INNER JOIN dawis_md.kegg_genes_enzyme kge on kge.enzyme = kce.enzyme "
			+ "LEFT OUTER JOIN kegg_enzyme ke on ke.entry = kce.enzyme "
			+ "where kce.entry = ?";

	public static final String getEnzymeClass = "SELECT kec.class "
			+ "FROM dawis_md.kegg_enzyme_class kec " + "where kec.entry = ?";

	public static final String getEnzymeOrthology = "SELECT * "
			+ "FROM dawis_md.kegg_enzyme_orthology keo "
			+ "where keo.entry = ?";

	public static final String getEnzymeSynonyms = "SELECT bs.bsynonym "
			+ "FROM dawis_md.brenda_synonyms bs " + "where bs.enzyme = ?";

	public static final String getEnzymeSubstrates = "SELECT * "
			+ "FROM dawis_md.kegg_enzyme_substrate kes "
			+ "where kes.entry = ?";

	public static final String getEnzymeProducts = "SELECT * "
			+ "FROM dawis_md.kegg_enzyme_product kep " + "where kep.entry = ?";

	public static final String getEnzymeCofactors = "SELECT * "
			+ "FROM dawis_md.kegg_enzyme_cofactor kec " + "where kec.entry = ?";

	public static final String getEnzymeInhibitors = "SELECT * "
			+ "FROM dawis_md.kegg_enzyme_inhibitor kei "
			+ "where kei.entry = ?";

	public static final String getEnzymeEffectors = "SELECT * "
			+ "FROM dawis_md.kegg_enzyme_effector kee " + "where kee.entry = ?";

	public static final String getEnzymePDBs = "SELECT kes.structures "
			+ "FROM dawis_md.kegg_enzyme_structures kes "
			+ "where kes.entry = ?";

	public static final String getEnzymeDetails = "SELECT ke.enzyme_comment, ke.reference, ke.sysname "
			+ "FROM dawis_md.kegg_enzyme ke " + "where ke.entry = ?";

	public static final String getEnzymeDBLinks = "SELECT * "
			+ "FROM dawis_md.kegg_enzyme_dblinks ked " + "where ked.entry = ?";

	public static final String getOrganismForEnzyme = "SELECT distinct kge.org "
			+ "FROM dawis_md.kegg_genes_enzyme kge " + "where kge.enzyme = ?";

	public static final String getNCBIIDForEnzyme = "SELECT distinct taxonomy "
			+ "FROM uniprot u "
			+ "INNER JOIN uniprot_accessionnumbers ua on ua.uniprot_id = u.uniprot_id "
			+ "INNER JOIN enzyme_enzyme2uniprot eu on eu.uniprot_id = ua.accession_number "
			+ "where ";

	/*
	 * get compound information
	 */

	public static final String getCompoundStartQuery = "SELECT distinct kcn.entry, kcn.name, kc.organism "
			+ "FROM dawis_md.kegg_compound kc "
			+ "INNER JOIN dawis_md.kegg_compound_name kcn on kcn.entry = kc.entry "
			+ "INNER JOIN dawis_md.kegg_taxonomy t on kc.organism = t.org "
			+ "where ";

	public static final String getCompoundStartQueryOrganismIndependent = "SELECT distinct kc.entry, kcn.name, kc.organism "
			+ "FROM dawis_md.kegg_compound kc "
			+ "INNER JOIN dawis_md.kegg_compound_name kcn on kcn.entry = kc.entry "
			+ "where ";

	public static final String getCompoundFromPathwayByNumber = "SELECT distinct kpc.entry "
			+ "FROM dawis_md.kegg_pathway_compound kpc "
			+ "INNER JOIN dawis_md.kegg_compound kc on kc.entry = kpc.entry "
			+ "where kpc.kegg_number = ?";

	public static final String getCompoundFromPathway = "SELECT distinct kpc.entry "
			+ "FROM dawis_md.kegg_pathway_compound kpc "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.number = kpc.kegg_number and kp.org = kpc.organismus "
			+ "where kp.pathway_name = ";

	public static final String getCompoundFromPathway2 = "SELECT distinct kpc.entry "
			+ "FROM dawis_md.kegg_pathway_compound kpc "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.number = kpc.kegg_number and kp.org = kpc.organismus "
			+ "where kp.pathway_name = ?";

	public static final String getCompoundFromTRANSPATHPathway = "SELECT distinct tpmi.molecule_id, tpm.molecule_name "
			+ "FROM dawis_md.tp_pathway_molecules_involved tpmi "
			+ "INNER JOIN dawis_md.tp_molecule tpm on tpm.molecule_id = tpmi.molecule_id "
			+ "where tpmi.pathway_id = ?";

	public static final String getCompoundFromTPPathway2 = "SELECT t.molecule_id "
			+ "FROM dawis_md.tp_pathway_molecules_involved t "
			+ "INNER JOIN dawis_md.tp_molecule_synonyms s on s.molecule_id = t.molecule_id "
			+ "where t.pathway_id = ";

	public static final String getCompoundFromPathwayOrganismSpecific = "SELECT distinct kpc.entry "
			+ "FROM dawis_md.kegg_pathway_compound kpc "
			+ "INNER JOIN dawis_md.kegg_compound kc on kc.entry = kpc.entry "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.org = kpc.organismus and kp.number = kpc.kegg_number "
			+ "where kp.pathway_name = ?";

	public static final String getCompoundFromReaction = "SELECT distinct kcr.entry "
			+ "FROM dawis_md.kegg_compound_reaction kcr "
			+ "LEFT OUTER JOIN dawis_md.kegg_compound kp on kp.entry = kcr.entry "
			+ "where kcr.reaction = ?";

	public static final String getCompoundFromFactor = "SELECT tl.accession_number "
			+ "FROM dawis_md.tf_factor2db_links t "
			+ "inner join dawis_md.tf_external_database_links tl on tl.accession_number = t.database_id "
			+ "where tl.database_name like 'transpath' and factor_id = ";

	public static final String getTranspathCompoundFromEnzyme = "SELECT tpml.molecule_id "
			+ "FROM dawis_md.tp_external_database_links tpl "
			+ "INNER JOIN dawis_md.tp_molecule2db_link tpml on tpml.database_id = tpl.accession_number "
			+ "where tpl.database_name like 'BRENDA' and tpl.database_identifier = ?";

	public static final String getCompoundFromReactionOrganismSpecific = "SELECT distinct kcr.entry, kpc.organismus "
			+ "FROM dawis_md.kegg_compound_reaction kcr "
			+ "INNER JOIN dawis_md.kegg_pathway_compound kpc on kpc.entry = kcr.entry "
			+ "LEFT OUTER JOIN dawis_md.kegg_compound kp on kp.entry = kcr.entry "
			+ "where ";

	public static final String getCompoundFromReactionPair = "SELECT distinct krc.compound "
			+ "FROM dawis_md.kegg_rpair_compound krc "
			+ "LEFT OUTER JOIN dawis_md.kegg_compound kc on kc.entry = krc.compound "
			+ "where krc.entry = ?";

	public static final String getTRANSPATHCompoundFromGene = "SELECT distinct tpgd.product_id "
			+ "FROM dawis_md.tp_gene_product tpgd "
			+ "LEFT OUTER JOIN dawis_md.tp_molecule tpm on tpm.molecule_id = tpgd.product_id "
			+ "where tpgd.gene_id = ?";

	public static final String getTRANSPATHCompoundFromGene2 = "SELECT distinct tpgd.product_id "
			+ "FROM dawis_md.tp_gene_product tpgd "
			+ "LEFT OUTER JOIN dawis_md.tp_molecule tpm on tpm.molecule_id = tpgd.product_id "
			+ "where tpgd.gene_id = ";

	public static final String getCompoundFromTPPathway = "SELECT tm.molecule_id, tm.molecule_name, tpo.latin_name "
			+ "FROM tp_pathway_molecules_involved t "
			+ "inner join tp_molecule tm on tm.molecule_id = t.molecule_id "
			+ "inner join tp_organism tpo on tpo.organism_id = tm.organism_Id "
			+ "where t.pathway_id = ?";

	public static final String getCompoundFromTPPathwayOrganismIndependent = "SELECT tm.molecule_id, tm.molecule_name "
			+ "FROM tp_pathway_molecules_involved t "
			+ "inner join tp_molecule tm on tm.molecule_id = t.molecule_id "
			+ "where t.pathway_id = ?";

	public static final String getCompoundFromReactionPairOrganismSpecific = "SELECT distinct krc.compound, kpc.organismus "
			+ "FROM dawis_md.kegg_rpair_compound krc "
			+ "INNER JOIN dawis_md.kegg_pathway_compound kpc on kpc.entry = krc.compound "
			+ "LEFT OUTER JOIN dawis_md.kegg_compound kc on kc.entry = krc.compound "
			+ "where ";

	public static final String getCompoundFromEnzyme = "SELECT  distinct kce.entry "
			+ "FROM dawis_md.kegg_compound_enzyme kce "
			+ "left outer join dawis_md.kegg_compound kc on kc.entry = kce.entry "
			+ "where kce.enzyme = ?";

	public static final String getCompoundFromEnzyme2 = "SELECT  distinct kce.entry "
			+ "FROM dawis_md.kegg_compound_enzyme kce " + "where kce.enzyme = ";

	public static final String getCompoundName = "SELECT kcn.name "
			+ "FROM dawis_md.kegg_compound_name kcn " + "where kcn.entry = ?";

	public static final String getTPCompoundName = "SELECT tpm.molecule_name "
			+ "FROM dawis_md.tp_molecule tpm " + "where tpm.molecule_id = ?";

	public static final String getTPCompoundStartQuery = "SELECT molecule_id "
			+ "FROM dawis_md.tp_molecule " + "where ";

	public static final String getTPCompound = "SELECT molecule_id "
			+ "FROM dawis_md.tp_molecule_synonyms "
			+ "where molecule_synonym = ?";

	public static final String getTPCompoundSynonyms = "SELECT molecule_synonym "
			+ "FROM dawis_md.tp_molecule_synonyms " + "where molecule_id = ?";

	public static final String getCompoundSynonyms = "SELECT tps.molecule_synonym "
			+ "FROM dawis_md.tp_molecule_synonyms tps "
			+ "where tps.molecule_id = ?";

	public static final String getCompoundDetails = "SELECT * "
			+ "FROM dawis_md.kegg_compound kc " + "where kc.entry = ?";

	public static final String getTranspathCompoundDetails = "SELECT * "
			+ "FROM dawis_md.tp_molecule tpm " + "where tpm.molecule_id = ?";

	public static final String getTRANSPATHCompoundFromGeneOntology = "SELECT tpgo.molecule_id "
			+ "FROM dawis_md.tp_molecule_go_process tpgo "
			+ "where tpgo.go_id = ?";

	public static final String getTRANSPATHCompoundFromGeneOntology2 = "SELECT tpgo.molecule_id "
			+ "FROM dawis_md.tp_molecule_go_process tpgo "
			+ "where tpgo.go_id = ";

	public static final String getCompoundFeature = "SELECT tpm.molecule_feature "
			+ "FROM dawis_md.tp_molecule_features tpm "
			+ "where tpm.molecule_id = ?";

	public static final String getCompoundComment = "SELECT tpm.comment_id "
			+ "FROM dawis_md.tp_molecule_comments tpm "
			+ "where tpm.molecule_id = ?";

	public static final String getCompoundSuperfamily = "SELECT * "
			+ "FROM dawis_md.tp_molecule_superfamalies tpm "
			+ "where tpm.molecule_id = ?";

	public static final String getCompoundSubfamily = "SELECT * "
			+ "FROM dawis_md.tp_molecule_subfamilies tpm "
			+ "where tpm.molecule_id = ?";

	public static final String getOrganismForCompound = "SELECT  distinct kpc.organismus "
			+ "FROM dawis_md.kegg_pathway_compound kpc "
			+ "where kpc.entry = ?";

	public static final String getOrganismForTranspathCompound = "SELECT tpo.organism_id "
			+ "FROM dawis_md.tp_organism tpo "
			+ "INNER JOIN dawis_md.tp_molecule tpm on tpm.organism_id = tpo.organism_id "
			+ "where tpm.molecule_id = ?";

	public static final String getCompoundFromDisease = "SELECT tpml.molecule_id "
			+ "FROM dawis_md.tp_molecule2db_link tpml "
			+ "INNER JOIN dawis_md.tp_external_database_links tpedl on tpedl.accession_number = tpml.database_id "
			+ "where tpedl.database_name = 'MIM' and tpedl.database_identifier = ?";

	public static final String getCompoundFromDisease2 = "SELECT tpml.molecule_id "
			+ "FROM dawis_md.tp_molecule2db_link tpml "
			+ "INNER JOIN dawis_md.tp_external_database_links tpedl on tpedl.accession_number = tpml.database_id "
			+ "where tpedl.database_name = 'MIM' and tpml.molecule_id = ";

	public static final String getCompoundFromTranspathReaction = "SELECT molecule_id "
			+ "FROM (SELECT * FROM dawis_md.tp_reaction_molecule_up "
			+ "UNION SELECT * FROM dawis_md.tp_reaction_molecule_down) tabelle "
			+ "where reaction_id = ";

	public static final String getTranspathReactionFromCompound = "SELECT reaction_id "
			+ "FROM (SELECT * FROM dawis_md.tp_reaction_molecule_up "
			+ "UNION SELECT * FROM dawis_md.tp_reaction_molecule_down) tabelle "
			+ "where molecule_id = ?";

	public static final String getCompoundFromProtein = "SELECT t.molecule_id "
			+ "FROM tp_molecule2db_link t "
			+ "inner join tp_external_database_links tpl on tpl.accession_number = t.database_id "
			+ "inner join uniprot_accessionnumbers ua on ua.accession_number = tpl.database_identifier "
			+ "where database_name like 'swissprot' and ua.uniprot_id = ?";

	public static final String getCompoundFromProtein2 = "SELECT t.molecule_id "
			+ "FROM tp_molecule2db_link t "
			+ "inner join tp_external_database_links tpl on tpl.accession_number = t.database_id "
			+ "inner join uniprot_accessionnumbers ua on ua.accession_number = tpl.database_identifier "
			+ "where database_name like 'swissprot' and (ua.uniprot_id = ";

	/*
	 * get reaction information
	 */

	public static final String getReactionStartQuery = "SELECT distinct kr.entry, kr.name, kpr.organismus "
			+ "FROM dawis_md.kegg_reaction kr "
			+ "INNER JOIN dawis_md.kegg_pathway_reaction kpr on kpr.entry = kr.entry "
			+ "where ";

	public static final String getReactionStartQueryOrganismIndepenent = "SELECT distinct kr.entry, kr.name "
			+ "FROM dawis_md.kegg_reaction kr " + "where ";

	public static final String getTranspathReactionStartQuery = "SELECT distinct tpr.reaction_id, tpr.reaction_name "
			+ "FROM dawis_md.tp_reaction tpr " + "where ";

	public static final String getReactionFromPathwayByNumber = "SELECT distinct kpr.entry, kr.name "
			+ "FROM dawis_md.kegg_pathway_reaction kpr "
			+ "INNER JOIN dawis_md.kegg_reaction kr on kr.entry = kpr.entry "
			+ "where kpr.kegg_number = ?";

	public static final String getTRANSPATHReactionFromPathway = "SELECT distinct pri.reaction_id, pri.pathway_reaction "
			+ "FROM dawis_md.tp_pathway_reactions_involved pri "
			+ "where pri.pathway_id = ?";

	public static final String getTRANSPATHReactionFromPathway2 = "SELECT distinct pri.reaction_id "
			+ "FROM dawis_md.tp_pathway_reactions_involved pri "
			+ "where pri.pathway_id = ";

	public static final String getReactionFromPathway = "SELECT distinct kpr.entry, kr.name, kpr.organismus "
			+ "FROM dawis_md.kegg_pathway_reaction kpr "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.org = kpr.organismus AND kp.number = kpr.kegg_number "
			+ "INNER JOIN dawis_md.kegg_reaction kr on kr.entry = kpr.entry "
			+ "where kp.pathway_name = ?";

	public static final String getReactionFromPathway2 = "SELECT distinct kpr.entry "
			+ "FROM dawis_md.kegg_pathway_reaction kpr "
			+ "INNER JOIN dawis_md.kegg_pathway kp on kp.org = kpr.organismus AND kp.number = kpr.kegg_number "
			+ "INNER JOIN dawis_md.kegg_reaction kr on kr.entry = kpr.entry "
			+ "where kp.pathway_name = ";

	public static final String getReactionFromGlycan = "SELECT distinct kgr.reaction "
			+ "FROM dawis_md.kegg_glycan_reaction kgr " + "where kgr.entry = ?";

	public static final String getReactionFromGlycan2 = "SELECT distinct kgr.reaction "
			+ "FROM dawis_md.kegg_glycan_reaction kgr " + "where kgr.entry = ";

	public static final String getReactionFromGlycanOrganismSpecific = "SELECT distinct kgr.reaction, kr.name, kpr.organismus "
			+ "FROM dawis_md.kegg_glycan_reaction kgr "
			+ "INNER JOIN dawis_md.kegg_pathway_reaction kpr on kpr.entry = kgr.reaction "
			+ "LEFT OUTER JOIN dawis_md.kegg_reaction kr on kr.entry = kgr.reaction "
			+ "where ";

	public static final String getReactionFromEnzyme = "SELECT distinct kre.entry, kr.name "
			+ "FROM dawis_md.kegg_reaction_enzyme kre "
			+ "LEFT OUTER JOIN dawis_md.kegg_reaction kr on kr.entry = kre.entry "
			+ "where kre.enzyme = ?";

	public static final String getReactionFromEnzyme2 = "SELECT distinct kre.entry "
			+ "FROM dawis_md.kegg_reaction_enzyme kre "
			+ "LEFT OUTER JOIN dawis_md.kegg_reaction kr on kr.entry = kre.entry "
			+ "where kre.enzyme = ";

	public static final String getReactionFromEnzymeOrganismSpecific = "SELECT distinct kre.entry, kr.name, kpr.organismus "
			+ "FROM dawis_md.kegg_reaction_enzyme kre "
			+ "INNER JOIN dawis_md.kegg_pathway_reaction kpr on kre.entry = kpr.entry "
			+ "LEFT OUTER JOIN dawis_md.kegg_reaction kr on kr.entry = kre.entry "
			+ "where ";

	public static final String getReactionFromCompound = "SELECT distinct kcr.reaction, kr.name "
			+ "FROM dawis_md.kegg_compound_reaction kcr "
			+ "LEFT OUTER JOIN dawis_md.kegg_reaction kr on kr.entry = kcr.reaction "
			+ "where kcr.entry = ?";

	public static final String getTRANSPATHReactionUpstreamFromCompound = "SELECT * "
			+ "FROM dawis_md.tp_molecule_reaction_up " + "where molecule_id = ";

	public static final String getTRANSPATHReactionDownstreamFromCompound = "SELECT * "
			+ "FROM dawis_md.tp_molecule_reaction_down "
			+ "where molecule_id = ";

	public static final String getTRANSPATHReactionMoleculeDown = "SELECT * "
			+ "FROM dawis_md.tp_reaction_molecule_down "
			+ "where reaction_id = ?";

	public static final String getTRANSPATHReactionMoleculeUp = "SELECT * "
			+ "FROM dawis_md.tp_reaction_molecule_up "
			+ "where reaction_id = ?";

	public static final String getTRANSPATHReactionUpstreamFromGene = "SELECT reaction_id, reaction_upstream as reaction_name, gene_id "
			+ "FROM dawis_md.tp_molecule_reaction_up " + "where gene_id = ";

	public static final String getTRANSPATHReactionDownstreamFromGene = "SELECT reaction_id, reaction_downstream as reaction_name, gene_id "
			+ "FROM dawis_md.tp_molecule_reaction_down " + "where gene_id = ";

	public static final String getReactionFromCompound2 = "SELECT distinct kcr.reaction "
			+ "FROM dawis_md.kegg_compound_reaction kcr "
			+ "where kcr.entry = ";

	public static final String getReactionFromCompoundOrganismSpecific = "SELECT distinct kcr.reaction, kr.name, kpr.organismus "
			+ "FROM dawis_md.kegg_compound_reaction kcr "
			+ "INNER JOIN dawis_md.kegg_pathway_reaction kpr on kpr.entry = kcr.reaction "
			+ "LEFT OUTER JOIN dawis_md.kegg_reaction kr on kr.entry = kcr.reaction "
			+ "where ";

	public static final String getReactionFromReactionPair = "SELECT distinct krr.reaction, kr.name "
			+ "FROM dawis_md.kegg_rpair_reaction krr "
			+ "LEFT OUTER JOIN dawis_md.kegg_reaction kr on krr.reaction = kr.entry "
			+ "where krr.entry = ?";

	public static final String getReactionFromReactionPair2 = "SELECT distinct krr.reaction "
			+ "FROM dawis_md.kegg_rpair_reaction krr " + "where krr.entry = ";

	public static final String getReactionFromReactionPairOrganismSpecific = "SELECT distinct krr.reaction, kr.name, kpr.organismus "
			+ "FROM dawis_md.kegg_rpair_reaction krr "
			+ "INNER JOIN dawis_md.kegg_pathway_reaction kpr on kpr.entry = krr.reaction "
			+ "LEFT OUTER JOIN dawis_md.kegg_reaction kr on krr.reaction = kr.entry "
			+ "where ";

	public static final String getReactionDetails = "SELECT * "
			+ "FROM dawis_md.kegg_reaction kr " + "where kr.entry = ?";

	public static final String getReactionOrthology = "SELECT * "
			+ "FROM dawis_md.kegg_reaction_orthology kro "
			+ "where kro.entry = ?";

	public static final String getReactionSubstrates = "SELECT distinct ks.substrate_name "
			+ "FROM dawis_md.kegg_kgml_substrate ks "
			+ "inner join kegg_reaction_kgml kr on kr.reaction_id = ks.reaction_id "
			+ "where kr.reaction_name = ?";

	public static final String getTRANSPATHReactionSubstrates = "SELECT distinct tps.molecule_id, tps.molecule "
			+ "FROM dawis_md.tp_reaction_molecule_up tps "
			+ "where tps.reaction_id = ?";

	public static final String getReactionProducts = "SELECT distinct kp.product_name "
			+ "FROM dawis_md.kegg_kgml_product kp "
			+ "inner join kegg_reaction_kgml kr on kr.reaction_id = kp.reaction_id "
			+ "where kr.reaction_name = ?";

	public static final String getTRANSPATHReactionProducts = "SELECT distinct tpp.molecule_id, tpp.molecule "
			+ "FROM dawis_md.tp_reaction_molecule_down tpp "
			+ "where tpp.reaction_id = ?";

	public static final String getReactionType = "SELECT distinct rt.reaction_type "
			+ "FROM dawis_md.kegg_reaction_kgml rt "
			+ "where rt.reaction_name = ?";

	public static final String getTranspathReactionComment = "SELECT distinct tpc.comment_id "
			+ "FROM dawis_md.tp_reaction_comments tpc "
			+ "where tpc.reaction_id = ?";

	public static final String getTRANSPATHReactionInhibitors = "SELECT distinct tpi.level_reaction_id, tpi.level_reaction "
			+ "FROM dawis_md.tp_reaction_inhibitors tpi "
			+ "where tpi.reaction_id = ?";

	public static final String getTRANSPATHReactionCatalysts = "SELECT distinct tpc.molecule_id, tpc.molecule "
			+ "FROM dawis_md.tp_reaction_catalysts tpc "
			+ "where tpc.reaction_id = ?";

	public static final String getTranspathReactionDetails = "SELECT distinct tpr.reaction_effect, tpr.reaction_type, tpr.reaction_name "
			+ "FROM dawis_md.tp_reaction tpr " + "where tpr.reaction_id = ?";

	public static final String getOrganismForReaction = "SELECT distinct kpr.organismus "
			+ "FROM dawis_md.kegg_pathway_reaction kpr "
			+ "where kpr.entry = ?";

	/*
	 * get reaction pair information
	 */

	public static final String getReactionPairStartQuery = "SELECT distinct kr.entry, kr.name, kpr.organismus "
			+ "FROM dawis_md.kegg_rpair kr "
			+ "INNER JOIN dawis_md.kegg_rpair_reaction krpr on krpr.entry = kr.entry "
			+ "INNER JOIN dawis_md.kegg_pathway_reaction kpr on krpr.reaction = kpr.entry "
			+ "INNER JOIN dawis_md.kegg_taxonomy t on kpr.organismus = t.org "
			+ "where ";

	public static final String getReactionPairStartQueryOrganismIndependent = "SELECT distinct kr.entry, kr.name "
			+ "FROM dawis_md.kegg_rpair kr " + "where ";

	public static final String getReactionPairFromReaction = "SELECT krr.entry, krp.name "
			+ "FROM dawis_md.kegg_rpair_reaction krr "
			+ "LEFT OUTER JOIN dawis_md.kegg_rpair krp on krr.entry = krp.entry "
			+ "where krr.reaction = ?";

	public static final String getReactionPairFromReactionOrganismSpecific = "SELECT distinct krr.entry, krp.name, kpr.organismus "
			+ "FROM dawis_md.kegg_rpair_reaction krr "
			+ "LEFT OUTER JOIN dawis_md.kegg_pathway_reaction kpr on kpr.entry = krr.reaction "
			+ "LEFT OUTER JOIN dawis_md.kegg_rpair krp on krr.entry = krp.entry "
			+ "where ";

	public static final String getReactionPairFromCompound = "SELECT krc.entry, kr.name "
			+ "FROM dawis_md.kegg_rpair_compound krc "
			+ "LEFT OUTER JOIN dawis_md.kegg_rpair kr on kr.entry = krc.entry "
			+ "where krc.compound = ?";

	public static final String getReactionPairFromCompound2 = "SELECT krc.entry "
			+ "FROM dawis_md.kegg_rpair_compound krc "
			+ "where krc.compound = ";

	public static final String getReactionPairFromReactionPair = "SELECT krprp.relatedpair, kr.name "
			+ "FROM dawis_md.kegg_rpair_relatedpair krprp "
			+ "LEFT OUTER JOIN dawis_md.kegg_rpair kr on kr.entry = krprp.relatedpair "
			+ "where krprp.entry = ?";

	public static final String getReactionPairFromReactionPair2 = "SELECT krprp.relatedpair "
			+ "FROM dawis_md.kegg_rpair_relatedpair krprp "
			+ "where krprp.entry = ";

	public static final String getReactionPairFromReactionPairOrganismSpecific = "SELECT krprp.relatedpair, kr.name, kpr.organismus "
			+ "FROM dawis_md.kegg_rpair_relatedpair krprp "
			+ "INNER JOIN dawis_md.kegg_rpair_reaction krr on krr.entry = krprp.relatedpair "
			+ "INNER JOIN dawis_md.kegg_pathway_reaction kpr on krr.reaction = kpr.entry "
			+ "LEFT OUTER JOIN dawis_md.kegg_rpair kr on kr.entry = krprp.relatedpair "
			+ "where ";

	public static final String getReactionPairDetails = "SELECT * "
			+ "FROM dawis_md.kegg_rpair krp " + "where krp.entry = ?";

	public static final String getOrganismForReactionPair = "SELECT distinct kpr.organismus "
			+ "FROM dawis_md.kegg_pathway_reaction kpr "
			+ "INNER JOIN dawis_md.kegg_rpair_reaction krpr on krpr.reaction = kpr.entry "
			+ "where krpr.entry = ?";

	/*
	 * get glycan information
	 */

	public static final String getGlycanStartQuery = "SELECT distinct kg.entry, kgn.name, kpg.organismus "
			+ "FROM dawis_md.kegg_glycan kg "
			+ "INNER JOIN dawis_md.kegg_glycan_name kgn on kgn.entry = kg.entry "
			+ "INNER JOIN dawis_md.kegg_pathway_glycan kpg on kpg.entry = kg.entry "
			+ "INNER JOIN dawis_md.kegg_taxonomy t on kpg.organismus = t.org "
			+ "where ";

	public static final String getGlycanStartQueryOrganismIndependent = "SELECT distinct kg.entry, kgn.name "
			+ "FROM dawis_md.kegg_glycan kg "
			+ "LEFT OUTER JOIN dawis_md.kegg_glycan_name kgn on kgn.entry = kg.entry "
			+ "where ";

	public static final String getGlycanFromPathway = "SELECT kpg.entry, kpg.organismus "
			+ "FROM dawis_md.kegg_pathway_glycan kpg "
			+ "inner join dawis_md.kegg_pathway kp on kp.number = kpg.kegg_number and kp.org = kpg.organismus "
			+ "where kp.pathway_name = ?";

	public static final String getGlycanFromPathway2 = "SELECT kpg.entry "
			+ "FROM dawis_md.kegg_pathway_glycan kpg "
			+ "inner join dawis_md.kegg_pathway kp on kp.number = kpg.kegg_number and kp.org = kpg.organismus "
			+ "where kp.pathway_name = ";

	public static final String getGlycanFromPathwayByNumber = "SELECT distinct kpg.entry "
			+ "FROM dawis_md.kegg_pathway_glycan kpg "
			+ "where kpg.kegg_number = ?";

	public static final String getGlycanIDFromEnzyme = "SELECT kge.entry "
			+ "FROM dawis_md.kegg_glycan_enzyme kge " + "where kge.enzyme = ?";

	public static final String getGlycanFromEnzyme = "SELECT distinct kge.entry, kpg.organismus "
			+ "FROM dawis_md.kegg_glycan_enzyme kge "
			+ "INNER JOIN dawis_md.kegg_pathway_glycan kpg on kpg.entry = kge.entry "
			+ "where kge.enzyme = ?";

	public static final String getGlycanFromEnzyme2 = "SELECT distinct kge.entry "
			+ "FROM dawis_md.kegg_glycan_enzyme kge " + "where kge.enzyme = ";

	public static final String getGlycanIDFromReaction = "SELECT kgr.entry "
			+ "FROM dawis_md.kegg_glycan_reaction kgr "
			+ "where kgr.reaction = ?";

	public static final String getGlycanFromReaction = "SELECT distinct kgr.entry , kpg.organismus "
			+ "FROM dawis_md.kegg_glycan_reaction kgr "
			+ "INNER JOIN dawis_md.kegg_pathway_glycan kpg on kpg.entry = kgr.entry "
			+ "where ";

	public static final String getGlycanName = "SELECT kgn.name "
			+ "FROM dawis_md.kegg_glycan_name kgn " + "where kgn.entry = ?";

	public static final String getGlycanDetails = "SELECT * "
			+ "FROM dawis_md.kegg_glycan kg " + "where kg.entry = ?";

	public static final String getGlycanClass = "SELECT kgc.class "
			+ "FROM dawis_md.kegg_glycan_class kgc " + "where kgc.entry =?";

	public static final String getGlycanOrthology = "SELECT * "
			+ "FROM dawis_md.kegg_glycan_orthology kgo "
			+ "where kgo.entry = ?";

	public static final String getTPGlycanFromKEGGGlycan = "SELECT t.molecule_id "
			+ "FROM dawis_md.tp_molecule_synonyms t "
			+ "INNER JOIN dawis_md.kegg_glycan_name kgn on kgn.name = t.molecule_synonym "
			+ "where kgn.entry = ?";

	public static final String getKEGGGlycanFromTPGlycan = "SELECT kgn.entry "
			+ "FROM dawis_md.tp_molecule_synonyms t "
			+ "INNER JOIN dawis_md.kegg_glycan_name kgn on kgn.name = t.molecule_synonym "
			+ "where t.molecule_id = ?";

	public static final String getOrganismForGlycan = "SELECT kpg.organismus "
			+ "FROM dawis_md.kegg_pathway_glycan kpg " + "where kpg.entry = ?";

	/*
	 * get gene ontology information
	 */
	public static final String getGOStartQuery = "SELECT distinct gt.acc, gt.name, gs.species "
			+ "FROM dawis_md.go_term gt "
			+ "INNER JOIN dawis_md.go_species gs on gs.id = gt.id "
			+ "INNER JOIN dawis_md.go_term_synonym gts on gts.term_id = gt.id "
			+ "where ";

	public static final String getGOStartQueryOrganismIndependent = "SELECT distinct gt.acc, gt.name "
			+ "FROM dawis_md.go_term gt "
			+ "INNER JOIN dawis_md.go_term_synonym gts on gts.term_id = gt.id "
			+ "where ";

	public static final String getGOFromEnzyme = "SELECT distinct gt.acc, gt.name "
			+ "FROM dawis_md.go_dbxref gd "
			+ "INNER JOIN dawis_md.go_term_dbxref gtd on gd.id = gtd.dbxref_id "
			+ "INNER JOIN dawis_md.go_term gt on gt.id = gtd.term_id "
			+ "where gd.xref_key = ?";

	public static final String getGOFromEnzyme2 = "SELECT gt.acc "
			+ "FROM dawis_md.go_dbxref gd "
			+ "INNER JOIN dawis_md.go_term_dbxref gtd on gd.id = gtd.dbxref_id "
			+ "INNER JOIN dawis_md.go_term gt on gt.id = gtd.term_id "
			+ "where gd.xref_key = ";

	public static final String getGOFromCompound = "SELECT tpm.go_id, gt.name "
			+ "FROM dawis_md.tp_molecule_go_process tpm "
			+ "INNER JOIN dawis_md.go_term gt on gt.acc = tpm.go_id "
			+ "where tpm.molecule_id = ?";

	public static final String getGOIDFromProtein = "SELECT ud.primary_id "
			+ "FROM dawis_md.uniprot_dbxref ud "
			+ "where ud.db_name = 'GO' and ud.uniprot_id = ?";

	public static final String getGOIDFromProtein2 = "SELECT ud.primary_id "
			+ "FROM dawis_md.uniprot_dbxref ud "
			+ "where ud.db_name = 'GO' and ud.uniprot_id = ";

	public static final String getGOFromProtein = "SELECT gt.acc, gt.name "
			+ "FROM dawis_md.go_term gt " + "where gt.acc = ?";

	public static final String getGOFromProtein2 = "SELECT gt.acc, gt.name "
			+ "FROM dawis_md.go_term gt " + "where gt.acc In ";

	public static final String getGOFromHPRDProtein = "Select go_number, name "
			+ "FROM (SELECT * FROM hprd_gene_ontology_2_mol_funktion_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_cell_component_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_bio_process_term) tabelle "
			+ "where hprd_id = ?";

	public static final String getGONumberFromHPRDProtein = "Select go_number "
			+ "FROM (SELECT * FROM hprd_gene_ontology_2_mol_funktion_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_cell_component_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_bio_process_term) tabelle "
			+ "where hprd_id = ";

	public static final String getGOFromHPRDProtein1 = "SELECT * "
			+ "FROM dawis_md.hprd_gene_ontology_2_mol_funktion_term "
			+ "where hprd_id = ";

	public static final String getGOFromHPRDProtein2 = "SELECT * "
			+ "FROM dawis_md.hprd_gene_ontology_2_cell_component_term "
			+ "where hprd_id = ";

	public static final String getGOFromHPRDProtein3 = "SELECT * "
			+ "FROM dawis_md.hprd_gene_ontology_2_bio_process_term "
			+ "where hprd_id = ";

	public static final String getOrganismForGO = "SELECT  distinct gs.species "
			+ "FROM dawis_md.go_term gt "
			+ "INNER JOIN dawis_md.go_species gs on gs.id = gt.id "
			+ "where gt.acc = ?";

	public static final String getGOSynonyms = "SELECT  distinct gts.term_synonym "
			+ "FROM dawis_md.go_term_synonym gts "
			+ "INNER JOIN dawis_md.go_term gt on gts.term_id = gt.id "
			+ "where gt.acc = ?";

	public static final String getGOOntology = "SELECT  distinct gt.term_type, gt.name "
			+ "FROM dawis_md.go_term gt " + "where gt.acc = ?";

	public static final String getGOTermDefinition = "SELECT distinct gtd.term_definition "
			+ "FROM dawis_md.go_term_definition gtd "
			+ "INNER JOIN dawis_md.go_term gt on gtd.term_id = gt.id "
			+ "where gt.acc = ?";

	public static final String getNCBIIDForGO = "SELECT  distinct nbci_taxa_id "
			+ "FROM dawis_md.go_term gt "
			+ "INNER JOIN dawis_md.go_species gs on gs.id = gt.id "
			+ "where gt.acc = ?";

	/*
	 * get drug information
	 */

	public static final String getDrugStartQuery = "SELECT kd.entry, kdn.name "
			+ "FROM dawis_md.kegg_drug kd "
			+ "LEFT OUTER JOIN dawis_md.kegg_drug_name kdn on kdn.entry = kd.entry "
			+ "where ";

	public static final String getDrugFromPathway = "SELECT kpd.entry, kpd.organismus "
			+ "FROM dawis_md.kegg_pathway_drug kpd "
			+ "where kpd.kegg_number = ?";

	public static final String getDrugFromPathway2 = "SELECT kpd.entry "
			+ "FROM dawis_md.kegg_pathway_drug kpd "
			+ "where kpd.kegg_number = ";

	public static final String getDrugFromTPPathway2 = "SELECT t.molecule_id "
			+ "FROM dawis_md.tp_pathway_molecules_involved t "
			+ "INNER JOIN dawis_md.tp_molecule_synonyms s on s.molecule_id = t.molecule_id "
			+ "where t.pathway_id = ";

	public static final String getDrugFromPathwayByNumber = "SELECT kpd.entry "
			+ "FROM dawis_md.kegg_pathway_drug kpd "
			+ "where kpd.kegg_number = ?";

	public static final String getDrugName = "SELECT kdn.name "
			+ "FROM dawis_md.kegg_drug_name kdn " + "where kdn.entry = ?";

	public static final String getDrugDetails = "SELECT * "
			+ "FROM dawis_md.kegg_drug kd " + "where kd.entry = ?";

	public static final String getDrugActivity = "SELECT * "
			+ "FROM dawis_md.kegg_drug_activity kda " + "where kda.entry = ?";

	public static final String getTPDrugFromKEGGDrug = "SELECT t.molecule_id "
			+ "FROM dawis_md.tp_molecule_synonyms t "
			+ "INNER JOIN dawis_md.kegg_drug_name kdn on kdn.name = t.molecule_synonym "
			+ "where kdn.entry = ?";

	public static final String getKEGGDrugFromTPDrug = "SELECT kdn.entry  "
			+ "FROM dawis_md.tp_molecule_synonyms t "
			+ "INNER JOIN dawis_md.kegg_drug_name kdn on kdn.name = t.molecule_synonym "
			+ "where t.molecule_id = ?";

	/*
	 * get fragment information
	 */

	public static final String getFragmentDetails = "SELECT * "
			+ "FROM dawis_md.tf_fragment " + "where fragment_id = ?";

	public static final String getFragmentMethod = "SELECT * "
			+ "FROM dawis_md.tf_fragment_methods " + "where fragment_id = ?";

	public static final String getTransfacSpecies = "SELECT * "
			+ "FROM dawis_md.tf_organism " + "where organism_id = ?";

}
