package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.ContainerSingelton;
import graph.CreatePathway;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.GraphTheoryAlgorithms;
import graph.algorithms.MultidimensionalScaling;
import graph.algorithms.NetworkProperties;
import graph.algorithms.NodeAttributeNames;
import graph.algorithms.NodeAttributeTypes;
import graph.algorithms.ShortestPathsExperimentClustering;
//import graph.algorithms.SteinerTree;
import graph.jung.classes.MyGraph;
import graph.jung.classes.MyVisualizationViewer;
import gui.visualization.PreRenderManager;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import mdsj.MDSJ;

//import org.math.plot.FrameView;
//import org.math.plot.Plot2DPanel;
//import org.math.plot.plots.ColoredScatterPlot;
//import org.math.plot.plots.ScatterPlot;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;

//import com.jujutsu.tsne.FastTSne;
//import com.jujutsu.tsne.MatrixOps;
//import com.jujutsu.tsne.MemOptimizedTSne;
//import com.jujutsu.tsne.PrincipalComponentAnalysis;
//import com.jujutsu.tsne.SimpleTSne;
//import com.jujutsu.tsne.TSne;
//import com.jujutsu.utils.MatrixUtils;





import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.math.plot.Plot2DPanel;
import org.math.plot.PlotPanel;
import org.math.plot.render.AbstractDrawer;
import org.math.plot.plots.ColoredScatterPlot;
import org.math.plot.plots.ScatterPlot;

import com.jujutsu.tsne.FastTSne;
import com.jujutsu.tsne.TSne;
import com.jujutsu.utils.MatrixUtils;

import cluster.clientimpl.ClusterDataUploadWindow;
import cluster.graphdb.GraphDBTransportNode;

public class InfoWindow {

	int nodes, edges, nodedegrees, maxpath, mindegree, maxdegree, cutnodes,
			cliques;
	float avgsp, avgneighbordegree;
	double density, centralization, avgnodedegree, matchingindex;
	boolean connected;
	long time;

