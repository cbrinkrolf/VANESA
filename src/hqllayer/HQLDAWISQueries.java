package hqllayer;

public class HQLDAWISQueries {

	/*
	 * get pathway information
	 */
	public static final String getKEGGPathwayStartQuery = 
		"SELECT DISTINCT p.pathwayName, p.title, t.name "
			+ "FROM kegg_pathway p, kegg_taxonomy t "
			+ "WHERE p.org = t.org AND ";

	public static final String getKEGGPathwayName = 
		"SELECT DISTINCT p.title "
			+ "FROM kegg_pathway p " + "WHERE p.pathwayName = ?";

	public static final String getKEGGPathwayOrganismusIndependentStartQuery = 
		"SELECT DISTINCT p.number, p.title "
			+ "FROM kegg_pathway p " + "WHERE ";

	public static final String getPathwayFromPathway = 
		"SELECT DISTINCT ken.id.entryName, ken.keggPathway.title, ken.keggPathway.org "
			+ "FROM kegg_entry_name ken "
			+ "WHERE ken.keggEntry.type = 'map' and ken.id.pathwayName = ?";

	public static final String getTranspathPathwayStartQuery = 
		"SELECT DISTINCT tpp.pathwayId, tpp.name "
			+ "FROM tp_pathway tpp " + "WHERE ";

	public static final String getTPPathwayFromTPPathway = 
		"SELECT DISTINCT tpl.pathwayId.pathwayId, tpp.name "
			+ "FROM tp_pathway_level tpl, tp_pathway tpp "
			+ "WHERE tpp.pathwayId = tpl.pathwayId.pathwayId AND tpl.superOrdinatedId = ?";

	public static final String getPathwayFromPathway2 = 
		"SELECT DISTINCT ken.id.entryName "
			+ "FROM kegg_entry_name ken "
			+ "WHERE ken.keggEntry.type = 'map' and ken.keggEntry.id.pathwayName = ";

	public static final String getTPPathwayFromTPPathway2 = 
		"SELECT DISTINCT tpl.superOrdinatedId, tpl.superOrdinated "
			+ "FROM tp_pathway_level tpl " + "WHERE tpl.pathwayId.pathwayId = ";

	public static final String getTPPathwayComments = 
		"SELECT DISTINCT tpc.pathwayCommentsId "
			+ "FROM tp_pathway_comments tpc " + "WHERE tpc.pathwayId = ?";

	public static final String getTPPathwayReference = 
		"SELECT r.pubmedId " +
		"FROM tp_pathway tpp " +
		"INNER JOIN tpp.referenceId r "
		+ "WHERE tpp.pathwayId = ?";

	public static final String getTPPathwayFromCompound = 
		"SELECT tppm.pathwayId.pathwayId "
			+ "FROM tp_pathway_molecules_involved tppm "
			+ "WHERE tppm.moleculeId = ?";

	public static final String getTPPathwayFromReaction = 
		"SELECT tppr.pathwayId.pathwayId "
			+ "FROM tp_pathway_reactions_involved tppr "
			+ "WHERE tppr.reactionId = ?";

	public static final String getPathwayCountFromGene = 
		"SELECT count(kgp.id.entry) "
			+ "FROM kegg_genes_pathway kgp "
			+ "WHERE kgp.number = ?";

	public static final String getPathwayFromGene = 
		"SELECT DISTINCT kgp.number, kgp.id.name, kgp.org "
			+ "FROM kegg_genes_pathway kgp " + "WHERE kgp.id.entry = ?";

	public static final String getPathwayFromGeneOrganismIndependent = 
		"SELECT DISTINCT kgp.number, kgp.id.name "
			+ "FROM kegg_genes_pathway kgp " + "WHERE kgp.id.entry = ?";

	public static final String getPathwayFromEnzyme = 
		"SELECT DISTINCT kp.pathwayName, kp.title, kp.org "
			+ "FROM kegg_pathway kp, kegg_enzyme_pathway kep "			
			+ "WHERE kep.id.number = kp.number AND kep.id.organismus = kp.org " +
			"AND kep.id.entry = ?";

	public static final String getPathwayFromEnzyme2 = 
		"SELECT kp.pathwayName "
			+ "FROM kegg_enzyme_pathway kep, kegg_pathway kp "
			+ "WHERE kep.id.number = kp.number AND kep.id.organismus = kp.org AND kep.id.entry = ";

	public static final String getPathwayNumberAndNameFromEnzyme = 
		"SELECT kep.id.number "
			+ "FROM kegg_enzyme_pathway kep " + "WHERE kep.id.entry = ?";

	public static final String getPathwayFromCompound = 
		"SELECT DISTINCT kpc.id.number, kp.title "
			+ "FROM kegg_pathway_compound kpc, kegg_pathway kp " 
			+ "WHERE kp.number = kpc.id.number AND kp.org = kpc.id.organismus  AND kpc.id.entry = ?";

	public static final String getPathwayFromCompound2 = 
		"SELECT DISTINCT kpc.id.number, kp.title "
			+ "FROM kegg_pathway_compound kpc, kegg_pathway kp "
			+ "WHERE kp.number = kpc.id.number AND kp.org = kpc.id.organismus AND kpc.id.entry = ";

	public static final String getPathwayFromTRANSPATHCompound = 
		"SELECT DISTINCT tpmi.pathwayId.pathwayId, tpp.name "
			+ "FROM tp_pathway_molecules_involved tpmi, tp_pathway tpp "
			+ "WHERE tpp.pathwayId = tpmi.pathwayId.pathwayId AND tpmi.moleculeId = ?";

	public static final String getTRANSPATHPathwayMoleculesInvolved = 
		"SELECT DISTINCT tpmi.moleculeId, tpmi.molecule "
			+ "FROM tp_pathway_molecules_involved tpmi "
			+ "WHERE tpmi.pathwayId.pathwayId = ?";

	public static final String getPathwayFromCompoundOrganismSpecific = 
		"SELECT kp.pathwayName, kp.title, kp.org "
			+ "FROM kegg_pathway_compound kpc, kegg_pathway kp  "
			+ "WHERE kp.org = kpc.id.organismus AND kp.number = kpc.id.number AND ";

	public static final String getPathwayFromReaction = 
		"SELECT DISTINCT kpr.id.number, kp.title "
			+ "FROM kegg_pathway_reaction kpr, kegg_pathway kp "
			+ "WHERE kp.number = kpr.id.number AND kpr.id.entry = ?";

	public static final String getPathwayFromReactionOrganismSpecific = 
		"SELECT kp.pathwayName, kp.title, kp.org "
			+ "FROM kegg_pathway_reaction kpr, kegg_pathway kp "
			+ "WHERE kp.number = kpr.id.number and kp.org = kpr.id.organismus AND ";

	public static final String getPathwayFromGlycan = 
		"SELECT DISTINCT kpg.id.number, kp.title "
			+ "FROM kegg_pathway_glycan kpg, kegg_pathway kp "
			+ "WHERE kp.org = kpg.id.organismus AND kp.number = kpg.id.number AND kpg.id.entry = ?";

	public static final String getPathwayFromGlycanOrganismSpecific = 
		"SELECT kp.pathwayName, kp.title, kp.org "
			+ "FROM kegg_pathway_glycan kpg, kegg_pathway kp "
			+ "WHERE kp.org = kpg.id.organismus AND kp.number = kpg.id.number AND ";

	public static final String getPathwayFromDrug = 
		"SELECT kpd.id.number, kp.title, kpd.id.organismus "
			+ "FROM kegg_pathway_drug kpd, kegg_pathway kp "
			+ "WHERE kp.number = kpd.id.number AND kpd.id.entry = ?";

	public static final String getPathwayFromDrug2 = 
		"SELECT kpd.id.number, kp.title, kpd.id.organismus "
			+ "FROM kegg_pathway_drug kpd, kegg_pathway kp "
			+ "WHERE kp.number = kpd.id.number AND kpd.id.entry = ";

	public static final String getPathwayNumber = 
		"SELECT kp.number "
			+ "FROM kegg_pathway kp " + "WHERE kp.pathwayName = ?";

	public static final String getPathwayOrganism = 
		"SELECT kp.org "
			+ "FROM kegg_pathway kp " + "WHERE kp.org = ?";

	public static final String getPathwayOrganism2 = 
		"SELECT t.name "
			+ "FROM kegg_pathway kp, kegg_taxonomy t "
			+ "WHERE t.org = kp.org  AND kp.pathwayName = ?";

	public static final String getPathwayMap = 
		"SELECT kp.image "
			+ "FROM kegg_pathway kp " + "WHERE kp.pathwayName = ?";

	public static final String getPathwayMapByNumber = 
		"SELECT kp.image "
			+ "FROM kegg_pathway kp " + "WHERE kp.number = ?";

	public static final String getTPPathway = 
		"SELECT DISTINCT t.pathwayId "
			+ "FROM tp_pathway t, kegg_pathway kp "
			+ "WHERE kp.title like t.name AND t.type like 'pathway' AND t.name LIKE ";

	public static final String getTPPathwayName = 
		"SELECT t.name "
			+ "FROM tp_pathway t, kegg_pathway kp "
			+ "WHERE t.name like kp.title AND t.type like 'pathway' AND t.pathwayId = ? ";

	public static final String getKEGGPathwayFromTPPathway = 
		"SELECT DISTINCT kp.number "
			+ "FROM tp_pathway t, kegg_pathway kp "
			+ "WHERE t.name like kp.title AND t.type like 'pathway' AND t.pathwayId = ";

	public static final String getTPPathwayFromKEGGPathway = 
		"SELECT DISTINCT t.pathwayId "
			+ "FROM tp_pathway t, kegg_pathway kp "
			+ "WHERE t.name like kp.title AND t.type like 'pathway' and kp.number = ";

	public static final String getKEGGPathwayFromTPPathway2 = 
		"SELECT DISTINCT kp.number "
			+ "FROM tp_pathway t, kegg_pathway kp "
			+ "WHERE t.name like kp.title AND t.type like 'pathway' and t.pathwayId = ? ";

	public static final String getTPPathwayFromKEGGPathway2 = 
		"SELECT DISTINCT t.pathwayId "
			+ "FROM tp_pathway t, kegg_pathway kp "
			+ "WHERE t.name like kp.title AND t.type like 'pathway' and kp.number = ? ";

	public static final String getTranspathPathwayType = 
		"SELECT t.type "
			+ "FROM tp_pathway t " + "WHERE t.pathwayId = ?";

	public static final String getTranspathPathwayComment = 
		"SELECT tpc.pathwayCommentsId "
			+ "FROM tp_pathway_comments tpc "
			+ "WHERE tpc.pathwayId.pathwayId = ?";

	public static final String getOrganismForPathway = 
		"SELECT DISTINCT p.org "
			+ "FROM kegg_pathway p " + "WHERE ";

	/*
	 * get disease information
	 */
	public static final String getOMIMDiseaseStartQuery = "SELECT DISTINCT d.mim,d.title "
			+ "FROM omim_disease d " + "INNER JOIN d.osynonym os " + "WHERE ";

	public static final String getOMIMDiseaseName = "SELECT DISTINCT d.title "
			+ "FROM omim_disease d " + "WHERE d.mim = ?";

	public static final String getDiseaseDiagnosisType = "SELECT dt.name "
			+ "FROM omim_diagnosis_type dt,omim_disease d  "
			+ "WHERE dt.id=cast(d.type as string) " + "AND d.mim = ?";

	public static final String getDiseaseFromGene = "SELECT d.mim,d.title "
			+ "FROM omim_disease d, kegg_genes_dblinks gdbl "
			+ "WHERE gdbl.identifier=d.mim "
			+ "AND gdbl.dbname='OMIM' AND gdbl.entry = ?";

	public static final String getDiseaseFromGene2 = "SELECT d.mim "
			+ "FROM omim_disease d, kegg_genes_dblinks gdbl "
			+ "WHERE gdbl.identifier = d.mim "
			+ "AND gdbl.dbname='OMIM' AND gdbl.entry=?";

	public static final String getDiseaseFromEMBLGene = "SELECT ogm.mim, ogm.title "
			+ "FROM omim_gene_map ogm, omim_gene_symbol ogs, embl_features ef "
			+ "WHERE ogs.cm=ogm.cm AND ef.id.FValue=ogs.symbol "
			+ "AND ef.id.FKey = 'gene' and ef.id.FAttribute = 'gene' and ef.id.primaryAc = ?";

	public static final String getDiseaseFromTRANSPATHGene = "SELECT tpedl.databaseIdentifier,od.title "
			+ "FROM tp_gene t, omim_disease od  "
			+ "INNER JOIN t.externalDatabaseLinkId tpedl "
			+ "WHERE od.mim = tpedl.databaseIdentifier "
			+ "AND tpedl.database like '%MIM' and t.geneId = ?";	

