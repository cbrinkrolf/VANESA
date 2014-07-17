package graph.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * Implementierung des Callable-Interface für das Preprocessing.<BR>
 * Es wird Callable eingesetzt obwohl kein Rückgabewert benötigt wird. Grund dafür ist der Aufruf
 * der Threads über invokeAll den es für Runnable nicht gibt (gibt es eine bessere Möglichkeit?)
 * 
 */
public class DCBpreprocessing implements Callable<HashSet<Integer>>{

	private int vertex1;
	private int vertex2;
	private DCBTests test;
	
	public DCBpreprocessing(int vertex1, int vertex2, ConcurrentHashMap<Integer, HashSet<Integer>> adjacencies, double density, CopyOnWriteArrayList<Double> ranges, int attrdim, ConcurrentHashMap<Integer, ArrayList<Double>> attributes) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.test = new DCBTests(adjacencies, density, ranges, attrdim, attributes);
		
	}




	/*
	 * Prüft ob die beiden übergebenen Knoten homogen sind. 
	 * Rückgabewert:
	 * 	Set der Knoten der Kante, wenn die Kante homogen ist;
	 * 	ein leeres Set, sonst
	 */
	@Override
	public HashSet<Integer> call() throws Exception {
		HashSet<Integer> preResult = new HashSet<>();
		
		
		HashSet<Integer> testVertices = new HashSet<Integer>();
	
		testVertices.add(vertex1);
		testVertices.add(vertex2);
		if(test.testHomogenity(testVertices)){
			preResult.add(vertex1);
			preResult.add(vertex2);
		}

		return preResult;
	}



}
