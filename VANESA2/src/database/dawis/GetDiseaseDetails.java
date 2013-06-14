package database.dawis;

import java.util.ArrayList;

import pojos.DBColumn;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Disease;
import configurations.Wrapper;

public class GetDiseaseDetails {

	private Disease disease = null;
	private DAWISNode don = null;

	public GetDiseaseDetails(Disease d) {
		
		disease = d;
		fillNodeWithInformations();
		
	}

	private void fillNodeWithInformations() {

		don = disease.getDAWISNode();
		don.setID(disease.getLabel());
		if (!disease.getName().equals("")){
			don.setName(disease.getName());
		} else {
			getName(disease.getLabel());
		}
		don.setOrganism(disease.getOrganism());

		getDiseaseFeatures(disease.getLabel());
		getDiseaseSynonyms(disease.getLabel());
		getDisorder();
		getLocation();
		getDiagnosisType();
		getReferenz();
		don.setDataLoaded();
		
	}
	
	private void getReferenz()
	{
		String[] det={disease.getLabel()};
		String query=DAWISQueries.getDiseaseReference;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] details=column.getColumn();
			
			don.setReference(details[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getReferenz() {
//		
//		String [] det = {disease.getLabel()};
//		String query = DAWISQueries.getDiseaseReference;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		
//		Iterator <String[]>it = results.iterator();
//		while (it.hasNext()){
//			String [] details = it.next();
//			don.setReference(details[0]);
//		}
//	}
	
	private void getDiagnosisType()
	{
		String[] det={disease.getLabel()};
		String query=DAWISQueries.getDiseaseDiagnosisType;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] details=column.getColumn();
			
			don.setDiagnosisType(details[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getDiagnosisType() {
//		
//		String [] det = {disease.getLabel()};
//		String query = DAWISQueries.getDiseaseDiagnosisType;
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		
//		Iterator <String[]>it = results.iterator();
//		while (it.hasNext()){
//			String [] details = it.next();
//			don.setDiagnosisType(details[0]);
//		}
//	}
	
	private void getLocation()
	{
		String[] det={disease.getLabel()};
		String query=DAWISQueries.getDiseaseLocations;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] details=column.getColumn();
			
			don.setLocation(details[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getLocation() {
//		
//		String [] det = {disease.getLabel()};
//		String query = DAWISQueries.getDiseaseLocations;
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		
//		Iterator <String[]>it = results.iterator();
//		while (it.hasNext()){
//			String [] details = it.next();
//			don.setLocation(details[0]);
//		}
//	}
	
	private void getDisorder()
	{

		String[] det={disease.getLabel()};
		String query=DAWISQueries.getDiseaseDisorder;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] details=column.getColumn();
			
			don.setDisorder(details[0]);
		}

	}
	
//	@SuppressWarnings("unchecked")
//	private void getDisorder() {
//		
//		String [] det = {disease.getLabel()};
//		String query = DAWISQueries.getDiseaseDisorder;
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		
//		Iterator <String[]>it = results.iterator();
//		while (it.hasNext()){
//			String [] details = it.next();
//			don.setDisorder(details[0]);
//		}
//		
//	}
	
	private void getName(String id)
	{
		String[] det={id};
		String query=DAWISQueries.getOMIMDiseaseName;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] details=column.getColumn();
			
			disease.setName(details[0]);
			don.setName(disease.getName());
		}

	}
	
//	@SuppressWarnings("unchecked")
//	private void getName(String id) {
//
//		String [] det = {id};
//		String query = DAWISQueries.getOMIMDiseaseName;
//		Vector <String[]>results = new Wrapper().requestDbContent(3, query, det);
//		
//		Iterator <String[]>it = results.iterator();
//		while (it.hasNext()){
//			String [] details = it.next();
//			disease.setName(details[0]);
//			don.setName(disease.getName());
//		}
//		
//	}
	
	/**
	 * get all synonyms of the disease by mim store their data in DAWISOMIMNode
	 * 
	 * @param mim
	 */
	private void getDiseaseFeatures(String mim)
	{
		String[] param={mim};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, DAWISQueries.getDiseaseFeatures, param);
		
		for (DBColumn column : results)
		{
			String[] details=column.getColumn();
			
			don.setDomain(details[0]);
			don.setFeature(details[1]);
		}

	}
	
//	/**
//	 * get all synonyms of the disease by mim store their data in DAWISOMIMNode
//	 * 
//	 * @param mim
//	 */
//	@SuppressWarnings("unchecked")
//	private void getDiseaseFeatures(String mim) {
//
//		String[] param = { mim };
//		Vector <String[]>results = new Wrapper().requestDbContent(3,
//				DAWISQueries.getDiseaseFeatures, param);
//		Iterator <String[]>it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] resultDetails = it.next();
//			don.setDomain(resultDetails[0]);
//			don.setFeature(resultDetails[1]);
//		}
//
//	}
	
	/**
	 * get all synonyms of the disease store their data in DAWISOMIMNode
	 * 
	 * @param mim
	 */
	private void getDiseaseSynonyms(String mim)
	{

		String[] param={mim};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, DAWISQueries.getDiseaseSynonyms, param);
		
		for (DBColumn column : results)
		{
			String[] details=column.getColumn();
			
			don.setSynonym(details[0]);
		}

	}
	
//	/**
//	 * get all synonyms of the disease store their data in DAWISOMIMNode
//	 * 
//	 * @param mim
//	 */
//	@SuppressWarnings("unchecked")
//	private void getDiseaseSynonyms(String mim) {
//
//		String[] param = { mim };
//		Vector <String[]> results = new Wrapper().requestDbContent(3,
//				DAWISQueries.getDiseaseSynonyms, param);
//		Iterator <String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] resultDetails = it.next();
//			don.setSynonym(resultDetails[0]);
//		}
//
//	}

}
