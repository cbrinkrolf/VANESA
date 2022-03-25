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
		int logicalNodes = 0;
		for (BiologicalNodeAbstract bna : pw.getGraph().getAllVertices()) {
			if (bna.hasRef()) {
				logicalNodes++;
			}
		}

		String instructions = "<html>" + tableStart + writeLine("", "all = ", "true +", "logical")
				+ writeLine("Number of nodes:", nodes + "", nodes - logicalNodes + "", logicalNodes + "")
				// + writeLine("Number of (true) nodes:", nodes - logicalNodes + "")
				// + writeLine("Number of logical nodes:", logicalNodes + "")
				+ writeLine("Number of edges:", edges + "") + writeLine("<hr>Nodes", "");

		Map<String, Integer> nodesMap = getNodeTypes();
		Map<String, Integer> nodesMapLogical = getNodeTypesLogical();

		for (String type : nodesMap.keySet()) {
			if (nodesMapLogical.containsKey(type)) {
				instructions += writeLine(type + ": ", nodesMap.get(type) + "",
						nodesMap.get(type) - nodesMapLogical.get(type) + "", nodesMapLogical.get(type) + "");
			} else {
				instructions += writeLine(type + ": ", nodesMap.get(type) + "");
			}
		}

		Map<String, Integer> edgesMap = getEdgeTypes();
		instructions += writeLine("<hr>Edges", "");

		for (String type : edgesMap.keySet()) {
			instructions += writeLine(type + ": ", edgesMap.get(type) + "");
		}

		instructions += tableEnd + "</html>";

		// REENABLE
		JOptionPane.showMessageDialog(w.getFrame(), instructions, "Network Properties", JOptionPane.DEFAULT_OPTION);

	}

	private Map<String, Integer> getNodeTypes() {
		Map<String, Integer> map = new HashMap<>();
		String type;
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			type = bna.getBiologicalElement();
			if (!map.containsKey(type)) {
				map.put(type, 0);
			}
			map.put(type, map.get(type) + 1);
		}
		return map;
	}

	private Map<String, Integer> getNodeTypesLogical() {
		Map<String, Integer> map = new HashMap<>();
		String type;
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (bna.hasRef()) {
				type = bna.getBiologicalElement();
				if (!map.containsKey(type)) {
					map.put(type, 0);
				}
				map.put(type, map.get(type) + 1);
			}
		}
		return map;
	}

	private Map<String, Integer> getEdgeTypes() {
		Map<String, Integer> map = new HashMap<>();
		String type;
		for (BiologicalEdgeAbstract bea : pw.getAllEdges()) {
			type = bea.getBiologicalElement();
			if (!map.containsKey(type)) {
				map.put(type, 0);
			}
			map.put(type, map.get(type) + 1);
		}
		return map;
	}

	private String writeLine(String description, String attribute1, String attribute2, String attribute3) {

		return "<tr>" + "<th style=\"text-align: left;color:#666;text-transform:uppercase;\" scope=\"col\">"
				+ description + "</th>" + "<td style=\"padding:5px;color:#888;\">" + attribute1 + "</td>"
				+ "<td style=\"padding:5px;color:#888;\">" + attribute2 + "</td>"
				+ "<td style=\"padding:5px;color:#888;\">" + attribute3 + "</td></tr>";
	}

	private String writeLine(String description, String attribute1) {
		return this.writeLine(description, attribute1, "", "");
	}
}