	public static final String getDiseaseFromTRANSPATHGene2 = 
		"SELECT tpedl.databaseIdentifier "
			+ "FROM tp_gene t "
			+ "INNER JOIN t.externalDatabaseLinkId tpedl "
			+ "WHERE tpedl.database like '%MIM' and t.geneId = ";

	public static final String getDiseaseFromTRANSFACGene2 = 
		"SELECT tfdl.link.identifier " +
		"FROM tf_gene2db_links tfdl " +
		"WHERE tfdl.link.databaseName like '%MIM' AND tfdl.geneId.geneId = ";		
	
	public static final String getDiseaseFromHPRDProtein = 
		"SELECT hm.omimId, od.title "
			+ "FROM hprd_hprd_id_mapping hm, omim_disease od "
			+ "WHERE od.mim=cast(hm.omimId as string) " +
			"AND hm.hprdId = ?";

	public static final String getDiseaseFromTPProtein = 
		"SELECT tpdl.databaseIdentifier "
			+ "FROM tp_molecule t " 
			+ "INNER JOIN t.externalDatabaseLinkId tpdl " 
			+ "WHERE tpdl.database like '%mim' and t.moleculeId = ?";

	public static final String getDiseaseIDFromProtein = 
		"SELECT ud.id.primaryId "
			+ "FROM uniprot_dbxref ud " + "WHERE ";

	public static final String getDiseaseFromProtein = 
		"SELECT d.mim, d.title "
			+ "FROM omim_disease d " + "WHERE d.mim = ?";

	public static final String getDiseaseFromCompound = 
		"SELECT tpedl.databaseIdentifier "
			+ "FROM tp_molecule tpml "
			+ "INNER JOIN tpml.externalDatabaseLinkId tpedl "
			+ "WHERE tpedl.database = 'MIM' and tpml.moleculeId = ?";

	public static final String getDiseaseFromCompound2 = 
		"SELECT tpedl.databaseIdentifier "
			+ "FROM tp_molecule tpml "
			+ "INNER JOIN tpml.externalDatabaseLinkId tpedl "
			+ "WHERE tpedl.database = 'MIM' and tpml.moleculeId = ?";

	public static final String getDiseaseSymbolsAndLocations = 
		"SELECT gs.symbol, gm.location "
			+ "FROM omim_gene_map gm, omim_gene_symbol gs "
			+ "WHERE gm.cm = gs.cm AND gm.mim=?";

	public static final String getDiseaseLocations = 
		"SELECT gm.location "
			+ "FROM omim_gene_map gm " + "WHERE gm.mim=?";

	public static final String getDiseaseReference = 
		"SELECT r.entry "
			+ "From omim_reference r " + "WHERE r.disease.mim=?";

	public static final String getDiseaseSynonyms = 
		"SELECT o.osynonym "
			+ "FROM omim_osynonym o " + "WHERE o.disease.mim=?";

	public static final String getDiseaseDetails = 
		"SELECT dt.id, gd.disorder "
			+ "FROM omim_diagnosis_type dt, omim_disease d, omim_gene_map gm, omim_gene_disorders gd "
			+ "WHERE cast(d.type as string)=dt.id AND d.mim = gm.mim AND gd.cm = gm.cm AND d.mim=?";

	public static final String getDiseaseDisorder = 
		"SELECT do.disorder "
			+ "FROM omim_gene_disorders do " + "WHERE do.MIM=?";

	public static final String getDiseaseFeatures = 
		"SELECT cs.odomain, cs.feature "
			+ "FROM omim_clinical_synopsis cs " + "WHERE cs.disease.mim=?";

	/*
	 * get gene information
	 */

	public static final String getGeneStartQuery = 
		"SELECT DISTINCT g.id.entry, g.id.name, t.org "
			+ "FROM kegg_genes_name g, kegg_taxonomy t "
			+ "WHERE g.id.org = t.latinName " +
					"AND g.id.org is not null AND t.latinName is not null ";

	public static final String getGeneStartQueryOrganismIndependent = 
		"SELECT DISTINCT g.id.entry, g.id.name "
			+ "FROM kegg_genes_name g " + "WHERE ";

	public static final String getEMBLGeneOrganismIndependentStartQuery = 
			"FROM embl_description ed " + "WHERE  ";

	public static final String getEMBLGeneStartQuery = 
		"SELECT DISTINCT ed.id.primaryAc, ed.id.description, eo.id.species "
			+ "FROM embl_description ed, embl_organismdata eo "
			+ "WHERE eo.id.primaryAc = ed.id.primaryAc " +
					"AND eo.id.primaryAc is not null AND ed.id.primaryAc is not null AND ";

	public static final String getTransfacGeneOrganismIndependentStartQuery = 
		"SELECT DISTINCT tfg.geneId, tfg.shortGeneTerm "
			+ "FROM tf_gene tfg, tf_gene_synonyms tfgs "
			+ "WHERE tfgs.geneId = tfg.geneId " +
					"AND tfgs.geneId is not null AND tfg.geneId is not null AND ";

	public static final String getTransfacGeneStartQuery = 
		"SELECT DISTINCT tfg.geneId, tfg.shortGeneTerm, tfo.latinName "
			+ "FROM tf_gene tfg, tf_organism tfo, tf_gene_synonyms tfgs "
			+ "WHERE tfo.organismId = tfg.organismId AND tfgs.geneId = tfg.geneId " +
					"AND tfo.organismId is not null AND tfg.organismId is not null " +
					"AND tfgs.geneId is not null AND tfg.geneId is not null AND ";

	public static final String getTranspathGeneStartQuery = 
		"SELECT DISTINCT tpg.geneId, tpg.name, tpo.latinName "
			+ "FROM tp_gene tpg, tp_organism tpo, tp_gene_synonyms tpgs "
			+ "WHERE tpo.organismId = tpg.organismId AND tpo.organismId is not null AND tpg.organismId is not null " +
					"AND tpgs.geneId = tpg.geneId AND tpgs.geneId is not null AND ";

	public static final String getTranspathGeneOrganismIndependentStartQuery = 
		"SELECT DISTINCT tpg.geneId, tpg.name "
			+ "FROM tp_gene tpg, tp_gene_synonyms tpgs "
			+ "WHERE tpgs.geneId = tpg.geneId AND tpgs.geneId is not null AND tpg.geneId is not null AND ";

	public static final String getKEGGGeneFromTranspathGene = 
		"SELECT tpl.databaseIdentifier "
		+"FROM tp_gene tpg " +
				"INNER JOIN tpg.externalDatabaseLinkId tpl "
			+ "WHERE tpl.database = 'ENTREZGENE' AND tpg.geneId = ?";

	public static final String getKEGGGeneFromTransfacGene = 
		"SELECT tfdl.link.identifier " +
		"FROM tf_gene2db_links tfdl " +
		"WHERE tfdl.link.databaseName like 'ENTREZGENE' AND tfdl.geneId.geneId = ";	
	
	public static final String getGeneFromDisease = 
		"SELECT g.entry, gs.symbol "
			+ "FROM omim_gene_map gm, omim_gene_symbol gs, kegg_genes_dblinks g "
			+ "WHERE gm.cm = gs.cm AND g.identifier = gm.mim AND g.dbname = 'OMIM' " +
					"AND gm.cm is not null AND gs.cm is not null AND gm.mim is not null AND g.identifier is not null " +
					"AND gm.mim =?";

	public static final String getTRANSPATHGeneFromDisease = 
		"SELECT tg.geneId, tg.name "
			+ "FROM tp_gene tg " +
					"INNER JOIN tg.externalDatabaseLinkId tpl " +
					"WHERE tpl.database like '%MIM' AND tpl.databaseIdentifier = ?";
	
	public static final String getTPGeneFromDisease = 
		"SELECT tg.geneId "
		+ "FROM tp_gene tg " +
				"INNER JOIN tg.externalDatabaseLinkId tpl " +
				"WHERE tpl.database like '%MIM' AND tpl.databaseIdentifier = ?";

	public static final String getTRANSFACGeneFromDisease =
		"SELECT tfdl.geneId.geneId, tfdl.geneId.shortGeneTerm " +
		"FROM tf_gene2db_links tfdl " +
		"WHERE tfdl.link.databaseName like '%MIM' AND tfdl.link.identifier = ? ";		

	public static final String getTPGeneFromTPProtein = 
		"SELECT tpm.encodingGeneId "
			+ "FROM tp_molecule tpm " + "WHERE tpm.moleculeId = ?";

	public static final String getTFGeneFromTFProtein = 
		"SELECT tff.encodingGeneId "
			+ "FROM tf_factor tff " + "WHERE tff.factorId = ?";

	public static final String getTPGeneFromEnzyme = 
		"SELECT tpg.geneId "
			+ "FROM kegg_enzyme ke, tp_gene tpg, kegg_genes_enzyme ge " 
			+ "INNER JOIN tpg.externalDatabaseLinkId tpgl "
			+ "WHERE ke.entry = ge.id.enzyme AND ke.entry is not null AND ge.id.enzyme is not null " +
					"AND tpgl.database = 'ENTREZGENE' and ge.id.enzyme = ?";

	public static final String getTPGeneFromEnzyme2 = 
		"SELECT tpg.geneId "
		+ "FROM kegg_enzyme ke, tp_gene tpg, kegg_genes_enzyme ge " 
		+ "INNER JOIN tpg.externalDatabaseLinkId tpgl "
		+ "WHERE ke.entry = ge.id.enzyme AND ke.entry is not null AND ge.id.enzyme is not null " +
				"AND tpgl.database = 'ENTREZGENE' and ge.id.enzyme = ";
	
	public static final String getTPGeneFromTPCompound = 
		"SELECT DISTINCT tpgd.geneId "
			+ "FROM tp_gene_product tpgd, tp_molecule tpm "
			+ "WHERE tpm.moleculeId = tpgd.productId " +
					"AND tpm.moleculeId is not null AND tpgd.productId = ?";

	public static final String getTPGeneFromTPCompound2 = 
		"SELECT DISTINCT tpgd.geneId "
		+ "FROM tp_gene_product tpgd, tp_molecule tpm "
		+ "WHERE tpm.moleculeId = tpgd.productId " +
				"AND tpm.moleculeId is not null AND tpgd.productId = ";
	
	public static final String getTFGeneFromEnzyme = 
		"SELECT t.id.geneId "
			+ "FROM tf_gene2db_links t "
			+ "WHERE t.link.databaseName = 'BRENDA' AND t.link.identifier = ?";

	public static final String getTFGeneFromEnzyme2 = 
		"SELECT t.id.geneId "
		+ "FROM tf_gene2db_links t "
		+ "WHERE t.link.databaseName = 'BRENDA' AND t.link.identifier = ";
	
	public static final String getEMBLGeneType = 
		"SELECT ei.molecularType "
			+ "FROM embl_identification ei "
			+ "WHERE ei.primaryAc = ?";

	public static final String getEMBLGeneDescription = 
		"SELECT ed.id.description "
			+ "FROM embl_description ed " + "WHERE ed.id.primaryAc = ?";

	public static final String getKEGGOrganismSynonyms = 
		"SELECT t.org, t.latinName, t.name "
			+ "FROM kegg_taxonomy t " + "WHERE ";

	public static final String getGeneFromUniProtProtein = 
		"SELECT DISTINCT g.id.entry "
			+ "FROM kegg_genes g,uniprot_accessionnumbers a "
			+ "INNER JOIN g.keggGenesDblinkses l "
			+ "WHERE a.id.accessionNumber = l.identifier AND a.id.accessionNumber is not null AND l.identifier is not null " +
					"AND l.dbname = 'UniProt' AND a.id.uniprotId =?";

	public static final String getGeneFromUniProtProtein2 = 
		"SELECT DISTINCT g.id.entry "
		+ "FROM kegg_genes g,uniprot_accessionnumbers a "
		+ "INNER JOIN g.keggGenesDblinkses l "
		+ "WHERE a.id.accessionNumber = l.identifier AND a.id.accessionNumber is not null AND l.identifier is not null " +
				"AND l.dbname = 'UniProt' AND a.id.uniprotId =";
	
	public static final String getEMBLGeneFromUniProtProtein2 = 
		"SELECT DISTINCT u.id.primaryId "
			+ "FROM uniprot_dbxref u, uniprot_accessionnumbers ua "
			+ "WHERE ua.id.uniprotId = u.id.uniprotId AND u.id.dbName like 'EMBL' and ua.id.accessionNumber = ";

