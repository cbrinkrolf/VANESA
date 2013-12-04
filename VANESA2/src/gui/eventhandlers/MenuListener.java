package gui.eventhandlers;

import dataMapping.DataMappingMVC;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import graph.ContainerSingelton;
import graph.CreatePathway;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.Transformation;
import graph.algorithms.gui.RandomBipartiteGraphGui;
import graph.algorithms.gui.RandomConnectedGraphGui;
import graph.algorithms.gui.RandomGraphGui;
import graph.algorithms.gui.RandomHamiltonGraphGui;
import graph.algorithms.gui.RandomRegularGraphGui;
import graph.jung.classes.MyGraph;
import graph.layouts.gemLayout.GEMLayout;
//import graph.layouts.modularLayout.MDForceLayout;
import gui.AboutWindow;
import gui.InfoWindow;
import gui.MainWindow;
import gui.MainWindowSingelton;
import io.OpenDialog;
import io.SaveDialog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import miscalleanous.tables.MyTable;
import petriNet.ConvertToPetriNet;
import petriNet.Cov;
import petriNet.CovNode;
import petriNet.OpenModellicaResult;
import petriNet.PNEdge;
import petriNet.PNTableDialog;
import petriNet.PetriNetSimulation;
import petriNet.Place;
import petriNet.ReachController;
import petriNet.SimpleMatrixDouble;
import petriNet.Transition;
import save.graphPicture.WriteGraphPicture;
import xmlOutput.sbml.VAMLoutput;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import configurations.ProgramFileLock;
import configurations.gui.LayoutConfig;
import configurations.gui.Settings;

/*import edu.uci.ics.jung.graph.Edge;
 import edu.uci.ics.jung.graph.Vertex;
 import edu.uci.ics.jung.utils.Pair;
 import edu.uci.ics.jung.visualization.FRLayout;
 import edu.uci.ics.jung.visualization.ISOMLayout;
 import edu.uci.ics.jung.visualization.SpringLayout;
 import edu.uci.ics.jung.visualization.contrib.CircleLayout;
 import edu.uci.ics.jung.visualization.contrib.KKLayout;*/

public class MenuListener implements ActionListener {

	private MyTable tP;
	private MyTable tT;
	private MyTable tI;
	private Object[][] rP;
	private Object[][] rT;
	private Object[][] rI;
	private ArrayList<Double> start = new ArrayList<Double>();

	private JLabel invariant = new JLabel();
	private JPanel pane = new JPanel();
	private JDialog d = new JDialog();

	private JLabel reachable = new JLabel();

	private DenseDoubleMatrix2D c;

	private int places = 0;
	private int transitions = 0;
	private MainWindow w;

	private Cov cov;

