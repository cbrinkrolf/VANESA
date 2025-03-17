package graph.gui;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import graph.GraphInstance;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ReferenceDialog {
    private final JOptionPane pane;
    private final Pathway pw;
    private final JComboBox<String> box = new JComboBox<>();
    private final ArrayList<BiologicalNodeAbstract> list = new ArrayList<>();
    private final BiologicalNodeAbstract self;

    public ReferenceDialog(BiologicalNodeAbstract bna) {
        pw = GraphInstance.getPathway();
        self = bna;
        MigLayout layout = new MigLayout("", "[left]");

        // DefaultComboBoxModel<BiologicalNodeAbstract> dcbm = new
        // DefaultComboBoxModel<BiologicalNodeAbstract>((BiologicalNodeAbstract[])
        // pw.getAllNodes().toArray());
        JComboBox<BiologicalNodeAbstract> elementNames = new JComboBox<>();
        elementNames.setEditable(true);
        // elementNames.setModel(dcbm);
        elementNames.setMaximumSize(new Dimension(250, 40));
        elementNames.setSelectedItem(" ");
        AutoCompleteDecorator.decorate(elementNames);

        JPanel panel = new JPanel(layout);
        panel.add(new JLabel("Element"), "span 4");

        addNodeItems();
        AutoCompleteDecorator.decorate(box);
        box.setSelectedItem("");
        box.setMaximumSize(new Dimension(250, 300));
        panel.add(box, "span,wrap 5,growx ,gaptop 2");

        pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    }

    private void addNodeItems() {
        // sort entries by network label
        HashMap<String, BiologicalNodeAbstract> map = new HashMap<>();
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            if (bna != self && !bna.isLogical()) {
                if (self instanceof Place) {
                    if (bna instanceof Place) {
                        map.put(bna.getNetworkLabel(), bna);
                    }
                } else if (self instanceof Transition) {
                    if (bna instanceof Transition) {
                        map.put(bna.getNetworkLabel(), bna);
                    }
                } else {
                    map.put(bna.getNetworkLabel(), bna);
                }
            }
        }
        ArrayList<String> ids = new ArrayList<>(map.keySet());
        ids.sort(String::compareTo);
        for (String id : ids) {
            list.add(map.get(id));
            box.addItem(map.get(id).getNetworkLabel());
        }
    }

    public BiologicalNodeAbstract getAnswer() {
        JDialog dialog = pane.createDialog(null, "Select a reference");
        dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
        dialog.setVisible(true);
        Integer value = (Integer) pane.getValue();
        if (value != null && box.getSelectedIndex() > -1 && value == JOptionPane.OK_OPTION) {
            // details[0] = elementNames.getSelectedItem().toString();
            // details[1] = box.getSelectedItem().toString();
            return list.get(box.getSelectedIndex());
        }
        return null;
    }
}
