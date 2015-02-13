package graph.algorithms;

import edu.emory.mathcs.backport.java.util.Arrays;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.algorithms.gui.DenselyConnectedBiclusteringGUI;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;

public class DenselyConnectedBiclustering {
	
	
	private MainWindow w = MainWindowSingleton.getInstance();
	private GraphContainer con = ContainerSingelton.getInstance();
	private InternalGraphRepresentation graphRepresentation = con.getPathway(w.getCurrentPathway()).getGraphRepresentation(); 
	private Pathway pw = con.getPathway(w.getCurrentPathway());
	private MyGraph mg = pw.getGraph();
	private NetworkProperties np;
	
//	public static ProgressBar progressBar;

	//Minimale Gr��e des Graphen f�r die parallele Verarbeitung
	private final int MIN_PARALLEL_SIZE = 0;
	
	private int numOfThreads;
	
	//Atttribute der Knoten.
//	private HashMap<Integer, Double[]> attributes  = new HashMap<>();
//	private ConcurrentHashMap<Integer, ArrayList<Double>> attributes;
	
	private ArrayList<String> attrTyps;
	private ArrayList<String> attrNames;
	
	private HashMap<BiologicalNodeAbstract, Double> cyclesMap;
	private HashMap<BiologicalNodeAbstract, Double> cliquesMap;
	
	
	
	//Adjazenliste: Key: Knoten-ID Values: Liste der verbundenen Knoten
	private HashMap<Integer, HashSet<Integer>>[] adjacenciesArray;
	
	private HashMap<Integer, ArrayList<Double>>[] attributesArray;
	
	
	//Liste von IDs und den zugeh�rigen Knoten-Objekten
	private HashMap<Integer, BiologicalNodeAbstract> idBna = new HashMap<>();
	
	private int nodeType;

	
	private DCBTests test;


	/*
	 * Eingabedaten des Benutzers:
	 */
	//Max. Distanz der Attribute
	private final ArrayList<Double>[] rangesArray;
	//# der Attributs-Dimensionen dei �bereinstimmen m�ssen
	private int attrdim;
	//Min. Dichte der Cluster
	private double density;
	//Test2:
//	double range = 0.0;
//	int numDim = 3;
//	double density = 0.7;
	//Test1:
//	double[] ranges = {0.5, 0.5, 0.5};
//	int numDim = 2;
//	double density = 0.8;
	
//	double[] ranges = {0.5, 0.5, 0.5};
//	int numDim = 2;
//	double density = 0.8;
	
	//TODO entfernen (nur zum testen)
	private long preprocessingTime;
	private long seedGeneration1Time;

	// Constructor: Benutzereingaben werden gesetzt. Aufruf der dcb-Methode
	public DenselyConnectedBiclustering(double density, ArrayList<Double> ranges2, int nodeType, 
			ArrayList<String> attrTyps, ArrayList<String> attrNames, double attrdim, 
			HashMap<BiologicalNodeAbstract, Double> cyclesMap, HashMap<BiologicalNodeAbstract, Double> cliquesMap){
		this.density = density;
//		this.ranges = ranges2;
		this.attrTyps = attrTyps;
		this.attrNames = attrNames;
		this.attrdim = (int) attrdim;
		//TODO numOfThreads
		this.numOfThreads = 1;
		this.nodeType = nodeType;
		this.cyclesMap = cyclesMap;
		this.cliquesMap = cliquesMap;
		attributesArray = new HashMap[numOfThreads];
		adjacenciesArray = new HashMap[numOfThreads];
		adjacenciesArray[0] = new HashMap<Integer, HashSet<Integer>>();
		
		rangesArray = new ArrayList[numOfThreads];
		for(int i = 0; i < rangesArray.length; i++){
			rangesArray[i] = new ArrayList<Double>();
			for(Double value : ranges2){
				rangesArray[i].add(value);
			}
		}
		

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



	}
	

