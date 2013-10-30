package gui;

import graph.algorithms.Connectness;

import javax.swing.JOptionPane;

public class InfoWindow {
	
	int nodes, edges, nodedegrees, maxpath, mindegree, maxdegree, cutnodes, cliques;
	float avgsp, avgneighbordegree; 
	double density, centralization, avgnodedegree, matchingindex;
	boolean connected;
	

	public InfoWindow() {

		MainWindow w = MainWindowSingelton.getInstance();

		String tableStart = "<table  rules=\"rows\" style=\"border-collapse:separate; border-spacing:0; width:100%; border-top:1px solid #eaeaea;\">";
		String tableEnd = "</table>";

		Connectness connectnes = new Connectness();
		
		nodes = connectnes.getNodeCount();
		edges = connectnes.getEdgeCount();
		connected = connectnes.isGraphConnected();
		avgsp = connectnes.averageShortestPathLength();
		nodedegrees = connectnes.countNodeDegrees();
		avgneighbordegree = connectnes.averageNeighbourDegree();
		maxpath = connectnes.maxPathLength();
		density = connectnes.getDensity();
		centralization =  connectnes.getCentralization();
		mindegree = connectnes.getMinDegree();
		maxdegree = connectnes.getMaxDegree();
		avgnodedegree = connectnes.getAvgNodeDegree();
		matchingindex = connectnes.getGlobalMatchingIndex();
		cutnodes = connectnes.getCutNodes()[0];
//		cliques = connectnes.getNumberOfCliques();
		
		
        String instructions = "<html>"
				+ tableStart
				+ writeLine("Number of Nodes:", nodes + "")
				+ writeLine("Number of Edges:", edges + "")
				+ writeLine("Is input Graph Connected:", connected + "")
				+ writeLine("Average of shortest paths:",avgsp + "")
				+ writeLine("Number of Node degrees:", nodedegrees + "")
				+ writeLine("Average Neighbour Degree:", avgneighbordegree + "")
				+ writeLine("Maximum Path Length:", maxpath + "")
				+ writeLine("Graph Density:", density + "")
				+ writeLine("Centralization:", centralization + "")
				+ writeLine("Minimum/Maximum Degree:", mindegree + "/" + maxdegree)
				+ writeLine("Average Node Degree:", avgnodedegree + "")
				+ writeLine("Global Matching Index:", matchingindex + "")
//				+ writeLine("Number of Cliques:", cliques + "")
				;
		
        if(connected)
        	instructions+=writeLine("Number of Cut Nodes:", cutnodes + "");        
        
        instructions+=
        		tableEnd
				+ "</html>";

				
        JOptionPane.showMessageDialog(w.returnFrame(), instructions,
				"Network Statistics", 1);
		
	}
	

	private String writeLine(String description, String Attribute) {

		return "<tr>"
				+ "<th style=\"text-align: left;color:#666;text-transform:uppercase;\" scope=\"col\">"
				+ description + "</th>"
				+ "<td style=\"padding:10px;color:#888;\">" + Attribute
				+ "</td></tr>";
	}

}