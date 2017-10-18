package graph.algorithms.gui.clusters;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 
 * Custom renderer to show experimental values in cluster's cell tooltip
 * 
 * @author mlewinsk May 2015
 */
public class ClusterValueTooltipRenderer extends JLabel implements
		TableCellRenderer {
	/**
	 * generated UID
	 */
	private static final long serialVersionUID = -8026637709290430471L;

	private String[] values;

	public ClusterValueTooltipRenderer(String[] values) {
		this.values = values;

		setOpaque(true); // MUST do this for background to show up.
	}

	public Component getTableCellRendererComponent(JTable table,
			Object treeset, boolean isSelected, boolean hasFocus, int row,
			int column) {

		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else {
			setBackground(table.getBackground());
		}

		setToolTipText(values[row]);
		setText(treeset.toString());

		return this;
	}
}