	public static final String getGeneFromUniProtProteinOrganismSpecific = 
		"SELECT DISTINCT g.id.entry, g.id.org "
			+ "FROM kegg_genes g, uniprot_accessionnumbers a "
			+ "INNER JOIN g.keggGenesDblinkses l "
			+ "WHERE a.id.accessionNumber = l.identifier AND a.id.accessionNumber is not null AND l.identifier is not null " +
					"AND l.dbname = 'UniProt' AND a.id.uniprotId =?";

	public static final String getEMBLGeneFromUniProtProtein = 
		"SELECT DISTINCT ud.id.primaryId "
			+ "FROM uniprot_dbxref ud, uniprot_accessionnumbers ua "
			+ "WHERE ua.id.uniprotId = ud.id.uniprotId AND ua.id.uniprotId is not null AND ud.id.uniprotId is not null " +
					"AND ud.id.dbName like 'EMBL' and ua.id.accessionNumber = ?";

	public static final String getEMBLGeneFromFactor = 
		"SELECT t.link.accessionNumber "
			+ "FROM tf_factor2db_links t "
			+ "WHERE t.link.databaseName like 'embl' AND t.factor.factorId = ";

	public static final String getGeneFromHPRDProtein = 
		"SELECT hm.entrezgeneId "
			+ "FROM hprd_hprd_id_mapping hm " + "WHERE hm.hprdId = ?";

	public static final String getGeneFromHPRDProtein2 = 
		"SELECT hm.entrezgeneId "
		+ "FROM hprd_hprd_id_mapping hm " + "WHERE hm.hprdId = ";
	
	public static final String getTransfacGeneFromFactor = 
		"SELECT tff.geneId "
			+ "FROM tf_gene_encoded_factor tff "
			+ "WHERE tff.factorId = ?";

	public static final String getGeneFromEnzyme = 
		"SELECT g.id.entry "
			+ "FROM kegg_genes_enzyme g " + "WHERE g.id.enzyme = ?";

	public static final String getGeneFromEnzyme2 = 		
		"SELECT g.id.entry "
		+ "FROM kegg_genes_enzyme g " + "WHERE g.id.enzyme = ";
	
	public static final String getGeneFromTRANSPATHCompound = 
		"SELECT tpgp.geneId, tpgp.geneName "
			+ "FROM tp_gene_product tpgp "
			+ "WHERE tpgp.productId = ?";

	public static final String getTranspathGeneFromReactionUp = 
		"SELECT u.moleculeId " +
		"FROM tp_reaction_molecule_up u " +
		"WHERE reactionId = ";
	
	public static final String getTranspathGeneFromReactionDown =
		"SELECT d.moleculeId " +
		"FROM tp_reaction_molecule_down d "
			+ "WHERE reactionId = ";	
	//TODO: Union
	
	public static final String getGeneFromEnzymeOrganismSpecific = 
		"SELECT g.id.entry, g.id.org "
			+ "FROM kegg_genes_enzyme g " + "WHERE g.id.enzyme = ?";

	public static final String getGeneFromPathway = 
		"SELECT kgp.id.entry, kgp.org "
			+ "FROM kegg_genes_pathway kgp, kegg_pathway kp "
			+ "WHERE kp.number = kgp.number AND kp.number is not null AND kgp.number is not null " +
			"AND kp.org = kgp.org AND kp.org is not null AND kgp.org is not null AND kp.pathwayName = ?";

	public static final String getGeneFromPathway2 = 
		"SELECT kgp.id.entry, kgp.org "
		+ "FROM kegg_genes_pathway kgp, kegg_pathway kp "
		+ "WHERE kp.number = kgp.number AND kp.number is not null AND kgp.number is not null " +
		"AND kp.org = kgp.org AND kp.org is not null AND kgp.org is not null AND kp.pathwayName = ";

	public static final String getGeneFromPathwayOrganismIndependent = 
		"SELECT DISTINCT kgp.id.entry "
			+ "FROM kegg_genes_pathway kgp "
			+ "WHERE kgp.number = ?";

	public static final String getGeneName = 
		"SELECT kgn.id.name "
			+ "FROM kegg_genes_name kgn " + "WHERE kgn.id.entry = ?";

	public static final String getTPGeneSynonyms = 
		"SELECT tps.synonym "
			+ "FROM tp_gene_synonyms tps " + "WHERE tps.geneId.geneId = ?";

	public static final String getTFGeneSynonyms = 
		"SELECT tfs.synonym "
			+ "FROM tf_gene_synonyms tfs " + "WHERE tfs.geneId.geneId = ?";

	public static final String getEMBLGeneSynonyms = 
		"SELECT ek.id.keywords "
			+ "FROM embl_keywords ek " + "WHERE ek.id.primaryAc = ?";

	public static final String getTPGeneName = 
		"SELECT tpg.name "
			+ "FROM tp_gene tpg " + "WHERE tpg.geneId = ?";

	public static final String getTFGeneName = 
		"SELECT tfg.shortGeneTerm "
			+ "FROM tf_gene tfg " + "WHERE tfg.geneId = ?";

	public static final String getEMBLGeneName = 
		"SELECT ed.id.description "
			+ "FROM embl_description ed " + "WHERE ed.id.primaryAc = ?";

	public static final String getGeneDetails = 
			"FROM kegg_genes kg " + "WHERE kg.id.entry = ?";

	public static final String getKEGGGeneFromTFGene = 
		"SELECT t.link.identifier "
			+ "FROM tf_gene2db_links t "
			+ "WHERE t.link.databaseName like 'ENTREZGENE' AND t.geneId.geneId = ?";

	public static final String getKEGGGeneFromTPGene = 
		"SELECT tpgl.databaseIdentifier "
			+ "FROM tp_gene tpg "
			+ "INNER JOIN tpg.externalDatabaseLinkId tpgl " +
			"WHERE tpgl.database = 'ENTREZGENE' and tpg.geneId = ?";

	public static final String getTFGeneFromEMBLGene = 
		"SELECT t.geneId.geneId "
			+ "FROM tf_gene2db_links t "
			+ "WHERE t.link.databaseName like 'EMBL' AND t.link.identifier = ?";

	public static final String getEMBLGeneFromTFGene = 
		"SELECT t.link.identifier "
		+ "FROM tf_gene2db_links t "
		+ "WHERE t.link.databaseName like 'EMBL' AND t.geneId.geneId = ?";

	public static final String getTFGeneFromKEGGGene = 
		"SELECT t.geneId.geneId "
		+ "FROM tf_gene2db_links t "
		+ "WHERE t.link.databaseName like 'ENTREZGENE' AND t.link.identifier = ?";

	public static final String getTPGeneFromKEGGGene = 
		"SELECT tpg.geneId "
			+ "FROM tp_gene tpg " +
			"INNER JOIN tpg.externalDatabaseLinkId tpgl "
			+ "WHERE tpgl.database = 'ENTREZGENE' and tpgl.databaseIdentifier = ?";

	public static final String getGeneSequenceDetails = 
		"FROM kegg_genes_sequence kgs " + "WHERE kgs.id.entry = ?";

	public static final String getEMBLGeneSequenceData = 
		"FROM embl_sequencedata es " + "WHERE es.id.primaryAc = ?";

	public static final String getGeneOrthologyDetails = 
		"SELECT kgo.oragnism, kgo.orthology, kgo.name "
			+ "FROM kegg_genes_orthology kgo " + "WHERE kgo.entry = ?";

	public static final String getMotif = 
		"FROM kegg_genes_motif kgm " + "WHERE kgm.entry = ?";

	public static final String getEMBLGeneOrganismData = 
		"FROM embl_organismdata eo " + "WHERE eo.id.primaryAc = ?";

	public static final String getEMBLGeneSequenceLength = 
		"SELECT ei.id.sequenceLength "
			+ "FROM embl_identification ei "
			+ "WHERE ei.id.primaryAc = ?";

	public static final String getTransfacGeneDetails = 
		"FROM tf_gene tfg " + "WHERE tfg.geneId = ?";

	public static final String getTransfacGeneBindingRegion = 
		"SELECT DISTINCT tfgbr.stream,tfgbr.chipId,tfgbr.geneId.geneId "
			+ "FROM tf_gene_binding_region tfgbr "
			+ "WHERE tfgbr.geneId.geneId = ?";

	public static final String getTFGeneSite = 
		"SELECT DISTINCT s.siteId "
			+ "FROM tf_site s " + "WHERE s.geneId = ";

	public static final String getTFGeneSiteAndFactor = 
		"SELECT DISTINCT t.bindingSiteId, t.factor, tf.factorName "
			+ "FROM tf_factor_binding_sites t, tf_factor tf "
			+ "WHERE tf.factorId = t.factor AND tf.factorId is not null AND t.factor is not null " +
					"AND t.geneId = ?";

	public static final String getTFGeneFactor = 
		"SELECT t.factor, tf.factorName "
			+ "FROM tf_factor_binding_sites t, tf_factor tf "
			+ "WHERE tf.factorId = t.factor AND tf.factorId is not null AND t.factor is not null " +
					"AND t.geneId = ";

	public static final String getTFGeneFragment = 
		"SELECT DISTINCT t.chipId "
			+ "FROM tf_gene_binding_region t " + "WHERE t.geneId = ";

	public static final String getTFGeneFactor2 = 
		"SELECT t.factor, tf.factorName "
		+ "FROM tf_factor_binding_sites t, tf_factor tf "
		+ "WHERE tf.factorId = t.factor AND tf.factorId is not null AND t.factor is not null " +
				"AND t.geneId = ?";

	public static final String getTFGeneFragment2 = 
		"SELECT DISTINCT t.chipId "
			+ "FROM tf_gene_binding_region t " + "WHERE t.geneId = ?";

	public static final String getFactorDetails = 
		"FROM tf_factor tf  " + "WHERE tf.factorId = ?";

	public static final String getSiteDetails = 
		"FROM tf_site tf  " + "WHERE tf.siteId = ?";

	public static final String getTransfacGeneEncodingFactor = 
		"SELECT tfgef.factorName, tfgef.information "
			+ "FROM tf_gene_encoded_factor tfgef "
			+ "WHERE tfgef.geneId = ?";

	public static final String getTransfacGeneRegulation = 
		"SELECT tfgr.condition "
			+ "FROM tf_gene_regulation tfgr "
			+ "WHERE tfgr.geneId = ?";

	public static final String getTransfacGeneSynonyms = 
		"SELECT tfgs.synonym "
			+ "FROM tf_gene_synonyms tfgs " + "WHERE tfgs.geneId.geneId = ?";

	public static final String getTFGeneFromTPGene = 
		"SELECT tpgl.databaseIdentifier "
		+ "FROM tp_gene tpg "
		+ "INNER JOIN tpg.externalDatabaseLinkId tpgl " +
		"WHERE tpgl.database = 'TRANSFAC' and tpg.geneId = ?";
	
	public static final String getOrganismForGene = 
		"SELECT kg.id.org "
			+ "FROM kegg_genes kg " + "WHERE kg.id.entry = ?";

	public static final String getOrganismForEMBLGene = 
		"SELECT eo.id.species "
			+ "FROM embl_organismdata eo " + "WHERE eo.id.primaryAc = ?";

	public static final String getOrganismForTransfacGene = 
		"SELECT tfg.organismId.latinName "
			+ "FROM tf_gene tfg "
			+ "WHERE tfg.geneId = ?";

	public static final String getOrganismForTranspathGene = 
		"SELECT tpg.organismId.latinName "
			+ "FROM tp_gene tpg "
			+ "WHERE tpg.geneId = ?";

	public static final String getExonsForGene = 
		"SELECT DISTINCT ef.id.primaryAc "
			+ "FROM embl_features ef "
			+ "WHERE ef.id.FKey = 'exon' AND ";

	/*
	 * get protein information
	 */

	public static final String getUniProtProteinStartQuery = 
		"SELECT p.uniprotId, p.description, p.species "
			+ "FROM uniprot p " + "WHERE ";

	public static final String getUniProtProteinStartQueryOrganismIndependent = 
		"SELECT DISTINCT p.uniprotId, p.description "
			+ "FROM uniprot p " + "WHERE ";

	public static final String getUniProtProteinNameByAccessionNumber = 
		"SELECT DISTINCT p.uniprotId, p.description "
			+ "FROM uniprot p "
			+ "INNER JOIN p.uniprotAccessionnumberses ua "
			+ "WHERE ua.id.accessionNumber = ";

	public static final String getHPRDProteinStartQuery = 
		"SELECT DISTINCT hps.id.hprdId, hps.proteinName "
			+ "FROM hprd_protein_sequences hps " + "WHERE ";

	public static final String getTransfacProteinStartQuery = 
		"SELECT DISTINCT tff.factorId, tff.factorName, tff.organismId.latinName "
			+ "FROM tf_factor tff "
			+ "WHERE ";

