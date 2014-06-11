package graph.algorithms.gui;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.NetworkProperties;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;
import gui.images.ImagePath;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.remote.rmi.RMIIIOPServerImpl;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cluster.ComputeCallback;
import cluster.ClusterComputeThread;
import cluster.JobTypes;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class GraphColoringGUI implements ActionListener {

	JPanel p = new JPanel();
	GraphElementAbstract ab;
	GraphInstance graphInstance;
	boolean emptyPane = true;

	private JComboBox<String> chooseAlgorithm;
	private JButton colorizebutton;
	private JButton resetcolorbutton;
	private JButton degreedistributionbutton;

	private String[] algorithmNames = { "Node Degree", "Neighbor Degree",
			"Cycles (cluster)", "Cliques (cluster)", "Paths (cluster)",
			"Clustering (cluster)" };
	private int currentalgorithmindex = 0;
	private String[] colorrangenames = { "bluesea", "skyline", "darkmiddle",
			"darkleftmiddle", "rainbow" };
	private final int NODE_DEGREE = 0, NEIGHBOR_DEGREE = 1, CYCLES = 2,
			CLIQUES = 3, PATHRATING = 4, SPECTRAL = 5;

	private ImageIcon[] icons;

	private ImageIcon currentimage;
	private int currentimageid;
	private JRadioButton[] colorrangeradio = new JRadioButton[colorrangenames.length];
	private GraphColorizer gc;
	private JCheckBox logview;
	private ButtonGroup bg;

	// coloring variables
	private NetworkProperties np;
	private Iterator<BiologicalNodeAbstract> itn;
	private Hashtable<BiologicalNodeAbstract, Double> coloring;
	private BiologicalNodeAbstract bna;

	private TitledTab tab;

	private final Color greynodecolor = new Color(-4144960);

	public static ProgressBar progressbar;

	private MainWindow mw;

	private ComputeCallback helper;

	public GraphColoringGUI() {
		// set icon paths
		ImagePath imagepath = ImagePath.getInstance();
		ImageIcon[] tmpset = {
				new ImageIcon(imagepath.getPath("icon_colorrange_bluesea.png")),
				new ImageIcon(imagepath.getPath("icon_colorrange_skyline.png")),
				new ImageIcon(
						imagepath.getPath("icon_colorrange_darkmiddle.png")),
				new ImageIcon(imagepath.getPath("icon_colorrange_dark.png")),
				new ImageIcon(imagepath.getPath("icon_colorrange_rainbow.png")) };
		icons = tmpset;
		currentimage = icons[0];
		currentimageid = 0;

	}

	private void updateWindow() {

		bg = new ButtonGroup();

		chooseAlgorithm = new JComboBox<String>(algorithmNames);
		chooseAlgorithm.setActionCommand("algorithm");
		chooseAlgorithm.addActionListener(this);

		for (int i = 0; i < colorrangenames.length; i++) {
			colorrangeradio[i] = new JRadioButton(colorrangenames[i]);
			colorrangeradio[i].setActionCommand("" + i);
			colorrangeradio[i].addActionListener(this);
			bg.add(colorrangeradio[i]);

		}
		colorizebutton = new JButton("color selection");
		colorizebutton.setActionCommand("colorize");
		colorizebutton.addActionListener(this);
		colorizebutton.setEnabled(false);

		resetcolorbutton = new JButton("reset colors");
		resetcolorbutton.setActionCommand("resetcolors");
		resetcolorbutton.addActionListener(this);
		resetcolorbutton.setEnabled(false);
	
		MigLayout layout = new MigLayout("", "[][grow]", "");
		p.setLayout(layout);
		p.add(new JLabel("Algorithm"), "wrap");
		p.add(chooseAlgorithm, "wrap");
		p.add(new JLabel("Color Range"), "wrap");
		for (int i = 0; i < colorrangenames.length; i++) {
			p.add(colorrangeradio[i]);
			p.add(new JLabel(icons[i]), "wrap");
		}

		logview = new JCheckBox("Data in log(10)");
		logview.setSelected(false);
		logview.setActionCommand("logview");
		logview.addActionListener(this);
		logview.setEnabled(false);

		degreedistributionbutton = new JButton("Degree distribution");
		degreedistributionbutton.setActionCommand("degreedistribution");
		degreedistributionbutton.addActionListener(this);
		
		p.add(logview, "align left");
		p.add(colorizebutton, "align right, wrap");
		p.add(resetcolorbutton, "align left, wrap");
		p.add(degreedistributionbutton,"span 2, align right, wrap");

	}

	public synchronized void recolorGraph() {

		np = new NetworkProperties();
		itn = np.getPathway().getAllNodes().iterator();
		coloring = new Hashtable<BiologicalNodeAbstract, Double>();

		switch (currentalgorithmindex) {
		case NODE_DEGREE:
			while (itn.hasNext()) {
				bna = itn.next();
				coloring.put(bna,
						(double) np.getNodeDegree(np.getNodeAssignment(bna)));
			}
			break;
		case NEIGHBOR_DEGREE:
			coloring = np.averageNeighbourDegreeTable();
			break;
		case CYCLES:
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingelton.getInstance();
			mw.setEnable(false);
			mw.setLockedPane(true);
			progressbar = new ProgressBar();
			progressbar.init(100, "Computing", true);
			progressbar.setProgressBarString("Getting cluster results");

			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
				ClusterComputeThread rmicycles = new ClusterComputeThread(
						JobTypes.CYCLE_JOB_OCCURRENCE, helper);
				rmicycles.setAdjMatrix(np.getAdjacencyMatrix());
				rmicycles.start();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case CLIQUES:
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingelton.getInstance();
			mw.setEnable(false);
			mw.setLockedPane(true);
			progressbar = new ProgressBar();
			progressbar.init(100, "Computing", true);
			progressbar.setProgressBarString("Getting cluster results");

			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
				ClusterComputeThread rmicliques = new ClusterComputeThread(
						JobTypes.CLIQUE_JOB_OCCURRENCE, helper);
				rmicliques.setAdjMatrix(np.getAdjacencyMatrix());
				rmicliques.start();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case PATHRATING:
			// ClusterComputeThread rmiapsp = new ClusterComputeThread(
			// ClusterComputeThread.APSP_JOB, this);
			// rmiapsp.start();
			break;
		case SPECTRAL:
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingelton.getInstance();
			mw.setEnable(false);
			mw.setLockedPane(true);
			progressbar = new ProgressBar();
			progressbar.init(100, "Computing", true);
			progressbar.setProgressBarString("Getting cluster results");

			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ClusterComputeThread rmispectral = new ClusterComputeThread(
					JobTypes.SPECTRAL_CLUSTERING_JOB, helper);
			rmispectral.setAdjMatrix(np.getAdjacencyMatrix());
			rmispectral.start();
			break;

		default:
			System.err.println("ERROR! JobType not found.");
			break;
		}

		gc = new GraphColorizer(coloring, currentimageid, logview.isSelected());
		// recolor button enable after first Coloring, logview enabled
		colorizebutton.setEnabled(true);
		logview.setEnabled(true);
		resetcolorbutton.setEnabled(true);
	}

	public void reactiveateUI() {
		// close Progress bar and reactivate UI
		GraphColoringGUI.progressbar.closeWindow();
		mw = MainWindowSingelton.getInstance();
		mw.setEnable(true);
		mw.setLockedPane(false);
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
	}

	public void removeAllElements() {
		emptyPane = true;
	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public TitledTab getTitledTab() {

		tab = new TitledTab("Coloring", null, p, null);

		return tab;
	}

	public void returnComputeData(HashSet<HashSet<Integer>> sets, int jobtype) {
		switch (jobtype) {
		case JobTypes.SPECTRAL_CLUSTERING_JOB:
			HashSet<HashSet<Integer>> clusterdata = sets;
			if (!clusterdata.isEmpty()) {
				// Map ids to BNAs
				double value;
				Color clustercolor;
				BiologicalNodeAbstract bna;
				for (HashSet<Integer> cluster : clusterdata) {
					if (cluster.size() < 5)
						continue;
					clustercolor = new Color((int) (Math.random() * 0x1000000));
					for (int nodeid : cluster) {
						bna = np.getNodeAssignmentbackwards(nodeid - 1);
						bna.setColor(clustercolor);
					}
				}
			}

			break;
		default:
			System.out.println("Wrong Job Type: returnComputeData - "
					+ toString());
			break;
		}

		// Set COLORS
		gc = new GraphColorizer(coloring, currentimageid, logview.isSelected());
		// recolor button enable after first Coloring, logview enabled
		colorizebutton.setEnabled(true);
		logview.setEnabled(true);
		resetcolorbutton.setEnabled(true);
	}

	public void returnComputeData(Hashtable<Integer, Double> table, int jobtype) {

		// Determine jobtype and behaviour
		switch (jobtype) {
		case JobTypes.CYCLE_JOB_OCCURRENCE:
			Hashtable<Integer, Double> cycledata = table;

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
					coloring.put(np.getNodeAssignmentbackwards(key), value);
				}

			}

			break;

		case JobTypes.CLIQUE_JOB_OCCURRENCE:
			Hashtable<Integer, Double> cliquedata = table;

			if (!cliquedata.isEmpty()) {
				// Map ids to BNAs
				Iterator<Entry<Integer, Double>> it = cliquedata.entrySet()
						.iterator();
				int key;
				double value;

				while (it.hasNext()) {
					Entry<Integer, Double> entry = it.next();
					key = entry.getKey();
					value = entry.getValue();
					// debug
					// System.out.println(key + " " + value);
					coloring.put(np.getNodeAssignmentbackwards(key), value);
				}
			}

			break;

		default:
			System.out.println("Wrong Job Type: returnComputeData - "
					+ toString());
			break;
		}

		// Set COLORS
		gc = new GraphColorizer(coloring, currentimageid, logview.isSelected());
		// recolor button enable after first Coloring, logview enabled
		colorizebutton.setEnabled(true);
		logview.setEnabled(true);
		resetcolorbutton.setEnabled(true);
	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if ("colorize".equals(command)) {
			recolorGraph();
			GraphInstance.getMyGraph().getVisualizationViewer().repaint();
		} else if ("resetcolors".equals(command)) {
			GraphContainer con = ContainerSingelton.getInstance();
			Iterator<BiologicalNodeAbstract> it = (Iterator<BiologicalNodeAbstract>) con
					.getPathway(
							MainWindowSingelton.getInstance()
									.getCurrentPathway()).getGraph()
					.getAllVertices().iterator();

			while (it.hasNext()) {
				bna = it.next();
				bna.setColor(greynodecolor);
			}

			GraphInstance.getMyGraph().getVisualizationViewer().repaint();

		} else if ("algorithm".equals(command)) {
			currentalgorithmindex = chooseAlgorithm.getSelectedIndex();
			for (int i = 0; i < colorrangeradio.length; i++) {
				if (colorrangeradio[i].isSelected()) {
					recolorGraph();
					break;
				}
			}
			GraphInstance.getMyGraph().getVisualizationViewer().repaint();
		} else if ("logview".equals(command)) {
			recolorGraph();
			GraphInstance.getMyGraph().getVisualizationViewer().repaint();
		} else if ("degreedistribution".equals(command)) {
			NetworkProperties np = new NetworkProperties();
			np.showDegreeDistrbutionFrame(np.getPathway().getName());			
		}
		
		// get proper icon path
		for (int i = 0; i < colorrangenames.length; i++) {
			if ((i + "").equals(command)) {
				currentimage = icons[i];
				currentimageid = i;
				recolorGraph();
				// repaint, damit Farben auch angezeigt werden
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
				break;
			}
		}
	}
}