	/*
	 * Erstellung der Attribut-Matrix und Adjazenliste sowie Aufruf des Algorithmus
	 * �bersteigt der Graph eine bestimmte Gr��e wird der parallele Algorithmus verwendet
	 */
	public LinkedList<DCBresultSet> start(){
		
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
		
//		//Test 1:
//		attr.put(100, new Double[] {0.5, 0.0, -1.0});
//		attr.put(101, new Double[] {1.0, 1.0, -1.0});
//		attr.put(102, new Double[] {1.0, 0.5, -1.0});
//		attr.put(103, new Double[] {1.0, 1.0, -0.5});
//		attr.put(104, new Double[] {0.5, 0.0, 0.0});
//		attr.put(105, new Double[] {-1.0, 0.0, 2.0});
		
		//do not take vertices which have not the needed attributes
		HashSet<BiologicalNodeAbstract> allVertices = new HashSet<BiologicalNodeAbstract>();
		
		boolean experimentsIsSet = false;
		HashSet<String> experiments = null;
		BiologicalNodeAbstract graphNode;
		HashSet<NodeAttribute> nodeatts;
		

		
		if(nodeType == DenselyConnectedBiclusteringGUI.TYPE_GRAPHNODE_NR){
			for(BiologicalNodeAbstract vertex : mg.getAllVertices()){
					if(!experimentsIsSet){
						experiments = setExperiments();
						experimentsIsSet = true;
					}
					graphNode = vertex;
					
					nodeatts = graphNode.getNodeAttributesByType(NodeAttributeTypes.EXPERIMENT);
					ArrayList<String> biodata = new ArrayList<>();
					if(biodata.containsAll(experiments)){
						allVertices.add(vertex);
					}
			}
		}else if(nodeType == DenselyConnectedBiclusteringGUI.TYPE_DNA_NR){
			for(BiologicalNodeAbstract vertex : mg.getAllVertices()){
				if(vertex instanceof DNA){
					allVertices.add(vertex);
				}
			}
		}else if(nodeType == DenselyConnectedBiclusteringGUI.TYPE_RNA_NR){
			for(BiologicalNodeAbstract vertex : mg.getAllVertices()){
				if(vertex instanceof RNA){
					allVertices.add(vertex);
				}
			}
		}else if(nodeType == DenselyConnectedBiclusteringGUI.TYPE_PROTEIN_NR){
			for(BiologicalNodeAbstract vertex : mg.getAllVertices()){
				if(vertex instanceof Protein){
					allVertices.add(vertex);
				}
			}
		}else{
			allVertices.addAll(mg.getAllVertices());
		}
		
		
		
		if(attrNames.contains(DenselyConnectedBiclusteringGUI.GC_CYCLES)){
			
			allVertices.retainAll(cyclesMap.keySet());
			
			for(BiologicalNodeAbstract vertex: cyclesMap.keySet()){
				if(cyclesMap.get(vertex) == 0.0){
					allVertices.remove(vertex);
				}
			}
		}
		
		
		if(attrNames.contains(DenselyConnectedBiclusteringGUI.GC_CLIQUES)){
			
			allVertices.retainAll(cliquesMap.keySet());
			for(BiologicalNodeAbstract vertex: cliquesMap.keySet()){
				if(cliquesMap.get(vertex) == 0.0){
					allVertices.remove(vertex);
				}
			}
		}

		
		
		for(BiologicalNodeAbstract vertex1 : allVertices){
			HashSet<Integer> neigbours = new HashSet<Integer>();
			idBna.put(vertex1.getID(), vertex1);
			for(BiologicalNodeAbstract vertex2 : allVertices){
				
				
					
				if(graphRepresentation.doesEdgeExist(vertex1, vertex2) || graphRepresentation.doesEdgeExist(vertex2, vertex1)){
					
					neigbours.add(vertex2.getID());
					
				}
			}
			adjacenciesArray[0].put(vertex1.getID(), neigbours);

		}
		
		setAttributes2();
		
		HashSet<HashSet<Integer>> extended = null;
		
	    //Liste von Ergebnis-Objekten = Cluster mit ihren Eigenschaften
		
		
		if(adjacenciesArray[0].size() < MIN_PARALLEL_SIZE){
			numOfThreads = 1;
		}
		
		long start = System.currentTimeMillis();
		
		HashSet<HashSet<Integer>> seeds = preprocessingParallel();
		if(!seeds.isEmpty()){
			extended = expansionParallel(seeds);
		}
		System.out.println("Zeit gesamt (+ Aufruf): "+(System.currentTimeMillis()- start));
		
		System.out.println("----");
		System.out.println();
	
		LinkedList<DCBresultSet> results = null;
		if(extended != null){
			results = doResultsList(extended);
		}
		
		
		return results;
	}
	
	
	/**
	 * 
	 */
	private HashSet<String> setExperiments() {
		HashSet<String> experiments = new HashSet<String>();
		for(String type : attrTyps){
			if(type.equals(DenselyConnectedBiclusteringGUI.TYPE_GRAPHNODE)){
				experiments.add(attrNames.get(attrTyps.indexOf(type)));
			}
		}
		return experiments;
	}


