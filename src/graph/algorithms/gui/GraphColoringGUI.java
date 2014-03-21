package graph.algorithms.gui;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.NetworkProperties;
import graph.jung.classes.MyGraph;
import gui.MainWindowSingelton;
import gui.images.ImagePath;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cluster.ClusterComputeClient;

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

	private String[] algorithmNames = { "Node Degree", "Neighbor Degree",
			"Cycles (cluster)", "Cliques (cluster)", "Paths (cluster)" };
	private int currentalgorithmindex = 0;
	private String[] colorrangenames = { "bluesea", "skyline", "darkmiddle",
			"darkleftmiddle", "rainbow" };
	private final int NODE_DEGREE = 0, NEIGHBOR_DEGREE = 1, CYCLES = 2,
			CLIQUES = 3, PATHRATING = 4;

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

		p.add(logview, "align left");
		p.add(colorizebutton, "align right, wrap");
		p.add(resetcolorbutton, "align left, wrap");

	}

	public void recolorGraph() {

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
			// compute values over RMI
			ClusterComputeClient rmicycles = new ClusterComputeClient(
					ClusterComputeClient.CYCLE_JOB_OCCURRENCE);
			rmicycles.setAdjMatrix(np.getAdjacencyMatrix());
			if (rmicycles.start()) {
				Hashtable<Integer, Double> cycledata = rmicycles
						.getResultTable();

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
			}

			break;
		case CLIQUES:
			// compute values over RMI
			ClusterComputeClient rmicliques = new ClusterComputeClient(
					ClusterComputeClient.CLIQUE_JOB_OCCURRENCE);
			rmicliques.setAdjMatrix(np.getAdjacencyMatrix());
			if (rmicliques.start()) {
				Hashtable<Integer, Double> cliquedata = rmicliques
						.getResultTable();

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
						//System.out.println(key + " " + value);
						coloring.put(np.getNodeAssignmentbackwards(key), value);
					}
				}
			}

			break;
		case PATHRATING:
			ClusterComputeClient rmiapsp = new ClusterComputeClient(
					ClusterComputeClient.APSP_JOB);
			rmiapsp.start();
			break;

		default:
			break;
		}

		gc = new GraphColorizer(coloring, currentimageid, logview.isSelected());
		// recolor button enable after first Coloring, logview enabled
		colorizebutton.setEnabled(true);
		logview.setEnabled(true);
		resetcolorbutton.setEnabled(true);
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
