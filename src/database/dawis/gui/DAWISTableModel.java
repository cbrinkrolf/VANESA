package database.dawis.gui;

import miscalleanous.tables.NodePropertyTableModel;

@SuppressWarnings("serial")
public class DAWISTableModel extends NodePropertyTableModel{

	public DAWISTableModel(Object[][] rows, String[] headers) {
		super(rows, headers);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		if (c == 1)
			return Boolean.class;
		else 
			return String.class;
    }

	 @Override
	public void setValueAt(Object value, int row, int col) {
	        super.data[row][col] = value;
	        fireTableCellUpdated(row, col);
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		 	if (col == 1) {
		        return true;
		    } else {
		        return false;
		    }
			
	 }
}
