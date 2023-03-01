package gui.optionPanelWindows;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import graph.GraphInstance;

public class SatelliteWindow {
    private final JPanel panel = new JPanel();
    boolean emptyPane = true;

    public void revalidateSatelliteView() {
        SatelliteVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = GraphInstance.getMyGraph()
                                                                                                       .getSatelliteView();
        if (!emptyPane) {
            panel.removeAll();
        }
        panel.add(vv, BorderLayout.CENTER);
        emptyPane = false;
        panel.setVisible(true);
        panel.repaint();
    }

    public void removeAllElements() {
        emptyPane = true;
        panel.removeAll();
        panel.setVisible(false);
    }

    public JPanel getSatellitePane() {
        panel.setVisible(false);
        return panel;
    }
}
