package graph.algorithms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Protein;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;

public class DenselyConnectedBiclustering {
	
	
	MainWindow w = MainWindowSingelton.getInstance();
	GraphContainer con = ContainerSingelton.getInstance();
	InternalGraphRepresentation graphRepresentation = con.getPathway(w.getCurrentPathway()).getGraphRepresentation(); 
	Pathway pw = con.getPathway(w.getCurrentPathway());
	MyGraph mg = pw.getGraph();

	//Atttributes of vertices.
	HashMap<Integer, Double[]> attr  = new HashMap<>();
	
	//Adjacencie-List: Key: vertex-ID Values: list of connected vertex-IDs
	Hashtable<Integer, HashSet<Integer>> adjacencies = new Hashtable<Integer, HashSet<Integer>>();
	
	//List of IDs and corresponding vertices
	HashMap<Integer, BiologicalNodeAbstract> idBna = new HashMap<Integer, BiologicalNodeAbstract>();
	
	LinkedList<DCBresultSet> results;
	
	public LinkedList<DCBresultSet> getResults() {
		return results;
	}

	double[] ranges;
	int numDim;
	double density;
	//Test2:
//	double range = 0.0;
//	int numDim = 3;
//	double density = 0.7;
	//Test1:
//	double range = 0.5;
//	int numDim = 2;
//	double density = 0.8;
	
	// throws IllegalArgumentException, IOException
	public DenselyConnectedBiclustering(double density, double[] ranges, double attrdim){
		this.density = density;
		this.ranges = ranges;
		this.numDim = (int) attrdim;
		
//		FileReader fr = new FileReader(attributes);
//		BufferedReader br = new BufferedReader(fr);
//
//		try{
//		    while(br.ready()){
//		    	String line = br.readLine();
//		    	String[] column = line.split("\t");
//		    	Integer id = Integer.parseInt(column[0]);
//		    	Double[] values = new Double[column.length -1];
//		    	if(values.length != ranges.length){
//		    		br.close();
//		    		throw new IllegalArgumentException();
//		    	}
//		    	for(int i = 0; i < values.length; i++){
//		    		values[i] = Double.parseDouble(column[i+1]);
//		    	}
//		    	attr.put(id, values);	
//		    }
//	    
//		}catch (IOException e){
//			throw e;
//		}finally{
//			br.close();
//		}

		dcb();

	}
	

	/*
	 * Generates Attribute-Matrix and call methods preprocessingGraph and expansion.
	 */
	public void dcb(){
		
		//Test 2:
//		attr.put(100, new Double[] {2.0, -2.0, 1.0, 0.0, 2.0, 1.0, 2.0});
//		attr.put(101, new Double[] {1.0, 1.0, 0.0, -2.0, 0.0, 2.0, 1.0});
//		attr.put(102, new Double[] {1.0, 0.0, 0.0, 1.0, 0.0, 2.0, 1.0});
//		attr.put(103, new Double[] {0.0, 0.0, -1.0, -1.0, 2.0, 1.0, 0.0});
//		attr.put(104, new Double[] {1.0, 1.0, 2.0, 0.0, 0.0, 2.0, 2.0});
//		attr.put(105, new Double[] {1.0, 1.0, -1.0, -1.0, 0.0, 2.0, 0.0});
//		attr.put(106, new Double[] {0.0, 2.0, -1.0, -1.0, 2.0, 0.0, 0.0});
//		attr.put(107, new Double[] {1.0, 1.0, -1.0, -1.0, 1.0, 1.0, 0.0});
//		attr.put(108, new Double[] {1.0, 0.0, 2.0, 0.0, 0.0, 2.0, 1.0});
//		attr.put(109, new Double[] {2.0, 2.0, -1.0, -1.0, -1.0, -1.0, 0.0});
//		attr.put(110, new Double[] {2.0, 0.0, 1.0, 1.0, 1.0, -2.0, -1.0});
		
		//Test 1:
//		attr.put(100, new Double[] {0.5, 0.0, -1.0});
//		attr.put(101, new Double[] {1.0, 1.0, -1.0});
//		attr.put(105, new Double[] {1.0, 0.5, -1.0});
//		attr.put(104, new Double[] {1.0, 1.0, -0.5});
//		attr.put(106, new Double[] {0.5, 0.0, 0.0});
//		attr.put(107, new Double[] {-1.0, 0.0, 2.0});
		
		for(BiologicalNodeAbstract vertex1 : mg.getAllVertices()){
			HashSet<Integer> neigbours = new HashSet<Integer>();
			idBna.put(vertex1.getID(), vertex1);
			
			for(BiologicalNodeAbstract vertex2 : mg.getAllVertices()){
				
				
					
				if(graphRepresentation.doesEdgeExist(vertex1, vertex2) || graphRepresentation.doesEdgeExist(vertex2, vertex1)){
					
					neigbours.add(vertex2.getID());
					
				}
			}
			this.adjacencies.put(vertex1.getID(), neigbours);

		}
		
		setAttributes();
	    
		preprocessingGraph();
		
		expansion();

	}
	
	
	
