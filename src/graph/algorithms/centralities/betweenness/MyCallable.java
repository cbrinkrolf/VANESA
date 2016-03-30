
package graph.algorithms.centralities.betweenness;


import java.util.*;
import java.util.concurrent.Callable;



public class MyCallable implements Callable<int[]>
    {
    
    Vertex[] vertices = null;
    String[] combinedEdges = null;
    int[] timesedgesUsed = null;
    int i;
    private final int threadnummer;
    
    public MyCallable( Vertex[] vertices, final String[] combinedEdges, final int i, final int threads){
        this.threadnummer = i % threads;
        this.vertices = vertices;
        this.combinedEdges = combinedEdges;
        this.timesedgesUsed = new int[combinedEdges.length];
        this.i = i;
        
    }

    @Override
    public int[] call() throws Exception {
        computePaths(vertices[i]);
        List<String> usededges;
        int index;
        for (Vertex v : vertices)
	{
	    //System.out.println("Distance from "+ vertices[i]  +" to " + v + ": " + v.minDistance);
	    //List<Vertex> path = getShortestPathTo(v);
            usededges = getShortestPathTo(v);
            for (String string : usededges) {
            index = Arrays.asList(combinedEdges).indexOf(string);
            timesedgesUsed[index]++;
            
            }
	    //System.out.println("Path: " + path);
  
        }
        for (Vertex v : vertices)
	{  v.previous = null;
           v.minDistance=Integer.MAX_VALUE;}
        // System.out.println(timesedgesUsed[0]+" "+timesedgesUsed[1]+" "+timesedgesUsed[2]+" "+timesedgesUsed[3]
        //        +" "+timesedgesUsed[4]+" "+timesedgesUsed[5]+" " +timesedgesUsed[6]+" " +timesedgesUsed[7]+ " <---- So oft wurde Kante genutzt von " + i);
        return timesedgesUsed;
        
    }
    
    
    
    
    
    private void computePaths(Vertex source)
    {
        source.minDistance = 0;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
	vertexQueue.add(source);
	while (!vertexQueue.isEmpty()) {
	    Vertex u = vertexQueue.poll();
            // Visit each edge exiting u
            for (Edge e : u.adjacencies)
            {
                Vertex v = e.target;
                int distanceThroughU = u.minDistance+1;
		if (distanceThroughU < v.minDistance) {
		    vertexQueue.remove(v);

		    v.minDistance = distanceThroughU;
		    v.previous=u;
		    vertexQueue.add(v);
		}}
        }
    }
    
    
    
        private  List<String> getShortestPathTo(Vertex target)
    {
        List<Vertex> path = new ArrayList<Vertex>();
        List<String> usededges = new ArrayList<String>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
        {path.add(vertex);
        //System.out.println("Knoten:" +vertex.id+" zum "+vertex.besucheCounter + ". Mal besucht.");
        if(vertex.previous != null){
            vertex.besucheCounter.addAndGet(1);
            usededges.add(String.valueOf(vertex.previous.id)+String.valueOf(vertex.id));}
        }

        
        Collections.reverse(path);
       // System.out.println(path +" errechneter Pfad.");
        return usededges;
    }
    
}