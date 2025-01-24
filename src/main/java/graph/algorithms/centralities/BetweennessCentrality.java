package graph.algorithms.centralities;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BetweennessCentrality {
	private final int[] nodei;
	private final int[] nodej;
	private int threads = 1;
	private boolean enableComments = false;

	public BetweennessCentrality(int[] nodei, int[] nodej) {
		this.nodei = nodei;
		this.nodej = nodej;
	}

	public GraphCentrality calcCentrality() throws InterruptedException, ExecutionException {
		final String[] combinedEdges = new String[nodei.length];
		final int[] addedtimesedgesUsed = new int[nodei.length];
		final Map<Integer, Vertex> myMap = new HashMap<>();

		// Create the network
		for (int y = 0; y < nodei.length; y++) {
			combinedEdges[y] = nodei[y] + String.valueOf(nodej[y]); // 01,10,12,21...

			Vertex v1 = myMap.get(nodei[y]);
			Vertex v2 = myMap.get(nodej[y]);
			if (v1 == null) {// wenn knoten noch nicht existiert, lege ihn an
				v1 = new Vertex(nodei[y]);
				myMap.put(nodei[y], v1);
			}
			if (v2 == null) {
				v2 = new Vertex(nodej[y]);
				myMap.put(nodej[y], v2);
			}
			v1.adjacencies.add(new Edge(v2, v1)); // fuege eine kante zwischen den beiden knoten ein
		}

		long time = System.currentTimeMillis();

		// Parallele Berechnung des kuerzesten Pfades
		Collection<Vertex> values = myMap.values();
		Vertex[] vertices = values.toArray(new Vertex[0]);
		Set<Callable<int[]>> callables = new HashSet<>();

		for (int i = 0; i < vertices.length; i++) {
			callables.add(new ShortestPathCalculator(vertices, combinedEdges, i));
		}

		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		List<Future<int[]>> results = executorService.invokeAll(callables);

		// Addieren der Ergebnisse
		for (Future<int[]> future : results) {
			int[] futureArray = future.get();
			for (int j = 0; j < addedtimesedgesUsed.length; j++) {
				addedtimesedgesUsed[j] += futureArray[j];
			}
		}

		executorService.shutdown();
		System.out.println(threads + "\t" + (System.currentTimeMillis() - time) / 1000.0);

		if (enableComments) {
			for (Vertex vertex : vertices) {
				System.out.printf("Knoten %d wurde %d mal besucht.%n", vertex.id, vertex.timesWalkedOver.get());
			}
			for (int j : nodei) {
				System.out.print(j + " ");
			}
			System.out.println();
			for (int j : nodej) {
				System.out.print(j + " ");
			}
			System.out.println();
			for (int j : addedtimesedgesUsed) {
				System.out.print(j + " ");
			}
			System.out.println("<---- So oft wurde entsprechende Kante genutzt");
		}
		return new GraphCentrality(vertices, addedtimesedgesUsed);
	}

	public static class GraphCentrality {
		public final Vertex[] vertices;
		public final int[] edgesWalkedOver;

		public GraphCentrality(Vertex[] vertices, int[] addedtimesedgesUsed) {
			this.vertices = vertices;
			this.edgesWalkedOver = addedtimesedgesUsed;
		}

		@Override
		public String toString() {
			final StringBuilder ret = new StringBuilder("GraphCentrality [vertices=");
			for (Vertex v : vertices)
				ret.append(v.id).append(":").append(v.timesWalkedOver).append(" ");
			ret.append(", edgeswalkedover=").append(Arrays.toString(edgesWalkedOver)).append("]");
			return ret.toString();
		}
	}

	public static class Edge {
		public final Vertex source;
		public final Vertex target;

		public Edge(Vertex argTarget, Vertex argSource) {
			target = argTarget;
			source = argSource;
		}
	}

	public static class Vertex {
		public final int id;
		/**
		 * Thread-safe visited counter
		 */
		public final AtomicInteger timesWalkedOver = new AtomicInteger(0);
		public final ArrayList<Edge> adjacencies = new ArrayList<>();
		private final ConcurrentHashMap<Long, Integer> minDistances = new ConcurrentHashMap<>();
		private final ConcurrentHashMap<Long, Vertex> previouses = new ConcurrentHashMap<>();

		public Vertex(int number) {
			id = number;
		}

		public int getMinDistance(long threadId) {
			Integer ret = minDistances.get(threadId);
			return ret == null ? Integer.MAX_VALUE : ret;
		}

		public Vertex getPrevious(long threadId) {
			return previouses.get(threadId);
		}

		public void setPrevious(long threadId, Vertex previous) {
			if (previous == null) {
				previouses.remove(threadId);
			} else {
				previouses.put(threadId, previous);
			}
		}

		public void setMinDistance(long threadId, int minDistance) {
			minDistances.put(threadId, minDistance);
		}

		@Override
		public String toString() {
			return Integer.toString(id);
		}
	}

	private static class ShortestPathCalculator implements Callable<int[]> {
		private final Vertex[] vertices;
		private final int[] timesEdgesUsed;
		private final int threadId;
		private final List<String> combinedEdgesList;

		public ShortestPathCalculator(Vertex[] vertices, final String[] combinedEdges, final int threadId) {
			this.vertices = vertices;
			this.timesEdgesUsed = new int[combinedEdges.length];
			this.threadId = threadId;
			combinedEdgesList = Arrays.asList(combinedEdges);
			Arrays.fill(timesEdgesUsed, 0);
		}

		@Override
		public int[] call() {
			computePaths(vertices[threadId]);
			for (Vertex v : vertices) {
				List<String> usedEdges = getShortestPathTo(v);
				for (String string : usedEdges) {
					int index = combinedEdgesList.indexOf(string);
					timesEdgesUsed[index]++;
				}
			}
			for (Vertex v : vertices) {
				v.setPrevious(threadId, null);
				v.setMinDistance(threadId, Integer.MAX_VALUE);
			}
			return timesEdgesUsed;
		}

		private void computePaths(Vertex source) {
			source.setMinDistance(threadId, 0);
			Comparator<Vertex> comparator = (v, u) -> Integer.compare(v.getMinDistance(threadId),
																	  u.getMinDistance(threadId));

			PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>(vertices.length, comparator);
			vertexQueue.add(source);

			while (!vertexQueue.isEmpty()) {
				Vertex u = vertexQueue.poll();

				// Jede Kante besuchen, die u verlaesst
				for (Edge e : u.adjacencies) {
					Vertex v = e.target;

					int distanceThroughU = u.getMinDistance(threadId) + 1;
					if (distanceThroughU < v.getMinDistance(threadId)) {
						vertexQueue.remove(v);

						v.setMinDistance(threadId, distanceThroughU);
						v.setPrevious(threadId, u);
						vertexQueue.add(v);
					}
				}
			}
		}

		private List<String> getShortestPathTo(Vertex target) {
			List<Vertex> path = new ArrayList<>();
			List<String> usedEdges = new ArrayList<>();
			for (Vertex vertex = target; vertex != null; vertex = vertex.getPrevious(threadId)) {
				path.add(vertex);
				if (vertex.getPrevious(threadId) != null) {
					vertex.timesWalkedOver.addAndGet(1);
					usedEdges.add(vertex.getPrevious(threadId).id + String.valueOf(vertex.id));
				}
			}
			Collections.reverse(path);
			return usedEdges;
		}
	}
}