	private void setAttributes() {
		int numOfAttributes = ranges.length;
		for(int id : adjacencies.keySet()){
			
			Double[] values = new Double[numOfAttributes];
			values[0] = (double) adjacencies.get(id).size();
			
			Double[] factor = {1.0, 10.0, 200.0, 30.0, 50.0, 0.5};
			
			if(numOfAttributes  > 1){
				BiologicalNodeAbstract vertex = idBna.get(id);

                if(vertex instanceof Protein){
                    Protein p = (Protein) vertex;
                    values[1] = (double) p.getAaSequence().length();  
                }else{
                	values[1] = 0.0;
                }
                
                for(int i = 2; i < numOfAttributes; i++){
                	values[i] = Math.random()*factor[i%6];
                }
			}
			
			attr.put(id, values);
		}
		
	}


	/*
	 * Test if each vertex has at least one edge which satisfies homogeneity. 
	 * If not: remove form adjacencies.
	 */
	public void preprocessingGraph(){
		HashSet<Integer> removalAdjacencies = new HashSet<Integer>();
		for(int vertex1 : adjacencies.keySet()){

			boolean isHomogen = false;

			for(int vertex2 : adjacencies.get(vertex1)){
					
					HashSet<Integer> testVertices = new HashSet<Integer>();
				
					testVertices.add(vertex1);
					testVertices.add(vertex2);
					if(testHomogenity(testVertices)){
						isHomogen = true;
					}
					
			}
			
			
			if(!isHomogen){
				removalAdjacencies.add(vertex1);
			}
		
		}
		
		removeFormAdjacencies(removalAdjacencies);

	}
	
	/*
	 * Removes each vertex of removalAdjacencies form neighbour-list of adjacencies
	 * and in the end the vertex itself.
	 */
	private void removeFormAdjacencies(HashSet<Integer> removalAdjacencies) {
		for(int vertex : removalAdjacencies){
			for(int neigbour : adjacencies.get(vertex)){
				adjacencies.get(neigbour).remove(vertex);
			}
			
			adjacencies.remove(vertex);
			
		}
		
	}