	public static final String getTransfacProteinStartQueryOrganismIndependent = 
		"SELECT DISTINCT tff.factorId, tff.factorName "
			+ "FROM tf_factor tff " + "WHERE ";

	public static final String getTFProteinFromTPProtein = 
		"SELECT tpml.databaseIdentifier "
		+"FROM tp_molecule tpm " +
		"INNER JOIN tpm.externalDatabaseLinkId tpml " +
		"WHERE tpml.database like 'transfac' and tpm.moleculeId = ?";

	public static final String getTPProteinFromTFProtein = 
		"SELECT tpm.moleculeId "
			+ "FROM tp_molecule tpm " +
			"INNER JOIN	tpm.externalDatabaseLinkId tpml " +
			"WHERE tpml.database = 'TRANSFAC' AND tpml.databaseIdentifier = ?";
	
	public static final String getUniprotProteinFromTransfacProtein = 
		"SELECT u.id.uniprotId "
			+ "FROM uniprot_dbxref u "
			+ "WHERE u.id.dbName like 'TRANSFAC' and u.id.primaryId = ";

	public static final String getUniprotProteinFromTransfacProtein2 = 
		"SELECT u.id.uniprotId "
		+ "FROM uniprot_dbxref u "
		+ "WHERE u.id.dbName like 'TRANSFAC' and u.id.primaryId = ?";

	public static final String getTransfacProteinFromUniprotProtein2 = 
		"SELECT u.id.primaryId "
			+ "FROM uniprot_dbxref u "
			+ "WHERE u.id.dbName like 'TRANSFAC' and u.id.uniprotId = ? ";

	public static final String getUniprotProteinFromHPRDProtein = 
		"SELECT u.id.uniprotId "
			+ "FROM uniprot_accessionnumbers u, hprd_hprd_id_mapping hhm "
			+ "WHERE hhm.swissprotId = u.id.accessionNumber AND hhm.swissprotId is not null AND u.id.accessionNumber is not null " +
					"AND hhm.hprdId = ?";

	public static final String getHPRDProteinFromUniprotProtein = 
		"SELECT hhm.hprdId "
			+ "FROM uniprot_accessionnumbers u, hprd_hprd_id_mapping hhm "
			+ "WHERE hhm.swissprotId = u.id.accessionNumber AND hhm.swissprotId is not null AND u.id.accessionNumber is not null " +
					"AND u.id.uniprotId = ?";

	public static final String getHPRDProteinInteractor = 
		"SELECT h.interactor2HprdId, h.interactor2GeneSymbol "
			+ "FROM hprd_protein_protein h "
			+ "WHERE h.interactor1HprdId = ?";

	public static final String getTFProteinInteractor = 
		"SELECT t.factor.factorId, t.factor.factorName, t.factorComplex, t.complextName "
			+ "FROM tf_factor_complexes t "
			+ "WHERE t.factorComplex = ";

	public static final String getProteinFromGene = 
		"SELECT u.id.uniprotId, u.id.description "
			+ "FROM kegg_genes_dblinks l, uniprot u "
			+ "INNER JOIN u.uniprotAccessionnumberses a "
			+ "WHERE a.id.accessionNumber = l.identifier AND a.id.accessionNumber is not null AND l.identifier is not null " +
					"AND l.entry = ?";

	// ud.primary_id is a gene id; write a ' ' befor it
	public static final String getProteinFromEMBLGene = 
		"SELECT u.id.uniprotId, u.id.description "
			+ "FROM uniprot u "
			+ "INNER JOIN  u.uniprotDbxrefs ud "
			+ "WHERE ud.id.dbName like 'EMBL' AND ";

	public static final String getProteinFromTPCompound = 
		"SELECT ua.id.uniprotId "
			+ "FROM tp_molecule tpm, uniprot_accessionnumbers ua "
			+ "INNER JOIN tpm.externalDatabaseLinkId tpml "
			+ "WHERE ua.id.accessionNumber = tpml.databaseIdentifier " +
					"AND ua.id.accessionNumber is not null AND tpml.databaseIdentifier is not null " +
					"AND tpml.database LIKE 'swissprot' AND tpm.moleculeId = ?";

	public static final String getProteinFromTPCompound2 = 
		"SELECT ua.id.uniprotId "
		+ "FROM tp_molecule tpm, uniprot_accessionnumbers ua "
		+ "INNER JOIN tpm.externalDatabaseLinkId tpml "
		+ "WHERE ua.id.accessionNumber = tpml.databaseIdentifier " +
				"AND ua.id.accessionNumber is not null AND tpml.databaseIdentifier is not null " +
				"AND tpml.database LIKE 'swissprot' AND tpm.moleculeId = ";
	
	public static final String getProteinFromTransfacGene = 
		"SELECT tff.factorId, tff.factorName "
			+ "FROM tf_gene_encoded_factor tff "
			+ "WHERE tff.geneId.geneId = ? ";

	public static final String getProteinFromTransfacGene2 = 
		"SELECT tff.factorId, tff.factorName "
		+ "FROM tf_gene_encoded_factor tff "
		+ "WHERE tff.geneId.geneId =  ";

	public static final String getProteinFromTranspathGene = 
		"SELECT tff.factorId, tff.factorName "
			+ "FROM tf_gene_encoded_factor tff, tp_gene tpg "
			+ "INNER JOIN tpg.externalDatabaseLinkId tpgl "
			+ "WHERE tff.geneId.geneId = tpgl.databaseIdentifier " +
					"AND tff.geneId.geneId is not null AND tpgl.databaseIdentifier is not null " +
					"AND tpgl.database LIKE 'TRANSFAC' AND tff.geneId.geneId = ? ";

	public static final String getProteinFromTranspathGene2 = 
		"SELECT tff.factorId, tff.factorName "
		+ "FROM tf_gene_encoded_factor tff, tp_gene tpg "
		+ "INNER JOIN tpg.externalDatabaseLinkId tpgl "
		+ "WHERE tff.geneId.geneId = tpgl.databaseIdentifier " +
				"AND tff.geneId.geneId is not null AND tpgl.databaseIdentifier is not null " +
				"AND tpgl.database LIKE 'TRANSFAC' AND tff.geneId.geneId =  ";


	public static final String getHPRDProtein = 
		"SELECT hhim.hprdId "
			+ "FROM hprd_hprd_id_mapping hhim, uniprot_accessionnumbers ud "
			+ "WHERE ud.id.accessionNumber = hhim.swissprotId AND ud.id.accessionNumber is not null AND hhim.swissprotId is not null " +
					"AND ud.id.uniprotId = ? ";

	public static final String getHPRDProteinIsoformen = 
		"SELECT count(hsi.id.isoformId) "
			+ "FROM hprd_sequence_information hsi " + "WHERE hsi.id.hprdId = ? ";

	public static final String getHPRDProteinFromGene = 
		"SELECT hhim.hprdId, hhim.mainName "
			+ "FROM hprd_hprd_id_mapping hhim "
			+ "WHERE hhim.entrezgeneId = ? ";

	public static final String getHPRDProteinFromGene2 = 
		"SELECT hhim.hprdId, hhim.mainName "
		+ "FROM hprd_hprd_id_mapping hhim "
		+ "WHERE hhim.entrezgeneId = ";

	public static final String getHPRDProteinExpression = 
		"SELECT hte.expressionTerm "
			+ "FROM hprd_tissue_expressions hte " + "WHERE hte.hprdId = ? ";

	public static final String getHPRDProteinProzess = 
		"SELECT h.name "
			+ "FROM hprd_gene_ontology_2_bio_process_term h "
			+ "WHERE h.id.hprdId = ? ";

	public static final String getHPRDProteinCellComponent = 
		"SELECT h.name "
			+ "FROM hprd_gene_ontology_2_cell_component_term h "
			+ "WHERE h.id.hprdId = ? ";

	public static final String getHPRDProteinMolecularFunktion = 
		"SELECT h.name "
			+ "FROM hprd_gene_ontology_2_mol_funktion_term h "
			+ "WHERE h.id.hprdId = ? ";

	// write ' ' in front of ud.primary_id
	public static final String getProteinFromDisease = 
		"SELECT ud.id.uniprotId, u.description "
		+ "FROM uniprot u " +
		"INNER JOIN u.uniprotDbxrefs ud "
		+ "WHERE ud.id.dbName = 'MIM' AND ";

	public static final String getHPRDProteinFromDisease = 
		"SELECT hhim.hprdId, hhim.mainName "
			+ "FROM hprd_hprd_id_mapping hhim "
			+ "WHERE hhim.omimId = ?";

	public static final String getHPRDProteinFromDisease2 = 
		"SELECT hhim.hprdId, hhim.mainName "
		+ "FROM hprd_hprd_id_mapping hhim "
		+ "WHERE hhim.omimId = ";
	
	// write ' ' in front of ud.primary_id
	public static final String getProteinFromGO = 
		"SELECT ud.id.uniprotId, u.description "
		+ "FROM uniprot u " +
		"INNER JOIN u.uniprotDbxrefs ud "
		+ "WHERE ud.id.primaryId = ?";

	public static final String getHPRDProteinFromGO = 
		"Select hprd_id "
			+ "FROM (SELECT * FROM hprd_gene_ontology_2_mol_funktion_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_cell_component_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_bio_process_term) tabelle "
			+ "WHERE go_number = ?";
	//TODO: Union

	public static final String getProteinFromEnzyme = 
		"SELECT ua.id.uniprotId "
			+ "FROM enzyme_enzyme2uniprot eeu, uniprot_accessionnumbers ua "
			+ "WHERE ua.id.accessionNumber = eeu.id.uniprotId " +
					"AND ua.id.accessionNumber is not null AND eeu.id.uniprotId is not null " +
					"AND eeu.id.enzymeId = ?";

	public static final String getProteinFromEnzyme2 = 
		"SELECT ua.id.uniprotId "
		+ "FROM enzyme_enzyme2uniprot eeu, uniprot_accessionnumbers ua "
		+ "WHERE ua.id.accessionNumber = eeu.id.uniprotId " +
				"AND ua.id.accessionNumber is not null AND eeu.id.uniprotId is not null " +
				"AND eeu.id.enzymeId = ";

	public static final String getProteinDetails = 
		"FROM uniprot u " + "WHERE u.id.uniprotId = ?";

	public static final String getProteinDetailsByAccessionNumber = 
			"FROM uniprot u "
			+ "INNER JOIN u.uniprotAccessionnumberses ua "
			+ "WHERE ua.id.accessionNumber = ?";

	public static final String getProteinAccessionnumber = 
		"FROM uniprot_accessionnumbers u " + "WHERE u.id.uniprotId = ?";

	public static final String getUniProtProteinID = 
			"FROM uniprot_accessionnumbers u " + "WHERE u.id.accessionNumber = ?";

	public static final String getProteinGeneName = 
			"FROM uniprot_genenames u " + "WHERE u.id.uniprotId = ?";

	public static final String getProteinGeneNameByAccessionNumber = 
		"FROM uniprot_genenames u "
			+ "INNER JOIN u.uniprot.uniprotAccessionnumberses uan "
			+ "WHERE uan.id.accessionNumber = ?";
	
	public static final String getProteinGeneSynonyms = 
			"FROM uniprot_genesynonyms u " + "WHERE u.id.uniprotId = ?";

	public static final String getProteinGeneSynonymsByAccessionNumber = 
		"FROM uniprot_genesynonyms u "
			+ "INNER JOIN u.uniprot.uniprotAccessionnumberses uan "
			+ "WHERE uan.id.accessionNumber = ?";

	public static final String getProteinFromFactor = 
		"SELECT t.link.accessionNumber "
			+ "FROM tf_factor2db_links t "
			+ "WHERE t.link.databaseName LIKE 'swissprot' and factor.factorId = ";

	public static final String getProteinPDBs = 
		"SELECT ud.id.primaryId "
			+ "FROM uniprot_dbxref ud "
			+ "WHERE ud.id.dbName = 'PDB' and ud.id.uniprotId = ?";

	public static final String getProteinPDBsByAccessionNumber = 
		"SELECT ud.id.primaryId "
			+ "FROM uniprot_dbxref ud "
			+ "INNER JOIN ud.uniprot.uniprotAccessionnumberses uan "
			+ "WHERE ud.id.dbName = 'PDB' and uan.id.accessionNumber = ?";

	public static final String getTransfacProteinDetails = 
		"FROM tf_factor tff " + "WHERE tff.factorId = ?";

	public static final String getTFProteinSynonyms = 
		"SELECT tf.factorSynonym "
			+ "FROM tf_factor_synonyms tf " + "WHERE tf.factor.factorId = ?";

