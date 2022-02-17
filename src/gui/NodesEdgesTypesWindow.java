package gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.algorithms.NetworkProperties;

public class NodesEdgesTypesWindow {

	private Pathway pw;

	public NodesEdgesTypesWindow(Pathway pw) {
		this.pw = pw;

		MainWindow w = MainWindow.getInstance();

		String tableStart = "<table  rules=\"rows\" style=\"border-collapse:separate; border-spacing:0; width:100%; border-top:1px solid #eaeaea;\">";
		String tableEnd = "</table>";

		NetworkProperties np = new NetworkProperties();

		int nodes = np.getNodeCount();
		int edges = np.getEdgeCount();
		

		String instructions = "<html>"
				+ tableStart
				+ writeLine("Number of Nodes:", nodes + "")
				+ writeLine("Number of Edges:", edges + "")
				+ writeLine("<hr>Nodes", "");
		
		Map<String, Integer> nodesMap = getNodeTypes();
		for(String type : nodesMap.keySet()){
			instructions += writeLine(type+": ", nodesMap.get(type)+"");
		}
		
		
		Map<String, Integer> edgesMap = getEdgeTypes();
		instructions += writeLine("<hr>Edges", "");
		
		for(String type : edgesMap.keySet()){
			instructions += writeLine(type+": ", edgesMap.get(type)+"");
		}

		instructions += tableEnd + "</html>";

		// REENABLE
		 JOptionPane.showMessageDialog(w.getFrame(), instructions,
		 "Network Properties", JOptionPane.DEFAULT_OPTION);
		
	}
	
	private Map<String, Integer> getNodeTypes(){
		Map<String, Integer> map = new HashMap<>();
		String type;
		for(BiologicalNodeAbstract bna : pw.getAllGraphNodes()){
			type = bna.getBiologicalElement();
			if(!map.containsKey(type)){
				map.put(type, 0);
			}
			map.put(type, map.get(type)+1);
		}
		return map;
	}
	
	private Map<String, Integer> getEdgeTypes(){
		Map<String, Integer> map = new HashMap<>();
		String type;
		for(BiologicalEdgeAbstract bea : pw.getAllEdges()){
			type = bea.getBiologicalElement();
			if(!map.containsKey(type)){
				map.put(type, 0);
			}
			map.put(type, map.get(type)+1);
		}
		return map;
	}

	private String writeLine(String description, String Attribute) {

		return "<tr>"
				+ "<th style=\"text-align: left;color:#666;text-transform:uppercase;\" scope=\"col\">"
				+ description + "</th>"
				+ "<td style=\"padding:5px;color:#888;\">" + Attribute
				+ "</td></tr>";
	}
}