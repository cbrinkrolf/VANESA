package graph.algorithms.gui.clusters;

import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClusterColorEditor extends AbstractCellEditor
                         implements TableCellEditor,
			            ActionListener {
    /**
	 * generated UID
	 */
	private static final long serialVersionUID = 2053300989603187950L;
	Color currentColor;
    JButton button;
    JColorChooser colorChooser;
    JDialog dialog;
    protected static final String EDIT = "edit";

    public ClusterColorEditor() {
        //Set up the editor (from the table's point of view),
        //which is a button.
        //This button brings up the color chooser dialog,
        //which is the editor from the user's point of view.
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);

        //Set up the dialog that the button brings up.
        colorChooser = new JColorChooser();
        dialog = JColorChooser.createDialog(button,
                                        "Pick a Color",
                                        true,  //modal
                                        colorChooser,
                                        this,  //OK button handler
                                        null); //no CANCEL button handler
    }

    /**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            button.setBackground(currentColor);
            colorChooser.setColor(currentColor);
            dialog.setVisible(true);

            //Make the renderer reappear.
            fireEditingStopped();

        } else { //User pressed dialog's "OK" button.
            currentColor = colorChooser.getColor();
        }
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return currentColor;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        currentColor = (Color)value;
        return button;
    }
}

