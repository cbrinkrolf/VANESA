/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MDLayoutConfig.java
 *
 * Created on 16.07.2008, 12:31:43
 */
package configurations.gui;

import graph.layouts.modularLayout.MDLayout;
import gui.algorithms.ScreenSize;

import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

/**
 *
 * @author dao
 */
public class MDLayoutConfig extends ConfigPanel {

    public static int default_maxIterations = 500;
    public static final double default_amultiplier = 0.4;
    public static final double default_rmultiplier = 0.4;
    public static final double default_edgeMultiplier = 1.0;
    public static final double default_minEdgeLength = 18.0;
    public static final double default_lowTemp = 3.0;
    public static int maxIterations = default_maxIterations;
    public static double amultiplier = default_amultiplier;
    public static double rmultiplier = default_rmultiplier;
    public static double edgeMultiplier = default_edgeMultiplier;
    public static double minEdgeLength = default_minEdgeLength;
    public static double lowTemp = default_lowTemp;
    public static boolean showInnerNode;
    public static boolean concernVertexBound;
    public int selectOption = -1;

    /** Creates new form MDLayoutConfig */
    public MDLayoutConfig() {
        super(MDLayout.class);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jSpinner_max_iterations = new javax.swing.JSpinner();
        jSpinner_low_temperature = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSpinner_preferredEdgeLength_multiplier = new javax.swing.JSpinner();
        jSpinner_repulsion_multiplier = new javax.swing.JSpinner();
        jSpinner_attraction_multiplier = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSpinner_min_edge_length = new javax.swing.JSpinner();
//        btnOk = new javax.swing.JButton();
//        btnCancel = new javax.swing.JButton();
//        btnReset = new JButton("reset");
        jSpinner_attraction_multiplier.setModel(
                new javax.swing.SpinnerNumberModel(
                amultiplier, 0.0d, 2.0d, 0.01));
        jSpinner_repulsion_multiplier.setModel(
                new javax.swing.SpinnerNumberModel(
                rmultiplier, 0.0d, 2.0d, 0.01d));
        jSpinner_preferredEdgeLength_multiplier.setModel(new javax.swing.SpinnerNumberModel(
                MDLayoutConfig.edgeMultiplier, 0.1d, 5.0d, 0.1d));
        jSpinner_low_temperature.setModel(
                new javax.swing.SpinnerNumberModel(
                lowTemp, 0.0d, 20.0d, 0.1d));
        jSpinner_max_iterations.setModel(
                new javax.swing.SpinnerNumberModel(
                maxIterations, 0, 1000, 1));
        jSpinner_min_edge_length.setModel(
                new javax.swing.SpinnerNumberModel(
                minEdgeLength, 1.0, 200, 1));

        jLabel5.setText("max iterations:");

        jLabel4.setText("low temperature:");

        jLabel3.setText("preferred edge length multiplier:");

        jLabel1.setText("attraction multiplier:");

        jLabel2.setText("repulsion multiplier:");

        jLabel6.setText("minmal edge length:");
        jLabelShowInnerNode = new JLabel("show inner nodes:");
        jLabelRegardVertexDim = new JLabel("regard vertex label:");
        jCheckBoxShowInnerNode = new JCheckBox(null, null, showInnerNode);
        jCheckBoxRegardVertexDim = new JCheckBox(null, null, concernVertexBound);
        jCheckBoxShowInnerNode.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                showInnerNode = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        jCheckBoxRegardVertexDim.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                concernVertexBound = (e.getStateChange() == ItemEvent.SELECTED);
            }
        });
//        btnOk.setText("ok");
//
//        btnOk.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                MDLayoutConfig.this.selectOption = JOptionPane.OK_OPTION;
//                setValues();
//            }
//        });

