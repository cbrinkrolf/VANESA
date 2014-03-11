package database.ppi;

//import edu.uci.ics.jung.graph.Vertex;
import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import pojos.DBColumn;
import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;
import biologicalObjects.edges.PhysicalInteraction;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Protein;
import configurations.Wrapper;

public class PPIConnector extends SwingWorker {

	private HashMap<String, String[]> entries2infos = new HashMap<String, String[]>();
	private ArrayList<String[]> connections = new ArrayList<String[]>();
	private HashSet<String> conSet = new HashSet<String>();
	private HashSet<String> newNodes = new HashSet<String>();
	private HashMap<String, BiologicalNodeAbstract> name2Vertex = new HashMap<String, BiologicalNodeAbstract>();

	private Pathway pw;
	private MyGraph myGraph;

	private ProgressBar bar;
	private int buildingDepth;

	private int x = 1;
	private int y = 1;

	private String root_id;

	private String[] root_details;
	private ArrayList<DBColumn> results;
	private String database;

	private String query;
	private String[] param;
	private boolean finalise = true;
	private boolean binary;
	private boolean complex;

	public PPIConnector(ProgressBar bar, String[] details, String db) {

		this.bar = bar;
		this.root_details = details;
		this.root_id = details[3];
		this.database = db;

	}


	private void stopVisualizationModel() {
		myGraph.lockVertices();
		myGraph.stopVisualizationModel();
	}

	private void startVisualizationModel() {
		myGraph.unlockVertices();
		myGraph.restartVisualizationModel();
	}

	private void drawNodes() {

		Iterator<String> i = entries2infos.keySet().iterator();
		String id;
		String[] infos;
		Protein protein ;
		
		while (i.hasNext()) {

			id = i.next();
			infos = entries2infos.get(id);

			protein = new Protein(infos[0], infos[1]);
			protein.setAaSequence(infos[2]);
			if (id.equals(root_id)) {
				protein.setColor(Color.RED);
			}

			name2Vertex.put(id, protein);
			pw.addVertex(protein, new Point(x*100, y*100));

			//myGraph.moveVertex(protein.getVertex(), x * 100, y * 100);

			if (x > Math.sqrt(entries2infos.size())) {
				x = 1;
				y++;
			} else {
				x++;
			}

		}
	}

	private void drawEdges() {

		Iterator<String[]> it = connections.iterator();
		String[] entry;
		BiologicalNodeAbstract first;
		BiologicalNodeAbstract second;
		while (it.hasNext()) {
			entry = it.next();
			first = (name2Vertex.get(entry[0]));
			second = (name2Vertex.get(entry[1]));

			// System.out.println(entry[0] + " " + first+ "   ---   " + entry[1]
			// + " " +second);
			if(myGraph.getJungGraph().findEdge(first, second) == null && (first != second)){
			//if (!adjazenzList.doesEdgeExist(first, second) && (first != second)) {
				buildEdge(first, second, false);
			}
		}
	}

	private void buildEdge(BiologicalNodeAbstract one,
			BiologicalNodeAbstract two, boolean directed) {

		PhysicalInteraction r = new PhysicalInteraction( "", "", one, two);

		r.setDirected(directed);
		r.setReference(false);
		r.setHidden(false);
		r.setVisible(true);

		pw.addEdge(r);
	}

	public int getSearchDepth() {
		return buildingDepth;
	}

	public void setSearchDepth(int searchDepth) {
		this.buildingDepth = searchDepth;
	}

	public MyGraph getGraph() {
		return myGraph;
	}

	@Override
	protected Object doInBackground() throws Exception {

		Runnable run = new Runnable() {
			public void run() {
				bar = new ProgressBar();
				bar.init(100, "   Loading Data from " + database, true);
				bar.setProgressBarString("Querying Database");
			}
		};
		SwingUtilities.invokeLater(run);

		if (binary) {
			newNodes = new HashSet<String>();
			newNodes.add(root_id);
			buildInterations(true);
		}
		if (complex) {
			newNodes = new HashSet<String>();
			newNodes.add(root_id);
			buildInterations(false);
		}

		return null;
	}

	private void buildInterations(boolean binary) {

		//MARTIN Abfrage f�r neue und alte DB (switch)
		
		if (database.equals("HPRD")) {
			if(MainWindow.useOldDB)
				query = PPIqueries.hprd_interactionsForID;
			else
				query = PPIqueries.hprd_interactionsForID_new;
			
		} else if (database.equals("MINT")) {
			if (binary) {
				query = PPIqueries.mint_interactionsForID;
			} else {
				query = PPIqueries.mint_complexInteractionsForID;
			}
		} else if (database.equals("IntAct")) {
			if (binary) {
				query = PPIqueries.intact_interactionsForID;
			} else {
				query = PPIqueries.intact_complexInteractionsForID;
			}
		}

		for (int i = 0; i < buildingDepth + 1; i++) {

			if (i == buildingDepth) {
				if (!finalise) {
					break;
				}
			}

			HashSet<String> currentNodeSet = (HashSet<String>) newNodes.clone();
			newNodes = new HashSet<String>();
			
			for (String node : currentNodeSet) {

				String[] param2 = { node };
				int dbID = 4;
				if (database.equals("HPRD")) {
					param2 = new String[2];
					param2[0] = node;
					param2[1] = node;
					dbID = 3;
				}

				results = new Wrapper().requestDbContent(dbID, query, param2);

				for (DBColumn column : results) {
					String[] row = column.getColumn();

					String idA = row[0];
					String shortLabel_A = row[1];
					String fullName_A = row[2];
					String sequence_A = row[3];

					String idB = row[4];
					String shortLabel_B = row[5];
					String fullName_B = row[6];
					String sequence_B = row[7];

					// connect leafs
					if (i == buildingDepth) {
						if (entries2infos.containsKey(idA)
								&& entries2infos.containsKey(idB)) {
							String[] connection = { idA, idB };
							String key1 = idA + "-" + idB;
							String key2 = idB + "-" + idA;

							if (conSet.contains(key1) || conSet.contains(key2)) {
								// TODO was soll das? -by Benny -
							} else {
								connections.add(connection);
								conSet.add(key1);
								// conSet.add(key2);
							}
						}
					} else {
						if (!entries2infos.containsKey(idA)) {
							String[] infos_A = { shortLabel_A, fullName_A,
									sequence_A };
							entries2infos.put(idA, infos_A);
							newNodes.add(idA);
						}
						if (!entries2infos.containsKey(idB)) {
							String[] infos_B = { shortLabel_B, fullName_B,
									sequence_B };
							entries2infos.put(idB, infos_B);
							newNodes.add(idB);
						}

						String[] connection = { idA, idB };
						String key1 = idA + "-" + idB;
						String key2 = idB + "-" + idA;
						if (conSet.contains(key1) || conSet.contains(key2)) {

						} else {
							connections.add(connection);
							conSet.add(key1);
							// conSet.add(key2);
						}
					}
				}
			}
		}

	}

