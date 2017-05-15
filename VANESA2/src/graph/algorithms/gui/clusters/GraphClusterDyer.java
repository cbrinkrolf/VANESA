package graph.algorithms.gui.clusters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXTable;
import org.jfree.ui.RefineryUtilities;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;

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

	final boolean DEBUG = false;

	private final int X = 800, Y = 600;
	private Object[][] data;

	final int SCORE = 0, LABELS = 1, COLOR = 2;
	
	private MyGraph mg;
	
	public Color GREY = new Color(-4144960);
	

	public GraphClusterDyer(TreeMap<Double, TreeSet<String>> dataset) {
		super("Cluster Dyer: "+MainWindow.getInstance().getCurrentPathway());
		setPreferredSize(new Dimension(X, Y));
		
		mg = GraphContainer.getInstance().getPathway(MainWindow.getInstance().getCurrentPathway()).getGraph();

		HashMap<String, Double> expvalues = new HashMap<>();
		
		NodeAttribute att;
		for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
			if((att = bna.getNodeAttributeByName("CholAVG")) != null){
				expvalues.put(bna.getLabel(),att.getDoublevalue());
			}
		}
		
		String[] values = new String[dataset.size()];
		//iterate over dataset and determine the experimental values

		String valuesentry;
		int linecounter = dataset.size() - 1;
		double avgval;
		for(Entry<Double, TreeSet<String>> e : dataset.entrySet()){
			valuesentry = "";
			avgval = 0;
			
			for(String label : e.getValue()){
				if (expvalues.get(label) != null) {
					valuesentry += expvalues.get(label) + "; ";
					avgval += expvalues.get(label);
				}else
					valuesentry+="null; ";
			}
			
			String avgString = "�"+(avgval/e.getValue().size());
			if(avgString.length()>6)
				valuesentry+=avgString.substring(0, 6);
			else
				valuesentry+=avgString;
			
			values[linecounter] = valuesentry;
			linecounter--;
						
		}
		
		
		
		
				
		TablePanel newContentPane = new TablePanel(dataset,values);
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

		public TablePanel(TreeMap<Double, TreeSet<String>> dataset, String[] values) {
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
			JTable table = new JXTable(tablemodel);
			// JTable table = new JTable(data,columnNames);
			table.setPreferredScrollableViewportSize(new Dimension(500, 70));
			table.setFillsViewportHeight(true);

			table.setFont(new Font("Arial", Font.PLAIN, 14));
			table.setRowHeight(table.getRowHeight() + 5);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.getColumnModel().getColumn(0).setPreferredWidth(50);
			table.getColumnModel().getColumn(1).setPreferredWidth(650);
			table.getColumnModel().getColumn(2).setPreferredWidth(50);

			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
	        //Set up renderer and editor for the Color column.
	        table.setDefaultRenderer(Color.class,
	                                 new ClusterColorRenderer(true));
	        table.setDefaultEditor(Color.class,
	                               new ClusterColorEditor());
	       
	        
	        //Set up renderer for cluster tooltips
	        table.setDefaultRenderer(TreeSet.class, new ClusterValueTooltipRenderer(values));
	        
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

		@SuppressWarnings("unchecked")
		private void colorClusterNodes(Object newcolor, Object labelstopaint) {
			
			Color color = (Color) newcolor;
			TreeSet<String> labels = (TreeSet<String>) labelstopaint;
			
			
			for (BiologicalNodeAbstract bna : mg.getAllVertices()){
				if(labels.contains(bna.getLabel())){
					if(bna.getColor().equals(bna.getDefaultColor()))
						bna.setColor(color);
					else
						bna.setColor(blend(color, bna.getColor()));
				}								
			}

			GraphInstance.getMyGraph().getVisualizationViewer().repaint();
			
		}
		
		 public Color blend(Color c0, Color c1) {
			    double totalAlpha = c0.getAlpha() + c1.getAlpha();
			    double weight0 = c0.getAlpha() / totalAlpha;
			    double weight1 = c1.getAlpha() / totalAlpha;

			    double r = weight0 * c0.getRed() + weight1 * c1.getRed();
			    double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
			    double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
			    double a = Math.max(c0.getAlpha(), c1.getAlpha());

			    return new Color((int) r, (int) g, (int) b, (int) a);
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
