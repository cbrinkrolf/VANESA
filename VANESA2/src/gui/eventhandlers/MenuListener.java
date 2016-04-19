package gui.eventhandlers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.freehep.util.export.ExportDialog;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.Expression;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.SRNA;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cluster.clientimpl.ClusterDataUploadWindow;
import configurations.ProgramFileLock;
import configurations.Wrapper;
import configurations.gui.LayoutConfig;
import configurations.gui.Settings;
import dataMapping.DataMappingColorMVC;
import database.mirna.miRNAqueries;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalFileChooser;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
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
import graph.algorithms.gui.smacof.view.SmacofView;
import graph.jung.classes.MyGraph;
import graph.jung.classes.MyVisualizationViewer;
import graph.layouts.Circle;
import graph.layouts.GraphCenter;
import graph.layouts.gemLayout.GEMLayout;
import graph.layouts.hctLayout.HCTLayout;
import graph.layouts.hebLayout.HEBLayout;
//import graph.layouts.modularLayout.MDForceLayout;
import gui.AboutWindow;
import gui.InfoWindow;
import gui.LabelToDataMappingWindow;
import gui.LabelToDataMappingWindow.InputFormatException;
import gui.MainWindow;
import gui.MainWindowSingleton;
import gui.visualization.PreRenderManager;
import io.EdalSaveDialog;
import io.OpenDialog;
import io.PNDoc;
import io.SaveDialog;
import miscalleanous.tables.MyTable;
import petriNet.ConvertMetabolicNet;
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
import pojos.DBColumn;
import xmlInput.sbml.JSBMLinput;

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

	private PetriNetSimulation simulation = null;

	@Override
	public void actionPerformed(ActionEvent e) {
		w = MainWindowSingleton.getInstance();
		String event = e.getActionCommand();
		GraphInstance graphInstance = new GraphInstance();
		GraphContainer con = ContainerSingelton.getInstance();

		if ("new Network".equals(event)) {

			int option = JOptionPane.showOptionDialog(MainWindowSingleton.getInstance(), "Which type of modeling do you prefer?",
					"Choose Network Type...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					new String[] { "Biological Graph", "Petri Net" }, JOptionPane.CANCEL_OPTION);
			if (option != -1) {
				new CreatePathway();
				graphInstance.getPathway().setPetriNet(option == JOptionPane.NO_OPTION);
				w.getBar().paintToolbar(option == JOptionPane.NO_OPTION);
				w.updateAllGuiElements();
			}
		} else if ("open Network".equals(event)) {

			OpenDialog op = new OpenDialog();
			op.execute();

		} else if ("openEdal".equals(event)) {
			int SERVER_PORT = 2000;
			String SERVER_ADDRESS = "bit-249.ipk-gatersleben.de";

			Subject subject = null;
			try {
				subject = EdalHelpers.authenticateSampleUser();
			} catch (EdalAuthenticateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			/** alternatively use Google+ login **/
			// Subject subject = EdalHelpers.authenticateGoogleUser("", 3128);

			/** connect to running EDAL server on "bit-249" **/
			ClientDataManager dataManagerClient = null;
			try {
				dataManagerClient = new ClientDataManager(SERVER_ADDRESS, SERVER_PORT, new Authentication(subject));
			} catch (EdalAuthenticateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// JFrame jf = new JFrame();

			EdalFileChooser dialog = new EdalFileChooser(MainWindowSingleton.getInstance(), dataManagerClient);
			dialog.setLocationRelativeTo(MainWindowSingleton.getInstance());
			dialog.setFileSelectionMode(EdalFileChooser.FILES_AND_DIRECTORIES);
			dialog.showConnectionButton(false);

			// dialog.setFileFilter(new EdalFileNameExtensionFilter("sbml",
			// "sbml"));

			int result = dialog.showOpenDialog();
			// System.out.println(result + " " +
			// EdalFileChooser.APPROVE_OPTION);
			if (result == EdalFileChooser.APPROVE_OPTION) {
				ClientPrimaryDataFile df = null;
				ClientPrimaryDataEntity de = dialog.getSelectedFile();
				if (de instanceof ClientPrimaryDataFile) {
					df = (ClientPrimaryDataFile) de;
					// File f = new File(df.getName());
					// File f;
					try {
						ByteArrayOutputStream os = new ByteArrayOutputStream();

						df.read(os);
						byte[] b = os.toByteArray();
						InputStream is = new ByteArrayInputStream(b);
						JSBMLinput jsbmlInput = new JSBMLinput();
						// jsbmlInput = pathway==null ? new JSBMLinput() : new
						// JSBMLinput(pathway);
						String res = jsbmlInput.loadSBMLFile(is, df.getName());
						if (res.length() > 0) {
							JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(), res);
						}
						// os.
					} catch (RemoteException | PrimaryDataFileException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} else {
					System.out.println("please choose a file, not a dir");
				}
			}

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
			// System.out.println("Nodes:
			// "+graphInstance.getMyGraph().getAllVertices().size());
			// System.out.println("Edges:
			// "+graphInstance.getMyGraph().getAllEdges().size());
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new SaveDialog( // GRAPHML+MO+GON=14
							SaveDialog.FORMAT_GRAPHML + SaveDialog.FORMAT_MO + SaveDialog.FORMAT_GON
							// + SaveDialog.FORMAT_SBML
									+ SaveDialog.FORMAT_PNML + SaveDialog.FORMAT_ITXT + SaveDialog.FORMAT_TXT);
					// +SaveDialog.FORMAT_SBML);
				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("save as".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new SaveDialog(SaveDialog.FORMAT_SBML);
				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("saveEdal".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// System.out.println("click");
					new EdalSaveDialog();
					// new SaveDialog(SaveDialog.FORMAT_SBML);
				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("save".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					if (graphInstance.getPathway().getFilename() != null) {
						// new JSBMLoutput(graphInstance.getPathway()
						// .getFilename(), graphInstance.getPathway());
					} else {
						new SaveDialog(SaveDialog.FORMAT_SBML);
					}
				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("exit".equals(event)) {
			ProgramFileLock.releaseLock();
			System.exit(0);
			// TODO delete
		} else if ("springLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeGraphLayout(1);
					// graphInstance.getMyGraph().changeToSpringLayout();
					LayoutConfig.changeToLayout(SpringLayout.class);
				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("kkLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeGraphLayout(2);
					// graphInstance.getMyGraph().changeToKKLayout();
					LayoutConfig.changeToLayout(KKLayout.class);
				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("frLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeGraphLayout(3);
					// graphInstance.getMyGraph().changeToFRLayout();
					LayoutConfig.changeToLayout(FRLayout.class);
				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("circleLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToCircleLayout();
					LayoutConfig.changeToLayout(CircleLayout.class);
					// graphInstance.getMyGraph().changeGraphLayout(4);

				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("hebLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					LayoutConfig.changeToLayout(HEBLayout.class);
				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("hctLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					LayoutConfig.changeToLayout(HCTLayout.class);
				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("gemLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToCircleLayout();
					LayoutConfig.changeToLayout(GEMLayout.class);
					// graphInstance.getMyGraph().changeGraphLayout(4);

				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("isomLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeGraphLayout(5);
					// graphInstance.getMyGraph().changeToISOMLayout();
					LayoutConfig.changeToLayout(ISOMLayout.class);
				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}
		} else if ("MDLayout".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeGraphLayout(5);
					// LayoutConfig.changeToLayout(MDForceLayout.class);
				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
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
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				new InfoWindow(false);
			}

		} else if ("about".equals(event)) {

			new AboutWindow();

		} else if ("graphSettings".equals(event)) {

			new Settings(3);

		} else if ("visualizationSettings".equals(event)) {
			new Settings(4);
		} /*
			 * else if ("animation".equals(event)) { if (con.containsPathway())
			 * { } //new Regulation(); }
			 */
		else if ("openTestP".equals(event)) {
			// System.out.println("testP");
			graphInstance = new GraphInstance();
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
			d.setLocationRelativeTo(MainWindowSingleton.getInstance());
			d.setVisible(true);

		} else if ("openTestT".equals(event)) {

			graphInstance = new GraphInstance();
			Pathway pw = graphInstance.getPathway();
			Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
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

			Iterator<BiologicalNodeAbstract> it2 = pw.getAllGraphNodes().iterator();
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
			d.setLocationRelativeTo(MainWindowSingleton.getInstance());
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
				d.pack();
				d.setLocationRelativeTo(MainWindowSingleton.getInstance());
				d.setVisible(true);
			} else {
				// System.out.println("ist keine Invariante");
				this.invariant.setText("This vector is not a valid invariant");
				d.pack();
				d.setLocationRelativeTo(MainWindowSingleton.getInstance());
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

				values.put(rT[i][0].toString(), Double.parseDouble(rT[i][1].toString()));
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
				d.setLocationRelativeTo(MainWindowSingleton.getInstance());
				d.setVisible(true);
			} else {
				// System.out.println("ist keine Invariante");
				this.invariant.setText("This vector is not a valid invariant");
				d.pack();
				d.setLocationRelativeTo(MainWindowSingleton.getInstance());
				d.setVisible(true);
			}

		} else if ("openCov".equals(event)) {
			graphInstance = new GraphInstance();
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

			rP = new Object[places + 1][2];
			rI = new Object[places + 1][2];

			int i = 0;
			rP[0][0] = "Places";
			rP[0][1] = "Marking";
			rI[0][0] = "Places";
			rI[0][1] = "P-Invariants";

			Iterator<BiologicalNodeAbstract> it2 = pw.getAllGraphNodes().iterator();
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
			d.setLocationRelativeTo(MainWindowSingleton.getInstance());
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
				d.setLocationRelativeTo(MainWindowSingleton.getInstance());
				d.setVisible(true);
			} else {
				// System.out.println("ist keine Invariante");
				this.reachable.setText("no valid p-invariant");
				d.pack();
				d.setLocationRelativeTo(MainWindowSingleton.getInstance());
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
					Pathway pw = graphInstance.getContainer().getPathway(pwName);
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
			 * "Diese Markierung ist weder erreichbar noch wird sie ueberdeckt!."
			 * ); } else { this.reachable.setText(
			 * "wird ueberdeckt oder erreicht"); int minDist = nodes.size();
			 * Vector<Vertex> v = null; Vector<Vertex> vTemp = null;
			 * ShortestPath sp; for (int i = 0; i < nodeList.size(); i++) { n =
			 * nodeList.get(i); vTemp = new
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
			 * System.out.println("v: "+vertex); // System.out.println("v2: "
			 * +v.get(i-1)); edges = vertex.getOutEdges(); Iterator<Edge> it =
			 * edges.iterator(); while (it.hasNext()) { Edge edge = it.next();
			 * //
			 * System.out.println(edge.getEndpoints().getSecond().toString());
			 * if (edge.getEndpoints().getSecond().toString().equals( v.get(i -
			 * 1).toString())) { // System.out.println("Edge: "
			 * +edge.toString());
			 * System.out.println(edgeLabel.get(edge.toString())); break; }
			 * 
			 * } } }
			 */

			d.pack();
			d.setLocationRelativeTo(MainWindowSingleton.getInstance());
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
			if (JOptionPane.showConfirmDialog(MainWindowSingleton.getInstance(),
					"The calculation of the reach graph could take long time, especially if you have many places in your network. Do you want to perform the calculation anyway?",
					"Please Conform your action...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				new ReachController();
			graphInstance.getMyGraph().changeToGEMLayout();
		} else if ("editElements".equals(event))
			new PNTableDialog().setVisible(true);
		else if ("loadModResult".equals(event))
			new OpenModellicaResult().execute();
		else if ("simulate".equals(event)) {
			if (simulation == null) {
				simulation = new PetriNetSimulation();
			}
			simulation.showMenue();
		} else if ("convertIntoPetriNet".equals(event) && (con.getPathwayNumbers() > 0)) {
			MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
			g.disableGraphTheory();
			// new CompareGraphsGUI();
			new ConvertMetabolicNet();

			/*
			 * Component[] c =
			 * MainWindowSingleton.getInstance().getContentPane()
			 * .getComponents(); for (int i = 0; i < c.length; i++) { if
			 * (c[i].getClass().getName().equals("javax.swing.JPanel")) {
			 * MainWindowSingleton .getInstance() .getBar() .paintToolbar(
			 * con.getPathway(w.getCurrentPathway()) .isPetriNet()); break; } }
			 */
		} else if ("dataMappingColor".equals(event)) {
			DataMappingColorMVC.createDataMapping();
		} else if ("dataMappingDB".equals(event)) {
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				new ClusterDataUploadWindow();
			} else
				JOptionPane.showMessageDialog(null, "please load a network first.");

		} else if ("datamining".equals(event)) {
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				new SmacofView();
			} else
				JOptionPane.showMessageDialog(null, "please load a network first.");

		} else if ("rendererSettings".equals(event)) {
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				PreRenderManager.getInstance();
			} else
				JOptionPane.showMessageDialog(null, "please load a network first.");

		} else if ("resolveReferences".equals(event)) {

			if (con.containsPathway()) {
				// System.out.println("resolve");
				Pathway old = con.getPathway(w.getCurrentPathway());
				//
				// Pathway new =

				// ByteArrayOutputStream bos = new ByteArrayOutputStream();
				// Serialize it

				Pathway pw = new CreatePathway(old).getPathway();
				// pw = old.clone()

				Transformation t = new Transformation();
				t.resolveReferences(pw);
				// MainWindow.
				// Tansformation.resolveReferences(pw);
				// pw = old;
			}

		} else if ("createDoc".equals(event)) {
			new PNDoc();
		} else if ("dataLabelMapping".equals(event)) {

			// Open new window for file input
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				try {
					new LabelToDataMappingWindow();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} catch (InputFormatException ife) {
					JOptionPane.showMessageDialog(w, ife.getMessage(), "Inputfile error", JOptionPane.ERROR_MESSAGE);
				}
			} else
				JOptionPane.showMessageDialog(null, "please load a network first.");

		} else if ("mirnaTest".equals(event)) {
			System.out.println("mirnatest");
			// code for testing number of mirnas matching a pathway

			/*
			 * Iterator<BiologicalNodeAbstract> bnas =
			 * graphInstance.getMyGraph().getAllVertices().iterator(); int c =0;
			 * while(bnas.hasNext()){ if(bnas.next() instanceof DNA){ c++; } }
			 * System.out.println("dnas: "+c);
			 */
			final String QUESTION_MARK = new String("\\?");
			String finalQueryString = "SELECT distinct targetgene FROM db_mirna2.mirtarbase;";// SELECT
																								// COUNT(DISTINCT
			Set<String> pws = new HashSet<String>(); // targetgene
			// FROM
			ArrayList<DBColumn> list = new ArrayList<DBColumn>();

			// list = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA,
			// finalQueryString);
			// System.out.println(list.size());
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			// System.out.println("res: "+list.get(0).getColumn()[0]);
			// System.out.println(list.size());
			HashMap<String, HashSet<String>> pw2genes = new HashMap<String, HashSet<String>>();
			HashMap<String, HashSet<String>> genes2mrina = new HashMap<String, HashSet<String>>();
			BufferedReader in;
			String pw;
			String mirna;
			System.out.println(list.size());

			FileReader fr;
			try {
				fr = new FileReader("pws.txt");

				BufferedReader br = new BufferedReader(fr);

				String zeile = br.readLine();

				while ((zeile = br.readLine()) != null) {
					pws.add(zeile);
					// pws.add("00062");
				}

				br.close();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			System.out.println("PWs: " + pws.size());
			Iterator<String> its = pws.iterator();

			double count = 0;
			String output = "";
			while (its.hasNext()) {

				// if(count%10 == 0){
				System.out.println(count / (pws.size()) * 100.0 + "%");
				// }
				pw = its.next();

				finalQueryString = "SELECT distinct kegg_genes_name.name FROM dawismd.kegg_genes_pathway join dawismd.kegg_genes_name on kegg_genes_pathway.id = kegg_genes_name.id where kegg_genes_pathway.number = '"
						+ pw + "' AND kegg_genes_pathway.org = 'hsa';";

				// finalQueryString = "SELECT count(distinct
				// kegg_genes_pathway.id) FROM dawismd.kegg_genes_pathway inner
				// join dawismd.kegg_genes_name on kegg_genes_pathway.id =
				// kegg_genes_name.id where kegg_genes_pathway.number='"
				// + pw + "' and kegg_genes_pathway.org = 'hsa';";

				list = new Wrapper().requestDbContent(Wrapper.dbtype_KEGG, finalQueryString);
				System.out.println(list.size());
				if (list.size() > 0) {
					// System.out.println("drin");
					pw2genes.put(pw, new HashSet<String>());
				}

				for (int i = 0; i < list.size(); i++) {
					pw2genes.get(pw).add(list.get(i).getColumn()[0]);
				}

				output += pw + "\t" + list.get(0).getColumn()[0] + "\r\n";
				count++;
			}
			System.out.println(output);
			HashSet<String> genes = new HashSet<String>();

			System.out.println("pws: " + pw2genes.keySet().size());
			int sum = 0;

			for (String key : pw2genes.keySet()) {
				genes.addAll(pw2genes.get(key));
			}

			System.out.println("all genes: " + genes.size());
			String number;
			String gene;
			Iterator<String> it = genes.iterator();
			count = 0;
			while (it.hasNext()) {
				System.out.println("Retr. mirnas: " + count / (genes.size()) * 100.0 + "%");
				gene = it.next();
				if (!gene.contains("'")) {
					// finalQueryString = "SELECT distinct mirnaname FROM
					// mirtarbase where targetgene = '" + gene + "';";

					finalQueryString = "SELECT distinct Matures.Name FROM Matures Matures inner join TargetGenes TargetGenes on Matures.ID=TargetGenes.mID inner join TargetGene TargetGene on TargetGenes.ID=TargetGene.tgsID where NOT DB='ensemble' AND TargetGene.Accession = '"
							+ gene + "' AND TargetGenes.SpeciesID = 54;";
					list = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, finalQueryString);
					// if (list.size() > 0) {
					genes2mrina.put(gene, new HashSet<String>());
					// }

					for (int i = 0; i < list.size(); i++) {
						genes2mrina.get(gene).add(list.get(i).getColumn()[0]);
					}
				}
				count++;

			}

			HashSet<String> cleanMirna = new HashSet<String>();

			// HashSet<String> mirnas;
			Iterator<String> it2;
			// String mirna;
			StringBuilder sb = new StringBuilder();
			sb.append("PathwayID\tunigene\tmirna\r\n");
			for (String pathway : pw2genes.keySet()) {
				it = pw2genes.get(pathway).iterator();
				while (it.hasNext()) {
					gene = it.next();
					if (genes2mrina.containsKey(gene)) {
						it2 = genes2mrina.get(gene).iterator();
						while (it2.hasNext()) {
							mirna = it2.next();
							sb.append(pathway + "\t" + gene + "\t" + mirna + "\r\n");
						}
					}
				}
			}

			PrintWriter out;
			try {
				out = new PrintWriter("output.txt");
				out.println(sb.toString());
				out.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (String key : pw2genes.keySet()) {
				cleanMirna.clear();
				it = pw2genes.get(key).iterator();

				while (it.hasNext()) {
					gene = it.next();
					if (genes2mrina.containsKey(gene)) {
						cleanMirna.addAll(genes2mrina.get(gene));
					}
				}
				System.out.println("PW: " + key + " genes: " + pw2genes.get(key).size() + " mirnas: " + cleanMirna.size());

			}

			/*
			 * Iterator<String> it = gene2mirna.keySet().iterator();
			 * ArrayList<DBColumn> list2 = new ArrayList<DBColumn>();
			 * 
			 * Writer writer = null; try { writer = new BufferedWriter(new
			 * OutputStreamWriter( new FileOutputStream("genes2miRNA.txt"),
			 * "utf-8")); writer.write("gene,miRNAs"); while (it.hasNext()) {
			 * gene = it.next(); writer.write(gene + "," + gene2mirna.get(gene)
			 * + "\n"); // System.out.println(gene+" "+gCount.get(gene));; }
			 * 
			 * } catch (IOException ex) { // report } finally { try {
			 * writer.close(); } catch (Exception ex) { } }
			 * 
			 * int i = 0; while (it.hasNext()) {// && i<10) { if (i % 10 == 0) {
			 * // System.out.println(i*100.0/gCount.keySet().size()+"%"); // }
			 * gene = it.next();// list.get(i).getColumn()[0];
			 * System.out.println(gene2mirna.get(gene));
			 * 
			 * String q2 =
			 * "SELECT kegg_genes_pathway.name,kegg_genes_pathway.name," +
			 * "kegg_genes_pathway.number,kegg_genes_pathway.org, kegg_genes_name.name FROM "
			 * + "dawismd.kegg_genes_pathway inner join " +
			 * "dawismd.kegg_genes_name on kegg_genes_pathway.id=kegg_genes_name.id "
			 * + "where kegg_genes_name.name = '" + gene +
			 * "' and kegg_genes_pathway.org='hsa' order by kegg_genes_pathway.name,"
			 * + "kegg_genes_name.name;"; //
			 * q2.replaceFirst(QUESTION_MARK,gene); // System.out.println(q2);
			 * list2 = new // Wrapper().requestDbContent(2, q2); //
			 * System.out.println(list2.size()); for (int j = 0; j <
			 * list2.size(); j++) { number = list2.get(j).getColumn()[2];
			 * 
			 * if (map.containsKey(number)) { map.put(number,
			 * 
			 * map.get(number) + gene2mirna.get(gene)); } else { map.put(number,
			 * gene2mirna.get(gene)); } } i++; }
			 * 
			 * it = map.keySet().iterator(); String key; while (it.hasNext()) {
			 * key = it.next(); System.out.println(key + "\t" + map.get(key)); }
			 */

			/*
			 * if (allKEGGPathways.size() > 0) { MirnaResultKeggWindow
			 * mirnaResultKeggWindow = new MirnaResultKeggWindow(
			 * allKEGGPathways); Vector keggPAthwayResults =
			 * mirnaResultKeggWindow .getAnswer(); if (keggPAthwayResults.size()
			 * != 0) { String keggPathwayNumber = ""; String keggPathwayName =
			 * ""; final Iterator it3 = keggPAthwayResults .iterator(); while
			 * (it3.hasNext()) {
			 * 
			 * String[] pathwayResutls = (String[]) it3 .next();
			 * keggPathwayNumber= "hsa"+pathwayResutls[1]; keggPathwayName =
			 * pathwayResutls[0]; } } }
			 */
			// }
			// }

		} else if ("mirnaTargets".equals(event)) {
			// System.out.println("targets: ");
			if (con.containsPathway()) {
				Pathway pw = graphInstance.getPathway();
				if (pw.hasGotAtLeastOneElement()) {
					Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
					BiologicalNodeAbstract bna;
					final String QUESTION_MARK = new String("\\?");
					ArrayList<DBColumn> resultsDBSearch;
					HashMap<String, SRNA> srnas = new HashMap<String, SRNA>();
					SRNA srna;
					Point2D p;
					Expression exp;
					HashMap<BiologicalNodeAbstract, ArrayList<DBColumn>> data = new HashMap<BiologicalNodeAbstract, ArrayList<DBColumn>>();
					while (it.hasNext()) {
						bna = it.next();

						// System.out.println(it.next().getName());
						String finalQueryString = miRNAqueries.miRNA_get_Mirnas.replaceFirst(QUESTION_MARK, "'" + bna.getLabel() + "'");
						// System.out.println(finalQueryString);
						resultsDBSearch = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, finalQueryString);
						if (resultsDBSearch.size() > 0) {
							data.put(bna, resultsDBSearch);
						}
					}

					String[] column;

					Iterator<BiologicalNodeAbstract> itBNA = data.keySet().iterator();

					while (itBNA.hasNext()) {
						bna = itBNA.next();
						resultsDBSearch = data.get(bna);
						for (int i = 0; i < resultsDBSearch.size(); i++) {
							column = resultsDBSearch.get(i).getColumn();
							if (srnas.containsKey(column[0])) {
								srna = srnas.get(column[0]);
							} else {
								srna = new SRNA(column[0], column[0]);
								// p = new
								// Point2D.Double(myGraph.getVertexLocation(bna).getX(),
								// myGraph.getVertexLocation(bna).getY());
								// .findNearestFreeVertexPosition(bna.getKEGGnode()
								// .getXPos(), bna.getKEGGnode().getYPos(),
								// 100);
								p = Circle.getPointOnCircle(pw.getGraph().getVertexLocation(bna), 20, 2.0 * ((double) (Math.random() % (Math.PI))));
								pw.addVertex(srna, p);
								srnas.put(column[0], srna);
								// srnaParents.put(srna.getID(),
								// bna.getID());
							}
							srna.setReference(false);
							exp = new Expression("", "", srna, bna);
							exp.setDirected(true);
							pw.addEdge(exp);
							pw.addEdgeToView(exp, true);
						}
					}
					pw.getGraph().updateGraph();
					pw.getGraph().updateLayout();

				} else {
					JOptionPane.showMessageDialog(w, "Please create a network before.");
				}
			} else {
				JOptionPane.showMessageDialog(w, "Please create a network before.");
			}

		} else if ("shake".equals(event)) {

			if (con.containsPathway()) {
				Runnable animator = new Runnable() {

					@Override
					public void run() {
						BiologicalNodeAbstract bna;
						Point2D p;
						Point2D inv;
						GraphInstance graphInstance = new GraphInstance();
						for (int i = 0; i < 10; i++) { // //
							double offset = 5;
							if (i % 2 == 0) {
								offset *= -1;
							}
							VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = graphInstance.getPathway().getGraph()
									.getVisualizationViewer();
							double scaleV = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
							double scaleL = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
							double scale;
							if (scaleV < 1) {
								scale = scaleV;
							} else {
								scale = scaleL;
							}
							offset /= scale;

							Iterator<BiologicalNodeAbstract> it = graphInstance.getPathway().getAllGraphNodes().iterator();
							while (it.hasNext()) {
								bna = it.next();

								if (bna instanceof Enzyme) {
									p = graphInstance.getPathway().getGraph().getVertexLocation(bna); //
									inv = //
									graphInstance.getPathway().getGraph().getVisualizationViewer().getRenderContext().getMultiLayerTransformer()
											.inverseTransform(p); // inv.setLocation(inv.getX()
																	// + offset,
																	// //
																	// inv.getY());

									// p = //
									graphInstance.getPathway().getGraph().getVisualizationViewer().getRenderContext().getMultiLayerTransformer()
											.transform(inv);
									vv.getModel().getGraphLayout().setLocation(bna, new Point2D.Double(p.getX() + offset, p.getY()));
								}
							}

							try {
								Thread.sleep(100);
							} catch (InterruptedException ex) {
							}
						}
					}
				};
				Thread thread = new Thread(animator);
				thread.start();
			}

		} else if ("graphPicture".equals(event)) {
			// System.out.println("shake it");
			// String latexFormula = FormulaParser.parseToLatex(formula);
			// TeXIcon icon = FormulaParser.getTeXIcon(latexFormula);
			Pathway pw = graphInstance.getPathway();
			MyGraph mg = pw.getGraph();
			MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> v = pw.getGraph().getVisualizationViewer();
			// System.out.println(v.getGraphLayout().getClass());
			// v.setBackground(Color.white);
			if (!(mg.getLayout() instanceof StaticLayout)) {
				graphInstance.getPathway().getGraph().changeToStaticLayout();
			}
			BasicVisualizationServer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = new BasicVisualizationServer<BiologicalNodeAbstract, BiologicalEdgeAbstract>(
					pw.getGraph().getLayout());
			// System.out.println(vv.getGraphLayout().getSize());
			VisualizationImageServer<BiologicalNodeAbstract, BiologicalEdgeAbstract> wvv = new VisualizationImageServer<BiologicalNodeAbstract, BiologicalEdgeAbstract>(
					pw.getGraph().getLayout(), pw.getGraph().getLayout().getSize());
			wvv.setBackground(Color.white);
			for (int i = 0; i < v.getPreRenderers().size(); i++) {
				wvv.addPreRenderPaintable(v.getPreRenderers().get(i));
			}

			GraphCenter gc = new GraphCenter(mg);
			double width = gc.getWidth() * 2;
			double height = gc.getHeight() * 2;

			// System.out.println(v.getWidth());
			// System.out.println(maxX-minX);
			// System.out.println();
			// System.out.println(v.getWidth());
			// System.out.println(v.getHeight());

			// wvv.setSize(new Dimension((int) width, (int) height));

			// wvv.setSize(1300,700);

			// v.getRenderer();
			/*
			 * wvv.getRenderer().setEdgeLabelRenderer((v.getRenderer().
			 * getEdgeLabelRenderer()));
			 * wvv.getRenderer().setEdgeRenderer(v.getRenderer().getEdgeRenderer
			 * ()); wvv.getRenderer().setVertexLabelRenderer(v.getRenderer().
			 * getVertexLabelRenderer());
			 * wvv.getRenderer().setVertexRenderer(v.getRenderer().
			 * getVertexRenderer());
			 * 
			 * 
			 * 
			 * 
			 * 
			 * wvv.getRenderContext().setVertexStrokeTransformer(v.
			 * getRenderContext().getVertexStrokeTransformer());
			 * wvv.getRenderContext().setVertexLabelTransformer(v.
			 * getRenderContext().getVertexLabelTransformer());
			 * wvv.getRenderContext().setVertexShapeTransformer(v.
			 * getRenderContext().getVertexShapeTransformer());
			 * wvv.getRenderContext().setVertexDrawPaintTransformer(v.
			 * getRenderContext().getVertexDrawPaintTransformer());
			 * wvv.getRenderContext().setVertexFillPaintTransformer(v.
			 * getRenderContext().getVertexFillPaintTransformer());
			 * wvv.getRenderContext().setVertexLabelRenderer(v.getRenderContext(
			 * ).getVertexLabelRenderer());
			 * 
			 * wvv.getRenderContext().setEdgeLabelTransformer(v.getRenderContext
			 * ().getEdgeLabelTransformer());
			 * wvv.getRenderContext().setEdgeStrokeTransformer(v.
			 * getRenderContext().getEdgeArrowStrokeTransformer());
			 * wvv.getRenderContext().setEdgeDrawPaintTransformer(v.
			 * getRenderContext().getEdgeDrawPaintTransformer());
			 * wvv.getRenderContext().setEdgeFillPaintTransformer(v.
			 * getRenderContext().getEdgeFillPaintTransformer());
			 * wvv.getRenderContext().setEdgeShapeTransformer(v.getRenderContext
			 * ().getEdgeShapeTransformer());
			 * wvv.getRenderContext().setEdgeLabelRenderer(v.getRenderContext().
			 * getEdgeLabelRenderer());
			 * wvv.getRenderContext().setEdgeArrowTransformer(v.getRenderContext
			 * ().getEdgeArrowTransformer());
			 */
			// wvv.setSize(v.getSize());

			// wvv.setRenderContext(v.getRenderContext());
			wvv.setBackground(Color.white);
			// int width = v.getSize().width;
			// int height = v.getSize().height;
			// wvv.setDoubleBuffered(false);
			// v.setDoubleBuffered(false);
			// vv.setDoubleBuffered(false);
			wvv.setRenderContext(v.getRenderContext());

			double scaleV = v.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
			double scaleL = v.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();

			double scale;
			if (scaleV < 1) {
				scale = scaleV;
				// System.out.println("kleiner");
				// wvv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setScale(scaleV,
				// scaleV, wvv.getCenter());
				// wvv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setScale(1,
				// 1, wvv.getCenter());
			} else {
				scale = scaleL;
				// wvv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setScale(scaleV,
				// scaleV, wvv.getCenter());
				// wvv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setScale(scaleL,
				// scaleL, wvv.getCenter());
			}
			// System.out.println(scaleL);
			wvv.setBounds((int) (gc.getMinX()), (int) (gc.getMinX()), (int) (width * scale) + 100, (int) (height * scale) + 100);
			// wvv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setScale(scaleV,
			// scaleV, wvv.getCenter());
			// wvv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setScale(scaleL,
			// scaleL, wvv.getCenter());

			//Properties p = new Properties();
			//p.setProperty("PageSize", "A5");
			// p.setProperty(SVGGraphics2D.TEXT_AS_SHAPES, "false");
			//VectorGraphics g;

			// g = new SVGGraphics2D(new File("Output.svg"), new Dimension(100,
			// 100));
			// g.setProperties(p);
			// g.startExport();
			// wvv.print(g);
			// g.endExport();

			ExportDialog export = new ExportDialog();
			// export.setUserProperties(p);
			// System.out.println(export.getInitialValue());

			// export.setWantsInput(true);
			// export.setInitialSelectionValue(5);
			export.showExportDialog(v, "Export graph as ...", wvv, "export");
			// System.out.println(export.getSelectionValues());
			// wvv.setDoubleBuffered(true);
			// vv.setDoubleBuffered(true);
			// v.setDoubleBuffered(true);

			/*
			 * Properties p = new Properties(); p.setProperty("PageSize", "A5");
			 * VectorGraphics g; try { g = new SVGGraphics2D(new
			 * File("Output.svg"), new Dimension(400, 300));
			 * g.setBackground(Color.WHITE); g.setProperties(p);
			 * g.startExport(); //vis.paintAll(g); g.endExport(); } catch
			 * (IOException e1) { // TODO Auto-generated catch block
			 * e1.printStackTrace(); }
			 */
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
		Iterator<BiologicalNodeAbstract> hsit = graphInstance.getPathway().getAllGraphNodes().iterator();
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
		Iterator<BiologicalEdgeAbstract> edgeit = graphInstance.getPathway().getAllEdges().iterator();
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
		SimpleMatrixDouble cMatrix = new SimpleMatrixDouble(this.initArray(numberPlaces, numberTransitions));
		cMatrix.add(bMatrix);
		cMatrix.add(fMatrix);

		c = new DenseDoubleMatrix2D(cMatrix.getData());
	}
}
