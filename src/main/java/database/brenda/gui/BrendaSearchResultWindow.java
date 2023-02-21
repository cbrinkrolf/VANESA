package database.brenda.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import gui.MainWindow;
import gui.tables.MyTable;
import gui.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;

public class BrendaSearchResultWindow implements ActionListener {
    private final JSpinner searchDepth;
    private final JCheckBox organismSpecificBox = new JCheckBox();
    private boolean ok = false;
    private final JDialog dialog;
    private final JCheckBox disregard = new JCheckBox();
    private final JCheckBox inhibitorBox = new JCheckBox();
    private final JCheckBox coFactorBox = new JCheckBox();
    private final JCheckBox autoCoarseDepth = new JCheckBox();
    private final JCheckBox autoCoarseEnzymeNomenclature = new JCheckBox();

    private final JTextField field = new JTextField(10);
    private TableRowSorter<NodePropertyTableModel> sorter;

    private MyTable table;

    public BrendaSearchResultWindow(String[][] result) {
        String[] columNames = {"Enzyme", "Name", "reaction", "organism"};
        initTable(result, columNames);
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(800, 400));
        MigLayout layout = new MigLayout();
        JPanel mainPanel = new JPanel(layout);
        organismSpecificBox.setSelected(true);
        inhibitorBox.setSelected(false);
        coFactorBox.setSelected(false);
        mainPanel.add(new JLabel("Following enzymes have been found. Please select the enzymes of interest"), "span 2");
        mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
        mainPanel.add(sp, "span 4, growx");
        mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
        mainPanel.add(new JLabel("What kind of settings do you wish to apply to the calculation?"), "span 2, wrap 15 ");
        SpinnerNumberModel model1 = new SpinnerNumberModel(1, 1, 20, 1);
        searchDepth = new JSpinner(model1);
        mainPanel.add(new JLabel("Search Depth"), "span 1, gaptop 2 ");
        mainPanel.add(searchDepth, "span 1,wrap,gaptop 2");
        mainPanel.add(new JLabel("Organism specific calculation"), "span 1, gaptop 2 ");
        mainPanel.add(organismSpecificBox, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Include Inhibitors"), "span 1, gaptop 2 ");
        mainPanel.add(inhibitorBox, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Include coFactors"), "span 1, gaptop 2 ");
        mainPanel.add(coFactorBox, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Disregard Currency Metabolites"), "span 1, gaptop 2 ");
        mainPanel.add(disregard, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Coarse all results of the same query."), "span 1, gaptop 2 ");
        mainPanel.add(autoCoarseDepth, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Coarse enzyme due to their Enzyme nomenclature (EC-Number)."), "span 1, gaptop 2 ");
        mainPanel.add(autoCoarseEnzymeNomenclature, "span 1,wrap,gaptop 2");
        field.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (field.getText().trim().length() < 1) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter(field.getText().trim()));
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        mainPanel.add(new JLabel("Filter results"), "span 1, gaptop 2");
        mainPanel.add(field, "span 1,wrap,gaptop 2");
        ActionListener coarseListener = arg0 -> {
            if (autoCoarseEnzymeNomenclature.isSelected()) {
                autoCoarseDepth.setSelected(false);
                autoCoarseDepth.setEnabled(false);
            } else if (autoCoarseDepth.isSelected()) {
                autoCoarseEnzymeNomenclature.setSelected(false);
                autoCoarseEnzymeNomenclature.setEnabled(false);
            } else {
                autoCoarseEnzymeNomenclature.setEnabled(true);
                autoCoarseDepth.setEnabled(true);
            }
        };
        autoCoarseEnzymeNomenclature.addActionListener(coarseListener);
        autoCoarseDepth.addActionListener(coarseListener);
        mainPanel.add(new JSeparator(), "span, growx, gaptop 7");
        JButton cancel = new JButton("cancel");
        cancel.addActionListener(this);
        cancel.setActionCommand("cancel");
        JButton newButton = new JButton("ok");
        newButton.addActionListener(this);
        newButton.setActionCommand("new");
        disregard.addActionListener(this);
        disregard.setActionCommand("disregard");
        JOptionPane optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
        JButton[] buttons = {newButton, cancel};
        optionPane.setOptions(buttons);
        dialog = new JDialog(new JFrame(), "Settings", true);
        dialog.setContentPane(optionPane);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public Vector<String[]> getAnswer() {
        dialog.pack();
        dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
        dialog.setVisible(true);
        Vector<String[]> v = new Vector<>();
        if (ok) {
            String tempEnzyme = "";
            int[] selectedRows = table.getSelectedRows();
            for (int selectedRow : selectedRows) {
                String enzymes = table.getValueAt(selectedRow, 0).toString();
                String organism = table.getValueAt(selectedRow, 3).toString();
                String[] details = {enzymes, organism};
                if (organismSpecificBox.isSelected() || !tempEnzyme.equals(enzymes)) {
                    v.add(details);
                }
                tempEnzyme = enzymes;
            }
        }
        return v;
    }

    private void initTable(Object[][] rows, String[] columNames) {
        NodePropertyTableModel model = new NodePropertyTableModel(rows, columNames);
        table = new MyTable();
        sorter = new TableRowSorter<>(model);
        table.setModel(model);
        sorter.setRowFilter(null);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnControlVisible(false);
        table.setHighlighters(HighlighterFactory.createSimpleStriping());
        table.setFillsViewportHeight(true);
        table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
        table.setHorizontalScrollEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
        table.setRowSelectionInterval(0, 0);
    }

    public Integer getSearchDepth() {
        try {
            searchDepth.commitEdit();
        } catch (java.text.ParseException ignored) {
        }
        return (Integer) searchDepth.getValue();
    }

    public boolean getOrganismSpecificDecision() {
        return organismSpecificBox.isSelected();
    }

    public boolean getInhibitorsDecision() {
        return inhibitorBox.isSelected();
    }

    public boolean getCoFactorsDecision() {
        return coFactorBox.isSelected();
    }

    public boolean getDisregarded() {
        return disregard.isSelected();
    }

    public boolean getAutoCoarseDepth() {
        return autoCoarseDepth.isSelected();
    }

    public boolean getAutoCoarseEnzymeNomenclature() {
        return autoCoarseEnzymeNomenclature.isSelected();
    }

    public void actionPerformed(ActionEvent e) {
        String event = e.getActionCommand();
        if ("cancel".equals(event)) {
            dialog.setVisible(false);
            MainWindow.getInstance().closeProgressBar();
        } else if ("new".equals(event)) {
            if (table.getSelectedRows().length == 0) {
                JOptionPane.showMessageDialog(null, "Please choose an enzyme.", "Message", JOptionPane.INFORMATION_MESSAGE);
            } else {
                ok = true;
                dialog.setVisible(false);
            }
        } else if ("disregard".equals(event)) {
            if (disregard.isSelected()) {
                new BrendaPatternListWindow();
            }
        }
    }
}
