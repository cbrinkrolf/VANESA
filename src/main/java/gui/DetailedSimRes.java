package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import io.SuffixAwareFilter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import graph.animations.RegulationTableModel;
import gui.tables.MyTable;
import io.SaveDialog;
import net.miginfocom.swing.MigLayout;
import petriNet.PlotsPanel;
import petriNet.Series;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;

public class DetailedSimRes implements ActionListener {
    private final JFrame dialog;
    private final Pathway pw;
    private final String simId;

    public DetailedSimRes(Pathway pw, String simId) {
        this.pw = pw;
        if (simId == null) {
            simId = pw.getPetriPropertiesNet().getSimResController().getLastActive().getId();
        }
        this.simId = simId;
        final MyTable table = new MyTable();
        table.setModel(this.getTableModel(simId));
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnControlVisible(false);
        table.setHighlighters(HighlighterFactory.createSimpleStriping());
        table.setFillsViewportHeight(true);
        table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
        table.setHorizontalScrollEnabled(true);
        table.getTableHeader().setReorderingAllowed(true);
        table.getTableHeader().setResizingAllowed(true);
        table.getColumn("Label").setPreferredWidth(100);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        final JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(600, 200));

        final JPanel dialogPanel = new JPanel(new MigLayout("fill", "", "[][][][][grow][]"));
        dialogPanel.add(new JLabel("Results for each Timestep t and for all Places:"), "span 2");
        dialogPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
        dialogPanel.add(sp, "span 4, growx, wrap");

        final JButton exportSimResult = new JButton("Export Simulation Result");
        exportSimResult.setActionCommand("exportSimResult");
        exportSimResult.addActionListener(this);

        dialogPanel.add(exportSimResult, "wrap");

        dialogPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");

        // draw a new plot according to the current time step selection
        final PlotsPanel pp = new PlotsPanel(simId);
        dialogPanel.add(pp, "span 4, grow, wrap");
        JButton button = new JButton("Save results to folder");
        button.addActionListener(pp);

        dialogPanel.add(button);

        // show table containing all data
        dialog = new JFrame("Simulation results");
        dialog.setTitle("Simulation Results");
        dialog.setResizable(true);
        dialog.setContentPane(dialogPanel);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
        dialog.pack();
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
    }

    private RegulationTableModel getTableModel(String simId) {
        final SimulationResult simRes = pw.getPetriPropertiesNet().getSimResController().get(simId);
        int rowsSize = pw.getPlaceCount();
        int rowsDim = simRes.getTime().size();
        // get Data from all Places
        final Object[][] rows = new Object[rowsSize][rowsDim + 1];
        int i = 0;
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            if (!(bna instanceof Place) || bna.isLogical()) {
                continue;
            }
            Series series = simRes.get(bna, SimulationResultController.SIM_TOKEN);
            rows[i][0] = bna.getName();
            for (int j = 1; j <= rowsDim; j++) {
                if (series != null && series.size() > j - 1) {
                    rows[i][j] = Math.max(0, series.get(j - 1));
                } else {
                    rows[i][j] = "-";
                }
            }
            i++;
        }
        // create column labels for table view
        final String[] columnNames = new String[rowsDim + 1];
        // String selectorValues[] = new String[rowsSize];
        columnNames[0] = "Label";
        for (i = 0; i < rowsDim; i++) {
            columnNames[i + 1] = "t=" + simRes.getTime().get(i);
        }
        return new RegulationTableModel(rows, columnNames);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String event = e.getActionCommand();
        if (event.equals("exportSimResult")) {
            new SaveDialog(new SuffixAwareFilter[]{SuffixAwareFilter.VANESA_SIM_RESULT},
                    SaveDialog.DATA_TYPE_SIMULATION_RESULTS, null, dialog, simId);
        }
    }
}