	private LinkedList<DCBresultSet> doResultsList(
			HashSet<HashSet<Integer>> extended) {

		LinkedList<DCBresultSet> results = new LinkedList<>();
		
		//Objekt das die ben�tigten Scores (densiti und homogenity) liefert
		test = new DCBTests(adjacenciesArray[0], density, rangesArray[0], attrdim, attributesArray[0]);
		

		//Erstellung der Ergebnis-Objekte zur Ausgabe
		for(HashSet<Integer> cluster : extended){
			int numOfVertices = cluster.size();
			double densityScore  = test.densityScore(cluster);
			int numOfhomogenAttributes = test.homogenityScore(cluster);
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
		
		return results;
	}
	
	private void setAttributes2() {

//		attributes = new ConcurrentHashMap<Integer, ArrayList<Double>>();

		Hashtable<BiologicalNodeAbstract, Double> averageNeighbourDegreeTable = null;
		ArrayList<String> experimentNames = null;
		
		np = new NetworkProperties();
		
		ArrayList<Double> values;
		ArrayList<Double>[] valuesArray = new ArrayList[numOfThreads];
		
		for(int z = 0; z < attributesArray.length; z++){
			attributesArray[z] = new HashMap<Integer, ArrayList<Double>>();
			for(int id : adjacenciesArray[0].keySet()){
				valuesArray[z] = new ArrayList<Double>();
//				values = new ArrayList<Double>();
				BiologicalNodeAbstract vertex = idBna.get(id);
				for(int i = 0; i < attrTyps.size(); i++){
					String itemTyp = attrTyps.get(i);
					String item = attrNames.get(i);
			        switch(itemTyp){
			        case DenselyConnectedBiclusteringGUI.TYPE_BNA:
				        switch(item){
				        case DenselyConnectedBiclusteringGUI.GC_DEGREE:	    		
				        	valuesArray[z].add((double) adjacenciesArray[0].get(id).size());
				            break;
				            
				        case DenselyConnectedBiclusteringGUI.GC_NEIGHBOUR:
				        	if(averageNeighbourDegreeTable == null){
				        		
				        		
				        		averageNeighbourDegreeTable = np.averageNeighbourDegreeTable();
				        	}
				        	valuesArray[z].add(averageNeighbourDegreeTable.get(vertex));
				            break;
	
				        case DenselyConnectedBiclusteringGUI.GC_CYCLES:
				        	if(cyclesMap.containsKey(vertex)){
				        		valuesArray[z].add(cyclesMap.get(vertex));
				        	}else{
				        		valuesArray[z].add(0.0);
				        	}
				        	
				        	break;
				        
				        case DenselyConnectedBiclusteringGUI.GC_CLIQUES:
				        	valuesArray[z].add(cliquesMap.get(vertex));
				        	break;
				        	
				        default:
				        	break;
				        }
			            break;
			        case DenselyConnectedBiclusteringGUI.TYPE_GRAPHNODE:
			        	BiologicalNodeAbstract graphNode;
//			        	if(experimentNames == null){
//			        		experimentNames = new ArrayList<String>();
//			        		for(int j = 0; j < graphNode.getSuperNode().biodata.length; j++){
//			        			experimentNames.add(graphNode.getSuperNode().biodata[j]);
//			        		}
//			        	}
//
//			        	valuesArray[z].add(graphNode.getSuperNode().biodataEntries[experimentNames.indexOf(item)]);
			        	
			        	
			            break;
			        case DenselyConnectedBiclusteringGUI.TYPE_DNA:
				        switch(item){
				        case "Sequence length": 
				        	
				    		DNA d = (DNA) vertex;
				    		valuesArray[z].add((double) d.getNtSequence().length());
				        	
				            break;
				        default:
				        	break;
				        }
			        	break;
			        case DenselyConnectedBiclusteringGUI.TYPE_RNA:
				        switch(item){
				        case "Sequence length":  
				        	
				    		RNA r = (RNA) vertex;
				    		valuesArray[z].add((double) r.getNtSequence().length());
				    		
				            break;
				        default:
				        	break;
				        }
			        	break;
			        case DenselyConnectedBiclusteringGUI.TYPE_PROTEIN:
				        switch(item){
				        case "Sequence length":
	//		                if(vertex instanceof Protein){
		                    Protein p = (Protein) vertex;
		                    valuesArray[z].add((double) p.getAaSequence().length());  
	//		                }else{
	//		                	values.add(0.0);
	//		                }
				            break;
				        default:
				        	break;
				        }
			        	break;
			        	
			        default:
			        	break;
			        }
				}
				attributesArray[z].put(id, valuesArray[z]);
			}
		}
		
	}

//	//Random-Generierung der Attribute; wird sp�ter durch echte Werte ersetzt
//	private void setAttributes() {
//		int numOfAttributes = ranges.size();
//		for(int id : adjacencies.keySet()){
//			
//			ArrayList<Double> values = new ArrayList<Double>();
//			values.add((double) adjacencies.get(id).size());
//			
//			Double[] factor = {1.0, 10.0, 200.0, 30.0, 50.0, 0.5};
//			
//			if(numOfAttributes  > 1){
//				BiologicalNodeAbstract vertex = idBna.get(id);
//
//                if(vertex instanceof Protein){
//                    Protein p = (Protein) vertex;
//                    values.add((double) p.getAaSequence().length());  
//                }else{
//                	values.add(0.0);
//                }
//                
//                for(int i = 2; i < numOfAttributes; i++){
//                	values.add(Math.random()*factor[i%6]);
//                }
//			}
//			
//			attributes.put(id, values);
//		}
//		
//	}


	/*
	 * �berpr�ft ob jeder Knoten mindestens einen Nachbarn hat mit dem Homogenit�t erf�llt ist
	 * Wenn nicht wird der Knoten aus der Adjazenzliste entfernt
	 */
	public HashMap<HashSet<Integer>, HashSet<Integer>> preprocessingGraph(){
		HashSet<Integer> removalAdjacencies = new HashSet<>();
		
		//Objekt das die ben�tigten Tests (densiti und homogenity) durchf�hrt
		test = new DCBTests(adjacenciesArray[0], density, rangesArray[0], attrdim, attributesArray[0]);
		
		for(int vertex1 : adjacenciesArray[0].keySet()){

			boolean isHomogen = false;

			for(int vertex2 : adjacenciesArray[0].get(vertex1)){
					
					HashSet<Integer> testVertices = new HashSet<Integer>();
				
					testVertices.add(vertex1);
					testVertices.add(vertex2);
					if(test.testHomogenity(testVertices)){
						isHomogen = true;
					}
					
			}
			
			
			if(!isHomogen){
				removalAdjacencies.add(vertex1);
			}
		
		}
		
		removeFormAdjacencies(removalAdjacencies);
		
		
		/*
		 * Generierung der Seeds: Erstes HashSet = verbundener Subgraph (mit zwei Elementen)
		 * Zweites HashSet = Nachbarn-knoten dieses Subgraphen
		 * Nachbarn werden nur hinzugef�gt wenn ihrere IDs gr��er sind
		 * als die beiden des Subgraphen
		 */
		HashMap<HashSet<Integer>, HashSet<Integer>> seeds = new HashMap<>();
		//Objekt das die ben�tigten Tests (densiti und homogenity) durchf�hrt
		test = new DCBTests(adjacenciesArray[0], density, rangesArray[0], attrdim, attributesArray[0]);
				
		
		
		for(int vertex : adjacenciesArray[0].keySet()){
			for(int neighbour : adjacenciesArray[0].get(vertex)){
				if(vertex < neighbour){
					HashSet<Integer> testSet = new HashSet<Integer>();
					testSet.add(vertex);
					testSet.add(neighbour);
					if(test.testDensity(testSet) && test.testHomogenity(testSet)){
						HashSet<Integer> tempSeed = new HashSet<Integer>();
						tempSeed.add(vertex);
						tempSeed.add(neighbour);
						HashSet<Integer> tempNeighbours = new HashSet<Integer>();
						double maxID = Math.max(vertex, neighbour);
		
						for(int connectedNode : adjacenciesArray[0].get(vertex)){
							if(connectedNode > maxID){
								tempNeighbours.add(connectedNode);
							}
						}
						for(int connectedNode : adjacenciesArray[0].get(neighbour)){
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
		
		return seeds;

	}
	
	private HashSet<HashSet<Integer>> preprocessingParallel(){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DenselyConnectedBiclusteringGUI.progressBar.setProgressBarString("Preprocessing");
			}
		});
		
		HashSet<HashSet<Integer>> seeds = new HashSet<>();
		
		long starttime;
		long endtime;
		

		//Ben�tigte Objekte zur Parallelisierung des Preprocessing:
		ExecutorService executorPreprocessing = Executors.newFixedThreadPool(numOfThreads);
		HashSet<DCBpreprocessing> tasks = new HashSet<DCBpreprocessing>();
		List<Future<HashSet<Integer>>> futRes = new LinkedList<>();
		
		//Modifizierte Adjazenzliste (Jeder Knoten kommt nur einfach vor):
		HashMap<Integer, HashSet<Integer>> adjacenciesSingle = adjacenciesSingle();

		starttime = System.currentTimeMillis();
		
		//Preprocessing: Jedes Knoten-Paar aus adjacenciesSingle wird in einem Task verarbeitet
		//(d.h. bildet eine eigenes Callable-Objekt).
		for(Integer vertex1 : adjacenciesSingle.keySet()){
			for(Integer vertex2 : adjacenciesSingle.get(vertex1)){
				tasks.add(new DCBpreprocessing(vertex1, vertex2, adjacenciesArray[0], density, rangesArray[0], attrdim, attributesArray[0]));
			}
		}
		
		try {
			futRes = executorPreprocessing.invokeAll(tasks);
			
		} catch (InterruptedException e1) {
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DenselyConnectedBiclusteringGUI.reactivateUI();
					
					JOptionPane.showMessageDialog(
							null,
							"Preprocessing dosen't work.",
							"Error", JOptionPane.ERROR_MESSAGE);
		
					e1.printStackTrace();
			
			
				}
			});
			
			return seeds;
		}
		
		executorPreprocessing.shutdown();
		
		endtime = System.currentTimeMillis();
		
		preprocessingTime = endtime-starttime;

		
		//Liste der Seeds die nach dem Preprocessing NICHT entfernt werden
//		HashSet<Integer> verticesWhitelist = new HashSet<>();
		
//		HashMap<Integer, HashSet<Integer>> adjacencies_temp = new HashMap<>();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DenselyConnectedBiclusteringGUI.progressBar.setProgressBarString("Seedgeneration part 1");
			}
		});
		
		long seedgeneration1start = System.currentTimeMillis();
		
		int adjacenciesSizeVorher = adjacenciesArray[0].size();
		
		for(int i = 0; i <adjacenciesArray.length; i++){
			adjacenciesArray[i] = new HashMap<Integer, HashSet<Integer>>();
		}
		
		/*
		 * Erstellen eine neue Adjazenzliste. Und 1. Teil der Seedgeneration: 
		 * Adjacencies der Seeds werden sp�ter hinzugef�gt
		 */
		try {
			for(Future<HashSet<Integer>> res : futRes){
			
				HashSet<Integer> seed = res.get();
				if(!seed.isEmpty()){
					seeds.add(seed);
					
					//Aufbau Adjazenzliste:
					Iterator<Integer> seedelements = seed.iterator();
					int vertex1 = seedelements.next();
					int vertex2 = seedelements.next();
					if(adjacenciesArray[0].containsKey(vertex1)){
						
						for(int i = 0; i <adjacenciesArray.length; i++){
							adjacenciesArray[i].get(vertex1).add(vertex2);
						}
						
						
					}else{
						for(int i = 0; i <adjacenciesArray.length; i++){
							HashSet<Integer> neighbour = new HashSet<>();
							neighbour.add(vertex2);
							adjacenciesArray[i].put(vertex1, neighbour);
						}
					}
					if(adjacenciesArray[0].containsKey(vertex2)){
						for(int i = 0; i <adjacenciesArray.length; i++){
							adjacenciesArray[i].get(vertex2).add(vertex1);
						}
						
					}else{
						for(int i = 0; i <adjacenciesArray.length; i++){
							HashSet<Integer> neighbour = new HashSet<>();
							neighbour.add(vertex1);
							adjacenciesArray[i].put(vertex2, neighbour);
						}
					}
					
				}

			}
		} catch (InterruptedException | ExecutionException e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DenselyConnectedBiclusteringGUI.reactivateUI();
					JOptionPane.showMessageDialog(
							null,
							"Seedgeneration dosen't work.",
							"Error", JOptionPane.ERROR_MESSAGE);
		
					e.printStackTrace();
				}
			});
			return seeds;
			
		}

		seedGeneration1Time = System.currentTimeMillis() - seedgeneration1start;
		
