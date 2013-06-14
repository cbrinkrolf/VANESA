package database.dawis;

import java.util.ArrayList;

import pojos.DBColumn;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.GeneOntology;
import configurations.Wrapper;

public class GetGODetails {

	private GeneOntology geneOntology = null;
	private DAWISNode don = null;
	
	public GetGODetails(GeneOntology g) {
	
		geneOntology = g;
		fillNodeWithInformations();
		
	}

	private void fillNodeWithInformations() {

		don = geneOntology.getDAWISNode();
		don.setID(geneOntology.getLabel());
		don.setName(geneOntology.getName());
		don.setOrganism(geneOntology.getOrganism());
		getGOSynonyms(geneOntology.getLabel());
		getGOOntology(geneOntology.getLabel());
		getGOTermDefinition(geneOntology.getLabel());
		don.setDataLoaded();
		
	}
	
	private void getGOOntology(String id)
	{
		String query=DAWISQueries.getGOOntology;
		String[] det={id};

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setOntology(res[0]);
			
			if (geneOntology.getName().equals(""))
			{
				geneOntology.setName(res[1]);
				don.setName(geneOntology.getName());
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getGOOntology(String id) {
//		
//		String query = DAWISQueries.getGOOntology;
//		String [] det = {id};
//		
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		java.util.Iterator <String[]>it = results.iterator();
//		
//		while (it.hasNext()){
//			String [] res = it.next();
//			don.setOntology(res[0]);
//			if (geneOntology.getName().equals("")){
//				geneOntology.setName(res[1]);
//				don.setName(geneOntology.getName());
//			}
//		}
//		
//	}
	
	private void getGOSynonyms(String id)
	{
		String query=DAWISQueries.getGOSynonyms;
		String[] det={id};

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] synonym=column.getColumn();
			
			don.setSynonym(synonym[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getGOSynonyms(String id) {
//		
//		String query = DAWISQueries.getGOSynonyms;
//		String [] det = {id};
//		
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		java.util.Iterator <String[]>it = results.iterator();
//		
//		while (it.hasNext()){
//			String [] synonym = it.next();
//			don.setSynonym(synonym[0]);
//		}
//		
//	}
	
	private void getGOTermDefinition(String id)
	{
		String query=DAWISQueries.getGOTermDefinition;
		String[] det={id};

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] definition=column.getColumn();

			don.setDefinition(definition[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getGOTermDefinition(String id) {
//		
//		String query = DAWISQueries.getGOTermDefinition;
//		String [] det = {id};
//		
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		java.util.Iterator <String[]>it = results.iterator();
//		
//		while (it.hasNext()){
//			String [] definition = it.next();
//			don.setDefinition(definition[0]);
//		}
//		
//	}

}
