package graph.algorithms.centralities.betweenness;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Vertex {
  public final int id;
  public AtomicInteger timeswalkedover = new AtomicInteger(0);//visit counter
  public ArrayList<Edge> adjacencies;
  private ConcurrentHashMap<Long, Integer> minDistances;
  private ConcurrentHashMap<Long, Vertex> previouses;


  public Vertex(int number, int threads) {
    id = number;
    adjacencies = new ArrayList<Edge>();

    minDistances = new ConcurrentHashMap<Long, Integer>();
    previouses = new ConcurrentHashMap<Long, Vertex>();
  }


  public int getMinDistance(long threadId) {
    Integer ret = minDistances.get(threadId);

    if(ret == null)
      return Integer.MAX_VALUE;

    else
      return ret;
  }


  public Vertex getPrevious(long threadId) {
    return previouses.get(threadId);
  }


  public void setPrevious(long threadId, Vertex previous) {
    if(previous == null){
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
