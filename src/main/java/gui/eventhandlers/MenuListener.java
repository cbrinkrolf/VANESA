package gui.eventhandlers;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

import graph.jung.classes.MyGraph;
import org.apache.batik.ext.awt.image.codec.png.PNGEncodeParam;
import org.apache.batik.ext.awt.image.codec.png.PNGImageEncoder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DynamicNode;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import configurations.ProgramFileLock;
import configurations.gui.LayoutConfig;
import configurations.gui.Settings;
import dataMapping.DataMappingColorMVC;
import database.mirna.MirnaStatistics;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.CreatePathway;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.gui.RandomBipartiteGraphGui;
import graph.algorithms.gui.RandomConnectedGraphGui;
import graph.algorithms.gui.RandomGraphGui;
import graph.algorithms.gui.RandomHamiltonGraphGui;
import graph.algorithms.gui.RandomRegularGraphGui;
import graph.algorithms.gui.smacof.view.SmacofView;
import graph.layouts.gemLayout.GEMLayout;
import graph.layouts.hctLayout.HCTLayout;
import graph.layouts.hebLayout.HEBLayout;
import gui.AboutWindow;
import gui.AllPopUpsWindow;
import gui.InfoWindow;
import gui.LabelToDataMappingWindow;
import gui.LabelToDataMappingWindow.InputFormatException;
import gui.MainWindow;
import gui.MyPopUp;
import gui.NodesEdgesTypesWindow;
import gui.visualization.PreRenderManager;
import io.OpenDialog;
import io.PNDoc;
import io.SaveDialog;
import miscalleanous.tables.MyTable;
import petriNet.Cov;
import petriNet.CovNode;
import petriNet.OpenModelicaResult;
import petriNet.PNTableDialog;
import petriNet.ReachController;
import petriNet.SimpleMatrixDouble;
import transformation.Rule;
import transformation.RuleManager;
import transformation.TransformationInformation;
import transformation.Transformator;
import transformation.gui.RuleManagementWindow;
import transformation.gui.TransformationInformationWindow;
import util.KineticBuilder;
import util.VanesaUtility;
import xmlOutput.sbml.JSBMLoutput;

public class MenuListener implements ActionListener {
	private MyTable tP;
	private MyTable tT;
	private MyTable tI;
	private Object[][] rP;
	private Object[][] rT;
	private Object[][] rI;
	private ArrayList<Double> start = new ArrayList<>();

	private JLabel invariant = new JLabel();
	private JPanel pane = new JPanel();
	private JDialog d = new JDialog();

	private JLabel reachable = new JLabel();

	private DenseDoubleMatrix2D c;

	private int places = 0;
	private int transitions = 0;
	private MainWindow w;

	private Cov cov;
	private static MenuListener instance;

	private MenuListener() {
	}

