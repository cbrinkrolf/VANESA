package gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class CustomTableCellRenderer extends JLabel implements TableCellRenderer{

	public CustomTableCellRenderer() {
		// TODO Auto-generated constructor stub
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
	    if (isSelected) {
            // cell (and perhaps other cells) are selected
        }

        if (hasFocus) {
            // this cell is the anchor and the table has the focus
        }

        // Configure the component with the specified value
        setText(value.toString());

        // Set tool tip if desired
        setToolTipText("");

        // Since the renderer is a component, return itself
        return this;
				
	}
	
	 @Override
	public void validate() {}
     @Override
	public void revalidate() {}
     @Override
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
     @Override
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

}
