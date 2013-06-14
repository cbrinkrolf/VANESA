package graph.algorithms.alignment;


import gui.algorithms.ScreenSize;
import gui.jmatrixview.DefaultMatrixCellRenderer;
import gui.jmatrixview.MatrixControlPanel;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;
import blast.AllAgainstAll;
import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

public class SimilarityMatrix {

//	private Pathway pathwayA, pathwayB;
	private DoubleMatrix2D similarityMatrix;
	private int noNodesA, noNodesB;
	private int noEdgesA, noEdgesB;
	private HashMap<String, Integer> graphA_id2position_Mapping, graphB_id2position_Mapping;
	private HashMap<String, Integer> graphA_name2position, graphB_name2position;
	
	private HashMap<String, ArrayList<BiologicalNodeAbstract>> declaration2NodeSet_graphA;
	private HashMap<String, ArrayList<BiologicalNodeAbstract>> declaration2NodeSet_graphB;
	
	private double maxSimValue = -Math.log10(Double.MIN_VALUE);
	private double min;
	private double max;
	
//	private boolean isSparse;
//	private DoubleFactory2D factory;
//	private int sparseNodeNoLimit = 500;
//	private double sparseNodeEdgesRatioLimit = 2;// 1/3;
	
	public SimilarityMatrix(AdjacencyMatrix amA, AdjacencyMatrix amB, Pathway pwA, Pathway pwB){
		
		this.graphA_id2position_Mapping = amA.getId2position();
		this.graphB_id2position_Mapping = amB.getId2position();
		
		this.graphA_name2position = amA.getName2position();
		this.graphB_name2position = amB.getName2position();
		
		noNodesA = pwA.getAllNodes().size();
		noEdgesA = pwA.getAllEdges().size();
		noNodesB = pwB.getAllNodes().size();
		noEdgesB = pwB.getAllEdges().size();

//		this.setSparsity();

		analyseGraphs("A", pwA, pwB);
		analyseGraphs("B", pwA, pwB);
		
	}
	
	
	public void buildSimilarityMatrixWithBLAST(){
		
		similarityMatrix = new DenseDoubleMatrix2D(noNodesA, noNodesB);
		similarityMatrix.assign(0);
		
		int posA, posB;
		posA = posB = -1;
		
		for (Iterator<String> iter = declaration2NodeSet_graphA.keySet().iterator(); iter.hasNext();) {
			
			String declaration = iter.next();
			
			if(declaration2NodeSet_graphB.containsKey(declaration)){
				
				// compare Proteins
				if(declaration.equals(Elementdeclerations.protein)){
					
//					BiologicalNodeSet proteinNodeSet_graphA = declaration2NodeSet_graphA.get(declaration);
					ArrayList<BiologicalNodeAbstract>  proteinNodeSet_graphA = declaration2NodeSet_graphA.get(declaration);
					
					// this map has as key the protein name and as value the AA-sequence 
					HashMap<String, String> map_graphA = new HashMap<String, String>();
					for (Iterator<BiologicalNodeAbstract> iterator = proteinNodeSet_graphA.iterator(); iterator.hasNext();) {
						BiologicalNodeAbstract node = iterator.next();
						Protein protein = (Protein) node;
//						map_graphA.put(protein.getName(), protein.getAaSequence());
						map_graphA.put(node.getVertex().toString(), protein.getAaSequence());
					}
					
//					BiologicalNodeSet proteinNodeSet_graphB = declaration2NodeSet_graphB.get(declaration);
					ArrayList<BiologicalNodeAbstract> proteinNodeSet_graphB = declaration2NodeSet_graphB.get(declaration);
					HashMap<String, String> map_graphB = new HashMap<String, String>();
					for (Iterator<BiologicalNodeAbstract> iterator = proteinNodeSet_graphB.iterator(); iterator.hasNext();) {
						BiologicalNodeAbstract node = iterator.next();
						Protein protein = (Protein) node;
//						map_graphB.put(protein.getName(), protein.getAaSequence());
						map_graphB.put(node.getVertex().toString(), protein.getAaSequence());
					}
					
					// call BLAST for AA sequences (blastp)
//					BLAST_Client client = new BLAST_Client();
//					client.allAgainstAll(map_graphA, map_graphB, "blastp");
					AllAgainstAll blast = new AllAgainstAll(map_graphA, map_graphB, "blastp");
					blast.run();
					
//					System.out.println(client.getSimMatrix());
					
					Collection c = blast.getReferenceID2Position().keySet();
				    Iterator<String> iterator = c.iterator();
				    while(iterator.hasNext()){
						
						String refID = iterator.next();
						int posRef = (blast.getReferenceID2Position().get(refID)).intValue();
						
						int posQuery = -1;
						
						Collection c2 = blast.getQueryID2Position().keySet();
					    Iterator<String> iterator2 = c2.iterator();
					    while(iterator2.hasNext()){
					    	
							String queryID = iterator2.next();
							posQuery = (blast.getQueryID2Position().get(queryID)).intValue();
							
							double simValue = blast.getSimMatrix().get(posRef, posQuery);
							
//							System.out.println(simValue);
							
							posA = (graphA_id2position_Mapping.get(refID)).intValue();
							posB = (graphB_id2position_Mapping.get(queryID)).intValue();
							similarityMatrix.set(posA, posB, simValue);
//							similarityMatrix.set(posB, posA, simValue);
							
						}
						
					}
					
				} // end Protein comparison
				
				
				// compare Nuleotid sequences (DNA, gene)
				if(declaration.equals(Elementdeclerations.dna)){
					
					ArrayList<BiologicalNodeAbstract> ntNodeSet_graphA = declaration2NodeSet_graphA.get(declaration);
					
					// this map has as key the gene name and as value the nt-sequence 
					HashMap<String, String> map_graphA = new HashMap<String, String>();
					for (Iterator<BiologicalNodeAbstract> iterator = ntNodeSet_graphA.iterator(); iterator.hasNext();) {
						BiologicalNodeAbstract node = iterator.next();
						if(node instanceof DNA){
							DNA dna = (DNA) node;
//							map_graphA.put(dna.getName(), dna.getNtSequence());
							map_graphA.put(node.getVertex().toString(), dna.getNtSequence());
						} else if (node instanceof Gene) {
							Gene gene = (Gene) node;
//							map_graphA.put(gene.getName(), gene.getNtSequence());
							map_graphA.put(node.getVertex().toString(), gene.getNtSequence());
						}
					}
					
					ArrayList<BiologicalNodeAbstract> ntNodeSet_graphB = declaration2NodeSet_graphB.get(declaration);
					HashMap<String, String> map_graphB = new HashMap<String, String>();
					for (Iterator<BiologicalNodeAbstract> iterator = ntNodeSet_graphB.iterator(); iterator.hasNext();) {
						BiologicalNodeAbstract node = iterator.next();
						if(node instanceof DNA){
							DNA dna = (DNA) node;
							map_graphB.put(node.getVertex().toString(), dna.getNtSequence());
						} else if (node instanceof Gene) {
							Gene gene = (Gene) node;
							map_graphB.put(node.getVertex().toString(), gene.getNtSequence());
						}
					}
					
					// call BLAST for nt-sequences (blastn)
//					BLAST_Client client = new BLAST_Client();
//					client.allAgainstAll(map_graphA, map_graphB, "blastn");
					AllAgainstAll blast = new AllAgainstAll(map_graphA, map_graphB, "blastn");
					blast.run();
					
					for (Iterator iterator = blast.getReferenceID2Position().keySet().iterator(); iterator.hasNext();) {
						
						String refID = (String) iterator.next();
						int posRef = (blast.getReferenceID2Position().get(refID)).intValue();
						int posQuery = -1;
						
						for (Iterator iterator2 = blast.getQueryID2Position().keySet().iterator(); iterator.hasNext();) {
							
							String queryID = (String) iterator2.next();
							posQuery = (blast.getQueryID2Position().get(queryID)).intValue();
							
							double simValue = blast.getSimMatrix().get(posRef, posQuery);
							posA = (graphA_id2position_Mapping.get(refID)).intValue();
							posB = (graphB_id2position_Mapping.get(queryID)).intValue();
							similarityMatrix.set(posA, posB, simValue);
//							similarityMatrix.set(posB, posA, simValue);
							
						}

					}
					
				} // end Gene, DNA comparison
				
				
				//	compare Nuleotid sequences (RNAs)
				if(declaration.equals(Elementdeclerations.rna)){
					
					ArrayList<BiologicalNodeAbstract> ntNodeSet_graphA = declaration2NodeSet_graphA.get(declaration);
					
					// this map has as key the gene name and as value the nt-sequence 
					HashMap<String, String> map_graphA = new HashMap<String, String>();
					for (Iterator iterator = ntNodeSet_graphA.iterator(); iterator.hasNext();) {
						BiologicalNodeAbstract node = (BiologicalNodeAbstract) iterator.next();
						RNA rna = (RNA) node;
						map_graphA.put(node.getVertex().toString(), rna.getNtSequence());
					}
					
					ArrayList<BiologicalNodeAbstract> ntNodeSet_graphB = declaration2NodeSet_graphB.get(declaration);
					HashMap<String, String> map_graphB = new HashMap<String, String>();
					for (Iterator iterator = ntNodeSet_graphB.iterator(); iterator.hasNext();) {
						BiologicalNodeAbstract node = (BiologicalNodeAbstract) iterator.next();
						RNA rna = (RNA) node;
						map_graphB.put(node.getVertex().toString(), rna.getNtSequence());
					}
					
					// call BLAST for nt-sequences (blastn)
//					BLAST_Client client = new BLAST_Client();
//					client.allAgainstAll(map_graphA, map_graphB, "blastn");
					AllAgainstAll blast = new AllAgainstAll(map_graphA, map_graphB, "blastn");
					blast.run();
					
					for (Iterator iterator = blast.getReferenceID2Position().keySet().iterator(); iterator.hasNext();) {
						
						String refID = (String) iterator.next();
						int posRef = (blast.getReferenceID2Position().get(refID)).intValue();
						int posQuery = -1;
						
						for (Iterator iterator2 = blast.getQueryID2Position().keySet().iterator(); iterator.hasNext();) {
							
							String queryID = (String) iterator2.next();
							posQuery = (blast.getQueryID2Position().get(queryID)).intValue();
							
							double simValue = blast.getSimMatrix().get(posRef, posQuery);
							posA = (graphA_id2position_Mapping.get(refID)).intValue();
							posB = (graphB_id2position_Mapping.get(queryID)).intValue();
							similarityMatrix.set(posA, posB, simValue);
//							similarityMatrix.set(posB, posA, simValue);
							
						}
						
					}
					
				} // end RNA comparison
				else{
					
//					ArrayList<BiologicalNodeAbstract> nodeSet_graphA = declaration2NodeSet_graphA.get(declaration);
//					ArrayList<BiologicalNodeAbstract> nodeSet_graphB = declaration2NodeSet_graphB.get(declaration);
//					
//					for (BiologicalNodeAbstract nodeA : nodeSet_graphA) {
//						
//						for (BiologicalNodeAbstract nodeB : nodeSet_graphB) {
//							
//							if (nodeA.getBiologicalElement().equals(nodeB.getBiologicalElement())) {
//								
//								posA = ((Integer) graphA_id2position_Mapping.get(nodeA.getVertex().toString())).intValue();
//								posB = ((Integer) graphB_id2position_Mapping.get(nodeB.getVertex().toString())).intValue();
//								similarityMatrix.set(posA, posB, 300);
//								
//							}
//							
//						}
//						
//					}
					
				}
				
			} // end if(contains declaration)
			
		} // end for-loop for declarations
		
	}
	
	
	private void analyseGraphs(String graph, Pathway pathwayA, Pathway pathwayB) {
		
		Pathway pathway = null;
		HashMap<String, ArrayList<BiologicalNodeAbstract>> map = null;
		if(graph.equals("A")){
			pathway = pathwayA;
			declaration2NodeSet_graphA = new HashMap<String, ArrayList<BiologicalNodeAbstract>>();
			map = declaration2NodeSet_graphA;
		}else if(graph.equals("B")){
			pathway = pathwayB;
			declaration2NodeSet_graphB = new HashMap<String, ArrayList<BiologicalNodeAbstract>>();
			map = declaration2NodeSet_graphB;
		}
		
		for (Object element : pathway.getAllNodes()) {
			
			BiologicalNodeAbstract bioNode = (BiologicalNodeAbstract) element;
			String declaration = bioNode.getBiologicalElement();
			
			if (declaration.equals(Elementdeclerations.protein) || 
					declaration.equals(Elementdeclerations.enzyme) ||
					declaration.equals(Elementdeclerations.membraneChannel) ||
					declaration.equals(Elementdeclerations.receptor) ||
					declaration.equals(Elementdeclerations.membraneReceptor)) {
				
				declaration = Elementdeclerations.protein;
				
			} else if( declaration.equals(Elementdeclerations.gene) ||
					declaration.equals(Elementdeclerations.dna)) {
				
				declaration = Elementdeclerations.dna;
				
			} else if( declaration.equals(Elementdeclerations.rna) || 
					declaration.equals(Elementdeclerations.mRNA) ||
					declaration.equals(Elementdeclerations.sRNA)) {
				
				declaration = Elementdeclerations.rna;
				
//			} else if(declaration.equals(Elementdeclerations.smallMolecule) ||
//					declaration.equals(Elementdeclerations.compound)){
//				
//				declaration = Elementdeclerations.smallMolecule;
//				
			} else { // All others
				declaration = "others";
			}
			
			if (map.containsKey(declaration)) {
//				map.get(declaration).addNode(bioNode);
				map.get(declaration).add(bioNode);
			} else {
//				BiologicalNodeSet nodeSet = new BiologicalNodeSet(declaration, pathway.getName());
//				nodeSet.addNode(bioNode);
//				map.put(declaration, nodeSet);
				ArrayList<BiologicalNodeAbstract> newList = new ArrayList<BiologicalNodeAbstract>();
				newList.add(bioNode);
				map.put(declaration, newList);
			}
		}
	}


