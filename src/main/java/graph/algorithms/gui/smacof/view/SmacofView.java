package graph.algorithms.gui.smacof.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import configurations.Workspace;
import graph.VanesaGraph;
import org.jdesktop.swingx.JXTable;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import graph.GraphInstance;
import graph.algorithms.NodeAttributeType;
import graph.algorithms.gui.smacof.DoSmacof;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

public class SmacofView extends JFrame implements ActionListener {
	private static final long serialVersionUID = -1342672281173544345L;

	private final VanesaGraph graph;

	private final JPanel panelparam = new JPanel();
	// Thread that executes the SMACOF algorithm
	private DoSmacof dosmacof = null;

	private JComboBox<String> choosedismeasure;
	private JButton startcomputationbutton;
	private JButton stopcomputationbutton;
	private JSlider slidermaxiter;
	private JSlider sliderepsilon;
	private JSlider sliderp;
	private JSlider sliderresultdim;
	private JLabel labelmaxiter;
	private JLabel labelepsilon;
	private JLabel labelp;
	private JLabel labelresultdim;
	private static JLabel labelcuriteration = null;
	private static JLabel labelcurepsilon = null;
	private static JLabel labelcurtime = null;
	private final String[] dismeasure = { "EUCLIDEAN", "MINKOWSKI",
			// no support	"NONE",
			//			"MAHALANOBIS",
			"CANBERRA",
			//		    "DIVERGENCE",
			//		    "BRAY_CURTIS",
			//		    "SOERGEL",
			//		    "BAHATTACHARYYA",
			//		    "WAVE_HEDGES",
			"ANGULAR_SEPERATION", "CORRELATION" };
	private final HashMap<Integer, double[]> smacof_data_map = new HashMap<>();
	private final HashMap<BiologicalNodeAbstract, Integer> mapped_nodes = new HashMap<>();
	private final HashMap<Integer, BiologicalNodeAbstract> mapped_nodes_backwards = new HashMap<>();
	private final HashMap<String, Integer> attributes = new HashMap<>();
	private Object[][] tabledata;
	private final String[] tableColumnNames = { "Type", "Name", "Count", "Evaluate" };
	final int TYPE_STR = 0, NAME_STR = 1, COUNT_STR = 2, UPLOAD_BOOL = 3;

