package database.dawis;

import java.util.ArrayList;
import java.util.Hashtable;

import pojos.DBColumn;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Protein;
import configurations.Wrapper;

public class GetProteinDetails {

	private Protein protein = null;
	private DAWISNode don = null;
	private String hprdProtein;
	private String uniprotProtein;
	private String transpathProtein;
	private String transfacProtein;
	boolean isAccessionNumber = false;

	public Hashtable<String, String> proteinIDs;

	public GetProteinDetails(Protein p) {

		protein = p;
		don = protein.getDAWISNode();
		proteinIDs = don.getAllIDDBRelationsAsHashtable();
		String startDB = protein.getDB();

		if (proteinIDs.containsKey("UniProt")) {
			uniprotProtein = proteinIDs.get("UniProt");
			if (!startDB.equalsIgnoreCase("UniProt")) {
				don.setSynonym(uniprotProtein + "(UniProt)");
			}
		}
		if (proteinIDs.containsKey("HPRD")) {
			hprdProtein = proteinIDs.get("HPRD");
			if (!startDB.equalsIgnoreCase("HPRD")) {
				don.setSynonym(hprdProtein + "(HPRD)");
			}
		}
		if (proteinIDs.containsKey("TRANSPATH")) {
			transpathProtein = proteinIDs.get("TRANSPATH");
			if (!startDB.equalsIgnoreCase("TRANSPATH")) {
				don.setSynonym(transpathProtein + "(TRANSPATH)");
			}
		}
		if (proteinIDs.containsKey("TRANSFAC")) {
			transfacProtein = proteinIDs.get("TRANSFAC");
			if (!startDB.equalsIgnoreCase("TRANSFAC")) {
				don.setSynonym(transfacProtein + "(TRANSFAC)");
			}
		}

		if (!protein.getName().equals("")) {
			don.setName(protein.getName());
		} else {
			getName();
		}
		don.setOrganism(protein.getOrganism());

		if (uniprotProtein != null) {
			fillNodeWithInformations();
		}

		if (hprdProtein != null) {
			fillNodeWithHPRDProteinInformation();
		}

		if (transpathProtein != null) {
			fillNodeWithTPProteinInformation();
		}

		if (transfacProtein != null) {
			fillNodeWithTFProteinInformation();
		}

		don.setDataLoaded();

	}

	private void fillNodeWithTPProteinInformation() {
		getTPComplexName();
		getTPSubfamily();
		getTPSuperfamily();
		getTPComment();
	}
	
