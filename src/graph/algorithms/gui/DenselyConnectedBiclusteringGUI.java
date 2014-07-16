package graph.algorithms.gui;

import graph.GraphInstance;
import graph.algorithms.DCBresultSet;
import graph.algorithms.DenselyConnectedBiclustering;
import graph.algorithms.NetworkProperties;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import cluster.ClusterComputeThread2;
import cluster.ComputeCallback2;
import cluster.JobTypes;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import biologicalElements.GraphElementAbstract;
import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.GraphNode;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;

public class DenselyConnectedBiclusteringGUI implements ActionListener, ListSelectionListener {

	JPanel p = new JPanel();
	GraphElementAbstract ab;
	GraphInstance graphInstance;
	boolean emptyPane = true;
	private TitledTab tab;

	private JButton calculate, applyNum, file, clear, back, forward;
	private JPanel propertypanel, clusterpanel, densitypanel, attrdimpanel, attrnumpanel, attrpanel,
			presentationpanel;
	private JSpinner attrdimspinner, attrnumspinner;
	private SpinnerNumberModel attrdimvalues, attrnumvalues;
	private JLabel attrlabel, attrminmax, presentationLabel;
	private JComboBox<String> choosePresentation;
	private ArrayList<JFormattedTextField> rangeField;
	private ArrayList<JComboBox> attrTypList;
	private ArrayList<DefaultComboBoxModel> comboBoxModel;
	private ArrayList<JComboBox> attrList;
	private JScrollPane clusterPane;
	private JList<DCBClusterLabel> clusterList;
	public static ProgressBar progressBar;

	private JFormattedTextField desityField;
	private JDialog dialog;
	private JFileChooser chooser;
	private JTextField path;

	MigLayout layout;
	boolean applyNumDone = false;
	boolean chooserDone = false;
	private String[] presentationString = { "size", "color"};
	private double DENSITY_MIN = 0d;
	private double DENSITY_MAX = 1d;
	private double DENSITY_DEFAULT = 0.5;
	private double ATTR_MIN = 0d;
	private double ATTR_MAX = 1000d;
	private double ATTRNUM_MIN = 1d;
	private double ATTRNUM_MAX = 20d;
	private double ATTRNUM_DEFAULT = 2d;
	private double ATTRDIM_MIN = 0d;
	private double ATTRDIM_MAX = 10d;
	private double ATTRDIM_DEFAULT = 2d;
	private double RANGE_DEFAULT = 5d;
	
	public static final String TYPE_GRAPHNODE = "Experimental data";
	public static final String TYPE_PROTEIN = "Protein";
	public static final String TYPE_DNA = "DNA";
	public static final String TYPE_RNA = "RNA";
	public static final String TYPE_BNA = "Graph characteristic";
	
	public static final String GC_DEGREE = "Node degree";
	public static final String GC_NEIGHBOUR = "Neighbour degree";
	public static final String GC_CYCLES = "Cycles";
	public static final String GC_CLIQUES = "Cliques";
	
	
	public static final int TYPE_GRAPHNODE_NR = 0;
	public static final int TYPE_PROTEIN_NR = 1;
	public static final int TYPE_DNA_NR = 2;
	public static final int TYPE_RNA_NR = 3;
	public static final int TYPE_BNA_NR = 4;
	
	DefaultListModel<DCBClusterLabel> listModel;
	
	private LinkedList<HashSet<BiologicalNodeAbstract>> selectedClusters;

	private JTable table;
	private LinkedList<DCBresultSet> results = new LinkedList<DCBresultSet>();
	
	private Pathway pw;
	private MyGraph mg;
	private NetworkProperties np;
	
	private HashMap<BiologicalNodeAbstract, Color> pickedVertices = new HashMap<BiologicalNodeAbstract, Color>();
	private HashMap<BiologicalEdgeAbstract, Color> pickedEdges = new HashMap<BiologicalEdgeAbstract, Color>();

	
	private ArrayList<String> attrTypes;
	private ArrayList<String> experiments;
	
	private int numOfServerJobs = 0;
	
	private ArrayList<Double> ranges;
	private ArrayList<String> attrTyps;
	private ArrayList<String> attrNames;
	private int nodeType = 4;
	private double density;
	private double attrdim;
	
	private HashMap<BiologicalNodeAbstract, Double> cyclesMap;
	private HashMap<BiologicalNodeAbstract, Double> cliquesMap;
	private boolean successComputData= true;
	
	public DenselyConnectedBiclusteringGUI() {

	}

