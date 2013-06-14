package database.kegg;

import graph.ContainerSingelton;
import graph.CreatePathway;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.MergeGraphs;
import gui.MainWindow;
import gui.ProgressBar;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import biologicalElements.Pathway;
import biologicalObjects.edges.Compound;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.SmallMolecule;

import pojos.DBColumn;
import configurations.Wrapper;
import database.Connection.DatabaseQueryValidator;
import database.kegg.gui.KEGGResultWindow;

public class KeggSearch extends SwingWorker implements PropertyChangeListener {

	private String pathway = new String();
	private String enzyme = new String();
	private String gene = new String();
	private String compound = new String();
	private String organismus = new String();

	private DatabaseQueryValidator dqv = new DatabaseQueryValidator();

	private ArrayList<DBColumn> results = new ArrayList<DBColumn>();

	private boolean continueProgress = false;
	private KEGGResultWindow dsrw = null;
	private MainWindow w = null;
	private ProgressBar bar = null;
	private Pathway mergePW = null;

	public KeggSearch(String[] input) {
		pathway = input[0];
		organismus = input[1];
		enzyme = input[2];
		gene = input[3];
		compound = input[4];
	}

	public KeggSearch(String[] input, MainWindow w, ProgressBar bar,
			Pathway mergePW) {
		pathway = input[0];
		organismus = input[1];
		enzyme = input[2];
		gene = input[3];
		compound = input[4];
		this.w = w;
		this.bar = bar;
		this.mergePW = mergePW;
	}

	public ArrayList<DBColumn> requestDbContent() {
		String queryStart = "Select pathway.pathway_name from ";
		String queryEnd = "";
		boolean firstEntries = false;
		boolean organism = false;

		if (!pathway.equals("")) {
			queryStart = queryStart + "(" + KEGGQueries.KEGGPathwayQuery
					+ dqv.prepareString(pathway, "p.pathway_name", "p.title")
					+ ") as pathway";
			firstEntries = true;
		}

		if (!organismus.equals("")) {
			organism = true;
		}

		if (!enzyme.equals("")) {
			if (firstEntries) {
				queryStart = queryStart + " Inner join ("
						+ KEGGQueries.KEGGEnzymeQuery
						+ dqv.prepareString(enzyme, "n.entry", "n.name")
						+ ") as enzyme ";
				queryEnd = " on pathway.pathway_name=enzyme.pathway_name";

			} else {
				firstEntries = true;
				queryStart = queryStart + "(" + KEGGQueries.KEGGEnzymeQuery
						+ dqv.prepareString(enzyme, "n.entry", "n.name")
						+ ") as pathway ";
			}
		}

		if (!gene.equals("")) {

			if (firstEntries) {
				queryStart = queryStart + " Inner join ("
						+ KEGGQueries.KEGGGeneQuery
						+ dqv.prepareString(gene, "n.entry", "n.name")
						+ ") as gene ";

				if (queryEnd.equals("")) {
					queryEnd = " on pathway.pathway_name=gene.pathway_name";
				} else {
					queryEnd = queryEnd + "=gene.pathway_name";
				}

			} else {
				queryStart = queryStart + "(" + KEGGQueries.KEGGGeneQuery
						+ dqv.prepareString(gene, "n.entry", "n.name")
						+ ") as pathway ";
				firstEntries = true;
			}
		}

		if (!compound.equals("")) {

			if (firstEntries) {
				queryStart = queryStart + " Inner join ("
						+ KEGGQueries.KEGGCompoundQuery
						+ dqv.prepareString(compound, "c.entry", "c.name")
						+ ") as compound ";

				if (queryEnd.equals("")) {
					queryEnd = " on pathway.pathway_name=compound.pathway_name";
				} else {
					queryEnd = queryEnd + "=compound.pathway_name";
				}

			} else {
				queryStart = queryStart + "(" + KEGGQueries.KEGGCompoundQuery
						+ dqv.prepareString(compound, "c.entry", "c.name")
						+ ") as pathway ";
				firstEntries = true;
			}
		}

		String pathway_names = "(";

		ArrayList<DBColumn> tempResults = new ArrayList<DBColumn>();

		if (firstEntries) {
			tempResults = new Wrapper().requestDbContent(2, queryStart
					+ queryEnd + " LIMIT 0,1000;");
			boolean firstPathwayName = true;

			for (DBColumn column : tempResults) {
				String[] d = column.getColumn();

				if (firstPathwayName) {
					pathway_names = pathway_names + "'" + d[0] + "'";
					firstPathwayName = false;
				} else {
					pathway_names = pathway_names + ",'" + d[0] + "'";
				}
			}
		}

		pathway_names = pathway_names + ")";

		String lastQuery;

		if (!organism && !firstEntries) {
			return new ArrayList<DBColumn>();
		} else if (!organism && tempResults.size() == 0) {
			return new ArrayList<DBColumn>();
		} else if (organism && firstEntries && tempResults.size() == 0) {
			return new ArrayList<DBColumn>();
		} else if (!organism && tempResults.size() > 0) {
			lastQuery = "Select p.pathway_name, p.title, t.name from kegg_pathway p "
					+ "LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where p.pathway_name In "
					+ pathway_names;
		} else if (organism && pathway_names.length() < 4) {
			lastQuery = "Select p.pathway_name, p.title, t.name from kegg_pathway p "
					+ "LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where "
					+ dqv.prepareString(organismus, "t.name", "t.latin_name");
		} else {
			lastQuery = "Select p.pathway_name, p.title, t.name from kegg_pathway p "
					+ "LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where p.pathway_name In "
					+ pathway_names
					+ " AND "
					+ dqv.prepareString(organismus, "t.name", "t.latin_name");
		}

		return new Wrapper().requestDbContent(2, lastQuery + " LIMIT 0,1000;");

	}

