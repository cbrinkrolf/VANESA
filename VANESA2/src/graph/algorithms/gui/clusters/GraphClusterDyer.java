package graph.algorithms.gui.clusters;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindowSingleton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.jfree.ui.RefineryUtilities;

import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * The GraphClusterDyer applies colors to given BiologicalNodeAbstract_s
 * depending on the dataset given.
 * 
 * @author Martin Mai 2015
 */
public class GraphClusterDyer extends JFrame {

	/**
	 * generated UID
	 */
	private static final long serialVersionUID = -5481882713834668420L;

	final boolean DEBUG = true;

	private final int X = 800, Y = 600;
	private Object[][] data;

	final int SCORE = 0, LABELS = 1, COLOR = 2;
	
	private MyGraph mg;
	
	public Color GREY = new Color(-4144960);
	

	public GraphClusterDyer(TreeMap<Double, TreeSet<String>> dataset) {
		super("Cluster Dyer: "+MainWindowSingleton.getInstance().getCurrentPathway());
		setPreferredSize(new Dimension(X, Y));
		mg = GraphInstance.getMyGraph();

		TablePanel newContentPane = new TablePanel(dataset);
		newContentPane.setOpaque(true); // content panes must be opaque
		setContentPane(newContentPane);

		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// setResizable(false);

	}

	public class TablePanel extends JPanel {
		/**
		 * generated UID
		 */
		private static final long serialVersionUID = 7594693595853651596L;

		public TablePanel(TreeMap<Double, TreeSet<String>> dataset) {
			super(new GridLayout(1, 0));

			String[] columnNames = { "Score", "Labels", "Color" };

			data = new Object[dataset.size()][columnNames.length];

			int linecounter = dataset.size() - 1;
			for (Entry<Double, TreeSet<String>> entry : dataset.entrySet()) {
				if ((entry.getKey() + "").length() > 5)
					data[linecounter][SCORE] = entry.getKey().toString()
							.substring(0, 5);
				else
					data[linecounter][SCORE] = entry.getKey();

				data[linecounter][LABELS] = entry.getValue();
				data[linecounter][COLOR] = new Color(-4144960);

				linecounter--;
			}

			ClusterTableModel tablemodel = new ClusterTableModel(columnNames,
					data);
			JTable table = new JTable(tablemodel);
			// JTable table = new JTable(data,columnNames);
			table.setPreferredScrollableViewportSize(new Dimension(500, 70));
			table.setFillsViewportHeight(true);

			table.setFont(new Font("Arial", Font.PLAIN, 14));
			table.setRowHeight(table.getRowHeight() + 5);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.getColumnModel().getColumn(0).setPreferredWidth(50);
			table.getColumnModel().getColumn(1).setPreferredWidth(680);
			table.getColumnModel().getColumn(2).setPreferredWidth(50);

			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
	        //Set up renderer and editor for the Favorite Color column.
	        table.setDefaultRenderer(Color.class,
	                                 new ClusterColorRenderer(true));
	        table.setDefaultEditor(Color.class,
	                               new ClusterColorEditor());

			// Create the scroll pane and add the table to it.
			JScrollPane scrollPane = new JScrollPane(table);

			// Add the scroll pane to this panel.
			add(scrollPane);
		}

	}

	class ClusterTableModel extends AbstractTableModel {
		/**
		 * generated UID
		 */
		private static final long serialVersionUID = -6041132360721123994L;
		private String[] columnNames;
		private Object[][] data;

		public ClusterTableModel(String[] columnNames, Object[][] data) {
			this.columnNames = columnNames;
			this.data = data;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/*
		 * JTable uses this method to determine the default renderer/ editor for
		 * each cell. If we didn't implement this method, then the last column
		 * would contain text ("true"/"false"), rather than a check box.
		 */
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {

			if (col == 2)
				return true;
			else
				return false;
		}

		/*
		 * Don't need to implement this method unless your table's data can
		 * change.
		 */
		public void setValueAt(Object value, int row, int col) {
			if (DEBUG) {
				System.out.println("Setting value at " + row + "," + col
						+ " to " + value + " (an instance of "
						+ value.getClass() + ")");
			}

			
			
			data[row][col] = value;
			fireTableCellUpdated(row, col);
			if(col == COLOR)
				colorClusterNodes(value, data[row][LABELS]);
			

		}

		private void colorClusterNodes(Object newcolor, Object labelstopaint) {
			
			Color color = (Color) newcolor;
			TreeSet<String> labels = (TreeSet<String>) labelstopaint;
			
			
			for (BiologicalNodeAbstract bna : mg.getAllVertices()){
				if(labels.contains(bna.getLabel())){
					if(bna.getColor().equals(GREY))
						bna.setColor(color);
					else
						bna.setColor(new Color(bna.getColor().getRGB()/2));
				}								
			}

			GraphInstance.getMyGraph().getVisualizationViewer().repaint();
			
		}

		private void printDebugData() {
			int numRows = getRowCount();
			int numCols = getColumnCount();

			for (int i = 0; i < numRows; i++) {
				System.out.print("    row " + i + ":");
				for (int j = 0; j < numCols; j++) {
					System.out.print("  " + data[i][j]);
				}
				System.out.println();
			}
			System.out.println("--------------------------");
		}
	}

}
