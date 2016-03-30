
package graph.algorithms.centralities.betweenness;
/*
Retrieved from: http://en.literateprograms.org/Dijkstra's_algorithm_(Java)?oldid=15444
*/

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class Vertex implements Comparable<Vertex> 
{
    public final int id;
    //public int besucheCounter = 0;
    public AtomicInteger besucheCounter = new AtomicInteger(0);
    public ArrayList<Edge> adjacencies;
    public int minDistance = Integer.MAX_VALUE;
    public Vertex previous;
    public Vertex(int number, int threads){
        id = number; adjacencies = new ArrayList<Edge>();
       
   } 
    
    public String toString() { return Integer.toString(id); }
    public int compareTo(Vertex other)
    {
        return Integer.compare(minDistance, other.minDistance);
    }

}

class Tuple<X, Y> { 
  public final X x; 
  public final Y y; 
  public Tuple(X x, Y y) { 
    this.x = x; 
    this.y = y; 
  } 
} 

class Edge
{
    public final Vertex source;
    public final Vertex target;
    public int besucheCounter = 0;
    public Edge(Vertex argTarget, Vertex argSource)
    { target = argTarget;
      source = argSource;}
}

public class Dijkstra
{
   

    
    
    public static void main(String[] args) throws InterruptedException, ExecutionException
    {
        
        
//        int nodei[] = {0,1,1,2,2,0,0,3};//Anfangsarrays
//        int nodej[] = {1,0,2,1,0,2,3,0};
    	int nodei[] = {0,1,0,2,1,2,2,3,3,4,4,5,4,6};//Anfangsarrays
    	int nodej[] = {1,0,2,0,2,1,3,2,4,3,5,4,6,4};
    	
    	
    	
        int threads = 1;
       
        
        String[] combinedEdges = new String[nodei.length];//Kombiniert
        int timesedgesUsed[] = new int[nodei.length];//Counter-Array
        int addedtimesedgesUsed[] = new int[nodei.length];
        Arrays.fill(timesedgesUsed, 0);

        
        Map<Integer,Vertex> myMap = new HashMap<>();
        Vertex v1 = null;
        Vertex v2 = null;
        
        //Aufbau des Netzes
        for (int y = 0; y < nodei.length; y++)
	{
            combinedEdges[y] = (String.valueOf(nodei[y])+String.valueOf(nodej[y]));
            
            
            v1 = myMap.get(nodei[y]);
            v2 = myMap.get(nodej[y]);
            if(v1 == null){
               myMap.put(nodei[y],new Vertex(nodei[y],threads));
            }
            if(v2 == null){
              myMap.put(nodej[y],new Vertex(nodej[y],threads));
            }
            v1 = myMap.get(nodei[y]);
            v2 = myMap.get(nodej[y]);
            
            v1.adjacencies.add(new Edge(v2, v1));   
             }
        
            
    
        //Parallele Berechnung des kuerzesten Pfades
        Collection<Vertex> values = myMap.values();
        Vertex[] vertices =  myMap.values().toArray(new Vertex[values.size()]); 
	ExecutorService executorService = Executors.newFixedThreadPool(threads);
	Set<Callable<int[]>> callables = new HashSet<>();
        int i;
        for (i = 0; i < vertices.length; i++) {
		callables.add(new MyCallable(vertices, combinedEdges, i,threads));
        }
        
        List<Future<int[]>> results = executorService.invokeAll(callables);
        //Addieren der Ergebnisse
    	for(Future<int[]> future : results){
            int[] futureArray = (int[])future.get();
            for (int j = 0; j < addedtimesedgesUsed.length; j++) {
             //   System.out.print(" "+futureArray[j]);
                addedtimesedgesUsed[j]  = addedtimesedgesUsed[j] +  futureArray[j];
            }
           // System.out.println();
            
                                 
        }
        executorService.shutdown();
        
        
        System.out.println(Arrays.toString(addedtimesedgesUsed));
        
        for(Vertex v : vertices)
        	System.out.println("node:"+v.id+"  besucht: "+v.besucheCounter.get());
        
        
//        System.out.println("Knoten "+vertices[0].id+ " wurde "+ vertices[0].besucheCounter.get()+" mal besucht.");
//        System.out.println("Knoten "+vertices[1].id+ " wurde "+ vertices[1].besucheCounter.get()+" mal besucht.");
//        System.out.println("Knoten "+vertices[2].id+ " wurde "+ vertices[2].besucheCounter.get()+" mal besucht.");
//        System.out.println("Knoten "+vertices[3].id+ " wurde "+ vertices[3].besucheCounter.get()+" mal besucht.");
//        
//        System.out.println(nodei[0]+" "+nodei[1]+" "+nodei[2]+" "+nodei[3]+" "+nodei[4]+" "+nodei[5]+" "+nodei[6]+" "+nodei[7]);
//        System.out.println(nodej[0]+" "+nodej[1]+" "+nodej[2]+" "+nodej[3]+" "+nodej[4]+" "+nodej[5]+" "+nodej[6]+" "+nodej[7]);
//        System.out.println(addedtimesedgesUsed[0]+" "+addedtimesedgesUsed[1]+" "+addedtimesedgesUsed[2]+" "+addedtimesedgesUsed[3]
//                +" "+addedtimesedgesUsed[4]+" "+addedtimesedgesUsed[5]+" "+addedtimesedgesUsed[6]+" " +addedtimesedgesUsed[7]
//                + " <---- So oft wurde entsprechende Kante genutzt");
        
   }
    
}
