package graph.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import biologicalElements.GraphElementAbstract;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

public class LabelsWindow implements ActionListener {
    private final JPanel panel;
    private final JTextField label = new JTextField("");
    private final JButton add;
    private final GraphElementAbstract gea;

    private final JDialog dialog;
    //private HashMap<JButton, Parameter> parameters = new HashMap<JButton, Parameter>();

    public LabelsWindow(GraphElementAbstract gea) {
        this.gea = gea;
        MigLayout layout = new MigLayout("", "[left]");

        //DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<String>(ElementNamesSingelton.getInstance().getEnzymes());
        //elementNames.setEditable(true);
        //elementNames.setModel(dcbm);

        //elementNames.setMaximumSize(new Dimension(250,40));
        //elementNames.setSelectedItem(" ");
        //AutoCompleteDecorator.decorate(elementNames);

        panel = new JPanel(layout);
        add = new JButton("Add");
        add.setActionCommand("add");
        add.addActionListener(this);
        label.setPreferredSize(new Dimension(100, 10));

        //panel.add(value, "span,wrap 5,growx ,gaptop 2");

        //panel.add(new JLabel("Label"), "span 2, gaptop 2 ");
        //panel.add(elementNames, "span,wrap,growx ,gap 10, gaptop 2");
        //panel.add(new JSeparator(), "span, growx, wrap 10, gaptop 7 ");

        JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        dialog = pane.createDialog(MainWindow.getInstance().getFrame(), "Labels");
        this.repaint();
        dialog.pack();
        dialog.setVisible(true);
    }

    private void listParameters() {
        panel.add(new JLabel("Labels:"), "span 2, wrap, gaptop 2");
        for (String s : gea.getLabelSet()) {
            panel.add(new JLabel(s), "wrap");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("add".equals(e.getActionCommand())) {
            gea.addLabel(label.getText());
            label.setText("");
            this.repaint();
        }
    }

    private void repaint() {
        panel.removeAll();
        panel.add(new JLabel("Label"));
        panel.add(label, "wrap");
        panel.add(add, "wrap");
        this.listParameters();
        panel.repaint();
        dialog.pack();
    }
}
