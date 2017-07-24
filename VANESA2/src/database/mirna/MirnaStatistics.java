package database.mirna;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.edges.Expression;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.SRNA;
import configurations.Wrapper;
import graph.layouts.Circle;
import gui.MyPopUp;
import pojos.DBColumn;

public class MirnaStatistics {

	public void addMirnaSources(Pathway pw) {
		int counterEdges = 0;
		int counterNodes = 0;

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		final String QUESTION_MARK = new String("\\?");
		ArrayList<DBColumn> resultsDBSearch;
		HashMap<String, BiologicalNodeAbstract> bnas = new HashMap<String, BiologicalNodeAbstract>();
		// SRNA srna;
		Point2D p;
		Expression exp;
		HashMap<BiologicalNodeAbstract, ArrayList<DBColumn>> data = new HashMap<BiologicalNodeAbstract, ArrayList<DBColumn>>();
		while (it.hasNext()) {
			bna = it.next();
			bnas.put(bna.getLabel(), bna);
		}

		if (pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked().size() > 0) {
			it = pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked().iterator();
		} else {
			it = pw.getAllGraphNodes().iterator();
		}
		
		while (it.hasNext()) {
			bna = it.next();
			String finalQueryString = miRNAqueries.miRNA_get_SourcingMirnas.replaceFirst(QUESTION_MARK, "'" + bna.getLabel() + "'");
			resultsDBSearch = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, finalQueryString);
			if (resultsDBSearch.size() > 0) {
				data.put(bna, resultsDBSearch);
			}
		}
		

		String[] column;

