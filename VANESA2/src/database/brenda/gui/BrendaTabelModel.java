package database.brenda.gui;

import miscalleanous.tables.NodePropertyTableModel;
import database.brenda.MoleculeBox;
import database.brenda.MoleculeBoxSingelton;


public class BrendaTabelModel extends NodePropertyTableModel{

	private MoleculeBox box = MoleculeBoxSingelton.getInstance();
	
	public BrendaTabelModel(Object[][] rows, String[] headers) {
		super(rows, headers);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Class getColumnClass(int c) {
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
