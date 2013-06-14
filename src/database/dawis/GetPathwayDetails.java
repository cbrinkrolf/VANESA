package database.dawis;

import java.util.ArrayList;
import java.util.Hashtable;

import pojos.DBColumn;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.PathwayMap;
import configurations.Wrapper;

/**
 * 
 * @author Olga Mantler
 *
 */

/**
 * get pathway details
 */
public class GetPathwayDetails {

	private PathwayMap pathwayNode = null;
	private DAWISNode don = null;
	public Hashtable <String, String> pathwayIDDBRelations;
	private String tpPathway;
	private String keggPathway;

	public GetPathwayDetails(PathwayMap p) {

		pathwayNode = p;
		don = pathwayNode.getDAWISNode();
		pathwayIDDBRelations = don.getAllIDDBRelationsAsHashtable();
		String startDB = pathwayNode.getDB();

		if (pathwayIDDBRelations.containsKey("Transpath")){
			tpPathway = pathwayIDDBRelations.get("Transpath");
			if (!startDB.equalsIgnoreCase("Transpath")){
				don.setSynonym(tpPathway+"(Transpath)");
			}
		}
		if (pathwayIDDBRelations.containsKey("KEGG")){
			keggPathway = pathwayIDDBRelations.get("KEGG");
			if (!startDB.equalsIgnoreCase("KEGG")){
				don.setSynonym(keggPathway+"(KEGG)");
			}
		}
		
		if (!pathwayNode.getName().equals("")){
			don.setName(pathwayNode.getName());
		} else {
			getName(pathwayNode.getLabel());
		}
		don.setOrganism(pathwayNode.getOrganism());
		
		if (keggPathway!=null){
			fillNodeWithInformations();
		}
		
		if (tpPathway!=null){
			fillNodeWithTPPathwayInformation();
		}
		
		don.setDataLoaded();
	}
	
	private void fillNodeWithTPPathwayInformation() {
		getReference();
		getComments();
	}
	
	private void getComments()
	{
		String[] det={tpPathway};
		String query=DAWISQueries.getTPPathwayComments;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setComment(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getComments() {
//		String [] det = {tpPathway};
//		String query = DAWISQueries.getTPPathwayComments;
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		Iterator <String[]>it = results.iterator();
//		while (it.hasNext()){
//			String [] res = it.next();
//			don.setComment(res[0]);
//		}
//	}
	
	private void getReference()
	{
		String[] det={tpPathway};
		String query=DAWISQueries.getTPPathwayReference;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			if (res[0]!=null)
			{
				if (!res[0].equals(""))
				{
					don.setReference(res[0]+"(pubMed)");
				}
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getReference() {
//		String [] det = {tpPathway};
//		String query = DAWISQueries.getTPPathwayReference;
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		Iterator <String[]>it = results.iterator();
//		while (it.hasNext()){
//			String [] res = it.next();
//			if (res[0]!=null){
//				if (!res[0].equals("")){
//					don.setReference(res[0]+"(pubMed)");
//				}
//			}
//		}
//	}

	private void fillNodeWithInformations() {
		getPathwayMap(keggPathway);		
	}
	
	
	private void getName(String id)
	{
		String query=new String();
		
		if (id.startsWith("CH"))
		{
			query=DAWISQueries.getTPPathwayName;
		}
		else
		{
			query=DAWISQueries.getKEGGPathwayName;
		}

		String[] det={id};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			pathwayNode.setName(res[0]);
			don.setName(pathwayNode.getName());
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getName(String id) {
//		
//		String query = "";
//		if (id.startsWith("CH")){
//			query = DAWISQueries.getTPPathwayName;
//		} else {
//			query = DAWISQueries.getKEGGPathwayName;
//		}
//		
//		String [] det = {id};
//		Vector <String []> results = new Wrapper().requestDbContent(3, query, det);
//		
//		Iterator <String []> it = results.iterator();
//		String [] s = null;
//		while (it.hasNext()){
//			s = it.next(); 
//			pathwayNode.setName(s[0]);
//			don.setName(pathwayNode.getName());
//		}
//	}

	private void getPathwayMap(String id)
	{
		String query=new String();

		query=DAWISQueries.getPathwayMap;

		if (id.length()<6)
		{
			id="map"+id;
		}
		
		String[] det={id};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		String[] s=null;
		
		for (DBColumn column : results)
		{
			s=column.getColumn();
		}
		
		if (s!=null)
		{
			don.setPathwayMap(s[0]);
		}
		else
		{
			don.setPathwayMap("");
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getPathwayMap(String id) {
//		
//		String query = "";
//		
//		query = DAWISQueries.getPathwayMap;
//
//		if (id.length()<6){
//			id = "map"+id;
//		}
//		String [] det = {id};
//		
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//	
//		Iterator <String[]>it = results.iterator();
//		String [] s = null;
//		while (it.hasNext()){
//			s = it.next(); 
//		}
//		if (s!=null){
//			don.setPathwayMap(s[0]);
//		} else {
//			don.setPathwayMap("");
//		}
//		
//	}

}
