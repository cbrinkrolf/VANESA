package graph.animations;

import java.util.Hashtable;

import javax.swing.table.DefaultTableModel;


public class RegulationTabelModel extends DefaultTableModel{
	
	private Hashtable nodes;
	
	public RegulationTabelModel(Object[][] rows, String[] headers, Hashtable nodes) {
		super(rows, headers);
		this.nodes = nodes;
	}
	
	@Override
	public Class getColumnClass(int c) {
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
