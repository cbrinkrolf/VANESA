package graph.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;

/*
 * Implementierung des Callable-Interface f�r das Preprocessing.<BR>
 * Es wird Callable eingesetzt obwohl kein R�ckgabewert ben�tigt wird. Grund daf�r ist der Aufruf
 * der Threads �ber invokeAll den es f�r Runnable nicht gibt (gibt es eine bessere M�glichkeit?)
 * 
 */
public class DCBpreprocessing implements Callable<HashSet<Integer>>{

	private int vertex1;
	private int vertex2;
	private DCBTests test;
	
	public DCBpreprocessing(int vertex1, int vertex2, HashMap<Integer, HashSet<Integer>> adjacenciesArray, double density, ArrayList<Double> ranges, int attrdim, HashMap<Integer, ArrayList<Double>> attributesArray) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.test = new DCBTests(adjacenciesArray, density, ranges, attrdim, attributesArray);
		
	}




	/*
	 * Pr�ft ob die beiden �bergebenen Knoten homogen sind. 
	 * R�ckgabewert:
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
