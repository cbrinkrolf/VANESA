package graph.algorithms.gui;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.NetworkProperties;
import graph.algorithms.NodeAttributeNames;
import graph.algorithms.NodeAttributeTypes;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingleton;
import gui.ProgressBar;
import gui.images.ImagePath;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import cluster.clientimpl.ClusterComputeThread;
import cluster.clientimpl.ComputeCallback;
import cluster.slave.JobTypes;
import cluster.slave.LayoutPoint2D;

public class GraphColoringGUI implements ActionListener {

	JPanel p = new JPanel();
	GraphElementAbstract ab;
	GraphInstance graphInstance;
	boolean emptyPane = true;

	private JComboBox<String> chooseAlgorithm;
	private JComboBox<ImageIcon> chooseColorPalette;
	private JButton colorizebutton;
	private JButton resetcolorbutton;
	private JButton degreedistributionbutton;

	private String[] algorithmNames = { "Node Degree", 
			"Cycles (remote)", "Cliques (remote)", "FRlayout (remote)",
			"Spectral apsp (remote)", "Multilayout (remote)",
			"MDS forcelayout (remote)", "APSP Clustering (r)" };
	private int currentalgorithmindex = 0;
	private String[] colorrangenames = { "bluesea", "skyline", "darkmiddle",
			"darkleftmiddle", "rainbow" };
	private final int NODE_DEGREE = 0, CYCLES = 1,
			CLIQUES = 2, FRLAYOUT = 3, SPECTRAL = 4, MULTILAYOUT = 5,
			MDSFLAYOUT = 6, APSPCLUSTERING = 7;

	private ImageIcon[] icons;

	private ImageIcon currentimage;
	private int currentimageid;
	private GraphColorizer gc;
	private JCheckBox logview;
	private ButtonGroup bg;

	// coloring variables
	private NetworkProperties np;
	private Iterator<BiologicalNodeAbstract> itn;
	private Hashtable<BiologicalNodeAbstract, Double> coloring;
	private BiologicalNodeAbstract bna;

	private final Color greynodecolor = new Color(-4144960);

	private MainWindow mw;
	private GraphContainer con;
	private Pathway pw;
	private MyGraph mg;

	private ComputeCallback helper;

	private HashMap<BiologicalNodeAbstract, Integer> nodeassignment;
	private HashMap<Integer, BiologicalNodeAbstract> nodeassignmentbackward;
	private BiologicalNodeAbstract from,to;

	private int nodeindex, edgeindex;
	private int[] edgearray;

	private ByteArrayOutputStream baos;
	private ObjectOutputStream oos;
	private byte[] jobinformation;
	private HashMap<String,String> parameters;

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
		
		chooseColorPalette = new JComboBox<ImageIcon>(icons);	
		chooseColorPalette.setActionCommand("colorpalette");
		chooseColorPalette.addActionListener(this);

		colorizebutton = new JButton("color selection");
		colorizebutton.setActionCommand("colorize");
		colorizebutton.addActionListener(this);

		resetcolorbutton = new JButton("reset colors");
		resetcolorbutton.setActionCommand("resetcolors");
		resetcolorbutton.addActionListener(this);

		MigLayout layout = new MigLayout("", "[][grow]", "");
		p.setLayout(layout);
		p.add(new JLabel("Algorithm"), "wrap");
		p.add(chooseAlgorithm, "wrap");
		p.add(new JLabel("Color Range"), "wrap");
		p.add(chooseColorPalette, "wrap");		

		logview = new JCheckBox("Data in log(10)");
		logview.setSelected(false);
		logview.setActionCommand("logview");
		logview.addActionListener(this);

		degreedistributionbutton = new JButton("Degree distribution");
		degreedistributionbutton.setActionCommand("degreedistribution");
		degreedistributionbutton.addActionListener(this);

