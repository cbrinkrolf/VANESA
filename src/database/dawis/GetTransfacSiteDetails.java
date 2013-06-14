package database.dawis;

import java.util.ArrayList;

import pojos.DBColumn;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Site;
import configurations.Wrapper;

public class GetTransfacSiteDetails {
	private Site site = null;
	private DAWISNode don = null;
	
	public GetTransfacSiteDetails(Site s){
		site = s;
		don = site.getDAWISNode();
		fillNodeWithInformations();
	}

	private void fillNodeWithInformations() {
		don.setID(site.getLabel());
		getFactorDetails();
	}
	
	private void getFactorDetails()
	{
		String[] det={site.getLabel()};
		String query=DAWISQueries.getSiteDetails;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			don.setComment(res[1]);
			don.setDefinition(res[2]);
			don.setElement(res[3]);
			don.setLocation(res[4]);
			don.setReference(res[6]);
			don.setEndPoint(res[7]);
			don.setStartPoint(res[8]);
			don.setType(res[9]);
		}
	}
	
//	private void getFactorDetails() {
//		
//		String[] det = { site.getLabel() };
//		String query = DAWISQueries.getSiteDetails;
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//
//			String[] res = (String[]) it.next();
//			don.setComment(res[1]);
//			don.setDefinition(res[2]);
//			don.setElement(res[3]);
//			don.setLocation(res[4]);
//			don.setReference(res[6]);
//			don.setEndPoint(res[7]);
//			don.setStartPoint(res[8]);
//			don.setType(res[9]);
//	
//		}
//	}
}
