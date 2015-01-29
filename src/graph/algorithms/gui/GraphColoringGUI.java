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
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.miginfocom.swing.MigLayout;
import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import cluster.ClusterComputeThread;
import cluster.ComputeCallback;
import cluster.JobTypes;
import cluster.LayoutPoint2D;

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
			"Cycles (remote)", "Cliques (remote)", "FRlayout (remote)",
			"Spectral apsp (remote)", "Multilayout (remote)" };
	private int currentalgorithmindex = 0;
	private String[] colorrangenames = { "bluesea", "skyline", "darkmiddle",
			"darkleftmiddle", "rainbow" };
	private final int NODE_DEGREE = 0, NEIGHBOR_DEGREE = 1, CYCLES = 2,
			CLIQUES = 3, FRLAYOUT = 4, SPECTRAL = 5, MULTILAYOUT = 6;

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
	private GraphContainer con;
	private Pathway pw;
	private MyGraph mg;

	private ComputeCallback helper;
	
	private HashMap<BiologicalNodeAbstract, Integer> nodeassignment;
	private HashMap<Integer, BiologicalNodeAbstract> nodeassignmentbackward;
	
	private int nodeindex, edgeindex;
	private BiologicalNodeAbstract from, to;
	private int[] edgearray;

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

		int nodes = np.getPathway().countNodes(),
		nodewithAttribute = 0;
		
		switch (currentalgorithmindex) {
		case NODE_DEGREE:
//			//CHANGE 			
//			Pathway pwxxx =  np.getPathway();
//			
//			
//			ChangedFlags cf = pwxxx.getChangedFlags("graphanalysis");
//			System.out.println(cf.isNodeChanged());
//			cf.reset();
//			System.out.println(cf.isNodeChanged());
			
			//check for existance
			nodes = np.getPathway().countNodes();
			nodewithAttribute = 0;
			for(BiologicalNodeAbstract bn: np.getPathway().getAllNodes()){
				if(bn.getNodeAttributeByName(NodeAttributeNames.NODE_DEGREE)!= null){
					nodewithAttribute++;
				}
			}
			
			//not existant
			if(nodewithAttribute!=nodes){
				//get current node degree values
				while (itn.hasNext()) {
					bna = itn.next();
					coloring.put(bna, (double) np.getNodeDegree(np
							.getNodeAssignment(bna)));
					// saving
					bna.addAttribute(NodeAttributeTypes.GRAPH_PROPERTY,
							NodeAttributeNames.NODE_DEGREE,
							np.getNodeDegree(np.getNodeAssignment(bna)));
				}
				
			//already exists
			}else{
				while (itn.hasNext()) {
					bna = itn.next();
					coloring.put(bna, bna.getNodeAttributeByName(NodeAttributeNames.NODE_DEGREE).getDoublevalue());
				}
			}
			
			break;
			
		case NEIGHBOR_DEGREE:
			//check for existance
			nodes = np.getPathway().countNodes();
			nodewithAttribute = 0;
			for(BiologicalNodeAbstract bn: np.getPathway().getAllNodes()){
				if(bn.getNodeAttributeByName(NodeAttributeNames.NEIGHBOR_DEGREE)!= null){
					nodewithAttribute++;
				}
			}
			
			//not existant
			if(nodewithAttribute!=nodes){
				coloring = np.averageNeighbourDegreeTable();
				
				//saving
				while (itn.hasNext()) {
					bna = itn.next();
					bna.addAttribute(NodeAttributeTypes.GRAPH_PROPERTY,
							NodeAttributeNames.NEIGHBOR_DEGREE,
							coloring.get(bna));
				}
				
				
			//already exists	
			}else{
				while (itn.hasNext()) {
					bna = itn.next();
					coloring.put(bna, bna.getNodeAttributeByName(NodeAttributeNames.NEIGHBOR_DEGREE).getDoublevalue());
				}
			}
			
			
			break;
		case CYCLES:
			
			//check for existance
			nodewithAttribute = 0;
			for(BiologicalNodeAbstract bn: np.getPathway().getAllNodes()){
				if(bn.getNodeAttributeByName(NodeAttributeNames.CYCLES)!= null){
					nodewithAttribute++;
				}
			}
			
			//not existant
			if(nodewithAttribute == 0){
				// Lock UI and initiate Progress Bar
				mw = MainWindowSingleton.getInstance();
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
				//Saving in different thread
				
			//already exists
			}else{
				while (itn.hasNext()) {
					bna = itn.next();
					if(bna.hasAttributeByName(NodeAttributeNames.CYCLES)){
						coloring.put(bna, bna.getNodeAttributeByName(NodeAttributeNames.CYCLES).getDoublevalue());
					}
				}				
			}
			
			
			

			break;
		case CLIQUES:
			
			//check for existance
			nodewithAttribute = 0;
			for(BiologicalNodeAbstract bn: np.getPathway().getAllNodes()){
				if(bn.getNodeAttributeByName(NodeAttributeNames.CLIQUES)!= null){
					nodewithAttribute++;
				}
			}
			
			//not existant
			if(nodewithAttribute == 0){
				// Lock UI and initiate Progress Bar
				mw = MainWindowSingleton.getInstance();
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
			}else{
				while (itn.hasNext()) {
					bna = itn.next();
					if(bna.hasAttributeByName(NodeAttributeNames.CLIQUES)){
						coloring.put(bna, bna.getNodeAttributeByName(NodeAttributeNames.CLIQUES).getDoublevalue());
					}
				}							
			}
			
			

			break;
			
		case FRLAYOUT:
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingleton.getInstance();
			mw.setLockedPane(true);
			progressbar = new ProgressBar();
			progressbar.init(100, "Computing", true);
			progressbar.setProgressBarString("Setting up data.");

			
			
			//get network structure
			con = ContainerSingelton.getInstance();
			pw = con.getPathway(mw.getCurrentPathway());
			mg = pw.getGraph();

			//setup assignment maps
			nodeassignment = new HashMap<BiologicalNodeAbstract, Integer>();
			nodeassignmentbackward = new HashMap<Integer, BiologicalNodeAbstract>();
						
			nodeindex = 0;
			
			//assign nodes
			for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
				nodeassignment.put(bna, nodeindex);
				nodeassignmentbackward.put(nodeindex,bna);
				nodeindex++;
			}

			//determine right edge amount
			edgeindex = 0;
			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
				if(bea.isDirected()){
					edgeindex++;
				}else{
					edgeindex+=2;
				}
			}			
			System.out.println("number of real edges: "+edgeindex+"\n size: "+mg.getAllEdges().size());
						
			edgearray = new int[edgeindex*2];
			edgeindex = 0;
			//build edgearray
			BiologicalNodeAbstract from, to;
			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
				from = bea.getFrom();
				to = bea.getTo();
				
				edgearray[edgeindex] = nodeassignment.get(from);
				edgeindex++;
				edgearray[edgeindex] = nodeassignment.get(to);
				edgeindex++;
				
				//if undirected, set second edge, too
				if(!bea.isDirected()){
					edgearray[edgeindex] = nodeassignment.get(to);
					edgeindex++;
					edgearray[edgeindex] = nodeassignment.get(from);
					edgeindex++;					
				}
			}			
