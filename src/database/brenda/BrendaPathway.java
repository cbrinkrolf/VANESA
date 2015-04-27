package database.brenda;

import gui.ProgressBar;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

//import edu.uci.ics.jung.graph.Vertex;

public class BrendaPathway extends BrendaConnector {

	
	
	private HashSet<String> enzymes2Search = new HashSet<String>();

	public BrendaPathway(String[] details, Pathway mergePW, boolean headless) {
		super(details, mergePW, headless);
	}

	@Override
	protected void getEnzymeDetails(String[] details) {

		// System.out.println("len: "+details.length);
		
		// System.out.println("zyme: "+details[1]);
		super.enzymes.clear();
		super.enzyme_organism = adoptOrganism(details[1]);
		// System.out.println(enzyme);
		// System.out.println(tree.getRoot());
		// System.out.println("vor");
		//System.out.println("e: " + enzyme);
		enzymes2Search.add("2.7.1.2");
		enzymes2Search.add("5.3.1.9");
		enzymes2Search.add("2.7.1.11");
		enzymes2Search.add("4.1.2.13");
		enzymes2Search.add("5.3.1.1");
		enzymes2Search.add("1.2.1.12");
		enzymes2Search.add("2.7.2.3");
		enzymes2Search.add("5.4.2.11");
		enzymes2Search.add("5.4.2.12");
		enzymes2Search.add("4.2.1.11");
		enzymes2Search.add("2.7.1.40");
		enzymes2Search.add("4.1.1.1");
		enzymes2Search.add("1.1.1.1");
		enzymes2Search.add("1.1.1.27");
		enzymes2Search.add("1.2.1.10");
		enzymes2Search.add("2.3.1.54");
		enzymes2Search.add("1.2.7.1");
		enzymes2Search.add("1.12.7.2");
		enzymes2Search.add("2.3.1.8");
		enzymes2Search.add("2.7.2.1");
		Iterator<String> it = this.enzymes2Search.iterator();
		 while(it.hasNext()){
			 super.processBrendaElement(it.next(), tree.getRoot());
		 }
		
		//processBrendaElement(details[0], tree.getRoot());
		// System.out.println("ende");

	}

	@Override
	public void done() {
		super.done();
		Iterator<BiologicalNodeAbstract> it2 = super.getGraph().getAllVertices().iterator();
		 BiologicalNodeAbstract bna;
		 while(it2.hasNext()){
			 bna = it2.next();
			 if(enzymes2Search.contains(bna.getLabel())){
				 bna.setColor(Color.RED);
			 }
		 }
	}
}
