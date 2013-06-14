package database.dawis;

import java.util.ArrayList;

import pojos.DBColumn;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Fragment;
import configurations.Wrapper;

public class GetTransfacFragmentDetails {
	private Fragment fragment = null;
	private DAWISNode don = null;
	
	public GetTransfacFragmentDetails(Fragment f){
		fragment = f;
		don = fragment.getDAWISNode();
		fillNodeWithInformations();
	}
	
	private void fillNodeWithInformations() {
		don.setID(fragment.getLabel());	
		getFragmentDetails();
		getMethod();
	}
	
	private void getMethod()
	{
		String[] det={fragment.getLabel()};
		String query=DAWISQueries.getFragmentMethod;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setMethod(res[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getMethod() {
//		
//		String[] det = { fragment.getLabel() };
//		String query = DAWISQueries.getFragmentMethod;
//		Vector <String []> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator <String []> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setMethod(res[0]);
//		}
//	}
	
	private void getFragmentDetails()
	{
		String[] det={fragment.getLabel()};
		String query=DAWISQueries.getFragmentDetails;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			don.setNucleotidSequence(res[1]);
			don.setSequenceSource(res[2]);
			
			if (!res[3].equals(""))
			{
				getSpecies(res[3]);
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getFragmentDetails() {
//		String[] det = { fragment.getLabel() };
//		String query = DAWISQueries.getFragmentDetails;
//		Vector <String []> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator <String []> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setNucleotidSequence(res[1]);
//			don.setSequenceSource(res[2]);
//			if (!res[3].equals("")){
//				getSpecies(res[3]);
//			}
//		}
//	}
	
	private void getSpecies(String string)
	{
		String[] det={string};
		String query=DAWISQueries.getTransfacSpecies;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setOrganism(res[1]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getSpecies(String string) {
//		
//		String[] det = { string };
//		String query = DAWISQueries.getTransfacSpecies;
//		Vector <String []> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator <String []> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setOrganism(res[1]);
//		}
//	}
	
}
