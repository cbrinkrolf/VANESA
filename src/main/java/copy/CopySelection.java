package copy;

import java.awt.geom.Point2D;
import java.util.*;

import javax.swing.JOptionPane;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import graph.VanesaGraph;
import gui.MainWindow;

public class CopySelection {
	private final Set<BiologicalNodeAbstract> bnas = new HashSet<>();
	private final Vector<BiologicalEdgeAbstract> beas = new Vector<>();
	private final boolean petriNet;
	private final Map<BiologicalNodeAbstract, Point2D> locations = new HashMap<>();

	public CopySelection(Set<BiologicalNodeAbstract> vertices, Set<BiologicalEdgeAbstract> edges) {
		Pathway pw = GraphInstance.getPathway();
		petriNet = pw.isPetriNet();
		VanesaGraph graph = GraphInstance.getGraph();
		for (BiologicalNodeAbstract v : vertices) {
			locations.put(v, graph.getNodePosition(v));
			bnas.add(v);
		}
		for (BiologicalEdgeAbstract o : pw.getAllEdges()) {
			if (bnas.contains(o.getFrom()) && bnas.contains(o.getTo())) {
				beas.add(o);
			}
		}
	}

	public void paste() {
		Pathway pw = GraphInstance.getPathway();
		if (petriNet ^ pw.isPetriNet()) {
			JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(),
					"Copy-Paste is not possible from biological graph to petri net and vice versa!",
					"Operation not possible...", JOptionPane.ERROR_MESSAGE);
			return;
		}
		BiologicalNodeAbstract bna1;
		BiologicalNodeAbstract bna2;
		BiologicalEdgeAbstract bea2;
		HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> map = new HashMap<>();
		for (BiologicalEdgeAbstract bea : beas) {
			if (!map.containsKey(bea.getFrom())) {
				bna1 = bea.getFrom().clone();
				bna1.removeAllConnectionEdges();
				map.put(bea.getFrom(), bna1);
				pw.addVertex(bna1, locations.get(bea.getFrom()));
			} else {
				bna1 = map.get(bea.getFrom());
			}
			if (!map.containsKey(bea.getTo())) {
				bna2 = bea.getTo().clone();
				bna2.removeAllConnectionEdges();
				map.put(bea.getTo(), bna2);
				pw.addVertex(bna2, locations.get(bea.getTo()));
			} else {
				bna2 = map.get(bea.getTo());
			}
			bea2 = bea.clone();
			bea2.setFrom(bna1);
			bea2.setTo(bna2);
			pw.addEdge(bea2);
			//bea.setEdge(oldEdge);
		}
		for (BiologicalNodeAbstract bna : bnas) {
			if (!map.containsKey(bna)) {
				bna1 = bna.clone();
				bna1.removeAllConnectionEdges();
				map.put(bna, bna1);
				pw.addVertex(bna1, locations.get(bna));
			}
		}
		pw.updateMyGraph();
		MainWindow.getInstance().updateElementTree();
	}
}