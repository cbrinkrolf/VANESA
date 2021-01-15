package graph.algorithms;

import java.util.Comparator;
import java.util.HashSet;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class DCBresultSet implements Comparator<DCBresultSet>{
	
	int numOfVertices;
	double density;
	int numOfhomogenAttributes;
	String labels;
	HashSet<BiologicalNodeAbstract> vertices;
	

	public DCBresultSet(int numOfVertices, double density, int numOfhomogenAttributes, 
			String labels, HashSet<BiologicalNodeAbstract> vertices) {
		this.numOfVertices = numOfVertices;
		this.density = density;
		this.numOfhomogenAttributes = numOfhomogenAttributes;
		this.labels = labels;
		this.vertices = vertices;
	}


	public DCBresultSet() {
	}


	public int getNumOfVertices() {
		return numOfVertices;
	}


	public double getDensity() {
		return density;
	}


	public int getNumOfhomogenAttributes() {
		return numOfhomogenAttributes;
	}


	public String getLabels() {
		return labels;
	}


	public HashSet<BiologicalNodeAbstract> getVertices() {
		return vertices;
	}



	@Override
	public int compare(DCBresultSet o1, DCBresultSet o2) {
		Integer thisNum = o1.getNumOfVertices();
		Integer otherNum = o2.getNumOfVertices();
		
        int c1 = (thisNum.compareTo(otherNum))*(-1); // first compare
        if (c1 != 0) {
        	return c1;
        } 
        
        Double thisDens = o1.getDensity();
        Double otherDens = o2.getDensity();
        
        int c2 = (thisDens.compareTo(otherDens))*(-1); // second compare
        if (c2 != 0) {
        	return c2;
        } 
		
        Integer thisDim = o1.getNumOfhomogenAttributes();
        Integer otherDim = o2.getNumOfhomogenAttributes();
		
        int c3 = (thisDim.compareTo(otherDim))*(-1); // third compare


        return c3;
	
	}
	
	

}
