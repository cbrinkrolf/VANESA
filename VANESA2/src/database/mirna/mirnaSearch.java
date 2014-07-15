package database.mirna;

import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import biologicalElements.Pathway;
import biologicalObjects.edges.Expression;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.SRNA;
import configurations.Wrapper;
import pojos.DBColumn;
import database.kegg.KEGGConnector;
import database.kegg.KEGGQueries;
import database.mirna.gui.MirnaResultKeggWindow;
import database.mirna.gui.MirnaResultWindow;
import database.ppi.PPIConnector;
import database.ppi.PPISearchResultWindow;
import database.ppi.PPIqueries;

public class mirnaSearch extends SwingWorker {

	private String name, acc, sequence, database, gene;

	private MainWindow w;
	private ProgressBar bar;
	private ArrayList<DBColumn> results;
	private MirnaResultWindow mirnaResultWindow;
	private ArrayList<DBColumn> allSpecificElements = new ArrayList<DBColumn>();
	private ArrayList<DBColumn> allKEGGPathways = new ArrayList<DBColumn>();

	public mirnaSearch(String[] input, MainWindow w, ProgressBar bar) {

		name = input[0];
		acc = input[1];
		sequence = input[2];
		gene = input[3];
		database = "miRNA";

		this.w = w;
		this.bar = bar;

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

		} else if (gene.length() > 0) {
			String[] parameters = { "%" + gene + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
					miRNAqueries.miRNA_onlyGene, parameters);

		} else if (name.length() > 0) {
			String[] parameters = { "%" + name + "%" };
			return new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
					miRNAqueries.miRNA_onlyName, parameters);

		}

		return null;
	}

	protected Object doInBackground() throws Exception {
		//w.setLockedPane(true);
		results = requestDbContent();
		//w.setLockedPane(false);
		return null;
	}

	public void done() {
		Boolean continueProgress = false;
		endSearch(w, bar);

		if (results.size() > 0) {
			continueProgress = true;
			mirnaResultWindow = new MirnaResultWindow(results);
		} else {
			endSearch(w, bar);
			JOptionPane.showMessageDialog(w,
					"Sorry, no entries have been found.");
		}

		if (continueProgress) {
			Vector<String[]> results = mirnaResultWindow.getAnswer();
			//System.out.println(results.get(0)[0] + " " + results.get(0)[1]);
			if (results.size() != 0) {
				final Iterator<String[]> it = results.iterator();
				while (it.hasNext()) {
					String[] details = it.next();
					String name = details[0];

					final String QUESTION_MARK = new String("\\?");
					String finalQueryString = miRNAqueries.miRNA_get_Genes
							.replaceFirst(QUESTION_MARK, "'%" + name + "%'");
					allKEGGPathways = new Wrapper().requestDbContent(
							Wrapper.dbtype_MiRNA, finalQueryString);
					//System.out.println(finalQueryString);
					if (allKEGGPathways.size() > 0) {
						
						Pathway pw = new CreatePathway(database + " network for " + name).getPathway();

						// pw.setOrganism(organism);
						// pw.setLink(pathwayLink);
						// pw.setImagePath(pathwayImage);
						// pw.setNumber(pathwayNumber);
						MyGraph myGraph = pw.getGraph();

						//stopVisualizationModel();
						
						SRNA root = new SRNA(name, name);
						
						pw.addVertex(root, new Point2D.Double(0,0));
						
						Iterator<DBColumn> cols = allKEGGPathways.iterator();
						DNA dna;
						String[] dbcol;
						while(cols.hasNext()){
							dbcol = cols.next().getColumn();
							
							dna = new DNA(dbcol[0], dbcol[0]);
							pw.addVertex(dna, new Point2D.Double(0,0));
							pw.addEdge(new Expression("","",root, dna));
							
						}
						//startVisualizationModel();

						myGraph.changeToGEMLayout();
						myGraph.fitScaleOfViewer(myGraph.getSatelliteView());
						myGraph.normalCentering();
						bar.closeWindow();

						MainWindow window = MainWindowSingelton.getInstance();
						window.updateOptionPanel();
						window.setVisible(true);
						
						
					}

				}
			}
		}
		//endSearch(w, bar);
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
}
