package gui.optionPanelWindows;

import javax.swing.JPanel;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import graph.GraphInstance;
import net.miginfocom.swing.MigLayout;

public class SatelliteWindow extends JPanel {
	boolean emptyPane = true;

	public SatelliteWindow() {
		setLayout(new MigLayout("ins 0, fill"));
		setVisible(false);
	}

	public void revalidateSatelliteView() {
		SatelliteVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = GraphInstance.getMyGraph()
				.getSatelliteView();
		if (!emptyPane) {
			removeAll();
		}
		add(vv, "growx");
		emptyPane = false;
		setVisible(true);
		repaint();
	}

	public void removeAllElements() {
		emptyPane = true;
		removeAll();
		setVisible(false);
	}
}
