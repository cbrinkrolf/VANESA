package database.brenda.gui;

import database.brenda.MoleculeBox;
import miscalleanous.tables.NodePropertyTableModel;


public class BrendaTabelModel extends NodePropertyTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MoleculeBox box = MoleculeBox.getInstance();
	
	public BrendaTabelModel(Object[][] rows, String[] headers) {
		super(rows, headers);
	}
	
	@Override
	public Class<?> getColumnClass(int c) {
		if (c == 2)
			return Boolean.class;
		else if (c == 0)
			return Integer.class;
		return String.class;
    }

	 @Override
	public void setValueAt(Object value, int row, int col) {
	        super.data[row][col] = value;
	        box.changeValues(data[row][1].toString(),(Boolean)value);
	        fireTableCellUpdated(row, col);
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		 	if (col == 2) {
		        return true;
		    } else {
		        return false;
		    }
			
	 }
}