	// public Vector requestDbContent() throws SQLException,
	// InstantiationException,
	// IllegalAccessException, ClassNotFoundException {
	//
	// String queryStart = "Select pathway.pathway_name from ";
	// String queryEnd = "";
	// boolean firstEntries = false;
	// boolean organism = false;
	//
	// if (!pathway.equals("")) {
	// queryStart = queryStart + "(" + KEGGQueries.KEGGPathwayQuery
	// + dqv.prepareString(pathway, "p.pathway_name", "p.title")
	// + ") as pathway";
	// firstEntries = true;
	// }
	//
	// if (!organismus.equals("")) {
	// organism = true;
	// }
	//
	// if (!enzyme.equals("")) {
	// if (firstEntries) {
	// queryStart = queryStart + " Inner join ("
	// + KEGGQueries.KEGGEnzymeQuery
	// + dqv.prepareString(enzyme, "n.entry", "n.name")
	// + ") as enzyme ";
	// queryEnd = " on pathway.pathway_name=enzyme.pathway_name";
	//
	// } else {
	// firstEntries = true;
	// queryStart = queryStart + "(" + KEGGQueries.KEGGEnzymeQuery
	// + dqv.prepareString(enzyme, "n.entry", "n.name")
	// + ") as pathway ";
	// }
	// }
	//
	// if (!gene.equals("")) {
	//
	// if (firstEntries) {
	// queryStart = queryStart + " Inner join ("
	// + KEGGQueries.KEGGGeneQuery
	// + dqv.prepareString(gene, "n.entry", "n.name")
	// + ") as gene ";
	//
	// if (queryEnd.equals("")) {
	// queryEnd = " on pathway.pathway_name=gene.pathway_name";
	// } else {
	// queryEnd = queryEnd + "=gene.pathway_name";
	// }
	//
	// } else {
	// queryStart = queryStart + "(" + KEGGQueries.KEGGGeneQuery
	// + dqv.prepareString(gene, "n.entry", "n.name")
	// + ") as pathway ";
	// firstEntries = true;
	// }
	// }
	//
	// if (!compound.equals("")) {
	//
	// if (firstEntries) {
	// queryStart = queryStart + " Inner join ("
	// + KEGGQueries.KEGGCompoundQuery
	// + dqv.prepareString(compound, "c.entry", "c.name")
	// + ") as compound ";
	//
	// if (queryEnd.equals("")) {
	// queryEnd = " on pathway.pathway_name=compound.pathway_name";
	// } else {
	// queryEnd = queryEnd + "=compound.pathway_name";
	// }
	//
	// } else {
	// queryStart = queryStart + "(" + KEGGQueries.KEGGCompoundQuery
	// + dqv.prepareString(compound, "c.entry", "c.name")
	// + ") as pathway ";
	// firstEntries = true;
	// }
	// }
	//
	// String pathway_names = "(";
	//
	// Vector tempResults = new Vector();
	// if (firstEntries) {
	// tempResults = new Wrapper().requestDbContent(2, queryStart
	// + queryEnd + " LIMIT 0,1000;");
	// Iterator tempIt = tempResults.iterator();
	// boolean firstPathwayName = true;
	// while (tempIt.hasNext()) {
	// String[] d = (String[]) tempIt.next();
	// if (firstPathwayName) {
	// pathway_names = pathway_names + "'" + d[0] + "'";
	// firstPathwayName = false;
	// } else {
	// pathway_names = pathway_names + ",'" + d[0] + "'";
	// }
	// }
	// }
	// pathway_names = pathway_names + ")";
	//
	// String lastQuery;
	//
	// if (!organism && !firstEntries) {
	// return new Vector();
	// } else if (!organism && tempResults.size() == 0) {
	// return new Vector();
	// } else if (organism && firstEntries && tempResults.size() == 0) {
	// return new Vector();
	// } else if (!organism && tempResults.size() > 0) {
	// lastQuery = "Select p.pathway_name, p.title, t.name from kegg_pathway p "
	// +
	// "LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where p.pathway_name In "
	// + pathway_names;
	// } else if (organism && pathway_names.length() < 4) {
	// lastQuery = "Select p.pathway_name, p.title, t.name from kegg_pathway p "
	// + "LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where "
	// + dqv.prepareString(organismus, "t.name", "t.latin_name");
	// } else {
	// lastQuery = "Select p.pathway_name, p.title, t.name from kegg_pathway p "
	// +
	// "LEFT OUTER JOIN kegg_taxonomy as t on p.org=t.org where p.pathway_name In "
	// + pathway_names
	// + " AND "
	// + dqv.prepareString(organismus, "t.name", "t.latin_name");
	// }
	//
	// return new Wrapper().requestDbContent(2, lastQuery + " LIMIT 0,1000;");
	//
	// }