//		adjacencies = adjacencies_temp;
		
		//Knoten die nach Preprocessing entfernt werden
//		HashSet<Integer> removalAdjacencies = new HashSet<>();
//		removalAdjacencies.addAll(adjacencies.keySet());
//		removalAdjacencies.removeAll(verticesWhitelist);
		
		/*
		 * Entfernt die Knoten die durch das Preprocessing rausgefiltert wurden
		 */
//		removeFormAdjacencies(removalAdjacencies);
		

		System.out.println("remove: ");
		System.out.println((adjacenciesSizeVorher-adjacenciesArray[0].size()) + " von " + 
				adjacenciesSizeVorher + " Knoten");
		
//		System.out.println(removalAdjacencies.size() + " von " + (verticesWhitelist.size()+removalAdjacencies.size()) + " Knoten");
//		for(Integer removeVertex : removalAdjacencies){
//			System.out.print(removeVertex + " ");
//		}
//		System.out.println();
//		System.out.println();
		System.out.println("Anzahl Threads: " + numOfThreads);
		System.out.println();
		
		return seeds;

	}
	
	/*
	 * Paralleles Preprocessing
	 * Jede Kante wird einzeln gerp�ft (im Callable DCBpreprocessing)
	 */
	public HashSet<HashSet<Integer>> expansionParallel(HashSet<HashSet<Integer>> seeds){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DenselyConnectedBiclusteringGUI.progressBar.setProgressBarString("Seedgeneration part 2");
			}
		});
		
		long starttime2;
		long endtime2;
		
		//Ben�tigte Objekte zur Parallelisierung der Expansion:
		List<Future<LinkedHashSet<HashSet<Integer>>>> futureExpanded = new LinkedList<>();
		ExecutorService executeExpansion = Executors.newFixedThreadPool(numOfThreads);
		HashSet<DCBexpansion> tasksExpansion = new HashSet<>();
		
		starttime2 = System.currentTimeMillis();
		/*
		 * intitialisierung der Callables f�r die Expansion; Zun�chst sind noch keine Seeds enthalten
		 * Es werden so viele Callables erzeugt wie es Threads gibt
		 */
		for(int i = 0; i < numOfThreads; i++){
			tasksExpansion.add(new DCBexpansion(adjacenciesArray[i], density, rangesArray[i], attrdim, attributesArray[i]));
		}
		
		//vergleicht die DCBexpansion-Objekte anhand ihrer Nachbarn-Menge
		DCBexpansionComparator expansionComparator = new DCBexpansionComparator();
		/*
		 * Seedgeneration 2. Teil: hinzuf�gen der Seed-Adjacencies und f�llen der Callables mit den
		 * Seeds: Seed wird immer dem Callable hinzugef�gt das bisher die wenigsten Nachbarn (summe
		 * �ber alle seeds des Objekts) enth�lt
		 */
		for(HashSet<Integer> seed : seeds){
			HashSet<Integer> neighbours = new HashSet<>();
			int maxID = Collections.max(seed);
			for(Integer vertex: seed){
				for(Integer neighbour: adjacenciesArray[0].get(vertex)){
					if(neighbour > maxID){
						neighbours.add(neighbour);
					}
				}
			}
			DCBexpansion minSeed = Collections.min(tasksExpansion, expansionComparator); // ein comparator-objekt reicht
			minSeed.putSeed(seed, neighbours);
		}
		
		endtime2 = System.currentTimeMillis();
		

		

		long starttime3;
		long endtime3;
		
		starttime3 = System.currentTimeMillis();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DenselyConnectedBiclusteringGUI.progressBar.setProgressBarString("Expansion");
			}
		});
		
		try {
			futureExpanded = executeExpansion.invokeAll(tasksExpansion);
		} catch (InterruptedException e1) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DenselyConnectedBiclusteringGUI.reactivateUI();
					JOptionPane.showMessageDialog(
							null,
							"Expansion dosen't work.",
							"Error", JOptionPane.ERROR_MESSAGE);
		
					e1.printStackTrace();
				}
			});
			return null;
			
		}
		
		executeExpansion.shutdown();
		
		
		

		
		
		HashSet<HashSet<Integer>> extended = new LinkedHashSet<>();
		
		int doppelCluster = 0;
		for(Future<LinkedHashSet<HashSet<Integer>>> res : futureExpanded){
			try {
				doppelCluster += res.get().size();
				extended.addAll(res.get());
			} catch (InterruptedException | ExecutionException e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						DenselyConnectedBiclusteringGUI.reactivateUI();
						JOptionPane.showMessageDialog(
								null,
								"Expansion dosen't work (no results).",
								"Error", JOptionPane.ERROR_MESSAGE);
		
						e.printStackTrace();
					}
				});
				return null;
				
			}
		}
		
		endtime3 = System.currentTimeMillis();
		
		long removetimestart = System.currentTimeMillis();
		
