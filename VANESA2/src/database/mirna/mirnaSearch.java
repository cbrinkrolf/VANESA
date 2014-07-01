package database.mirna;

import gui.MainWindow;
import gui.ProgressBar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import biologicalObjects.nodes.Enzyme;

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

	private String name, acc, sequence, database;

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
		database = "miRNA";

		this.w = w;
		this.bar = bar;

	}

	private ArrayList<DBColumn> requestDbContent() {

		if (sequence.length() > 0 && acc.length() > 0 && name.length() > 0) {
			String[] parameters = { "%" + sequence + "%", "%" + acc + "%",
					"%" + name + "%" };
			return new Wrapper().requestDbContent(6, miRNAqueries.miRNA_all,
					parameters);
		} else if (sequence.length() > 0 && acc.length() > 0) {
			String[] parameters = { "%" + sequence + "%", "%" + acc + "%" };
			return new Wrapper().requestDbContent(6,
					miRNAqueries.miRNA_sequence_acc, parameters);
		} else if (sequence.length() > 0 && name.length() > 0) {
			String[] parameters = { "%" + sequence + "%", "%" + name + "%" };
			return new Wrapper().requestDbContent(6,
					miRNAqueries.miRNA_sequence_name, parameters);
		} else if (acc.length() > 0 && name.length() > 0) {
			String[] parameters = { "%" + acc + "%", "%" + name + "%" };
			return new Wrapper().requestDbContent(6,
					miRNAqueries.miRNA_acc_name, parameters);
		} else if (sequence.length() > 0) {

			String[] parameters = { "%" + sequence + "%" };
			return new Wrapper().requestDbContent(6,
					miRNAqueries.miRNA_onlySequence, parameters);

		} else if (acc.length() > 0) {

			String[] parameters = { "%" + acc + "%" };
			return new Wrapper().requestDbContent(6,
					miRNAqueries.miRNA_onlyAccession, parameters);

		} else if (name.length() > 0) {
			String[] parameters = { "%" + name + "%" };
			return new Wrapper().requestDbContent(6,
					miRNAqueries.miRNA_onlyName, parameters);

		}

		return null;
	}

	protected Object doInBackground() throws Exception {
		w.setLockedPane(true);
		results = requestDbContent();
		w.setLockedPane(false);
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
			Vector results = mirnaResultWindow.getAnswer();
			if (results.size() != 0) {
				final Iterator it = results.iterator();
				while (it.hasNext()) {
					
					Vector keggGenesVector = new Vector<String>();
					String[] details = (String[]) it.next();
					String microRNA_accession = details[1];
					String[] parsingDetails = { details[0] };
					allSpecificElements = new Wrapper().requestDbContent(6,
							miRNAqueries.miRNA_get_Genes, parsingDetails);
					Boolean targets = false;
					String targetString = "(";
					for (DBColumn column : allSpecificElements) {
						String[] resultDetails = (String[]) column.getColumn();
						targetString = targetString + "'" + resultDetails[0]
								+ "',";
						targets = true;
						keggGenesVector.add(resultDetails[0]);
					}
					targetString = targetString + "'x')";
//System.out.println(targetString);
					if (targets) {
						
						String[] geneDetails = { targetString };
						final String QUESTION_MARK = new String("\\?");
						String finalQueryString = miRNAqueries.miRNA_get_Pathways
								.replaceFirst(QUESTION_MARK, targetString);
						allKEGGPathways = new Wrapper().requestDbContent(2,
								finalQueryString);

						if (allKEGGPathways.size() > 0) {
							MirnaResultKeggWindow mirnaResultKeggWindow = new MirnaResultKeggWindow(
									allKEGGPathways);
							Vector keggPAthwayResults = mirnaResultKeggWindow
									.getAnswer();
							if (keggPAthwayResults.size() != 0) {
								String keggPathwayNumber = "";
								String keggPathwayName = "";
								final Iterator it3 = keggPAthwayResults
										.iterator();
								while (it3.hasNext()) {

									String[] pathwayResutls = (String[]) it3
											.next();
									keggPathwayNumber= "hsa"+pathwayResutls[1];
									keggPathwayName = pathwayResutls[0];
								}

								String[] keggParamters = {keggPathwayNumber, keggPathwayName,"Homo sapiens"};
								KEGGConnector kc=new KEGGConnector(bar, keggParamters, false);
								kc.setMirnas(keggGenesVector);
								kc.setMirnaName(microRNA_accession);

								kc.execute();
								//new mirnaConnector(keggGenesVector, kc.getPw());
								
							}
						}

					} else {
						JOptionPane
								.showMessageDialog(w,
										"Sorry, no targets recorded for this specific microRNA in TarBase.");
					}
				}
			}
		}
		endSearch(w, bar);
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
