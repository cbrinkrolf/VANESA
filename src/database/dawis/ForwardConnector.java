package database.dawis;

import graph.GraphInstance;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.util.ArrayList;

import pojos.DBColumn;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.PathwayMap;
import configurations.Wrapper;
import database.kegg.KEGGConnector;

public class ForwardConnector {

	ProgressBar bar;
	GraphInstance graphInstance = new GraphInstance();
	MainWindow mW = MainWindowSingelton.getInstance();
	PathwayMap pathway;
	DAWISNode node;

	public ForwardConnector(PathwayMap pw) {

		pathway = pw;
		node = pathway.getDAWISNode();
		prozessPathway();

	}

	private void prozessPathway() {

		if (node.getName().equals("")) {
			getPathwayName();
		}
		if (node.getOrganism().equals("")) {
			getPathwayOrganism();
		}

		String name, organism;
		name = node.getName();
		organism = node.getOrganism();
		if (organism == null) {
			organism = "map";
		} else {
			if (organism.equals("")) {
				organism = "map";
			}
		}

		String[] data = { name, organism, "", "", "" };
		KEGGConnector kc = new KEGGConnector(bar, data, true);
		kc.execute();

	}
	
	private void getPathwayOrganism()
	{
		String[] attributes={pathway.getLabel()};
		ArrayList<DBColumn> result=new Wrapper().requestDbContent(3, DAWISQueries.getPathwayOrganism2, attributes);
		
		for (DBColumn column : result)
		{
			String[] organism=column.getColumn();
			
			pathway.setOrganism(organism[0]);
			node.setOrganism(organism[0]);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getPathwayOrganism() {
//		String[] attributes = { pathway.getLabel() };
//		Vector<String[]> result = new Wrapper().requestDbContent(3,
//				DAWISQueries.getPathwayOrganism2, attributes);
//		Iterator<String[]> it = result.iterator();
//		while (it.hasNext()) {
//			String[] organism = it.next();
//			pathway.setOrganism(organism[0]);
//			node.setOrganism(organism[0]);
//		}
//	}
	
	private void getPathwayName()
	{
		String id=pathway.getLabel();

		String query=DAWISQueries.getKEGGPathwayName;

		String[] det={id};
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);

		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			
			pathway.setName(res[0]);
			node.setName(pathway.getName());
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private void getPathwayName() {
//
//		String id = pathway.getLabel();
//
//		String query = DAWISQueries.getKEGGPathwayName;
//
//		String[] det = { id };
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, det);
//
//		Iterator<String[]> it = results.iterator();
//		String[] s = null;
//		while (it.hasNext()) {
//			s = it.next();
//			pathway.setName(s[0]);
//			node.setName(pathway.getName());
//		}
//	}

}
