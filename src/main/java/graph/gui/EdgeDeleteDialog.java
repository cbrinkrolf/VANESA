package graph.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import graph.GraphInstance;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

public class EdgeDeleteDialog {
    private final JOptionPane pane;
    private final JRadioButton selectedEdge;
    private final JRadioButton allEdges;
    private final JComboBox<String> edges = new JComboBox<>();
    private final HashMap<Integer, BiologicalEdgeAbstract> edgeMap = new HashMap<>();

    public EdgeDeleteDialog(BiologicalEdgeAbstract edge) {
        MigLayout layout = new MigLayout("", "[left]");

        ButtonGroup group = new ButtonGroup();
        selectedEdge = new JRadioButton("Selected Edge");
        allEdges = new JRadioButton("All Sub-Edges");
        selectedEdge.setSelected(true);

        group.add(selectedEdge);
        group.add(allEdges);

        JPanel panel = new JPanel(layout);

        panel.add(new JLabel("Select Start Node"), "");
        addAllConnectingEdges(edge, edges, edgeMap);
        AutoCompleteDecorator.decorate(edges);
        panel.add(edges, "span,wrap,growx,gap 10");

        // panel.add(new JSeparator(), "span, growx, wrap 10");

        panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

        panel.add(new JLabel("Edge(s) to delete:"), "");
        panel.add(selectedEdge, "gap 10");
        panel.add(allEdges, "span,wrap,growx");

        pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    }

    private void addAllConnectingEdges(BiologicalEdgeAbstract edge, JComboBox<String> edgeBox,
                                       HashMap<Integer, BiologicalEdgeAbstract> map) {
        for (BiologicalEdgeAbstract childEdge : edge.getTo().getConnectingEdges()) {
            if (childEdge.getFrom().getCurrentShownParentNode(GraphInstance.getPathway().getGraph()) == edge.getFrom() |
                childEdge.getTo().getCurrentShownParentNode(GraphInstance.getPathway().getGraph()) == edge.getFrom()) {
                if (childEdge.isDirected()) {
                    edgeBox.addItem(childEdge.getFrom().getLabel() + " --> " + childEdge.getTo().getLabel());
                } else {
                    edgeBox.addItem(childEdge.getFrom().getLabel() + " --- " + childEdge.getTo().getLabel());
                }
                edgeMap.put(edgeBox.getItemCount() - 1, childEdge);
            }
        }
    }

    /**
     * @return The original, hierarchical edge to delete all sub-Edges. One single sub-Edge to be deleted. Null if
     * nothing selected or aborted.
     */
    public Set<BiologicalEdgeAbstract> getAnswer() {
        JDialog dialog = pane.createDialog(null, "Delete a hierarchical edge");
        // dialog.show();
        dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
        dialog.setVisible(true);
        Integer value = (Integer) pane.getValue();
        Set<BiologicalEdgeAbstract> ret = new HashSet<>();
        if (value != null && value == JOptionPane.OK_OPTION) {
            if (selectedEdge.isSelected()) {
                ret.add(edgeMap.get(edges.getSelectedIndex()));
                return ret;
            }
            if (allEdges.isSelected()) {
                for (int key : edgeMap.keySet()) {
                    ret.add(edgeMap.get(key));
                }
                return ret;
            }
        }
        return null;
    }
}