		p.add(logview, "align left");
		p.add(colorizebutton, "align right, wrap");
		p.add(resetcolorbutton, "align left, wrap");
		p.add(degreedistributionbutton, "span 2, align right, wrap");

	}

	public void recolorGraph() throws IOException {

		np = new NetworkProperties();
		itn = np.getPathway().getAllNodes().iterator();
		coloring = new Hashtable<BiologicalNodeAbstract, Double>();

		int nodes = np.getPathway().countNodes();

		switch (currentalgorithmindex) {
		case NODE_DEGREE:
			// get current node degree values
			while (itn.hasNext()) {
				bna = itn.next();
				coloring.put(bna,
						(double) np.getNodeDegree(np.getNodeAssignment(bna)));
				// saving
				bna.addAttribute(NodeAttributeTypes.GRAPH_PROPERTY,
						NodeAttributeNames.NODE_DEGREE,
						np.getNodeDegree(np.getNodeAssignment(bna)));
			}
			break;
			
		case CYCLES:
			//Set parameters
			parameters = new HashMap<>();
			parameters.put("mincirclesize", ""+4);
			
			// open objectstream
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingleton.getInstance();
			mw.showProgressBar("attempting to queue job.");

						
			oos.writeObject(np.getAdjacencyMatrix());
			oos.writeObject(parameters);
						
			//close objectstream and transform to bytearray
			oos.close();			
			jobinformation = baos.toByteArray();
			
			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
				ClusterComputeThread rmicycles = new ClusterComputeThread(
						JobTypes.CYCLE_JOB_OCCURRENCE,jobinformation, helper);
				rmicycles.start();
			} catch (RemoteException e) {
				e.printStackTrace();
				reactiveateUI();
			}
			break;

		case CLIQUES:
			//Set parameters
			parameters = new HashMap<>();
			parameters.put("neigborrating", "--neighborsOff");
			parameters.put("connectivityrating", "--connectivityOff");

			// open objectstream
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingleton.getInstance();
			MainWindow.progressbar = new ProgressBar();
			MainWindow.progressbar.init(100, "Computing", true);
			MainWindow.progressbar.setProgressBarString("Setting up data.");

			oos.writeObject(np.getAdjacencyMatrix());
			oos.writeObject(parameters);
			
			//close objectstream and transform to bytearray
			oos.close();			
			jobinformation = baos.toByteArray();			
			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
				ClusterComputeThread rmicliques = new ClusterComputeThread(
						JobTypes.CLIQUE_JOB_OCCURRENCE, jobinformation, helper);
				rmicliques.start();
			} catch (RemoteException e) {
				e.printStackTrace();
				reactiveateUI();
			}
			break;

		case FRLAYOUT:
			//Set parameters
			parameters = new HashMap<>();
			parameters.put("width", ""+1200);
			parameters.put("height", ""+1200);
			parameters.put("iterations",""+700);
			parameters.put("temperaturecurve","const"); //linear/const
			parameters.put("attraction",""+1.0);
			parameters.put("repulsion",""+10.0);
			parameters.put("starttemperature",""+0.1);			
			
			// open objectstream
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingleton.getInstance();			
			mw.showProgressBar("attempting to queue job.");


			// get network structure
			con = ContainerSingelton.getInstance();
			pw = con.getPathway(mw.getCurrentPathway());
			mg = pw.getGraph();

			// setup assignment maps
			nodeassignment = new HashMap<BiologicalNodeAbstract, Integer>();
			nodeassignmentbackward = new HashMap<Integer, BiologicalNodeAbstract>();

			nodeindex = 0;

			// assign nodes
			for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
				nodeassignment.put(bna, nodeindex);
				nodeassignmentbackward.put(nodeindex, bna);
				nodeindex++;
			}

			// determine right edge amount
			edgeindex = 0;
			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
				if (bea.isDirected()) {
					edgeindex++;
				} else {
					edgeindex += 2;
				}
			}
			System.out.println("number of real edges: " + edgeindex
					+ "\n size: " + mg.getAllEdges().size());

			edgearray = new int[edgeindex * 2];
			edgeindex = 0;
			// build edgearray

			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
				from = bea.getFrom();
				to = bea.getTo();

				edgearray[edgeindex] = nodeassignment.get(from);
				edgeindex++;
				edgearray[edgeindex] = nodeassignment.get(to);
				edgeindex++;

				// if undirected, set second edge, too
				if (!bea.isDirected()) {
					edgearray[edgeindex] = nodeassignment.get(to);
					edgeindex++;
					edgearray[edgeindex] = nodeassignment.get(from);
					edgeindex++;
				}
			}
			
			oos.writeObject(mg.getAllVertices().size());
			oos.writeObject(edgearray);
			oos.writeObject(parameters);
			
			//close objectstream and transform to bytearray
			oos.close();			
			jobinformation = baos.toByteArray();
			
			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
				ClusterComputeThread rmifrlayout = new ClusterComputeThread(
						JobTypes.LAYOUT_FR_JOB, jobinformation, helper);
				rmifrlayout.start();
			} catch (RemoteException e) {
				e.printStackTrace();
				reactiveateUI();
			}
			break;

		case MULTILAYOUT:
			//Set parameters
			parameters = new HashMap<>();
			parameters.put("edgecutting", ""+0.6);
			parameters.put("seed", ""+(int) (Math.random()*1000));
			
			// open objectstream
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingleton.getInstance();
			mw.showProgressBar("attempting to queue job.");


			// get network structure

			con = ContainerSingelton.getInstance();
			pw = con.getPathway(mw.getCurrentPathway());
			mg = pw.getGraph();

			// setup assignment maps
			nodeassignment = new HashMap<BiologicalNodeAbstract, Integer>();
			nodeassignmentbackward = new HashMap<Integer, BiologicalNodeAbstract>();

			nodeindex = 0;

			// assign nodes
			for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
				nodeassignment.put(bna, nodeindex);
				nodeassignmentbackward.put(nodeindex, bna);
				nodeindex++;
			}

			// determine right edge amount
			edgeindex = 0;
			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
				if (bea.isDirected()) {
					edgeindex++;
				} else {
					edgeindex += 2;
				}
			}
			
			edgearray = new int[edgeindex * 2];
			edgeindex = 0;
			// build edgearray

			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
				from = bea.getFrom();
				to = bea.getTo();

				edgearray[edgeindex] = nodeassignment.get(from);
				edgeindex++;
				edgearray[edgeindex] = nodeassignment.get(to);
				edgeindex++;

				// only undirected graphs
				edgearray[edgeindex] = nodeassignment.get(to);
				edgeindex++;
				edgearray[edgeindex] = nodeassignment.get(from);
				edgeindex++;
			}
			
			oos.writeObject(mg.getAllVertices().size());
			oos.writeObject(edgearray);
			oos.writeObject(parameters);
			
			//close objectstream and transform to bytearray
			oos.close();			
			jobinformation = baos.toByteArray();

			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
				ClusterComputeThread rmimultilayout = new ClusterComputeThread(
						JobTypes.LAYOUT_MULTILEVEL_JOB, jobinformation, helper);
				rmimultilayout.start();
			} catch (RemoteException e) {
				e.printStackTrace();
				reactiveateUI();
			}
			break;

		case SPECTRAL:
			//Set parameters
			parameters = new HashMap<>();
			
			// open objectstream
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingleton.getInstance();
			mw.showProgressBar("attempting to queue job.");


			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
			} catch (RemoteException e) {
				e.printStackTrace();
				reactiveateUI();
			}
			
			oos.writeObject(np.getAdjacencyMatrix());
			oos.writeObject(parameters);
						
			//close objectstream and transform to bytearray
			oos.close();			
			jobinformation = baos.toByteArray();
			
			ClusterComputeThread rmispectral = new ClusterComputeThread(
					JobTypes.SPECTRAL_CLUSTERING_JOB, jobinformation, helper);
			rmispectral.start();

			oos.close();
			break;

		case MDSFLAYOUT:
			//Set parameters
			parameters = new HashMap<>();
			parameters.put("cycles", ""+50);
			parameters.put("edgeweighting", "-distance_by_degree"); //-distance_by_degree, -floyd, empty
			parameters.put("randomedgeweight", "yes");
						
			// open objectstream
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingleton.getInstance();
			mw.showProgressBar("attempting to queue job.");


			// get network structure
			con = ContainerSingelton.getInstance();
			pw = con.getPathway(mw.getCurrentPathway());
			mg = pw.getGraph();

			// setup assignment maps
			nodeassignment = new HashMap<BiologicalNodeAbstract, Integer>();
			nodeassignmentbackward = new HashMap<Integer, BiologicalNodeAbstract>();
			nodeindex = 0;

			// assign nodes
			for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
				nodeassignment.put(bna, nodeindex);
				nodeassignmentbackward.put(nodeindex, bna);
				nodeindex++;
			}

			// determine right edge amount
			edgeindex = 0;
			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
				if (bea.isDirected()) {
					edgeindex++;
				} else {
					edgeindex += 2;
				}
			}

			edgearray = new int[edgeindex];
			edgeindex = 0;
			// build edgearray

			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
				from = bea.getFrom();
				to = bea.getTo();

				edgearray[edgeindex] = nodeassignment.get(from);
				edgeindex++;
				edgearray[edgeindex] = nodeassignment.get(to);
				edgeindex++;

			}
						
			oos.writeObject(mg.getAllVertices().size());
			oos.writeObject(edgearray);
			oos.writeObject(parameters);
						
			//close objectstream and transform to bytearray
			oos.close();			
			jobinformation = baos.toByteArray();

			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
				ClusterComputeThread rmimdsflayout = new ClusterComputeThread(
						JobTypes.LAYOUT_MDS_FR_JOB, jobinformation, helper);
				rmimdsflayout.start();
			} catch (RemoteException e) {
				e.printStackTrace();
				reactiveateUI();
			}

			oos.close();
			break;
			
		case APSPCLUSTERING:
			//Set parameters
			parameters = new HashMap<>();
			parameters.put("minclustersize", ""+5);
			parameters.put("topclusters",""+50);
			HashMap<Integer, Double> experimentdata = new HashMap<>();
			
			
			// open objectstream
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingleton.getInstance();
			mw.showProgressBar("attempting to queue job.");


			// get network structure
			con = ContainerSingelton.getInstance();
			pw = con.getPathway(mw.getCurrentPathway());
			mg = pw.getGraph();

			// setup assignment maps
			nodeassignment = new HashMap<BiologicalNodeAbstract, Integer>();
			nodeassignmentbackward = new HashMap<Integer, BiologicalNodeAbstract>();

			nodeindex = 0;

			// assign nodes
			for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
				nodeassignment.put(bna, nodeindex);
				nodeassignmentbackward.put(nodeindex, bna);
				nodeindex++;
			}

			// determine right edge amount
			edgeindex = 0;
			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
				if (bea.isDirected()) {
					edgeindex++;
				} else {
					edgeindex += 2;
				}
			}
			System.out.println("number of real edges: " + edgeindex
					+ "\n size: " + mg.getAllEdges().size());

			edgearray = new int[edgeindex * 2];
			edgeindex = 0;
			// build edgearray
			BiologicalNodeAbstract from,
			to;
			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
				from = bea.getFrom();
				to = bea.getTo();

				edgearray[edgeindex] = nodeassignment.get(from);
				edgeindex++;
				edgearray[edgeindex] = nodeassignment.get(to);
				edgeindex++;

				// if undirected, set second edge, too
				if (!bea.isDirected()) {
					edgearray[edgeindex] = nodeassignment.get(to);
					edgeindex++;
					edgearray[edgeindex] = nodeassignment.get(from);
					edgeindex++;
				}
			}
			
			NodeAttribute att;
			BiologicalNodeAbstract gnode;
			for (int i = 0; i < nodes; i++) {
				gnode = np.getNodeAssignmentbackwards(i);
				att = gnode.getNodeAttributeByName("chol logFC");
				if(att != null)
					experimentdata.put(i,att.getDoublevalue());
				else
					experimentdata.put(i,0.0d);

			}
			oos.writeObject(mg.getAllVertices().size());
			oos.writeObject(edgearray);
			oos.writeObject(experimentdata);
			oos.writeObject(parameters);
			
			//close objectstream and transform to bytearray
			oos.close();			
			jobinformation = baos.toByteArray();
			
			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
				ClusterComputeThread rmiapspclustering = new ClusterComputeThread(
						JobTypes.APSP_CLUSTERING_JOB, jobinformation, helper);
				rmiapspclustering.start();
			} catch (RemoteException e) {
				e.printStackTrace();
				reactiveateUI();
			}
			break;

		}

		gc = new GraphColorizer(coloring, currentimageid, logview.isSelected());
	}

	private void printEdgeArray(int nodes, int[] edgearray) {
		try {
			FileWriter fw = new FileWriter("edjearray" + mw.getCurrentPathway());
			BufferedWriter out = new BufferedWriter(fw);

			out.write(nodes + "\n");
			out.write((edgearray.length / 2) + "\n");
			for (int i = 0; i < edgearray.length; i++) {
				out.write(edgearray[i] + "\t");
			}

			out.close();
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// //DEBUG print experimental data
		try {
			FileWriter fw = new FileWriter("experiments"
					+ mw.getCurrentPathway());
			BufferedWriter out = new BufferedWriter(fw);

			BiologicalNodeAbstract gnode;

			NodeAttribute att;
			for (int i = 0; i < nodes; i++) {
				gnode = np.getNodeAssignmentbackwards(i);
				att = gnode.getNodeAttributeByName("chol logFC");
				
				if(att != null)
					out.write(i + "\t" + att.getDoublevalue()+"\n");

			}
			out.close();
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void reactiveateUI() {
		// close Progress bar and reactivate UI
		mw = MainWindowSingleton.getInstance();
		mw.closeProgressBar();

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

		BiologicalNodeAbstract bna;
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
					bna = np.getNodeAssignmentbackwards(key);
					coloring.put(bna, value);
					// saving
					bna.addAttribute(NodeAttributeTypes.GRAPH_PROPERTY,
							NodeAttributeNames.CYCLES, coloring.get(bna));
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
					bna = np.getNodeAssignmentbackwards(key);
					coloring.put(bna, value);
					// saving
					bna.addAttribute(NodeAttributeTypes.GRAPH_PROPERTY,
							NodeAttributeNames.CLIQUES, coloring.get(bna));
				}
			}

			break;
			
		case JobTypes.APSP_CLUSTERING_JOB:
			if (!table.isEmpty()) {
				// Map ids to BNAs
				Iterator<Entry<Integer, Double>> it = table.entrySet()
						.iterator();
				int key;
				double value;

				while (it.hasNext()) {
					Entry<Integer, Double> entry = it.next();
					key = entry.getKey();
					value = entry.getValue();
					bna = np.getNodeAssignmentbackwards(key);
					coloring.put(bna, value);
					// saving
					bna.addAttribute(NodeAttributeTypes.GRAPH_PROPERTY,
							NodeAttributeNames.SP_CLUSTERING, coloring.get(bna));
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

		try {

			String command = e.getActionCommand();

			if ("colorize".equals(command)) {
				recolorGraph();
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
			} else if ("resetcolors".equals(command)) {
				GraphInstance.getMyGraph().getAllVertices().stream()
						.forEach(bna -> bna.resetAppearance());
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();

			} else if ("algorithm".equals(command)) {
				currentalgorithmindex = chooseAlgorithm.getSelectedIndex();				
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
			} else if ("logview".equals(command)) {
				recolorGraph();
				GraphInstance.getMyGraph().getVisualizationViewer().repaint();
			} else if ("degreedistribution".equals(command)) {
				NetworkProperties np = new NetworkProperties();
				HashMap<Integer, Integer> degreemap = np.getNodeDegreeDistribution();
				ValueComparator bvc = new ValueComparator(degreemap);
				TreeMap<Integer, Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
				sorted_map.putAll(degreemap);
				new NodeAttributeBarChart("Statistics","Node degree distibution", "Degree", "Count", sorted_map);
				
			} else if ("colorpalette".equals(command)){
				currentimageid = chooseColorPalette.getSelectedIndex();			}

			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void realignNetwork(HashMap<Integer, LayoutPoint2D> coords) {
		// get network structure
		MainWindow w = MainWindowSingleton.getInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		Pathway pw = con.getPathway(w.getCurrentPathway());
		MyGraph mg = pw.getGraph();

		for (Entry<Integer, LayoutPoint2D> entry : coords.entrySet()) {
			// get bna from assignment
			// tmppoint= new
			mg.getVisualizationViewer()
					.getModel()
					.getGraphLayout()
					.setLocation(
							nodeassignmentbackward.get(entry.getKey()),
							new Point((int) entry.getValue().getX(),
									(int) entry.getValue().getY()));
		}
	}
}