	public static synchronized MenuListener getInstance() {
		if (MenuListener.instance == null) {
			MenuListener.instance = new MenuListener();
		}
		return MenuListener.instance;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		w = MainWindow.getInstance();
		String event = e.getActionCommand();
		final GraphInstance graphInstance = new GraphInstance();
		GraphContainer con = GraphContainer.getInstance();
		MenuActionCommands command = MenuActionCommands.get(event);
		if (command == null) {
			return;
		}

		switch (command) {
		case newNetwork:
			int option = JOptionPane.showOptionDialog(w.getFrame(), "Which type of modeling do you prefer?",
					"Choose Network Type...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					new String[] { "Biological Graph", "Petri Net" }, JOptionPane.CANCEL_OPTION);
			if (option != -1) {
				new CreatePathway();
				graphInstance.getPathway().setIsPetriNet(option == JOptionPane.NO_OPTION);
				w.getBar().paintToolbar(option == JOptionPane.NO_OPTION);
				w.updateAllGuiElements();
			}
			break;
		case openNetwork:
			OpenDialog op = new OpenDialog();
			op.execute();
			break;
		case closeNetwork:
			w.removeTab(true);
			break;
		case closeAllNetworks:
			w.removeAllTabs();
			break;
		case mathGraph:
			new RandomGraphGui();
			break;
		case biGraph:
			new RandomBipartiteGraphGui();
			break;
		case regularGraph:
			new RandomRegularGraphGui();
			break;
		case phospho:
			op = new OpenDialog();
			op.execute();
			break;
		case connectedGraph:
			new RandomConnectedGraphGui();
			break;
		case hamiltonGraph:
			new RandomHamiltonGraphGui();
			break;
		case exportNetwork:
			// System.out.println("Nodes:
			// "+graphInstance.getMyGraph().getAllVertices().size());
			// System.out.println("Edges:
			// "+graphInstance.getMyGraph().getAllEdges().size());
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new SaveDialog( // GRAPHML+MO+GON=14
							SaveDialog.FORMAT_GRAPHML + SaveDialog.FORMAT_MO + SaveDialog.FORMAT_GON
							// + SaveDialog.FORMAT_SBML
									+ SaveDialog.FORMAT_PNML + SaveDialog.FORMAT_ITXT + SaveDialog.FORMAT_TXT,
							SaveDialog.DATA_TYPE_NETWORK_EXPORT);
					// +SaveDialog.FORMAT_SBML);
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case saveAs:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new SaveDialog(SaveDialog.FORMAT_SBML, SaveDialog.DATA_TYPE_NETWORK_EXPORT);
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case save:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					if (graphInstance.getPathway().getFile() != null) {
						JSBMLoutput jsbmlOutput;
						File file = graphInstance.getPathway().getFile();
						try {
							jsbmlOutput = new JSBMLoutput(new FileOutputStream(file), new GraphInstance().getPathway());
							String out = jsbmlOutput.generateSBMLDocument();
							if (out.length() > 0) {
								MyPopUp.getInstance().show("Error", out);
							} else {
								GraphContainer.getInstance().renamePathway(GraphInstance.getPathwayStatic(),
										file.getName());
								GraphInstance.getPathwayStatic().setName(file.getName());
								GraphInstance.getPathwayStatic().setTitle(file.getName());
								MainWindow.getInstance().renameSelectedTab(file.getName());
								MyPopUp.getInstance().show("JSbml export", "Saving was successful!");
							}
						} catch (FileNotFoundException | XMLStreamException e1) {
							e1.printStackTrace();
						}
					} else {
						new SaveDialog(SaveDialog.FORMAT_SBML, SaveDialog.DATA_TYPE_NETWORK_EXPORT);
					}
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case exit:
			ProgramFileLock.releaseLock();
			System.exit(0);
			break;
		case springLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToSpringLayout();
					LayoutConfig.changeToLayout(SpringLayout.class);
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network before.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network before.");
			}
			break;
		case kkLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToKKLayout();
					LayoutConfig.changeToLayout(KKLayout.class);
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network before.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network before.");
			}
			break;
		case frLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToFRLayout();
					LayoutConfig.changeToLayout(FRLayout.class);
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case circleLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToCircleLayout();
					LayoutConfig.changeToLayout(CircleLayout.class);
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case hebLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					LayoutConfig.changeToLayout(HEBLayout.class);
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network before.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network before.");
			}
			break;
		case hctLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					LayoutConfig.changeToLayout(HCTLayout.class);
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network before.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network before.");
			}
			break;
		case gemLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToCircleLayout();
					LayoutConfig.changeToLayout(GEMLayout.class);
					// graphInstance.getMyGraph().changeGraphLayout(4);

				} else {
					MyPopUp.getInstance().show("Error", "Please create a network before.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network before.");
			}
			break;
		case isomLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToISOMLayout();
					LayoutConfig.changeToLayout(ISOMLayout.class);
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case databaseSettings:
			new Settings(0);
			break;
		case internet:
			new Settings(1);
			break;
		case interaction:
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				new InfoWindow(false);
			}
			break;
		case about:
			new AboutWindow();
			break;
		case graphSettings:
			new Settings(2);
			break;
		case visualizationSettings:
			new Settings(3);
			break;
		case openTestP:
			// System.out.println("testP");
			Pathway pw = graphInstance.getPathway();
			Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
			BiologicalNodeAbstract bna;

			places = 0;
			transitions = 0;
			while (it.hasNext()) {
				bna = it.next();
				if (bna instanceof Place) {
					places++;
				} else if (bna instanceof Transition) {
					transitions++;
				}
			}

			rP = new Object[places][2];

			int i = 0;

			Iterator<BiologicalNodeAbstract> it2 = pw.getAllGraphNodes().iterator();
			while (it2.hasNext()) {
				bna = it2.next();
				if (bna instanceof Place) {
					rP[i][0] = bna.getName();
					rP[i][1] = 0;
					i++;
				}
			}
			String[] cNames = new String[2];
			cNames[0] = "Vertex";
			cNames[1] = "Value";

			tP = new MyTable(rP, cNames);

			JButton testP = new JButton("Test P-invariant");
			testP.setActionCommand(MenuActionCommands.testP.value);
			testP.addActionListener(this);

			if (transitions == 0 || places == 0) {
				testP.setEnabled(false);
			} else {
				testP.setEnabled(true);
			}
			// System.out.println("trans: " + transitions);

			// d = new JDialog();
			// pane = new JPanel();
			invariant.setText("");
			d.setAlwaysOnTop(true);
			d.setContentPane(pane);
			pane.removeAll();
			pane.add(tP);
			pane.add(testP);
			pane.add(invariant);
			d.pack();
			d.setLocationRelativeTo(w.getFrame());
			d.setVisible(true);

			break;
		case openTestT:

			pw = graphInstance.getPathway();
			it = pw.getAllGraphNodes().iterator();

			transitions = 0;
			places = 0;

			while (it.hasNext()) {
				bna = it.next();
				if (bna instanceof Transition) {
					transitions++;
				} else if (bna instanceof Place) {
					places++;
				}
			}

			rT = new Object[transitions][2];

			i = 0;

			it2 = pw.getAllGraphNodes().iterator();
			while (it2.hasNext()) {
				bna = it2.next();
				if (bna instanceof Transition) {
					rT[i][0] = bna.getName();
					rT[i][1] = 0;
					i++;
				}
			}
			cNames = new String[2];
			cNames[0] = "Transition";
			cNames[1] = "Value";

			tT = new MyTable(rT, cNames);

			JButton testT = new JButton("Test T-invariant");
			testT.setActionCommand(MenuActionCommands.testT.value);
			testT.addActionListener(this);

			if (transitions == 0 || places == 0) {
				testT.setEnabled(false);
			} else {
				testT.setEnabled(true);
			}

			invariant.setText("");
			d.setAlwaysOnTop(true);
			d.setContentPane(pane);
			pane.removeAll();
			pane.add(tT);
			pane.add(testT);
			pane.add(invariant);
			d.pack();
			d.setLocationRelativeTo(w.getFrame());
			d.setVisible(true);

			// System.out.println("testT");
			break;
		case testP:

			if (c == null) {
				this.createCMatrix();
			}

			double[] vd = new double[this.places];
			HashMap<String, Double> values = new HashMap<String, Double>();

			// System.out.println(names.size());
			for (i = 0; i < places; i++) {

				values.put(rP[i][0].toString(), Double.parseDouble(rP[i][1].toString()));
				vd[i] = Double.parseDouble(rP[i][1].toString());
			}

			DenseDoubleMatrix1D v = new DenseDoubleMatrix1D(vd);
			DenseDoubleMatrix1D x = new DenseDoubleMatrix1D(c.columns());
			// System.out.println(v.size());
			c.zMult(v, x, 1, 0, true);
			// System.out.println(x);
			IntArrayList l = new IntArrayList();
			x.getNonZeros(l, null);
			// System.out.println(l.size());
			if (l.size() == 0) {
				// System.out.println("ist Invariante");
				this.invariant.setText("This vector is a valid invariant");
			} else {
				// System.out.println("ist keine Invariante");
				this.invariant.setText("This vector is not a valid invariant");
			}
			d.pack();
			d.setLocationRelativeTo(w.getFrame());
			d.setVisible(true);
			break;
		case testT:
			if (c == null) {
				this.createCMatrix();
			}
			vd = new double[this.transitions];
			values = new HashMap<String, Double>();

			// System.out.println(names.size());
			for (i = 0; i < transitions; i++) {

				values.put(rT[i][0].toString(), Double.parseDouble(rT[i][1].toString()));
				vd[i] = Double.parseDouble(rT[i][1].toString());
			}
			v = new DenseDoubleMatrix1D(vd);
			x = new DenseDoubleMatrix1D(c.rows());
			/*
			 * System.out.println(v.size()); System.out.println(x.size());
			 * System.out.println(c.size());
			 */
			c.zMult(v, x, 1, 0, false);
			// System.out.println(x);
			l = new IntArrayList();
			x.getNonZeros(l, null);
			// System.out.println(l.size());
			if (l.size() == 0) {
				// System.out.println("ist Invariante");
				this.invariant.setText("This vector is a valid invariant");
			} else {
				// System.out.println("ist keine Invariante");
				this.invariant.setText("This vector is not a valid invariant");
			}
			d.pack();
			d.setLocationRelativeTo(w.getFrame());
			d.setVisible(true);
			break;
		case openCov:
			pw = graphInstance.getPathway();
			it = pw.getAllGraphNodes().iterator();

			places = 0;
			transitions = 0;
			while (it.hasNext()) {
				bna = it.next();
				if (bna instanceof Place) {
					places++;
				} else if (bna instanceof Transition) {
					transitions++;
				}
			}

			rP = new Object[places + 1][2];
			rI = new Object[places + 1][2];

			i = 0;
			rP[0][0] = "Places";
			rP[0][1] = "Marking";
			rI[0][0] = "Places";
			rI[0][1] = "P-Invariants";

			it2 = pw.getAllGraphNodes().iterator();
			while (it2.hasNext()) {
				bna = it2.next();
				if (bna instanceof Place) {
					rP[i + 1][0] = bna.getName();
					rP[i + 1][1] = 0;
					rI[i + 1][0] = bna.getName();
					rI[i + 1][1] = 0;
					i++;
				}
			}
			cNames = new String[2];
			cNames[0] = "Node";
			cNames[1] = "Value";

			tP = new MyTable(rP, cNames);
			tI = new MyTable(rI, cNames);

			d.setAlwaysOnTop(true);
			d.setContentPane(pane);
			pane.removeAll();
			pane.add(tP);
			pane.add(tI);
			JButton testCov = new JButton("reach");
			testCov.addActionListener(this);
			testCov.setActionCommand(MenuActionCommands.cov.value);
			pane.add(testCov);
			pane.add(reachable);
			d.pack();
			d.setLocationRelativeTo(w.getFrame());
			d.setVisible(true);
			// String name = graphInstance.getPathway().getPetriNet()
			// .getCovGraph();
			// System.out.println("name: "+name);

			// Pathway pw = graphInstance.getContainer().getPathway(name);
			// System.out.println(pw.getAllNodes().size());
			break;
		case cov:
			// Teste ob Invariante:
			if (c == null) {
				this.createCMatrix();
			}

			vd = new double[this.places];
			// HashMap<String, Double> values = new HashMap<String, Double>();

			// System.out.println(names.size());
			for (i = 0; i < places; i++) {

				// values.put(rI[i+1][0].toString(),
				// Double.parseDouble(rI[i+1][1]
				// .toString()));
				vd[i] = Double.parseDouble(rI[i + 1][1].toString());
			}
			// System.out.println(vd);
			v = new DenseDoubleMatrix1D(vd);
			x = new DenseDoubleMatrix1D(c.columns());
			// System.out.println(v.size());
			// System.out.println("groesse: "+v);
			// System.out.println("groesse: "+x);
			// System.out.println("groesse: "+c);
			c.zMult(v, x, 1, 0, true);
			// System.out.println(x);
			l = new IntArrayList();
			x.getNonZeros(l, null);
			// System.out.println(l.size());
			boolean validInvariant = false;
			boolean reachable = false;
			if (l.size() == 0) {
				// System.out.println("ist Invariante");
				// this.reachable.setText("This vector is a valid Invariante");
				validInvariant = true;
			} else {
				// System.out.println("ist keine Invariante");
				this.reachable.setText("no valid p-invariant");
			}
			d.pack();
			d.setLocationRelativeTo(w.getFrame());
			d.setVisible(true);
			if (validInvariant) {
				double[] s = new double[start.size()];
				// System.out.println("Start:");
				for (i = 0; i < start.size(); i++) {
					s[i] = this.start.get(i);
					// System.out.print(this.start.get(i));
				}

				double[] target = new double[this.places];
				for (i = 0; i < places; i++) {

					// values.put(rP[i+1][0].toString(),
					// Double.parseDouble(rP[i+1][1]
					// .toString()));
					target[i] = Double.parseDouble(rP[i + 1][1].toString());
				}

				// start-marking
				DenseDoubleMatrix1D sv = new DenseDoubleMatrix1D(s);
				// target marking
				DenseDoubleMatrix1D tv = new DenseDoubleMatrix1D(target);
				double erg1 = v.zDotProduct(sv, 0, v.size());
				double erg2 = v.zDotProduct(tv, 0, v.size());
				if (erg1 != erg2) {
					// System.out.println("nicht erreichbar, Test durch
					// Invariante");
					this.reachable.setText("not reachable");
				} else {
					if (this.cov == null) {
						cov = new Cov();
					}
					// CovNode root = cov.getRoot();
					String pwName = cov.getNewName();
					HashMap<String, Integer> name2id = cov.getName2id();
					double[] markierung = new double[name2id.size()];
					String name;
					String value;
					int index = 0;
					for (i = 1; i <= places; i++) {
						name = rP[i][0].toString();
						value = rP[i][1].toString();
						index = name2id.get(name);
						markierung[index] = Double.parseDouble(value);
					}
					for (int j = 0; j < markierung.length; j++) {
						// System.out.println(markierung[j]);
					}
					// System.out.println(name2id);
					pw = graphInstance.getContainer().getPathway(pwName);
					Iterator<BiologicalNodeAbstract> iter = pw.getAllGraphNodes().iterator();
					CovNode n = null;
					Object o;

					while (iter.hasNext() && !reachable) {
						o = iter.next();
						if (o instanceof CovNode) {
							n = (CovNode) o;
							if (n.getTokenList().isGreaterEqual(markierung)) {
								if (n.getTokenList().isEqual(markierung)) {
									// System.out.println("wird erreicht durch:
									// "
									// + n.getTokenList());
									this.reachable.setText("it is reached by " + n.getTokenList());
									reachable = true;
									break;
								}
							}
						}
					}

					Iterator<BiologicalNodeAbstract> iter2 = pw.getAllGraphNodes().iterator();
					while (iter2.hasNext() && !reachable) {
						o = iter2.next();
						if (o instanceof CovNode) {
							n = (CovNode) o;
							if (n.getTokenList().isGreater(markierung)) {
								// System.out.println("wird ueberdeckt von "
								// + n.getTokenList());
								this.reachable.setText("it is covered by " + n.getTokenList());
								reachable = true;
								break;
							}
						}
					}

					if (!reachable) {
						// System.out.println("nicht erreichbar");
						this.reachable.setText("not reachable");
					}
				}
			}

			/*
			 * if (nodeList.size() == 0) { this.reachable.setText(
			 * "Diese Markierung ist weder erreichbar noch wird sie ueberdeckt!." ); } else
			 * { this.reachable.setText( "wird ueberdeckt oder erreicht"); int minDist =
			 * nodes.size(); Vector<Vertex> v = null; Vector<Vertex> vTemp = null;
			 * ShortestPath sp; for (int i = 0; i < nodeList.size(); i++) { n =
			 * nodeList.get(i); vTemp = new ShortestPath(root.getVertex().toString(), n
			 * .getVertex().toString(), true) .calculateShortestPath();
			 *
			 * // = sp.calculateShortestPath(); if (vTemp.size() < minDist) { v =
			 * (Vector<Vertex>) vTemp.clone(); minDist = v.size();
			 * System.out.println("dist: " + minDist); } } System.out.println(v); Set edges;
			 * HashSet allEdges = pw.getAllEdges(); HashMap<String, String> edgeLabel = new
			 * HashMap<String, String>(); Iterator allIt = allEdges.iterator(); // Object o;
			 * CovEdge ce; while (allIt.hasNext()) { o = allIt.next(); if (o instanceof
			 * CovEdge) { ce = (CovEdge) o; edgeLabel.put(ce.getEdge().toString(),
			 * ce.getLabel()); } }
			 *
			 * // BiologicalEdgeAbstract edge; // CovEdge ce; Vertex vertex; for (int i = 1;
			 * i < v.size(); i++) { vertex = v.get(i); // System.out.println("v: "+vertex);
			 * // System.out.println("v2: " +v.get(i-1)); edges = vertex.getOutEdges();
			 * Iterator<Edge> it = edges.iterator(); while (it.hasNext()) { Edge edge =
			 * it.next(); // System.out.println(edge.getEndpoints().getSecond().toString());
			 * if (edge.getEndpoints().getSecond().toString().equals( v.get(i -
			 * 1).toString())) { // System.out.println("Edge: " +edge.toString());
			 * System.out.println(edgeLabel.get(edge.toString())); break; }
			 *
			 * } } }
			 */

			d.pack();
			d.setLocationRelativeTo(w.getFrame());
			d.setVisible(true);
			// w.updateTheoryProperties();
			break;
		case createCov:
			// System.out.println("cov erstellen");
			// MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
			// Cov cov = new Cov();
			if (JOptionPane.showConfirmDialog(w.getFrame(),
					"The calculation of the reach graph could take long time, especially if you have many places in your network. Do you want to perform the calculation anyway?",
					"Please Conform your action...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				new ReachController();
			GraphInstance.getMyGraph().changeToGEMLayout();
			break;
		case editElements:
			new PNTableDialog().setVisible(true);
			break;
		case loadModResult:
			new OpenModelicaResult().execute();
			break;
		case simulate:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					graphInstance.getPathway().getPetriNetSimulation().showMenu();
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case dataMappingColor:
			DataMappingColorMVC.createDataMapping();
			break;
		case datamining:
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				new SmacofView();
			} else
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			break;
		case rendererSettings:
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				PreRenderManager.getInstance();
			} else
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			break;
		case createDoc:
			MyPopUp.getInstance().show("Latex generation", "Generation in progress, it will take a short moment!");
			String docDir = VanesaUtility.getWorkingDirectoryPath() + File.separator + "documentation" + File.separator;
			File dir = new File(docDir);

			if (!dir.isDirectory()) {
				dir.mkdir();
			}

			try {
				FileUtils.cleanDirectory(dir);
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			pw = graphInstance.getPathway();
			VisualizationImageServer<BiologicalNodeAbstract, BiologicalEdgeAbstract> wvv = pw.prepareGraphToPrint();

			try {

				Dimension size = wvv.getSize();
				BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_BYTE_INDEXED);
				Graphics2D grp = image.createGraphics();
				wvv.paint(grp);

				File outfile = new File(docDir + "export.png");
				OutputStream fos = new FileOutputStream(outfile);

				PNGEncodeParam param = PNGEncodeParam.getDefaultEncodeParam(image);

				PNGImageEncoder encoder = new PNGImageEncoder(fos, param);
				try {
					encoder.encode(image);
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					fos.close();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Thread thread = new Thread() {
				public void run() {
					try {
						while (!new File(docDir + "export.png").exists()) {
							System.out.println("sleep");
							sleep(100);
						}
						System.out.println("file found");
						// stop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread.start();
			System.out.println("nach thread");
			new PNDoc(docDir + "doc.tex");

			String bin = "pdflatex";

			if (SystemUtils.IS_OS_WINDOWS) {
				bin += ".exe";
			}

			ProcessBuilder pb;
			Process p;

			try {
				pb = new ProcessBuilder(bin, docDir + "doc.tex");
				pb.directory(new File(docDir));
				p = pb.start();

				Thread t = new Thread() {
					public void run() {
						try {
							BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
							while (p.isAlive()) {
								System.out.println(br.readLine());
								sleep(100);
								System.out.println("alive1");
							}
							// p.destroyForcibly();

							Process p2 = pb.start();
							BufferedReader br2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));

							while (p2.isAlive()) {
								System.out.println(br2.readLine());
								sleep(100);
							}
							System.out.println("pdf ended");
							MyPopUp.getInstance().show("Latex compilation successful!", "PDF can be found at:\n" + docDir);
							// stop();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				t.start();
			} catch (IOException e1) {
				MyPopUp.getInstance().show("Compilation of latex failed!",
						"pdflatex executable could not be found!\n Generated Latex file can be found at:\n" + docDir);
				System.err.println("Could not compile latex. Find tex-file at: " + docDir);
			}
			break;
		case dataLabelMapping:
			// Open new window for file input
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				try {
					new LabelToDataMappingWindow();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} catch (InputFormatException ife) {
					MyPopUp.getInstance().show("Inputfile error", ife.getMessage());
					// JOptionPane.showMessageDialog(w, ife.getMessage(), "Inputfile error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case enrichMirna:
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				MirnaStatistics mirna = new MirnaStatistics(graphInstance.getPathway());
				mirna.enrichMirnas(true, true, false);
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case enrichGene:
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				MirnaStatistics mirna = new MirnaStatistics(graphInstance.getPathway());
				mirna.enrichGenes(true, true, false);
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case shake:
			shakeEnzymes();
			break;
		case graphPicture:
			pw = graphInstance.getPathway();
			wvv = pw.prepareGraphToPrint();
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new SaveDialog(SaveDialog.FORMAT_PNG + SaveDialog.FORMAT_SVG, SaveDialog.DATA_TYPE_GRAPH_PICTURE,
							wvv, MainWindow.getInstance().getFrame(), null);
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case wuff:
			// System.out.println("clicked");
			if (con.containsPathway()) {
				pw = graphInstance.getPathway();
				if (pw.hasGotAtLeastOneElement()) {

					it = pw.getAllGraphNodes().iterator();
					DynamicNode dn;
					String maximalSpeed;
					while (it.hasNext()) {
						bna = it.next();
						if (bna instanceof DynamicNode) {
							dn = (DynamicNode) bna;
							maximalSpeed = KineticBuilder.createConvenienceKinetic(bna);
							System.out.println(maximalSpeed);
							dn.setMaximalSpeed(maximalSpeed);
						}
					}
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case transform:
			if (con.containsPathway()) {
				pw = graphInstance.getPathway();
				if (pw.hasGotAtLeastOneElement() && !pw.isPetriNet()) {
					List<Rule> rules = RuleManager.getInstance().getActiveRules();

					if(rules.size() == 0){
						MyPopUp.getInstance().show("Error", "No active transformation rules found!.");
						return;
					}
					// MainWindow w = MainWindow.getInstance();
					// new CreatePathway();
					// graphInstance.getPathway().setPetriNet(true);
					// w.getBar().paintToolbar(option == JOptionPane.NO_OPTION);
					// w.updateAllGuiElements();
					// petriNet = graphInstance.getPathway();
					Transformator t = new Transformator();
					Pathway petriNet = t.transform(pw, rules);
					TransformationInformation tInfo = new TransformationInformation();
					tInfo.setPetriNet(petriNet);
					tInfo.setBnToPnMapping(t.getBnToPN());
					tInfo.setMatches(t.getMatches());
					pw.setTransformationInformation(tInfo);
					// pw.setPetriNet(petriNet);
					// pw.setBnToPnMapping(t.getBnToPN());
					w.updateProjectProperties();
					// CreatePathway.showPathway(petriNet);
				} else {
					MyPopUp.getInstance().show("Error",
							"Please create a biological network first. A Petri net cannot be transformed!.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case ruleManager:
			RuleManagementWindow.getInstance().show();
			break;
		case showPN:
			if (con.containsPathway()) {
				pw = graphInstance.getPathway();
				if (pw.getTransformationInformation() != null && pw.getTransformationInformation().getPetriNet() != null
						&& !pw.isPetriNet()) {
					CreatePathway.showPathway(pw.getTransformationInformation().getPetriNet());
				} else {
					MyPopUp.getInstance().show("Error",
							"Please transform the biological network into a Petri net first!.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case showTransformResult:
			if (con.containsPathway()) {
				pw = graphInstance.getPathway();
				if (pw.getTransformationInformation() != null && pw.getTransformationInformation().getPetriNet() != null
						&& !pw.isPetriNet()) {
					new TransformationInformationWindow(pw).show();
				} else {
					MyPopUp.getInstance().show("Error",
							"Please transform the biological network into a Petri net first!.");
				}
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case allPopUps:
			new AllPopUpsWindow();
			break;
		case nodesEdgesTypes:
			if (con.containsPathway()) {
				pw = graphInstance.getPathway();
				new NodesEdgesTypesWindow(pw);
			} else {
				MyPopUp.getInstance().show("Error", "Please create a network first.");
			}
			break;
		}
	}

	private void shakeEnzymes() {
		if (!GraphContainer.getInstance().containsPathway()) {
			return;
		}
		CompletableFuture.runAsync(() -> {
			GraphInstance graphInstance = new GraphInstance();
            Pathway pw = graphInstance.getPathway();
            MyGraph graph = pw.getGraph();
			for (int i = 0; i < 10; i++) {
				double offset = 5;
				if (i % 2 == 0) {
					offset *= -1;
				}
				VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv =
                        pw.getGraph().getVisualizationViewer();
				double scaleV = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
				double scaleL = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
				double scale;
				if (scaleV < 1) {
					scale = scaleV;
				} else {
					scale = scaleL;
				}
				offset /= scale;
				for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
					if (bna instanceof Enzyme) {
						Point2D p = graph.getVertexLocation(bna);
						Point2D inv = graph.getVisualizationViewer().getRenderContext().getMultiLayerTransformer().inverseTransform(p);
                        graph.getVisualizationViewer().getRenderContext().getMultiLayerTransformer().transform(inv);
						vv.getModel().getGraphLayout().setLocation(bna, new Point2D.Double(p.getX() + offset, p.getY()));
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException ignored) {
				}
			}
		});
	}

	private double[][] initArray(int m, int n) {
		double[][] array = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				array[i][j] = 0;
			}
		}
		return array;
	}

	private void createCMatrix() {
		GraphInstance graphInstance = new GraphInstance();
		Iterator<BiologicalNodeAbstract> hsit = graphInstance.getPathway().getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		Place p;
		HashMap<BiologicalNodeAbstract, Integer> hmplaces = new HashMap<>();
		HashMap<BiologicalNodeAbstract, Integer> hmtransitions = new HashMap<>();
		int numberPlaces = 0;
		int numberTransitions = 0;
		ArrayList<String> names = new ArrayList<>();
		while (hsit.hasNext()) {
			bna = (BiologicalNodeAbstract) hsit.next();
			if (bna instanceof Transition) {
				hmtransitions.put(bna, numberTransitions);
				numberTransitions++;
			} else if (bna instanceof Place) {
				p = (Place) bna;
				hmplaces.put(bna, numberPlaces);
				names.add(p.getName());
				this.start.add(p.getTokenStart());
				numberPlaces++;
			}
		}
		double[][] f = this.initArray(numberPlaces, numberTransitions);
		double[][] b = this.initArray(numberPlaces, numberTransitions);
		// einkommende Kanten (backward matrix)
		Iterator<BiologicalEdgeAbstract> edgeit = graphInstance.getPathway().getAllEdges().iterator();
		PNArc edge;
		Object o;
		while (edgeit.hasNext()) {
			o = edgeit.next();
			if (o instanceof PNArc) {
				edge = (PNArc) o;
				// pair = edge.getEdge().getEndpoints();
				// T->P
				if (hmplaces.containsKey(edge.getTo())) {
					int i = hmplaces.get(edge.getTo());
					int j = hmtransitions.get(edge.getFrom());
					b[i][j] += edge.getPassingTokens();
				}
				// P->T
				else {
					int i = hmplaces.get(edge.getFrom());
					int j = hmtransitions.get(edge.getTo());
					f[i][j] -= edge.getPassingTokens();
				}
			}
		}
		SimpleMatrixDouble bMatrix = new SimpleMatrixDouble(b);
		SimpleMatrixDouble fMatrix = new SimpleMatrixDouble(f);
		SimpleMatrixDouble cMatrix = new SimpleMatrixDouble(this.initArray(numberPlaces, numberTransitions));
		cMatrix.add(bMatrix);
		cMatrix.add(fMatrix);

		c = new DenseDoubleMatrix2D(cMatrix.getData());
	}
}
