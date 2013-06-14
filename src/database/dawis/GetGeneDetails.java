package database.dawis;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import pojos.DBColumn;

import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Gene;
import configurations.Wrapper;

public class GetGeneDetails {

	private Gene gene = null;
	private DAWISNode don = null;

	private String keggGene = "";
	private String emblGene = "";
	private String transpathGene = "";
	private String transfacGene = "";
	public Hashtable<String, String> geneIDDBRelations;

	public GetGeneDetails(Gene g) {

		gene = g;
		don = gene.getDAWISNode();
		geneIDDBRelations = don.getAllIDDBRelationsAsHashtable();
		String startDB = gene.getDB();

		if (geneIDDBRelations.containsKey("KEGG")) {
			keggGene = geneIDDBRelations.get("KEGG");
			if (!startDB.equalsIgnoreCase("KEGG")) {
				don.setSynonym(keggGene + "(KEGG)");
			}
		}
		if (geneIDDBRelations.containsKey("EMBL")) {
			emblGene = geneIDDBRelations.get("EMBL");
			don.setSynonym(emblGene + "(" + "EMBL" + ")");
		}
		if (geneIDDBRelations.containsKey("Transfac")) {
			transfacGene = geneIDDBRelations.get("Transfac");
			if (!startDB.equalsIgnoreCase("Transfac")) {
				don.setSynonym(transfacGene + "(Transfac)");
			}
		}
		if (geneIDDBRelations.containsKey("Transpath")) {
			transpathGene = geneIDDBRelations.get("Transpath");
			if (!startDB.equalsIgnoreCase("Transpath")) {
				don.setSynonym(transpathGene + "(Transpath)");
			}
		}

		don.setID(gene.getLabel());
		don.setOrganism(gene.getOrganism());

		if (!gene.getName().equals("")) {
			don.setName(gene.getName());
		} else {
			getName(gene.getLabel());
		}
		fillNodeWithInformations();

	}
	
	private void getName(String label)
	{
		String[] det=new String[1];
		String query=new String();
		
		if (label.equals(keggGene))
		{
			det[0]=keggGene;
			query=DAWISQueries.getGeneName;
		}
		else if (label.equals(transpathGene))
		{
			det[0]=transpathGene;
			query=DAWISQueries.getTPGeneName;
		}
		else if (label.equals(transfacGene))
		{
			det[0]=transfacGene;
			query=DAWISQueries.getTFGeneName;
		}
		else if (label.equals(emblGene))
		{
			det[0]=emblGene;
			query=DAWISQueries.getEMBLGeneName;
		}
		
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] names=column.getColumn();

			gene.setName(names[0]);
			don.setName(gene.getName());
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getName(String label) {
//		String[] det = new String[1];
//		String query = "";
//		if (label.equals(keggGene)) {
//			det[0] = keggGene;
//			query = DAWISQueries.getGeneName;
//		} else if (label.equals(transpathGene)) {
//			det[0] = transpathGene;
//			query = DAWISQueries.getTPGeneName;
//		} else if (label.equals(transfacGene)) {
//			det[0] = transfacGene;
//			query = DAWISQueries.getTFGeneName;
//		} else if (label.equals(emblGene)) {
//			det[0] = emblGene;
//			query = DAWISQueries.getEMBLGeneName;
//		}
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] names = it.next();
//			gene.setName(names[0]);
//			don.setName(gene.getName());
//		}
//	}

	private void fillNodeWithInformations() {

		if (!keggGene.equals("")) {
			getGeneDetails();
			getSequenceDetails();
			getOrthologyDetails();
			getMotif();
		} else if (!emblGene.equals("")) {
			getEMBLGeneOrganismData();
			getEMBLGeneSequence();
			getEMBLGeneSequenceLength();
			getEMBLGeneDescription();
			getEMBLGeneType();
		} else if (!transpathGene.equals("")) {

		} else if (!transfacGene.equals("")) {

		}

		getSynonyms();
		don.setDataLoaded();
	}
	
