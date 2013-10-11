package gui;

import graph.algorithms.Connectness;

import javax.swing.JOptionPane;

public class InfoWindow {

	public InfoWindow() {

		MainWindow w = MainWindowSingelton.getInstance();

		String tableStart = "<table  rules=\"rows\" style=\"border-collapse:separate; border-spacing:0; width:100%; border-top:1px solid #eaeaea;\">";
		String tableEnd = "</table>";

		Connectness connectnes = new Connectness();
		
        String instructions = "<html>"
				+ tableStart
				+ writeLine("Number of Nodes:", connectnes.getNodeCount() + "")
				+ writeLine("Number of Edges:", connectnes.getEdgeCount() + "")
				+ writeLine("Is input Graph Connected:", connectnes.isGraphConnected() + "")
				+ writeLine("Average of shortest paths:", connectnes.averageShortestPathLength()+ "")
				+ writeLine("Number of Node degrees:", connectnes.countNodeDegrees()+ "")
				+ writeLine("Average Neighbour Degree:", connectnes.averageNeighbourDegree()+ "")
				+ writeLine("Maximum Path Length:", connectnes.maxPathLength()+ "")
				+ writeLine("Graph Density:", connectnes.getDensity()+ "")
				+ writeLine("Centralization:", connectnes.getCentralization()+ "")
				+ writeLine("Minimum/Maximum Degree:", connectnes.getMinDegree()+ "/"+connectnes.getMaxDegree())
				+ writeLine("Average Node Degree:", connectnes.getAvgNodeDegree()+ "")
				+ writeLine("Global Matching Index:", connectnes.getGlobalMatchingIndex()+ "")
				+ tableEnd
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