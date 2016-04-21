package graph.algorithms.centralities.betweenness;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Betweennesscentralitiy {

	private int nodei[];
	private int nodej[];
	private int threads = 1;
	private boolean enableComments = false;

	public Betweennesscentralitiy(int[] nodei, int[] nodej) {
		this.nodei = nodei;
		this.nodej = nodej;
	}

	public GraphCentrality calcCentrality() throws InterruptedException,
			ExecutionException {

		String[] combinedEdges = new String[nodei.length]; // Kombiniert
		int addedtimesedgesUsed[] = new int[nodei.length]; // Counter-Array

		Map<Integer, Vertex> myMap = new HashMap<>();
		Vertex v1 = null;
		Vertex v2 = null;

		// Aufbau des Netzes
		for (int y = 0; y < nodei.length; y++) {

			combinedEdges[y] = (String.valueOf(nodei[y]) + String
					.valueOf(nodej[y])); // 01,10,12,21...

			v1 = myMap.get(nodei[y]); // hole knoten y aus der hashmap
			v2 = myMap.get(nodej[y]);

			if (v1 == null) {// wenn knoten noch nicht existiert, lege ihn an
				myMap.put(nodei[y], new Vertex(nodei[y], threads));
			}

			if (v2 == null) {
				myMap.put(nodej[y], new Vertex(nodej[y], threads));
			}

			v1 = myMap.get(nodei[y]);
			v2 = myMap.get(nodej[y]);

			v1.adjacencies.add(new Edge(v2, v1)); // fuege eine kante zwischen
													// den beiden knoten ein
		}

		long time = System.currentTimeMillis();
		
		// Parallele Berechnung des kuerzesten Pfades
		Collection<Vertex> values = myMap.values();
		Vertex[] vertices = values.toArray(new Vertex[values.size()]);
		Set<Callable<int[]>> callables = new HashSet<>();

		for (int i = 0; i < vertices.length; i++) {
			callables.add(new MyCallable(vertices, combinedEdges, i, threads));
		}

		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		List<Future<int[]>> results = executorService.invokeAll(callables);

		// Addieren der Ergebnisse
		for (Future<int[]> future : results) {
			int[] futureArray = (int[]) future.get();

			for (int j = 0; j < addedtimesedgesUsed.length; j++) {
				// System.out.print(futureArray[j]);
				addedtimesedgesUsed[j] += futureArray[j];
			}
		}

		executorService.shutdown();
		System.out.println(threads+"\t"+(System.currentTimeMillis()-time)/1000.0);
		
		

		if (enableComments == true) {
			for (int i = 0; i < vertices.length; i++) {
				System.out.println("Knoten " + vertices[i].id + " wurde "
						+ vertices[i].timeswalkedover.get() + " mal besucht.");

			}
			for (int i = 0; i < nodei.length; i++) {
				System.out.print(nodei[i] + " ");
			}
			System.out.println();
			for (int i = 0; i < nodej.length; i++) {
				System.out.print(nodej[i] + " ");
			}
			System.out.println();
			for (int i = 0; i < addedtimesedgesUsed.length; i++) {
				System.out.print(addedtimesedgesUsed[i] + " ");
			}
			System.out
					.println("<---- So oft wurde entsprechende Kante genutzt");
		}
		
		return new GraphCentrality(vertices, addedtimesedgesUsed);

	}

	public static void main(String[] args) throws InterruptedException,
			ExecutionException, IOException, ClassNotFoundException {
		// public static void calculateBetweennesscentralitiy(int nodei[],int
		// nodej[],int threads)throws InterruptedException, ExecutionException,
		// IOException, ClassNotFoundException{

		int nodei[] = { 0, 1, 1, 2, 2, 0, 0, 3 };
		int nodej[] = { 1, 0, 2, 1, 0, 2, 3, 0 };

		Betweennesscentralitiy n = new Betweennesscentralitiy(nodei,nodej);
		System.out.println(n.calcCentrality().toString());

	}
	
	//Decorator for 
	public class GraphCentrality{
		@Override
		public String toString() {
			
			String ret = new String();
			ret+="GraphCentrality [vertices=";
			
			for(Vertex v : vertices)
				ret+=v.id+":"+v.timeswalkedover+" ";
			
			ret+=", edgeswalkedover="
					+ Arrays.toString(edgeswalkedover) + "]";
			
			return ret;
		}

		public Vertex[] vertices;
		public int[] edgeswalkedover;
		
		public GraphCentrality(Vertex[] vertices, int[] addedtimesedgesUsed){
			this.vertices = vertices;
			this.edgeswalkedover = addedtimesedgesUsed;
		}
	}
}
