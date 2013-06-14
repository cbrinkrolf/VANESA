package database.dawis;

import java.util.ArrayList;
import java.util.Hashtable;

import pojos.DBColumn;
import biologicalObjects.nodes.CompoundNode;
import biologicalObjects.nodes.DAWISNode;
import configurations.Wrapper;

public class GetCompoundDetails {

	private CompoundNode compound = null;
	private DAWISNode don = null;
	public Hashtable<String, String> compoundIDDBRelations;
	String keggCompound;
	String tpCompound;

	public GetCompoundDetails(CompoundNode c) {

		compound = c;
		don = compound.getDAWISNode();
		compoundIDDBRelations = don.getAllIDDBRelationsAsHashtable();
		String startDB = compound.getDB();

		if (compoundIDDBRelations.containsKey("Transpath")) {
			tpCompound = compoundIDDBRelations.get("Transpath");
			if (!startDB.equalsIgnoreCase("Transpath")) {
				don.setSynonym(tpCompound + "(Transpath)");
			}
		}
		if (compoundIDDBRelations.containsKey("KEGG")) {
			keggCompound = compoundIDDBRelations.get("KEGG");
			if (!startDB.equalsIgnoreCase("KEGG")) {
				don.setSynonym(keggCompound + "(KEGG)");
			}
		}

		if (!compound.getName().equals("")) {
			don.setName(compound.getName());
		} else {
			getName(compound.getLabel());
		}

		don.setOrganism(compound.getOrganism());

		if (keggCompound != null) {
			fillNodeWithInformations();
		}

		if (tpCompound != null) {
			fillNodeWithTPCompoundInformations();
		}

		don.setDataLoaded();

	}

	private void fillNodeWithTPCompoundInformations() {
		getTPDetails();
		getSubfamily();
		getSuperfamily();
	}
	
	private void getSuperfamily()
	{
		String[] det={tpCompound};
		String query=DAWISQueries.getCompoundSuperfamily;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setSuperfamily(res[1]+"("+res[0]+")");
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getSuperfamily() {
//		String[] det = { tpCompound };
//		String query = DAWISQueries.getCompoundSuperfamily;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setSuperfamily(res[1] + "(" + res[0] + ")");
//		}
//	}
	
	private void getSubfamily()
	{
		String[] det={tpCompound};
		String query=DAWISQueries.getCompoundSubfamily;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setSubfamily(res[1]+"("+res[0]+")");
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getSubfamily() {
//		String[] det = { tpCompound };
//		String query = DAWISQueries.getCompoundSubfamily;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setSubfamily(res[1] + "(" + res[0] + ")");
//		}
//	}
	
	private void getTPDetails()
	{
		String[] det={tpCompound};
		String query=DAWISQueries.getTranspathCompoundDetails;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setClassification(res[1]);
			don.setIsoelectricPoint(res[4]);
			don.setWeight(res[5]);
			don.setNucleotidSequence(res[7]);
			don.setNucleotidSequenceLength(res[9]);
			don.setSequenceSource(res[10]);
			don.setInformation(res[8]);
			don.setType(res[12]);
			don.setOrganism(res[13]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getTPDetails() {
//		String[] det = { tpCompound };
//		String query = DAWISQueries.getTranspathCompoundDetails;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setClassification(res[1]);
//			don.setIsoelectricPoint(res[4]);
//			don.setWeight(res[5]);
//			don.setNucleotidSequence(res[7]);
//			don.setNucleotidSequenceLength(res[9]);
//			don.setSequenceSource(res[10]);
//			don.setInformation(res[8]);
//			don.setType(res[12]);
//			don.setOrganism(res[13]);
//		}
//	}

	private void fillNodeWithInformations() {

		getSynonyms();
		getDetails();

	}
	
	private void getName(String id)
	{
		String query=new String();
		
		if (id.startsWith("MO"))
		{
			query=DAWISQueries.getTPCompoundName;
		}
		else
		{
			query=DAWISQueries.getCompoundName;
		}

		String[] det={id};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		String[] s=null;
		
		for (DBColumn column : results)
		{
			s=column.getColumn();

			compound.setName(s[0]);
			don.setName(compound.getName());
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getName(String id) {
//
//		String query = "";
//		if (id.startsWith("MO")) {
//			query = DAWISQueries.getTPCompoundName;
//		} else {
//			query = DAWISQueries.getCompoundName;
//		}
//
//		String[] det = { id };
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		String[] s = null;
//		while (it.hasNext()) {
//			s = it.next();
//			compound.setName(s[0]);
//			don.setName(compound.getName());
//		}
//	}
	
	private void getSynonyms()
	{
		String[] det={keggCompound};
		String query=DAWISQueries.getCompoundName;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setSynonym(res[0]);
		}

	}
	
//	@SuppressWarnings("unchecked")
//	private void getSynonyms() {
//
//		String[] det = { keggCompound };
//		String query = DAWISQueries.getCompoundName;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setSynonym(res[0]);
//		}
//
//	}
	
	private void getDetails()
	{
		String[] det={keggCompound};
		String query=DAWISQueries.getCompoundDetails;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setAtoms(res[1]);
			don.setAtomsNumber(res[2]);
			don.setBonds(res[3]);
			don.setBondsNumber(res[4]);
			don.setComment(res[5]);
			don.setFormula(res[6]);
			don.setWeight(res[7]);
			don.setModule(res[8]);
			don.setRemark(res[10]);
			if (!don.getNucleotidSequence().equals(""))
			{
				don.setNucleotidSequence(res[11]);
			}
		}

	}
	
//	@SuppressWarnings("unchecked")
//	private void getDetails() {
//
//		String[] det = { keggCompound };
//		String query = DAWISQueries.getCompoundDetails;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setAtoms(res[1]);
//			don.setAtomsNumber(res[2]);
//			don.setBonds(res[3]);
//			don.setBondsNumber(res[4]);
//			don.setComment(res[5]);
//			don.setFormula(res[6]);
//			don.setWeight(res[7]);
//			don.setModule(res[8]);
//			don.setRemark(res[10]);
//			if (!don.getNucleotidSequence().equals("")) {
//				don.setNucleotidSequence(res[11]);
//			}
//		}
//
//	}

}
