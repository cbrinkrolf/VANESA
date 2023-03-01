package database.kegg.gui;

import gui.MainWindow;
import gui.tables.MyTable;
import gui.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import pojos.DBColumn;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class KEGGResultWindow {
    private final JOptionPane panel;
    private final List<String[]> map = new ArrayList<>();
    private MyTable table;
    private final JCheckBox checkBox;
    private final JCheckBox autoCoarse;
    private int valuesCount = 0;

    public KEGGResultWindow(ArrayList<DBColumn> result) {
        for (DBColumn column : result) {
            String[] resultDetails = column.getColumn();
            String[] details = {
                    resultDetails[0] != null ? resultDetails[0] : "", // pathway name
                    resultDetails[1] != null ? resultDetails[1] : "map", // pathway title
                    resultDetails[2] != null ? resultDetails[2] : "map" // pathway name long
            };
            map.add(details);
            valuesCount++;
        }

        Object[][] rows = new Object[valuesCount][3];
        for (int i = 0; i < map.size(); i++) {
            rows[i][0] = false;
            rows[i][1] = map.get(i)[1];
            rows[i][2] = map.get(i)[2];
        }
        initTable(rows, new String[]{"Selected", "Title", "Organism"});
        JScrollPane sp = new JScrollPane(table);
        MigLayout layout = new MigLayout();
        JPanel mainPanel = new JPanel(layout);
        checkBox = new JCheckBox("Search MirBase/TarBase for possibly connected microRNAs", false);
        autoCoarse = new JCheckBox("Coarse MirBase/TarBase results to their associated node", false);
        autoCoarse.setEnabled(false);
        checkBox.addActionListener(arg0 -> {
            if (checkBox.isSelected()) {
                autoCoarse.setEnabled(true);
            } else {
                autoCoarse.setSelected(false);
                autoCoarse.setEnabled(false);
            }
        });
        mainPanel.add(new JLabel("Following Pathways have been found. Please select the pathways of interest"), "");
        mainPanel.add(new JSeparator(), "gap 10, wrap, growx");
        mainPanel.add(sp, "span 2, growx");
        mainPanel.add(new JSeparator(), "gap 10, wrap, growx");
        mainPanel.add(checkBox);
        mainPanel.add(new JSeparator(), "gap 10, wrap, growx");
        mainPanel.add(autoCoarse);
        panel = new JOptionPane(mainPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    }

    public List<String[]> getAnswer() {
        JDialog dialog = panel.createDialog(MainWindow.getInstance().getFrame(), "");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        Integer value = (Integer) panel.getValue();
        if (value == null || value.equals(JOptionPane.UNINITIALIZED_VALUE) || value.equals(JOptionPane.CANCEL_OPTION))
            return null;
        List<String[]> result = new ArrayList<>();
        for (int i = 0; i < valuesCount; i++) {
            if ((Boolean) table.getValueAt(table.convertRowIndexToView(i), table.convertColumnIndexToView(0))) {
                String organism = table.getValueAt(table.convertRowIndexToView(i), table.convertColumnIndexToView(2)).toString();
                String title = table.getValueAt(table.convertRowIndexToView(i), table.convertColumnIndexToView(1)).toString();
                for (String[] details : map) {
                    if (details[1].equals(title) && details[2].equals(organism)) {
                        result.add(details);
                    }
                }
            }
        }
        return result;
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public boolean getAutoCoarse() {
        return autoCoarse.isSelected();
    }

    private void initTable(Object[][] rows, String[] columNames) {
        NodePropertyTableModel model = new NodePropertyTableModel(rows, columNames) {
            private static final long serialVersionUID = 1L;

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 0;
            }
        };
        table = new MyTable();
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnControlVisible(false);
        table.addHighlighter(new ColorHighlighter());
        table.setHorizontalScrollEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getColumn(0).setMaxWidth(50);
        table.getColumn(columNames[0]).setCellRenderer(table.getDefaultRenderer(Boolean.class));
        if (valuesCount == 1) {
            table.setValueAt(true, 0, 0);
        }
    }
}
