package database.mirna;

import graph.CreatePathway;
import graph.hierarchies.HierarchyList;
import graph.hierarchies.HierarchyListComparator;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingleton;
import gui.ProgressBar;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import pojos.DBColumn;
import biologicalElements.Pathway;
import biologicalObjects.edges.Expression;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.SRNA;
import configurations.Wrapper;
import database.mirna.gui.MirnaResultWindow;

public class mirnaSearch extends SwingWorker<Object, Object> {

	private String name, acc, sequence, database, gene;

	private MainWindow w;
	private ArrayList<DBColumn> resultsDBSearch;
	private MirnaResultWindow mirnaResultWindow;
	private boolean headless;

	public mirnaSearch(String[] input, MainWindow w,boolean headless) {

		name = input[0];
		acc = input[1];
		sequence = input[2];
		gene = input[3];
		database = "miRNA";

		this.w = w;
		this.headless = headless;

	}

	private ArrayList<DBColumn> requestDbContent() {

		if (sequence.length() > 0 && acc.length() > 0 && name.length() > 0) {
			String[] parameters = { "%" + sequence + "%", "%" + acc + "%",
					"%" + name + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
					miRNAqueries.miRNA_all, parameters);
		} else if (sequence.length() > 0 && acc.length() > 0) {
			String[] parameters = { "%" + sequence + "%", "%" + acc + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
					miRNAqueries.miRNA_sequence_acc, parameters);
		} else if (sequence.length() > 0 && name.length() > 0) {
			String[] parameters = { "%" + sequence + "%", "%" + name + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
					miRNAqueries.miRNA_sequence_name, parameters);
		} else if (acc.length() > 0 && name.length() > 0) {
			String[] parameters = { "%" + acc + "%", "%" + name + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
					miRNAqueries.miRNA_acc_name, parameters);
		} else if (sequence.length() > 0) {

			String[] parameters = { "%" + sequence + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
					miRNAqueries.miRNA_onlySequence, parameters);

		} else if (acc.length() > 0) {

			String[] parameters = { "%" + acc + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
					miRNAqueries.miRNA_onlyAccession, parameters);

		} else if (name.length() > 0) {
			String[] parameters = { "%" + name + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
					miRNAqueries.miRNA_onlyName, parameters);

		} else if (gene.length() > 0) {
			String[] parameters = { "%" + gene + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
					miRNAqueries.miRNA_onlyGene, parameters);

		}

		return null;
	}

	protected Object doInBackground() throws Exception {
		// w.setLockedPane(true);
		resultsDBSearch = requestDbContent();
		// w.setLockedPane(false);
		return null;
	}