	public InfoWindow(boolean extended) {

		MainWindow w = MainWindowSingleton.getInstance();

		String tableStart = "<table  rules=\"rows\" style=\"border-collapse:separate; border-spacing:0; width:100%; border-top:1px solid #eaeaea;\">";
		String tableEnd = "</table>";

		NetworkProperties np = new NetworkProperties();

		nodes = np.getNodeCount();
		edges = np.getEdgeCount();
		density = np.getDensity();
		connected = np.isGraphConnected();
		mindegree = np.getMinDegree();
		maxdegree = np.getMaxDegree();

		if (extended) {
			avgsp = np.averageShortestPathLength();
			nodedegrees = np.countNodeDegrees();
			avgneighbordegree = np.averageNeighbourDegree();
			maxpath = np.maxPathLength();
			centralization = np.getCentralization();
			avgnodedegree = np.getAvgNodeDegree();
			matchingindex = np.getGlobalMatchingIndex();
			cutnodes = np.getCutNodes()[0];
		}

		String instructions = "<html>"
				+ tableStart
				+ writeLine("Number of Nodes:", nodes + "")
				+ writeLine("Number of Edges:", edges + "")
				+ writeLine("Graph Density:", density + "")
				+ writeLine("Is input Graph Connected:", connected + "")
				+ writeLine("Minimum/Maximum Degree:", mindegree + "/"
						+ maxdegree);

		if (extended) {
			instructions += writeLine("Average of shortest paths:", avgsp + "")
					+ writeLine("Number of Node degrees:", nodedegrees + "")
					+ writeLine("Average Neighbour Degree:", avgneighbordegree
							+ "")
					+ writeLine("Maximum Path Length:", maxpath + "")
					+ writeLine("Centralization:", centralization + "")
					+ writeLine("Average Node Degree:", avgnodedegree + "")
					+ writeLine("Global Matching Index:", matchingindex + "")
					+ writeLine("Number of fundamental cycles:",
							np.getFundamentalCycles() + "");
			if (connected) {
				instructions += writeLine("Number of Cut Nodes:", cutnodes + "");
				instructions += writeLine("Edge Connectivity:",
						np.getEdgeConnectivity() + "");
			}
		}

		instructions += tableEnd + "</html>";

		// REENABLE
		 JOptionPane.showMessageDialog(w.returnFrame(), instructions,
		 "Network Properties", JOptionPane.DEFAULT_OPTION);
		
		
//		HashSet<String> m = new HashSet<>();
//		m.add("STARD13");
//		m.add("ARHGAP1");
//		m.add("AAA");
//		new SteinerTree(m);
		
		
		//
		// System.out.println("done.");
		// cs.saveAdjMatrix("Jan.N"+nodes+"E"+edges+".adj");
		// System.out.println("export done.");

		// cs.saveGraphCoordinates("clustering_coords.dat");
		// cs.savePackedAdjList("padjlist");

		// startTime();
		// cs.AllPairShortestPaths(false);
		// endTime("FloydWarshall");

		// cs.removeGreyNodes();
		// startTime();
		// GPUSocketClient l = new GPUSocketClient();
		// endTime("GPU APSP");

		// new ShortestPathsExperimentClustering("Norm");

		// new MultidimensionalScaling("inv1");
		// new MultidimensionalScaling(NodeAttributeNames.CHOLESTEATOMA, false);

		// new
		// new
		// MultidimensionalScaling(NodeAttributeNames.GO_CELLULAR_COMPONENT,true);

		// new
		// new
		// MultidimensionalScaling(NodeAttributeNames.GO_BIOLOGICAL_PROCESS,true);

		// new
		// MultidimensionalScaling(NodeAttributeNames.GO_MOLECULAR_FUNCTION,true);

		// new ClusterDataUploadWindow();

//		 np.AllPairShortestPaths(true);

		// int nodes = np.getNodeCount();

/*		double nodedegree, ndegree, cycles, cliques;
		
		for (BiologicalNodeAbstract bna : GraphInstance.getMyGraph()
				.getAllVertices()) {
			try {
				nodedegree = bna.getNodeAttributeByName(NodeAttributeNames.NODE_DEGREE).getDoublevalue();
			} catch (NullPointerException e) {
				nodedegree = 0.0;
			}
			try {
				ndegree = bna.getNodeAttributeByName(NodeAttributeNames.NEIGHBOR_DEGREE).getDoublevalue();
				} catch (NullPointerException e) {
				ndegree = 0.0;
			}			
			try {
				cycles = bna.getNodeAttributeByName(NodeAttributeNames.CYCLES).getDoublevalue();
			} catch (NullPointerException e) {
				cycles = 0.0;
			}
			try {
				cliques = bna.getNodeAttributeByName(NodeAttributeNames.CLIQUES).getDoublevalue();
				} catch (NullPointerException e) {
				cliques = 0.0;
			}
			System.out.println(bna.getLabel()+"\t"+nodedegree+"\t"+ndegree+"\t"+cycles+"\t"+cliques);
		}
		
		
		
	*/	
		// rectangle paint testing

		MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = GraphInstance
				.getMyGraph().getVisualizationViewer();

		HashSet<BiologicalNodeAbstract> bnas_cyto = new HashSet<>();
		HashSet<BiologicalNodeAbstract> bnas_nucl = new HashSet<>();
		HashSet<BiologicalNodeAbstract> bnas_nucle = new HashSet<>();

		ArrayList<Paintable> ps = new ArrayList<VisualizationServer.Paintable>();
		for (VisualizationViewer.Paintable p : vv.getPreRenderers()) {
			if (p instanceof LocalBackboardPaintable)
				ps.add(p);
		}

		// for(Paintable p : ps){
		// vv.removePreRenderPaintable(p);
		// }

		for (BiologicalNodeAbstract bna : GraphInstance.getMyGraph()
				.getAllVertices()) {
			for (NodeAttribute na : bna.getNodeAttributes()) {
				if (na.getStringvalue().equals("Cytoplasm"))
					bnas_cyto.add(bna);
				if (na.getStringvalue().equals("Nucleus"))
					bnas_nucl.add(bna);
				if (na.getStringvalue().equals("Nucleolus"))
					bnas_nucle.add(bna);
			}

		}
		Color anno = new Color((int) (Math.random() * 0x1000000));
		Random r = new Random(System.currentTimeMillis());
		anno = new Color(anno.getRed(), anno.getGreen(), anno.getBlue(), 180);

//		Color grey = new Color(192, 192, 192);
//		for (BiologicalNodeAbstract b : GraphInstance.getMyGraph()
//				.getAllVertices()) {
//
//			System.out.println(b.getColor() + " \t " + b.getDefaultColor());
//			if (b.getColor().equals(grey)) {
//				b.setColor(new Color(0, 0, 100));
//			}
//
//		}

		// vv.addPreRenderPaintable(new LocalBackboardPaintable(bnas_cyto, anno,
		// 100, "fadeoval", "Cytoplasm"));
		// vv.addPreRenderPaintable(new LocalBackboardPaintable(bnas_nucl,
		// Color.RED, 40, "rect", "Nucleus"));
		// vv.addPreRenderPaintable(new LocalBackboardPaintable(bnas_nucle,
		// Color.blue, 25, "oval"));

		// vv.addPreRenderPaintable(new LocalBackboardPaintable(bnas_nucle,
		// Color.white, 10, "oval"));

		// double perplexity = 50.0d;

//		TSne tsne = new FastTSne();
		// TSne tsne = new SimpleTSne();
		// TSne tsne = new MemOptimizedTSne();
		int iters = 10;
//		System.out.println("Running " + iters + " iterations of TSne on "
//				+ "Chol dataset");
		// double [][] X = MatrixUtils.simpleRead2DMatrix(new File(filename),
		// " ");

		// LOC
		/*
		 * HashMap<String, Integer> locales = new HashMap<>();
		 * BiologicalNodeAbstract bna; int locindex = 0;
		 * 
		 * // estimate assignment of loc -> int [0..locales-1] for (int i = 0; i
		 * < nodes; i++) { bna = np.getNodeAssignmentbackwards(i); for
		 * (NodeAttribute att : bna.getNodeAttributes()) { if
		 * (att.getName().equals( NodeAttributeNames.GO_CELLULAR_COMPONENT)) {
		 * if (!locales.containsKey(att.getStringvalue())) {
		 * locales.put(att.getStringvalue(), locindex); locindex++; } }
		 * 
		 * } }
		 * 
		 * System.out.printf("found %d locales.\n", locales.size()); int
		 * subindex; double[][] X = new double[nodes][locales.size()]; //
		 * for(int i = 0; i<nodes; i++) // java.util.Arrays.fill(X[i], 1.0d);
		 * 
		 * 
		 * 
		 * for (int i = 0; i < X.length; i++) { bna =
		 * np.getNodeAssignmentbackwards(i); for (NodeAttribute na : bna
		 * .getNodeAttributesByType(NodeAttributeTypes.ANNOTATION)) { if
		 * (na.getName().equals( NodeAttributeNames.GO_CELLULAR_COMPONENT)) {
		 * subindex = locales.get(na.getStringvalue()); X[i][subindex] = 1.0d; }
		 * }
		 * 
		 * System.out.println(np.getNodeAssignmentbackwards(i).getLabel()+"\t\t"+
		 * Arrays.toString(X[i]));
		 * 
		 * }
		 */

		// Chol

		int experiments = 7;
		double[][] X = new double[nodes][experiments];
		NodeAttribute att;
		for (int i = 0; i < nodes; i++) {
			for (int j = 0; j < experiments; j++) {
				att = np.getNodeAssignmentbackwards(i).getNodeAttributeByName(
						"Chol" + (j + 1));
				if (att != null) {
					X[i][j] = ((att.getDoublevalue() + 0.0d) * 1.0d);
					// X[i][j] = ((att.getDoublevalue()+0.0d)/100.0d);

				} else {
					X[i][j] = 0.0d;
				}
			}
			// DEBUG
			// System.out.println(np.getNodeAssignmentbackwards(i).getLabel()
			// + "\t\t" + Arrays.toString(X[i]));
		}
		//
		//
		// //print to file
		try {
			PrintWriter data = new PrintWriter("chol1-7.dat", "UTF-8");
			PrintWriter labels = new PrintWriter("chol1-7.labels", "UTF-8");

			for (int i = 0; i < nodes; i++) {
				for (int j = 0; j < experiments; j++) {
					if (j == experiments - 1)
						data.print(X[i][j] + "\n");
					else
						data.print(X[i][j] + " ");
				}
				labels.print(np.getNodeAssignmentbackwards(i).getLabel() + "\n");
			}

			labels.close();
			data.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		//
		try {
			PrintWriter data = new PrintWriter("cellularcomp.dat", "UTF-8");
			BiologicalNodeAbstract bna;
			String[] cells;
			for (int i = 0; i < nodes; i++) {
				cells = new String[11];
				for (int b = 0; b < cells.length; b++) {
					cells[b] = "0";
				}
				bna = np.getNodeAssignmentbackwards(i);
				for (NodeAttribute na : bna.getNodeAttributes()) {
					if (na.getName().equals(
							NodeAttributeNames.GO_CELLULAR_COMPONENT)) {

						if (na.getStringvalue().equals("Nucleus"))
							cells[0] = "1";
						else if (na.getStringvalue().equals("Cytoplasm"))
							cells[1] = "1";
						else if (na.getStringvalue().equals("Plasma membrane"))
							cells[2] = "1";
						else if (na.getStringvalue().equals("Extracellular"))
							cells[3] = "1";
						else if (na.getStringvalue().equals("Mitochondrion"))
							cells[4] = "1";
						else if (na.getStringvalue().equals("Nucleolus"))
							cells[5] = "1";
						else if (na.getStringvalue().equals(
								"Endoplasmic reticulum"))
							cells[6] = "1";
						else if (na.getStringvalue().equals("Golgi apparatus"))
							cells[7] = "1";
						else if (na.getStringvalue().equals("Endosome"))
							cells[8] = "1";
						else if (na.getStringvalue().equals("Cytosol"))
							cells[9] = "1";
						else if (na.getStringvalue().equals(
								"Integral to membrane"))
							cells[10] = "1";

					}
				}
				// System.out.println(Arrays.toString(cells).replaceAll("\\]",
				// "")
				// .replaceAll("\\[", "").replaceAll(" ", ""));
				data.println(Arrays.toString(cells).replaceAll("\\]", "")
						.replaceAll("\\[", "").replaceAll(" ", ""));
			}

			data.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] cells;
		try {
			PrintWriter data = new PrintWriter("molecfunc.dat", "UTF-8");
			BiologicalNodeAbstract bna;
			for (int i = 0; i < nodes; i++) {
				cells = new String[19];
				for (int b = 0; b < cells.length; b++) {
					cells[b] = "0";
				}
				bna = np.getNodeAssignmentbackwards(i);
				for (NodeAttribute na : bna.getNodeAttributes()) {
					if (na.getName().equals(
							NodeAttributeNames.GO_MOLECULAR_FUNCTION)) {

						if (na.getStringvalue().equals(
								"Transcription factor activity"))
							cells[0] = "1";
						else if (na.getStringvalue().equals(
								"Transcription regulator activity"))
							cells[1] = "1";
						else if (na.getStringvalue().equals(
								"Transporter activity"))
							cells[2] = "1";
						else if (na.getStringvalue().equals(
								"Receptor signaling complex scaffold activity"))
							cells[3] = "1";
						else if (na.getStringvalue().equals("DNA binding"))
							cells[4] = "1";
						else if (na.getStringvalue().equals(
								"Ubiquitin-specific protease activity"))
							cells[5] = "1";
						else if (na.getStringvalue().equals(
								"Protein serine/threonine kinase activity"))
							cells[6] = "1";
						else if (na.getStringvalue()
								.equals("Receptor activity"))
							cells[7] = "1";
						else if (na.getStringvalue().equals("RNA binding"))
							cells[8] = "1";
						else if (na.getStringvalue().equals(
								"Catalytic activity"))
							cells[9] = "1";
						else if (na.getStringvalue().equals(
								"Cell adhesion molecule activity"))
							cells[10] = "1";
						else if (na.getStringvalue().equals(
								"G-protein coupled receptor activity"))
							cells[11] = "1";
						else if (na.getStringvalue().equals("Protein binding"))
							cells[12] = "1";
						else if (na.getStringvalue().equals("GTPase activity"))
							cells[13] = "1";
						else if (na.getStringvalue().equals(
								"Cytoskeletal protein binding"))
							cells[14] = "1";
						else if (na.getStringvalue().equals(
								"Structural molecule activity"))
							cells[15] = "1";
						else if (na.getStringvalue().equals(
								"Auxiliary transport protein activity"))
							cells[16] = "1";
						else if (na.getStringvalue().equals(
								"Calcium ion binding"))
							cells[17] = "1";
						else if (na.getStringvalue().equals(
								"Extracellular matrix structural constituent"))
							cells[18] = "1";

					}
				}
				// System.out.println(Arrays.toString(cells).replaceAll("\\]",
				// "")
				// .replaceAll("\\[", "").replaceAll(" ", ""));
				data.println(Arrays.toString(cells).replaceAll("\\]", "")
						.replaceAll("\\[", "").replaceAll(" ", ""));
			}

			data.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// for(BiologicalEdgeAbstract bea :
		// GraphInstance.getMyGraph().getAllEdges()){
		// bea.setColor(new Color(192,192,192,30));
		// }

		GraphInstance.getMyGraph().getVisualizationViewer().repaint();

		//USUAL DATA OUTPUT just for testing
/*		
		try {
			PrintWriter data = new PrintWriter("biolproc.dat", "UTF-8");
			BiologicalNodeAbstract bna;
			for (int i = 0; i < nodes; i++) {
				cells = new String[10];
				for (int b = 0; b < cells.length; b++) {
					cells[b] = "0";
				}
				bna = np.getNodeAssignmentbackwards(i);
				for (NodeAttribute na : bna.getNodeAttributes()) {
					if (na.getName().equals(
							NodeAttributeNames.GO_BIOLOGICAL_PROCESS)) {

						if (na.getStringvalue().equals("Signal transduction"))
							cells[0] = "1";
						else if (na.getStringvalue().equals(
								"Cell communication"))
							cells[1] = "1";
						else if (na
								.getStringvalue()
								.equals("Regulation of nucleobase, nucleoside, nucleotide and nucleic acid metabolism"))
							cells[2] = "1";
						else if (na.getStringvalue().equals(
								"Protein metabolism"))
							cells[3] = "1";
						else if (na.getStringvalue().equals("Metabolism"))
							cells[4] = "1";
						else if (na.getStringvalue().equals(
								"Cell growth and/or maintenance"))
							cells[5] = "1";
						else if (na.getStringvalue().equals("Energy pathways"))
							cells[6] = "1";
						else if (na.getStringvalue().equals("Transport"))
							cells[7] = "1";
						else if (na.getStringvalue().equals("Immune response"))
							cells[8] = "1";
						else if (na.getStringvalue().equals("Apoptosis"))
							cells[9] = "1";
					}
				}
				// System.out.println(Arrays.toString(cells).replaceAll("\\]",
				// "")
				// .replaceAll("\\[", "").replaceAll(" ", ""));
				data.println(Arrays.toString(cells).replaceAll("\\]", "")
						.replaceAll("\\[", "").replaceAll(" ", ""));
			}

			data.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double[][] chol = MatrixUtils.simpleRead2DMatrix(
				new File("chol1-7.dat"), " "),
		// cellular component:
		// annotation = MatrixUtils.simpleRead2DMatrix(newFile("cellularcomp.dat"),",");

		// molecular function:
//		annotation = MatrixUtils.simpleRead2DMatrix(new File("molecfunc.dat"),",");
		
		// biological process
		annotation = MatrixUtils.simpleRead2DMatrix(new File("biolproc.dat"),",");


		double[][] cholannotation = new double[chol.length][chol[0].length
				+ annotation[0].length];
		for (int i = 0; i < chol.length; i++) {
			for (int j = 0; j < chol[0].length; j++) {
				cholannotation[i][j] = chol[i][j];
			}
		}
		for (int i = 0; i < annotation.length; i++) {
			for (int j = 0; j < annotation[0].length; j++) {
				cholannotation[i][experiments + j] = annotation[i][j];
			}
		}
		
		*/
		//RUN TSNE
//		System.out.println(Arrays.toString(cholannotation[0]));
//
//		double[][] Y = tsne.tsne(cholannotation, 2, 6, 50, 500, true);
//
//		for (int i = 0; i < nodes; i++) {
//			BiologicalNodeAbstract bna = np.getNodeAssignmentbackwards(i);
//
//			Point2D pos = GraphInstance.getPathwayStatic().getGraph()
//					.getVertexLocation(bna);
//
//			pos.setLocation(100.0d * Y[i][0], 100.0d * Y[i][1]);
//		}

		// System.out.println(Arrays.toString(X[0]));
		//
		// X = MatrixUtils.simpleRead2DMatrix(new File("ESC.dissim"), ",");
		// String labels[] = MatrixUtils.simpleReadLines(new
		// File("ESC.labels"));
		//
		// double Z[][] = MDSJ.stressMinimization(X);
		// System.out.println("Z.x="+Z.length+"  Z.y="+Z[0].length);
		//
		// double Y[][] = new double[Z[0].length][Z.length];
		//
		// for(int i = 0; i<Y[0].length;i++){
		// for (int j = 0; j < Y.length; j++) {
		// Y[j][i] = Z[i][j];
		// }
		// }

		// System.out.println("X:" + MatrixOps.doubleArrayToString(X));
		// X = MatrixOps.log(X, true);
		// System.out.println("X:" + MatrixOps.doubleArrayToString(X));
		// X = MatrixOps.centerAndScale(X);
		// System.out.println("Shape is: " + X.length + " x " + X[0].length);
		// System.out.println("Starting TSNE: " + new Date());
		// System.out.println(Arrays.toString(X[0]));
		// PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		// double [][] Y = pca.pca(X,2);

		// double [][] Y = tsne.tsne(X, 2, experiments, perplexity, iters,true);
		// System.out.println(Arrays.toString(X[0]));
		// // double[][] Y = tsne.tsne(X, 2, locales.size(), perplexity, iters,
		// true);
		//
		// System.out.println("Finished TSNE: " + new Date());
		// // System.out.println("Result is = " + Y.length + " x " + Y[0].length
		// +
		// // " => \n" + MatrixOps.doubleArrayToString(Y));
		// System.out.println("Result is = " + Y.length + " x " + Y[0].length);
		// System.out.println(Arrays.toString(Y[0]));

		// int x, y;
		// for (int i = 0; i < Y.length; i++) {
		// x = (int) (Y[i][0] * 10.0d);
		// y = (int) (Y[i][1] * 10.0d);
		//

		// }

//		 Plot2DPanel plot = new Plot2DPanel();
//		
//		 ColoredScatterPlot setosaPlot = new ColoredScatterPlot("setosa", Y,
//		 labels);
//		
//		 // ScatterPlot setosaPlot = new ScatterPlot("setosa", Color.BLACK,
//		 Y,);
//		 plot.plotCanvas.setNotable(true);
//		 plot.plotCanvas.setNoteCoords(true);
//		 plot.plotCanvas.addPlot(setosaPlot);
//		
//		 FrameView plotframe = new FrameView(plot);
//		 plotframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		 plotframe.setVisible(true);

	}

	private String writeLine(String description, String Attribute) {

		return "<tr>"
				+ "<th style=\"text-align: left;color:#666;text-transform:uppercase;\" scope=\"col\">"
				+ description + "</th>"
				+ "<td style=\"padding:10px;color:#888;\">" + Attribute
				+ "</td></tr>";
	}

	private void startTime() {
		time = System.currentTimeMillis();
	}

	private void endTime(String message) {
		time = System.currentTimeMillis() - time;
		System.out.println(message + " took \t\t" + formatMillis(time));
	}

	static public String formatMillis(long val) {
		StringBuilder buf = new StringBuilder(20);
		String sgn = "";

		if (val < 0) {
			sgn = "-";
			val = Math.abs(val);
		}

		append(buf, sgn, 0, (val / 3600000));
		append(buf, ":", 2, ((val % 3600000) / 60000));
		append(buf, ":", 2, ((val % 60000) / 1000));
		append(buf, ".", 3, (val % 1000));
		return buf.toString();
	}

	/**
	 * Append a right-aligned and zero-padded numeric value to a
	 * `StringBuilder`.
	 */
	static private void append(StringBuilder tgt, String pfx, int dgt, long val) {
		tgt.append(pfx);
		if (dgt > 1) {
			int pad = (dgt - 1);
			for (long xa = val; xa > 9 && pad > 0; xa /= 10) {
				pad--;
			}
			for (int xa = 0; xa < pad; xa++) {
				tgt.append('0');
			}
		}
		tgt.append(val);
	}

}