// DEBUG			
//			for (int i = 0; i < edgearray.length; i++) {
//				System.out.print(nodeassignmentbackward.get(edgearray[i]).getLabel()+",");
//				if((i+1) %2 == 0)
//					System.out.println();
//			}

//			printEdgeArray(mg.getAllVertices().size(), edgearray);
			
			
			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
				ClusterComputeThread rmifrlayout = new ClusterComputeThread(
						JobTypes.LAYOUT_FR_JOB, helper);
				rmifrlayout.setEdgeArray(edgearray);
				rmifrlayout.setNodes(nodeassignment.size());
				rmifrlayout.start();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//reactiveateUI();
			
			
			break;	
			
		case MULTILAYOUT:
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingleton.getInstance();
			mw.setLockedPane(true);
			progressbar = new ProgressBar();
			progressbar.init(100, "Computing", true);
			progressbar.setProgressBarString("Setting up data.");

			//get network structure
			
			con = ContainerSingelton.getInstance();
			pw = con.getPathway(mw.getCurrentPathway());
			mg = pw.getGraph();

			//setup assignment maps
			nodeassignment = new HashMap<BiologicalNodeAbstract, Integer>();
			nodeassignmentbackward = new HashMap<Integer, BiologicalNodeAbstract>();
						
			nodeindex = 0;
			
			//assign nodes
			for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
				nodeassignment.put(bna, nodeindex);
				nodeassignmentbackward.put(nodeindex,bna);
				nodeindex++;
			}

			//determine right edge amount
			edgeindex = 0;
			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
				if(bea.isDirected()){
					edgeindex++;
				}else{
					edgeindex+=2;
				}
			}			
			System.out.println("number of real edges: "+edgeindex+"\n size: "+mg.getAllEdges().size());
						
			edgearray = new int[edgeindex*2];
			edgeindex = 0;
			//build edgearray

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
// DEBUG			
//			for (int i = 0; i < edgearray.length; i++) {
//				System.out.print(nodeassignmentbackward.get(edgearray[i]).getLabel()+",");
//				if((i+1) %2 == 0)
//					System.out.println();
//			}