	public SmacofView() {
		super("SMACOF: " + MainWindow.getInstance().getCurrentPathway());
		int x = 600;
		int y = 800;
		setPreferredSize(new Dimension(x, y));
		this.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		graph = GraphInstance.getGraph();

		if (graph != null) {
			try {
				// Hier werden die Daten aus dem Graphen gelesen
				for (BiologicalNodeAbstract bna : graph.getNodes()) {
					for (NodeAttribute na : bna.getNodeAttributesByType(NodeAttributeType.EXPERIMENT)) {
						// count mapped nodes with given attribute
						if (!attributes.containsKey(na.getName())) {
							attributes.put(na.getName(), 1);
						} else {
							attributes.put(na.getName(), attributes.get(na.getName()) + 1);
						}
					}
					// Martin
					for (NodeAttribute na : bna.getNodeAttributesByType(NodeAttributeType.GRAPH_PROPERTY)) {
						// count mapped nodes with given attribute
						if (!attributes.containsKey(na.getName())) {
							attributes.put(na.getName(), 1);
						} else {
							attributes.put(na.getName(), attributes.get(na.getName()) + 1);
						}
					}

				}
				// tabledata fuer die Datentabelle anlegen und befuellen
				tabledata = new Object[attributes.size()][tableColumnNames.length];
				TreeSet<String> displayset = new TreeSet<>(attributes.keySet());
				for (int line = 0; line < attributes.size(); line++) {
					// hard coded for now, can be dynamically chosen in future
					tabledata[line][TYPE_STR] = "Experiment";
					tabledata[line][NAME_STR] = displayset.pollFirst();
					tabledata[line][COUNT_STR] = attributes.get(tabledata[line][NAME_STR]) + "/" + graph.getNodeCount();
					tabledata[line][UPLOAD_BOOL] = true;

				}

				initPanel();
				this.setLayout(new BorderLayout(5, 5));
				this.add(panelparam, BorderLayout.NORTH);
				SmacofTablePanel tablepanel = new SmacofTablePanel();
				tablepanel.setOpaque(true);
				this.add(tablepanel, BorderLayout.SOUTH);
				pack();
				this.setLocationRelativeTo(MainWindow.getInstance().getFrame());
				setVisible(true);
				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			} catch (IndexOutOfBoundsException ex) {
				JOptionPane.showMessageDialog(null, "Network contains no additional attributes.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Please open a network first.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void initPanel() {
		choosedismeasure = new JComboBox<>(dismeasure);
		choosedismeasure.setActionCommand("dismeasure");
		choosedismeasure.addActionListener(this);

		slidermaxiter = new JSlider(0, 10000, 1000);
		slidermaxiter.setPaintTicks(true);
		slidermaxiter.setMinorTickSpacing(10);
		labelmaxiter = new JLabel("max. iterations: " + slidermaxiter.getValue());
		slidermaxiter.addChangeListener(
				e -> labelmaxiter.setText("max. iterations: " + ((JSlider) e.getSource()).getValue()));

		sliderepsilon = new JSlider(-15, 10, -5);
		sliderepsilon.setPaintTicks(true);
		sliderepsilon.setMinorTickSpacing(1);
		labelepsilon = new JLabel("epsilon: " + Math.pow(10, sliderepsilon.getValue()));
		sliderepsilon.addChangeListener(e -> {
			double myepsilon;
			double tmp = ((JSlider) e.getSource()).getValue();
			if (tmp >= 0) {
				myepsilon = tmp;
			} else {
				myepsilon = Math.pow(10, tmp);
			}
			labelepsilon.setText("epsilon: " + myepsilon);
		});

		sliderp = new JSlider(1, 10, 2);
		sliderp.setPaintTicks(true);
		sliderp.setMinorTickSpacing(1);
		labelp = new JLabel("p: " + sliderp.getValue());
		sliderp.addChangeListener(e -> labelp.setText("p: " + ((JSlider) e.getSource()).getValue()));

		sliderresultdim = new JSlider(1, 2, 2);
		sliderresultdim.setPaintTicks(true);
		sliderresultdim.setMinorTickSpacing(1);
		labelresultdim = new JLabel("result dimension: " + sliderresultdim.getValue());
		sliderresultdim.addChangeListener(
				e -> labelresultdim.setText("result dimension: " + ((JSlider) e.getSource()).getValue()));

		startcomputationbutton = new JButton("start computation");
		startcomputationbutton.setActionCommand("start computation");
		startcomputationbutton.addActionListener(this);

		stopcomputationbutton = new JButton("stop computation");
		stopcomputationbutton.setActionCommand("stop computation");
		stopcomputationbutton.addActionListener(this);
		stopcomputationbutton.setEnabled(false);

		labelcuriteration = new JLabel("iteration");
		labelcurepsilon = new JLabel("current epsilon");
		labelcurtime = new JLabel("last iteration time");

		MigLayout layout = new MigLayout("", "[][grow]", "");
		panelparam.setLayout(layout);
		panelparam.add(new JLabel("dissimilarity measure                "), "");
		panelparam.add(choosedismeasure, "wrap");

		panelparam.add(labelmaxiter, "");
		panelparam.add(slidermaxiter, "wrap");

		panelparam.add(labelepsilon, "");
		panelparam.add(sliderepsilon, "wrap");

		panelparam.add(labelp, "");
		panelparam.add(sliderp, "wrap");

		panelparam.add(labelresultdim, "");
		panelparam.add(sliderresultdim, "wrap");

		// enable remote computation only on dev mode
		if (Workspace.getCurrentSettings().isDeveloperMode()) {
			panelparam.add(startcomputationbutton, "");
			panelparam.add(stopcomputationbutton, "");
		} else {
			panelparam.add(startcomputationbutton, "");
			panelparam.add(stopcomputationbutton, "wrap");
		}

		panelparam.add(new JLabel("computation status"), "wrap");
		panelparam.add(labelcuriteration, "wrap");
		panelparam.add(labelcurepsilon, "wrap");
		panelparam.add(labelcurtime, "wrap");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			String command = e.getActionCommand();
			switch (command) {

			// Start der Berechnungen
			case "start computation":
				// Bevor der Algorithmus gestartet wird, muessen die angewaehlten Daten verpackt
				// werden:
				// in mapped_nodes halten wir fest welcher Knoten welche ID besitzt, fuer die
				// Zuordnung der Ergebnisse
				// in smacof_data_map befinden sich alle Daten, die zur Berechnung des Smacof
				// Algorithmus benoetigt werden
				NodeAttribute nat;
				int size_used_attributes = 0;
				for (int line = 0; line < attributes.keySet().size(); line++) {
					if ((Boolean) tabledata[line][UPLOAD_BOOL]) {
						size_used_attributes++;
					}
				}
				int node_id = 0;
				for (BiologicalNodeAbstract bna : graph.getNodes()) {
					mapped_nodes.put(bna, node_id);
					mapped_nodes_backwards.put(node_id, bna);
					smacof_data_map.put(node_id, new double[size_used_attributes]);
					for (int line = 0; line < attributes.keySet().size(); line++) {
						if ((Boolean) tabledata[line][UPLOAD_BOOL]) {
							nat = bna.getNodeAttributeByName((String) tabledata[line][NAME_STR]);
							if (nat != null) {
								smacof_data_map.get(node_id)[line] = nat.getDoubleValue();
							}
						}
					}
					node_id++;
				}
				// printSmacofMap(smacof_data_map);

				// hier wird der Thread fuer den SMACOF Algorithmus angestossen
				double myepsilon = sliderepsilon.getValue() >= 0 ? sliderepsilon.getValue() : Math.pow(10,
						sliderepsilon.getValue());
				//				//APSP statt werte
				//				NetworkProperties np = new NetworkProperties();
				//				short[][] apsp = np.AllPairShortestPaths(false);
				//				smacof_data_map.clear();
				//				for (int i = 0; i < apsp.length; i++) {
				//					double[] dline = new double[apsp[i].length];
				//					for(int j = 0; j< apsp[i].length; j++){
				//						dline[j] = apsp[i][j];
				//					}
				//					smacof_data_map.put(i, dline);
				//
				//				}

				// get weights:
				//					Weighting W = new Weighting(mapped_nodes, mapped_nodes_backwards);
				//					W.getWeightsByAdjacency();
				//			    	W.getWeightsByCellularComponent();

				dosmacof = new DoSmacof(smacof_data_map, mapped_nodes, (String) choosedismeasure.getSelectedItem(),
						slidermaxiter.getValue(), myepsilon, sliderp.getValue(), sliderresultdim.getValue(), this);
				// start SMACOF algorithm as a thread

				startcomputationbutton.setEnabled(false);
				dosmacof.start();

				MainWindow w = MainWindow.getInstance();
				w.showProgressBar("computing new coordinates via SMACOF ...");
				stopcomputationbutton.setEnabled(true);
				break;
			// Abbruch der Berechnungen
			case "stop computation":
				try {
					if (dosmacof != null && dosmacof.isAlive()) {
						dosmacof.stopSmacof();
						dosmacof.interrupt();
						stopcomputationbutton.setEnabled(false);
						startcomputationbutton.setEnabled(true);
						MainWindow mw = MainWindow.getInstance();
						mw.closeProgressBar();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * Die statischen set()-Funktionen sind zum Setzen der Statuslabels
	 */
	public static void setLabelcuriteration(int i) {
		if (labelcuriteration != null) {
			labelcuriteration.setText("iteration: " + i);
		}
	}

	public static void setLabelcurepsilon(double e) {
		if (labelcurepsilon != null) {
			labelcurepsilon.setText("current epsilon: " + shortenDouble(e));
		}
	}

	public static void setLabelcurtime(double s) {
		if (labelcurtime != null) {
			labelcurtime.setText("last iteration time: " + s + " sec");
		}
	}

	private static String shortenDouble(double d) {
		String sd = String.valueOf(d);
		String shortend = "";
		if (sd.contains("E") && sd.length() >= 5) {
			shortend = sd.substring(0, sd.indexOf(".") + 4) + sd.substring(sd.indexOf("E"), sd.length());
		} else if (sd.length() >= 5) {
			shortend = sd.substring(0, sd.indexOf(".")) + sd.substring(sd.indexOf("."), sd.indexOf(".") + 4);
		} else if (Double.isNaN(d)) {
			shortend = "0";
		} else {
			shortend = sd;
		}
		return shortend;
	}

	class SmacofTablePanel extends JPanel {
		private static final long serialVersionUID = -2344221533928376628L;

		public SmacofTablePanel() {
			super(new MigLayout("", "[right]"));

			SmacofTableModel tablemodel = new SmacofTableModel(tableColumnNames, tabledata);
			JTable table = new JXTable(tablemodel);
			table.setPreferredScrollableViewportSize(new Dimension(600, 250));
			table.setFillsViewportHeight(true);
			table.setFont(new Font("Arial", Font.PLAIN, 14));
			table.setRowHeight(table.getRowHeight() + 5);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			// Create the scroll pane and add the table to it.
			JScrollPane scrollPane = new JScrollPane(table);

			// Add the scroll pane to this panel.
			this.add(scrollPane);
			this.setVisible(true);
		}
	}

	static class SmacofTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -1996177080571000673L;

		private final String[] columnNames;
		private final Object[][] data;

		public SmacofTableModel(String[] columnNames, Object[][] data) {
			this.columnNames = columnNames;
			this.data = data;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/*
		 * JTable uses this method to determine the default renderer/ editor for each
		 * cell. If we didn't implement this method, then the last column would contain
		 * text ("true"/"false"), rather than a checkbox.
		 */
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			return col == 3;
		}

		/*
		 * Don't need to implement this method unless your table's data can change.
		 */
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}

	/*
	 * Smacof thread returns and is done, reenable buttons
	 */
	public void returned() {
		startcomputationbutton.setEnabled(true);
		stopcomputationbutton.setEnabled(false);
	}
}
