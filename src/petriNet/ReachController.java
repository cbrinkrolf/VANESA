package petriNet;

import java.util.HashSet;
import java.util.Iterator;

import graph.GraphInstance;
import biologicalElements.Pathway;

public class ReachController {

	private Pathway pw;
	private GraphInstance graphInstance = new GraphInstance();
	private HashSet nodes;
	
	public ReachController(){
	
		//this.pw = graphInstance.getPathway();
		//this.nodes = this.pw.getAllNodes();
		
		
		
		if(this.isBounded()){
			//Reach r = new Reach();
			//System.out.println("beschraenkt");
		}
		else{
		//	System.out.println("(partiell) unbeschraenkt");
			
		}
		
		Cov c = new Cov();
	}
	
	private boolean isBounded(){
		//boolean bounded = false;
		
		Iterator it = this.nodes.iterator();
		Object o;
		Place p;
		while(it.hasNext()){
			o = it.next();
			if(o instanceof Place){
				p = (Place) o;
				//System.out.println(p.getName());
				if(p.getTokenMax() <= 0.0){
					return false;
				}
			}
		}
		return true;
		
	}
}