	public DoubleMatrix2D getMatrix(){
		
		return this.similarityMatrix;
		
	}


	public void buildSimilarityMatrixByType() {

		similarityMatrix = new DenseDoubleMatrix2D(noNodesA, noNodesB);
		similarityMatrix.assign(0);
		
		int posA, posB;
		posA = posB = -1;
		
		for (Iterator<String> iter = declaration2NodeSet_graphA.keySet().iterator(); iter.hasNext();) {
			
			String declaration = iter.next();
			
			ArrayList<BiologicalNodeAbstract> nodeSet_graphA = declaration2NodeSet_graphA.get(declaration);
			ArrayList<BiologicalNodeAbstract> nodeSet_graphB = declaration2NodeSet_graphB.get(declaration);
			
			for (BiologicalNodeAbstract nodeA : nodeSet_graphA) {
				
				for (BiologicalNodeAbstract nodeB : nodeSet_graphB) {
					
					if (nodeA.getBiologicalElement().equals(nodeB.getBiologicalElement())) {
						
						posA = (graphA_id2position_Mapping.get(nodeA.getVertex().toString())).intValue();
						posB = (graphB_id2position_Mapping.get(nodeB.getVertex().toString())).intValue();
						similarityMatrix.set(posA, posB, 1);
						
					}
					
				}
				
			}
			
		}
		
	}
	
