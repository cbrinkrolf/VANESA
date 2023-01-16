package database.mirna;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.SwingWorker;

import biologicalElements.Pathway;
import biologicalObjects.edges.Expression;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.SRNA;
import configurations.Wrapper;
import database.mirna.gui.MirnaResultWindow;
import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MyPopUp;
import pojos.DBColumn;

public class mirnaSearch extends SwingWorker<Object, Object> {

	private String name, acc, sequence, database, gene;

	private ArrayList<DBColumn> resultsDBSearch;
	private MirnaResultWindow mirnaResultWindow;
	private boolean headless;
	private boolean hsaOnly;
	private boolean isSources;
	private boolean isTargets;
	private boolean mirnaNew;

	public mirnaSearch(String[] input, boolean hsaOnly, boolean headless, boolean sources, boolean targets, boolean mirnaNew) {

		name = input[0];
		acc = input[1];
		sequence = input[2];
		gene = input[3];
		database = "miRNA";

		this.hsaOnly = hsaOnly;
		this.headless = headless;
		this.isSources = sources;
		this.isTargets = targets;
		this.mirnaNew = mirnaNew;

	}

	private ArrayList<DBColumn> requestDbContent() {

		if (sequence.length() > 0 && acc.length() > 0 && name.length() > 0) {
			String[] parameters = { "%" + sequence + "%", "%" + acc + "%", "%" + name + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, miRNAqueries.miRNA_all, parameters, mirnaNew);
		} else if (sequence.length() > 0 && acc.length() > 0) {
			String[] parameters = { "%" + sequence + "%", "%" + acc + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, miRNAqueries.miRNA_sequence_acc, parameters, mirnaNew);
		} else if (sequence.length() > 0 && name.length() > 0) {
			String[] parameters = { "%" + sequence + "%", "%" + name + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, miRNAqueries.miRNA_sequence_name, parameters, mirnaNew);
		} else if (acc.length() > 0 && name.length() > 0) {
			String[] parameters = { "%" + acc + "%", "%" + name + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, miRNAqueries.miRNA_acc_name, parameters, mirnaNew);
		} else if (sequence.length() > 0) {

			String[] parameters = { "%" + sequence + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, miRNAqueries.miRNA_onlySequence, parameters, mirnaNew);

		} else if (acc.length() > 0) {

			String[] parameters = { "%" + acc + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, miRNAqueries.miRNA_onlyAccession, parameters, mirnaNew);

		} else if (name.length() > 0) {
			String[] parameters = { "%" + name + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, miRNAqueries.miRNA_onlyName, parameters, mirnaNew);

		} else if (gene.length() > 0) {
			String[] parameters = { "%" + gene + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, miRNAqueries.miRNA_onlyGene, parameters, mirnaNew);

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
		MainWindow.getInstance().closeProgressBar();
		if (resultsDBSearch.size() > 0) {
			continueProgress = true;
			// System.out.println(resultsDBSearch.size());
			mirnaResultWindow = new MirnaResultWindow(resultsDBSearch);
		} else {
			MyPopUp.getInstance().show("miRNA Search", "No entries have been found!");
		}

		if (continueProgress) {
			Vector<String[]> results = mirnaResultWindow.getAnswer();
			// System.out.println(results.get(0)[0] + " " + results.get(0)[1]);
			// System.out.println(results.size());
			if (results.size() > 0) {
				MainWindow.getInstance().showProgressBar("Fetching network.");
				final Iterator<String[]> it = results.iterator();
				int count = 0;
				while (it.hasNext()) {
					String[] details = it.next();
					String name = details[0];
					String sequence = "";
					if (details.length > 2) {
						sequence = details[2];
					}
					// System.out.println("name: "+name);
					final String QUESTION_MARK = new String("\\?");
					// System.out.println(name);
					if (this.name.length() > 0) {
						// System.out.println("longer");

						// TODO split serach to search for sources and/or targets, depending on search criterion
						String finalQueryString = miRNAqueries.miRNA_get_TargetGenes.replaceFirst(QUESTION_MARK,
								"'" + name + "'");
						// System.out.println(finalQueryString);
						if (this.hsaOnly) {
							finalQueryString = finalQueryString.substring(0, finalQueryString.length() - 2);
							finalQueryString += "AND TargetGenes.SpeciesID=54;";
							// System.out.println(finalQueryString);
						}

						resultsDBSearch = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, finalQueryString, mirnaNew);
						// System.out.println("s: "+resultsDBSearch.size());

						if (resultsDBSearch.size() > 0) {
							count += resultsDBSearch.size();

							Pathway pw = new CreatePathway(database + " network for " + name).getPathway();

							// pw.setOrganism(organism);
							// pw.setLink(pathwayLink);
							// pw.setImagePath(pathwayImage);
							// pw.setNumber(pathwayNumber);
							MyGraph myGraph = pw.getGraph();

							// stopVisualizationModel();

							SRNA root = new SRNA(name, name);
							root.setNtSequence(sequence);

							pw.addVertex(root, new Point2D.Double(0, 0));

							Iterator<DBColumn> cols = resultsDBSearch.iterator();
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
								myGraph.fitScaleOfViewer(myGraph.getSatelliteView());
								myGraph.normalCentering();
							}
							MainWindow.getInstance().closeProgressBar();

							MainWindow window = MainWindow.getInstance();
							window.updateOptionPanel();
							window.getFrame().setVisible(true);

							// class HLC implements
							// HierarchyListComparator<String>{
							//
							// int c;
							//
							// public HLC(int chars){
							// c = chars;
							// }
							//
							// public String getValue(BiologicalNodeAbstract n)
							// {
							// return (String) n.getLabel().substring(0,c);
							// }
							//
							// public String getSubValue(
							// BiologicalNodeAbstract n) {
							// return n.getLabel();
							// }
							// }
							// HierarchyList<String> l = new
							// HierarchyList<String>();
							// l.addAll(myGraph.getAllVertices());
							// l.sort(new HLC(1));
							// l.coarse();

						}
					} else if (gene.length() > 0) {
						String finalQueryString = miRNAqueries.miRNA_get_TargetingMirnas.replaceFirst(QUESTION_MARK,
								"'" + name + "'");
						// System.out.println(finalQueryString);
						if (this.hsaOnly) {
							finalQueryString = finalQueryString.substring(0, finalQueryString.length() - 2);
							finalQueryString += " AND TargetGenes.SpeciesID=54;";
							// System.out.println(finalQueryString);
						}

						resultsDBSearch = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, finalQueryString, mirnaNew);
						if (resultsDBSearch.size() > 0) {
							count += resultsDBSearch.size();
							Pathway pw = new CreatePathway(database + " network for " + name).getPathway();

							// pw.setOrganism(organism);
							// pw.setLink(pathwayLink);
							// pw.setImagePath(pathwayImage);
							// pw.setNumber(pathwayNumber);
							MyGraph myGraph = pw.getGraph();

							// stopVisualizationModel();

							DNA root = new DNA(name, name);

							pw.addVertex(root, new Point2D.Double(0, 0));

							Iterator<DBColumn> cols = resultsDBSearch.iterator();
							SRNA srna;
							String[] dbcol;
							Expression e;
							while (cols.hasNext()) {
								dbcol = cols.next().getColumn();
								srna = new SRNA(dbcol[0], dbcol[0]);
								srna.setNtSequence(dbcol[1]);
								pw.addVertex(srna, new Point2D.Double(0, 0));
								e = new Expression("", "", srna, root);
								e.setDirected(true);
								pw.addEdge(e);

							}
							// startVisualizationModel();
							myGraph.restartVisualizationModel();

							if (!headless) {
								myGraph.changeToGEMLayout();
								myGraph.fitScaleOfViewer(myGraph.getSatelliteView());
								myGraph.normalCentering();
							}
							pw.saveVertexLocations();
							MainWindow.getInstance().closeProgressBar();

							MainWindow window = MainWindow.getInstance();
							window.updateOptionPanel();
							window.getFrame().setVisible(true);

						}

					}
					MainWindow.getInstance().closeProgressBar();

					if (count == 0) {
						MyPopUp.getInstance().show("miRNA Search", "No entries have been found!");
					}
				}
			}
		}
	}
}