	public static final String getTPProteinComplexName = 
		"SELECT tpmc.complex "
			+ "FROM tp_molecule_complex tpmc "
			+ "WHERE tpmc.moleculeId.moleculeId = ?";

	public static final String getTPProteinComment = 
		"SELECT tpmc.comment "
			+ "FROM tp_molecule_comments tpmc "
			+ "WHERE tpmc.moleculeId.moleculeId = ?";

	public static final String getTPProteinSuperfamily = 
		"SELECT tpms.superfamilyId, tpms.superfamaly "
			+ "FROM tp_molecule_superfamalies tpms "
			+ "WHERE tpms.moleculeId.moleculeId = ?";

	public static final String getTPProteinSubfamily = 
		"SELECT tpms.superfamilyId, tpms.superfamaly "
			+ "FROM tp_molecule_subfamilies tpms "
			+ "WHERE tpms.moleculeId.moleculeId = ?";

	public static final String getTFProteinFeatures = 
		"SELECT tf.feature "
			+ "FROM tf_factor_features tf " + "WHERE tf.factor.factorId = ?";

	public static final String getFragmentsOfFactor = 
		"SELECT tfbf.fragmentId.fragmentId "
			+ "FROM tf_fragment_binding_factor tfbf "
			+ "WHERE tfbf.factor.factorId = ";

	public static final String getFragmentsOfFactor2 = 
		"SELECT tfbf.fragmentId.fragmentId "
		+ "FROM tf_fragment_binding_factor tfbf "
		+ "WHERE tfbf.factor.factorId = ?";

	public static final String getMatrixOfSite = 
		"SELECT t.matrixId "
			+ "FROM tf_site_matrices t " + "WHERE t.siteId.siteId = ";

	public static final String getMatrixOfFactor = 
		"SELECT t.matrixId.matrixId "
			+ "FROM tf_matrix_binding_factor t "
			+ "WHERE t.factor.factorId = ";

	public static final String getSiteOfFactor = 
		"SELECT t.siteId.siteId "
			+ "FROM tf_site_binding_factor t "
			+ "WHERE t.factor.factorId = ";

	public static final String getSiteOfFactor2 = 
		"SELECT DISTINCT t.siteId.siteId "
		+ "FROM tf_site_binding_factor t "
		+ "WHERE t.factor.factorId = ?";

	public static final String getOrganismForProtein = 
		"SELECT u.species "
			+ "FROM uniprot u " + "WHERE u.uniprotId = ?";

	/*
	 * get enzyme information
	 */

	public static final String getEnzymeStartQuery = 
		"SELECT DISTINCT ke.entry, ke.sysname, bo.organismName  "
			+ "FROM kegg_enzyme ke , brenda_synonyms bs "
			+ "INNER JOIN bs.organism bo "
			+ "WHERE bs.enzyme.ecNumber = ke.entry AND bs.enzyme.ecNumber is not null AND ke.entry is not null " +
					"AND ";

	public static final String getEnzymeStartQueryOrganismIndependent = 
		"SELECT DISTINCT ke.entry, ke.sysname "
			+ "FROM kegg_enzyme ke , brenda_synonyms bs "
			+ "WHERE bs.enzyme.ecNumber = ke.entry AND bs.enzyme.ecNumber is not null AND ke.entry is not null " +
					"AND ";

	public static final String getTPEnzymeFromKEGGEnzyme = 
		"SELECT tpm.moleculeId "
			+ "FROM tp_molecule tpm " +
			"INNER JOIN tpm.externalDatabaseLinkId tpml " 
			+ "WHERE tpml.database like 'brenda' and tpml.databaseIdentifier = ?";

	public static final String getKEGGEnzymeFromTPEnzyme = 
		"SELECT tpml.databaseIdentifier "
		+ "FROM tp_molecule tpm " +
		"INNER JOIN tpm.externalDatabaseLinkId tpml " 
		+ "WHERE tpml.database like 'brenda' and tpm.moleculeId= ?";


	public static final String getEnzymeFromGene = 
		"SELECT ge.id.enzyme, g.sysname "
			+ "FROM kegg_genes_enzyme ge, kegg_enzyme g "
			+ "WHERE g.entry = ge.id.enzyme " +
					"AND ge.id.entry=?";

	public static final String getEnzymeFromTranspathGene = 
		"SELECT ge.id.enzyme, ke.sysname "
			+ "FROM kegg_enzyme ke, kegg_genes_enzyme ge, tp_gene tpg "
			+ "INNER JOIN  tpg.externalDatabaseLinkId tpgl "
			+ "WHERE ke.entry = ge.id.enzyme AND tpgl.databaseIdentifier = ge.id.entry " +
					"AND tpgl.database = 'ENTREZGENE' and tpg.geneId=?";

	public static final String getEnzymeFromTranspathGene2 = 
		"SELECT ge.id.enzyme, ke.sysname "
		+ "FROM kegg_enzyme ke, kegg_genes_enzyme ge, tp_gene tpg "
		+ "INNER JOIN  tpg.externalDatabaseLinkId tpgl "
		+ "WHERE ke.entry = ge.id.enzyme AND tpgl.databaseIdentifier = ge.id.entry " +
				"AND tpgl.database = 'ENTREZGENE' and tpg.geneId=";
	
	public static final String getEnzymeFromTranspathCompound = 
		"SELECT tpml.databaseIdentifier "
		+ "FROM tp_molecule tpm " +
		"INNER JOIN tpm.externalDatabaseLinkId tpml "
		+ "WHERE tpml.database LIKE 'BRENDA' AND tpm.moleculeId = ";

	public static final String getEnzymeFromTranspathCompound2 = 
		"SELECT tpml.databaseIdentifier "
		+ "FROM tp_molecule tpm " +
		"INNER JOIN tpm.externalDatabaseLinkId tpml "
		+ "WHERE tpml.database LIKE 'BRENDA' AND tpm.moleculeId = ?";

	public static final String getEnzymeFromTransfacGene = 
		"SELECT t.link.identifier "
			+ "FROM tf_gene2db_links t "
			+ "WHERE t.link.databaseName = 'BRENDA' AND t.geneId.geneId = ?";

	public static final String getEnzymeFromTransfacGene2 =
		"SELECT t.link.identifier "
		+ "FROM tf_gene2db_links t "
		+ "WHERE t.link.databaseName = 'BRENDA' AND t.geneId.geneId = ?";

	public static final String getEnzymeFromGeneOrganismSpecific = 
		"SELECT ge.id.enzyme, g.sysname, ge.id.org "
			+ "FROM kegg_genes_enzyme ge, kegg_enzyme g "
			+ "WHERE g.entry = ge.id.enzyme AND ge.id.entry=?";

	public static final String getEnzymeFromProtein = 
		"SELECT eeu.id.enzymeId "
			+ "FROM enzyme_enzyme2uniprot eeu, uniprot_accessionnumbers ua "
			+ "WHERE ua.id.accessionNumber = eeu.id.uniprotId AND ua.id.uniprotId = ?";

	public static final String getEnzymeFromProtein2 = 
		"SELECT eeu.id.enzymeId "
		+ "FROM enzyme_enzyme2uniprot eeu, uniprot_accessionnumbers ua "
		+ "WHERE ua.id.accessionNumber = eeu.id.uniprotId AND ua.id.uniprotId = ";

	public static final String getEnzymeFromPathway = 
		"SELECT ke.entry, ke.sysname, kp.org "
		+"FROM kegg_enzyme ke, kegg_pathway kp " +
		"INNER JOIN ke.keggEnzymePathwaies kep "
		+ "WHERE kp.number = kep.id.number AND kp.pathwayName = ?";

	public static final String getEnzymeFromPathway2 = 
		"SELECT ke.entry "
		+"FROM kegg_enzyme ke, kegg_pathway kp " +
		"INNER JOIN ke.keggEnzymePathwaies kep "
		+ "WHERE kp.number = kep.id.number AND kp.pathwayName = ";

	public static final String getEnzymeFromPathwayOrganismSpecific = 
		"SELECT ke.entry, ke.sysname "
			+ "FROM kegg_enzyme ke "
			+ "INNER JOIN ke.keggEnzymePathwaies kep "
			+ "WHERE kep.id.number = ?";

	public static final String getEnzymeFromGlycan = 
		"SELECT kge.id.enzyme, ke.sysname "
			+ "FROM kegg_glycan_enzyme kge, kegg_enzyme ke "
			+ "WHERE ke.entry = kge.id.enzyme AND ke.entry is not null AND kge.id.entry = ?";

	public static final String getEnzymeFromGlycanOrganismSpecific = 
		"SELECT DISTINCT kge.id.enzyme, ke.sysname, ge.id.org "
			+ "FROM kegg_glycan_enzyme kge, kegg_genes_enzyme ge, kegg_enzyme ke "
			+ "WHERE kge.id.enzyme = ge.id.enzyme AND ke.entry = kge.id.enzyme " +
					"AND ke.entry is not null AND kge.id.entry = ?";

	public static final String getEnzymeFromReaction = 
		"SELECT kre.id.enzyme, ke.sysname "
			+ "FROM kegg_reaction_enzyme kre, kegg_enzyme ke "
			+ "WHERE ke.entry = kre.id.enzyme AND ke.entry is not null AND kre.id.entry = ?";

	public static final String getEnzymeFromReactionOrganismSpecific = 
		"SELECT DISTINCT kre.id.enzyme, ke.sysname, ge.id.org "
			+ "FROM kegg_reaction_enzyme kre, kegg_genes_enzyme ge, kegg_enzyme ke "
			+ "WHERE kre.id.enzyme = ge.id.enzyme AND ke.entry = kre.id.enzyme AND ke.entry is not null " +
					"AND kre.id.entry = ?";

	public static final String getEnzymeFromGO = 
		"SELECT DISTINCT gd.xrefKey, ke.sysname "
			+ "FROM go_term gt, go_term_dbxref gtd, go_dbxref gd, kegg_enzyme ke "
			+ "WHERE gtd.id.termId = gt.id AND gd.id = gtd.id.dbxrefId AND gd.xrefDbname = 'EC' AND ke.entry = gd.xrefKey " +
					"AND gt.acc = ?";

	public static final String getEnzymeFromGOOrganismSpecific = 
		"SELECT DISTINCT gd.xrefKey, ke.sysname, kge.id.org "
			+ " FROM go_term gt, go_term_dbxref gtd, go_dbxref gd, kegg_genes_enzyme kge, kegg_enzyme ke "
			+ "WHERE gtd.id.termId = gt.id AND gd.id = gtd.id.dbxrefId AND gd.xrefDbname = 'EC' AND kge.id.enzyme = gd.xrefKey " +
					"AND ke.entry = gd.xrefKey AND gt.acc = ?";

	public static final String getEnzymeFromCompound = 
		"SELECT kce.id.enzyme, ke.sysname "
			+ "FROM kegg_compound_enzyme kce, kegg_enzyme ke "
			+ "WHERE ke.entry = kce.id.enzyme AND kce.id.entry = ?";

	public static final String getEnzymeFromCompound2 =
		"SELECT kce.id.enzyme, ke.sysname "
		+ "FROM kegg_compound_enzyme kce, kegg_enzyme ke "
		+ "WHERE ke.entry = kce.id.enzyme AND kce.id.entry = ";
	
	public static final String getEnzymeFromUniprotProtein = 
		"SELECT eeu.id.enzymeId "
			+ "FROM enzyme_enzyme2uniprot eeu, uniprot_accessionnumbers ua "
			+ "WHERE ua.id.accessionNumber = eeu.id.uniprotId AND ua.id.uniprotId = ?";

	public static final String getEnzymeFromCompoundOrganismSpecific = 
		"SELECT kce.id.enzyme, ke.sysname, kge.id.org "
			+ "FROM kegg_compound_enzyme kce, kegg_genes_enzyme kge, kegg_enzyme ke "
			+ "WHERE kge.id.enzyme = kce.id.enzyme AND ke.entry = kce.id.enzyme " +
					"AND kce.id.entry = ?";

	public static final String getEnzymeClass = 
		"SELECT kec.id.class_ "
			+ "FROM kegg_enzyme_class kec " + "WHERE kec.id.entry = ?";

	public static final String getEnzymeOrthology = 
		"FROM kegg_enzyme_orthology keo "
			+ "WHERE keo.id.entry = ?";

	public static final String getEnzymeSynonyms = 
		"SELECT bs.synonym "
			+ "FROM brenda_synonyms bs " + "WHERE bs.enzyme = ?";

	public static final String getEnzymeSubstrates =
		"FROM kegg_enzyme_substrate kes "
			+ "WHERE kes.id.entry = ?";

	public static final String getEnzymeProducts =
			"FROM kegg_enzyme_product kep " + "WHERE kep.id.entry = ?";

