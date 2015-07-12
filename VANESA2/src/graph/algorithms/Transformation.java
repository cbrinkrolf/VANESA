package graph.algorithms;

import java.util.HashSet;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Transformation {
	
	public Pathway resolveReferences(Pathway pw){
		//System.out.println("resolve");
		Iterator<BiologicalEdgeAbstract> it = pw.getAllEdges().iterator();
		BiologicalEdgeAbstract bea;
		BiologicalNodeAbstract ref;
		HashSet<BiologicalNodeAbstract> references = new HashSet<BiologicalNodeAbstract>();
		System.out.println("nodes: "+pw.getAllGraphNodes().size());
		while(it.hasNext()){
			bea = it.next();
			System.out.println(bea.getFrom().getLabel()+" -> "+bea.getTo().getLabel());
			if(bea.getFrom().hasRef()){
				//Object o = new Object();
				//bea.clone();
				System.out.println("drin");
				//System.out.println("drin");
				//System.out.println(bea.getFrom().getLabel());
				//System.out.println(bea.getFrom().getRef().getLabel());
				pw.removeElement(bea);
				ref = bea.getFrom();
				//references.add(bea.getFrom());
				//System.out.println(bea.getFrom().getID());
				bea.setFrom(ref.getRef());
				pw.addEdge(bea);
				references.add(ref);
			}
			if(bea.getTo().hasRef()){
				System.out.println("drin2");
				//System.out.println(bea.getTo().getLabel());
				//System.out.println(bea.getTo().getRef().getLabel());
				//System.out.println("found ref");
				//System.out.println(bea.getTo().getID());
				ref = bea.getTo();
				
				bea.setTo(ref.getRef());
				pw.addEdge(bea);
				references.add(ref);
			}
		}
		System.out.println(references.size());
		
		Iterator<BiologicalNodeAbstract> nodes = references.iterator();
		 BiologicalNodeAbstract bna; 
		 while(nodes.hasNext()){
			 bna = nodes.next();
			 System.out.println(bna.getLabel());
			 pw.removeElement(bna);
		 }
		 
		 //System.out.println("nodes: "+pw.getAllNodes().size());
		 //System.out.println("nodes: "+pw.getGraph().getJungGraph().getVertexCount());
		 //System.out.println("edges: "+pw.getGraph().getJungGraph().getEdgeCount());
		
		return pw;
	}

}
