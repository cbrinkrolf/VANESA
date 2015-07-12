package graph.algorithms.gui;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.NetworkProperties;
import graph.algorithms.NodeAttributeNames;
import graph.algorithms.NodeAttributeTypes;
import graph.algorithms.gui.clusters.GraphClusterDyer;
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.TreeSet;

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
			"Cycles (r)", "Cliques (r)", "FRlayout (r)",
			"Spectral apsp (r)", "Multilayout (remote)",
			"MDS forcelayout (r)", "APSP Clustering occ (r)",
			"APSP Clustering score (r)",
			"DCB clusters(r)",
			"DCB grid(r)"};
	private int currentalgorithmindex = 0;

	private final int NODE_DEGREE = 0, CYCLES = 1,
			CLIQUES = 2, FRLAYOUT = 3, SPECTRAL = 4, MULTILAYOUT = 5,
			MDSFLAYOUT = 6, APSPCLUSTERING_OCC = 7, APSPCLUSTERING_SCORE = 8,
			DCB_CLUSTERS = 9, DCB_GRID = 10;

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

	private MainWindow mw;
	private GraphContainer con;
	private Pathway pw;
	private MyGraph mg;

	private ComputeCallback helper;

	private HashMap<BiologicalNodeAbstract, Integer> nodeassignment;
	private HashMap<Integer, BiologicalNodeAbstract> nodeassignmentbackward;
	private BiologicalNodeAbstract from,to;
	private NodeAttribute att;
	private BiologicalNodeAbstract gnode;

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
		itn = np.getPathway().getAllGraphNodes().iterator();
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

			
			//REGULAR
			// determine right edge amount
