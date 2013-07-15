package biologicalObjects.nodes;

import java.util.ArrayList;
import java.util.Vector;

import pojos.DBColumn;
import biologicalElements.Elementdeclerations;
import configurations.Wrapper;
import database.dawis.DAWISQueries;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Gene extends BiologicalNodeAbstract {

	private String ntSequence = "";

	private Vector<String[]> proteins = new Vector<String[]>();
	private Vector<String[]> enzymes = new Vector<String[]>();

	private boolean hasProteins = false;
	private boolean hasEnzymes = false;

	public Gene(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.gene);
		shapes = new VertexShapes();
		setShape(shapes.getEllipse());
	}
	
	public void lookUpAtAllDatabases()
	{
		DAWISNode node=getDAWISNode();
		String db=getDB();
		String[] det={getLabel()};
		ArrayList<DBColumn> results=new ArrayList<DBColumn>();
		
		if (db.equalsIgnoreCase("KEGG"))
		{
			results=new Wrapper().requestDbContent(3, DAWISQueries.getTPGeneFromKEGGGene, det);
			
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();
				String id=res[0];
				
				node.addID(id, getLabel());
				node.addIDDBRelation("Transpath", id);
			}

			results=new Wrapper().requestDbContent(3, DAWISQueries.getTFGeneFromKEGGGene, det);
			
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();
				String id=res[0];
				node.addID(id, getLabel());
				node.addIDDBRelation("Transfac", id);
			}
		}
		else if (db.equalsIgnoreCase("Transpath"))
		{

			results=new Wrapper().requestDbContent(3, DAWISQueries.getKEGGGeneFromTPGene, det);
			
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();
				String id=res[0];
				
				node.addID(id, getLabel());
				node.addIDDBRelation("KEGG", id);
			}

			results=new Wrapper().requestDbContent(3, DAWISQueries.getTFGeneFromTPGene, det);
			
			for (DBColumn column : results)
			{
				String[] res2=column.getColumn();
				String id2=res2[0];
				
				node.addID(id2, getLabel());
				node.addIDDBRelation("Transfac", id2);
			}

		}
		else if (db.equalsIgnoreCase("Transfac"))
		{
			results=new Wrapper().requestDbContent(3, DAWISQueries.getKEGGGeneFromTFGene, det);
			
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();
				String id=res[0];
				
				node.addID(id, getLabel());
				node.addIDDBRelation("KEGG", id);

				if (!id.equals(""))
				{
					String[] det2={id};
					results=new Wrapper().requestDbContent(3, DAWISQueries.getTPGeneFromKEGGGene, det2);
					
					for (DBColumn column2 : results)
					{
						String[] res2=column2.getColumn();
						String id2=res2[0];
						node.addID(id2, getLabel());
						node.addIDDBRelation("Transpath", id2);
					}
				}
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	public void lookUpAtAllDatabases() {
//		DAWISNode node = getDAWISNode();
//		String db = getDB();
//		String[] det = { getLabel() };
//		Vector<String[]> results;
//		Iterator<String[]> it;
//
//		if (db.equalsIgnoreCase("KEGG")) {
//
//			results = new Wrapper().requestDbContent(3,
//					DAWISQueries.getTPGeneFromKEGGGene, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("Transpath", id);
//			}
//
//			results = new Wrapper().requestDbContent(3,
//					DAWISQueries.getTFGeneFromKEGGGene, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("Transfac", id);
//
//			}
//
//		} else if (db.equalsIgnoreCase("Transpath")) {
//
//			results = new Wrapper().requestDbContent(3,
//					DAWISQueries.getKEGGGeneFromTPGene, det);
//			it = results.iterator();
//
//			while (it.hasNext()) {
//
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("KEGG", id);
//
//			}
//
//			results = new Wrapper().requestDbContent(3,
//					DAWISQueries.getTFGeneFromTPGene, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res2 = it.next();
//				String id2 = res2[0];
//				node.addID(id2, getLabel());
//				node.addIDDBRelation("Transfac", id2);
//			}
//
//		} else if (db.equalsIgnoreCase("Transfac")) {
//
//			results = new Wrapper().requestDbContent(3,
//					DAWISQueries.getKEGGGeneFromTFGene, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("KEGG", id);
//
//				if (!id.equals("")) {
//					String[] det2 = { id };
//					results = new Wrapper().requestDbContent(3,
//							DAWISQueries.getTPGeneFromKEGGGene, det2);
//					it = results.iterator();
//					while (it.hasNext()) {
//						String[] res2 = it.next();
//						String id2 = res2[0];
//						node.addID(id2, getLabel());
//						node.addIDDBRelation("Transpath", id2);
//					}
//				}
//			}
//		}
//	}

	public boolean hasProteins() {
		return hasProteins;
	}

	public boolean hasEnzymes() {
		return hasEnzymes;
	}

	public void addProtein(String[] proteinID) {

		if (!proteins.contains(proteinID)) {
			proteins.add(proteinID);
		}
		hasProteins = true;
	}

	public Vector<String[]> getProteins() {
		return proteins;
	}

	public void addEnzyme(String[] enzymeID) {
		if (!enzymes.contains(enzymeID)) {
			enzymes.add(enzymeID);
		}
		hasEnzymes = true;
	}

	public Vector<String[]> getEnzymes() {
		return enzymes;
	}

	@Override
	public void rebuildShape(VertexShapes vs) {
//		setShape(vs.getEllipse(getVertex()));
	}

	public String getNtSequence() {
		return ntSequence;
	}

	public void setNtSequence(String ntSequence) {
		this.ntSequence = ntSequence;
	}

	@Override
	public void setDB(String geneDB) {
		this.db = geneDB;
	}

	@Override
	public String getDB() {
		return this.db;
	}
}
