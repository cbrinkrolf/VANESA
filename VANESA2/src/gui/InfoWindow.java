package gui;

import graph.algorithms.MultidimensionalScaling;
import graph.algorithms.NetworkProperties;
import graph.algorithms.NodeAttributeNames;
import graph.algorithms.ShortestPathsExperimentClustering;

import javax.swing.JOptionPane;

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

		NetworkProperties cs = new NetworkProperties();

		nodes = cs.getNodeCount();
		edges = cs.getEdgeCount();
		density = cs.getDensity();
		connected = cs.isGraphConnected();
		mindegree = cs.getMinDegree();
		maxdegree = cs.getMaxDegree();
		
		if(extended){
		avgsp = cs.averageShortestPathLength();
		nodedegrees = cs.countNodeDegrees();
		avgneighbordegree = cs.averageNeighbourDegree();
		maxpath = cs.maxPathLength();
		centralization = cs.getCentralization();
		avgnodedegree = cs.getAvgNodeDegree();
		matchingindex = cs.getGlobalMatchingIndex();
		cutnodes = cs.getCutNodes()[0];
		}

		String instructions = "<html>"
				+ tableStart
				+ writeLine("Number of Nodes:", nodes + "")
				+ writeLine("Number of Edges:", edges + "")
				+ writeLine("Graph Density:", density + "")
				+ writeLine("Is input Graph Connected:", connected + "")
				+ writeLine("Minimum/Maximum Degree:", mindegree + "/"
						+ maxdegree);
		
		if(extended){
			instructions += writeLine("Average of shortest paths:", avgsp + "")
					+ writeLine("Number of Node degrees:", nodedegrees + "")
					+ writeLine("Average Neighbour Degree:", avgneighbordegree + "")
					+ writeLine("Maximum Path Length:", maxpath + "")
					+ writeLine("Centralization:", centralization + "")
					+ writeLine("Average Node Degree:", avgnodedegree + "")
					+ writeLine("Global Matching Index:", matchingindex + "")
					+ writeLine("Number of fundamental cycles:",
							cs.getFundamentalCycles() + "");
			if (connected) {
				instructions += writeLine("Number of Cut Nodes:", cutnodes + "");
				instructions += writeLine("Edge Connectivity:",
						cs.getEdgeConnectivity() + "");
			}
		}					

		instructions += tableEnd + "</html>";
		//
		JOptionPane.showMessageDialog(w.returnFrame(), instructions,
				"Network Properties", JOptionPane.DEFAULT_OPTION);		
		//
		// System.out.println("done.");
//		cs.saveAdjMatrix("Jan.N"+nodes+"E"+edges+".adj");
//		System.out.println("export done.");

		//cs.saveGraphCoordinates("clustering_coords.dat");
		// cs.savePackedAdjList("padjlist");

		// startTime();
		// cs.AllPairShortestPaths(false);
		// endTime("FloydWarshall");

//		 cs.removeGreyNodes();
		// startTime();
		// GPUSocketClient l = new GPUSocketClient();
		// endTime("GPU APSP");
		
//		new ShortestPathsExperimentClustering("Norm");	
	
//		new MultidimensionalScaling("inv1");
//		new MultidimensionalScaling(NodeAttributeNames.CHOLESTEATOMA, false);

//		new MultidimensionalScaling(NodeAttributeNames.GO_CELLULAR_COMPONENT,true);

//		new MultidimensionalScaling(NodeAttributeNames.GO_BIOLOGICAL_PROCESS,true);
		
		
		
		
		

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