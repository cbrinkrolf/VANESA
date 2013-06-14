package database.dawis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import pojos.DBColumn;

import configurations.Wrapper;

import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Glycan;

public class GetGlycanDetails {

	private Glycan glycan = null;
	private DAWISNode don = null;
	private Vector <String>name = new Vector<String>();

	public GetGlycanDetails(Glycan g) {
	
		glycan = g;
		fillNodeWithInformations();
	}
	
	private void fillNodeWithInformations() {

		don = glycan.getDAWISNode();
		don.setID(glycan.getLabel());
		if (!glycan.getName().equals("")){
			don.setName(glycan.getName());
		} else {
			glycan.setName(getName());
			don.setName(glycan.getName());
		}
		don.setOrganism(glycan.getOrganism());
		getDetails();
		getSynonyms();
		don.setDataLoaded();
		getOrthology();
		getClassification();

	}

	private String getName() {

		getNameData();
		return createString(name);		
		
	}
	
	private void getNameData()
	{
		String[] det={glycan.getLabel()};
		String query=DAWISQueries.getGlycanName;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] names=column.getColumn();
			
			name.add(names[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getNameData() {
//		String [] det = {glycan.getLabel()};
//		String query = DAWISQueries.getGlycanName;
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		
//		Iterator <String[]>it = results.iterator();
//		while (it.hasNext()){
//			String [] names = it.next();
//			name.add(names[0]);
//		}
//	}

	private String createString(Vector <String>v){
		String s = "";
		Iterator <String> it = v.iterator();
		boolean first = false;
		while (it.hasNext()) {
			if (!first){
				s = s + it.next();
				first = true;
			} else {
				s = s + "; "+ it.next();
			}
		}
		return s;
	}
	
	private void getClassification()
	{
		String[] det={glycan.getLabel()};
		String query=DAWISQueries.getGlycanClass;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setClassification(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getClassification() {
//
//		String [] det = {glycan.getLabel()};
//		String query = DAWISQueries.getGlycanClass;
//		
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		Iterator <String[]>it = results.iterator();
//		
//		while (it.hasNext()){
//			String [] res = it.next();	
//			don.setClassification(res[0]);
//		}
//		
//	}
	
	private void getOrthology()
	{
		String[] det={glycan.getLabel()};
		String query=DAWISQueries.getGlycanOrthology;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setOrthology(res[1]+": "+res[2]);
		}
	}

	
//	@SuppressWarnings("unchecked")
//	private void getOrthology() {
//
//		String [] det = {glycan.getLabel()};
//		String query = DAWISQueries.getGlycanOrthology;
//		
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		Iterator <String[]>it = results.iterator();
//		
//		while (it.hasNext()){
//			String [] res = it.next();	
//			don.setOrthology(res[1]+": "+res[2]);
//		}
//		
//	}
	
	private void getSynonyms()
	{
		String[] det={glycan.getLabel()};
		String query=DAWISQueries.getGlycanName;

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
//		String [] det = {glycan.getLabel()};
//		String query = DAWISQueries.getGlycanName;
//		
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		Iterator <String[]>it = results.iterator();
//		
//		while (it.hasNext()){
//			String [] res = it.next();	
//			don.setSynonym(res[0]);
//		}
//		
//	}
	
	private void getDetails()
	{
		String[] det={glycan.getLabel()};
		String query=DAWISQueries.getGlycanDetails;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setBracket(res[1]);
			don.setComposition(res[2]);
			don.setEdge(res[3]);
			don.setWeight(res[4]);
			don.setNode(res[5]);
			don.setReference(res[6]);
			don.setRemark(res[7]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getDetails() {
//
//		String [] det = {glycan.getLabel()};
//		String query = DAWISQueries.getGlycanDetails;
//		
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		Iterator <String[]>it = results.iterator();
//		
//		while (it.hasNext()){
//			String [] res = it.next();	
//			don.setBracket(res[1]);
//			don.setComposition(res[2]);
//			don.setEdge(res[3]);
//			don.setWeight(res[4]);
//			don.setNode(res[5]);
//			don.setReference(res[6]);
//			don.setRemark(res[7]);
//
//		}
//		
//	}
}
