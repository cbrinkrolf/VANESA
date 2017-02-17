package cluster.clientimpl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXTable;
import org.jfree.ui.RefineryUtilities;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import cluster.master.IClusterJobs;
import cluster.slave.JobTypes;
import graph.ContainerSingelton;
import graph.algorithms.NodeAttributeNames;
import graph.algorithms.NodeAttributeTypes;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

/**
 * The GraphClusterDyer applies colors to given BiologicalNodeAbstract_s
 * depending on the dataset given.
 * 
 * @author Martin Mai 2015
 */
public class ClusterDataUploadWindow extends JFrame {

	/**
	 * generated UID
	 */
	private static final long serialVersionUID = -5481882713834668420L;

	final boolean DEBUG = true;

	private Object[][] data;

	final int TYPE_STR = 0, NAME_STR = 1, COUNT_STR = 2, UPLOAD_BOOL = 3;
	private HashMap<String, Integer> attributes;
	private ArrayList<HashMap<String, Double>> mappingslist;
	private ArrayList<String> attnames;
	private HashMap<String, Double> uniprotmapping;
	private MyGraph mg;
	private JButton uploadButton;

	public Color GREY = new Color(-4144960);

	public ClusterDataUploadWindow() {
		super("Cluster Upload window: "
				+ MainWindow.getInstance().getCurrentPathway());
		// setPreferredSize(new Dimension(X, Y));

		if (MainWindow.getInstance().getCurrentPathway() != null) {

			TablePanel newContentPane = new TablePanel();
			newContentPane.setOpaque(true); // content panes must be opaque
			setContentPane(newContentPane);

			pack();
			RefineryUtilities.centerFrameOnScreen(this);
			setVisible(true);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			// setResizable(false);
		}else
			JOptionPane.showMessageDialog(null, "please load or create a network");

	}

	public class TablePanel extends JPanel {
		/**
		 * generated UID
		 */
		private static final long serialVersionUID = 7594693595853651596L;

		public TablePanel() {
			super(new MigLayout("", "[right]"));

			String[] columnNames = { "Type", "Name", "Count", "Upload" };

			// deterimine experimental datasets on current network
			attributes = new HashMap<String, Integer>();

			mg = ContainerSingelton
					.getInstance()
					.getPathway(
							MainWindow.getInstance()
									.getCurrentPathway()).getGraph();

			for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
				for (NodeAttribute na : bna
						.getNodeAttributesByType(NodeAttributeTypes.EXPERIMENT)) {

					// count mapped nodes with given attribute
					if (!attributes.containsKey(na.getName())) {
						attributes.put(na.getName(), 1);
					} else {
						attributes.put(na.getName(),
								attributes.get(na.getName()) + 1);
					}
				}
			}

			// Initialize Table with Data and summary
			data = new Object[attributes.keySet().size()][columnNames.length];
			TreeSet<String> displayset = new TreeSet<>(attributes.keySet());

			for (int line = 0; line < attributes.keySet().size(); line++) {

				// hard coded for now, can be dynamically chosen in future
				data[line][TYPE_STR] = "Experiment";
				data[line][NAME_STR] = displayset.pollFirst();
				data[line][COUNT_STR] = attributes.get(data[line][NAME_STR])
						+ "/" + mg.getAllVertices().size();
				data[line][UPLOAD_BOOL] = new Boolean(false);

			}

			ClusterTableModel tablemodel = new ClusterTableModel(columnNames,
					data);
			JTable table = new JXTable(tablemodel);
			// JTable table = new JTable(data,columnNames);
			table.setPreferredScrollableViewportSize(new Dimension(600, 180));
			table.setFillsViewportHeight(true);

			table.setFont(new Font("Arial", Font.PLAIN, 14));
			table.setRowHeight(table.getRowHeight() + 5);
			// table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			// table.getColumnModel().getColumn(0).setPreferredWidth(50);
			// table.getColumnModel().getColumn(1).setPreferredWidth(680);
			// table.getColumnModel().getColumn(2).setPreferredWidth(50);

			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			// Create the scroll pane and add the table to it.
			JScrollPane scrollPane = new JScrollPane(table);

			// Add the scroll pane to this panel.
			add(scrollPane, "wrap");

			uploadButton = new JButton("UPLOAD");
			uploadButton.addActionListener(new UploadListener());

			add(uploadButton, "wrap");
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
			if (col == 3)
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

	class UploadListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ClusterDataUploadWindow.this.setVisible(false);

			// determine
			mappingslist = new ArrayList<HashMap<String, Double>>();
			attnames = new ArrayList<String>();
			for (int line = 0; line < attributes.keySet().size(); line++) {

				if ((Boolean) data[line][UPLOAD_BOOL]) {
					uniprotmapping = new HashMap<String, Double>();
					NodeAttribute att, db;
					String id;
					for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
						att = bna
								.getNodeAttributeByName((String) data[line][NAME_STR]);
						if (att != null) {
							db = bna.getNodeAttributeByName(NodeAttributeNames.UNIPROT);
							if (db != null) {
								id = db.getStringvalue();
								id = id.trim();
								if (id.length() > 6)
									id = id.substring(0, 6);

								uniprotmapping.put(id, att.getDoublevalue());
							}
						}

					}

					mappingslist.add(uniprotmapping);
					attnames.add((String) data[line][NAME_STR]);
				}
			}

			// Transfer to arrays
			String[] experimentArray = new String[attnames.size()];
			for (int i = 0; i < attnames.size(); i++)
				experimentArray[i] = attnames.get(i);

			@SuppressWarnings("unchecked")
			HashMap<String, Double>[] mappingsarray = new HashMap[attnames
					.size()];
			for (int i = 0; i < attnames.size(); i++)
				mappingsarray[i] = mappingslist.get(i);

			Thread export = new Thread(new Runnable() {
				@Override
				public void run() {// send over RMI
					if (!mappingslist.isEmpty()) {
						String url = "rmi://cassiopeidae/ClusterJobs";
						IClusterJobs server;
						MappingCallback helper;
						try {

							server = (IClusterJobs) Naming.lookup(url);
							helper = new MappingCallback();
							if (!server.submitMapping(JobTypes.MAPPING_UNIPROT,
									experimentArray, mappingsarray, helper)) {
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										JOptionPane.showMessageDialog(
												MainWindow
														.getInstance(),
												"Queue is at maximum capacity!");
									}
								});
							}

						} catch (NotBoundException nbe) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									JOptionPane
											.showMessageDialog(
													MainWindow
															.getInstance()
															.returnFrame(),
													"RMI Interface could not be established.",
													"Error",
													JOptionPane.ERROR_MESSAGE);
								}
							});
							nbe.printStackTrace();

						} catch (RemoteException re) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									JOptionPane.showMessageDialog(
											MainWindow.getInstance()
													.returnFrame(),
											"Cluster not reachable.", "Error",
											JOptionPane.ERROR_MESSAGE);
								}
							});
							re.printStackTrace();

						} catch (MalformedURLException mue) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									JOptionPane
											.showMessageDialog(
													MainWindow
															.getInstance()
															.returnFrame(),
													"Clusteradress could not be resolved.",
													"Error",
													JOptionPane.ERROR_MESSAGE);
								}
							});
							mue.printStackTrace();

						}

					}

				}
			});

			// lock UI
			MainWindow.getInstance().showProgressBar(
					"submitting data to cluster");

			// Start export
			export.start();
			
			ClusterDataUploadWindow.this.dispose();;
		}
	}
}
