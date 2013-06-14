package database.mirna;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

import graph.ContainerSingelton;
import graph.CreatePathway;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class mirnaConnector{

	Pathway p;	
	
	public mirnaConnector(Vector keggGenes, Pathway p) {
		
		this.p = p;
		
		Iterator it = p.getAllNodes().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) it.next();

			String name = bna.getName();
			String label = bna.getLabel();
			
			if(keggGenes.contains(name)||keggGenes.contains(label)){
				bna.setColor(Color.RED);
			}
		}
		
	}

}
