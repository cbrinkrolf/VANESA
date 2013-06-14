package database.dawis.webstart;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import pojos.DBColumn;

public class RemoteData {

	public Vector<String> objects = new Vector<String>();
	public Vector<String[]> elements = new Vector<String[]>();

	// Hashtable <synonym, label>
	public Hashtable<String, String> geneIDs = new Hashtable<String, String>();
	public Hashtable<String, String> pathwayIDs = new Hashtable<String, String>();
	public Hashtable<String, String> proteinIDs = new Hashtable<String, String>();

	public Vector<String> pathwayElements = new Vector<String>();
	public Vector<String> proteinElements = new Vector<String>();
	public Vector<String> geneElements = new Vector<String>();
	public Vector<String> diseaseElements = new Vector<String>();
	public Vector<String> enzymeElements = new Vector<String>();
	public Vector<String> geneOntologyElements = new Vector<String>();
	public Vector<String> glycanElements = new Vector<String>();
	public Vector<String> compoundElements = new Vector<String>();
	public Vector<String> drugElements = new Vector<String>();
	public Vector<String> reactionElements = new Vector<String>();
	public Vector<String> reactionPairElements = new Vector<String>();
	public Vector<String> interactionElements = new Vector<String>();
	public Vector<String> factorElements = new Vector<String>();
	public Vector<String> fragmentElements = new Vector<String>();
	public Vector<String> siteElements = new Vector<String>();
	public Vector<String> matrixElements = new Vector<String>();
	
	public RemoteData()
	{
	}
	
	public void addObject(String obj) {
		objects.add(obj);
	}

	public Vector<String> getObjects() {
		return objects;
	}

	public void addElementToCompoundVector(String domain) {
		compoundElements.add(domain);
	}

	public Vector<String> getCompoundRelatedDomains() {
		return compoundElements;
	}

	public void addElementToEnzymeVector(String domain) {
		enzymeElements.add(domain);
	}

	public Vector<String> getEnzymeRelatedDomains() {
		return enzymeElements;
	}

	public void addElementToReactionPairVector(String domain) {
		reactionPairElements.add(domain);
	}

	public Vector<String> getReactionPairRelatedDomains() {
		return reactionPairElements;
	}

	public void addElementToPathwayVector(String domain) {
		pathwayElements.add(domain);
	}

	public Vector<String> getPathwayRelatedDomains() {
		return pathwayElements;
	}

	public void addElementToProteinVector(String domain) {
		proteinElements.add(domain);
	}

	public Vector<String> getProteinRelatedDomains() {
		return proteinElements;
	}

	public void addElementToGeneVector(String domain) {
		geneElements.add(domain);
	}

	public Vector<String> getGeneRelatedDomains() {
		return geneElements;
	}

	public void addElementToDiseaseVector(String domain) {
		diseaseElements.add(domain);
	}

	public Vector<String> getDiseaseRelatedDomains() {
		return diseaseElements;
	}

	public void addElementToGeneOntologyVector(String domain) {
		geneOntologyElements.add(domain);
	}

	public Vector<String> getGeneOntologyRelatedDomains() {
		return geneOntologyElements;
	}

	public void addElementToGlycanVector(String domain) {
		glycanElements.add(domain);
	}

	public Vector<String> getGlycanRelatedDomains() {
		return glycanElements;
	}

	public void addElementToDrugVector(String domain) {
		drugElements.add(domain);
	}

	public Vector<String> getDrugRelatedDomains() {
		return drugElements;
	}

	public void addElementToReactionVector(String domain) {
		reactionElements.add(domain);
	}

	public Vector<String> getReactionRelatedDomains() {
		return reactionElements;
	}

	public void addElementToFactorVector(String domain) {
		factorElements.add(domain);
	}

	public Vector<String> getFactorRelatedDomains() {
		return factorElements;
	}

	public void addElementToFragmentVector(String domain) {
		fragmentElements.add(domain);
	}

	public Vector<String> getFragmentRelatedDomains() {
		return fragmentElements;
	}

	public void addElementToSiteVector(String domain) {
		siteElements.add(domain);
	}

	public Vector<String> getSiteRelatedDomains() {
		return siteElements;
	}

	public void addElementToMatrixVector(String domain) {
		matrixElements.add(domain);
	}

	public Vector<String> getMatrixRelatedDomains() {
		return matrixElements;
	}

//	public void addElements(Vector<String[]> e)
	public void addElements(ArrayList<DBColumn> e)
	{
		// TODO we have to change the code?
		for (DBColumn column : e)
		{
			String[] elem=column.getColumn();
			if (!elements.contains(elem))
			{
				elements.add(elem);
			}
		}
	}

	public Vector<String[]> getElements() {
		return elements;
	}

	public void setIdIdRelation(String synonym, String label) {
		// System.out.println("set id-id");
		// System.out.println("("+synonym+", "+label+")");
		geneIDs.put(synonym, label);
	}

	public String getIdIdRelation(String synonym) {
		return geneIDs.get(synonym);
	}

	public Hashtable<String, String> getIdIdRelationsAsHashtable() {
		return geneIDs;
	}

	// public void setPathwayIDs(String synonym, String label){
	// pathwayIDs.put(synonym, label);
	// }
	//	
	// public String getPathwayID(String synonym) {
	// return pathwayIDs.get(synonym);
	// }
	//	
	// public Hashtable <String, String> getPathwayIDsHashtable() {
	// return pathwayIDs;
	// }
	//	
	// public void setProteinIDs(String synonym, String label){
	// proteinIDs.put(synonym, label);
	// }
	//	
	// public String getProteinID(String synonym) {
	// return proteinIDs.get(synonym);
	// }
	//	
	// public Hashtable <String, String> getProteinIDsHashtable() {
	// return proteinIDs;
	// }
	//	
}
