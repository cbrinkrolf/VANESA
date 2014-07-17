package graph.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * Durchführung von Tests auf homogenity und density
 */
public class DCBTests {

	HashMap<Integer, HashSet<Integer>> adjacencies;
	ArrayList<Double> ranges;
	int numDim;
	double density;
	HashMap<Integer, ArrayList<Double>> attributes;
	
	
	
	public DCBTests(final HashMap<Integer, HashSet<Integer>> adjacenciesArray, double density, ArrayList<Double> ranges2, int attrdim, final HashMap<Integer, ArrayList<Double>> attributesArray) {
		this.adjacencies = adjacenciesArray;
		this.density = density;
		this.ranges = ranges2;
		this.numDim = attrdim;
		this.attributes = attributesArray;
	}
	

	
	/*
	 * Durchläuft die Attributliste und findet den größten und kleinsten Wert für jedes Attribut.
	 * Dannach wird die Distanz berechnet. Wenn die Distanz kleiner-gleich range für
	 * mindesten numDim Dimensionen ist sind die Knoten homogen.
	 */
	public boolean testHomogenity(HashSet<Integer> vertices){
		Iterator<Integer> it = vertices.iterator();
		int firstvertexID = it.next();
		int numAttr = ranges.size();
		int numOfSameDim = 0;

		for(int i = 0; i < numAttr; i++){
			double min = attributes.get(firstvertexID).get(i);
			double max = attributes.get(firstvertexID).get(i);
			
			for(int vertex : vertices){
				if(attributes.get(vertex).get(i) < min){
					min = attributes.get(vertex).get(i);
				}
				if(attributes.get(vertex).get(i) > max){
					max = attributes.get(vertex).get(i);
				}
			}
			if(max-min <= ranges.get(i)){
				numOfSameDim++;
				if(numOfSameDim == numDim){
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Density = # existierender Knoten/ # max. mögliche Knoten
	 * true wenn density des Subgraphs größer-gleich dem Grenzwert (density)
	 * Jede Kante wird zweimal gezählt (da die Adjazenzliste einen gerichten Graphen darstellt
	 */
	public boolean testDensity(HashSet<Integer> testVertices) {
		double edgecounter = 0;
		double maxedges = testVertices.size()*(testVertices.size()-1);
		for(int vertex1 : testVertices){
			for(int vertex2 : testVertices){
				assert adjacencies.containsKey(vertex1) : "Key " + vertex1 + " nicht vorhanden.";
				if(adjacencies.get(vertex1).contains(vertex2)){
					edgecounter++;
					if((edgecounter/maxedges)>=density){
						return true;
					}
				}
			}
		}
		return false;	
	}
	
	
	/*
	 * Durchläuft die Attributliste und findet den größten und kleinsten Wert für jedes Attribut.
	 * Dannach wird die Distanz berechnet. Wenn die Distanz kleiner-gleich range wird der Score 
	 * um eins erhöht.
	 */
	public int homogenityScore(HashSet<Integer> vertices) {
		Iterator<Integer> it = vertices.iterator();
		int firstvertexID = it.next();
		int numAttr = ranges.size();
		int numOfSameDim = 0;

		for(int i = 0; i < numAttr; i++){
			double min = attributes.get(firstvertexID).get(i);
			double max = attributes.get(firstvertexID).get(i);
			
			for(int vertex : vertices){
				if(attributes.get(vertex).get(i) < min){
					min = attributes.get(vertex).get(i);
				}
				if(attributes.get(vertex).get(i) > max){
					max = attributes.get(vertex).get(i);
				}
			}
			if(max-min <= ranges.get(i)){
				numOfSameDim++;
			}
		}
		return numOfSameDim;
	}

	/*
	 * Density = # existierender Knoten/ # max. mögliche Knoten
	 */
	public double densityScore(HashSet<Integer> testVertices) {
		double edgecounter = 0;
		double maxedges = testVertices.size()*(testVertices.size()-1);
		for(int vertex1 : testVertices){
			for(int vertex2 : testVertices){
				if(adjacencies.get(vertex1).contains(vertex2)){
					edgecounter++;
				}
			}
		}
		
		
		return (edgecounter/maxedges);
	}
}