//        btnCancel.setText("cancel");
//        btnCancel.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                MDLayoutConfig.this.selectOption = JOptionPane.CANCEL_OPTION;
//            }
//        });
//        btnReset.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                MDLayoutConfig.this.selectOption = JOptionPane.NO_OPTION;
//                resetValues();
//            }
//        });
        MigLayout layout = new MigLayout(
                "insets 20",
                "[left, grow 70]20[left, grow 30, fill]",
                "[][][][][][][][]60[]");
        Container content = this;
        content.setLayout(layout);
        content.add(this.jLabel1, "cell 0 0");
        content.add(this.jSpinner_attraction_multiplier, "cell 1 0");
        content.add(this.jLabel2, "cell 0 1");
        content.add(this.jSpinner_repulsion_multiplier, "cell 1 1");
        content.add(this.jLabel3, "cell 0 2");
        content.add(this.jSpinner_preferredEdgeLength_multiplier, "cell 1 2");
        content.add(this.jLabel4, "cell 0 3");
        content.add(this.jSpinner_low_temperature, "cell 1 3");
        content.add(this.jLabel5, "cell 0 4");
        content.add(this.jSpinner_max_iterations, "cell 1 4");
        content.add(this.jLabel6, "cell 0 5");
        content.add(this.jSpinner_min_edge_length, "cell 1 5");
        content.add(this.jLabelRegardVertexDim, "cell 0 6");
        content.add(this.jCheckBoxRegardVertexDim, "cell 1 6");
        content.add(this.jLabelShowInnerNode, "cell 0 7");
        content.add(this.jCheckBoxShowInnerNode, "cell 1 7");
//        content.add(this.btnOk, "cell 0 8 2 1, w 80!, align right");
//        content.add(this.btnReset, "cell 0 8 2 1, w 80!");
//        content.add(this.btnCancel, "cell 0 8 2 1, w 80!");
    }// </editor-fold>

    private void loadValues() {
        jSpinner_attraction_multiplier.setValue(
                amultiplier);
        jSpinner_repulsion_multiplier.setValue(
                rmultiplier);
        jSpinner_preferredEdgeLength_multiplier.setValue(
                MDLayoutConfig.edgeMultiplier);
        jSpinner_low_temperature.setValue(
                lowTemp);
        jSpinner_max_iterations.setValue(
                maxIterations);
        jSpinner_min_edge_length.setValue(
                minEdgeLength);
        jCheckBoxShowInnerNode.setSelected(showInnerNode);
        jCheckBoxRegardVertexDim.setSelected(concernVertexBound);
    }

    @Override
	public void resetValues() {
        amultiplier = default_amultiplier;
        rmultiplier = default_rmultiplier;
        maxIterations = default_maxIterations;
        lowTemp = default_lowTemp;
        minEdgeLength = default_minEdgeLength;
        edgeMultiplier = default_edgeMultiplier;
        showInnerNode = false;
        concernVertexBound = false;
        loadValues();
    }

    @Override
	public void setValues() {
        amultiplier = (Double) this.jSpinner_attraction_multiplier.getValue();
        rmultiplier = (Double) this.jSpinner_repulsion_multiplier.getValue();
        maxIterations = (Integer) this.jSpinner_max_iterations.getValue();
        lowTemp = (Double) this.jSpinner_low_temperature.getValue();
        minEdgeLength = (Double) this.jSpinner_min_edge_length.getValue();
        edgeMultiplier = (Double) this.jSpinner_preferredEdgeLength_multiplier.getValue();
    }

    public int getOption() {
        ScreenSize screen = new ScreenSize();
        int screenHeight = (int) screen.getheight();
        int screenWidth = (int) screen.getwidth();
        setLocation((screenWidth / 2) -
                getSize().width / 2,
                (screenHeight / 2) - getSize().height / 2);
        setVisible(true);
        return this.selectOption;
    }    // Variables declaration - do not modify
//    private javax.swing.JButton btnOk;
//    private javax.swing.JButton btnCancel;
//    private javax.swing.JButton btnReset;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSpinner jSpinner_attraction_multiplier;
    private javax.swing.JSpinner jSpinner_low_temperature;
    private javax.swing.JSpinner jSpinner_max_iterations;
    private javax.swing.JSpinner jSpinner_min_edge_length;
    private javax.swing.JSpinner jSpinner_preferredEdgeLength_multiplier;
    private javax.swing.JSpinner jSpinner_repulsion_multiplier;
    private JLabel jLabelShowInnerNode;
    private JLabel jLabelRegardVertexDim;
    private JCheckBox jCheckBoxShowInnerNode;
    private JCheckBox jCheckBoxRegardVertexDim;
    // End of variables declaration
}
