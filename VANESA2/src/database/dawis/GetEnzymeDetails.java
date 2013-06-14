package database.dawis;

import java.util.ArrayList;

import pojos.DBColumn;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Enzyme;
import configurations.Wrapper;

public class GetEnzymeDetails {

	private Enzyme enzyme = null;
	private DAWISNode don = null;

	public GetEnzymeDetails(Enzyme e) {

		enzyme = e;
		fillNodeWithInformations();

	}

	private void fillNodeWithInformations() {

		don = enzyme.getDAWISNode();
		don.setID(enzyme.getLabel());
		don.setName(enzyme.getName());
		don.setOrganism(enzyme.getOrganism());
		getSynonyms();
		getClassification();
		getEnzymeDetails();
		getPDBs();
		getOrthology();
		getSubstrate();
		getProducts();
		getCofactors();
		getEffectors();
		getInhibitors();
		getDBLinks();
		don.setDataLoaded();

	}
	
	private void getDBLinks()
	{
		String[] det={enzyme.getLabel()};
		String query=DAWISQueries.getEnzymeDBLinks;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setDBLink(res[1]+"("+res[2]+")");
		}
	}
	
//	private void getDBLinks() {
//		String[] det = { enzyme.getLabel() };
//		String query = DAWISQueries.getEnzymeDBLinks;
//
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = (String[]) it.next();
//			don.setDBLink(res[1] + "(" + res[2] + ")");
//		}
//	}
	
	private void getInhibitors()
	{
		String[] det={enzyme.getLabel()};
		String query=DAWISQueries.getEnzymeInhibitors;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setInhibitors(res[2]);
			don.setInhibitorsName(res[1]);
		}

	}
	
//	private void getInhibitors() {
//
//		String[] det = { enzyme.getLabel() };
//		String query = DAWISQueries.getEnzymeInhibitors;
//
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = (String[]) it.next();
//			don.setInhibitors(res[2]);
//			don.setInhibitorsName(res[1]);
//		}
//
//	}
	
	private void getCofactors()
	{
		String[] det={enzyme.getLabel()};
		String query=DAWISQueries.getEnzymeCofactors;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setCofactors(res[2]);
			don.setCofactorsName(res[1]);
		}

	}
	
//	private void getCofactors() {
//
//		String[] det = { enzyme.getLabel() };
//		String query = DAWISQueries.getEnzymeCofactors;
//
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = (String[]) it.next();
//			don.setCofactors(res[2]);
//			don.setCofactorsName(res[1]);
//		}
//
//	}
	
	private void getEffectors() {

		String[] det = { enzyme.getLabel() };
		String query = DAWISQueries.getEnzymeEffectors;

		ArrayList<DBColumn> results = new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setEffectors(res[2]);
			don.setEffectorsName(res[1]);
		}

	}
	
//	private void getEffectors() {
//
//		String[] det = { enzyme.getLabel() };
//		String query = DAWISQueries.getEnzymeEffectors;
//
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = (String[]) it.next();
//			don.setEffectors(res[2]);
//			don.setEffectorsName(res[1]);
//		}
//
//	}
	
	private void getProducts()
	{

		String[] det={enzyme.getLabel()};
		String query=DAWISQueries.getEnzymeProducts;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setProducts(res[2]);
			don.setProductsName(res[1]);
		}

	}
	
//	private void getProducts() {
//
//		String[] det = { enzyme.getLabel() };
//		String query = DAWISQueries.getEnzymeProducts;
//
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = (String[]) it.next();
//			don.setProducts(res[2]);
//			don.setProductsName(res[1]);
//		}
//
//	}

	private void getSubstrate()
	{
		String[] det={enzyme.getLabel()};
		String query=DAWISQueries.getEnzymeSubstrates;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setSubstrates(res[2]);
			don.setSubstratesName(res[1]);
		}

	}
	
	private void getOrthology()
	{

		String[] det={enzyme.getLabel()};
		String query=DAWISQueries.getEnzymeOrthology;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setOrthology(res[1]+": "+res[2]);
		}

	}
	
//	private void getOrthology() {
//
//		String[] det = { enzyme.getLabel() };
//		String query = DAWISQueries.getEnzymeOrthology;
//
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = (String[]) it.next();
//			don.setOrthology(res[1] + ": " + res[2]);
//		}
//
//	}
	
	private void getPDBs()
	{
		String[] det={enzyme.getLabel()};
		String query=DAWISQueries.getEnzymePDBs;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setPDBs(res[0]);
		}

	}
	
//	private void getPDBs() {
//
//		String[] det = { enzyme.getLabel() };
//		String query = DAWISQueries.getEnzymePDBs;
//
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = (String[]) it.next();
//			don.setPDBs(res[0]);
//		}
//
//	}
	
	private void getEnzymeDetails()
	{

		String[] det={enzyme.getLabel()};
		String query=DAWISQueries.getEnzymeDetails;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setComment(res[0]);
			don.setReference(res[1]);
			
			if (enzyme.getName().equals(""))
			{
				enzyme.setName(res[2]);
				don.setName(enzyme.getName());
			}
		}

	}
	
//	private void getEnzymeDetails() {
//
//		String[] det = { enzyme.getLabel() };
//		String query = DAWISQueries.getEnzymeDetails;
//
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = (String[]) it.next();
//			don.setComment(res[0]);
//			don.setReference(res[1]);
//			if (enzyme.getName().equals("")) {
//				enzyme.setName(res[2]);
//				don.setName(enzyme.getName());
//			}
//		}
//
//	}
	
	private void getClassification()
	{

		String[] det={enzyme.getLabel()};
		String query=DAWISQueries.getEnzymeClass;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setClassification(res[0]);
		}

	}

	
//	private void getClassification() {
//
//		String[] det = { enzyme.getLabel() };
//		String query = DAWISQueries.getEnzymeClass;
//
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = (String[]) it.next();
//			don.setClassification(res[0]);
//		}
//
//	}
	
	private void getSynonyms()
	{
		String[] det={enzyme.getLabel()};
		String query=DAWISQueries.getEnzymeSynonyms;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setSynonym(res[0]);
		}

	}
	
//	private void getSynonyms() {
//
//		String[] det = { enzyme.getLabel() };
//		String query = DAWISQueries.getEnzymeSynonyms;
//
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = (String[]) it.next();
//			don.setSynonym(res[0]);
//		}
//
//	}

//	private void getReferences() {
//
//	}

}
