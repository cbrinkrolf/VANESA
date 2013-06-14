package database.dawis;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import pojos.DBColumn;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.Wrapper;

/**
 * 
 * @author Olga
 *
 */

/**
 * control the organism data of elements
 */
public class OrganismController {

	/**
	 * get organism synonyms
	 * 
	 * @param organism
	 * @return
	 */
	public String[] getOrganismSynonyms(String organism)
	{
		String[] syn=null;
		String synonyms=DAWISQueries.getKEGGOrganismSynonyms;
		String firstQuery="";

		firstQuery=synonyms+" t.org = '"+organism+"' OR "+" t.latin_name = '"+organism+"' OR "+" t.name = '"+organism+"'";

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, firstQuery);

		for (DBColumn column : results)
		{
			syn=column.getColumn();

			for (int i=0; i<syn.length; i++)
			{
				syn[i]=syn[i].toLowerCase();
				if (syn[i].endsWith("."))
				{
					syn[i]=syn[i].substring(0, syn[i].length()-1);
				}
			}
		}

		return syn;
	}
	
//	/**
//	 * get organism synonyms
//	 * 
//	 * @param organism
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public String[] getOrganismSynonyms(String organism) {
//
//		String[] syn = null;
//		String synonyms = DAWISQueries.getKEGGOrganismSynonyms;
//		String firstQuery = "";
//
//		firstQuery = synonyms + " t.org = '" + organism + "' OR "
//				+ " t.latin_name = '" + organism + "' OR " + " t.name = '"
//				+ organism + "'";
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, firstQuery);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			syn = it.next();
//			for (int i = 0; i < syn.length; i++) {
//				syn[i] = syn[i].toLowerCase();
//				if (syn[i].endsWith(".")) {
//					syn[i] = syn[i].substring(0, syn[i].length() - 1);
//				}
//			}
//		}
//
//		return syn;
//	}

	/**
	 * test whether the organism of the element is the same as the organism of
	 * interest
	 * 
	 * @param acc
	 *            of the element
	 * @param synonyms
	 *            from organism of interest
	 * @return
	 */
	public boolean testForOrganism(String org, String[] synonyms) {

		boolean returnValue = false;

		for (int i = 0; i < synonyms.length && !returnValue; i++) {
			if (org.equals(synonyms[i])) {
				returnValue = true;
			}
		}

		return returnValue;
	}

	/**
	 * test whether the organism of interest is one of organisms of actual
	 * element
	 * 
	 * @param orgResults
	 * @param actuellObject
	 * @return
	 */
	public boolean testForOrganism(Vector<String> orgResults,
			String actualObject, String[] synonyms) {

		boolean returnValue = false;

		for (int i = 0; i < synonyms.length && !returnValue; i++) {
			if (actualObject.equals("Protein")) {

				synonyms[i] = synonyms[i] + ".";

			}
			if (orgResults.contains(synonyms[i])) {
				returnValue = true;
			} else {

				for (int j = 0; j < orgResults.size() && !returnValue; j++) {
					String vec = (orgResults.get(j)).toLowerCase();
					if (synonyms[i].equals(vec)) {
						returnValue = true;
					}
				}
			}
			if (synonyms[i].endsWith(".")) {
				synonyms[i] = synonyms[i]
						.substring(0, synonyms[i].length() - 1);
			}

		}

		return returnValue;
	}
	
	/**
	 * get all possible organisms for special element
	 * 
	 * @param id
	 * @param object
	 * @return
	 */
	public Vector<String> getOrganisms(BiologicalNodeAbstract bna)
	{
		ArrayList<DBColumn> org=null;
		String query=new String();
		String object=bna.getBiologicalElement();
		String[] det=new String[1];
		String organism=new String();

		Vector<String> r=new Vector<String>();

		Hashtable<String, String> ht=bna.getDAWISNode().getAllIDDBRelationsAsHashtable();
		Set<String> keys=ht.keySet();
		
		String synonymID=new String();
		String synonymDB=new String();
		
		for (String key : keys)
		{
			synonymDB=key;
			synonymID=ht.get(synonymDB);
			det[0]=synonymID;

			if (object.equals("Enzyme"))
			{
				if (synonymDB.equalsIgnoreCase("KEGG")|synonymDB.equalsIgnoreCase("Brenda")|synonymDB.equalsIgnoreCase("ENZYME"))
				{
					query=DAWISQueries.getOrganismForEnzyme;
				}
			}
			else if (object.equals("Compound"))
			{
				if (synonymDB.equalsIgnoreCase("KEGG"))
				{
					query=DAWISQueries.getOrganismForCompound;
				}
				else if (synonymDB.equalsIgnoreCase("Transpath"))
				{
					query=DAWISQueries.getOrganismForTranspathCompound;
				}
			}
			else if (object.equals("Gene Ontology"))
			{
				if (synonymDB.equalsIgnoreCase("Gene Ontology"))
				{
					query=DAWISQueries.getOrganismForGO;
				}
			}
			else if (object.equals("Glycan"))
			{
				if (synonymDB.equalsIgnoreCase("KEGG"))
				{
					query=DAWISQueries.getOrganismForGlycan;
				}
			}
			else if (object.equals("Reaction"))
			{
				if (synonymDB.equalsIgnoreCase("KEGG"))
				{
					query=DAWISQueries.getOrganismForReaction;
				}
			}
			else if (object.equals("Reaction Pair"))
			{
				if (synonymDB.equalsIgnoreCase("KEGG"))
				{
					query=DAWISQueries.getOrganismForReactionPair;
				}
			}
			else if (object.equals("Protein"))
			{
				if (synonymDB.equalsIgnoreCase("UniProt"))
				{
					query=DAWISQueries.getOrganismForProtein;
				}
				else if (synonymDB.equalsIgnoreCase("HPRD"))
				{
					r.add("Homo sapiens");
				}
			}
			else if (object.equals("Gene"))
			{
				if (synonymDB.equalsIgnoreCase("KEGG"))
				{
					query=DAWISQueries.getOrganismForGene;
				}
				else if (synonymDB.equalsIgnoreCase("EMBL"))
				{
					query=DAWISQueries.getOrganismForEMBLGene;
				}
				else if (synonymDB.equalsIgnoreCase("Transpath"))
				{
					query=DAWISQueries.getOrganismForTranspathGene;
				}
				else if (synonymDB.equalsIgnoreCase("Transfac"))
				{
					query=DAWISQueries.getOrganismForTransfacGene;
				}
			}
			else if (object.equals("Pathway Map"))
			{
				if (synonymDB.equalsIgnoreCase("KEGG"))
				{
					query=DAWISQueries.getOrganismForPathway+" p.pathway_name like '"+synonymID+"' or p.number = '"+synonymID+"'";
				}
			}

			if (!query.equals("")&&!det[0].equals(""))
			{
				if (!object.equals("Pathway Map"))
				{
					org=new Wrapper().requestDbContent(3, query, det);
				}
				else
				{
					if (synonymDB.equalsIgnoreCase("KEGG"))
					{
						org=new Wrapper().requestDbContent(3, query);
					}
					else
					{
						org=new Wrapper().requestDbContent(3, query, det);
					}
				}
			}

			// if organisms found
			if (org!=null&&org.size()>0)
			{
				// store organisms in vector for compare
				for (DBColumn column : org)
				{
					String[] s=column.getColumn();
					
					organism=s[0];
					r.add(organism);
				}
			}
		}

		return r;
	}
	
