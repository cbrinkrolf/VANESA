package graph.algorithms.gui;

import graph.algorithms.alignment.BiologicalNodeSet;

import java.util.HashMap;
import java.util.Iterator;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;

//MARTIN class could be deleted?
public class NodeSetComparison {

	private HashMap<String, BiologicalNodeSet> type2NodeSet_graphA, type2NodeSet_graphB;
	
	//private DoubleMatrix2D similarityMatrix;
	
	
	public NodeSetComparison(HashMap<String, BiologicalNodeSet> mapA, 
			HashMap<String, BiologicalNodeSet> mapB) {
		
		type2NodeSet_graphA = mapA;
		type2NodeSet_graphB = mapB;
		
		
		compare();
		
	}

	private void compare() {
		
		String type;
		BiologicalNodeSet nodeSet_A;
		BiologicalNodeSet nodeSet_B;
		
		for (Iterator<String> iter = type2NodeSet_graphB.keySet().iterator(); iter.hasNext();) {
			type = iter.next();
			nodeSet_A = type2NodeSet_graphA.get(type);
			nodeSet_B = type2NodeSet_graphB.get(type);
			
			if (nodeSet_A == null) {
				// do not compare
			}
			if(type.equalsIgnoreCase(Elementdeclerations.protein)){
				
				// call blastp
				HashMap<String, String> mapA = new HashMap<String, String>();
				for (Iterator iterator = nodeSet_A.id2Node.keySet().iterator(); iterator.hasNext();) {
					Protein protein = (Protein) iterator.next();
					String aaSeq = "";//protein.getAAsequence();
					mapA.put(protein.getName(), aaSeq);
				}
				HashMap<String, String> mapB = new HashMap<String, String>();
				for (Iterator iterator = nodeSet_B.id2Node.keySet().iterator(); iterator.hasNext();) {
					Protein protein = (Protein) iterator.next();
					String aaSeq = "";//protein.getAAsequence();
					mapB.put(protein.getName(), aaSeq);
				}
				
			}
			
			if(type.equalsIgnoreCase(Elementdeclerations.gene)){
				
				// call blastn
				HashMap<String, String> mapA = new HashMap<String, String>();
				for (Iterator iterator = nodeSet_A.id2Node.keySet().iterator(); iterator.hasNext();) {
//					Protein gene = (Protein) iterator.next();
					String ntSeq = "";
					String name = "";
					if (iterator.next() instanceof Gene) {
						Gene gene = (Gene) iterator.next();
						name = gene.getName();
//						ntSeq = gene.getNtSeq();
					}
					else if (iterator.next() instanceof DNA) {
						DNA dna = (DNA) iterator.next();
						name = dna.getName();
//						ntSeq = gene.getNtSeq();
					}
					else if (iterator.next() instanceof RNA) {
						RNA  rna = (RNA ) iterator.next();
						name = rna.getName();
//						ntSeq = gene.getNtSeq();
					}
					mapA.put(name, ntSeq);
				}
				HashMap<String, String> mapB = new HashMap<String, String>();
				for (Iterator iterator = nodeSet_B.id2Node.keySet().iterator(); iterator.hasNext();) {
//					Protein gene = (Protein) iterator.next();
					String ntSeq = "";
					String name = "";
					if (iterator.next() instanceof Gene) {
						Gene gene = (Gene) iterator.next();
						name = gene.getName();
//						ntSeq = gene.getNtSeq();
					}
					else if (iterator.next() instanceof DNA) {
						DNA dna = (DNA) iterator.next();
						name = dna.getName();
//						ntSeq = gene.getNtSeq();
					}
					else if (iterator.next() instanceof RNA) {
						RNA  rna = (RNA ) iterator.next();
						name = rna.getName();
//						ntSeq = gene.getNtSeq();
					}
					mapB.put(name, ntSeq);
				}
				
				
			}
			
			if(type.equalsIgnoreCase(Elementdeclerations.compound)){
				
				// compare Formula
				
			}
			
		}
		
		
		

		
	}
	
	
	
}
