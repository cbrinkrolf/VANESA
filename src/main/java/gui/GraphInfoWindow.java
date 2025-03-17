package gui;

import java.awt.*;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import graph.algorithms.NetworkProperties;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

public class GraphInfoWindow {
	private final Pathway pw = GraphInstance.getPathway();
	private final JPanel contentPanel = new JPanel(new MigLayout("ins 0, fillx, wrap"));

	public GraphInfoWindow() {
		final JPanel panel = new JPanel(new MigLayout("ins 0, fill"));
		panel.setPreferredSize(new Dimension(800, 600));
		contentPanel.setBackground(new Color(200, 200, 200));
		final JScrollPane scrollPane = new JScrollPane(contentPanel);
		panel.add(scrollPane, "grow");

		final NetworkProperties np = new NetworkProperties();
		int nodes = np.getNodeCount();
		int edges = np.getEdgeCount();
		int logicalNodes = 0;
		for (BiologicalNodeAbstract bna : pw.getGraph2().getNodes()) {
			if (bna.isLogical()) {
				logicalNodes++;
			}
		}
		int normalNodes = nodes - logicalNodes;
		final List<Object[]> nodeRows = new ArrayList<>();
		nodeRows.add(new String[] { "Total", String.format("%d (%d + %d logical)", nodes, normalNodes, logicalNodes),
				String.valueOf(np.getMinDegree()), String.valueOf(np.getMaxDegree()) });
		final var nodeLabelStatistics = getNodeLabelStatistics();
		for (final String label : nodeLabelStatistics.keySet().stream().sorted().collect(Collectors.toList())) {
			final NodeLabelStatistics statistics = nodeLabelStatistics.get(label);
			nodeRows.add(new String[] { label, String.valueOf(statistics.count), String.valueOf(statistics.minDegree),
					String.valueOf(statistics.maxDegree) });
		}
		final JXTable nodesTable = new JXTable(new DefaultTableModel(nodeRows.toArray(new Object[0][]),
				new Object[] { "", "Count", "Min Degree", "Max Degree" }));
		nodesTable.setHorizontalScrollEnabled(false);
		nodesTable.setFillsViewportHeight(false);
		final JScrollPane nodesTableScrollPane = new JScrollPane(nodesTable);
		nodesTableScrollPane.setPreferredSize(new Dimension(100, 200));

		final List<Object[]> edgeRows = new ArrayList<>();
		edgeRows.add(new String[] { "Total", String.valueOf(edges) });
		final var edgeLabelStatistics = getEdgeLabelStatistics();
		for (final String label : edgeLabelStatistics.keySet().stream().sorted().collect(Collectors.toList())) {
			final EdgeLabelStatistics statistics = edgeLabelStatistics.get(label);
			edgeRows.add(new String[] { label, String.valueOf(statistics.count) });
		}
		final JXTable edgesTable = new JXTable(
				new DefaultTableModel(edgeRows.toArray(new Object[0][]), new Object[] { "", "Count" }));
		edgesTable.setHorizontalScrollEnabled(false);
		edgesTable.setFillsViewportHeight(false);
		final JScrollPane edgesTableScrollPane = new JScrollPane(edgesTable);
		edgesTableScrollPane.setPreferredSize(new Dimension(100, 200));

		addHeader("Nodes");
		contentPanel.add(nodesTableScrollPane, "growx");
		addHeader("Edges");
		contentPanel.add(edgesTableScrollPane, "growx");

		final boolean connected = np.isGraphConnected();
		// TODO: better layout
		contentPanel.add(new JLabel("Graph Density: " + np.getDensity()));
		contentPanel.add(new JLabel("Is Graph Connected: " + connected));
		contentPanel.add(new JLabel("Average shortest path length: " + np.averageShortestPathLength()));
		contentPanel.add(new JLabel("Number of Node degrees: " + np.countNodeDegrees()));
		contentPanel.add(new JLabel("Average Neighbour Degree: " + np.averageNeighbourDegree()));
		contentPanel.add(new JLabel("Maximum Path Length: " + np.maxPathLength()));
		contentPanel.add(new JLabel("Centralization: " + np.getCentralization()));
		contentPanel.add(new JLabel("Average Node Degree: " + np.getAvgNodeDegree()));
		contentPanel.add(new JLabel("Global Matching Index: " + np.getGlobalMatchingIndex()));
		contentPanel.add(new JLabel("Number of fundamental cycles: " + np.getFundamentalCycles()));
		if (connected) {
			contentPanel.add(new JLabel("Number of Cut Nodes: " + np.getCutNodes()[0]));
			contentPanel.add(new JLabel("Edge Connectivity: " + np.getEdgeConnectivity()));
		}

		JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(), panel, "Network Properties",
				JOptionPane.PLAIN_MESSAGE);
	}

	private void addHeader(final String label) {
		final JPanel header = new JPanel(new MigLayout("left"));
		header.setBackground(new Color(164, 164, 164));
		final JLabel headerLabel = new JLabel(label);
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
		header.add(headerLabel);
		contentPanel.add(header, "growx");
	}

	private Map<String, NodeLabelStatistics> getNodeLabelStatistics() {
		final Map<String, NodeLabelStatistics> result = new HashMap<>();
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			final String type = bna.getBiologicalElement();
			NodeLabelStatistics statistics = result.get(type);
			if (statistics == null) {
				statistics = new NodeLabelStatistics();
				result.put(type, statistics);
			}
			statistics.count++;
			final int degree = bna.getConnectingEdges().size();
			if (statistics.minDegree == -1 || degree < statistics.minDegree) {
				statistics.minDegree = degree;
			}
			if (statistics.maxDegree == -1 || degree > statistics.maxDegree) {
				statistics.maxDegree = degree;
			}
		}
		return result;
	}

	private Map<String, EdgeLabelStatistics> getEdgeLabelStatistics() {
		final Map<String, EdgeLabelStatistics> result = new HashMap<>();
		for (final BiologicalEdgeAbstract bea : pw.getAllEdges()) {
			final String type = bea.getBiologicalElement();
			EdgeLabelStatistics statistics = result.get(type);
			if (statistics == null) {
				statistics = new EdgeLabelStatistics();
				result.put(type, statistics);
			}
			statistics.count++;
		}
		return result;
	}

	private static class NodeLabelStatistics {
		public int count;
		public int minDegree = -1;
		public int maxDegree = -1;
	}

	private static class EdgeLabelStatistics {
		public int count;
	}
}