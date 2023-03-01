package io;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.Place;
import prettyFormula.FormulaParser;
import graph.GraphContainer;
import graph.gui.Parameter;
import gui.MainWindow;
import gui.PopUpDialog;

public class PNDoc {
    public PNDoc(String file) {
        Pathway pw = GraphContainer.getInstance().getPathway(MainWindow.getInstance().getCurrentPathway());
        StringBuilder sb = new StringBuilder();
        createHeader(pw, sb);
        writeInitialValues(pw, sb);
        sb.append("\\section*{Equations}\n\\addcontentsline{toc}{section}{Equations}\n");
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodesSortedAlphabetically()) {
            if (bna instanceof ContinuousTransition) {
                createReaction(pw, sb, (ContinuousTransition) bna);
            }
        }
        createFooter(sb);
        writeFile(sb, file);
    }

    private void createReaction(Pathway pw, StringBuilder sb, ContinuousTransition t) {
        sb.append("\\cprotect\\subsection{\\verb\"").append(t.getName()).append("\"");
        if (t.isKnockedOut()) {
            sb.append(" (knocked out, $v=0$)");
        }
        sb.append("}\n");
        sb.append("\\noindent\\verb\"").append(t.getName()).append("\" : $");
        Iterator<BiologicalEdgeAbstract> it = pw.getGraph().getJungGraph().getInEdges(t).iterator();
        while (it.hasNext()) {
            BiologicalEdgeAbstract bea = it.next();
            BiologicalNodeAbstract bna = bea.getFrom();
            String weight = "";
            if (bea instanceof PNArc) {
                PNArc edge = (PNArc) bea;
                if (!edge.getFunction().equals("1")) {
                    weight = edge.getFunction() + " ";
                }
            }
            if (bna.isLogical()) {
                bna = bea.getFrom().getLogicalReference();
            }
            sb.append("\\verb\"").append(weight).append(bna.getName()).append("\"");
            if (it.hasNext()) {
                sb.append(" + ");
            }
        }
        sb.append(" \\rightarrow ");
        it = pw.getGraph().getJungGraph().getOutEdges(t).iterator();
        while (it.hasNext()) {
            BiologicalEdgeAbstract bea = it.next();
            BiologicalNodeAbstract bna = bea.getTo();
            String weight = "";
            if (bea instanceof PNArc) {
                PNArc edge = (PNArc) bea;
                if (!edge.getFunction().equals("1")) {
                    weight = edge.getFunction() + " ";
                }
            }
            if (bea.getTo().isLogical()) {
                bna = bea.getTo().getLogicalReference();
            }
            sb.append("\\verb\"").append(weight).append(bna.getName()).append("\"");
            if (it.hasNext()) {
                sb.append(" + ");
            }
        }
        sb.append("$\n");
        sb.append("\\begin{align*}\n");
        sb.append("\\scriptstyle\n");
        sb.append("f = ").append(FormulaParser.parseToLatex(t.getMaximalSpeed())).append("\n");
        sb.append("\\end{align*}\n");
        List<Parameter> parameters = t.getParameters();
        if (parameters.size() > 0) {
            sb.append("\\vspace{1em}\n");
            sb.append("\\begin{center}\\begin{tabular}{lll}\\toprule\n");
            sb.append("Name & Value & Unit\\\\\\midrule\n");
            for (Parameter p : parameters) {
                sb.append("\\verb+").append(p.getName()).append("+ & ").append(p.getValue()).append(" & ");
                sb.append(p.getUnit()).append("\\\\\n");
            }
            sb.append("\\bottomrule\n");
            sb.append("\\end{tabular}\\end{center}\n");
            sb.append("\\vspace{2em}\n");
        }

        sb.append("\\hrule\n");
        sb.append("\\vspace{2em}\n");
    }

    private void createHeader(Pathway pw, StringBuilder sb) {
        String name = pw.getName();
        name = name.replace("_", "\\_");
        sb.append("" + "\\documentclass{article}\n");
        sb.append("\\usepackage{amsmath,verbatim,booktabs,longtable,cprotect,graphicx,a4wide}\n");
        sb.append("\\begin{document}\n");
        sb.append("\\pagenumbering{gobble}\n");
        sb.append("\\begin{titlepage}");
        sb.append("\\author{\\TeX ed by \\emph{VANESA} Copyright \\copyright}\n");
        sb.append("\\title{Documentation of ").append(name).append("}\n");
        sb.append("\\date{\\today}\n");
        sb.append("\\maketitle\n");
        sb.append("\\end{titlepage}\n");
        sb.append("\\newpage\n");
        sb.append("\\pagenumbering{arabic}\n");
        sb.append("\\begin{figure}[!ht]\n");
        sb.append("\\centering\n");
        sb.append("\\includegraphics[width=1.2\\textwidth]{export}");
        sb.append("\\caption{Picture of Network}\n");
        sb.append("\\end{figure}\n");
        sb.append("\\newpage\n");
        sb.append("\\tableofcontents\n");
        sb.append("\\newpage\n");
        sb.append("\\setcounter{section}{1}");
    }

    private void createFooter(StringBuilder sb) {
        sb.append("\\end{document}");
    }

    private void writeFile(StringBuilder sb, String file) {
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(sb.toString());
            //PopUpDialog.getInstance().show("Latex export successful!", "Latex file was written to:\n"+file);
        } catch (FileNotFoundException e) {
            PopUpDialog.getInstance().show("Latex export error!", "Something went wrong!");
            e.printStackTrace();
        }
    }

    private void writeInitialValues(Pathway pw, StringBuilder sb) {
        sb.append("\\section*{Initial values}\n");
        sb.append("\\addcontentsline{toc}{section}{Initial values}\n");
        sb.append("\\begin{center}\\begin{longtable}{lll}\\toprule\n");
        sb.append("Name & Value & Unit\\\\\\midrule\n");
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodesSortedAlphabetically()) {
            if (bna instanceof Place && !bna.isLogical()) {
                Place p = (Place) bna;
                sb.append("\\verb+").append(p.getName()).append("+ & ").append(p.getTokenStart()).append(" & mmol");
                if (bna.isConstant()) {
                    sb.append(" (const.)");
                }
                sb.append("\\\\\n");
            }
        }
        sb.append("\\bottomrule\n");
        sb.append("\\end{longtable}\\end{center}\n");
        sb.append("\\newpage\n");
    }
}