	public static final String getEnzymeCofactors =
			"FROM kegg_enzyme_cofactor kec " + "WHERE kec.id.entry = ?";

	public static final String getEnzymeInhibitors =
		"FROM kegg_enzyme_inhibitor kei "
			+ "WHERE kei.id.entry = ?";

	public static final String getEnzymeEffectors =
			"FROM kegg_enzyme_effector kee " + "WHERE kee.id.entry = ?";

	public static final String getEnzymePDBs = 
		"SELECT kes.id.structures "
			+ "FROM kegg_enzyme_structures kes "
			+ "WHERE kes.id.entry = ?";

	public static final String getEnzymeDetails = 
		"SELECT ke.comment, ke.reference, ke.sysname "
			+ "FROM kegg_enzyme ke " + "WHERE ke.entry = ?";

	public static final String getEnzymeDBLinks = 
			"FROM kegg_enzyme_dblinks ked " + "WHERE ked.id.entry = ?";

	public static final String getOrganismForEnzyme = 
		"SELECT DISTINCT kge.id.org "
			+ "FROM kegg_genes_enzyme kge " + "WHERE kge.id.enzyme = ?";

	public static final String getNCBIIDForEnzyme = 
		"SELECT DISTINCT u.taxonomy "
			+ "FROM uniprot u, enzyme_enzyme2uniprot eu "
			+ "INNER JOIN u.uniprotAccessionnumberses ua "
			+ "WHERE eu.id.uniprotId = ua.id.accessionNumber AND ";

	/*
	 * get compound information
	 */

	public static final String getCompoundStartQuery = 
		"SELECT DISTINCT kcn.entry, kcn.name, kc.organism "
			+ "FROM kegg_compound kc, kegg_taxonomy t "
			+ "INNER JOIN kc.keggCompoundNames kcn "
			+ "WHERE kc.organism = t.org AND ";

	public static final String getCompoundStartQueryOrganismIndependent = 
		"SELECT DISTINCT kc.entry, kcn.name, kc.organism "
			+ "FROM kegg_compound kc "
			+ "INNER JOIN kc.keggCompoundNames kcn "
			+ "WHERE ";

	public static final String getCompoundFromPathwayByNumber = 
		"SELECT DISTINCT kpc.id.entry "
			+ "FROM kegg_pathway_compound kpc, kegg_compound kc "
			+ "WHERE kc.entry = kpc.id.entry AND kpc.id.number = ?";

	public static final String getCompoundFromPathway = 
		"SELECT DISTINCT kpc.id.entry "
			+ "FROM kegg_pathway_compound kpc, kegg_pathway kp "
			+ "WHERE kp.number = kpc.id.number AND kp.org = kpc.id.organismus " +
					"AND kp.pathwayName = ";

	public static final String getCompoundFromPathway2 = 
		"SELECT DISTINCT kpc.id.entry "
		+ "FROM kegg_pathway_compound kpc, kegg_pathway kp "
		+ "WHERE kp.number = kpc.id.number AND kp.org = kpc.id.organismus " +
				"AND kp.pathwayName = ?";

	public static final String getCompoundFromTRANSPATHPathway = 
		"SELECT DISTINCT tpmi.moleculeId, tpm.name "
			+ "FROM tp_pathway_molecules_involved tpmi, tp_molecule tpm "
			+ "WHERE tpm.moleculeId = tpmi.moleculeId AND tpmi.pathwayId.pathwayId = ?";

	public static final String getCompoundFromTPPathway2 = 
		"SELECT tpmi.moleculeId "
			+ "FROM tp_pathway_molecules_involved tpmi, tp_molecule_synonyms ms "
			+ "WHERE ms.moleculeId.moleculeId = tpmi.moleculeId AND tpmi.pathwayId.pathwayId = ";

	public static final String getCompoundFromPathwayOrganismSpecific = 
		"SELECT DISTINCT kpc.id.entry "
			+ "FROM kegg_pathway_compound kpc, kegg_compound kc, kegg_pathway kp "
			+ "WHERE kc.entry = kpc.id.entry AND kp.org = kpc.id.organismus AND kp.number = kpc.id.number " +
					"AND kp.pathwayName = ?";

	public static final String getCompoundFromReaction = 
		"SELECT DISTINCT kcr.id.entry "
			+ "FROM kegg_compound_reaction kcr, kegg_compound kp "
			+ "WHERE kp.entry = kcr.id.entry AND kp.entry is not null " +
					"AND kcr.id.reaction = ?";

	public static final String getCompoundFromFactor = 
		"SELECT t.link.accessionNumber "
			+ "FROM tf_factor2db_links t "
			+ "WHERE t.link.databaseName LIKE 'transpath' and t.factor.factorId = ";

	public static final String getTranspathCompoundFromEnzyme = 
		"SELECT tpm.moleculeId "
			+ "FROM tp_molecule tpm "
			+ "INNER JOIN tpm.externalDatabaseLinkId tpml "
			+ "WHERE tpml.database LIKE 'BRENDA' AND tpml.databaseIdentifier = ?";

	public static final String getCompoundFromReactionOrganismSpecific = 
		"SELECT DISTINCT kcr.id.entry, kpc.id.organismus "
			+ "FROM kegg_compound_reaction kcr, kegg_pathway_compound kpc, kegg_compound kp "
			+ "WHERE kpc.id.entry = kcr.id.entry AND kp.entry = kcr.id.entry AND kp.entry is not null " +
					"AND ";

	public static final String getCompoundFromReactionPair = 
		"SELECT DISTINCT krc.id.compound "
			+ "FROM kegg_rpair_compound krc, kegg_compound kc "
			+ "WHERE kc.entry = krc.id.compound AND kc.entry is not null AND krc.id.entry = ?";

	public static final String getTRANSPATHCompoundFromGene = 
		"SELECT DISTINCT tpgd.productId "
			+ "FROM tp_gene_product tpgd, tp_molecule tpm "
			+ "WHERE tpm.moleculeId = tpgd.productId " +
					"AND tpgd.geneId.geneId = ?";

	public static final String getTRANSPATHCompoundFromGene2 = 
		"SELECT DISTINCT tpgd.productId "
		+ "FROM tp_gene_product tpgd, tp_molecule tpm "
		+ "WHERE tpm.moleculeId = tpgd.productId " +
				"AND tpgd.geneId.geneId = ";

	public static final String getCompoundFromTPPathway = 
		"SELECT tpm.moleculeId, tpm.name, tpo.latinName "
			+ "FROM tp_pathway_molecules_involved t, tp_molecule tpm, tp_organism tpo "
			+ "WHERE tpm.moleculeId = t.moleculeId AND cast(tpo.organismId as string) = tpm.organismId " +
					"AND t.pathwayId.pathwayId = ?";

	public static final String getCompoundFromTPPathwayOrganismIndependent = 
		"SELECT tpm.moleculeId, tpm.name "
			+ "FROM tp_pathway_molecules_involved t, tp_molecule tpm "
			+ "WHERE tpm.moleculeId = t.moleculeId AND t.pathwayId.pathwayId = ?";

	public static final String getCompoundFromReactionPairOrganismSpecific = 
		"SELECT DISTINCT krc.id.compound, kpc.id.organismus "
			+ "FROM kegg_rpair_compound krc, kegg_pathway_compound kpc, kegg_compound kc "
			+ "WHERE kpc.id.entry = krc.id.compound AND kc.entry = krc.id.compound AND kc.entry is not null AND ";

	public static final String getCompoundFromEnzyme = 
		"SELECT  DISTINCT kce.id.entry "
			+ "FROM kegg_compound_enzyme kce, kegg_compound kc "
			+ "WHERE kc.entry = kce.id.entry AND kc.entry is not null AND kce.id.enzyme = ?";

	public static final String getCompoundFromEnzyme2 = 
		"SELECT  DISTINCT kce.id.entry "
			+ "FROM kegg_compound_enzyme kce " + "WHERE kce.id.enzyme = ";

	public static final String getCompoundName = 
		"SELECT kcn.id.name "
			+ "FROM kegg_compound_name kcn " + "WHERE kcn.id.entry = ?";

	public static final String getTPCompoundName = 
		"SELECT tpm.name "
			+ "FROM tp_molecule tpm " + "WHERE tpm.moleculeId = ?";

	public static final String getTPCompoundStartQuery = 
		"SELECT tpm.moleculeId "
			+ "FROM tp_molecule tpm " + "WHERE ";

	public static final String getTPCompound = 
		"SELECT tpms.moleculeId.moleculeId "
			+ "FROM tp_molecule_synonyms tpms "
			+ "WHERE tpms.synonym = ?";

	public static final String getTPCompoundSynonyms = 
		"SELECT tpms.synonym "
			+ "FROM tp_molecule_synonyms tpms " + "WHERE tpms.moleculeId.moleculeId = ?";

	public static final String getCompoundSynonyms = 
		"SELECT tpms.synonym "
			+ "FROM tp_molecule_synonyms tpms "
			+ "WHERE tpms.moleculeId.moleculeId = ?";

	public static final String getCompoundDetails = 
		"FROM kegg_compound kc " + "WHERE kc.entry = ?";

	public static final String getTranspathCompoundDetails = 
		"FROM tp_molecule tpm " + "WHERE tpm.moleculeId = ?";

	public static final String getTRANSPATHCompoundFromGeneOntology = 
		"SELECT tpgo.moleculeId.moleculeId "
			+ "FROM tp_molecule_go_process tpgo "
			+ "WHERE tpgo.goId = ?";

	public static final String getTRANSPATHCompoundFromGeneOntology2 =
		"SELECT tpgo.moleculeId.moleculeId "
		+ "FROM tp_molecule_go_process tpgo "
		+ "WHERE tpgo.goId = ";

	public static final String getCompoundFeature = 
		"SELECT tpm.feature "
			+ "FROM tp_molecule_features tpm "
			+ "WHERE tpm.moleculeId = ?";

	public static final String getCompoundComment = 
		"SELECT tpm.comment "
			+ "FROM tp_molecule_comments tpm "
			+ "WHERE tpm.moleculeId.moleculeId = ?";

	public static final String getCompoundSuperfamily =
		"FROM tp_molecule_superfamalies tpm "
			+ "WHERE tpm.moleculeId.moleculeId = ?";

	public static final String getCompoundSubfamily = 
			"FROM tp_molecule_subfamilies tpm "
			+ "WHERE tpm.moleculeId.moleculeId = ?";

	public static final String getOrganismForCompound = 
		"SELECT DISTINCT kpc.id.organismus "
			+ "FROM kegg_pathway_compound kpc "
			+ "WHERE kpc.id.entry = ?";

	public static final String getOrganismForTranspathCompound = 
		"SELECT tpm.organismId.organismId "		
			+ "FROM tp_molecule tpm "
			+ "WHERE tpm.moleculeId = ?";

	public static final String getCompoundFromDisease = 
		"SELECT tpm.moleculeId "
			+ "FROM tp_molecule tpm "
			+ "INNER JOIN tpm.externalDatabaseLinkId tpml "
			+ "WHERE tpml.database = 'MIM' AND tpml.databaseIdentifier = ?";

	public static final String getCompoundFromDisease2 = 
		"SELECT tpm.moleculeId "
		+ "FROM tp_molecule tpm "
		+ "INNER JOIN tpm.externalDatabaseLinkId tpml "
		+ "WHERE tpml.database = 'MIM' AND tpml.databaseIdentifier = ";

	public static final String getCompoundFromTranspathReaction = 
		"SELECT molecule_id "
			+ "FROM (SELECT * FROM tp_reaction_molecule_up "
			+ "UNION SELECT * FROM tp_reaction_molecule_down) tabelle "
			+ "WHERE reaction_id = ";
	//TODO: Union
	
	public static final String getTranspathReactionFromCompound = 
		"SELECT reaction_id "
			+ "FROM (SELECT * FROM tp_reaction_molecule_up "
			+ "UNION SELECT * FROM tp_reaction_molecule_down) tabelle "
			+ "WHERE molecule_id = ?";
	//TODO: Union
	
	public static final String getCompoundFromProtein = 
		"SELECT tpm.moleculeId "
			+"FROM tp_molecule tpm, uniprot_accessionnumbers ua "
			+ "INNER JOIN tpm.externalDatabaseLinkId tpml "
			+ "WHERE ua.id.accessionNumber = tpml.databaseIdentifier " +
					"AND tpml.database LIKE 'swissprot' AND ua.id.uniprotId = ?";

	public static final String getCompoundFromProtein2 = 		
		"SELECT tpm.moleculeId "
		+"FROM tp_molecule tpm, uniprot_accessionnumbers ua "
		+ "INNER JOIN tpm.externalDatabaseLinkId tpml "
		+ "WHERE ua.id.accessionNumber = tpml.databaseIdentifier " +
				"AND tpml.database LIKE 'swissprot' AND ua.id.uniprotId = ";