		Iterator<BiologicalNodeAbstract> itBNA = data.keySet().iterator();
		BiologicalNodeAbstract tmp;
		while (itBNA.hasNext()) {
			bna = itBNA.next();
			resultsDBSearch = data.get(bna);
			for (int i = 0; i < resultsDBSearch.size(); i++) {
				column = resultsDBSearch.get(i).getColumn();
				if (bnas.containsKey(column[0])) {
					tmp = bnas.get(column[0]);
				} else {
					tmp = new SRNA(column[0], column[0]);
					// p = new
					// Point2D.Double(myGraph.getVertexLocation(bna).getX(),
					// myGraph.getVertexLocation(bna).getY());
					// .findNearestFreeVertexPosition(bna.getKEGGnode()
					// .getXPos(), bna.getKEGGnode().getYPos(),
					// 100);
					p = Circle.getPointOnCircle(pw.getGraph().getVertexLocation(bna), 20, 2.0 * ((double) (Math.random() % (Math.PI))));
					pw.addVertex(tmp, p);
					bnas.put(column[0], tmp);
					counterNodes++;
					// srnaParents.put(srna.getID(),
					// bna.getID());
					System.out.println(tmp.getName());
				}
				if (!pw.existEdge(bna, tmp)) {
					exp = new Expression("", "", bna, tmp);
					exp.setDirected(true);
					pw.addEdge(exp);
					pw.addEdgeToView(exp, true);
					counterEdges++;
				}
			}
		}
		pw.getGraph().updateGraph();
		pw.getGraph().updateLayout();
		MyPopUp.getInstance().show("miRNA source enrichment", counterNodes + " nodes and " + counterEdges + " edges have been added!");
	}

	public void addMirnaTargets(Pathway pw) {
		int counterEdges = 0;
		int counterNodes = 0;

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		final String QUESTION_MARK = new String("\\?");
		ArrayList<DBColumn> resultsDBSearch;
		HashMap<String, BiologicalNodeAbstract> bnas = new HashMap<String, BiologicalNodeAbstract>();
		BiologicalNodeAbstract tmp;
		Point2D p;
		Expression exp;
		HashMap<BiologicalNodeAbstract, ArrayList<DBColumn>> data = new HashMap<BiologicalNodeAbstract, ArrayList<DBColumn>>();
		while (it.hasNext()) {
			bna = it.next();
			bnas.put(bna.getLabel(), bna);
		}
		
		if (pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked().size() > 0) {
			it = pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked().iterator();
		} else {
			it = pw.getAllGraphNodes().iterator();
		}
		
		while (it.hasNext()) {
			bna = it.next();
			String finalQueryString = miRNAqueries.miRNA_get_TargetingMirnas.replaceFirst(QUESTION_MARK, "'" + bna.getLabel() + "'");
			resultsDBSearch = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, finalQueryString);
			if (resultsDBSearch.size() > 0) {
				data.put(bna, resultsDBSearch);
			}
		}

		String[] column;

		Iterator<BiologicalNodeAbstract> itBNA = data.keySet().iterator();

		while (itBNA.hasNext()) {
			bna = itBNA.next();
			resultsDBSearch = data.get(bna);
			for (int i = 0; i < resultsDBSearch.size(); i++) {
				column = resultsDBSearch.get(i).getColumn();
				if (bnas.containsKey(column[0])) {
					tmp = bnas.get(column[0]);
				} else {
					tmp = new SRNA(column[0], column[0]);
					// p = new
					// Point2D.Double(myGraph.getVertexLocation(bna).getX(),
					// myGraph.getVertexLocation(bna).getY());
					// .findNearestFreeVertexPosition(bna.getKEGGnode()
					// .getXPos(), bna.getKEGGnode().getYPos(),
					// 100);
					p = Circle.getPointOnCircle(pw.getGraph().getVertexLocation(bna), 20, 2.0 * ((double) (Math.random() % (Math.PI))));
					pw.addVertex(tmp, p);
					bnas.put(column[0], tmp);
					// srnaParents.put(srna.getID(),
					// bna.getID());
					counterNodes++;
				}
				if (!pw.existEdge(tmp, bna)) {
					exp = new Expression("", "", tmp, bna);
					exp.setDirected(true);
					pw.addEdge(exp);
					pw.addEdgeToView(exp, true);
					counterEdges++;
				}
			}
		}
		pw.getGraph().updateGraph();
		pw.getGraph().updateLayout();
		MyPopUp.getInstance().show("miRNA target enrichment", counterNodes + " nodes and " + counterEdges + " edges have been added!");
	}

	public void createKeggStatistics(boolean sources, boolean targets, boolean hsaOnly) {
		// code for testing number of mirnas matching a pathway

		/*
		 * Iterator<BiologicalNodeAbstract> bnas =
		 * graphInstance.getMyGraph().getAllVertices().iterator(); int c =0;
		 * while(bnas.hasNext()){ if(bnas.next() instanceof DNA){ c++; } }
		 * System.out.println("dnas: "+c);
		 */
		final String QUESTION_MARK = new String("\\?");
		String finalQueryString = "SELECT distinct targetgene FROM db_mirna2.mirtarbase;";// SELECT
																							// COUNT(DISTINCT
		Set<String> pws = new HashSet<String>(); // targetgene
		// FROM
		ArrayList<DBColumn> list = new ArrayList<DBColumn>();

		// list = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
		// finalQueryString);
		// System.out.println(list.size());
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		// System.out.println("res: "+list.get(0).getColumn()[0]);
		// System.out.println(list.size());
		HashMap<String, HashSet<String>> pw2genes = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> genes2mrinaSources = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> genes2mrinaTargets = new HashMap<String, HashSet<String>>();

		BufferedReader in;
		String pw;
		String mirna;
		System.out.println(list.size());

		FileReader fr;
		try {
			fr = new FileReader("pws.txt");

			BufferedReader br = new BufferedReader(fr);

			String zeile = br.readLine();

			while ((zeile = br.readLine()) != null) {
				pws.add(zeile);
				// pws.add("00010");
				// pws.add("00020");
				// pws.add("00061");
			}

			br.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("PWs: " + pws.size());
		Iterator<String> its = pws.iterator();

		double count = 0;
		String output = "";
		while (its.hasNext()) {

			// if(count%10 == 0){
			System.out.println(count / (pws.size()) * 100.0 + "%");
			// }
			pw = its.next();

			finalQueryString = "SELECT distinct kegg_genes_name.name FROM dawismd.kegg_genes_pathway join dawismd.kegg_genes_name on kegg_genes_pathway.id = kegg_genes_name.id where kegg_genes_pathway.number = '"
					+ pw + "' AND kegg_genes_pathway.org = 'hsa';";

			// System.out.println(finalQueryString);
			// finalQueryString = "SELECT count(distinct
			// kegg_genes_pathway.id) FROM dawismd.kegg_genes_pathway inner
			// join dawismd.kegg_genes_name on kegg_genes_pathway.id =
			// kegg_genes_name.id where kegg_genes_pathway.number='"
			// + pw + "' and kegg_genes_pathway.org = 'hsa';";

			list = new Wrapper().requestDbContent(Wrapper.dbtype_KEGG, finalQueryString);
			System.out.println(list.size());
			if (list.size() > 0) {
				// System.out.println("drin");
				pw2genes.put(pw, new HashSet<String>());
			}

			for (int i = 0; i < list.size(); i++) {
				pw2genes.get(pw).add(list.get(i).getColumn()[0]);
			}

			output += pw + "\t" + list.get(0).getColumn()[0] + "\r\n";
			count++;
		}
		System.out.println(output);
		HashSet<String> genes = new HashSet<String>();

		System.out.println("pws: " + pw2genes.keySet().size());
		int sum = 0;

		for (String key : pw2genes.keySet()) {
			genes.addAll(pw2genes.get(key));
		}

		System.out.println("all genes: " + genes.size());
		String number;
		String gene;
		Iterator<String> it = genes.iterator();
		count = 0;
		while (it.hasNext()) {
			System.out.println("Retr. mirnas: " + count / (genes.size()) * 100.0 + "%");
			gene = it.next();
			if (!gene.contains("'")) {
				// System.out.println(gene);
				// finalQueryString = "SELECT distinct mirnaname FROM
				// mirtarbase where targetgene = '" + gene + "';";

				// miRNA sources

				finalQueryString = "SELECT distinct Matures.Name FROM Matures join overlappingtranscripts on matures.hpID = overlappingtranscripts.hpID join hairpins on matures.hpID = hairpins.ID where overlappingtranscripts.Name = overlappingtranscripts.Accession and overlappingtranscripts.Name = '"
						+ gene + "' ";
				if (hsaOnly) {
					finalQueryString += " AND SpeciesID=54";
				}
				finalQueryString += ";";
				list = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, finalQueryString);
				genes2mrinaSources.put(gene, new HashSet<String>());

				for (int i = 0; i < list.size(); i++) {
					// System.out.println(gene);
					genes2mrinaSources.get(gene).add(list.get(i).getColumn()[0]);
				}

				// miRNA targets
				finalQueryString = "SELECT distinct Matures.Name FROM Matures Matures inner join TargetGenes TargetGenes on Matures.ID=TargetGenes.mID inner join TargetGene TargetGene on TargetGenes.ID=TargetGene.tgsID where NOT DB='ensemble' AND TargetGene.Accession = '"
						+ gene + "' ";

				if (hsaOnly) {
					finalQueryString += " AND TargetGenes.SpeciesID=54";
				}
				finalQueryString += ";";

				list = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, finalQueryString);
				// System.out.println(list.size());
				genes2mrinaTargets.put(gene, new HashSet<String>());

				for (int i = 0; i < list.size(); i++) {
					genes2mrinaTargets.get(gene).add(list.get(i).getColumn()[0]);
				}
			}
			count++;
		}

		HashSet<String> cleanMirnaT = new HashSet<String>();
		HashSet<String> cleanMirnaS = new HashSet<String>();
		HashSet<String> cleanMirnaST = new HashSet<String>();

		// HashSet<String> mirnas;
		Iterator<String> it2;
		// String mirna;
		StringBuilder sb = new StringBuilder();
		sb.append("PathwayID\tunigene\tmirna\r\n");
		for (String pathway : pw2genes.keySet()) {
			it = pw2genes.get(pathway).iterator();
			while (it.hasNext()) {
				gene = it.next();
				if (genes2mrinaTargets.containsKey(gene)) {
					it2 = genes2mrinaTargets.get(gene).iterator();
					while (it2.hasNext()) {
						mirna = it2.next();
						sb.append(pathway + "\t" + gene + "\t" + mirna + "\r\n");
					}
				}
			}
		}

		PrintWriter out;
		try {
			out = new PrintWriter("output.txt");
			out.println(sb.toString());
			out.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (String key : pw2genes.keySet()) {
			cleanMirnaT.clear();
			cleanMirnaS.clear();
			cleanMirnaST.clear();

			it = pw2genes.get(key).iterator();

			while (it.hasNext()) {
				gene = it.next();

				if (genes2mrinaSources.containsKey(gene)) {
					cleanMirnaS.addAll(genes2mrinaSources.get(gene));
				}
				if (genes2mrinaTargets.containsKey(gene)) {
					cleanMirnaT.addAll(genes2mrinaTargets.get(gene));
				}
				cleanMirnaST.addAll(cleanMirnaS);
				cleanMirnaST.addAll(cleanMirnaT);

			}
			System.out.println("PW: " + key + " genes: " + pw2genes.get(key).size() + " Sources: " + cleanMirnaS.size() + " Targets: "
					+ cleanMirnaT.size() + " Union_S+T: " + cleanMirnaST.size() + " Intersect_S+T: "
					+ (cleanMirnaS.size() + cleanMirnaT.size() - cleanMirnaST.size()));

		}

		/*
		 * Iterator<String> it = gene2mirna.keySet().iterator();
		 * ArrayList<DBColumn> list2 = new ArrayList<DBColumn>();
		 * 
		 * Writer writer = null; try { writer = new BufferedWriter(new
		 * OutputStreamWriter( new FileOutputStream("genes2miRNA.txt"),
		 * "utf-8")); writer.write("gene,miRNAs"); while (it.hasNext()) { gene =
		 * it.next(); writer.write(gene + "," + gene2mirna.get(gene) + "\n"); //
		 * System.out.println(gene+" "+gCount.get(gene));; }
		 * 
		 * } catch (IOException ex) { // report } finally { try {
		 * writer.close(); } catch (Exception ex) { } }
		 * 
		 * int i = 0; while (it.hasNext()) {// && i<10) { if (i % 10 == 0) { //
		 * System.out.println(i*100.0/gCount.keySet().size()+"%"); // } gene =
		 * it.next();// list.get(i).getColumn()[0];
		 * System.out.println(gene2mirna.get(gene));
		 * 
		 * String q2 = "SELECT kegg_genes_pathway.name,kegg_genes_pathway.name,"
		 * +
		 * "kegg_genes_pathway.number,kegg_genes_pathway.org, kegg_genes_name.name FROM "
		 * + "dawismd.kegg_genes_pathway inner join " +
		 * "dawismd.kegg_genes_name on kegg_genes_pathway.id=kegg_genes_name.id "
		 * + "where kegg_genes_name.name = '" + gene +
		 * "' and kegg_genes_pathway.org='hsa' order by kegg_genes_pathway.name,"
		 * + "kegg_genes_name.name;"; // q2.replaceFirst(QUESTION_MARK,gene); //
		 * System.out.println(q2); list2 = new // Wrapper().requestDbContent(2,
		 * q2); // System.out.println(list2.size()); for (int j = 0; j <
		 * list2.size(); j++) { number = list2.get(j).getColumn()[2];
		 * 
		 * if (map.containsKey(number)) { map.put(number,
		 * 
		 * map.get(number) + gene2mirna.get(gene)); } else { map.put(number,
		 * gene2mirna.get(gene)); } } i++; }
		 * 
		 * it = map.keySet().iterator(); String key; while (it.hasNext()) { key
		 * = it.next(); System.out.println(key + "\t" + map.get(key)); }
		 */

		/*
		 * if (allKEGGPathways.size() > 0) { MirnaResultKeggWindow
		 * mirnaResultKeggWindow = new MirnaResultKeggWindow( allKEGGPathways);
		 * Vector keggPAthwayResults = mirnaResultKeggWindow .getAnswer(); if
		 * (keggPAthwayResults.size() != 0) { String keggPathwayNumber = "";
		 * String keggPathwayName = ""; final Iterator it3 = keggPAthwayResults
		 * .iterator(); while (it3.hasNext()) {
		 * 
		 * String[] pathwayResutls = (String[]) it3 .next(); keggPathwayNumber=
		 * "hsa"+pathwayResutls[1]; keggPathwayName = pathwayResutls[0]; } } }
		 */
		// }
		// }
	}

}
