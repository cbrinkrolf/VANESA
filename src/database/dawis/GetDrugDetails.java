package database.dawis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import pojos.DBColumn;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Drug;
import configurations.Wrapper;

public class GetDrugDetails {

	private Drug drug = null;
	private DAWISNode don = null;
	private Vector <String> name = new Vector <String> ();
	
	public GetDrugDetails(Drug d) {
		
		drug = d;	
		fillNodeWithInformations();	
				
	}

	private void fillNodeWithInformations() {

		don = drug.getDAWISNode();
		don.setID(drug.getLabel());
		if (!drug.getName().equals("")){
			don.setName(drug.getName());
		} else {
			getName();
			drug.setName(createString(name));
			don.setName(drug.getName());
		}
		don.setOrganism(drug.getOrganism());
		getSynonyms();
		getDetails();
		getActivity();
		don.setDataLoaded();
		
	}
	
	private void getName()
	{

		String[] det={drug.getLabel()};
		String query=DAWISQueries.getDrugName;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] names=column.getColumn();

			name.add(names[0]);
		}

	}
	
//	private void getName() {
//
//		String [] det = {drug.getLabel()};
//		String query = DAWISQueries.getDrugName;
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		
//		Iterator it = results.iterator();
//		while (it.hasNext()){
//			String [] names = (String []) it.next();
//			name.add(names[0]);
//		}
//		
//	}
	
	@SuppressWarnings("rawtypes")
	private String createString(Vector v){
		String s = "";
		Iterator it = v.iterator();
		boolean first = false;
		while (it.hasNext()) {
			if (!first){
				s = s + (String) it.next();
				first = true;
			} else {
				s = s + "; "+ (String) it.next();
			}
		}
		return s;
	}
	
//	private String createLocationDisplayTag(String location, Vector symbols) {
//		String loc = "";
//		Iterator it = symbols.iterator();
//		int i = 0;
//		while (it.hasNext()) {
//			if (i == 0) {
//				loc = location + "(" + (String) it.next();
//			} else if (i > 0 && i < (symbols.size() - 1)) {
//				loc = loc + ", " + (String) it.next();
//			} else {
//				loc = loc + ", " + (String) it.next() + ")";
//			}
//			i++;
//		}
//		return loc;
//	}
	
	private void getActivity()
	{
		String[] det={drug.getLabel()};
		String query=DAWISQueries.getDrugActivity;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setActivity(res[1]);
		}

	}
	
//	private void getActivity() {
//
//		String [] det = {drug.getLabel()};
//		String query = DAWISQueries.getDrugActivity;
//		
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//		
//		while (it.hasNext()){
//			String [] res = (String []) it.next();	
//			don.setActivity(res[1]);
//		}
//		
//	}
	
	private void getDetails()
	{
		String[] det={drug.getLabel()};
		String query=DAWISQueries.getDrugDetails;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setAtoms(res[1]);
			don.setAtomsNumber(res[2]);
			don.setBonds(res[3]);
			don.setBondsNumber(res[4]);
			don.setBracket(res[5]);
			don.setComment(res[6]);
			don.setComponent(res[7]);
			don.setFormula(res[8]);
			don.setWeight(res[9]);
			don.setOriginal(res[10]);
			don.setRemark(res[11]);
			don.setRepeat(res[12]);
			don.setTarget(res[12]);

		}

	}
	
//	private void getDetails() {
//
//		String [] det = {drug.getLabel()};
//		String query = DAWISQueries.getDrugDetails;
//		
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//		
//		while (it.hasNext()){
//			
//			String [] res = (String []) it.next();	
//			don.setAtoms(res[1]);
//			don.setAtomsNumber(res[2]);
//			don.setBonds(res[3]);
//			don.setBondsNumber(res[4]);
//			don.setBracket(res[5]);
//			don.setComment(res[6]);
//			don.setComponent(res[7]);
//			don.setFormula(res[8]);
//			don.setWeight(res[9]);
//			don.setOriginal(res[10]);
//			don.setRemark(res[11]);
//			don.setRepeat(res[12]);
//			don.setTarget(res[12]);
//			
//		}
//		
//	}
	
	private void getSynonyms()
	{
		String[] det={drug.getLabel()};
		String query=DAWISQueries.getDrugName;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setSynonym(res[0]);
		}

	}
	
//	private void getSynonyms() {
//
//		String [] det = {drug.getLabel()};
//		String query = DAWISQueries.getDrugName;
//		
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//		
//		while (it.hasNext()){
//			String [] res = (String []) it.next();	
//			don.setSynonym(res[0]);
//		}
//		
//	}

}
