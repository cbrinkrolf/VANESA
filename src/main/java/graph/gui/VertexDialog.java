package graph.gui;

import biologicalElements.EnzymeNames;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.compartment.Compartment;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VertexDialog {
    private final JOptionPane pane;
    private final JTextField name;
    private final JComboBox<String> elementNames = new JComboBox<>();
    private final JComboBox<String> compartment = new JComboBox<>();
    private final JComboBox<String> elementType = new JComboBox<>();
    private final Pathway pw;
    private int lastTypeIdx = -1;
    private final DefaultComboBoxModel<String> dcbm;
    private final DefaultComboBoxModel<String> dcbmEmpty;

    public VertexDialog(Pathway pw, int lastTypeidx) {
        this.pw = pw;
        MigLayout layout = new MigLayout("", "[left]");
        dcbm = new DefaultComboBoxModel<>(EnzymeNames.getInstance().getEnzymes());
        dcbmEmpty = new DefaultComboBoxModel<>(new String[]{""});
        elementNames.setEditable(true);

        elementNames.setModel(dcbm);

        elementNames.setMaximumSize(new Dimension(250, 40));
        elementNames.setSelectedItem("");

        AutoCompleteDecorator.decorate(elementNames);

        name = new JTextField(20);
        JPanel panel = new JPanel(layout);

        panel.add(new JLabel("Element"), "span 4");

        elementType.addItemListener(e -> {
            if (Elementdeclerations.enzyme.equals(elementType.getSelectedItem())) {
                elementNames.setModel(dcbm);
                AutoCompleteDecorator.decorate(elementNames);
            } else {
                elementNames.setModel(dcbmEmpty);
                AutoCompleteDecorator.decorate(elementNames);
            }
        });
        addNodeItems();
        AutoCompleteDecorator.decorate(elementType);
        if (pw.isHeadless()) {
            if (lastTypeidx < 0) {
                elementType.setSelectedItem(Elementdeclerations.anyBNA);
            } else {
                elementType.setSelectedIndex(lastTypeidx);
            }
        } else {
            if (lastTypeidx < 0) {
                elementType.setSelectedItem(Elementdeclerations.enzyme);
            } else {
                elementType.setSelectedIndex(lastTypeidx);
            }
        }
        elementType.setMaximumSize(new Dimension(250, 300));
        panel.add(elementType, "span,wrap 5,growx ,gaptop 2");
        if (!pw.isHeadless()) {
            panel.add(new JLabel("Compartment"), "span 4, gapright 4");
            AutoCompleteDecorator.decorate(compartment);
            compartment.setMaximumSize(new Dimension(250, 300));
            compartment.setSelectedItem("Cytoplasma");
            panel.add(compartment, "span,wrap 5,growx ,gaptop 2");
        }
        if (pw.isHeadless()) {
            panel.add(new JLabel("Name"), "span 2, gaptop 2 ");
            name.setText("N" + (pw.getNodeCount() + 1));
            panel.add(name, "span,wrap,growx ,gap 10, gaptop 2");
        } else {
            panel.add(new JLabel("Label"), "span 2, gaptop 2 ");
            panel.add(elementNames, "span,wrap,growx ,gap 10, gaptop 2");
        }
        panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");
        pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    }

    private void addNodeItems() {
        List<String> nodeItems = new Elementdeclerations().getNotPNNodeDeclarations();
        if (pw.isHeadless()) {
            elementType.addItem(Elementdeclerations.anyBNA);
        }
        for (String element : nodeItems) {
            elementType.addItem(element);
        }
        if (!pw.isHeadless()) {
            List<Compartment> compartmentList = pw.getCompartmentManager().getAllCompartmentsAlphabetically();
            for (Compartment c : compartmentList) {
                compartment.addItem(c.getName());
            }
        }
    }

    public Map<String, String> getAnswer(Component relativeTo) {
        JDialog dialog = pane.createDialog(null, "Create an element");
        dialog.setLocationRelativeTo(relativeTo != null ? relativeTo : MainWindow.getInstance().getFrame());
        dialog.setVisible(true);
        if ((Integer) pane.getValue() == JOptionPane.OK_OPTION) {
            Map<String, String> details = new HashMap<>();
            if (pw.isHeadless()) {
                details.put("name", name.getText().trim());
            } else {
                details.put("name", elementNames.getSelectedItem().toString().trim());
                details.put("compartment", compartment.getSelectedItem().toString().trim());
            }
            details.put("elementType", elementType.getSelectedItem().toString().trim());
            lastTypeIdx = elementType.getSelectedIndex();
            return details;
        }
        return null;
    }

    public int getLastTypeIdx() {
        return lastTypeIdx;
    }
}