	public void done() {
		Boolean continueProgress = false;
		MainWindowSingleton.getInstance().closeProgressBar();

		if (resultsDBSearch.size() > 0) {
			continueProgress = true;
			mirnaResultWindow = new MirnaResultWindow(resultsDBSearch);
		} else {
			JOptionPane.showMessageDialog(w,
					"Sorry, no entries have been found.");
		}

		if (continueProgress) {
			Vector<String[]> results = mirnaResultWindow.getAnswer();
			// System.out.println(results.get(0)[0] + " " + results.get(0)[1]);
			if (results.size() != 0) {
				MainWindowSingleton.getInstance().showProgressBar("Fetching network.");
				final Iterator<String[]> it = results.iterator();
				while (it.hasNext()) {
					String[] details = it.next();
					String name = details[0];
					//System.out.println("name: "+name);
					final String QUESTION_MARK = new String("\\?");
					if (name.length() > 0) {
						String finalQueryString = miRNAqueries.miRNA_get_Genes
								.replaceFirst(QUESTION_MARK, "'" + name + "'");
						//System.out.println(finalQueryString);
						resultsDBSearch = new Wrapper().requestDbContent(
								Wrapper.dbtype_MiRNA, finalQueryString);
						//System.out.println(resultsDBSearch.size());
						
						if (resultsDBSearch.size() > 0) {

							Pathway pw = new CreatePathway(database
									+ " network for " + name).getPathway();

							// pw.setOrganism(organism);
							// pw.setLink(pathwayLink);
							// pw.setImagePath(pathwayImage);
							// pw.setNumber(pathwayNumber);
							MyGraph myGraph = pw.getGraph();

							// stopVisualizationModel();

							SRNA root = new SRNA(name, name);

							pw.addVertex(root, new Point2D.Double(0, 0));

							Iterator<DBColumn> cols = resultsDBSearch
									.iterator();
							DNA dna;
							String[] dbcol;
							Expression e;
							while (cols.hasNext()) {
								dbcol = cols.next().getColumn();
								dna = new DNA(dbcol[0], dbcol[0]);
								pw.addVertex(dna, new Point2D.Double(0, 0));
								e = new Expression("", "", root, dna);
								e.setDirected(true);
								pw.addEdge(e);

							}
							// startVisualizationModel();
							myGraph.restartVisualizationModel();

							if (!headless) {
								myGraph.changeToGEMLayout();
								myGraph.fitScaleOfViewer(myGraph
										.getSatelliteView());
								myGraph.normalCentering();
							}
							MainWindowSingleton.getInstance().closeProgressBar();

							MainWindow window = MainWindowSingleton
									.getInstance();
							window.updateOptionPanel();
							window.setVisible(true);

//							 class HLC implements
//							 HierarchyListComparator<String>{
//							
//							 int c;
//							
//							 public HLC(int chars){
//							 c = chars;
//							 }
//							
//							 public String getValue(BiologicalNodeAbstract n)
//							 {
//							 return (String) n.getLabel().substring(0,c);
//							 }
//							
//							 public String getSubValue(
//							 BiologicalNodeAbstract n) {
//							 return n.getLabel();
//							 }
//							 }
//							 HierarchyList<String> l = new
//							 HierarchyList<String>();
//							 l.addAll(myGraph.getAllVertices());
//							 l.sort(new HLC(1));
//							 l.coarse();

						}
					} else if (gene.length() > 0) {
						String finalQueryString = miRNAqueries.miRNA_get_Mirnas
								.replaceFirst(QUESTION_MARK, "'" + name + "'");
						resultsDBSearch = new Wrapper().requestDbContent(
								Wrapper.dbtype_MiRNA, finalQueryString);
						// System.out.println(finalQueryString);
						if (resultsDBSearch.size() > 0) {

							Pathway pw = new CreatePathway(database
									+ " network for " + name).getPathway();

							// pw.setOrganism(organism);
							// pw.setLink(pathwayLink);
							// pw.setImagePath(pathwayImage);
							// pw.setNumber(pathwayNumber);
							MyGraph myGraph = pw.getGraph();

							// stopVisualizationModel();

							DNA root = new DNA(name, name);

							pw.addVertex(root, new Point2D.Double(0, 0));

							Iterator<DBColumn> cols = resultsDBSearch
									.iterator();
							SRNA srna;
							String[] dbcol;
							Expression e;
							while (cols.hasNext()) {
								dbcol = cols.next().getColumn();

								srna = new SRNA(dbcol[0], dbcol[0]);
								pw.addVertex(srna, new Point2D.Double(0, 0));
								e = new Expression("", "", srna, root);
								e.setDirected(true);
								pw.addEdge(e);

							}
							// startVisualizationModel();
							myGraph.restartVisualizationModel();

							if (!headless) {
								myGraph.changeToGEMLayout();
								myGraph.fitScaleOfViewer(myGraph
										.getSatelliteView());
								myGraph.normalCentering();
							}
							MainWindowSingleton.getInstance().closeProgressBar();

							MainWindow window = MainWindowSingleton
									.getInstance();
							window.updateOptionPanel();
							window.setVisible(true);

						}

					}

				}
			}
		}
	}
}
