package biologicalObjects.nodes;

import java.util.ArrayList;
import java.util.Vector;

import pojos.DBColumn;
import biologicalElements.Elementdeclerations;
import configurations.Wrapper;
import database.dawis.DAWISQueries;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Protein extends BiologicalNodeAbstract {

	private String aaSequence = "";
	Vector<String> accessionnumbers = new Vector<String>();
	String uniprotProtein = "";
	public static boolean labelIsAccessionNumber = false;

	public Protein(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.protein);
		shapes = new VertexShapes();
		setShape(shapes.getEllipse());
		setAbstract(false);
		setReference(false);
	}

	@Override
	public void rebuildShape(VertexShapes vs) {
		//setShape(vs.getEllipse(getVertex()));
	}

	public String getAaSequence() {
		return aaSequence;
	}

	public void setAaSequence(String aaSequence)
	{
		this.aaSequence=aaSequence;
	}

	private void getAccessionNumbers()
	{

		String[] det={getLabel()};
		String query=DAWISQueries.getProteinAccessionnumber;

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();

			accessionnumbers.add(res[1]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getAccessionNumbers() {
//
//		String[] det = { getLabel() };
//		String query = DAWISQueries.getProteinAccessionnumber;
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//		Iterator<String[]> it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] res = it.next();
//			accessionnumbers.add(res[1]);
//		}
//
//	}
	
	public void lookUpAtAllDatabases()
	{
		String db=getDB();
		DAWISNode node=getDAWISNode();
		String[] det={getLabel()};

		ArrayList<DBColumn> results=new ArrayList<DBColumn>();
		
		if (db.equalsIgnoreCase("UniProt"))
		{
			getAccessionNumbers();
			if (accessionnumbers==null)
			{
				labelIsAccessionNumber=true;
				getUniProtID();
			}
			else
			{
				uniprotProtein=getLabel();
			}
			// testForEnzyme(uniprotProtein);
			String query=DAWISQueries.getHPRDProteinFromUniprotProtein+"'"+uniprotProtein+"' or u.accession_number = '"+uniprotProtein+"'";
			results=new Wrapper().requestDbContent(3, query);

			for (DBColumn column : results)
			{
				String[] res=column.getColumn();
				String id=res[0];
				node.addID(id, getLabel());
				node.addIDDBRelation("HPRD", id);
			}
		}
		else if (db.equalsIgnoreCase("HPRD"))
		{

			results=new Wrapper().requestDbContent(3, DAWISQueries.getUniprotProteinFromHPRDProtein, det);
			
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();

				String id=res[0];
				node.addID(id, getLabel());
				node.addIDDBRelation("UniProt", id);
			}

		}
		else if (db.equalsIgnoreCase("TRANSFAC"))
		{
			String protId[]={" "+getLabel()};
			results=new Wrapper().requestDbContent(3, DAWISQueries.getUniprotProteinFromTransfacProtein2, protId);

			for (DBColumn column : results)
			{
				String[] res=column.getColumn();

				String id=res[0];

				node.addID(id, getLabel());
				node.addIDDBRelation("UniProt", id);

				if (!id.equals(""))
				{
					results=new Wrapper().requestDbContent(3, DAWISQueries.getHPRDProteinFromUniprotProtein, det);
					
					for (DBColumn column2 : results)
					{
						String[] res2=column2.getColumn();

						String id1=res2[0];
						node.addID(id1, getLabel());
						node.addIDDBRelation("HPRD", id1);
					}
				}
			}

			results=new Wrapper().requestDbContent(3, DAWISQueries.getTPProteinFromTFProtein, det);
			
			for (DBColumn column : results)
			{
				String[] res2=column.getColumn();
				String id1=res2[0];
				node.addID(id1, getLabel());
				node.addIDDBRelation("Transpath", id1);

			}

		}
		else if (db.equalsIgnoreCase("Transpath"))
		{
			results=new Wrapper().requestDbContent(3, DAWISQueries.getTFProteinFromTPProtein, det);
			
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();

				String id=res[0];
				node.addID(id, getLabel());
				node.addIDDBRelation("Transfac", id);

				if (!id.equals(""))
				{
					results=new Wrapper().requestDbContent(3, DAWISQueries.getUniprotProteinFromTransfacProtein2, det);
					for (DBColumn column2 : results)
					{
						String[] res2=column2.getColumn();

						String id1=res2[0];
						node.addID(id1, getLabel());
						node.addIDDBRelation("UniProt", id1);

						if (!id1.equals(""))
						{
							results=new Wrapper().requestDbContent(3, DAWISQueries.getHPRDProteinFromUniprotProtein, det);
							
							for (DBColumn column3 : results)
							{
								String[] res3=column3.getColumn();

								String id2=res3[0];
								node.addID(id2, getLabel());
								node.addIDDBRelation("UniProt", id2);

							}
						}
					}
				}
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	public void lookUpAtAllDatabases() {
//
//		String db = getDB();
//		DAWISNode node = getDAWISNode();
//
//		String[] det = { getLabel() };
//
//		Vector<String[]> results;
//		Iterator<String[]> it;
//
//		if (db.equalsIgnoreCase("UniProt")) {
//			getAccessionNumbers();
//			if (accessionnumbers == null) {
//				labelIsAccessionNumber = true;
//				getUniProtID();
//			} else {
//				uniprotProtein = getLabel();
//			}
//			// testForEnzyme(uniprotProtein);
//			String query = DAWISQueries.getHPRDProteinFromUniprotProtein + "'"
//					+ uniprotProtein + "' or u.accession_number = '"
//					+ uniprotProtein + "'";
//			results = new Wrapper().requestDbContent(3, query);
//
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("HPRD", id);
//
//			}
//		} else if (db.equalsIgnoreCase("HPRD")) {
//
//			results = new Wrapper().requestDbContent(3,
//					DAWISQueries.getUniprotProteinFromHPRDProtein, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("UniProt", id);
//			}
//
//		} else if (db.equalsIgnoreCase("TRANSFAC")) {
//
//			String protId[] = { " " + getLabel() };
//			results = new Wrapper().requestDbContent(3,
//					DAWISQueries.getUniprotProteinFromTransfacProtein2, protId);
//
//			it = results.iterator();
//
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//
//				node.addID(id, getLabel());
//				node.addIDDBRelation("UniProt", id);
//
//				if (!id.equals("")) {
//					results = new Wrapper().requestDbContent(3,
//							DAWISQueries.getHPRDProteinFromUniprotProtein, det);
//					it = results.iterator();
//					while (it.hasNext()) {
//						String[] res1 = it.next();
//						String id1 = res1[0];
//						node.addID(id1, getLabel());
//						node.addIDDBRelation("HPRD", id1);
//
//					}
//				}
//			}
//
//			results = new Wrapper().requestDbContent(3,
//					DAWISQueries.getTPProteinFromTFProtein, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res1 = it.next();
//				String id1 = res1[0];
//				node.addID(id1, getLabel());
//				node.addIDDBRelation("Transpath", id1);
//
//			}
//
//		} else if (db.equalsIgnoreCase("Transpath")) {
//
//			results = new Wrapper().requestDbContent(3,
//					DAWISQueries.getTFProteinFromTPProtein, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("Transfac", id);
//
//				if (!id.equals("")) {
//					results = new Wrapper().requestDbContent(3,
//							DAWISQueries.getUniprotProteinFromTransfacProtein2,
//							det);
//					it = results.iterator();
//					while (it.hasNext()) {
//						String[] res1 = it.next();
//						String id1 = res1[0];
//						node.addID(id1, getLabel());
//						node.addIDDBRelation("UniProt", id1);
//
//						if (!id1.equals("")) {
//							results = new Wrapper()
//									.requestDbContent(
//											3,
//											DAWISQueries.getHPRDProteinFromUniprotProtein,
//											det);
//							it = results.iterator();
//							while (it.hasNext()) {
//								String[] res2 = it.next();
//								String id2 = res2[0];
//								node.addID(id2, getLabel());
//								node.addIDDBRelation("UniProt", id2);
//
//							}
//						}
//					}
//				}
//			}
//		}
//	}
	
	private void getUniProtID()
	{
		DAWISNode node=getDAWISNode();
		String[] det={getLabel()};
		String query=DAWISQueries.getUniProtProteinID;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			node.addID(res[0], getLabel());
			node.addIDDBRelation("UniProt", res[0]);

			uniprotProtein=res[0];
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getUniProtID() {
//		DAWISNode node = getDAWISNode();
//		String[] det = { getLabel() };
//		String query = DAWISQueries.getUniProtProteinID;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			node.addID(res[0], getLabel());
//			node.addIDDBRelation("UniProt", res[0]);
//
//			uniprotProtein = res[0];
//		}
//	}
}
