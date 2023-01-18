package graph.algorithms.gui.smacof.algorithms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import graph.GraphContainer;
import graph.algorithms.NodeAttributeNames;
import graph.algorithms.NodeAttributeTypes;
import graph.jung.classes.MyGraph;
import gui.MainWindow;

/**
 * 
 * The SMACOF Algorithms allows for pairwise weightings of points. This class will calculate and normalize pairwise weightings for a given attribute.
 * 
 * @author mlewinsk
 *
 */
public class Weighting {

	private float[][] weights;
	private HashMap<BiologicalNodeAbstract, Integer> mapped_nodes;
	private HashMap<Integer, BiologicalNodeAbstract> mapped_nodes_backwards;
	
	
	public Weighting(HashMap<BiologicalNodeAbstract, Integer> mapped_nodes, HashMap<Integer, BiologicalNodeAbstract> mapped_nodes_backwards){
		this.mapped_nodes = mapped_nodes;
		this.mapped_nodes_backwards = mapped_nodes_backwards;
		
		
		//check kind of weighting
		
		//get attributes for each node
		
		//compute weighting
		
		//normalize (optional)
		
		
		
	}
	
	/**
	 * 
	 * @return float[][] - adjacency matrix
	 */
	public float[][] getWeightsByAdjacency(){
		weights = new float[mapped_nodes.size()][mapped_nodes.size()];
		MainWindow w = MainWindow.getInstance();
		GraphContainer con = GraphContainer.getInstance();
		MyGraph mg = con.getPathway(w.getCurrentPathway()).getGraph();
		
		// for non non zero entries
		for (int i = 0; i < weights.length; i++) {
			Arrays.fill(weights[i], 1.0f);
		}
		for (int i = 0; i < weights.length; i++) {
			weights[i][i] = 0.0f;
		}
		
		
		int fromid, toid;
		BiologicalNodeAbstract from, to;
		Iterator<BiologicalEdgeAbstract> it = mg.getAllEdges().iterator();
		while (it.hasNext()) {

			// get Connected Nodes
			BiologicalEdgeAbstract bne = (BiologicalEdgeAbstract) it.next();
			from = ((BiologicalNodeAbstract) bne.getFrom());
			to = ((BiologicalNodeAbstract) bne.getTo());

			fromid = mapped_nodes.get(from);
			toid = mapped_nodes.get(to);

			// EDGES for analysis undirected
			weights[fromid][toid] = 1;
			weights[toid][fromid] = 1;
		}

//		//Test correct weights by label
//		BiologicalNodeAbstract bna_a = mapped_nodes_backwards.get(0), bna_b;
//		for(int i =0; i<weights[0].length; i++){
//			if(weights[0][i] == 1.0f){
//				bna_b = mapped_nodes_backwards.get(i);
//				System.out.printf("%s-%s(%d)\n",bna_a.getLabel(),bna_b.getLabel(),i);
//			}
//		}
			
		
//		for(float[] row : weights){
//			System.out.println(Arrays.toString(row));
//		}	
		
		return weights;		
	}
	
	public float[][] getWeightsByCellularComponent(){
		weights = new float[mapped_nodes.size()][mapped_nodes.size()];
		int nodes = mapped_nodes.size();

		HashMap<String, Integer> locales = new HashMap<>();
		BiologicalNodeAbstract bna;
		int locindex = 0;

		// estimate assignment of loc -> int [0..locales-1]
		for (int i = 0; i < nodes; i++) {
			bna = mapped_nodes_backwards.get(i);
			for (NodeAttribute att : bna.getNodeAttributes()) {
				if (att.getName().equals(
						NodeAttributeNames.GO_CELLULAR_COMPONENT)) {
					if (!locales.containsKey(att.getStringvalue())) {
						locales.put(att.getStringvalue(), locindex);
						locindex++;
					}
				}

			}
		}

		System.out.printf("found %d locales.\n", locales.size());
		//build vector matrix for localization pseudo variables
		int subindex;
		float[][] X = new float[nodes][locales.size()]; //
		for (int i = 0; i < nodes; i++)
			java.util.Arrays.fill(X[i], 0.0f);

		for (int i = 0; i < X.length; i++) {
			bna = mapped_nodes_backwards.get(i);
			for (NodeAttribute na : bna
					.getNodeAttributesByType(NodeAttributeTypes.ANNOTATION)) {
				if (na.getName().equals(
						NodeAttributeNames.GO_CELLULAR_COMPONENT)) {
					subindex = locales.get(na.getStringvalue());
					X[i][subindex] = 1.0f;
				}
			}

//			System.out.println(mapped_nodes_backwards.get(i).getLabel()
//					+ "\t\t" + Arrays.toString(X[i]));

		}
		
		
		//calc euclidean distance, bad if both vectors are big
/*		double sum = 0;
		int vsize = X[0].length;
		for (int a = 0; a < X.length; a++) {
			for (int b = a+1; b < X.length; b++) {
				sum = 0.0d;
				for (int i = 0; i < vsize; i++) {
					sum += Math.pow((X[a][i] - X[b][i]), 2);
				}
				weights[a][b] = (float)Math.pow(sum, 1.0 / 2.0d);
				weights[b][a] = weights[a][b];
			}
			System.out.println(mapped_nodes_backwards.get(a).getLabel() + "\t\t" + Arrays.toString(weights[a]));
		}
*/
		double max = 0;
		int indexmax1=0, indexmax2=0;
		
		double sum = 0;
		int vsize = X[0].length;
		for (int a = 0; a < X.length; a++) {
			for (int b = a+1; b < X.length; b++) {
				sum = 0.0d;
				for (int i = 0; i < vsize; i++) {
					if(X[a][i] == 1.0f && X[b][i] == 1.0f)
						sum += 1.0d;
				}
				weights[a][b] = (float)sum;
				weights[b][a] = weights[a][b];
				if(max<sum){
					max = sum;
					indexmax1 = a;
					indexmax2 = b;
				}
				
				
			}			
		}
		
//		//Normalize linear, had no effect
//		for (int a = 0; a < X.length; a++) {
//			for (int b = a+1; b < X.length; b++) {
//				weights[a][b] = weights[a][b]/(float)max;
//				weights[b][a] = weights[a][b];
//			}
////			System.out.println(mapped_nodes_backwards.get(a).getLabel() + "\t\t" + Arrays.toString(weights[a]));
//		}
		
		
		System.out.println(max +"\t"+mapped_nodes_backwards.get(indexmax1).getLabel()+"\t"+mapped_nodes_backwards.get(indexmax2).getLabel());
		System.out.println(Arrays.toString(weights[0]));
        
			
		return weights;	
		

	}
}