	private void getTPComment()
	{
		String[] det={transpathProtein};
		String query=DAWISQueries.getTPProteinComment;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setComment(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getTPComment() {
//		String[] det = { transpathProtein };
//		String query = DAWISQueries.getTPProteinComment;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setComment(res[0]);
//		}
//	}
	
	private void getTPSuperfamily()
	{
		String[] det={transpathProtein};
		String query=DAWISQueries.getTPProteinSuperfamily;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setSuperfamily(res[0]+"("+res[1]+")");
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getTPSuperfamily() {
//		String[] det = { transpathProtein };
//		String query = DAWISQueries.getTPProteinSuperfamily;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setSuperfamily(res[0] + "(" + res[1] + ")");
//		}
//	}

	private void getTPSubfamily()
	{
		String[] det={transpathProtein};
		String query=DAWISQueries.getTPProteinSubfamily;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setSubfamily(res[0]+"("+res[1]+")");
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getTPSubfamily() {
//		String[] det = { transpathProtein };
//		String query = DAWISQueries.getTPProteinSubfamily;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setSubfamily(res[0] + "(" + res[1] + ")");
//		}
//	}
	
	private void getTPComplexName()
	{
		String[] det={transpathProtein};
		String query=DAWISQueries.getTPProteinComplexName;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setComplexName(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getTPComplexName() {
//		String[] det = { transpathProtein };
//		String query = DAWISQueries.getTPProteinComplexName;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setComplexName(res[0]);
//		}
//	}

	private void fillNodeWithTFProteinInformation() {
		getTFProteinDetails();
		getTFFeatures();
		getTFSynonyms();
	}
	
	private void getTFSynonyms()
	{
		String[] det={transfacProtein};
		String query=DAWISQueries.getTFProteinSynonyms;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setSynonym(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getTFSynonyms() {
//		String[] det = { transfacProtein };
//		String query = DAWISQueries.getTFProteinSynonyms;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setSynonym(res[0]);
//		}
//	}
	
	private void getTFFeatures()
	{
		String[] det={transfacProtein};
		String query=DAWISQueries.getTFProteinFeatures;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
	
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setFeature(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getTFFeatures() {
//		String[] det = { transfacProtein };
//		String query = DAWISQueries.getTFProteinFeatures;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setFeature(res[0]);
//		}
//	}
	
	private void getTFProteinDetails()
	{
		String[] det={transfacProtein};
		String query=DAWISQueries.getTransfacProteinDetails;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			if (don.getAminoAcidSeqLength().equals(""))
			{
				don.setAminoAcidSeqLength(res[9]);
			}
			
			if (don.getWeigth().equals(""))
			{
				don.setWeight(res[10]);
			}
			
			if (don.getAminoAcidSeq().equals(""))
			{
				don.setAminoAcidSeq(res[11]);
			}
			
			if (don.getSequenceSource().equals(""))
			{
				don.setSequenceSource(res[12]);
			}
			
			if (don.getSubfamilies().equals(""))
			{
				don.setSubfamily(res[13]);
			}
			
			if (don.getSuperfamilies().equals(""))
			{
				don.setSuperfamily(res[14]);
			}
			don.setType(res[16]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getTFProteinDetails() {
//		String[] det = { transfacProtein };
//		String query = DAWISQueries.getTransfacProteinDetails;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			if (don.getAminoAcidSeqLength().equals("")) {
//				don.setAminoAcidSeqLength(res[9]);
//			}
//			if (don.getWeigth().equals("")) {
//				don.setWeight(res[10]);
//			}
//			if (don.getAminoAcidSeq().equals("")) {
//				don.setAminoAcidSeq(res[11]);
//			}
//			if (don.getSequenceSource().equals("")) {
//				don.setSequenceSource(res[12]);
//			}
//			if (don.getSubfamilies().equals("")) {
//				don.setSubfamily(res[13]);
//			}
//			if (don.getSuperfamilies().equals("")) {
//				don.setSuperfamily(res[14]);
//			}
//			don.setType(res[16]);
//		}
//	}
	
	private void getName()
	{
		String[] det=new String[1];
		String query=new String();
		
		if (protein.getLabel().equals(uniprotProtein))
		{
			det[0]=uniprotProtein;
			query=DAWISQueries.getUniProtProteinStartQueryOrganismIndependent+" p.uniprot_id = '"+uniprotProtein+"'";
		}
		else if (protein.getLabel().equals(hprdProtein))
		{
			det[0]=hprdProtein;
			query=DAWISQueries.getHPRDProteinStartQuery+" hps.HPRD_ID = '"+hprdProtein+"'";
		}
		else if (Protein.labelIsAccessionNumber)
		{
			det[0]=uniprotProtein;
			query=DAWISQueries.getUniProtProteinNameByAccessionNumber+"'"+uniprotProtein+"'";
		}

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query);	

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setName(res[1]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getName() {
//		String[] det = new String[1];
//		String query = "";
//		if (protein.getLabel().equals(uniprotProtein)) {
//			det[0] = uniprotProtein;
//			query = DAWISQueries.getUniProtProteinStartQueryOrganismIndependent
//					+ " p.uniprot_id = '" + uniprotProtein + "'";
//		} else if (protein.getLabel().equals(hprdProtein)) {
//			det[0] = hprdProtein;
//			query = DAWISQueries.getHPRDProteinStartQuery + " hps.HPRD_ID = '"
//					+ hprdProtein + "'";
//		} else if (Protein.labelIsAccessionNumber) {
//			det[0] = uniprotProtein;
//			query = DAWISQueries.getUniProtProteinNameByAccessionNumber + "'"
//					+ uniprotProtein + "'";
//		}
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setName(res[1]);
//		}
//	}

	private void fillNodeWithHPRDProteinInformation() {
		getIsoformenNumber();
		getExpression();
		getProzess();
		getCellComponent();
		getMolecularFunktion();
	}
	
	private void getCellComponent()
	{
		String[] det={hprdProtein};
		String query=DAWISQueries.getHPRDProteinCellComponent;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			if (don.getOrganelle().equals(""))
			{
				don.setOrganelle(res[0]);
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getCellComponent() {
//		String[] det = { hprdProtein };
//		String query = DAWISQueries.getHPRDProteinCellComponent;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			if (don.getOrganelle().equals("")) {
//				don.setOrganelle(res[0]);
//			}
//		}
//	}
	
	private void getMolecularFunktion()
	{
		String[] det={hprdProtein};
		String query=DAWISQueries.getHPRDProteinMolecularFunktion;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setFunction(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getMolecularFunktion() {
//		String[] det = { hprdProtein };
//		String query = DAWISQueries.getHPRDProteinMolecularFunktion;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setFunction(res[0]);
//		}
//	}
	
	private void getProzess()
	{
		String[] det={hprdProtein};
		String query=DAWISQueries.getHPRDProteinProzess;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setProzess(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getProzess() {
//		String[] det = { hprdProtein };
//		String query = DAWISQueries.getHPRDProteinProzess;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setProzess(res[0]);
//		}
//	}
	
	private void getExpression()
	{
		String[] det={hprdProtein};
		String query=DAWISQueries.getHPRDProteinExpression;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setExpression(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getExpression() {
//		String[] det = { hprdProtein };
//		String query = DAWISQueries.getHPRDProteinExpression;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setExpression(res[0]);
//		}
//	}
	
	private void getIsoformenNumber()
	{

		String[] det={hprdProtein};
		String query=DAWISQueries.getHPRDProteinIsoformen;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setIsoformenNumber(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getIsoformenNumber() {
//
//		String[] det = { hprdProtein };
//		String query = DAWISQueries.getHPRDProteinIsoformen;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setIsoformenNumber(res[0]);
//		}
//
//	}

	private void fillNodeWithInformations() {

		getProteinDetails();
		getGeneName();
		getGeneSynonym();
		getPDBs();

	}
	
	private void getPDBs()
	{

		String[] det={uniprotProtein};
		String query=DAWISQueries.getProteinPDBs;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setPDBs(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getPDBs() {
//
//		String[] det = { uniprotProtein };
//		String query = DAWISQueries.getProteinPDBs;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setPDBs(res[0]);
//		}
//
//	}
	
	private void getGeneSynonym()
	{

		String[] det={uniprotProtein};
		String query=DAWISQueries.getProteinGeneSynonyms;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setSynonym(res[1]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getGeneSynonym() {
//
//		String[] det = { uniprotProtein };
//		String query = DAWISQueries.getProteinGeneSynonyms;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setSynonym(res[1]);
//		}
//	}
	
	private void getGeneName()
	{

		String[] det={uniprotProtein};
		String query=DAWISQueries.getProteinGeneName;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setGeneName(res[1]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getGeneName() {
//
//		String[] det = { uniprotProtein };
//		String query = DAWISQueries.getProteinGeneName;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setGeneName(res[1]);
//		}
//	}
	

	private void getProteinDetails()
	{
		String[] det={uniprotProtein};
		String query=DAWISQueries.getProteinDetails;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			if (protein.getName().equals(""))
			{
				protein.setName(res[2]);
				don.setName(protein.getName());
			}
			
			don.setOrganelle(res[4]);
			don.setClassification(res[5]);
			don.setAminoAcidSeqLength(res[8]);
			don.setWeight(res[9]);
			don.setAminoAcidSeq(res[10]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getProteinDetails() {
//
//		String[] det = { uniprotProtein };
//		String query = DAWISQueries.getProteinDetails;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			if (protein.getName().equals("")) {
//				protein.setName(res[2]);
//				don.setName(protein.getName());
//			}
//			don.setOrganelle(res[4]);
//			don.setClassification(res[5]);
//			don.setAminoAcidSeqLength(res[8]);
//			don.setWeight(res[9]);
//			don.setAminoAcidSeq(res[10]);
//		}
//
//	}

}