	/*
	 * Finds the maximal graph which satisfies density and homogenity.
	 */
	private void expansion() {
		
		/*
		 * generate seeds: First HashSet = connected subgraph (with tow elements); 
		 * second HashSet = neighbour vertices of this subgraph
		 * neighbours are only added if the id of vertex is bigger than the tow of the 
		 * connected subgraph.
		 */
		Hashtable<HashSet<Integer>, HashSet<Integer>> seeds = new Hashtable<HashSet<Integer>, HashSet<Integer>>();
		
		for(int vertex : adjacencies.keySet()){
			for(int neighbour : adjacencies.get(vertex)){
				if(vertex < neighbour){
					HashSet<Integer> testSet = new HashSet<Integer>();
					testSet.add(vertex);
					testSet.add(neighbour);
					if(testDensity(testSet) && testHomogenity(testSet)){
						HashSet<Integer> tempSeed = new HashSet<Integer>();
						tempSeed.add(vertex);
						tempSeed.add(neighbour);
						HashSet<Integer> tempNeighbours = new HashSet<Integer>();
						double maxID = Math.max(vertex, neighbour);
		
						for(int connectedNode : adjacencies.get(vertex)){
							if(connectedNode > maxID){
								tempNeighbours.add(connectedNode);
							}
						}
						for(int connectedNode : adjacencies.get(neighbour)){
							if(connectedNode > maxID){
								tempNeighbours.add(connectedNode);
		
							}
						}
						
						tempNeighbours.remove(vertex);
						tempNeighbours.remove(neighbour);
						seeds.put(tempSeed, tempNeighbours);
					}
				}
			}
		
		}
		
		
		/*
		 * Iterates over the seeds-List and adds a connected vertex to the seed if density and homogenity
		 * are satisfied. If the test is not satisfied the last satisfying seed is added to the results
		 * (extended).
		 */
		LinkedHashSet<HashSet<Integer>> extended = new LinkedHashSet<HashSet<Integer>>();
		while(!seeds.isEmpty()){
			Hashtable<HashSet<Integer>, HashSet<Integer>>  seedsHelp = new Hashtable<HashSet<Integer>, HashSet<Integer>>();
			seedsHelp.putAll(seeds);
			seeds.clear();
			for(HashSet<Integer> nodeSet : seedsHelp.keySet()){
				boolean finish = true;
				for(int connectedNode : seedsHelp.get(nodeSet)){
					HashSet<Integer> testSet = new HashSet<Integer>();
					testSet.addAll(nodeSet);
					testSet.add(connectedNode);

					if(testDensity(testSet) && testHomogenity(testSet)){
						HashSet<Integer> tempNodeSet = new HashSet<Integer>();
						tempNodeSet.addAll(seedsHelp.get(nodeSet));
						for(int tempConnected : adjacencies.get(connectedNode)){
							tempNodeSet.add(tempConnected);
						}
						tempNodeSet.removeAll(testSet);
						seeds.put(testSet, tempNodeSet);
						finish = false;

					}

				}
				if(finish){
					extended.add(nodeSet);
				}
				
			}
		}
		
		/*
		 * Remove duplicate clusters
		 */
		LinkedHashSet<HashSet<Integer>> removeSubsets = new LinkedHashSet<HashSet<Integer>>();
		
		for(HashSet<Integer> cluster : extended){
			boolean subset = false;
			for(HashSet<Integer> clusterHelp : extended){
				if(clusterHelp.size() > cluster.size() && clusterHelp.containsAll(cluster)){
					subset = true;
				}
			}
			if(subset){
				removeSubsets.add(cluster);
			}
		}
		
		extended.removeAll(removeSubsets);
		
		
		results = new LinkedList<DCBresultSet>();

		for(HashSet<Integer> cluster : extended){
			int numOfVertices = cluster.size();
			double densityScore  = densityScore(cluster);
			int numOfhomogenAttributes = homogenityScore(cluster);
			HashSet<BiologicalNodeAbstract> vertices = new HashSet<BiologicalNodeAbstract>();
			String labels = new String();
			
			for(Integer id : cluster){
				BiologicalNodeAbstract bna = idBna.get(id);
				labels += bna.getLabel() + " ";
				vertices.add(bna);
			}
			results.add(new DCBresultSet(numOfVertices, densityScore, numOfhomogenAttributes, 
					labels, vertices));
		}
		

		Collections.sort( results, new DCBresultSet() );

//		Collections.reverse(results);
	}

	/*
	 * Iterates about List of attributes, finds the biggest and smallest Value for each 
	 * vertex of vertices and computes the distance. If distance is smaller or equal  
	 * to range score increase by one. 
	 */
	private int homogenityScore(HashSet<Integer> vertices) {
		Iterator<Integer> it = vertices.iterator();
		int firstvertexID = it.next();
		int numAttr = ranges.length;
		int numOfSameDim = 0;

		for(int i = 0; i < numAttr; i++){
			double min = attr.get(firstvertexID)[i];
			double max = attr.get(firstvertexID)[i];
			
			for(int vertex : vertices){
				if(attr.get(vertex)[i] < min){
					min = attr.get(vertex)[i];
				}
				if(attr.get(vertex)[i] > max){
					max = attr.get(vertex)[i];
				}
			}
			if(max-min <= ranges[i]){
				numOfSameDim++;
			}
		}
		return numOfSameDim;
	}

	/*
	 * Density = # existing edges/ # max. possible edges
	 */
	private double densityScore(HashSet<Integer> testVertices) {
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
	
	/*
	 * Iterates about List of attributes, finds the biggest and smallest Value for each 
	 * vertex of vertices and computes the distance. If distance is smaller or equal  
	 * to range for at least numDim dimensions the vertices are homogene. 
	 */
	public boolean testHomogenity(HashSet<Integer> vertices){
		Iterator<Integer> it = vertices.iterator();
		int firstvertexID = it.next();
		int numAttr = ranges.length;
		int numOfSameDim = 0;

		for(int i = 0; i < numAttr; i++){
			double min = attr.get(firstvertexID)[i];
			double max = attr.get(firstvertexID)[i];
			
			for(int vertex : vertices){
				if(attr.get(vertex)[i] < min){
					min = attr.get(vertex)[i];
				}
				if(attr.get(vertex)[i] > max){
					max = attr.get(vertex)[i];
				}
			}
			if(max-min <= ranges[i]){
				numOfSameDim++;
				if(numOfSameDim == numDim){
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Density = # existing edges/ # max. possible edges
	 * true if density of subgraph is bigger or equal to threshold (density)
	 * Each edge is counted twiche! (forward and backward)
	 */
	private boolean testDensity(HashSet<Integer> testVertices) {
		double edgecounter = 0;
		double maxedges = testVertices.size()*(testVertices.size()-1);
		for(int vertex1 : testVertices){
			for(int vertex2 : testVertices){
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
}
