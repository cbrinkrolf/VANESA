package io;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.gui.Parameter;
import gui.MainWindow;
import gui.MainWindowSingleton;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;

import petriNet.ContinuousTransition;
import petriNet.PNEdge;
import petriNet.Place;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import de.uni_bielefeld.cebitec.mzurowie.pretty_formula.main.FormulaParser;

public class PNDoc {

	private StringBuilder sb = new StringBuilder();
	private MainWindow w = MainWindowSingleton.getInstance();
	private GraphContainer con = ContainerSingelton.getInstance();
	private Pathway pw = con.getPathway(w.getCurrentPathway());

	public PNDoc() {
		MainWindow w = MainWindowSingleton.getInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		Pathway pw = con.getPathway(w.getCurrentPathway());

		this.createHeader();
		this.writeInitialValues();

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodesSortedAlphabetically().iterator();
		BiologicalNodeAbstract bna;

		sb.append("\\section{Gleichungen}\n");
		int i = 0;
		while (it.hasNext() && i < 3) {
			bna = it.next();

			if (bna instanceof ContinuousTransition) {
				// i++;
				this.createReaction((ContinuousTransition) bna);
			}
		}

		this.createFooter();
		this.writeFile();

	}

	private void createReaction(ContinuousTransition t) {

		// System.out.println(t.getName());
		Iterator<BiologicalEdgeAbstract> it = pw.getGraph().getJungGraph()
				.getInEdges(t).iterator();
		BiologicalEdgeAbstract bea;
		BiologicalNodeAbstract bna;
		sb.append("\\cprotect\\subsection{\\verb\""+t.getName()+"\"}\n"
				+ "\\noindent\\verb\"" + t.getName() + "\" : $");
		String weight;
		PNEdge edge;

		while (it.hasNext()) {
			bea = it.next();
			bna = bea.getFrom();
			weight = "";
			if (bea instanceof PNEdge) {
				edge = (PNEdge) bea;
				if (!edge.getFunction().equals("1")) {
					weight = edge.getFunction() + " ";
				}
			}
			if (bna.hasRef()) {
				bna = bea.getFrom().getRef();
			}
			sb.append("\\verb\"" + weight + bna.getName() + "\"");
			if (it.hasNext()) {
				sb.append(" + ");
			}
		}

		sb.append(" \\rightarrow ");

		it = pw.getGraph().getJungGraph().getOutEdges(t).iterator();
		while (it.hasNext()) {
			bea = it.next();
			bna = bea.getTo();

			weight = "";
			if (bea instanceof PNEdge) {
				edge = (PNEdge) bea;
				if (!edge.getFunction().equals("1")) {
					weight = edge.getFunction() + " ";
				}
			}

			if (bea.getTo().hasRef()) {
				bna = bea.getTo().getRef();
			}
			sb.append("\\verb\"" + weight + bna.getName() + "\"");
			if (it.hasNext()) {
				sb.append(" + ");
			}
		}
		sb.append("$\n");

		sb.append("\\begin{align*}\n");
		sb.append("\\scriptstyle\n");
		// System.out.println(t.getMaximumSpeed());
		sb.append("f = " + FormulaParser.parseToLatex(t.getMaximumSpeed())
				+ "\n");

		sb.append("\\end{align*}\n");

		if (t.getParameters().size() > 0) {
			sb.append("\\vspace{1em}\n");

			sb.append("\\begin{center}\\begin{tabular}{lll}\\toprule\n");

			sb.append("Name & Value & Unit\\\\\\midrule\n");

			Parameter p;
			for (int i = 0; i < t.getParameters().size(); i++) {
				p = t.getParameters().get(i);

				sb.append("\\verb+" + p.getName() + "+ & " + p.getValue()
						+ " & " + p.getUnit() + "\\\\\n");

			}
			sb.append("\\bottomrule\n");
			sb.append("\\end{tabular}\\end{center}\n");
			sb.append("\\vspace{2em}\n");
		}

		sb.append("\\hrule\n");
		sb.append("\\vspace{2em}\n");
	}

	private void createHeader() {
		sb.append("" + "\\documentclass{article}\n"
				+ "\\usepackage{amsmath,verbatim,booktabs,longtable,cprotect}\n"
				+ "\\begin{document}\n"
				+ "\\begin{center}"
				+ "\\TeX ed by \\emph{VANESA} on \\today, Copyright \\copyright\n"
				+ "\\end{center}\n"
				+ "\\tableofcontents\n"
				+ "\\newpage\n");
	}

	private void createFooter() {
		sb.append("\\end{document}");
	}

	private void writeFile() {
		PrintWriter out;
		try {
			out = new PrintWriter("doc.tex");
			out.println(sb.toString());
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void writeInitialValues(){
		
		
		sb.append("\\section*{Startwerte}\n"
				+ "\\addcontentsline{toc}{section}{Startwerte}\n"
				+ "\\begin{center}\\begin{longtable}{lll}\\toprule\n");

		sb.append("Name & Value & Unit\\\\\\midrule\n");

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodesSorted().iterator();
		BiologicalNodeAbstract bna;
		Place p;
		int i = 0;
		while (it.hasNext() && i < 3) {
			bna = it.next();
			if(bna instanceof Place){
			
			p = (Place) bna;

			sb.append("\\verb+" + p.getName() + "+ & " + p.getTokenStart()
					+ " & mmol" + "\\\\\n");
			}
		}
		sb.append("\\bottomrule\n");
		sb.append("\\end{longtable}\\end{center}\n"
				+ "\\newpage\n");
		
	}

}