	@Override
	public void actionPerformed(ActionEvent e) {

		w = MainWindowSingelton.getInstance();
		String event = e.getActionCommand();
		GraphInstance graphInstance = new GraphInstance();
		GraphContainer con = ContainerSingelton.getInstance();

		if ("new Network".equals(event)) {

			int option = JOptionPane.showOptionDialog(
					MainWindowSingelton.getInstance(),
					"Which type of modeling do you prefer?",
					"Choose Network Type...", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new String[] {
							"Biological Graph", "Petri Net" },
					JOptionPane.CANCEL_OPTION);
			if (option != -1) {
				new CreatePathway();
				graphInstance.getPathway().setPetriNet(
						option == JOptionPane.NO_OPTION);
				w.getBar().paintToolbar(option == JOptionPane.NO_OPTION);
				w.updateAllGuiElements();
			}
		} else if ("open Network".equals(event)) {

			OpenDialog op = new OpenDialog();
			op.execute();

		} else if ("close Network".equals(event)) {

			w.removeTab(true);

		} else if ("close All Networks".equals(event)) {

			w.removeAllTabs();

		} else if ("mathGraph".equals(event)) {

			new RandomGraphGui();

		} else if ("biGraph".equals(event)) {

			new RandomBipartiteGraphGui();

		} else if ("regularGraph".equals(event)) {

			new RandomRegularGraphGui();

		} else if ("phospho".equals(event)) {

			OpenDialog op = new OpenDialog();
			op.execute();

		} else if ("connectedGraph".equals(event)) {

			new RandomConnectedGraphGui();

		} else if ("hamiltonGraph".equals(event)) {

			new RandomHamiltonGraphGui();

		} else if ("export Network".equals(event)) {
			System.out.println("Nodes: "+graphInstance.getMyGraph().getAllVertices().size());
			System.out.println("Edges: "+graphInstance.getMyGraph().getAllEdges().size());
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new SaveDialog( // GRAPHML+MO+GON=14
							SaveDialog.FORMAT_GRAPHML + SaveDialog.FORMAT_MO
									+ SaveDialog.FORMAT_GON
									+ SaveDialog.FORMAT_SBML
									+ SaveDialog.FORMAT_ITXT
									+ SaveDialog.FORMAT_TXT);
					// +SaveDialog.FORMAT_SBML);
				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("save as".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new SaveDialog(16);
				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("save".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					if (graphInstance.getPathway().getFilename() != null) {
						try {
							new VAMLoutput(graphInstance.getPathway()
									.getFilename(), graphInstance.getPathway());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						new SaveDialog(16);
					}
				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("exit".equals(event)) {
			ProgramFileLock.releaseLock();
			System.exit(0);

		} else if ("graphPicture".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new WriteGraphPicture().writeFile();
				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("centerGraph".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					graphInstance.getPathway().getGraph().animatedCentering();
				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("printPicture".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new WriteGraphPicture().printGraph();
				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("springLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeGraphLayout(1);
					// graphInstance.getMyGraph().changeToSpringLayout();
					LayoutConfig.changeToLayout(SpringLayout.class);
				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("kkLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeGraphLayout(2);
					// graphInstance.getMyGraph().changeToKKLayout();
					LayoutConfig.changeToLayout(KKLayout.class);
				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("frLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeGraphLayout(3);
					// graphInstance.getMyGraph().changeToFRLayout();
					LayoutConfig.changeToLayout(FRLayout.class);
				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("circleLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToCircleLayout();
					LayoutConfig.changeToLayout(CircleLayout.class);
					// graphInstance.getMyGraph().changeGraphLayout(4);

				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("gemLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToCircleLayout();
					LayoutConfig.changeToLayout(GEMLayout.class);
					// graphInstance.getMyGraph().changeGraphLayout(4);

				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("isomLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeGraphLayout(5);
					// graphInstance.getMyGraph().changeToISOMLayout();
					LayoutConfig.changeToLayout(ISOMLayout.class);
				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		} else if ("MDLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeGraphLayout(5);
					//LayoutConfig.changeToLayout(MDForceLayout.class);
				} else {
					JOptionPane.showMessageDialog(w,
							"Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w,
						"Please create a network before.");
			}
		}
		// else if ("kegg settings".equals(event))
		// {
		//
		// new Settings().setSelection(0);
		//
		// }
		// else if ("brenda settings".equals(event))
		// {
		//
		// new Settings().setSelection(1);
		//
		// }
		// else if ("dawis settings".equals(event))
		// {
		//
		// new Settings().setSelection(2);
		//
		// }
		// else if ("ppi settings".equals(event))
		// {
		//
		// new Settings().setSelection(3);
		//
		// }
		else if ("database settings".equals(event)) {
			new Settings(0);
		} else if ("internet".equals(event)) {

			new Settings(1);

		} else if ("graphAlignemnt".equals(event)) {

			new Settings(2);

		} else if ("interaction".equals(event)) {

			new InfoWindow();

		} else if ("about".equals(event)) {

			new AboutWindow();

		} else if ("graphSettings".equals(event)) {

			new Settings(3);

		} /*
		 * else if ("animation".equals(event)) { if (con.containsPathway()) { }
		 * //new Regulation(); }
		 */
		else if ("openTestP".equals(event)) {
			// System.out.println("testP");
			graphInstance = new GraphInstance();
			Pathway pw = graphInstance.getPathway();
			Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();
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

			Iterator<BiologicalNodeAbstract> it2 = pw.getAllNodes().iterator();
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
			testP.setActionCommand("testP");
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
			d.setVisible(true);

		} else if ("openTestT".equals(event)) {

			graphInstance = new GraphInstance();
			Pathway pw = graphInstance.getPathway();
			Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();
			BiologicalNodeAbstract bna;

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

			int i = 0;

			Iterator<BiologicalNodeAbstract> it2 = pw.getAllNodes().iterator();
			while (it2.hasNext()) {
				bna = it2.next();
				if (bna instanceof Transition) {
					rT[i][0] = bna.getName();
					rT[i][1] = 0;
					i++;
				}
			}
			String[] cNames = new String[2];
			cNames[0] = "Transition";
			cNames[1] = "Value";

			tT = new MyTable(rT, cNames);

			JButton testT = new JButton("Test T-invariant");
			testT.setActionCommand("testT");
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
			d.setVisible(true);

			// System.out.println("testT");
		} else if ("testP".equals(event)) {

			if (c == null) {
				this.createCMatrix();
			}

			double[] vd = new double[this.places];
			HashMap<String, Double> values = new HashMap<String, Double>();

			// System.out.println(names.size());
			for (int i = 0; i < places; i++) {

				values.put(rP[i][0].toString(),
						Double.parseDouble(rP[i][1].toString()));
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
				d.pack();
				d.setVisible(true);
			} else {
				// System.out.println("ist keine Invariante");
				this.invariant.setText("This vector is not a valid invariant");
				d.pack();
				d.setVisible(true);
			}
		} else if ("testT".equals(event)) {
			if (c == null) {
				this.createCMatrix();
			}
			double[] vd = new double[this.transitions];
			HashMap<String, Double> values = new HashMap<String, Double>();

			// System.out.println(names.size());
			for (int i = 0; i < transitions; i++) {

				values.put(rT[i][0].toString(),
						Double.parseDouble(rT[i][1].toString()));
				vd[i] = Double.parseDouble(rT[i][1].toString());
			}
			DenseDoubleMatrix1D v = new DenseDoubleMatrix1D(vd);
			DenseDoubleMatrix1D x = new DenseDoubleMatrix1D(c.rows());
			/*
			 * System.out.println(v.size()); System.out.println(x.size());
			 * System.out.println(c.size());
			 */
			c.zMult(v, x, 1, 0, false);
			// System.out.println(x);
			IntArrayList l = new IntArrayList();
			x.getNonZeros(l, null);
			// System.out.println(l.size());
			if (l.size() == 0) {
				// System.out.println("ist Invariante");
				this.invariant.setText("This vector is a valid invariant");
				d.pack();
				d.setVisible(true);
			} else {
				// System.out.println("ist keine Invariante");
				this.invariant.setText("This vector is not a valid invariant");
				d.pack();
				d.setVisible(true);
			}

		} else if ("openCov".equals(event)) {
			graphInstance = new GraphInstance();
			Pathway pw = graphInstance.getPathway();
			Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();
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

			rP = new Object[places + 1][2];
			rI = new Object[places + 1][2];

			int i = 0;
			rP[0][0] = "Places";
			rP[0][1] = "Marking";
			rI[0][0] = "Places";
			rI[0][1] = "P-Invariants";

			Iterator<BiologicalNodeAbstract> it2 = pw.getAllNodes().iterator();
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
			String[] cNames = new String[2];
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
			testCov.setActionCommand("cov");
			pane.add(testCov);
			pane.add(reachable);
			d.pack();
			d.setVisible(true);
			// String name = graphInstance.getPathway().getPetriNet()
			// .getCovGraph();
			// System.out.println("name: "+name);

			// Pathway pw = graphInstance.getContainer().getPathway(name);
			// System.out.println(pw.getAllNodes().size());
		} else if ("cov".equals(event)) {

			// Teste ob Invariante:
			if (c == null) {
				this.createCMatrix();
			}

			double[] vd = new double[this.places];
			// HashMap<String, Double> values = new HashMap<String, Double>();

			// System.out.println(names.size());
			for (int i = 0; i < places; i++) {

				// values.put(rI[i+1][0].toString(),
				// Double.parseDouble(rI[i+1][1]
				// .toString()));
				vd[i] = Double.parseDouble(rI[i + 1][1].toString());
			}
			// System.out.println(vd);
			DenseDoubleMatrix1D v = new DenseDoubleMatrix1D(vd);
			DenseDoubleMatrix1D x = new DenseDoubleMatrix1D(c.columns());
			// System.out.println(v.size());
			// System.out.println("groesse: "+v);
			// System.out.println("groesse: "+x);
			// System.out.println("groesse: "+c);
			c.zMult(v, x, 1, 0, true);
			// System.out.println(x);
			IntArrayList l = new IntArrayList();
			x.getNonZeros(l, null);
			// System.out.println(l.size());
			boolean validInvariant = false;
			boolean reachable = false;
			if (l.size() == 0) {
				// System.out.println("ist Invariante");
				// this.reachable.setText("This vector is a valid Invariante");
				validInvariant = true;
				d.pack();
				d.setVisible(true);
			} else {
				// System.out.println("ist keine Invariante");
				this.reachable.setText("no valid p-invariant");
				d.pack();
				d.setVisible(true);
			}

			if (validInvariant) {
				double[] s = new double[start.size()];
				// System.out.println("Start:");
				for (int i = 0; i < start.size(); i++) {
					s[i] = this.start.get(i);
					// System.out.print(this.start.get(i));
				}

				double[] target = new double[this.places];
				for (int i = 0; i < places; i++) {

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
					// System.out.println("nicht erreichbar, Test durch Invariante");
					this.reachable.setText("not reachable");
				} else {
					if (this.cov == null) {
						cov = new Cov();
					}
					//CovNode root = cov.getRoot();
					String pwName = cov.getNewName();
					HashMap<String, Integer> name2id = cov.getName2id();
					double[] markierung = new double[name2id.size()];
					String name;
					String value;
					int index = 0;
					for (int i = 1; i <= places; i++) {
						name = rP[i][0].toString();
						value = rP[i][1].toString();
						index = name2id.get(name);
						markierung[index] = Double.parseDouble(value);
					}
					for (int j = 0; j < markierung.length; j++) {
						// System.out.println(markierung[j]);
					}
					// System.out.println(name2id);
					Pathway pw = graphInstance.getContainer()
							.getPathway(pwName);
					Iterator<BiologicalNodeAbstract> iter = pw.getAllNodes()
							.iterator();
					CovNode n = null;
					Object o;

					while (iter.hasNext() && !reachable) {
						o = iter.next();
						if (o instanceof CovNode) {
							n = (CovNode) o;
							if (n.getTokenList().isGreaterEqual(markierung)) {
								if (n.getTokenList().isEqual(markierung)) {
									// System.out.println("wird erreicht durch: "
									// + n.getTokenList());
									this.reachable.setText("it is reached by "
											+ n.getTokenList());
									reachable = true;
									break;
								}
							}
						}
					}

					Iterator<BiologicalNodeAbstract> iter2 = pw.getAllNodes()
							.iterator();
					while (iter2.hasNext() && !reachable) {
						o = iter2.next();
						if (o instanceof CovNode) {
							n = (CovNode) o;
							if (n.getTokenList().isGreater(markierung)) {
								// System.out.println("wird ueberdeckt von "
								// + n.getTokenList());
								this.reachable.setText("it is covered by "
										+ n.getTokenList());
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
			 * "Diese Markierung ist weder erreichbar noch wird sie ueberdeckt!."
			 * ); } else {
			 * this.reachable.setText("wird ueberdeckt oder erreicht"); int
			 * minDist = nodes.size(); Vector<Vertex> v = null; Vector<Vertex>
			 * vTemp = null; ShortestPath sp; for (int i = 0; i <
			 * nodeList.size(); i++) { n = nodeList.get(i); vTemp = new
			 * ShortestPath(root.getVertex().toString(), n
			 * .getVertex().toString(), true) .calculateShortestPath();
			 * 
			 * // = sp.calculateShortestPath(); if (vTemp.size() < minDist) { v
			 * = (Vector<Vertex>) vTemp.clone(); minDist = v.size();
			 * System.out.println("dist: " + minDist); } }
			 * System.out.println(v); Set edges; HashSet allEdges =
			 * pw.getAllEdges(); HashMap<String, String> edgeLabel = new
			 * HashMap<String, String>(); Iterator allIt = allEdges.iterator();
			 * // Object o; CovEdge ce; while (allIt.hasNext()) { o =
			 * allIt.next(); if (o instanceof CovEdge) { ce = (CovEdge) o;
			 * edgeLabel.put(ce.getEdge().toString(), ce.getLabel()); } }
			 * 
			 * // BiologicalEdgeAbstract edge; // CovEdge ce; Vertex vertex; for
			 * (int i = 1; i < v.size(); i++) { vertex = v.get(i); //
			 * System.out.println("v: "+vertex); //
			 * System.out.println("v2: "+v.get(i-1)); edges =
			 * vertex.getOutEdges(); Iterator<Edge> it = edges.iterator(); while
			 * (it.hasNext()) { Edge edge = it.next(); //
			 * System.out.println(edge.getEndpoints().getSecond().toString());
			 * if (edge.getEndpoints().getSecond().toString().equals( v.get(i -
			 * 1).toString())) { //
			 * System.out.println("Edge: "+edge.toString());
			 * System.out.println(edgeLabel.get(edge.toString())); break; }
			 * 
			 * } } }
			 */

			d.pack();
			d.setVisible(true);
			// w.updateTheoryProperties();

			// graphInstance.g
			// pw.getTab().getTitelTab().setVisible(false);
			// graphInstance.getContainer().getPathway(cov.getOldName()).getTab().getTitelTab().repaint();

			// System.out.println("klick");
		} else if ("createCov".equals(event)) {
			// System.out.println("cov erstellen");
			// MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
			// Cov cov = new Cov();
			if (JOptionPane
					.showConfirmDialog(
							MainWindowSingelton.getInstance(),
							"The calculation of the reach graph could take long time, especially if you have many places in your network. Do you want to perform the calculation anyway?",
							"Please Conform your action...",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				new ReachController();
			graphInstance.getMyGraph().changeToGEMLayout();
		} else if ("editElements".equals(event))
			new PNTableDialog().setVisible(true);
		else if ("loadModResult".equals(event))
			new OpenModellicaResult().execute();
		else if ("simulate".equals(event))
			new PetriNetSimulation();
		else if ("convertIntoPetriNet".equals(event)
				&& (con.getPathwayNumbers() > 0)) {
			MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
			g.disableGraphTheory();
			// new CompareGraphsGUI();
			new ConvertToPetriNet();

			Component[] c = MainWindowSingelton.getInstance().getContentPane()
					.getComponents();
			for (int i = 0; i < c.length; i++) {
				if (c[i].getClass().getName().equals("javax.swing.JPanel")) {
					MainWindowSingelton
							.getInstance()
							.getBar()
							.paintToolbar(
									con.getPathway(w.getCurrentPathway())
											.isPetriNet());
					break;
				}
			}
		} else if ("dataMapping".equals(event)){
			DataMappingMVC.createDataMapping();
		} else if("resolveReferences".equals(event)){
			
			//System.out.println("resolve");
			Pathway old = con.getPathway(w.getCurrentPathway());
			//
			//Pathway new =
			
			
			//ByteArrayOutputStream bos = new ByteArrayOutputStream();
			//Serialize it
			
			Pathway pw = new CreatePathway(old.getName()).getPathway();
			//pw = old.clone()
			Iterator<BiologicalNodeAbstract> it = old.getAllNodes().iterator();
			BiologicalNodeAbstract bna;
			while(it.hasNext()){
				bna =  it.next();
				pw.addVertex((BiologicalNodeAbstract)bna.clone(), old.getGraph().getVertexLocation(bna));
			}
			
			Iterator<BiologicalEdgeAbstract> it2 = old.getAllEdges().iterator();
			BiologicalEdgeAbstract bea;
			while(it2.hasNext()){
				bea = it2.next();
				pw.addEdge(bea);
			}
			Transformation t = new Transformation();
			//t.resolveReferences(old.clone());
			//MainWindow.
			//Tansformation.resolveReferences(pw);
			//pw = old;
			
		}
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
		Iterator<BiologicalNodeAbstract> hsit = graphInstance.getPathway()
				.getAllNodes().iterator();
		BiologicalNodeAbstract bna;
		Place p;
		HashMap<BiologicalNodeAbstract, Integer> hmplaces = new HashMap<BiologicalNodeAbstract, Integer>();
		HashMap<BiologicalNodeAbstract, Integer> hmtransitions = new HashMap<BiologicalNodeAbstract, Integer>();
		int numberPlaces = 0;
		int numberTransitions = 0;
		ArrayList<String> names = new ArrayList<String>();
		while (hsit.hasNext()) {
			bna = (BiologicalNodeAbstract) hsit.next();
			if (bna instanceof Transition) {
				hmtransitions.put(bna, new Integer(numberTransitions));
				numberTransitions++;
			} else if (bna instanceof Place) {
				p = (Place) bna;
				hmplaces.put(bna, new Integer(numberPlaces));
				names.add(p.getName());
				// System.out.println("name: " + p.getName());
				this.start.add(new Double(p.getTokenStart()));
				numberPlaces++;
			}
		}
		double[][] f = this.initArray(numberPlaces, numberTransitions);
		double[][] b = this.initArray(numberPlaces, numberTransitions);
		// einkommende Kanten (backward matrix)
		Iterator<BiologicalEdgeAbstract> edgeit = graphInstance.getPathway()
				.getAllEdges().iterator();
		PNEdge edge;
		Object o;
		while (edgeit.hasNext()) {
			o = edgeit.next();
			if (o instanceof PNEdge) {
				edge = (PNEdge) o;
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
		SimpleMatrixDouble cMatrix = new SimpleMatrixDouble(this.initArray(
				numberPlaces, numberTransitions));
		cMatrix.add(bMatrix);
		cMatrix.add(fMatrix);

		c = new DenseDoubleMatrix2D(cMatrix.getData());
	}
}
