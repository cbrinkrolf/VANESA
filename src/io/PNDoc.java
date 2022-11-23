package io;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.Place;
import de.uni_bielefeld.cebitec.mzurowie.pretty_formula.main.FormulaParser;
import graph.GraphContainer;
import graph.gui.Parameter;
import gui.MainWindow;
import gui.MyPopUp;

public class PNDoc {

	private StringBuilder sb = new StringBuilder();
	private MainWindow w = MainWindow.getInstance();
	private GraphContainer con = GraphContainer.getInstance();
	private Pathway pw = con.getPathway(w.getCurrentPathway());

	public PNDoc(String file) {
		MainWindow w = MainWindow.getInstance();
		Pathway pw = con.getPathway(w.getCurrentPathway());

		this.createHeader();
		this.writeInitialValues();

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodesSortedAlphabetically().iterator();
		BiologicalNodeAbstract bna;

		sb.append("\\section*{Equations}\n"
		+ "\\addcontentsline{toc}{section}{Equations}\n");
		
		int i = 0;
		while (it.hasNext() && i < 3) {
			bna = it.next();
			// System.out.println(bna.getName());
			if (bna instanceof ContinuousTransition) {
				// i++;
				this.createReaction((ContinuousTransition) bna);
			}
		}

		this.createFooter();
		this.writeFile(file);

	}

	private void createReaction(ContinuousTransition t) {

		// System.out.println(t.getName());
		Iterator<BiologicalEdgeAbstract> it = pw.getGraph().getJungGraph()
				.getInEdges(t).iterator();
		BiologicalEdgeAbstract bea;
		BiologicalNodeAbstract bna;
		
		String knocked = "";
		if(t.isKnockedOut()){
			knocked = " (knocked out, $v=0$)";
		}
		sb.append("\\cprotect\\subsection{\\verb\""+t.getName()+"\""+knocked+"}\n"
				+ "\\noindent\\verb\"" + t.getName() + "\" : $");
		String weight;
		PNArc edge;

		while (it.hasNext()) {
			bea = it.next();
			bna = bea.getFrom();
			weight = "";
			if (bea instanceof PNArc) {
				edge = (PNArc) bea;
				if (!edge.getFunction().equals("1")) {
					weight = edge.getFunction() + " ";
				}
			}
			if (bna.isLogical()) {
				bna = bea.getFrom().getLogicalReference();
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
			if (bea instanceof PNArc) {
				edge = (PNArc) bea;
				if (!edge.getFunction().equals("1")) {
					weight = edge.getFunction() + " ";
				}
			}

			if (bea.getTo().isLogical()) {
				bna = bea.getTo().getLogicalReference();
			}
			sb.append("\\verb\"" + weight + bna.getName() + "\"");
			if (it.hasNext()) {
				sb.append(" + ");
			}
		}
		sb.append("$\n");

		sb.append("\\begin{align*}\n");
		sb.append("\\scriptstyle\n");
		// System.out.println(t.getMaximalSpeed());
		sb.append("f = " + FormulaParser.parseToLatex(t.getMaximalSpeed())
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
		String name = pw.getName();
		name = name.replace("_", "\\_");
		sb.append("" + "\\documentclass{article}\n"
				+ "\\usepackage{amsmath,verbatim,booktabs,longtable,cprotect,graphicx,a4wide}\n"
				+ "\\begin{document}\n"
				+ "\\pagenumbering{gobble}\n"
				+ "\\begin{titlepage}"
				+ "\\author{\\TeX ed by \\emph{VANESA} Copyright \\copyright}\n"
				+ "\\title{Documentation of "+ name +"}\n"
				+ "\\date{\\today}\n"
				+ "\\maketitle\n"
				+ "\\end{titlepage}\n"
				+ "\\newpage\n"
				+ "\\pagenumbering{arabic}\n"
				+ "\\begin{figure}[!ht]\n"
				+ "\\centering\n"
				+ "\\includegraphics[width=1.2\\textwidth]{export}"
				+ "\\caption{Picture of Network}\n"
				+ "\\end{figure}\n"
				+ "\\newpage\n"
				+ "\\tableofcontents\n"
				+ "\\newpage\n"
				+ "\\setcounter{section}{1}");
	}

	private void createFooter() {
		sb.append("\\end{document}");
	}

	private void writeFile(String file) {
		PrintWriter out;
		try {
			out = new PrintWriter(file);
			out.println(sb.toString());
			out.close();
			//MyPopUp.getInstance().show("Latex export successful!", "Latex file was written to:\n"+file);
		} catch (FileNotFoundException e) {
			MyPopUp.getInstance().show("Latex export error!", "Something went wrong!");
			e.printStackTrace();
		}

	}
	
	private void writeInitialValues(){
		
		
		sb.append("\\section*{Initial values}\n"
				+ "\\addcontentsline{toc}{section}{Initial values}\n"
				+ "\\begin{center}\\begin{longtable}{lll}\\toprule\n");

		sb.append("Name & Value & Unit\\\\\\midrule\n");

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodesSortedAlphabetically().iterator();
		BiologicalNodeAbstract bna;
		Place p;
		while (it.hasNext()) {
			bna = it.next();
			if(bna instanceof Place && !bna.isLogical()){
			//System.out.println(bna.getName());
			p = (Place) bna;

			sb.append("\\verb+" + p.getName() + "+ & " + p.getTokenStart()
					+ " & mmol");
			if(bna.isConstant()){
				sb.append(" (const.)");
			}
			sb.append("\\\\\n");
			}
		}
		sb.append("\\bottomrule\n");
		sb.append("\\end{longtable}\\end{center}\n"
				+ "\\newpage\n");
	}
}
