package graph.algorithms;

import java.util.HashSet;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Transformation {
	
	public void resolveReferences(Pathway pw){
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		HashSet<BiologicalNodeAbstract> nodes = new HashSet<BiologicalNodeAbstract>();
		HashSet<BiologicalNodeAbstract> refNodes = new HashSet<BiologicalNodeAbstract>();
		
		// two loops, cannot operate and change (delete node) on the set which gets interated
		while(it.hasNext()){
			bna = it.next();
			if(bna.getRefs().size() >0){
				refNodes.add(bna);
			}
		}
		
		it = refNodes.iterator();
		while(it.hasNext()){
			bna = it.next();
			nodes = new HashSet<BiologicalNodeAbstract>(bna.getRefs());
			nodes.add(bna);
			pw.mergeNodes(nodes);
		}
	}

}