//	/**
//	 * get all possible organisms for special element
//	 * 
//	 * @param id
//	 * @param object
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public Vector<String> getOrganisms(BiologicalNodeAbstract bna) {
//
//		Vector<String> org = null;
//		String query = "";
//		String object = bna.getBiologicalElement();
//		String[] det = new String[1];
//		String organism = "";
//
//		Vector<String> r = new Vector<String>();
//
//		Hashtable<String, String> ht = bna.getDAWISNode()
//				.getAllIDDBRelationsAsHashtable();
//		Set<String> keys = ht.keySet();
//		Iterator<String> it = keys.iterator();
//		String synonymID = "";
//		String synonymDB = "";
//
//		while (it.hasNext()) {
//
//			synonymDB = it.next();
//			synonymID = ht.get(synonymDB);
//			det[0] = synonymID;
//
//			if (object.equals("Enzyme")) {
//				if (synonymDB.equalsIgnoreCase("KEGG")
//						| synonymDB.equalsIgnoreCase("Brenda")
//						| synonymDB.equalsIgnoreCase("ENZYME")) {
//					query = DAWISQueries.getOrganismForEnzyme;
//				}
//			} else if (object.equals("Compound")) {
//				if (synonymDB.equalsIgnoreCase("KEGG")) {
//					query = DAWISQueries.getOrganismForCompound;
//				} else if (synonymDB.equalsIgnoreCase("Transpath")) {
//					query = DAWISQueries.getOrganismForTranspathCompound;
//				}
//			} else if (object.equals("Gene Ontology")) {
//				if (synonymDB.equalsIgnoreCase("Gene Ontology")) {
//					query = DAWISQueries.getOrganismForGO;
//				}
//			} else if (object.equals("Glycan")) {
//				if (synonymDB.equalsIgnoreCase("KEGG")) {
//					query = DAWISQueries.getOrganismForGlycan;
//				}
//			} else if (object.equals("Reaction")) {
//				if (synonymDB.equalsIgnoreCase("KEGG")) {
//					query = DAWISQueries.getOrganismForReaction;
//				}
//			} else if (object.equals("Reaction Pair")) {
//				if (synonymDB.equalsIgnoreCase("KEGG")) {
//					query = DAWISQueries.getOrganismForReactionPair;
//				}
//			} else if (object.equals("Protein")) {
//				if (synonymDB.equalsIgnoreCase("UniProt")) {
//					query = DAWISQueries.getOrganismForProtein;
//				} else if (synonymDB.equalsIgnoreCase("HPRD")) {
//					r.add("Homo sapiens");
//				}
//			} else if (object.equals("Gene")) {
//				if (synonymDB.equalsIgnoreCase("KEGG")) {
//					query = DAWISQueries.getOrganismForGene;
//				} else if (synonymDB.equalsIgnoreCase("EMBL")) {
//					query = DAWISQueries.getOrganismForEMBLGene;
//				} else if (synonymDB.equalsIgnoreCase("Transpath")) {
//					query = DAWISQueries.getOrganismForTranspathGene;
//				} else if (synonymDB.equalsIgnoreCase("Transfac")) {
//					query = DAWISQueries.getOrganismForTransfacGene;
//				}
//			} else if (object.equals("Pathway Map")) {
//				if (synonymDB.equalsIgnoreCase("KEGG")) {
//					query = DAWISQueries.getOrganismForPathway
//							+ " p.pathway_name like '" + synonymID
//							+ "' or p.number = '" + synonymID + "'";
//				}
//			}
//
//			if (!query.equals("") && !det[0].equals("")) {
//				if (!object.equals("Pathway Map")) {
//					org = new Wrapper().requestDbContent(3, query, det);
//				} else {
//					if (synonymDB.equalsIgnoreCase("KEGG")) {
//						org = new Wrapper().requestDbContent(3, query);
//					} else {
//						org = new Wrapper().requestDbContent(3, query, det);
//					}
//				}
//			}
//
//			// if organisms found
//			if (org != null && org.size() > 0) {
//				Iterator it2 = org.iterator();
//
//				// store organisms in vector for compare
//				while (it2.hasNext()) {
//					String[] s = (String[]) it2.next();
//					organism = s[0];
//					r.add(organism);
//				}
//			}
//
//		}
//
//		return r;
//
//	}

	/**
	 * test whether the organism of the element is the same as the organism of
	 * interest
	 * 
	 * @param actuellObject
	 * @param organism
	 *            of the actual object
	 * @return
	 */
	public boolean testForSynonyms(String orgOfInterest, String organism) {

		String[] organismSynonyms = null;
		boolean isSameOrganismus = false;

		if (organism.equals(orgOfInterest)) {
			isSameOrganismus = true;
		} else {

			// look for synonyms
			organismSynonyms = getOrganismSynonyms(organism);

		}

		if (organismSynonyms != null && organismSynonyms.length > 0) {

			for (int i = 0; i < organismSynonyms.length; i++) {

				if (organismSynonyms[i].toLowerCase().equals(orgOfInterest)) {
					isSameOrganismus = true;
				}

			}
		}

		return isSameOrganismus;
	}

}