	private void getEMBLGeneType()
	{
		String query=DAWISQueries.getEMBLGeneType;
		String[] det={emblGene};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		if (!results.isEmpty())
		{
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();
				
				if (don.getType().equals(""))
				{
					don.setType(res[0]);
				}
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getEMBLGeneType() {
//		String query = DAWISQueries.getEMBLGeneType;
//		String[] det = { emblGene };
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		if (!results.isEmpty()) {
//
//			Iterator<String[]> it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				if (don.getType().equals("")) {
//					don.setType(res[0]);
//				}
//			}
//		}
//	}
	
	private void getEMBLGeneDescription()
	{
		String query=DAWISQueries.getEMBLGeneDescription;
		String[] det={emblGene};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		if (!results.isEmpty())
		{
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();

				if (don.getDefinition().equals(""))
				{
					don.setDefinition(res[0]);
				}
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getEMBLGeneDescription() {
//		String query = DAWISQueries.getEMBLGeneDescription;
//		String[] det = { emblGene };
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		if (!results.isEmpty()) {
//
//			Iterator<String[]> it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				if (don.getDefinition().equals("")) {
//					don.setDefinition(res[0]);
//				}
//			}
//		}
//	}
	
	private void getSynonyms()
	{
		String[] det=new String[1];
		String query="";

		if (!transpathGene.equals(""))
		{
			det[0]=transpathGene;
			query=DAWISQueries.getTPGeneSynonyms;
		}
		else if (!transfacGene.equals(""))
		{
			det[0]=transfacGene;
			query=DAWISQueries.getTFGeneSynonyms;
		}
		else if (!emblGene.equals(""))
		{
			det[0]=emblGene;
			query=DAWISQueries.getEMBLGeneSynonyms;
		}
		else if (!keggGene.equals(""))
		{
			det[0]=keggGene;
			query=DAWISQueries.getGeneName;
		}
		
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] syn=column.getColumn();

			don.setSynonym(syn[0]);
		}

	}
	
//	@SuppressWarnings("unchecked")
//	private void getSynonyms() {
//		String[] det = new String[1];
//		String query = "";
//
//		if (!transpathGene.equals("")) {
//			det[0] = transpathGene;
//			query = DAWISQueries.getTPGeneSynonyms;
//		} else if (!transfacGene.equals("")) {
//			det[0] = transfacGene;
//			query = DAWISQueries.getTFGeneSynonyms;
//		} else if (!emblGene.equals("")) {
//			det[0] = emblGene;
//			query = DAWISQueries.getEMBLGeneSynonyms;
//		} else if (!keggGene.equals("")) {
//			det[0] = keggGene;
//			query = DAWISQueries.getGeneName;
//		}
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] syn = it.next();
//			don.setSynonym(syn[0]);
//		}
//
//	}
	
	private void getEMBLGeneSequence()
	{
		String query=DAWISQueries.getEMBLGeneSequenceData;
		String[] det={emblGene};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		String sequence=new String();

		if (!results.isEmpty())
		{
			for (DBColumn column : results)
			{
				String[] seq=column.getColumn();
				
				sequence=seq[2];
			}
		}
		
		if (don.getNucleotidSequence().equals(""))
		{
			don.setNucleotidSequence(sequence);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getEMBLGeneSequence() {
//
//		String query = DAWISQueries.getEMBLGeneSequenceData;
//		String[] det = { emblGene };
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		String sequence = "";
//
//		if (!results.isEmpty()) {
//
//			Iterator<String[]> it = results.iterator();
//			while (it.hasNext()) {
//				String[] seq = it.next();
//				sequence = seq[2];
//			}
//
//		}
//		if (don.getNucleotidSequence().equals("")) {
//			don.setNucleotidSequence(sequence);
//		}
//	}
	
	private void getEMBLGeneOrganismData()
	{
		String query=DAWISQueries.getEMBLGeneOrganismData;
		String[] det={emblGene};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		String classification=new String();
		String organelle=new String();

		if (!results.isEmpty())
		{
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();

				classification=res[2];
				organelle=res[3];

				don.setClassification(classification);
				
				if (don.getOrganelle().equals(""))
				{
					don.setOrganelle(organelle);
				}
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getEMBLGeneOrganismData() {
//
//		String query = DAWISQueries.getEMBLGeneOrganismData;
//		String[] det = { emblGene };
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		String classification = "";
//		String organelle = "";
//
//		if (!results.isEmpty()) {
//
//			Iterator<String[]> it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//
//				classification = res[2];
//				organelle = res[3];
//
//				don.setClassification(classification);
//				if (don.getOrganelle().equals("")) {
//					don.setOrganelle(organelle);
//				}
//			}
//		}
//	}
	
	private void getEMBLGeneSequenceLength()
	{

		String query=DAWISQueries.getEMBLGeneSequenceLength;
		String[] det={emblGene};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		String sequenceLength=new String();

		if (!results.isEmpty())
		{
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();

				sequenceLength=res[0];
			}

		}
		
		if (don.getNucleotidSequenceLength().equals(""))
		{
			don.setNucleotidSequenceLength(sequenceLength);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getEMBLGeneSequenceLength() {
//
//		String query = DAWISQueries.getEMBLGeneSequenceLength;
//		String[] det = { emblGene };
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		String sequenceLength = "";
//
//		if (!results.isEmpty()) {
//
//			Iterator it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = (String[]) it.next();
//				sequenceLength = res[0];
//			}
//
//		}
//		if (don.getNucleotidSequenceLength().equals("")) {
//			don.setNucleotidSequenceLength(sequenceLength);
//		}
//	}

	@SuppressWarnings("unused")
	private String createString(Vector<String> v) {
		String s = "";
		Iterator<String> it = v.iterator();
		boolean first = false;
		while (it.hasNext()) {
			if (!first) {
				s = s + it.next();
				first = true;
			} else {
				s = s + "; " + it.next();
			}
		}
		return s;
	}
	

	private void getMotif()
	{
		String[] det={keggGene};
		String query=DAWISQueries.getMotif;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		String motif=new String();

		for (DBColumn column : results)
		{
			String[] mot=column.getColumn();

			motif=mot[2]+"("+mot[0]+")";
			don.setMotif(motif);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getMotif() {
//
//		String[] det = { keggGene };
//		String query = DAWISQueries.getMotif;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		String motif = "";
//
//		while (it.hasNext()) {
//
//			String[] mot = it.next();
//			motif = mot[2] + "(" + mot[0] + ")";
//			don.setMotif(motif);
//		}
//
//	}
	
	private void getOrthologyDetails()
	{

		String query=DAWISQueries.getGeneOrthologyDetails;
		String[] det={keggGene};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		String orthology=new String();

		if (!results.isEmpty())
		{
			for (DBColumn column : results)
			{
				String[] seq=column.getColumn();
				
				orthology=seq[1]+": "+seq[2];
			}
		}

		don.setOrthology(orthology);

	}
	
//	@SuppressWarnings("unchecked")
//	private void getOrthologyDetails() {
//
//		String query = DAWISQueries.getGeneOrthologyDetails;
//		String[] det = { keggGene };
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		String orthology = "";
//
//		if (!results.isEmpty()) {
//
//			Iterator<String[]> it = results.iterator();
//			while (it.hasNext()) {
//				String[] seq = it.next();
//				orthology = seq[1] + ": " + seq[2];
//			}
//		}
//
//		don.setOrthology(orthology);
//
//	}
	
	private void getSequenceDetails()
	{
		String query=DAWISQueries.getGeneSequenceDetails;
		String[] det={keggGene};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		String nucSeqLength=new String();
		String nucSeq=new String();
		String aminAcidSeqLength=new String();
		String aminAcidSeq=new String();
		String organism=new String();

		if (!results.isEmpty())
		{
			for (DBColumn column : results)
			{
				String[] seq=column.getColumn();

				organism=seq[1];
				aminAcidSeq=seq[2];
				aminAcidSeqLength=seq[3];
				nucSeq=seq[4];
				nucSeqLength=seq[5];
			}
		}

		don.setNucleotidSequenceLength(nucSeqLength);
		don.setNucleotidSequence(nucSeq);
		don.setAminoAcidSeqLength(aminAcidSeqLength);
		don.setAminoAcidSeq(aminAcidSeq);
		don.setOrganism(organism);
	}
	
//	@SuppressWarnings("unchecked")
//	private void getSequenceDetails() {
//
//		String query = DAWISQueries.getGeneSequenceDetails;
//		String[] det = { keggGene };
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		String nucSeqLength = "";
//		String nucSeq = "";
//		String aminAcidSeqLength = "";
//		String aminAcidSeq = "";
//		String organism = "";
//
//		if (!results.isEmpty()) {
//
//			Iterator<String[]> it = results.iterator();
//
//			while (it.hasNext()) {
//
//				String[] seq = it.next();
//				organism = seq[1];
//				aminAcidSeq = seq[2];
//				aminAcidSeqLength = seq[3];
//				nucSeq = seq[4];
//				nucSeqLength = seq[5];
//
//			}
//		}
//
//		don.setNucleotidSequenceLength(nucSeqLength);
//		don.setNucleotidSequence(nucSeq);
//		don.setAminoAcidSeqLength(aminAcidSeqLength);
//		don.setAminoAcidSeq(aminAcidSeq);
//		don.setOrganism(organism);
//
//	}
	
	private void getGeneDetails()
	{
		String query=DAWISQueries.getGeneDetails;
		String[] det={keggGene};

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		String definition=new String();
		String position=new String();
		String codonUsage=new String();

		if (!results.isEmpty())
		{
			for (DBColumn column : results)
			{
				String[] resultDetails=column.getColumn();
				
				codonUsage=resultDetails[2];
				definition=resultDetails[3];
				position=resultDetails[4];
			}
		}

		don.setDefinition(definition);
		don.setPosition(position);
		don.setCodonUsage(codonUsage);
	}
	
//	@SuppressWarnings("unchecked")
//	private void getGeneDetails() {
//
//		String query = DAWISQueries.getGeneDetails;
//		String[] det = { keggGene };
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		String definition = "";
//		String position = "";
//		String codonUsage = "";
//
//		if (!results.isEmpty()) {
//
//			Iterator<String[]> it = results.iterator();
//
//			while (it.hasNext()) {
//
//				String[] resultDetails = it.next();
//				codonUsage = resultDetails[2];
//				definition = resultDetails[3];
//				position = resultDetails[4];
//
//			}
//		}
//
//		don.setDefinition(definition);
//		don.setPosition(position);
//		don.setCodonUsage(codonUsage);
//
//	}

}
