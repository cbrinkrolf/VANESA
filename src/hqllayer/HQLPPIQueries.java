package hqllayer;

public class HQLPPIQueries {
	
	
	//////////////////////////
	// MINT queries
	
	public static final String mintHQL_resultForACnumber =
		"SELECT x.mintEntries.fullName,x.mintEntries.typeFull,x.mintEntries.orgFull, x.mintEntries.id " +
		"FROM mint_xref x " +
		"WHERE x.ac=?";
	
	public static final String mintHQL_resultForName =
		"SELECT m.fullName,m.typeFull,m.orgFull,m.id " +
		"FROM mint_entries m " +
		"WHERE m.fullName LIKE ?";
	
	public static String mintHQL_resultForAlias = 
		"SELECT a.mintEntries.fullName,a.mintEntries.typeFull,a.mintEntries.orgFull,a.mintEntries.id " +
		"FROM mint_alias a " +
		"WHERE a.alias LIKE ?";
	
	public static final String mintHQL_interactionsForID = 
		"SELECT " +
		"m.mintInteractionsBinary.mintEntriesByParticipantA.id," +
		"m.mintInteractionsBinary.mintEntriesByParticipantA.shortLabel," +
		"m.mintInteractionsBinary.mintEntriesByParticipantA.fullName," +
		"m.mintInteractionsBinary.mintEntriesByParticipantA.sequence," +
		"m.mintInteractionsBinary.mintEntriesByParticipantB.id," +
		"m.mintInteractionsBinary.mintEntriesByParticipantB.shortLabel," +
		"m.mintInteractionsBinary.mintEntriesByParticipantB.fullName," +
		"m.mintInteractionsBinary.mintEntriesByParticipantB.sequence " +
		"FROM mint_entry2interactions m " +		
		"WHERE m.mintEntries.id=?";
	
	public static final String mintHQL_complexInteractionsForID = 
		"SELECT " +
		"m.mintEntriesByParticipantA.id,m.mintEntriesByParticipantA.shortLabel," +
		"m.mintEntriesByParticipantA.fullName, m.mintEntriesByParticipantA.sequence, " +
		"m.mintEntriesByParticipantB.id, m.mintEntriesByParticipantB.shortLabel," +
		"m.mintEntriesByParticipantB.fullName, m.mintEntriesByParticipantB.sequence " +		
		"FROM mint_interactions_complex m " +
		"JOIN m.mintEntry2interactionses me " +
		"where me.mintEntries.id=?";
	
	//////////////////////////
	// IntAct queries
	
	public static final String intactHQL_resultForACnumber =
		"SELECT x.intactEntries.fullName,x.intactEntries.typeFull,x.intactEntries.orgFull,x.intactEntries.id " +
		"FROM intact_xref x " +
		"WHERE x.ac=?";
	
	public static final String intactHQL_resultForName =
		"SELECT m.fullName,m.typeFull,m.orgFull,m.id " +
		"FROM intact_entries m " +
		"WHERE m.fullName LIKE ?";
	
	public static String intactHQL_resultForAlias = 
		"SELECT a.intactEntries.fullName,a.intactEntries.typeFull,a.intactEntries.orgFull,a.intactEntries.id " +
		"FROM intact_alias a " +
		"WHERE a.alias LIKE ?";

	public static final String intactHQL_interactionsForID = 
		"SELECT " +
		"i.intactInteractionsBinary.intactEntriesByParticipantA.id," +
		"i.intactInteractionsBinary.intactEntriesByParticipantA.shortLabel," +
		"i.intactInteractionsBinary.intactEntriesByParticipantA.fullName," +
		"i.intactInteractionsBinary.intactEntriesByParticipantA.sequence," +
		"i.intactInteractionsBinary.intactEntriesByParticipantB.id," +
		"i.intactInteractionsBinary.intactEntriesByParticipantB.shortLabel," +
		"i.intactInteractionsBinary.intactEntriesByParticipantB.fullName," +
		"i.intactInteractionsBinary.intactEntriesByParticipantB.sequence " +
		"FROM intact_entry2interactions i " +		
		"WHERE i.intactEntries.id=?";

	public static final String intactHQL_complexInteractionsForID = 
		"SELECT " +
		"i.intactEntriesByParticipantA.id,i.intactEntriesByParticipantA.shortLabel," +
		"i.intactEntriesByParticipantA.fullName, i.intactEntriesByParticipantA.sequence," +
		"i.intactEntriesByParticipantB.id, i.intactEntriesByParticipantB.shortLabel," +
		"i.intactEntriesByParticipantB.fullName, i.intactEntriesByParticipantB.sequence " +		
		"FROM intact_interactions_complex i " +
		"JOIN i.intactEntry2interactionses ie " +
		"where ie.intactEntries.id=?";
	
	//////////////////////
	// HPRD queries
	
	public static String hprdHQL_resultForACnumber = 
		"SELECT h.mainName,h.geneSymbol,h.swissprotId,h.hprdId " +
		"FROM hprd_hprd_id_mapping h " +
		"WHERE h.swissprotId=?";
	
	public static final String hprdHQL_resultForName =
		"SELECT h.mainName,h.geneSymbol,h.swissprotId,h.hprdId " +
		"FROM hprd_hprd_id_mapping h " +
		"WHERE h.mainName LIKE ?";

	public static String hprdHQL_resultForAlias = 
		"SELECT h.mainName,h.geneSymbol,h.swissprotId,h.hprdId " +
		"FROM hprd_hprd_id_mapping h " +
		"WHERE h.geneSymbol LIKE ?";
	
	public static String hprdHQL_interactionsForID =
		"SELECT DISTINCT p.interactor1HprdId,p.interactor1GeneSymbol,m1.mainName,ps1.seqeunce," +
		"p.interactor2HprdId,p.interactor2GeneSymbol,m2.mainName,ps2.seqeunce " +
		"FROM hprd_protein_protein p,hprd_hprd_id_mapping as m1,hprd_hprd_id_mapping as m2," +
		"hprd_protein_sequences as ps1,hprd_protein_sequences as ps2 " +
		"WHERE m1.hprdId=p.interactor1HprdId AND m2.hprdId=p.interactor2HprdId " +
		"AND ps1.id.hprdId=p.interactor1HprdId AND ps2.id.hprdId=interactor2HprdId " +
		"AND p.interactor1HprdId=? OR interactor2HprdId=?";		
}