//			edgeindex = 0;
//			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
//				if (bea.isDirected()) {
//					edgeindex++;
//				} else {
//					edgeindex += 2;
//				}
//			}
//			System.out.println("number of real edges: " + edgeindex
//					+ "\n size: " + mg.getAllEdges().size());
//
//			edgearray = new int[edgeindex * 2];
//			edgeindex = 0;
//			// build edgearray
//
//			for (BiologicalEdgeAbstract bea : mg.getAllEdges()) {
//				from = bea.getFrom();
//				to = bea.getTo();
//
//				edgearray[edgeindex] = nodeassignment.get(from);
//				edgeindex++;
//				edgearray[edgeindex] = nodeassignment.get(to);
//				edgeindex++;
//
//				// if undirected, set second edge, too
//				if (!bea.isDirected()) {
//					edgearray[edgeindex] = nodeassignment.get(to);
//					edgeindex++;
//					edgearray[edgeindex] = nodeassignment.get(from);
//					edgeindex++;
//				}
//			}
			
			
			//Property based
			HashMap<String, HashSet<BiologicalNodeAbstract>> locales = new HashMap<String, HashSet<BiologicalNodeAbstract>>();
			edgeindex = 0;
			String locale; HashSet<BiologicalNodeAbstract> tmpset;
			for (BiologicalNodeAbstract bna : mg.getAllVertices()) {
				for(NodeAttribute na : bna.getNodeAttributesByType(NodeAttributeTypes.ANNOTATION)){
					if(na!= null && na.getName().equals(NodeAttributeNames.GO_CELLULAR_COMPONENT)){
						locale = na.getStringvalue();
						System.out.println(bna.getLabel() + "\t"+na.getStringvalue());
						if(!locales.containsKey(na.getStringvalue())){
							tmpset = new HashSet<BiologicalNodeAbstract>();
							tmpset.add(bna);
							locales.put(locale, tmpset);
						}else{
							locales.get(locale).add(bna);
						}						
					}
				}
			}
			
			//Count edges first
			int edges = 0,size;
			for(Entry<String, HashSet<BiologicalNodeAbstract>> e: locales.entrySet()){
				size = e.getValue().size();
				if(size>1){
					edges+=(2*size*(size-1));
				}
			}
			
			System.out.println("network has "+ edges+ " EDGES ");
			edgearray = new int[edges];
			
			
			BiologicalNodeAbstract[] bnaarr;
			for(Entry<String, HashSet<BiologicalNodeAbstract>> e: locales.entrySet()){
				bnaarr = new BiologicalNodeAbstract[e.getValue().size()];
				e.getValue().toArray(bnaarr);
				
				if(bnaarr.length>1){
					for(int i = 0; i<bnaarr.length;i++){
						for (int j = i+1; j < bnaarr.length; j++) {
							edgearray[edgeindex] = nodeassignment.get(bnaarr[i]);
							edgeindex++;
							edgearray[edgeindex] = nodeassignment.get(bnaarr[j]);
							edgeindex++;
							//add both sides of the connection
							edgearray[edgeindex] = nodeassignment.get(bnaarr[j]);
							edgeindex++;
							edgearray[edgeindex] = nodeassignment.get(bnaarr[i]);
							edgeindex++;						
						}
					}
				}			
			}
			
			
			
			//DEBUG
			
			
			
			
			
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
			parameters.put("edgecutting", ""+0.86);
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
			
		case DCB_CLUSTERS:
		case DCB_GRID:
			//Set parameters
			parameters = new HashMap<>();
			parameters.put("alpha",""+0.9);
			parameters.put("delta",""+5);
			parameters.put("omega",""+0.15);
			parameters.put("topclusters",""+50);
			HashMap<Integer, ArrayList<Double>> experimentdata = new HashMap<>();
			
			
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

				// if undirected, set second edge, too
				if (!bea.isDirected()) {
					edgearray[edgeindex] = nodeassignment.get(to);
					edgeindex++;
					edgearray[edgeindex] = nodeassignment.get(from);
					edgeindex++;
				}
			}
			
			ArrayList<Double> values;
			int experiments = 7;
			for (int i = 0; i < nodes; i++) {
				gnode = np.getNodeAssignmentbackwards(i);
				values = new ArrayList<Double>(7);
				for(int j = 1; j <= experiments; j++){
					att = gnode.getNodeAttributeByName("Chol"+j);
					if(att != null)
						values.add(att.getDoublevalue());
				}
				
				if(values.size() == experiments)
					experimentdata.put(i, values);
			}
			
			//DEBUG
			System.out.println(experimentdata.size()+"\n"+experimentdata);
			
			
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
				if (currentalgorithmindex == DCB_CLUSTERS) {
					ClusterComputeThread rmiapspclustering = new ClusterComputeThread(
							JobTypes.DCB_CLUSTERING_CLUSTERS,
							jobinformation, helper);
					rmiapspclustering.start();
				} else if (currentalgorithmindex == DCB_GRID) {
					ClusterComputeThread rmiapspclustering = new ClusterComputeThread(
							JobTypes.DCB_CLUSTERING_GRID,
							jobinformation, helper);
					rmiapspclustering.start();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				reactiveateUI();
			}
			break;
			
		case APSPCLUSTERING_OCC:
		case APSPCLUSTERING_SCORE:
			//Set parameters
			parameters = new HashMap<>();
			parameters.put("minclustersize", ""+5);
			parameters.put("topclusters",""+50);
			HashMap<Integer, Double> singleexperimentdata = new HashMap<>();
			
			
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

				// if undirected, set second edge, too
				if (!bea.isDirected()) {
					edgearray[edgeindex] = nodeassignment.get(to);
					edgeindex++;
					edgearray[edgeindex] = nodeassignment.get(from);
					edgeindex++;
				}
			}
			

			for (int i = 0; i < nodes; i++) {
				gnode = np.getNodeAssignmentbackwards(i);
				att = gnode.getNodeAttributeByName("chol logFC");
				if(att != null)
					singleexperimentdata.put(i,att.getDoublevalue());
			}
			

			/*PrintWriter out = new PrintWriter("GBM.exp","UTF-8");
			out.println(nodes);
			out.println(6);
			for (int i = 0; i < nodes; i++) {
				String line = "";
				gnode = np.getNodeAssignmentbackwards(i);
				// patient data export
				for (int p = 1; p <= 6; p++) {
					att = gnode.getNodeAttributeByName("GBM" + p);
					if (att != null) {
						if(p<6)
							line+= att.getDoublevalue()+"\t";
						else
							line+= att.getDoublevalue();
					}
				}
				
				if(line.length()>0){
					out.println(i+"\t"+line);
				}				
			}
			
			out.close();*/

			
			
			
			oos.writeObject(mg.getAllVertices().size());
			oos.writeObject(edgearray);
			oos.writeObject(singleexperimentdata);
			oos.writeObject(parameters);
			
			//close objectstream and transform to bytearray
			oos.close();			
			jobinformation = baos.toByteArray();

			// compute values over RMI
			try {
				helper = new ComputeCallback(this);
				if (currentalgorithmindex == APSPCLUSTERING_OCC) {
					ClusterComputeThread rmiapspclustering = new ClusterComputeThread(
							JobTypes.APSP_CLUSTERING_JOB_OCCURENCE,
							jobinformation, helper);
					rmiapspclustering.start();
				} else if (currentalgorithmindex == APSPCLUSTERING_SCORE) {
					ClusterComputeThread rmiapspclustering = new ClusterComputeThread(
							JobTypes.APSP_CLUSTERING_JOB_SCORING,
							jobinformation, helper);
					rmiapspclustering.start();
				}
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
	
	public void returnComputeData(HashMap<Double, HashSet<Integer>> map,
			int jobtype) {

		BiologicalNodeAbstract bna;
		// Determine jobtype and behaviour
		switch (jobtype) {
		case JobTypes.APSP_CLUSTERING_JOB_SCORING:
			if (!map.isEmpty()) {
				
				TreeMap<Double, TreeSet<String>> dataset = new TreeMap<>();
				TreeSet<String> tmpset;
				
				// Map ids to BNAs
				Iterator<Entry<Double, HashSet<Integer>>> it = map.entrySet()
						.iterator();
				double key;
				HashSet<Integer> value;
				TreeSet<String> clusterlabels;

				
				
				while (it.hasNext()) {
					Entry<Double, HashSet<Integer>> entry = it.next();
					key = entry.getKey();
					value = entry.getValue();
					
					//cluster viewer
					clusterlabels = new TreeSet<>();
					for(Integer nodeid : value){
						bna = np.getNodeAssignmentbackwards(nodeid);
						clusterlabels.add(bna.getLabel());
					}					
					
					dataset.put(key, clusterlabels);
				}
				
				new GraphClusterDyer(dataset);
				
			}
			
			break;
		case JobTypes.DCB_CLUSTERING_CLUSTERS:
			
			System.out.println("DONE");
			if (!map.isEmpty()) {
				
				TreeMap<Double, TreeSet<String>> dataset = new TreeMap<>();
				TreeSet<String> tmpset;
				
				// Map ids to BNAs
				Iterator<Entry<Double, HashSet<Integer>>> it = map.entrySet()
						.iterator();
				double key;
				HashSet<Integer> value;
				TreeSet<String> clusterlabels;

				
				
				while (it.hasNext()) {
					Entry<Double, HashSet<Integer>> entry = it.next();
					key = entry.getKey();
					value = entry.getValue();
					
					//cluster viewer
					clusterlabels = new TreeSet<>();
					for(Integer nodeid : value){
						bna = np.getNodeAssignmentbackwards(nodeid);
						clusterlabels.add(bna.getLabel());
					}					
					
					dataset.put(key, clusterlabels);
				}
				
				new GraphClusterDyer(dataset);
				
			}
			
			break;
	

		default:
			System.out.println("Wrong Job Type: returnComputeData - "
					+ toString());
			break;
		}
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
			
		case JobTypes.APSP_CLUSTERING_JOB_OCCURENCE:
			if (!table.isEmpty()) {
				
				TreeMap<Double, TreeSet<String>> dataset = new TreeMap<>();
				TreeSet<String> tmpset;
				
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
					
					//cluster viewer
						
					if(dataset.containsKey(value)){
						dataset.get(value).add(bna.getLabel());
					}else{
						tmpset = new TreeSet<>();
						tmpset.add(bna.getLabel());	
						dataset.put(value, tmpset);
					}
				
					// saving
					bna.addAttribute(NodeAttributeTypes.GRAPH_PROPERTY,
							NodeAttributeNames.SP_CLUSTERING, coloring.get(bna));
				}
				
				System.out.println(dataset);
				new GraphClusterDyer(dataset);
				
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
				TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(new NumbersThenWordsComparator());
				
				for(Entry<Integer,Integer> entry: np.getNodeDegreeDistribution().entrySet()){
					sorted_map.put(entry.getKey()+"", entry.getValue());
					
				}
				
				
				//sort by occurrence
//				ValueComparator bvc = new ValueComparator(degreemap);
//				TreeMap<Integer, Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
//				sorted_map.putAll(degreemap);
	
				//sort by key value
				
				
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
	
	class NumbersThenWordsComparator implements Comparator<String> {
	    private Integer intValue(String s) {
	        try {
	            return Integer.valueOf(s);
	        } catch (NumberFormatException e) {
	            return null;
	        }
	    }

	    @Override
	    public int compare(String s1, String s2) {
	        Integer i1 = intValue(s1);
	        Integer i2 = intValue(s2);
	        if (i1 == null && i2 == null) {
	            return s1.compareTo(s2);
	        } else if (i1 == null) {
	            return -1;
	        } else if (i2 == null) {
	            return 1;
	        } else {
	            return i1.compareTo(i2);
	        }
	    }       
	}



}
