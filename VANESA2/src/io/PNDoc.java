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
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import de.uni_bielefeld.cebitec.mzurowie.pretty_formula.main.FormulaParser;

public class PNDoc {
	
	private StringBuilder sb = new StringBuilder();
	private MainWindow w = MainWindowSingleton.getInstance();
	private GraphContainer con = ContainerSingelton.getInstance();
	private Pathway pw = con.getPathway(w.getCurrentPathway());
	
	public PNDoc(){
		MainWindow w = MainWindowSingleton.getInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		Pathway pw = con.getPathway(w.getCurrentPathway());
		
		this.createHeader();
		
		Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();
		BiologicalNodeAbstract bna;
		
		int i = 0;
		while(it.hasNext() && i<3){
			bna = it.next();
			
			if(bna instanceof ContinuousTransition){
				//i++;
				this.createReaction((ContinuousTransition) bna);
			}
		}
		
		this.createFooter();
		this.writeFile();
		
		
		
	}
	
	
	private void createReaction(ContinuousTransition t){
		
		//System.out.println(t.getName());
		Iterator<BiologicalEdgeAbstract> it = pw.getGraph().getJungGraph().getInEdges(t).iterator();
		BiologicalEdgeAbstract bea;
		BiologicalNodeAbstract bna;
		sb.append("\\noindent\\verb+"+t.getName()+"+ : $");
		while(it.hasNext()){
			bea = it.next();
			bna = bea.getFrom();
			
			if(bna.hasRef()){
				bna = bea.getFrom().getRef();
			}
			sb.append("\\verb+"+bna.getName()+"+");
			if(it.hasNext()){
				sb.append(" + ");
			}
		}
		
		sb.append(" \\rightarrow ");
		
		it = pw.getGraph().getJungGraph().getOutEdges(t).iterator();
		while(it.hasNext()){
			bea = it.next();
			bna = bea.getTo();
			if(bea.getTo().hasRef()){
				bna = bea.getTo().getRef();
			}
			sb.append("\\verb+"+bna.getName()+"+");
			if(it.hasNext()){
				sb.append(" + ");
			}
		}
		sb.append("$\n");
		
		sb.append("\\begin{align*}\n");
		sb.append("\\scriptstyle\n");
		//System.out.println(t.getMaximumSpeed());
		sb.append("f = "+FormulaParser.parseToLatex(t.getMaximumSpeed())+"\n");
		
		
		sb.append("\\end{align*}\n");
		
		
		if(t.getParameters().size() > 0){
			sb.append("\\vspace{1em}\n");
			
		sb.append("\\begin{center}\\begin{tabular}{lll}\\toprule\n");
		
		
		sb.append("Name & Value & Unit\\\\\\midrule\n");
		
		Parameter p;
		for(int i = 0; i<t.getParameters().size(); i++){
			p = t.getParameters().get(i);
			
			sb.append("\\verb+"+p.getName()+"+ & "+p.getValue()+" & "+p.getUnit()+"\\\\\n" );
			
		}
		sb.append("\\bottomrule\n");
		sb.append("\\end{tabular}\\end{center}\n");
		sb.append("\\vspace{2em}\n");
		}
		
		sb.append("\\hrule\n");
	}
	
	private void createHeader(){
		sb.append(""
				+ "\\documentclass{article}\n"
				+ "\\usepackage{amsmath,verbatim,booktabs}\n"
				+ "\\begin{document}\n");
	}
	
	private void createFooter(){
		sb.append("\\end{document}");
	}
	
	private void writeFile(){
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

}
