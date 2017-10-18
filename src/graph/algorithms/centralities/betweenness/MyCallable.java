package graph.algorithms.centralities.betweenness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;


public class MyCallable implements Callable<int[]> {

  private Vertex[] vertices = null;
  private String[] combinedEdges = null;
  private int[] timesEdgesUsed = null;
  private int i;
  private long threadId;

  private List<String> combinedEdgesList;

  public MyCallable(Vertex[] vertices, final String[] combinedEdges, final int i, final int threads){
    this.vertices = vertices;
    this.combinedEdges = combinedEdges;
    this.timesEdgesUsed = new int[combinedEdges.length];
    this.i = i;

    combinedEdgesList = Arrays.asList(combinedEdges);

    Arrays.fill(timesEdgesUsed, 0);
  }


  @Override
  public int[] call() throws Exception {
    this.threadId = i;
    //System.out.println("ThreadID: "+Thread.currentThread().getId()+" | VertexID: "+i);

    computePaths(vertices[i]);

    List<String> usedEdges;
    int index;

    for (Vertex v : vertices) {
      usedEdges = getShortestPathTo(v);

      for (String string : usedEdges) {
        index = combinedEdgesList.indexOf(string);
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

    Comparator<Vertex> comparator = new Comparator<Vertex>(){
      @Override
      public int compare(Vertex v, Vertex u) {
        return Integer.compare(v.getMinDistance(threadId), u.getMinDistance(threadId));
      }
    };


    PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>(vertices.length, comparator);
    vertexQueue.add(source);

    while (!vertexQueue.isEmpty()) {
      Vertex u = vertexQueue.poll();

      // Jede Kante besuchen, die u verlaesst
      for (Edge e : u.adjacencies) {
        Vertex v = e.target;

        int distanceThroughU = u.getMinDistance(threadId)+1;

        if (distanceThroughU < v.getMinDistance(threadId)) {
          vertexQueue.remove(v);

          v.setMinDistance(threadId, distanceThroughU);
          v.setPrevious(threadId, u);
          vertexQueue.add(v);
        }
      }
    }
  }


  private  List<String> getShortestPathTo(Vertex target) {
    List<Vertex> path = new ArrayList<Vertex>();
    List<String> usedEdges = new ArrayList<String>();

    for (Vertex vertex = target; vertex != null; vertex = vertex.getPrevious(threadId)) {
      path.add(vertex);

      if(vertex.getPrevious(threadId) != null) {
        vertex.timeswalkedover.addAndGet(1);
        usedEdges.add(String.valueOf(vertex.getPrevious(threadId).id)+String.valueOf(vertex.id));
      }
    }

    Collections.reverse(path);

    return usedEdges;
  }
}
