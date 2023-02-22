package database.gui;

import gui.MainWindow;
import gui.tables.MyTable;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public abstract class SearchResultWindow<T> {
    private final Class<T> entryType;
    private final JDialog dialog;
    protected final T[] tableValues;
    protected final MyTable table;
    private boolean ok;

    protected SearchResultWindow(Class<T> entryType, String[] columnNames, T[] tableValues) {
        this.entryType = entryType;
        this.tableValues = tableValues;
        table = new MyTable();
        table.setModel(new AbstractTableModel() {
            @Override
            public String getColumnName(int col) {
                return columnNames[col];
            }

            @Override
            public int getRowCount() {
                return tableValues.length;
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (rowIndex >= 0 && rowIndex < tableValues.length && columnIndex >= 0 &&
                        columnIndex < columnNames.length) {
                    return getTableValueColumnAt(tableValues[rowIndex], columnIndex);
                }
                return null;
            }
        });
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnControlVisible(false);
        table.addHighlighter(HighlighterFactory.createSimpleStriping());
        table.setFillsViewportHeight(true);
        table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
        table.setHorizontalScrollEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
        table.setRowSelectionInterval(0, 0);
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(800, 400));
        MigLayout layout = new MigLayout();
        JPanel mainPanel = new JPanel(layout);
        layoutMainPanelBeforeTable(mainPanel);
        mainPanel.add(new JSeparator(), "gap 10, wrap, growx");
        mainPanel.add(sp, "span 2, growx");
        layoutMainPanelAfterTable(mainPanel);
        JOptionPane panel = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> onCancelClickedInternal());
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(e -> onOkClickedInternal());
        panel.setOptions(new JButton[]{okButton, cancel});
        dialog = new JDialog(new JFrame(), "", true);
        dialog.setContentPane(panel);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    protected abstract Object getTableValueColumnAt(T value, int columnIndex);

    protected abstract void layoutMainPanelBeforeTable(JPanel mainPanel);

    protected abstract void layoutMainPanelAfterTable(JPanel mainPanel);

    private void onCancelClickedInternal() {
        dialog.setVisible(false);
        onCancelClicked();
        ok = false;
    }

    private void onOkClickedInternal() {
        if (onOkClicked()) {
            dialog.setVisible(false);
            ok = true;
        }
    }

    public boolean show() {
        dialog.pack();
        dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
        dialog.setVisible(true);
        return ok;
    }

    protected abstract boolean onOkClicked();

    protected abstract void onCancelClicked();

    public T[] getSelectedValues() {
        List<T> result = new ArrayList<>();
        for (int selectedRow : table.getSelectedRows()) {
            result.add(tableValues[selectedRow]);
        }
        //noinspection unchecked
        return result.toArray((T[]) Array.newInstance(entryType, 0));
    }
}