	/*
	 * get reaction information
	 */

	public static final String getReactionStartQuery = 
		"SELECT DISTINCT kr.entry, kr.name, kpr.id.organismus "
			+ "FROM kegg_reaction kr, kegg_pathway_reaction kpr "
			+ "WHERE kpr.id.entry = kr.entry AND ";

	public static final String getReactionStartQueryOrganismIndepenent = 
		"SELECT DISTINCT kr.entry, kr.name "
			+ "FROM kegg_reaction kr " + "WHERE ";

	public static final String getTranspathReactionStartQuery = 
		"SELECT DISTINCT tpr.reactionId, tpr.name "
			+ "FROM tp_reaction tpr " + "WHERE ";

	public static final String getReactionFromPathwayByNumber = 
		"SELECT DISTINCT kpr.id.entry, kr.name "
			+ "FROM kegg_pathway_reaction kpr, kegg_reaction kr "
			+ "WHERE kr.entry = kpr.id.entry AND kpr.id.number = ?";

	public static final String getTRANSPATHReactionFromPathway = 
		"SELECT DISTINCT pri.reactionId.reactionId, pri.reactionId.reaction "
			+ "FROM tp_pathway_reactions_involved pri "
			+ "WHERE pri.pathwayId.pathwayId = ?";

	public static final String getTRANSPATHReactionFromPathway2 = 
		"SELECT DISTINCT pri.reactionId.reactionId, pri.reactionId.reaction "
		+ "FROM tp_pathway_reactions_involved pri "
		+ "WHERE pri.pathwayId.pathwayId = ";
	
	public static final String getReactionFromPathway = 
		"SELECT DISTINCT kpr.id.entry, kr.name, kpr.id.organismus "
			+ "FROM kegg_pathway_reaction kpr, kegg_pathway kp, kegg_reaction kr "
			+ "WHERE kp.org = kpr.id.organismus AND kp.number = kpr.id.number AND kr.entry = kpr.id.entry " +
					"AND kp.pathwayName = ?";

	public static final String getReactionFromPathway2 = 
		"SELECT DISTINCT kpr.id.entry, kr.name, kpr.id.organismus "
		+ "FROM kegg_pathway_reaction kpr, kegg_pathway kp, kegg_reaction kr "
		+ "WHERE kp.org = kpr.id.organismus AND kp.number = kpr.id.number AND kr.entry = kpr.id.entry " +
				"AND kp.pathwayName = ";
	
	public static final String getReactionFromGlycan = 
		"SELECT DISTINCT kgr.id.reaction "
			+ "FROM kegg_glycan_reaction kgr " + "WHERE kgr.id.entry = ?";

	public static final String getReactionFromGlycan2 = 
		"SELECT DISTINCT kgr.id.reaction "
		+ "FROM kegg_glycan_reaction kgr " + "WHERE kgr.id.entry = ";

	public static final String getReactionFromGlycanOrganismSpecific = 
		"SELECT DISTINCT kgr.id.reaction, kr.name, kpr.id.organismus "
			+ "FROM kegg_glycan_reaction kgr, kegg_pathway_reaction kpr, kegg_reaction kr "
			+ "WHERE kpr.id.entry = kgr.id.reaction AND kr.entry = kgr.id.reaction AND kr.entry is not null " +
					"AND ";

	public static final String getReactionFromEnzyme = 
		"SELECT DISTINCT kre.id.entry, kr.name "
			+ "FROM kegg_reaction_enzyme kre, kegg_reaction kr "
			+ "WHERE kr.entry = kre.id.entry AND kr.entry is not null AND kre.id.enzyme = ?";

	public static final String getReactionFromEnzyme2 = 
		"SELECT DISTINCT kre.id.entry, kr.name "
		+ "FROM kegg_reaction_enzyme kre, kegg_reaction kr "
		+ "WHERE kr.entry = kre.id.entry AND kr.entry is not null AND kre.id.enzyme = ";
	
	public static final String getReactionFromEnzymeOrganismSpecific = 
		"SELECT DISTINCT kre.id.entry, kr.name, kpr.id.organismus "
			+ "FROM kegg_reaction_enzyme kre, kegg_pathway_reaction kpr, kegg_reaction kr "
			+ "WHERE kre.id.entry = kpr.id.entry AND kr.entry = kre.id.entry AND kr.entry is not null " +
					"AND ";

	public static final String getReactionFromCompound = 
		"SELECT DISTINCT kcr.id.reaction, kr.name "
			+ "FROM kegg_compound_reaction kcr, kegg_reaction kr "
			+ "WHERE kr.entry = kcr.id.reaction AND kr.entry is not null AND kcr.id.entry = ?";

	public static final String getTRANSPATHReactionUpstreamFromCompound = 
		"FROM tp_molecule_reaction_up mup " + "WHERE mup.moleculeId = ";

	public static final String getTRANSPATHReactionDownstreamFromCompound = 
		"FROM tp_molecule_reaction_down mdown "
			+ "WHERE mdown.moleculeId = ";

	public static final String getTRANSPATHReactionMoleculeDown = 
		"FROM tp_reaction_molecule_down rdown "
			+ "WHERE rdown.reactionId = ?";

	public static final String getTRANSPATHReactionMoleculeUp = 
			"FROM tp_reaction_molecule_up rup "
			+ "WHERE rup.reactionId = ?";

	public static final String getTRANSPATHReactionUpstreamFromGene = 
		"SELECT tpmrup.reactionId, tpmrup.reaction, tpmrup.moleculeId.encodingGeneId "
			+ "FROM tp_molecule_reaction_up tpmrup " + "WHERE tpmrup.moleculeId.encodingGeneId  = ";

	public static final String getTRANSPATHReactionDownstreamFromGene = 
		"SELECT tpmrdown.reactionId, tpmrdown.reaction, tpmrdown.moleculeId.encodingGeneId "
			+ "FROM tp_molecule_reaction_down tpmrdown " + "WHERE tpmrdown.moleculeId.encodingGeneId = ";

	public static final String getReactionFromCompound2 = 
		"SELECT DISTINCT kcr.id.reaction "
			+ "FROM kegg_compound_reaction kcr "
			+ "WHERE kcr.id.entry = ";

	public static final String getReactionFromCompoundOrganismSpecific = 
		"SELECT DISTINCT kcr.id.reaction, kr.name, kpr.id.organismus "
			+ "FROM kegg_compound_reaction kcr, kegg_pathway_reaction kpr, kegg_reaction kr "
			+ "WHERE kpr.id.entry = kcr.id.reaction AND kr.entry = kcr.id.reaction AND kr.entry is not null " +
					"AND ";

	public static final String getReactionFromReactionPair = 
		"SELECT DISTINCT krr.id.reaction, kr.name "
			+ "FROM kegg_rpair_reaction krr, kegg_reaction kr "
			+ "WHERE krr.id.reaction = kr.entry AND krr.id.reaction is not null AND krr.id.entry = ?";

	public static final String getReactionFromReactionPair2 = 
		"SELECT DISTINCT krr.id.reaction "
			+ "FROM kegg_rpair_reaction krr " + "WHERE krr.id.entry = ";

	public static final String getReactionFromReactionPairOrganismSpecific = 
		"SELECT DISTINCT krr.id.reaction, kr.name, kpr.id.organismus "
			+ "FROM kegg_rpair_reaction krr, kegg_pathway_reaction kpr, kegg_reaction kr "
			+ "WHERE kpr.id.entry = krr.id.reaction AND krr.id.reaction = kr.entry AND krr.id.reaction is not null " +
					"AND ";

	public static final String getReactionDetails = 
		"FROM kegg_reaction kr " + "WHERE kr.entry = ?";

	public static final String getReactionOrthology = 
		"FROM kegg_reaction_orthology kro "
			+ "WHERE kro.id.entry = ?";

	public static final String getReactionSubstrates = 
		"SELECT DISTINCT ks.substrateName "
			+ "FROM kegg_kgml_substrate ks, kegg_reaction_kgml kr "
			+ "WHERE kr.id = ks.reactionName.id AND kr.reaction = ?";

	public static final String getTRANSPATHReactionSubstrates = 
		"SELECT DISTINCT tps.moleculeId, tps.molecule "
			+ "FROM tp_reaction_molecule_up tps "
			+ "WHERE tps.reactionId.reactionId = ?";

	public static final String getReactionProducts = 
		"SELECT DISTINCT kp.productName "
			+ "FROM kegg_reaction_kgml kr "
			+ "INNER JOIN kr.products kp "
			+ "WHERE kr.reaction = ?";

	public static final String getTRANSPATHReactionProducts = 
		"SELECT DISTINCT tpp.moleculeId, tpp.molecule "
			+ "FROM tp_reaction_molecule_down tpp "
			+ "WHERE tpp.reactionId.reactionId = ?";

	public static final String getReactionType = 
		"SELECT DISTINCT rt.reactionType "
			+ "FROM kegg_reaction_kgml rt "
			+ "WHERE rt.reaction = ?";

	public static final String getTranspathReactionComment = 
		"SELECT DISTINCT tpc.comment "
			+ "FROM tp_reaction_comments tpc "
			+ "WHERE tpc.reactionId.reactionId = ?";

	public static final String getTRANSPATHReactionInhibitors = 
		"SELECT DISTINCT tpi.levelReactionId, tpi.levelReaction "
			+ "FROM tp_reaction_inhibitors tpi "
			+ "WHERE tpi.reactionId.reactionId = ?";

	public static final String getTRANSPATHReactionCatalysts = 
		"SELECT DISTINCT tpc.moleculeId, tpc.molecule "
			+ "FROM tp_reaction_catalysts tpc "
			+ "WHERE tpc.reactionId.reactionId = ?";

	public static final String getTranspathReactionDetails = 
		"SELECT DISTINCT tpr.effect, tpr.type, tpr.name "
			+ "FROM tp_reaction tpr " + "WHERE tpr.reactionId = ?";

	public static final String getOrganismForReaction = 
		"SELECT DISTINCT kpr.id.organismus "
			+ "FROM kegg_pathway_reaction kpr "
			+ "WHERE kpr.id.entry = ?";

	/*
	 * get reaction pair information
	 */

	public static final String getReactionPairStartQuery = 
		"SELECT DISTINCT kr.entry, kr.name, kpr.id.organismus "
			+ "FROM kegg_rpair kr, kegg_rpair_reaction krpr, kegg_pathway_reaction kpr, kegg_taxonomy t "
			+ "WHERE krpr.id.entry = kr.entry AND krpr.id.reaction = kpr.id.entry AND kpr.id.organismus = t.org " +
					"AND ";

	public static final String getReactionPairStartQueryOrganismIndependent = 
		"SELECT DISTINCT kr.entry, kr.name "
			+ "FROM kegg_rpair kr " + "WHERE ";

	public static final String getReactionPairFromReaction = 
		"SELECT krr.id.entry, krp.id.name "
			+ "FROM kegg_rpair_reaction krr, kegg_rpair krp "
			+ "WHERE krr.id.entry = krp.id.entry AND krr.id.entry is not null AND krr.id.reaction = ?";

	public static final String getReactionPairFromReactionOrganismSpecific = 
		"SELECT DISTINCT krr.id.entry, krp.id.name, kpr.id.organismus "
			+ "FROM kegg_rpair_reaction krr, kegg_pathway_reaction kpr, kegg_rpair krp "
			+ "WHERE kpr.id.entry = krr.id.reaction AND kpr.id.entry is not null " +
					"AND krr.id.entry = krp.id.entry AND krr.id.entry is not null " +
					"AND ";

	public static final String getReactionPairFromCompound = 
		"SELECT krc.id.entry, kr.name "
			+ "FROM kegg_rpair_compound krc, kegg_rpair kr "
			+ "WHERE kr.entry = krc.id.entry AND kr.entry is not null AND krc.id.compound = ?";

	public static final String getReactionPairFromCompound2 = 
		"SELECT krc.id.entry, kr.name "
		+ "FROM kegg_rpair_compound krc, kegg_rpair kr "
		+ "WHERE kr.entry = krc.id.entry AND kr.entry is not null AND krc.id.compound = ";

	public static final String getReactionPairFromReactionPair = 
		"SELECT krprp.id.relatedpair, kr.name "
			+ "FROM kegg_rpair_relatedpair krprp, kegg_rpair kr "
			+ "WHERE kr.entry = krprp.id.relatedpair AND kr.entry is not null AND krprp.id.entry = ?";

