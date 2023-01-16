package graph.algorithms;

import java.util.ArrayList;

/**
 * 
 * @author mlewinsk
 *
 *         Combination of two clustering datatypes, the experimental data in the
 *         biological networks and the shortest paths distance
 *
 */

public class ShortestPathsExperimentClustering {

	/* Shortest paths distance matrix */
	private short[][] spdistance;
	/* array with experimental data (index based on entry in adjacency array) */
	private double[] experimententries;
	/* cluster structure */
	private ArrayList<ArrayList<Short>> clusters;
	private ArrayList<ArrayList<Short>> newclusters;
	
	/* Graph structure container */
	private NetworkProperties np;

	private short i, iterations;
	private String experimentname;

	public ShortestPathsExperimentClustering(String experimentname) {
		this.experimentname = experimentname;
		clusters = new ArrayList<>();

		

		setupPathsDistance();
		setupExperimentArray();
		initializeClusters();
		
		//DEBUG
//		System.out.println(clusters);
		

		iterations = 2;
		for(i = 0; i<iterations; i++){
			long starttime = System.currentTimeMillis();
			nextIteration();
			System.out.printf("iteration %d : %dms\n",i,System.currentTimeMillis()-starttime);
		}

	}


	private void setupPathsDistance() {
		np = new NetworkProperties();
		spdistance = np.AllPairShortestPaths(false);
		
//		for (int i = 0; i < spdistance.length; i++) {
//			for (int j = 0; j < spdistance.length; j++) {
//				System.out.print(spdistance[i][j]);
//			}
//			System.out.println();
//		}
	}

	private void setupExperimentArray() {

		experimententries = new double[np.nodes];
		double currentvalue = 0.0d;

		for (i = 0; i < experimententries.length; i++) {
			// determine the node's experimental value and save it separately
			// into the array
			// MARTIN: behavior of non existent experimental data?
			try {
				currentvalue = np.getNodeAssignmentbackwards(i)
						.getNodeAttributeByName(experimentname)
						.getDoublevalue();

			} catch (NullPointerException e) {
				System.out.printf("node %d has no entry \'%s\' (%s) \n", i,
						experimentname, np.getNodeAssignmentbackwards(i)
								.getLabel());
				currentvalue = 0.00001337d;
			}

			experimententries[i] = currentvalue;
		}

		// DEBUG
		System.out.println(experimentname + ":");
		for (double d : experimententries) {
			System.out.println(d);
		}

	}

	private void initializeClusters() {
		//every node is a cluster
		clusters = new ArrayList<ArrayList<Short>>(np.nodes);
		ArrayList<Short> tmplist;
		
		for(i = 0; i<np.nodes; i++){
			tmplist = new ArrayList<>();
			tmplist.add(i);
			clusters.add(tmplist);
		}
	}
	

	private void nextIteration() {
		int outer = 0;
		short inner = 0;
		ArrayList<Short> cluster;
		ArrayList<Short> newcluster;
		newclusters = new ArrayList<ArrayList<Short>>();
		ArrayList<ArrayList<Short>> removelist = new ArrayList<>();
		
		//iterate over clusters
		for(outer = 0; outer < clusters.size(); outer++){
			cluster =  clusters.get(outer);
			//iterate over cluster
			for(inner = 0; inner <cluster.size(); inner++){
				//determine neighbors
				//iterate over neighbors of current node
				for(short nb : getNeighborIndexes(cluster.get(inner))){
					//create new cluster plus this neighbor
					//add new cluster to 'newclusters'
					if(cluster.contains(nb))
						continue;
					newcluster = new ArrayList<Short>();
					newcluster.add(nb);
					newcluster.addAll(cluster);
					newclusters.add(newcluster);
				}					
			}			
		}
		
		//remove duplicates from newclusters
		for(int i = 0; i<newclusters.size(); i++){
			for(int j = i+1; j<newclusters.size(); j++){
				if(newclusters.get(i).containsAll(newclusters.get(j)))
					removelist.add(newclusters.get(j));				
			}
		}
		
		newclusters.removeAll(removelist);
		
		//overwrite clusters with new clusters
		clusters = newclusters;
		
		//DEBUG
//		System.out.println(newclusters);
		
		//ITERATION END!
		
		
		
		
	}


	private ArrayList<Short> getNeighborIndexes(short nodeindex) {
		ArrayList<Short> returnlist = new ArrayList<>();
	
		for (short i = 0; i < spdistance.length; i++) {
			if(spdistance[nodeindex][i] == 1)
				returnlist.add(i);
		}		
		return returnlist;
	}

}
