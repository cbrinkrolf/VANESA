package database.dawis;

import java.util.ArrayList;

import pojos.DBColumn;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Reaction;
import configurations.Wrapper;

public class GetReactionDetails {

	private Reaction reaction = null;
	private DAWISNode don = null;

	public GetReactionDetails(Reaction r) {

		reaction = r;
		fillNodeWithInformations();

	}

	private void fillNodeWithInformations() {

		don = reaction.getDAWISNode();
		don.setID(reaction.getLabel());
		don.setName(reaction.getName());
		don.setOrganism(reaction.getOrganism());
		getDetails();
		getOrthology();
		don.setDataLoaded();

	}
	
	private void getOrthology()
	{
		String[] det={reaction.getLabel()};
		String query=DAWISQueries.getReactionOrthology;

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
//		String[] det = { reaction.getLabel() };
//		String query = DAWISQueries.getReactionOrthology;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			don.setOrthology(res[1] + ": " + res[2]);
//		}
//
//	}
	
	private void getDetails()
	{
		String[] det={reaction.getLabel()};
		String query=DAWISQueries.getReactionDetails;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			if (reaction.getName().equals(""))
			{
				reaction.setName(res[1]);
				don.setName(reaction.getName());
			}
			
			don.setComment(res[4]);
			don.setDefinition(res[5]);
			don.setEquation(res[6]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getDetails() {
//
//		String[] det = { reaction.getLabel() };
//		String query = DAWISQueries.getReactionDetails;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			if (reaction.getName().equals("")) {
//				reaction.setName(res[1]);
//				don.setName(reaction.getName());
//			}
//			don.setComment(res[4]);
//			don.setDefinition(res[5]);
//			don.setEquation(res[6]);
//		}
//
//	}

}