//		if(numOfThreads > 1){
			/*
			 * Entfernung doppelter Cluster: (gepr�ft wird auch ob ein Cluster ein anderes enth�lt)
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
			
			extended.removeAll(removeSubsets);
		
//		}
		
		long removetime = System.currentTimeMillis() - removetimestart;
		// TODO syso raus
		int counter = 1;
		for(DCBexpansion expansion : tasksExpansion){
			System.out.print("Thread " + counter + "; # Seeds " + expansion.numSeeds
					+ "; # Nachbarn " + expansion.getNumOfNeighbours() + "; # Cluster " 
					+ expansion.extendedSize); //+ "; " + " davon doppelt/ueberlappend: " + expansion.doppelExtended);
			
//			System.out.print( "; Seeds: ");
//			for(HashSet<Integer> seed : expansion.getSeeds().keySet()){
//				System.out.print("(");
//				for(Integer vertex : seed){
//					System.out.print(vertex + " ");
//				}
//				System.out.print("), ");
//				
//			}
			System.out.println();
			counter++;
		}
		System.out.println("# Cluster (mit doppelten/ueberlappenden): " 
				+ doppelCluster);
		System.out.println("# Cluster (ohne doppelte/ueberlappende): " + extended.size());
		System.out.println();
		
		System.out.println("Zeit fuer \"Preprocessing\": " + (preprocessingTime));
		System.out.println("Zeit fuer \"Seedgeneration Teil 1\": " + seedGeneration1Time);
		System.out.println("Zeit fuer \"Seedgeneration Teil 2\": " + (endtime2-starttime2));
		System.out.println("Zeit fuer \"Expansion\": " + (endtime3-starttime3));
		System.out.println("Zeit fuer Entfernung doppelter: " + removetime);
		System.out.println("Zeit gesamt: " + (preprocessingTime+seedGeneration1Time+(endtime2-starttime2)+(endtime3-starttime3)+removetime));
		System.out.println();
		System.out.println("Parameter:");
		System.out.println("Attribute: ");
		for(int i = 0; i < attrNames.size(); i++){
			System.out.println(attrNames.get(i) + ": " + rangesArray[0].get(i));
		}
		System.out.println("Density: " + density);
		System.out.println("Homogen Attribut (min.): " + attrdim);
		
		findeIntresstingClusters(extended);
		
		return extended;
		
	}
	
	
	
	/**
	 * @param extended
	 */
	private void findeIntresstingClusters(HashSet<HashSet<Integer>> extended) {
		
		
		TreeMap<Double, HashSet<HashSet<Integer>>> clusterValue = new TreeMap<Double, HashSet<HashSet<Integer>>>();
		
		ArrayList<Integer> attrIndices = new ArrayList<Integer>();
		
		for(String typ : attrTyps){
			if(typ.equals(DenselyConnectedBiclusteringGUI.TYPE_GRAPHNODE)){
				attrIndices.add(attrTyps.indexOf(typ));
			}
		}
		
		
		for(HashSet<Integer> cluster: extended){
			if(cluster.size() >= 4){
			
				double summ = 0;
				int counter = 0;
				
				double value = 0;
				for(int vertex : cluster){
					for(int index: attrIndices){
						summ = summ + attributesArray[0].get(vertex).get(index);
						counter++;
					}
					
					value = summ/counter;
				}
				
				if(clusterValue.containsKey(value)){
					clusterValue.get(value).add(cluster);
				}else{
					HashSet<HashSet<Integer>> tmp = new HashSet<HashSet<Integer>>();
					tmp.add(cluster);
					clusterValue.put(value, tmp);
				}
			
			}
			
		}
		
		ArrayList<Double> sortedValues = new ArrayList<Double>();
		sortedValues.addAll(clusterValue.keySet());
		Collections.sort(sortedValues);
		
		System.out.println("low expression");
		for(int i = 0; i < 5; i++){
		
			for(HashSet<Integer> cluster : clusterValue.get(sortedValues.get(i))){
				for(int index : cluster){
					System.out.print(idBna.get(index).getLabel() + " ");
				}
				System.out.print(": " + sortedValues.get(i));
			}
			System.out.println();
		}
		
		System.out.println();
		
		System.out.println("high expression");
		for(int i = (sortedValues.size()-1); i >(sortedValues.size()-6); i--){
		
			for(HashSet<Integer> cluster : clusterValue.get(sortedValues.get(i))){
				for(int index : cluster){
					System.out.print(idBna.get(index).getLabel() + " ");
				}
				System.out.print(": " + sortedValues.get(i));
			}
			System.out.println();
		}
		
		System.out.println();

		
	}
	
	
	static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                return e1.getValue().compareTo(e2.getValue());
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}


	private HashMap<Integer, HashSet<Integer>> adjacenciesSingle(){
			HashMap<Integer, HashSet<Integer>> adjacenciesSingle = new HashMap<Integer, HashSet<Integer>>();
					
			for(Integer key: adjacenciesArray[0].keySet()){
				for(Integer neighbour: adjacenciesArray[0].get(key)){
					if(!adjacenciesSingle.containsKey(neighbour)){
						if(adjacenciesSingle.containsKey(key)){
							adjacenciesSingle.get(key).add(neighbour);
						}else{
							HashSet<Integer> firstNeighbour = new HashSet<>();
							firstNeighbour.add(neighbour);
							adjacenciesSingle.put(key, firstNeighbour);
						}
					}
				}
			}
			
//			System.out.println("SingleAdjacencies:");
//			for(Integer vertex : adjacenciesSingle.keySet()){
//				System.out.print(vertex + ": ");
//				for(Integer neighbour : adjacenciesSingle.get(vertex)){
//					System.out.print(neighbour + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
			
			return adjacenciesSingle;
	
	}
	

	
	
	/*
	 * Removes each vertex of removalAdjacencies form neighbour-list of adjacencies
	 * and in the end the vertex itself.
	 */
	private void removeFormAdjacencies(HashSet<Integer> removalAdjacencies) {
		for(int vertex : removalAdjacencies){
			for(int neigbour : adjacenciesArray[0].get(vertex)){
				adjacenciesArray[0].get(neigbour).remove(vertex);
			}
			
			adjacenciesArray[0].remove(vertex);
			
		}
		
	}

	/*
	 * Findet alle Subgraphen die dicht und homogen genug sind
	 */
	private LinkedHashSet<HashSet<Integer>> expansion(HashMap<HashSet<Integer>, HashSet<Integer>> seeds) {

		/*
		 * Durchl�uft die Seeds-Liste und f�gt einen verbunden Knoten zum Seed hinzu
		 * wenn dichte und homogenit�t erf�llt sind. Sind die Kriterien ncht erf�llt so wird der
		 * Seed dem Ergebnisset (extended) zugeordnent.
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

					if(test.testDensity(testSet) && test.testHomogenity(testSet)){
						HashSet<Integer> tempNodeSet = new HashSet<Integer>();
						tempNodeSet.addAll(seedsHelp.get(nodeSet));
						for(int tempConnected : adjacenciesArray[0].get(connectedNode)){
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
		 * Entfernt doppelte Cluster
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
		
		return extended;
	}

	
//	public void reactivateUI() {
//		// close Progress bar and reactivate UI
//		
////		SwingUtilities.invokeLater(new Runnable() {
////			public void run() {
//				DenselyConnectedBiclusteringGUI.progressBar.closeWindow();
////			}
////		});
////		mw = MainWindowSingelton.getInstance();
////		mw.setEnable(true);
////		mw.setLockedPane(false);
//	}
	
	
	
}
