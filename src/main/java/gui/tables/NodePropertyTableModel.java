package gui.tables;

import javax.swing.table.AbstractTableModel;

public class NodePropertyTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -7174001672428120676L;

    private final String[] columnNames;
    protected final Object[][] data;

    public NodePropertyTableModel(Object[][] rows, String[] headers) {
        this.columnNames = headers;
        this.data = rows;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (c >= 0 && c < getColumnCount()) {
            return getValueAt(0, c).getClass();
        }
        return Object.class;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
}
