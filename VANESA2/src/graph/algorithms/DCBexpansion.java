package graph.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DCBexpansion implements Callable<LinkedHashSet<HashSet<Integer>>>{
	
	//TEST
	//extended Clusters:
	public int extendedSize;
	//doppelte Cluster:
	public int doppelExtended;
	//#Seeds zu beginn
	public int numSeeds;
	
	private HashMap<Integer, HashSet<Integer>> adjacencies;
	private DCBTests test;
	private HashMap<HashSet<Integer>, HashSet<Integer>> seeds;

	//Summe der Nachbarn aller bisher enthaltener seeds
	//(Nächster Seed wird immer dem Callable zugeordnet der bisher die wenigsten 
	//Nachbarn (insgesamt) hat)
	private int numOfNeighbours;

	
	
	public DCBexpansion(HashMap<Integer, HashSet<Integer>> adjacenciesArray,
			double density, ArrayList<Double> ranges, int attrdim,
			HashMap<Integer, ArrayList<Double>> attributesArray) {
		this.adjacencies = adjacenciesArray;
		this.test = new DCBTests(adjacenciesArray, density, ranges, attrdim, attributesArray);	
		seeds = new HashMap<>();
		numOfNeighbours = 0;
		
		
	
	}



	/*
	 * Expansion: Seeds werden gemäß der Kriteiren (homogenity/density erweitert)
	 * @see java.util.concurrent.Callable#call()
	 */
	public LinkedHashSet<HashSet<Integer>> call(){
		
		numSeeds = seeds.size();
		
		System.out.println("num of seeds:");
		LinkedHashSet<HashSet<Integer>> extended = new LinkedHashSet<HashSet<Integer>>();
		while(!seeds.isEmpty()){
			Hashtable<HashSet<Integer>, HashSet<Integer>>  seedsHelp = new Hashtable<HashSet<Integer>, HashSet<Integer>>();
			seedsHelp.putAll(seeds);
			System.out.println(seeds.size());// + ": " + seeds);
			seeds.clear();
			for(HashSet<Integer> nodeSet : seedsHelp.keySet()){
				boolean finish = true;
				for(int connectedNode : seedsHelp.get(nodeSet)){
					HashSet<Integer> testSet = new HashSet<Integer>();
					testSet.addAll(nodeSet);
					testSet.add(connectedNode);				
					if(test.testDensity(testSet) && test.testHomogenity(testSet)){
						HashSet<Integer> tempNodeSet = new HashSet<Integer>();
						tempNodeSet.addAll(seedsHelp.get(nodeSet));
						for(int tempConnected : adjacencies.get(connectedNode)){
//							if(tempConnected > connectedNode){
							tempNodeSet.add(tempConnected);
//							}
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
		 * Entfernung doppelter Cluster: (geprüft wird auch ob ein Cluster ein anderes enthält)
		 */
		LinkedHashSet<HashSet<Integer>> removeSubsets = new LinkedHashSet<HashSet<Integer>>();
		
		for(HashSet<Integer> cluster : extended){
			for(HashSet<Integer> clusterHelp : extended){
				if(clusterHelp.size() > cluster.size() && clusterHelp.containsAll(cluster)){
					removeSubsets.add(cluster);
					break;
				}
			}
		}
		
		
		extendedSize = extended.size();
		doppelExtended = removeSubsets.size();
		
		extended.removeAll(removeSubsets);
		return extended;
	}
	
	//hinzufügen eines seeds
	public void putSeed(HashSet<Integer> seed, HashSet<Integer> neighbours){
		numOfNeighbours += neighbours.size();
		seeds.put(seed, neighbours);
	}
	
	
	public HashMap<HashSet<Integer>, HashSet<Integer>> getSeeds(){
		return seeds;
	}
	
	public int getNumOfNeighbours(){
		return numOfNeighbours;
	}

	

	


}