	// private void buildInterations(boolean binary){
	//
	// if (database.equals("HPRD")) {
	// query = PPIqueries.hprd_interactionsForID;
	// } else if(database.equals("MINT")){
	// if (binary) {
	// query = PPIqueries.mint_interactionsForID;
	// } else {
	// query = PPIqueries.mint_complexInteractionsForID;
	// }
	// } else if(database.equals("IntAct")){
	// if (binary) {
	// query = PPIqueries.intact_interactionsForID;
	// } else {
	// query = PPIqueries.intact_complexInteractionsForID;
	// }
	// }
	//
	// for (int i = 0; i < buildingDepth+1; i++) {
	//
	// if (i == buildingDepth) {
	// if (!finalise ) {
	// break;
	// }
	// }
	//
	// HashSet<String> currentNodeSet = (HashSet<String>) newNodes.clone();
	// newNodes = new HashSet<String>();
	// for (String node : currentNodeSet) {
	//
	// String[] param2 = {node};
	// int dbID = 4;
	// if (database.equals("HPRD")) {
	// param2 = new String[2];
	// param2[0] = node;
	// param2[1] = node;
	// dbID = 3;
	// }
	//
	// results = new Wrapper().requestDbContent(dbID, query, param2);
	// Iterator iterator = results.iterator();
	// while (iterator.hasNext()) {
	// String[] row = (String[]) iterator.next();
	//
	// String idA = row[0];
	// String shortLabel_A = row[1];
	// String fullName_A = row[2];
	// String sequence_A = row[3];
	//
	// String idB = row[4];
	// String shortLabel_B = row[5];
	// String fullName_B = row[6];
	// String sequence_B = row[7];
	//
	// // connect leafs
	// if (i == buildingDepth) {
	// if (entries2infos.containsKey(idA) && entries2infos.containsKey(idB) ) {
	// String[] connection = {idA, idB};
	// String key1 = idA+"-"+idB;
	// String key2 = idB+"-"+idA;
	// if (conSet.contains(key1) || conSet.contains(key2)) {
	//
	// }else{
	// connections.add(connection);
	// conSet.add(key1);
	// // conSet.add(key2);
	// }
	// }
	// }else{
	// if (!entries2infos.containsKey(idA)) {
	// String[] infos_A = {shortLabel_A, fullName_A, sequence_A};
	// entries2infos.put(idA, infos_A);
	// newNodes.add(idA);
	// }
	// if (!entries2infos.containsKey(idB)) {
	// String[] infos_B = {shortLabel_B, fullName_B, sequence_B};
	// entries2infos.put(idB, infos_B);
	// newNodes.add(idB);
	// }
	//
	// String[] connection = {idA, idB};
	// String key1 = idA+"-"+idB;
	// String key2 = idB+"-"+idA;
	// if (conSet.contains(key1) || conSet.contains(key2)) {
	//
	// }else{
	// connections.add(connection);
	// conSet.add(key1);
	// // conSet.add(key2);
	// }
	// }
	// }
	// }
	// }
	//
	// }

	@Override
	public void done() {

		bar.setProgressBarString("Drawing network");

		String rootName = "";
		if (database.equals("HPRD")) {
			rootName = root_details[1];
		} else {
			rootName = root_details[0];
		}
		// System.out.println(root_details[0] + ", " + root_details[1] + ", " +
		// root_details[2] );
		pw = new CreatePathway(database + " network for " + rootName
				+ " (depth=" + buildingDepth + ")").getPathway();

		// pw.setOrganism(organism);
		// pw.setLink(pathwayLink);
		// pw.setImagePath(pathwayImage);
		// pw.setNumber(pathwayNumber);
		myGraph = pw.getGraph();

		stopVisualizationModel();
		drawNodes();
		drawEdges();
		startVisualizationModel();

		myGraph.changeToGEMLayout();
		myGraph.fitScaleOfViewer(myGraph.getSatelliteView());
		myGraph.normalCentering();
		bar.closeWindow();

		MainWindow window = MainWindowSingelton.getInstance();
		window.updateOptionPanel();
		window.setVisible(true);

	}

	public void setFinaliseGraph(boolean finaliseGraph) {
		this.finalise = finaliseGraph;
	}

	public void setIncludeBinaryInteractions(boolean binaryInteractions) {
		this.binary = binaryInteractions;
	}

	public void setIncludeComplexInteractions(boolean complexInteractions) {
		this.complex = complexInteractions;
	}

}