	/*
	 * normalize similarity matrix
	 */
	public void normalizeSimilarityMatrix(){
		
//		min = Double.MAX_VALUE;
//		max = -Double.MAX_VALUE;
//		this.similarityMatrix.forEachNonZero(new IntIntDoubleFunction(){
//			public double apply(int row, int col, double val){
//				if(val < min) min = val;
//				if(val > max) max = val;
//				return val;
//			}
//		});
		
		min = 0;
		max = maxSimValue;
		this.similarityMatrix.forEachNonZero(new IntIntDoubleFunction(){
			public double apply(int row, int col, double val){
				return normalize(min, max, val);
			}
		});
		
	}
	
	/*
	 * normalization between 0 and 1
	 */
	private double normalize(double min_old, double max_old, double value){
		
		return normalize(min_old, max_old, 0, 1, value);
		
	}
	
	/*
	 * general normalization
	 */
	private double normalize(double min_old, double max_old,
			double min_new, double max_new, double val){
		
		return ((val - min_old) / (max_old - min_old)) * (max_new - min_new) + min_new;
		
	}


	public boolean buildSimilarityMatrixByFile() {
		
		similarityMatrix = new DenseDoubleMatrix2D(noNodesA, noNodesB);
		similarityMatrix.assign(0);
		
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(true);
		int option = chooser.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			try{
				File file = chooser.getSelectedFile();
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine().trim();
				String[] graphB_IDs = line.split("\t");
				
				if ( !(noNodesB == graphB_IDs.length)) {
					JOptionPane.showMessageDialog(null, "Can not load file! \nThe number of nodes of the second graph does not match with the matrix file!");
					return false;
				}
				for (int i = 0; i < graphB_IDs.length; i++) {
					if( !graphB_name2position.containsKey(graphB_IDs[i])){
						JOptionPane.showMessageDialog(null, "Can not load file! \nGraph A does not contains at least one identifier from the matrix file!");
						return false;
					}
				}
				
//				String[] graphB_IDs = new String[noNodesB];
				int lineCounter = 0;
				while ((line = br.readLine()) != null) {
					
					line = line.trim();
					String[] newLine = line.split("\t");
					if ( !graphA_name2position.containsKey(newLine[0]) ) {
						JOptionPane.showMessageDialog(null, "Can not load file! \nThe first graph does not contains at least one identifier from the matrix file!");
						return false;
					}
					
					if ( !(newLine.length == noNodesB+1) ) {
						JOptionPane.showMessageDialog(null, "Can not load file! \nThe matrix file is not correct!");
						return false;
					}
					for (int j = 1; j < newLine.length; j++) {
						double val = Double.valueOf(newLine[j]);
						if(val > 0){
							int posB = graphB_name2position.get(graphB_IDs[j-1]).intValue();
							int posA = graphA_name2position.get(newLine[0]);
							similarityMatrix.set(posA, posB, val);
						}
					}
					lineCounter++;
				}
				
				if ( !(noNodesA == lineCounter)) {
					JOptionPane.showMessageDialog(null, "Can not load file! \nThe number of nodes of first graph does not match with the matrix file!");
					return false;
				}
				
			}catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Can not load file!");
				return false;
			}
			
		}
		
		return true;
		
	}
	
	public void visualizeSimilarityMatrix(){

		DoubleMatrix2D matrix = this.similarityMatrix.copy();
        
        String [] rowLabels = new String[noNodesA];
        for (Iterator iterator = this.graphA_name2position.keySet().iterator(); iterator.hasNext();) {
			String name = (String) iterator.next();
			int pos = this.graphA_name2position.get(name).intValue();
			rowLabels[pos] = name;
		}
        String [] colLabels = new String[noNodesB];
        for (Iterator iterator = this.graphB_name2position.keySet().iterator(); iterator.hasNext();) {
			String name = (String) iterator.next();
			int pos = this.graphB_name2position.get(name).intValue();
			colLabels[pos] = name;
		}
        int [] rowTypes = new int[noNodesA];
        int [] colTypes = new int[noNodesB];
        
//        int t = 0;
//        for(int l=0; l<size; l++) {
//            rowLabels[l] = "node "+ String.valueOf(l);
//            
//            rowTypes[l] = 0;
//            colTypes[l] = rowTypes[l];
//        }
        
        
        double minmin, maxmax;
//        minmin = Double.MAX_VALUE;
//        maxmax = Double.MIN_VALUE;
//        for(int i=0; i<matrix.columns(); i++) {
//            DoubleMatrix1D matcol = matrix.viewColumn(i);
//            double min = matcol.aggregate(cern.jet.math.Functions.functions.min, cern.jet.math.Functions.functions.identity);
//            double max = matcol.aggregate(cern.jet.math.Functions.functions.max, cern.jet.math.Functions.functions.identity);
//            minmin = Math.min(min, minmin);
//            maxmax = Math.max(max, maxmax);
//            
//        }
        minmin=0;
        maxmax = 1;
   
        
        
//        MultiShapeMatrixCellRenderer dmcr = new MultiShapeMatrixCellRenderer( 
//        		matrix, 
//        		java.awt.Color.white, 
//        		java.awt.Color.orange,
//        		minmin, 
//        		maxmax,
//        		colTypes,
//        		rowTypes
//        );
        DefaultMatrixCellRenderer dmcr = new DefaultMatrixCellRenderer(
        		matrix, Color.BLUE, Color.RED, minmin, maxmax, rowTypes, colTypes);
        MatrixControlPanel mcp = new MatrixControlPanel( 700, 700);
        
        
        JFrame f = null;
        JDialog dialog = new JDialog(f, "Similarity Matrix", true);
//        JOptionPane optionPane = new JOptionPane(mcp);
		dialog.setContentPane(mcp);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//        f= new JFrame("Similarity Matrix");
        
//        f.addWindowListener(new java.awt.event.WindowAdapter() {
//            public void windowClosing(java.awt.event.WindowEvent evt) {
//                System.exit(0);
//            }
//        });
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ScreenSize screen = new ScreenSize();
		int screenHeight = (int) screen.getheight();
		int screenWidth = (int) screen.getwidth();
		dialog.pack();
		dialog.setLocation((screenWidth / 2) - dialog.getSize().width / 2,
				(screenHeight / 2) - dialog.getSize().height / 2);
//        f.setSize(700, 700);
        
//        f.getContentPane().add(mcp);
        mcp.setData(matrix, dmcr, rowLabels , colLabels, rowTypes, colTypes); 
//        f.setVisible(true);
        dialog.setVisible(true);
		
	}
	
}