	public static final String getReactionPairFromReactionPair2 = 
		"SELECT krprp.id.relatedpair, kr.name "
		+ "FROM kegg_rpair_relatedpair krprp, kegg_rpair kr "
		+ "WHERE kr.entry = krprp.id.relatedpair AND kr.entry is not null AND krprp.id.entry = ";

	public static final String getReactionPairFromReactionPairOrganismSpecific = 
		"SELECT krprp.id.relatedpair, kr.name, kpr.id.organismus "
			+ "FROM kegg_rpair_relatedpair krprp, kegg_rpair_reaction krr, kegg_pathway_reaction kpr, kegg_rpair kr "
			+ "WHERE krr.id.entry = krprp.id.relatedpair AND krr.id.reaction = kpr.id.entry AND kr.entry = krprp.id.relatedpair " +
					"AND kr.entry is not null AND ";

	public static final String getReactionPairDetails =
			"FROM kegg_rpair krp " + "WHERE krp.id.entry = ?";

	public static final String getOrganismForReactionPair = 
		"SELECT DISTINCT kpr.id.organismus "
			+ "FROM kegg_pathway_reaction kpr, kegg_rpair_reaction krpr "
			+ "WHERE krpr.id.reaction = kpr.id.entry AND krpr.id.entry = ?";

	/*
	 * get glycan information
	 */

	public static final String getGlycanStartQuery = 
		"SELECT DISTINCT kg.entry, kgn.id.name, kpg.id.organismus "
			+ "FROM kegg_glycan kg "
			+ "INNER JOIN kg.keggGlycanNames kgn, kegg_pathway_glycan kpg, kegg_taxonomy t "
			+ "WHERE kpg.id.entry = kg.entry AND kpg.id.organismus = t.org " +
					"AND ";

	public static final String getGlycanStartQueryOrganismIndependent = 
		"SELECT DISTINCT kg.entry, kgn.id.name "
			+ "FROM kegg_glycan kg "
			+ "LEFT OUTER JOIN kg.keggGlycanNames kgn "
			+ "WHERE ";

	public static final String getGlycanFromPathway = 
		"SELECT kpg.id.entry, kpg.id.organismus "
			+ "FROM kegg_pathway_glycan kpg, kegg_pathway kp "
			+ "WHERE kp.number = kpg.id.number AND kp.org = kpg.id.organismus " +
					"AND kp.pathwayName = ?";

	public static final String getGlycanFromPathway2 = 
		"SELECT kpg.id.entry, kpg.id.organismus "
		+ "FROM kegg_pathway_glycan kpg, kegg_pathway kp "
		+ "WHERE kp.number = kpg.id.number AND kp.org = kpg.id.organismus " +
				"AND kp.pathwayName = ";

	public static final String getGlycanFromPathwayByNumber = 
		"SELECT DISTINCT kpg.id.entry "
			+ "FROM kegg_pathway_glycan kpg "
			+ "WHERE kpg.id.number = ?";

	public static final String getGlycanIDFromEnzyme = 
		"SELECT kge.id.entry "
			+ "FROM kegg_glycan_enzyme kge " + "WHERE kge.id.enzyme = ?";

	public static final String getGlycanFromEnzyme = 
		"SELECT DISTINCT kge.id.entry, kpg.id.organismus "
			+ "FROM kegg_glycan_enzyme kge, kegg_pathway_glycan kpg "
			+ "WHERE kpg.id.entry = kge.id.entry " +
					"AND kge.id.enzyme = ?";

	public static final String getGlycanFromEnzyme2 = 
		"SELECT DISTINCT kge.id.entry, kpg.id.organismus "
		+ "FROM kegg_glycan_enzyme kge, kegg_pathway_glycan kpg "
		+ "WHERE kpg.id.entry = kge.id.entry " +
				"AND kge.id.enzyme = ";
	
	public static final String getGlycanIDFromReaction = 
		"SELECT kgr.id.entry "
			+ "FROM kegg_glycan_reaction kgr "
			+ "WHERE kgr.id.reaction = ?";

	public static final String getGlycanFromReaction = 
		"SELECT DISTINCT kgr.id.entry , kpg.id.organismus "
			+ "FROM kegg_glycan_reaction kgr, kegg_pathway_glycan kpg "
			+ "WHERE kpg.id.entry = kgr.id.entry AND ";

	public static final String getGlycanName = 
		"SELECT kgn.id.name "
			+ "FROM kegg_glycan_name kgn " + "WHERE kgn.id.entry = ?";

	public static final String getGlycanDetails = 
		"FROM kegg_glycan kg " + "WHERE kg.entry = ?";

	public static final String getGlycanClass = 
		"SELECT kgc.id.class_ "
			+ "FROM kegg_glycan_class kgc " + "WHERE kgc.id.entry =?";

	public static final String getGlycanOrthology = 
		"FROM kegg_glycan_orthology kgo "
			+ "WHERE kgo.id.entry = ?";

	public static final String getTPGlycanFromKEGGGlycan = 
		"SELECT t.moleculeId.moleculeId "
			+ "FROM tp_molecule_synonyms t, kegg_glycan_name kgn "
			+ "WHERE kgn.id.name = t.synonym " +
					"AND kgn.id.entry = ?";

	public static final String getKEGGGlycanFromTPGlycan = 
		"SELECT kgn.id.entry "
			+ "FROM tp_molecule_synonyms t, kegg_glycan_name kgn "
			+ "WHERE kgn.id.name = t.synonym " +
					"AND t.moleculeId.moleculeId = ?";

	public static final String getOrganismForGlycan = 
		"SELECT kpg.id.organismus "
			+ "FROM kegg_pathway_glycan kpg " + "WHERE kpg.id.entry = ?";

	/*
	 * get gene ontology information
	 */
	
	public static final String getGOStartQuery = "SELECT DISTINCT gt.acc, gt.name, gs.species "
			+ "FROM go_term gt "
			+ "INNER JOIN go_species gs on gs.id = gt.id "
			+ "INNER JOIN go_term_synonym gts on gts.term_id = gt.id "
			+ "WHERE ";

	public static final String getGOStartQueryOrganismIndependent = 
		"SELECT DISTINCT gt.acc, gt.name "
			+ "FROM go_term gt, go_term_synonym gts "
			+ "WHERE gts.id.termId = gt.id " +
					"AND ";

	public static final String getGOFromEnzyme = 
		"SELECT DISTINCT gt.acc, gt.name "
			+ "FROM go_dbxref gd, go_term_dbxref gtd, go_term gt "
			+ "WHERE gd.id = gtd.id.dbxrefId AND gt.id = gtd.id.termId " +
					"AND gd.xrefKey = ?";

	public static final String getGOFromEnzyme2 = 
		"SELECT DISTINCT gt.acc, gt.name "
		+ "FROM go_dbxref gd, go_term_dbxref gtd, go_term gt "
		+ "WHERE gd.id = gtd.id.dbxrefId AND gt.id = gtd.id.termId " +
				"AND gd.xrefKey = ";

	public static final String getGOFromCompound = 
		"SELECT tpm.goId, gt.name "
			+ "FROM tp_molecule_go_process tpm, go_term gt "
			+ "WHERE gt.acc = tpm.goId AND tpm.moleculeId.moleculeId = ?";

	public static final String getGOIDFromProtein = 
		"SELECT ud.id.primaryId "
			+ "FROM uniprot_dbxref ud "
			+ "WHERE ud.id.dbName = 'GO' AND ud.id.uniprotId = ?";

	public static final String getGOIDFromProtein2 = 
		"SELECT ud.id.primaryId "
		+ "FROM uniprot_dbxref ud "
		+ "WHERE ud.id.dbName = 'GO' AND ud.id.uniprotId = ";
	
	public static final String getGOFromProtein = 
		"SELECT gt.acc, gt.name "
			+ "FROM go_term gt " + "WHERE gt.acc = ?";

	public static final String getGOFromProtein2 = 
		"SELECT gt.acc, gt.name "
			+ "FROM go_term gt " + "WHERE gt.acc IN ";

	public static final String getGOFromHPRDProtein = 
		"Select go_number, name "
			+ "FROM (SELECT * FROM hprd_gene_ontology_2_mol_funktion_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_cell_component_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_bio_process_term) tabelle "
			+ "WHERE hprd_id = ?";
	//TODO: Union
	
	public static final String getGONumberFromHPRDProtein = "Select go_number "
			+ "FROM (SELECT * FROM hprd_gene_ontology_2_mol_funktion_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_cell_component_term "
			+ "UNION SELECT * FROM hprd_gene_ontology_2_bio_process_term) tabelle "
			+ "WHERE hprd_id = ";
	//TODO: Union

	public static final String getGOFromHPRDProtein1 =
		"FROM hprd_gene_ontology_2_mol_funktion_term go "
			+ "WHERE go.id.hprdId = ";

	public static final String getGOFromHPRDProtein2 = 
			"FROM hprd_gene_ontology_2_cell_component_term go "
			+ "WHERE go.id.hprdId = ";

	public static final String getGOFromHPRDProtein3 = 
			"FROM hprd_gene_ontology_2_bio_process_term go "
			+ "WHERE go.id.hprdId = ";

	public static final String getOrganismForGO = 
		"SELECT  DISTINCT gs.species "
			+ "FROM go_term gt, go_species gs "
			+ "WHERE gs.id = gt.id " +
					"AND gt.acc = ?";

	public static final String getGOSynonyms = 
		"SELECT DISTINCT gts.id.termSynonym "
			+ "FROM go_term_synonym gts, go_term gt "
			+ "WHERE gts.id.termId = gt.id AND gt.acc = ?";

	public static final String getGOOntology = 
		"SELECT DISTINCT gt.termType, gt.name "
			+ "FROM go_term gt " + "WHERE gt.acc = ?";

	public static final String getGOTermDefinition = 
		"SELECT DISTINCT gtd.id.termDefinition "
			+ "FROM go_term_definition gtd, go_term gt "
			+ "WHERE gtd.id.termId = gt.id AND gt.acc = ?";

	public static final String getNCBIIDForGO = 
		"SELECT DISTINCT gs.ncbiTaxaId "
			+ "FROM go_term gt, go_species gs "
			+ "WHERE gs.id = gt.id AND gt.acc = ?";
	
	/*
	 * get drug information
	 */

	public static final String getDrugStartQuery = 
		"SELECT kd.entry, kdn.id.name "
			+ "FROM kegg_drug kd "
			+ "LEFT OUTER JOIN kd.keggDrugNames kdn "
			+ "WHERE ";

	public static final String getDrugFromPathway = 
		"SELECT kpd.id.entry, kpd.id.organismus "
			+ "FROM kegg_pathway_drug kpd "
			+ "WHERE kpd.id.number = ?";

	public static final String getDrugFromPathway2 = 
		"SELECT kpd.id.entry, kpd.id.organismus "
		+ "FROM kegg_pathway_drug kpd "
		+ "WHERE kpd.id.number = ";
	
	public static final String getDrugFromTPPathway2 = 
		"SELECT t.moleculeId "
			+ "FROM tp_pathway_molecules_involved t, tp_molecule_synonyms s "
			+ "WHERE s.moleculeId.moleculeId = t.moleculeId " +
					"AND t.pathwayId.pathwayId = ";

	public static final String getDrugFromPathwayByNumber = 
		"SELECT kpd.id.entry "
			+ "FROM kegg_pathway_drug kpd "
			+ "WHERE kpd.id.number = ?";

	public static final String getDrugName = 
		"SELECT kdn.id.name "
			+ "FROM kegg_drug_name kdn " + "WHERE kdn.id.entry = ?";

	public static final String getDrugDetails = 
			"FROM kegg_drug kd " + "WHERE kd.entry = ?";

	public static final String getDrugActivity = 
			"FROM kegg_drug_activity kda " + "WHERE kda.keggDrug.entry = ?";

	public static final String getTPDrugFromKEGGDrug = 
		"SELECT t.moleculeId "
			+ "FROM tp_molecule_synonyms t, kegg_drug_name kdn "
			+ "WHERE kdn.id.name = t.synonym " +
					"AND kdn.id.entry = ?";

	public static final String getKEGGDrugFromTPDrug = 
		"SELECT kdn.id.entry  "
			+ "FROM tp_molecule_synonyms t, kegg_drug_name kdn "
			+ "WHERE kdn.id.name = t.synonym AND t.moleculeId = ?";

	/*
	 * get fragment information
	 */

	public static final String getFragmentDetails = 
		"FROM tf_fragment tff " + "WHERE tff.fragmentId = ?";

	public static final String getFragmentMethod = 
		"FROM tf_fragment_methods tfm " + "WHERE tfm.fragmentId = ?";

	public static final String getTransfacSpecies = 
			"FROM tf_organism tfo " + "WHERE tfo.organismId = ?";

}
