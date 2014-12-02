package database.mirna;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class mirnaConnector{

	Pathway p;	
	
	public mirnaConnector(Vector<String> keggGenes, Pathway p) {
		
		this.p = p;
		
		Iterator<BiologicalNodeAbstract> it = p.getAllNodes().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = it.next();

			String name = bna.getName();
			String label = bna.getLabel();
			
			if(keggGenes.contains(name)||keggGenes.contains(label)){
				bna.setColor(Color.RED);
			}
		}
		
	}

}
