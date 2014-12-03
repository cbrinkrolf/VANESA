package graph.animations;

import javax.swing.table.DefaultTableModel;


public class RegulationTabelModel extends DefaultTableModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RegulationTabelModel(Object[][] rows, String[] headers) {
		super(rows, headers);
	}
	
	@Override
	public Class<?> getColumnClass(int c) {
		if (c == 0) return String.class;
		else return Integer.class;
    }

	/* public void setValueAt(Object value, int row, int col) {
	     super.setValueAt(value, row, col);
	     ((BiologicalNodeAbstract)nodes.get(row)).setAnimationValue(col,(Integer)value);
	}*/
	
	@Override
	public boolean isCellEditable(int row, int col) {
		 	if (col == 0) {
		        return false;
		    } else {
		        return true;
		    }		
	 }
}
