package graph.algorithms.centralities.betweenness;

public class Edge {
  public final Vertex source;
  public final Vertex target;

  public Edge(Vertex argTarget, Vertex argSource) {
    target = argTarget;
    source = argSource;
  }
}