	private void updateWindow() {

		calculate = new JButton("OK");
		
		forward = new JButton("forward");
		forward.setToolTipText("return to cluster selection");
		forward.setActionCommand("forward");
		forward.addActionListener(this);
		forward.setVisible(false);

		attrnumpanel = new JPanel(new MigLayout("", "[][]", ""));

		attrnumvalues = new SpinnerNumberModel(ATTRNUM_DEFAULT, ATTRNUM_MIN, ATTRNUM_MAX, 1d);

		attrnumspinner = new JSpinner(attrnumvalues);
		attrnumspinner.setPreferredSize(new Dimension(50, 10));

		attrnumpanel.add(attrnumspinner);
		attrnumpanel.add(new JLabel(" Min: " + ATTRNUM_MIN + " Max: "
				+ ATTRNUM_MAX));

		applyNum = new JButton("apply");

		applyNum.setToolTipText("Set number of attributes");
		applyNum.setActionCommand("applyNum");
		applyNum.addActionListener(this);

		attrpanel = new JPanel(new MigLayout("", "[][]", ""));

		attrlabel = new JLabel("Attribute ranges:");
		attrminmax = new JLabel("Min: " + ATTR_MIN + " Max: " + ATTR_MAX);

		attrpanel.setVisible(false);
		attrlabel.setVisible(false);
		attrminmax.setVisible(false);

		densitypanel = new JPanel(new MigLayout("", "[][]", ""));

		desityField = new JFormattedTextField(NumberFormat.getInstance());
		desityField.setPreferredSize(new Dimension(50, 10));

		desityField.setValue(DENSITY_DEFAULT);

		densitypanel.add(desityField);
		densitypanel.add(new JLabel(" Min: " + DENSITY_MIN + " Max: "
				+ DENSITY_MAX));

		attrdimpanel = new JPanel(new MigLayout("", "[][]", ""));

		attrdimvalues = new SpinnerNumberModel(ATTRDIM_DEFAULT, ATTRDIM_MIN, ATTRDIM_MAX, 1d);

		attrdimspinner = new JSpinner(attrdimvalues);
		attrdimspinner.setPreferredSize(new Dimension(50, 10));

		attrdimpanel.add(attrdimspinner);
		attrdimpanel.add(new JLabel(" Min: " + ATTRDIM_MIN + " Max: "
				+ ATTRDIM_MAX));

		presentationpanel = new JPanel(new MigLayout("", "[][]", ""));
		presentationLabel = new JLabel("Choose type of presentation:");
		choosePresentation = new JComboBox<String>(presentationString);
		presentationpanel.add(choosePresentation);

		calculate.setToolTipText("Do densely-connected biclustering");
		calculate.setActionCommand("calculate");
		calculate.addActionListener(this);
		calculate.setEnabled(false);

		file = new JButton("Add cluster file");
		file.setToolTipText("File contains former calculated clusters");
		file.setActionCommand("file");
		file.addActionListener(this);
		path = new JTextField("", 20);

		path.setEditable(false);
		path.setScrollOffset(1);

		layout = new MigLayout("", "[][grow]", "");
		propertypanel = new JPanel(layout);
//		propertypanel.setLayout(layout);
//		propertypanel.add(new JLabel("Densely-connected biclustering:"), "wrap");
		propertypanel.add(new JLabel("Densely-connected biclustering:"));
		propertypanel.add(forward, "align right, wrap");
		propertypanel.add(new JLabel("Number of attributes:"), "wrap");
		propertypanel.add(attrnumpanel, "span 2, wrap");
		propertypanel.add(applyNum, "span 2, align right, wrap");
		propertypanel.add(attrlabel, "wrap");
		propertypanel.add(attrpanel, "span 2, wrap");
		propertypanel.add(new JLabel("Density:"), "wrap");
		propertypanel.add(densitypanel, "span 2, wrap");
		propertypanel.add(new JLabel("Number of similar attributes:"), "wrap");
		propertypanel.add(attrdimpanel, "span 2, wrap");
		propertypanel.add(presentationLabel, "wrap");
		propertypanel.add(presentationpanel, "span 2, wrap");
		propertypanel.add(calculate, "span 2, align right, wrap");
		propertypanel.add(path);
		propertypanel.add(file, "wrap");
		
		p.add(propertypanel);

	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public void revalidateView() {

		graphInstance = new GraphInstance();

		if (emptyPane) {
			updateWindow();
			p.repaint();
			p.revalidate();
			emptyPane = false;
		} else {
			p.removeAll();
			updateWindow();
			p.repaint();
			p.revalidate();
			p.setVisible(true);

		}
		tab.repaint();
		tab.revalidate();
	}

	public void actionPerformed(ActionEvent e) {

		String event = e.getActionCommand();
		pw = graphInstance.getPathway();
		mg = pw.getGraph();
		pw.getGraph().setMouseModeTransform();

		if ("file".equals(event)) {
			chooser = new JFileChooser();
			int state = chooser.showOpenDialog(p);
			if (state == JFileChooser.APPROVE_OPTION) {
				path.setText(chooser.getSelectedFile().getPath());

			}

			BufferedReader br = null;

			try {
				FileReader fr = new FileReader(chooser.getSelectedFile());
				br = new BufferedReader(fr);
				MyGraph mg = pw.getGraph();
				HashMap<Integer, BiologicalNodeAbstract> idBna = new HashMap<Integer, BiologicalNodeAbstract>();

				for (BiologicalNodeAbstract vertex1 : mg.getAllVertices()) {
					idBna.put(vertex1.getID(), vertex1);
				}
				br.readLine(); // titel-line is not used
				while (br.ready()) {
					String line = br.readLine();
					String[] column = line.split("\t");
					int numOfVertices = Integer.parseInt(column[0]);
					double density = Double.parseDouble(column[1]);
					int numOfhomogenAttributes = Integer.parseInt(column[2]);
					String labels = column[3];

					String[] ids = column[4].split(" ");

					HashSet<BiologicalNodeAbstract> vertices = new HashSet<BiologicalNodeAbstract>();
					for (String id : ids) {
						if (idBna.get(Integer.parseInt(id)) != null) {
							vertices.add(idBna.get(Integer.parseInt(id)));
						} else {
							throw new IllegalArgumentException();
						}
					}

					DCBresultSet result = new DCBresultSet(numOfVertices,
							density, numOfhomogenAttributes, labels, vertices);
					results.add(result);
				}

				table = initTable(results);
				openResultDialog(table);

			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null,
						"Couldn't read cluster file.", "Error",
						JOptionPane.ERROR_MESSAGE);

				e1.printStackTrace();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "File has wrong format."
						+ "\nPlease use only files that are computed by DCB.",
						"Error", JOptionPane.ERROR_MESSAGE);

				nfe.printStackTrace();
			} catch (IllegalArgumentException iae) {
				JOptionPane
						.showMessageDialog(
								null,
								"Cluster file dose not suit graph."
										+ "\nPlease load differend graph or cluster file.",
								"Error", JOptionPane.ERROR_MESSAGE);

				iae.printStackTrace();
			} finally {
				results = new LinkedList<DCBresultSet>();
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		if ("applyNum".equals(event)) {
			attrpanel.removeAll();
			int num = ((Double) attrnumspinner.getValue()).intValue();

			rangeField = new ArrayList<JFormattedTextField>();
			
			setAttrTypes();
			
//			String[] attrTypString = { "Graph characteristic",
//					"Experimental Data", "Biological Characteristic" };
			
			String[] attrTypString = new String[attrTypes.size()];
					
			attrTypes.toArray(attrTypString);
			

			// Create the combo box, select item at index 4.
			// Indices start at 0, so 4 specifies the pig.
			attrTypList = new ArrayList<JComboBox>();

			String[] defaultAttrString = { GC_DEGREE, GC_NEIGHBOUR, GC_CYCLES, GC_CLIQUES };
			comboBoxModel = new ArrayList<DefaultComboBoxModel>();
			attrList = new ArrayList<JComboBox>();

			for (int i = 0; i < num; i++) {

				attrTypList.add(new JComboBox<String>(attrTypString));
				attrTypList.get(i).setSelectedIndex(0);
				attrTypList.get(i).addActionListener(this);
				attrTypList.get(i).setEditable(false);
				// rangeField[i] = new
				// JFormattedTextField(NumberFormat.getInstance());
				// rangeField[i].setPreferredSize(new Dimension(50, 10));
				// rangeField[i].setValue(0.5);

				comboBoxModel.add(new DefaultComboBoxModel<String>(
						defaultAttrString));

				attrList.add(new JComboBox<String>());
				attrList.get(i).setModel(comboBoxModel.get(i));
				attrList.get(i).setSelectedIndex(0);
				attrList.get(i).addActionListener(this);
				attrList.get(i).setEditable(false);

				attrpanel.add(attrTypList.get(i));
				attrpanel.add(attrList.get(i), "wrap");

				if (i == 0) {
					rangeField.add(new JFormattedTextField(NumberFormat
							.getInstance()));
					rangeField.get(0).setPreferredSize(new Dimension(50, 10));
					rangeField.get(0).setValue(RANGE_DEFAULT);

					attrpanel.add(rangeField.get(0));
					attrpanel.add(attrminmax, "wrap");
					attrminmax.setVisible(true);

				} else {
					rangeField.add(new JFormattedTextField(NumberFormat
							.getInstance()));
					rangeField.get(i).setPreferredSize(new Dimension(50, 10));
					rangeField.get(i).setValue(RANGE_DEFAULT);

					attrpanel.add(rangeField.get(i), "wrap");
				}

			}

			applyNumDone = true;
			attrlabel.setVisible(true);
			attrpanel.setVisible(true);
			calculate.setEnabled(true);
			attrpanel.updateUI();

		}
		if ("comboBoxChanged".equals(event)) {

			ArrayList<String> attr = new ArrayList<String>();
			int boxIndex = attrTypList.indexOf(e.getSource());
			if (boxIndex != -1) {
				String itemName = (String) attrTypList.get(boxIndex).getSelectedItem();
				switch (itemName) {
				case TYPE_BNA: // "Graph characteristic"
					attr.add(GC_DEGREE);
					attr.add(GC_NEIGHBOUR);
					attr.add(GC_CYCLES);
					attr.add(GC_CLIQUES);
					break;
				case TYPE_GRAPHNODE:// "Experimental Data
						// TODO aus Graph laden
					attr.addAll(experiments);
					break;
				case TYPE_DNA:
					attr.add("Sequence length");
					break;
				case TYPE_RNA:
					attr.add("Sequence length");
					break;
				case TYPE_PROTEIN:
					attr.add("Sequence length");
					break;
				default:
					break;
				}
				String[] attrArray = new String[attr.size()];
				attrArray = attr.toArray(attrArray);
				comboBoxModel.set(boxIndex, new DefaultComboBoxModel<String>(
						attrArray));

				attrList.get(boxIndex).setModel(comboBoxModel.get(boxIndex));

			}
			// if(e.getSource().equals(attrTypList[0])){
			// String[] attr = {"blub", "bla"};
			// comboBoxModel.removeAllElements();
			// comboBoxModel = new DefaultComboBoxModel<String>(attr);
			// attrList[0].setModel(comboBoxModel);
			// }

		}
		if ("calculate".equals(event)) {
			
			progressBar = new ProgressBar();
			progressBar.init(100, "DCB", true);
			
		
			boolean noMinMax = false;
			boolean toManyTypes = false;

			density = ((Number) desityField.getValue()).doubleValue();
			if (density < DENSITY_MIN || density > DENSITY_MAX) {
				noMinMax = true;
			}

			ranges = new ArrayList<Double>();
			attrTyps = new ArrayList<String>();
			attrNames = new ArrayList<String>();
			
//			LinkedHashMap<String, ArrayList<String>> attrNames2 = new LinkedHashMap <String, ArrayList<String>>();

			
			int typeCounter = 0;
			
			for (int i = 0; i < rangeField.size(); i++) {
				ranges.add(((Number) rangeField.get(i).getValue())
						.doubleValue());
				// ranges.add(((Number)rangeField.get(i).getValue()).doubleValue());
				if (ranges.get(i) < ATTR_MIN || ranges.get(i) > ATTR_MAX) {
					noMinMax = true;
					break;
				}else if(typeCounter > 1){
					toManyTypes = true;
					break;
				}else {
					String type = (String) attrTypList.get(i).getSelectedItem();
					attrTyps.add(type);
					attrNames.add((String) attrList.get(i).getSelectedItem());
					
					switch(type){
					case TYPE_GRAPHNODE: 
						typeCounter++;
						nodeType = TYPE_GRAPHNODE_NR;
						break;
					case TYPE_PROTEIN: 
						typeCounter++;
						nodeType = TYPE_PROTEIN_NR;
						break;
					case TYPE_DNA:
						typeCounter++;
						nodeType = TYPE_DNA_NR;
						break;
					case TYPE_RNA: 
						typeCounter++;
						nodeType = TYPE_RNA_NR;
						break;
					default:
						break;
					}
					
//					if(attrNames2.containsKey(attrTypList.get(i).getSelectedItem())){
//						attrNames2.get(attrTypList.get(i).getSelectedItem()).add((String) attrList.get(i).getSelectedItem());
//					}else{
//						ArrayList<String> tmp = new ArrayList<String>();
//						tmp.add((String) attrList.get(i).getSelectedItem());
//						attrNames2.put((String) attrTypList.get(i).getSelectedItem(), tmp);
//					}
					
//						attrNames2.put(attrTypList.get(i), attrList.get(i));
				}

			}
			if(typeCounter == 0){
				nodeType = TYPE_BNA_NR;
			}
			

			attrdim = (double) attrdimspinner.getValue();

			if (noMinMax) {
				reactivateUI();
				JOptionPane.showMessageDialog(null,
						"Please consider minimum and maximum Values.", "Error",
						JOptionPane.ERROR_MESSAGE);
				

			} else if (attrdim > ranges.size()) {
				reactivateUI();
				JOptionPane
						.showMessageDialog(
								null,
								"Number of similar attributes must not be greater than number of attributes.",
								"Error", JOptionPane.ERROR_MESSAGE);
				
			} else if (toManyTypes) {
				reactivateUI();
				JOptionPane
						.showMessageDialog(
								null,
								"These types of attributes could not be combined.",
								"Error", JOptionPane.ERROR_MESSAGE);
				

			} else {
				if(!(attrNames.contains(GC_CYCLES)||attrNames.contains(GC_CLIQUES))){
					cyclesMap = null;
					cliquesMap = null;
					startDcb();
				
				}else{
					np = new NetworkProperties();
					
					
					if(attrNames.contains(GC_CYCLES)){
						numOfServerJobs++;
		        		ComputeCallback2 helper;
							try {
								helper = new ComputeCallback2(this);

								ClusterComputeThread2 rmicycles = new ClusterComputeThread2(
										JobTypes.CYCLE_JOB_OCCURRENCE, helper);
								rmicycles.setAdjMatrix(np.getAdjacencyMatrix());
								rmicycles.start();
							} catch (RemoteException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					}
					
					if(attrNames.contains(GC_CLIQUES)){
						numOfServerJobs++;
		        		ComputeCallback2 helper;
							try {
								helper = new ComputeCallback2(this);

								ClusterComputeThread2 rmicycles = new ClusterComputeThread2(
										JobTypes.CLIQUE_JOB_OCCURRENCE, helper);
								rmicycles.setAdjMatrix(np.getAdjacencyMatrix());
								rmicycles.start();
							} catch (RemoteException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					}
					
				}

			}

		}
		if ("dialog_choose".equals(event)) {
			int numRows = table.getRowCount();
			TableModel tabelModel = table.getModel();

			selectedClusters = new LinkedList<HashSet<BiologicalNodeAbstract>>();
			ArrayList<DCBClusterLabel> clusterLabels = new ArrayList<DCBClusterLabel>();
			Color color = UIManager.getColor("List.selectionForeground");
			listModel = new DefaultListModel<DCBClusterLabel>();
			
			for (int i = 0; i < numRows; i++) {

				if ((boolean) tabelModel.getValueAt(i, 4)) {
					listModel.addElement(new DCBClusterLabel(results.get(i).getLabels(), color));
					selectedClusters.add(results.get(i).getVertices());
				}
			}
			
			DCBClusterLabel[] clusterArray = new DCBClusterLabel[clusterLabels.size()];
			clusterArray = clusterLabels.toArray(clusterArray);

			JLabel clusterText = new JLabel("Use ctrl or shift to select multiple clusters.");
			
			clusterList = new JList<DCBClusterLabel>();
//			String[] clusterLabels = {"bla", "bli", "blub"};
//			clusterList = new JList<DCBClusterLabel>(clusterArray); //data has type Object[]
//			clusterList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			
			clusterList.setModel(listModel);
			clusterList.setCellRenderer(new DCBListRenderer());
			
			
			clusterList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			clusterList.setLayoutOrientation(JList.VERTICAL);
			clusterList.setVisibleRowCount(-1);
			clusterList.addListSelectionListener(this);
			
			
			
			
			clusterPane = new JScrollPane(clusterList);
			clusterPane.setPreferredSize(new Dimension(350, 240));
			clusterpanel = new JPanel(new MigLayout("", "[grow]", ""));
			
			//TODO
			
			back = new JButton("back");
			back.setToolTipText("return to previous view");
			back.setActionCommand("back");
			back.addActionListener(this);
			
			clear = new JButton("clear");
			clear.setToolTipText("Clear selection");
			clear.setActionCommand("clear");
			clear.addActionListener(this);
			clear.setEnabled(false);
			
			
			clusterpanel.add(back, "align right, wrap");
			clusterpanel.add(clusterText, "wrap");
			clusterpanel.add(clusterPane, "align left, grow, wrap");
			clusterpanel.add(clear, "align center");

			p.removeAll();
			p.add(clusterpanel);

			
			tab.repaint();
			tab.revalidate();

			switch (choosePresentation.getSelectedIndex()) {
			case 0: // "size"
				HashMap<BiologicalNodeAbstract, Double> sizes = new HashMap<BiologicalNodeAbstract, Double>();
				for (BiologicalNodeAbstract vertex : mg.getAllVertices()) {
					vertex.setReference(false);
					sizes.put(vertex, 0.0);
				}

				for (HashSet<BiologicalNodeAbstract> cluster : selectedClusters) {
					for (BiologicalNodeAbstract vertex : cluster) {
						sizes.put(vertex, sizes.get(vertex) + 1);
					}
				}
				
				//weighting
				double maxRate = Collections.max(sizes.values());
				double minRate = Collections.min(sizes.values());
				double maxweight = 2;
				double minweight = 1;
				
				double currentvalue;
				
				for(BiologicalNodeAbstract vertex : sizes.keySet()){
					currentvalue = ((maxweight-minweight)/(maxRate-minRate))*sizes.get(vertex);
					currentvalue+=minweight;
					vertex.setNodesize(currentvalue);
				}
				
				
				break;
			case 1:// "color1"
				//alt
				Hashtable<BiologicalNodeAbstract, Double> coloring = new Hashtable<BiologicalNodeAbstract, Double>();
				for (BiologicalNodeAbstract vertex : mg.getAllVertices()) {
					vertex.setReference(false);
					coloring.put(vertex, 0.0);
				}

				for (HashSet<BiologicalNodeAbstract> cluster : selectedClusters) {
					for (BiologicalNodeAbstract vertex : cluster) {
						coloring.put(vertex, coloring.get(vertex) + 1);
					}
				}

				new GraphColorizer(coloring, 0, false);
				
				break;	
//			case 2:// "color2"
//				// neu
//				InternalGraphRepresentation graphRepresentation = pw
//						.getGraphRepresentation();
//
//				for (HashSet<BiologicalNodeAbstract> cluster : selectedClusters) {
//					for (BiologicalNodeAbstract vertex1 : cluster) {
//						vertex1.setReference(false);
//						vertex1.setColor(Color.blue);
//						for (BiologicalNodeAbstract vertex2 : cluster) {
//							if (graphRepresentation.doesEdgeExist(vertex1, vertex2)) {
//								vertex2.setReference(false);
//								BiologicalEdgeAbstract edge = graphRepresentation
//										.getEdge(vertex1, vertex2);
//								System.out.println(edge.getColor().toString());
//								edge.setReference(false);
//								edge.setColor(Color.green);
//
//								System.out.println(edge.getColor().toString());
//								System.out.println();
//							}
//						}
//					}
//				}
//				break;
			default:
				break;
			}
			
			
			
			

			mg.getVisualizationViewer().repaint();
			results = new LinkedList<DCBresultSet>();
			dialog.dispose();
		}
		if ("dialog_save".equals(event)) {
			JFileChooser saver = new JFileChooser();
			int state = saver.showSaveDialog(p);
			if (state == JFileChooser.APPROVE_OPTION) {
				Writer fw = null;

				try {
					fw = new FileWriter(saver.getSelectedFile());

					fw.write("Size");
					fw.write("\t");
					fw.write("Density");
					fw.write("\t");
					fw.write("Number...");
					fw.write("\t");
					fw.write("Graph Labels");
					fw.write("\t");
					fw.write("Graph IDs");
					fw.append(System.getProperty("line.separator"));

					for (DCBresultSet result : results) {

						fw.write(((Integer) result.getNumOfVertices())
								.toString());
						fw.write("\t");
						fw.write(((Double) ((Math.rint((result.getDensity()) * 1000)) / 1000))
								.toString());
						fw.write("\t");
						fw.write(((Integer) result.getNumOfhomogenAttributes())
								.toString());
						fw.write("\t");
						fw.write(result.getLabels());
						fw.write("\t");
						for (BiologicalNodeAbstract vertex : result
								.getVertices()) {
							fw.write(((Integer) vertex.getID()).toString());
							fw.write(" ");
						}
						fw.append(System.getProperty("line.separator"));

					}

				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,
							"File could not be saved.", "File not saved",
							JOptionPane.INFORMATION_MESSAGE);
					e1.printStackTrace();
				} finally {
					if (fw != null)
						try {
							fw.close();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
				}
			}

		}
		if ("dialog_cancel".equals(event)) {
			results = new LinkedList<DCBresultSet>();
			dialog.dispose();

		}
		if("clear".equals(event)){
			
			clusterList.clearSelection();

		}
		if("back".equals(event)){
			clusterList.clearSelection();
			p.removeAll();
			p.add(propertypanel);
			forward.setVisible(true);

			tab.repaint();
			tab.revalidate();
		}
		if("forward".equals(event)){
			p.removeAll();
			p.add(clusterpanel);

			tab.repaint();
			tab.revalidate();
		}
	}

	public void startDcb() {
		DenselyConnectedBiclustering dcb = new DenselyConnectedBiclustering(
				density, ranges, nodeType, attrTyps, attrNames, attrdim, cyclesMap, cliquesMap);

		results = dcb.start();
		// assert results != null : "No result";
		if (results != null) {
			table = initTable(results);
		} else {
			table = null;
		}
		
		reactivateUI();

		openResultDialog(table);
	}


	
	/**
	 * 
	 */
	private void setAttrTypes() {
		attrTypes = new ArrayList<String>();
		experiments = new ArrayList<String>();

		
		attrTypes.add("Graph characteristic");
		GraphNode graphNode;
		String expermientName;
		
		for(BiologicalNodeAbstract node : graphInstance.getPathway().getAllNodes()){
			if(node instanceof GraphNode){
				if(!attrTypes.contains(TYPE_GRAPHNODE)){
					attrTypes.add(TYPE_GRAPHNODE);
				}
				graphNode = (GraphNode) node;
				
				for (int i = 0; i < graphNode.getSuperNode().biodata.length; i++) {
					expermientName = graphNode.getSuperNode().biodata[i];
					if(!experiments.contains(expermientName)){
						experiments.add(expermientName);
					}
				}

			}else if((node instanceof DNA) && !attrTypes.contains(TYPE_DNA)){
				attrTypes.add(TYPE_DNA);

			}else if((node instanceof Protein) && !attrTypes.contains(TYPE_PROTEIN)){
				attrTypes.add(TYPE_PROTEIN);

			}else if((node instanceof RNA) && !attrTypes.contains(TYPE_RNA)){
				attrTypes.add(TYPE_RNA);

			}
			
		}
		
	}

	private void openResultDialog(JTable table) {

		if (table != null) {
			JScrollPane sp = new JScrollPane(table);
			JButton choose = new JButton("ok");
			JLabel clusterText = new JLabel("Use ctrl or shift to select multiple clusters.");
			choose.setToolTipText("Choose Clusters");
			choose.setActionCommand("dialog_choose");
			choose.addActionListener(this);
			JButton save = new JButton("save");
			save.setToolTipText("Save Clusters");
			save.setActionCommand("dialog_save");
			save.addActionListener(this);
			JButton cancel = new JButton("cancel");
			cancel.setActionCommand("dialog_cancel");
			cancel.addActionListener(this);

			JPanel dialogPane = new JPanel(new MigLayout("", "[]", ""));

			dialogPane.add(clusterText, "wrap");
			dialogPane.add(sp, "wrap");

			JPanel buttonPane = new JPanel(new MigLayout("", "[][][]", ""));
			buttonPane.add(choose);
			buttonPane.add(save);
			buttonPane.add(cancel);

			dialogPane.add(buttonPane, "align Center");
			choose.setVisible(true);

			JOptionPane optionPane = new JOptionPane(dialogPane,
					JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
					null, new Object[] {}, null);

			dialog = optionPane.createDialog(MainWindowSingelton.getInstance(),
					"Choose Cluster");
			dialog.setVisible(true);

		} else {
			JOptionPane.showMessageDialog(null,
					"No Clusters existing for this parameters.", "No Results",
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private JTable initTable(LinkedList<DCBresultSet> results) {
		String[] columNames = { "Size", "Density",
				"Number of homogen attributes", "Graph", "Check" };
		Object[][] rows = new Object[results.size()][5];
		int iterator_count = 0;

		for (DCBresultSet result : results) {

			rows[iterator_count][0] = ((Integer) result.getNumOfVertices())
					.toString();
			rows[iterator_count][1] = ((Double) ((Math.rint((result
					.getDensity()) * 1000)) / 1000)).toString();
			rows[iterator_count][2] = ((Integer) result
					.getNumOfhomogenAttributes()).toString();
			rows[iterator_count][3] = result.getLabels();
			rows[iterator_count][4] = false;

			iterator_count++;
		}

		JTable table = null;

		if (rows.length != 0) {
			@SuppressWarnings("serial")
			DefaultTableModel model = new DefaultTableModel(rows, columNames) {
				@Override
				public Class<?> getColumnClass(int column) {
					return getValueAt(0, column).getClass();
				}
			};
//
//			model.addTableModelListener(this);
			table = new JTable(model);
			

//			table = new JTable();
			
			table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			table.getSelectionModel().addListSelectionListener(this);

			table.setRowSelectionAllowed(true);
//			table.setRowSelectionInterval(0, 1);
			table.setColumnSelectionAllowed(false);
		}

		return table;

	}

	public TitledTab getTitledTab() {

		tab = new TitledTab("DCB", null, getPanel(), null);
		return tab;
	}
	
	private Color sumColors(Color color1, Color color2){
		
		Color result = new Color(color1.getRGB()+color2.getRGB());
		return result;
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		

		if(e.getSource().hashCode() == table.getSelectionModel().hashCode()){
			
			if (e.getValueIsAdjusting() == false) {
				for(int i = 0; i < table.getRowCount(); i++){
					table.setValueAt(false, i, 4);
				}
				
				for(int rowIndex : table.getSelectedRows()){
					table.setValueAt(true, rowIndex, 4);
				}
				table.repaint();
			}
		}else if(e.getSource().hashCode() == clusterList.hashCode()){
		
		
			InternalGraphRepresentation graphRepresentation = pw
					.getGraphRepresentation();
	
			
		    if (e.getValueIsAdjusting() == false) {
	
		        if (clusterList.getSelectedIndex() == -1) {
		        	//clear
		        	for(BiologicalNodeAbstract bna : pickedVertices.keySet()){
		        		bna.setColor(pickedVertices.get(bna));
		        	}
		        	
		        	pickedVertices.clear();
		        	
		        	for(BiologicalEdgeAbstract edge : pickedEdges.keySet()){
		        		edge.setColor(pickedEdges.get(edge));
		        	}
		        	
		        	pickedEdges.clear();
	
		        } else {
		        	
//		        	Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.PINK, Color.YELLOW};
		        	
		        	Color[] colors = {new Color(0.5f, 0f, 0f), new Color(0f, 0.5f, 0f), new Color(0f, 0f, 0.5f), new Color(0.5f, 0.3f, 0f), new Color(0f, 0.5f, 0.3f), new Color(0f, 0.3f, 0.5f)};
		        	
	//				GraphInstance g = new GraphInstance();
	//				final VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = g.getPathway().getGraph().getVisualizationViewer();
	//				vv.getPickedVertexState().clear();
		        	
		        	for(BiologicalNodeAbstract bna : pickedVertices.keySet()){
		        		bna.setColor(pickedVertices.get(bna));
		        	}
		        	
		        	for(BiologicalEdgeAbstract edge : pickedEdges.keySet()){
		        		edge.setColor(pickedEdges.get(edge));
		        	}
		        	Color nodeColor;
		        	Color edgeColor;
		        	for(int clusterIndex : clusterList.getSelectedIndices()){
		        		int colorIndex = clusterIndex%(colors.length);
		        		
//		        		System.out.println(clusterList);
		        		
		        		listModel.getElementAt(clusterIndex).setColor(colors[colorIndex]);
		        		
//		        		clusterList.setSelectionBackground(colors[colorIndex]);
		            	
			            for(BiologicalNodeAbstract bna1 : selectedClusters.get(clusterIndex)){
			        
			            	if(!pickedVertices.keySet().contains(bna1)){
			            		pickedVertices.put(bna1, bna1.getColor());
			            		bna1.setReference(false);
			            	}
			            	
			            	if(!bna1.getColor().equals(pickedVertices.get(bna1))){
//				            	color = new Color(bna1.getColor().getRGB()+colors[colorIndex].getRGB());
			            		nodeColor = sumColors(bna1.getColor(), colors[colorIndex]);
			            	}else{
			            		nodeColor = colors[colorIndex];
			            	}
			            	
			            	
			            	bna1.setColor(nodeColor);
		
			            	//TODO edge color!
				            for(BiologicalNodeAbstract bna2 : selectedClusters.get(clusterIndex)){
								BiologicalEdgeAbstract edge = graphRepresentation
										.getEdge(bna1, bna2);
								if(edge != null){
									if(!pickedEdges.keySet().contains(edge)){
										pickedEdges.put(edge, edge.getColor());
										edge.setReference(false);
									}
									
					            	if(!edge.getColor().equals(pickedEdges.get(edge))){
//						            	color = new Color(bna1.getColor().getRGB()+colors[colorIndex].getRGB());
					            		edgeColor = sumColors(edge.getColor(), colors[colorIndex]);
					            	}else{
					            		edgeColor = colors[colorIndex];
					            	}
									edge.setColor(edgeColor);
								}
				            }
			            }
		        	}
		            
		            
		            
		        	clear.setEnabled(true);
		            
		           
		            
		        }
		        mg.getVisualizationViewer().repaint();
		        
		    }
	    
		}
	}



	/**
	 * @param table
	 * @param jobtype
	 */
	public void returnComputeData(Hashtable<Integer, Double> table, int jobtype) {
		
		// Determine jobtype and behaviour
		switch (jobtype) {
		case JobTypes.CYCLE_JOB_OCCURRENCE:
			cyclesMap = new HashMap<BiologicalNodeAbstract, Double>();
			
			Hashtable<Integer, Double> cycledata = table;
			numOfServerJobs--;

			if (!cycledata.isEmpty()) {
				// Map ids to BNAs
				Iterator<Entry<Integer, Double>> it = cycledata.entrySet()
						.iterator();
				int key;
				double value;

				while (it.hasNext()) {
					Entry<Integer, Double> entry = it.next();
					key = entry.getKey();
					value = entry.getValue();
					// debug
					// System.out.println(key + " " + value);
					
					cyclesMap.put(np.getNodeAssignmentbackwards(key), value);

				}
				
			}else{
				successComputData = false;
				reactivateUI();
				JOptionPane.showMessageDialog(null,
						"No cycles found, please use different attributes.", "No cycles",
						JOptionPane.INFORMATION_MESSAGE);
				
			}

			break;

		case JobTypes.CLIQUE_JOB_OCCURRENCE:
			cliquesMap = new HashMap<BiologicalNodeAbstract, Double>();
			
			Hashtable<Integer, Double> cliquesdata = table;
			numOfServerJobs--;

			if (!cliquesdata.isEmpty()) {
				// Map ids to BNAs
				Iterator<Entry<Integer, Double>> it = cliquesdata.entrySet()
						.iterator();
				int key;
				double value;

				while (it.hasNext()) {
					Entry<Integer, Double> entry = it.next();
					key = entry.getKey();
					value = entry.getValue();
					// debug
					// System.out.println(key + " " + value);
					
					cliquesMap.put(np.getNodeAssignmentbackwards(key), value);

				}
				
			}else{
				successComputData = false;
				reactivateUI();
				JOptionPane.showMessageDialog(null,
						"No cliques found, please use different attributes.", "No cliques",
						JOptionPane.INFORMATION_MESSAGE);
				
			}
			
			break;

		default:
			System.out.println("Wrong Job Type: returnComputeData - "
					+ toString());
			break;
		}
		
		if(successComputData&&(numOfServerJobs == 0)){
			startDcb();
		}

		
	}
	
	
	public static void reactivateUI() {
		// close Progress bar and reactivate UI
		progressBar.closeWindow();
		MainWindow mw = MainWindowSingelton.getInstance();
		mw.setEnable(true);
		mw.setLockedPane(false);
	}
	
	
}
