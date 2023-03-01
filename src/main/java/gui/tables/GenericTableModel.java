package gui.tables;

import javax.swing.table.AbstractTableModel;

public abstract class GenericTableModel<T> extends AbstractTableModel {
    private final String[] columns;
    private final T[] rows;

    protected GenericTableModel(String[] columns, T[] rows) {
        this.columns = columns;
        this.rows = rows;
    }

    @Override
    public int getRowCount() {
        return rows.length;
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < rows.length && columnIndex >= 0 && columnIndex < columns.length) {
            return getValueAt(rows[rowIndex], columnIndex);
        }
        return null;
    }

    public abstract Object getValueAt(T entry, int columnIndex);
}