//			printEdgeArray(mg.getAllVertices().size(), edgearray);
			
			
			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
				ClusterComputeThread rmimultilayout = new ClusterComputeThread(
						JobTypes.LAYOUT_MULTILEVEL_JOB, helper);
				rmimultilayout.setEdgeArray(edgearray);
				rmimultilayout.setNodes(nodeassignment.size());
				rmimultilayout.start();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//reactiveateUI();
			
			
			break;
		case SPECTRAL:
			// Lock UI and initiate Progress Bar
			mw = MainWindowSingleton.getInstance();
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

	private void printEdgeArray(int nodes, int[] edgearray) {
		try {
			FileWriter fw = new FileWriter("edjearray"+mw.getCurrentPathway());
			BufferedWriter out = new BufferedWriter(fw);
			
			out.write(nodes+"\n");
			out.write((edgearray.length/2)+"\n");
			for (int i = 0; i < edgearray.length; i++) {
				out.write(edgearray[i]+"\t");
			}
			
			out.close();
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		// TODO Auto-generated method stub
		
	}

	public void reactiveateUI() {
		// close Progress bar and reactivate UI
		GraphColoringGUI.progressbar.closeWindow();
		mw = MainWindowSingleton.getInstance();
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
					//saving
					bna.addAttribute(NodeAttributeTypes.GRAPH_PROPERTY,
							NodeAttributeNames.CYCLES,
							coloring.get(bna));
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
					//saving
					bna.addAttribute(NodeAttributeTypes.GRAPH_PROPERTY,
							NodeAttributeNames.CLIQUES,
							coloring.get(bna));
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
			GraphInstance.getMyGraph().getAllVertices().stream().
				forEach(bna -> bna.resetAppearance());
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

	public void realignNetwork(HashMap<Integer, LayoutPoint2D> coords) {
		//get network structure
		MainWindow w = MainWindowSingleton.getInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		Pathway pw = con.getPathway(w.getCurrentPathway());
		MyGraph mg = pw.getGraph();
		
		for (Entry<Integer, LayoutPoint2D> entry : coords.entrySet()) {
			//get bna from assignment
			//tmppoint= new 
			mg.getVisualizationViewer().getModel().getGraphLayout()
			.setLocation(nodeassignmentbackward.get(entry.getKey()),
					new Point((int)entry.getValue().getX(), (int)entry.getValue().getY()));
		}		
	}
}