	@Override
	protected Void doInBackground() throws Exception {
		w.setLockedPane(true);
		// results=requestDbContent();
		results = KEGGQueries.requestDbContent(pathway, organismus, gene,
				compound, enzyme);
		w.setLockedPane(false);
		return null;
	}

	private final int SEPARATE_TABS=JOptionPane.YES_OPTION;
	private final int OVERVIEW_PW=JOptionPane.NO_OPTION;
	private final int MERGE=JOptionPane.CANCEL_OPTION;
	private Iterator it;
	private int answer = SEPARATE_TABS;
	private KEGGConnector kc;
	@Override
	public void done() {

		endSearch(w, bar);
		if (results.size() > 0) {
			continueProgress = true;
			dsrw = new KEGGResultWindow(results);

		} else {
			endSearch(w, bar);
			JOptionPane.showMessageDialog(w,
					"Sorry, no entries have been found.");
		}

		if (continueProgress) {
			Vector<String[]> results = dsrw.getAnswer();
			if (results==null) return;
			w.setEnable(true);
			if (results.size() != 0) {
				if (results.size() > 1 || mergePW!=null)
					answer= JOptionPane
							.showOptionDialog(
									w,
									"Shall the selected Pathways be loaded each into a separate tab, be combined into an overview Pathway or merged?",
									"Several Pathways selected...",JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE, null,
									new String[] { "Separate Tabs",
											"Overview Pathway", "Merge Pathways"}, SEPARATE_TABS);				
				it = results.iterator();	
					if (answer==OVERVIEW_PW){
					Pathway newPW = new CreatePathway("Overview Pathway").getPathway();
					newPW.getGraph().lockVertices();
					newPW.getGraph().stopVisualizationModel();
					ArrayList<BiologicalNodeAbstract> bnas=new ArrayList<BiologicalNodeAbstract>();
					if (!(enzyme==null || enzyme.equals("")))
						bnas.add( new Enzyme(enzyme, enzyme, newPW.getGraph().createNewVertex()));
					if (!(gene==null || gene.equals("")))
						bnas.add( new Gene(gene, gene, newPW.getGraph().createNewVertex()));					
					if (!(compound==null || compound.equals("")))
						bnas.add( new SmallMolecule(compound, compound, newPW.getGraph().createNewVertex()));
					for (String[] s : results){
						PathwayMap map=new PathwayMap(s[1],s[0], newPW.getGraph().createNewVertex());
						map.setReference(false);
						map.setAbstract(false);
						map=(PathwayMap) newPW.addElement(map);
						newPW.getGraph().moveVertex(map.getVertex(), 0, 0);
						for (BiologicalNodeAbstract bna : bnas){
						bna=(BiologicalNodeAbstract) newPW.addElement(bna);
						bna.setReference(false);
						bna.setAbstract(false);
						bna.setColor(Color.red);
						newPW.getGraph().moveVertex(bna.getVertex(), 0, 0);
						Compound c = new Compound(newPW.getGraph().createEdge(
								bna.getVertex(),map.getVertex() , true), "", "");
						c.setDirected(true);
						c.setReference(false);
						c.setAbstract(false);
						c=(Compound) newPW.addElement(c);

						}
					}
					newPW.getGraph().unlockVertices();
					newPW.getGraph().restartVisualizationModel();
					w.updateAllGuiElements();
					newPW.getGraph().changeToGEMLayout();
					newPW.getGraph().normalCentering();
				}
				else {
					kc = new KEGGConnector(bar,
							(String[]) it.next(), !(mergePW==null));
					kc.addPropertyChangeListener(this);
					kc.setSearchMicroRNAs(dsrw.getCheckBox().isSelected());
					kc.execute();	
				}
			}
		}

	}
	
	
	
	private void endSearch(final MainWindow w, final ProgressBar bar) {
		Runnable run = new Runnable() {
			public void run() {
				bar.closeWindow();
				w.setEnable(true);
			}
		};

		SwingUtilities.invokeLater(run);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue().equals("finished")){
		if (answer==MERGE) 
			if (mergePW==null) 
				mergePW=kc.getPw();
			else {
				w.removeTab(false);
				mergePW=new MergeGraphs(mergePW,kc.getPw(), false).getPw_new();	
			}
		if (it!=null && it.hasNext()){
			kc = new KEGGConnector(bar,
					(String[]) it.next(), !(answer==SEPARATE_TABS));
			kc.addPropertyChangeListener(this);
			kc.setSearchMicroRNAs(dsrw.getCheckBox().isSelected());
			kc.execute();	
		} else{
			mergePW = new GraphInstance().getContainer().getPathway(w.getCurrentPathway());
			w.setEnable(true);
			w.setLockedPane(false);
			w.updateAllGuiElements();
			mergePW.getGraph().getVisualizationViewer().repaint();
			mergePW.getGraph().disableGraphTheory();			
			mergePW.getGraph().normalCentering();
			
		}
		}
	}

}
