package graph.algorithms;

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

import org.jfree.ui.RefineryUtilities;

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
	
	private final int X=800,Y=600;

	public GraphClusterDyer(TreeMap<Double, TreeSet<Integer>> dataset) {
		super("Cluster Dyer");
		setPreferredSize(new Dimension(X,Y));

		
		TablePanel newContentPane = new TablePanel(dataset);
		newContentPane.setOpaque(true); // content panes must be opaque
		setContentPane(newContentPane);


		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}

	public static void main(String[] args) {

		// dataset
		TreeMap<Double, TreeSet<Integer>> dataset = new TreeMap<>();
		final int SETSIZE = 100;
		double avgsize = 0;
		// individual clusterset
		TreeSet<Integer> tmp;
		for (int i = 0; i < SETSIZE; i++) {
			tmp = new TreeSet<Integer>();
			int size = (int) (Math.random() * 5) + 2;
			avgsize += size;
			for (int j = 0; j < size; j++) {
				int tmpnode = (int) (Math.random() * 101);

				if (!tmp.contains(tmpnode)) {
					tmp.add(tmpnode);
				}
			}

			double tmpscore = Math.random() * 100;
//			System.out.printf("putting : %f, %s\n", tmpscore, tmp.toString());
			dataset.put(tmpscore, tmp);
		}

		System.out.printf("avgsize: %f\n", avgsize / SETSIZE);

		new GraphClusterDyer(dataset);

	}

	public class TablePanel extends JPanel {
		/**
		 * generated UID
		 */
		private static final long serialVersionUID = 7594693595853651596L;
		private boolean DEBUG = true;

		public TablePanel(TreeMap<Double, TreeSet<Integer>> dataset) {
			super(new GridLayout(1, 0));

			String[] columnNames = { "Score", "Labels", "Color"};

			Object[][] data = new Object[dataset.size()][columnNames.length];
			
			int linecounter = dataset.size()-1;
			for (Entry<Double, TreeSet<Integer>> entry : dataset.entrySet()) {
				if((entry.getKey()+"").length() >5)
					data[linecounter][0] = entry.getKey().toString().substring(0, 5);
				else
					data[linecounter][0] = entry.getKey();
				
				data[linecounter][1] = entry.getValue().toString();
				data[linecounter][2] = (Math.random()+"").substring(0, 5);
				linecounter--;
			}
			
			
			final JTable table = new JTable(data, columnNames);
			table.setPreferredScrollableViewportSize(new Dimension(500, 70));
			table.setFillsViewportHeight(true);
			table.setFont(new Font("Arial", Font.PLAIN, 18));
			table.setRowHeight(table.getRowHeight()+14);
			

			if (DEBUG) {
				table.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						printDebugData(table);
					}
				});
			}

			// Create the scroll pane and add the table to it.
			JScrollPane scrollPane = new JScrollPane(table);

			// Add the scroll pane to this panel.
			add(scrollPane);
		}

		private void printDebugData(JTable table) {
			int numRows = table.getRowCount();
			int numCols = table.getColumnCount();
			javax.swing.table.TableModel model = table.getModel();

			System.out.println("Value of data: ");
			for (int i = 0; i < numRows; i++) {
				System.out.print("    row " + i + ":");
				for (int j = 0; j < numCols; j++) {
					System.out.print("  " + model.getValueAt(i, j));
				}
				System.out.println();
			}
			System.out.println("--------------------------");
		}

	}
}
