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
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

import edu.uci.ics.jung.algorithms.layout.*;
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
import configurations.SettingsManager;
import configurations.gui.LayoutConfig;
import configurations.gui.SettingsPanel;
import dataMapping.DataMappingColorMVC;
import database.mirna.MirnaSearch;
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
import graph.jung.classes.MyGraph;
import graph.layouts.gemLayout.GEMLayout;
import graph.layouts.hctLayout.HCTLayout;
import graph.layouts.hebLayout.HEBLayout;
import gui.AboutWindow;
import gui.AllPopUpsWindow;
import gui.InfoWindow;
import gui.LabelToDataMappingWindow;
import gui.LabelToDataMappingWindow.InputFormatException;
import gui.MainWindow;
import gui.NodesEdgesTypesWindow;
import gui.PopUpDialog;
import gui.tables.MyTable;
import gui.visualization.PreRenderManager;
import io.OpenDialog;
import io.PNDoc;
import io.SaveDialog;
import io.SuffixAwareFilter;
import io.sbml.JSBMLOutput;
import petriNet.Cov;
import petriNet.CovNode;
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

public class MenuListener implements ActionListener {
	private Object[][] rP;
	private Object[][] rT;
	private Object[][] rI;
	private final List<Double> start = new ArrayList<>();

	private final JLabel invariant = new JLabel();
	private final JPanel pane = new JPanel();
	private final JDialog d = new JDialog();

	private final JLabel reachable = new JLabel();

	private DenseDoubleMatrix2D c;

	private int places = 0;
	private int transitions = 0;

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
		final MenuActionCommands command = MenuActionCommands.get(e.getActionCommand());
		if (command == null) {
			return;
		}
		switch (command) {
		case newNetwork:
			newNetwork();
			break;
		case openNetwork:
			openNetwork();
			break;
		case closeNetwork:
			MainWindow.getInstance().removeTab(true);
			break;
		case closeAllNetworks:
			MainWindow.getInstance().removeAllTabs();
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
		case connectedGraph:
			new RandomConnectedGraphGui();
			break;
		case hamiltonGraph:
			new RandomHamiltonGraphGui();
			break;
		case exportNetwork:
			exportNetwork();
			break;
		case saveAs:
			saveAs();
			break;
		case save:
			save();
			break;
		case exit:
			ProgramFileLock.releaseLock();
			System.exit(0);
			break;
		case springLayout:
			changeLayout(SpringLayout.class);
			break;
		case kkLayout:
			changeLayout(KKLayout.class);
			break;
		case frLayout:
			changeLayout(FRLayout.class);
			break;
		case circleLayout:
			changeLayout(CircleLayout.class);
			break;
		case hebLayout:
			changeLayout(HEBLayout.class);
			break;
		case hctLayout:
			changeLayout(HCTLayout.class);
			break;
		case gemLayout:
			changeLayout(GEMLayout.class);
			break;
		case isomLayout:
			changeLayout(ISOMLayout.class);
			break;
		case internet:
			new SettingsPanel(0);
			break;
		case interaction:
			interaction();
			break;
		case devMode:
			devMode();
			break;
		case about:
			new AboutWindow();
			break;
		case graphSettings:
			new SettingsPanel(1);
			break;
		case visualizationSettings:
			new SettingsPanel(2);
			break;
		case simulationSettings:
			new SettingsPanel(3);
			break;
		case openTestP:
			openTestP();
			break;
		case openTestT:
			openTestT();
			break;
		case testP:
			testP();
			break;
		case testT:
			testT();
			break;
		case openCov:
			openCov();
			break;
		case cov:
			cov();
			break;
		case createCov:
			createCov();
			break;
		case editElements:
			new PNTableDialog().setVisible(true);
			break;
		case loadModResult:
			loadModResult();
			break;
		case simulate:
			simulate();
			break;
		case dataMappingColor:
			DataMappingColorMVC.createDataMapping();
			break;
		case datamining:
			datamining();
			break;
		case rendererSettings:
			rendererSettings();
			break;
		case createDoc:
			createDoc();
			break;
		case dataLabelMapping:
			dataLabelMapping();
			break;
		case enrichMirna:
			enrichMirna();
			break;
		case enrichGene:
			enrichGene();
			break;
		case shake:
			shakeEnzymes();
			break;
		case graphPicture:
			graphPicture();
			break;
		case wuff:
			wuff();
			break;
		case transform:
			transform();
			break;
		case ruleManager:
			RuleManagementWindow.getInstance().show();
			break;
		case showPN:
			showPN();
			break;
		case showTransformResult:
			showTransformResult();
			break;
		case allPopUps:
			new AllPopUpsWindow();
			break;
		case nodesEdgesTypes:
			nodesEdgesTypes();
			break;
		}
	}

	private static void interaction() {
		if (ensurePathwayWithAtLeastOneElement()) {
			new InfoWindow(false);
		}
	}

	private static boolean ensurePathwayWithAtLeastOneElement() {
		final GraphContainer con = GraphContainer.getInstance();
		if (!con.containsPathway()) {
			PopUpDialog.getInstance().show("Error", "Please create a network first.");
			return false;
		}
		Pathway pw = GraphInstance.getPathway();
		if (pw == null || !pw.hasGotAtLeastOneElement()) {
			PopUpDialog.getInstance().show("Error", "Please create a network first.");
			return false;
		}
		return true;
	}

	private static void datamining() {
		if (ensurePathwayWithAtLeastOneElement()) {
			new SmacofView();
		}
	}

	private static void rendererSettings() {
		if (ensurePathwayWithAtLeastOneElement()) {
			PreRenderManager.getInstance();
		}
	}

	private static void dataLabelMapping() {
		// Open new window for file input
		if (ensurePathwayWithAtLeastOneElement()) {
			try {
				new LabelToDataMappingWindow();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (InputFormatException ife) {
				PopUpDialog.getInstance().show("Inputfile error", ife.getMessage());
				// JOptionPane.showMessageDialog(w, ife.getMessage(), "Inputfile error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private static void enrichGene() {
		if (ensurePathwayWithAtLeastOneElement()) {
			MirnaSearch.enrichGenes(GraphInstance.getPathway(), true, true, false);
		}
	}

	private static void enrichMirna() {
		if (ensurePathwayWithAtLeastOneElement()) {
			MirnaSearch.enrichMirnas(GraphInstance.getPathway(), true, true, false);
		}
	}

	private static void nodesEdgesTypes() {
		if (ensurePathway()) {
			new NodesEdgesTypesWindow(GraphInstance.getPathway());
		}
	}

	private static boolean ensurePathway() {
		final GraphContainer con = GraphContainer.getInstance();
		Pathway pw = GraphInstance.getPathway();
		if (!con.containsPathway() || pw == null) {
			PopUpDialog.getInstance().show("Error", "Please create a network first.");
			return false;
		}
		return true;
	}

	private static void showTransformResult() {
		final GraphContainer con = GraphContainer.getInstance();
		Pathway pw = GraphInstance.getPathway();
		if (con.containsPathway()) {
			if (pw.getTransformationInformation() != null && pw.getTransformationInformation().getPetriNet() != null
				&& !pw.isPetriNet()) {
				new TransformationInformationWindow(pw).show();
			} else {
				PopUpDialog.getInstance().show("Error",
						"Please transform the biological network into a Petri net first!.");
			}
		} else {
			PopUpDialog.getInstance().show("Error", "Please create a network first.");
		}
	}

	private static void showPN() {
		if (ensurePathway()) {
			Pathway pw = GraphInstance.getPathway();
			if (pw != null && pw.getTransformationInformation() != null
				&& pw.getTransformationInformation().getPetriNet() != null && !pw.isPetriNet()) {
				Pathway petriNet = pw.getTransformationInformation().getPetriNet();
				Map<BiologicalNodeAbstract, Point2D> staticNodes = new HashMap<>();
				for (BiologicalNodeAbstract bna : pw.getTransformationInformation().getBnToPnMapping().values()) {
					staticNodes.put(bna, petriNet.getGraph().getVertexLocation(bna));
				}
				petriNet.getGraph().changeToGEMLayout(staticNodes);
				CreatePathway.showPathway(petriNet);
			} else {
				PopUpDialog.getInstance().show("Error",
						"Please transform the biological network into a Petri net first!.");
			}
		}
	}

	private static void transform() {
		if (ensurePathwayWithAtLeastOneElement()) {
			Pathway pw = GraphInstance.getPathway();
			if (!pw.isPetriNet()) {
				List<Rule> rules = RuleManager.getInstance().getActiveRules();
				if (rules.isEmpty()) {
					PopUpDialog.getInstance().show("Error", "No active transformation rules found!.");
					return;
				}
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
				MainWindow.getInstance().updateProjectProperties();
				// CreatePathway.showPathway(petriNet);
			} else {
				PopUpDialog.getInstance().show("Error",
						"Please create a biological network first. A Petri net cannot be transformed!.");
			}
		}
	}

	private static void wuff() {
		if (ensurePathwayWithAtLeastOneElement()) {
			final Pathway pw = GraphInstance.getPathway();
			for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
				if (bna instanceof DynamicNode) {
					final DynamicNode dn = (DynamicNode) bna;
					final String maximalSpeed = KineticBuilder.createConvenienceKineticReversible(bna, pw);
					dn.setMaximalSpeed(maximalSpeed);
				}
			}
		}
	}

	private static void graphPicture() {
		if (ensurePathwayWithAtLeastOneElement()) {
			Pathway pw = GraphInstance.getPathway();
			new SaveDialog(new SuffixAwareFilter[]{SuffixAwareFilter.PNG, SuffixAwareFilter.SVG},
						   SaveDialog.DATA_TYPE_GRAPH_PICTURE, pw.prepareGraphToPrint());
		}
	}

	private static void createDoc() {
		Pathway pw = GraphInstance.getPathway();
		PopUpDialog.getInstance().show("Latex generation", "Generation in progress, it will take a short moment!");
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
		Thread thread = new Thread(() -> {
			try {
				while (!new File(docDir + "export.png").exists()) {
					Thread.sleep(100);
				}
			} catch (Exception e12) {
				e12.printStackTrace();
			}
		});
		thread.start();
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
			Thread t = new Thread(() -> {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while (p.isAlive()) {
						System.out.println(br.readLine());
						Thread.sleep(100);
					}
					// p.destroyForcibly();
					Process p2 = pb.start();
					BufferedReader br2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
					while (p2.isAlive()) {
						System.out.println(br2.readLine());
						Thread.sleep(100);
					}
					PopUpDialog.getInstance().show("Latex compilation successful!",
							"PDF can be found at:\n" + docDir);
				} catch (Exception e13) {
					e13.printStackTrace();
				}
			});
			t.start();
		} catch (IOException e1) {
			PopUpDialog.getInstance().show("Compilation of latex failed!",
					"pdflatex executable could not be found!\n Generated Latex file can be found at:\n" + docDir);
			System.err.println("Could not compile latex. Find tex-file at: " + docDir);
		}
	}

	private static void simulate() {
		if (ensurePathwayWithAtLeastOneElement()) {
			GraphInstance.getPathway().getPetriNetSimulation().showMenu();
		}
	}

	private static void loadModResult() {
		final GraphContainer con = GraphContainer.getInstance();
		Pathway pw = GraphInstance.getPathway();
		if (con.containsPathway()) {
			if (!pw.hasGotAtLeastOneElement()) {
				PopUpDialog.getInstance().show("Error", "Network is empty.");
				return;
			}
			if (pw.isPetriNet() || pw.getTransformationInformation() != null
								   && pw.getTransformationInformation().getPetriNet() != null) {
				List<SuffixAwareFilter> filters;
				filters = new ArrayList<>();
				filters.add(SuffixAwareFilter.VANESA_SIM_RESULT);
				new OpenDialog(filters).show();
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a Petri net first.");
			}
		} else {
			PopUpDialog.getInstance().show("Error", "Please create a network before.");
		}
	}

	private static void createCov() {
		final MainWindow w = MainWindow.getInstance();
		// MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
		// Cov cov = new Cov();
		if (JOptionPane.showConfirmDialog(w.getFrame(),
										  "The calculation of the reach graph could take long time, especially if you have many places in your network. Do you want to perform the calculation anyway?",
										  "Please Conform your action...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			new ReachController();
		GraphInstance.getMyGraph().changeToGEMLayout();
	}

	private void cov() {
		final MainWindow w = MainWindow.getInstance();
		// Teste ob Invariante:
		if (c == null) {
			createCMatrix();
		}
		double[] vd = new double[places];
		for (int i = 0; i < places; i++) {
			vd[i] = Double.parseDouble(rI[i + 1][1].toString());
		}
		DenseDoubleMatrix1D v = new DenseDoubleMatrix1D(vd);
		DenseDoubleMatrix1D x = new DenseDoubleMatrix1D(c.columns());
		c.zMult(v, x, 1, 0, true);
		IntArrayList l = new IntArrayList();
		x.getNonZeros(l, null);
		boolean validInvariant = false;
		if (l.isEmpty()) {
			// reachable.setText("This vector is a valid Invariante");
			validInvariant = true;
		} else {
			reachable.setText("no valid p-invariant");
		}
		d.pack();
		d.setLocationRelativeTo(w.getFrame());
		d.setVisible(true);
		if (validInvariant) {
			double[] s = new double[start.size()];
			for (int i = 0; i < start.size(); i++) {
				s[i] = start.get(i);
			}
			double[] target = new double[places];
			for (int i = 0; i < places; i++) {
				target[i] = Double.parseDouble(rP[i + 1][1].toString());
			}
			// start-marking
			DenseDoubleMatrix1D sv = new DenseDoubleMatrix1D(s);
			// target marking
			DenseDoubleMatrix1D tv = new DenseDoubleMatrix1D(target);
			double erg1 = v.zDotProduct(sv, 0, v.size());
			double erg2 = v.zDotProduct(tv, 0, v.size());
			if (erg1 != erg2) {
				reachable.setText("not reachable");
			} else {
				if (cov == null) {
					cov = new Cov();
				}
				String pwName = cov.getNewName();
				HashMap<String, Integer> name2id = cov.getName2id();
				double[] marking = new double[name2id.size()];
				for (int i = 1; i <= places; i++) {
					String name = rP[i][0].toString();
					String value = rP[i][1].toString();
					int index = name2id.get(name);
					marking[index] = Double.parseDouble(value);
				}
				final Pathway pw = GraphInstance.getContainer().getPathway(pwName);
				boolean reachable = false;
				for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
					if (bna instanceof CovNode) {
						CovNode n = (CovNode) bna;
						if (n.getTokenList().isGreaterEqual(marking)) {
							if (n.getTokenList().isEqual(marking)) {
								this.reachable.setText("it is reached by " + n.getTokenList());
								reachable = true;
								break;
							}
						}
					}
				}
				if (!reachable) {
					for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
						if (bna instanceof CovNode) {
							CovNode n = (CovNode) bna;
							if (n.getTokenList().isGreater(marking)) {
								this.reachable.setText("it is covered by " + n.getTokenList());
								reachable = true;
								break;
							}
						}
					}
				}
				if (!reachable) {
					this.reachable.setText("not reachable");
				}
			}
		}
		d.pack();
		d.setLocationRelativeTo(w.getFrame());
		d.setVisible(true);
		// w.updateTheoryProperties();
	}

	private void openCov() {
		Pathway pw = GraphInstance.getPathway();
		places = 0;
		transitions = 0;
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
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
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (bna instanceof Place) {
				rP[i + 1][0] = bna.getName();
				rP[i + 1][1] = 0;
				rI[i + 1][0] = bna.getName();
				rI[i + 1][1] = 0;
				i++;
			}
		}
		MyTable tP = new MyTable(rP, new String[] { "Node", "Value" });
		MyTable tI = new MyTable(rI, new String[] { "Node", "Value" });
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
		d.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		d.setVisible(true);
	}

	private void testT() {
		if (c == null) {
			createCMatrix();
		}
		double[] vd = new double[this.transitions];
		for (int i = 0; i < transitions; i++) {
			vd[i] = Double.parseDouble(rT[i][1].toString());
		}
		DenseDoubleMatrix1D v = new DenseDoubleMatrix1D(vd);
		DenseDoubleMatrix1D x = new DenseDoubleMatrix1D(c.rows());
		c.zMult(v, x, 1, 0, false);
		IntArrayList l = new IntArrayList();
		x.getNonZeros(l, null);
		if (l.isEmpty()) {
			invariant.setText("This vector is a valid invariant");
		} else {
			invariant.setText("This vector is not a valid invariant");
		}
		d.pack();
		d.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		d.setVisible(true);
	}

	private void testP() {
		if (c == null) {
			createCMatrix();
		}
		double[] vd = new double[places];
		for (int i = 0; i < places; i++) {
			vd[i] = Double.parseDouble(rP[i][1].toString());
		}
		DenseDoubleMatrix1D v = new DenseDoubleMatrix1D(vd);
		DenseDoubleMatrix1D x = new DenseDoubleMatrix1D(c.columns());
		c.zMult(v, x, 1, 0, true);
		IntArrayList l = new IntArrayList();
		x.getNonZeros(l, null);
		if (l.isEmpty()) {
			invariant.setText("This vector is a valid invariant");
		} else {
			invariant.setText("This vector is not a valid invariant");
		}
		d.pack();
		d.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		d.setVisible(true);
	}

	private void openTestT() {
		Pathway pw = GraphInstance.getPathway();
		transitions = 0;
		places = 0;
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (bna instanceof Transition) {
				transitions++;
			} else if (bna instanceof Place) {
				places++;
			}
		}
		rT = new Object[transitions][2];
		int i = 0;
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (bna instanceof Transition) {
				rT[i][0] = bna.getName();
				rT[i][1] = 0;
				i++;
			}
		}
		MyTable tT = new MyTable(rT, new String[] { "Transition", "Value" });
		JButton testT = new JButton("Test T-invariant");
		testT.setActionCommand(MenuActionCommands.testT.value);
		testT.addActionListener(this);
		testT.setEnabled(transitions != 0 && places != 0);
		invariant.setText("");
		d.setAlwaysOnTop(true);
		d.setContentPane(pane);
		pane.removeAll();
		pane.add(tT);
		pane.add(testT);
		pane.add(invariant);
		d.pack();
		d.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		d.setVisible(true);
	}

	private void openTestP() {
		Pathway pw = GraphInstance.getPathway();
		places = 0;
		transitions = 0;
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (bna instanceof Place) {
				places++;
			} else if (bna instanceof Transition) {
				transitions++;
			}
		}
		rP = new Object[places][2];
		int i = 0;
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (bna instanceof Place) {
				rP[i][0] = bna.getName();
				rP[i][1] = 0;
				i++;
			}
		}
		MyTable tP = new MyTable(rP, new String[] { "Vertex", "Value" });
		JButton testP = new JButton("Test P-invariant");
		testP.setActionCommand(MenuActionCommands.testP.value);
		testP.addActionListener(this);
		testP.setEnabled(transitions != 0 && places != 0);
		invariant.setText("");
		d.setAlwaysOnTop(true);
		d.setContentPane(pane);
		pane.removeAll();
		pane.add(tP);
		pane.add(testP);
		pane.add(invariant);
		d.pack();
		d.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		d.setVisible(true);
	}

	private static void devMode() {
		if (SettingsManager.getInstance().isDeveloperMode()) {
			MainWindow.getInstance().getMenu().setDeveloperLabel("Next launch: developer mode");
			PopUpDialog.getInstance().show("Mode changed", "Next time, VANESA will be started in normal mode!");
		} else {
			MainWindow.getInstance().getMenu().setDeveloperLabel("Next launch: normal mode");
			PopUpDialog.getInstance().show("Mode changed", "Next time, VANESA will be started in developer mode!");
		}
		SettingsManager.getInstance().setDeveloperMode(!SettingsManager.getInstance().isDeveloperMode());
	}

	@SuppressWarnings("rawtypes")
	private static void changeLayout(final Class<? extends Layout> layoutClass) {
		final GraphContainer con = GraphContainer.getInstance();
		final Pathway pw = GraphInstance.getPathway();
		if (con.containsPathway() && pw != null) {
			if (pw.hasGotAtLeastOneElement()) {
				LayoutConfig.changeToLayout(layoutClass);
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
		} else {
			PopUpDialog.getInstance().show("Error", "Please create a network first.");
		}
	}

	private static void save() {
		if (ensurePathwayWithAtLeastOneElement()) {
			Pathway pw = GraphInstance.getPathway();
			if (pw.getFile() != null) {
				JSBMLOutput jsbmlOutput;
				File file = pw.getFile();
				try {
					jsbmlOutput = new JSBMLOutput(new FileOutputStream(file), pw);
					String out = jsbmlOutput.generateSBMLDocument();
					if (out.length() > 0) {
						PopUpDialog.getInstance().show("Error", out);
					} else {
						GraphContainer.getInstance().renamePathway(pw, file.getName());
						pw.setName(file.getName());
						pw.setTitle(file.getName());
						MainWindow.getInstance().renameSelectedTab(file.getName());
						PopUpDialog.getInstance().show("JSBML export", "Saving was successful!");
					}
				} catch (FileNotFoundException | XMLStreamException e1) {
					e1.printStackTrace();
				}
			} else {
				new SaveDialog(new SuffixAwareFilter[]{SuffixAwareFilter.SBML}, SaveDialog.DATA_TYPE_NETWORK_EXPORT);
			}
		}
	}

	private static void saveAs() {
		if (ensurePathwayWithAtLeastOneElement()) {
			new SaveDialog(new SuffixAwareFilter[]{SuffixAwareFilter.SBML}, SaveDialog.DATA_TYPE_NETWORK_EXPORT);
		}
	}

	private static void exportNetwork() {
		if (ensurePathwayWithAtLeastOneElement()) {
				new SaveDialog(new SuffixAwareFilter[]{
						SuffixAwareFilter.GRAPH_ML, SuffixAwareFilter.MO, SuffixAwareFilter.CSML,
						SuffixAwareFilter.PNML, SuffixAwareFilter.GRAPH_TEXT_FILE
				}, SaveDialog.DATA_TYPE_NETWORK_EXPORT);
		}
	}

	private static void newNetwork() {
		final MainWindow w = MainWindow.getInstance();
		final int answer = JOptionPane.showOptionDialog(w.getFrame(), "Which type of modeling do you prefer?",
														"Choose Network Type...", JOptionPane.YES_NO_OPTION,
														JOptionPane.QUESTION_MESSAGE, null,
														new String[]{"Biological Graph", "Petri Net"},
														JOptionPane.CANCEL_OPTION);
		if (answer != -1) {
			new CreatePathway();
			GraphInstance.getPathway().setIsPetriNet(answer == JOptionPane.NO_OPTION);
			w.getBar().paintToolbar(answer == JOptionPane.NO_OPTION);
			w.updateAllGuiElements();
		}
	}

	private static void openNetwork() {
		new OpenDialog(SuffixAwareFilter.SBML, SuffixAwareFilter.VAML, SuffixAwareFilter.GRAPH_ML,
					   SuffixAwareFilter.KGML, SuffixAwareFilter.GRAPH_TEXT_FILE).show();
	}

	private void shakeEnzymes() {
		if (!GraphContainer.getInstance().containsPathway()) {
			return;
		}
		CompletableFuture.runAsync(() -> {
			Pathway pw = GraphInstance.getPathway();
			MyGraph graph = pw.getGraph();
			for (int i = 0; i < 10; i++) {
				double offset = 5;
				if (i % 2 == 0) {
					offset *= -1;
				}
				VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph()
						.getVisualizationViewer();
				double scaleV = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
				double scaleL = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT)
						.getScale();
				double scale = scaleV < 1 ? scaleV : scaleL;
				offset /= scale;
				for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
					if (bna instanceof Enzyme) {
						Point2D p = graph.getVertexLocation(bna);
						Point2D inv = graph.getVisualizationViewer().getRenderContext().getMultiLayerTransformer()
								.inverseTransform(p);
						graph.getVisualizationViewer().getRenderContext().getMultiLayerTransformer().transform(inv);
						vv.getModel().getGraphLayout().setLocation(bna,
								new Point2D.Double(p.getX() + offset, p.getY()));
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException ignored) {
				}
			}
		});
	}

	private void createCMatrix() {
		HashMap<BiologicalNodeAbstract, Integer> hmplaces = new HashMap<>();
		HashMap<BiologicalNodeAbstract, Integer> hmtransitions = new HashMap<>();
		int numberPlaces = 0;
		int numberTransitions = 0;
		ArrayList<String> names = new ArrayList<>();
		for (BiologicalNodeAbstract bna : GraphInstance.getPathway().getAllGraphNodes()) {
			if (bna instanceof Transition) {
				hmtransitions.put(bna, numberTransitions);
				numberTransitions++;
			} else if (bna instanceof Place) {
				Place p = (Place) bna;
				hmplaces.put(bna, numberPlaces);
				names.add(p.getName());
				this.start.add(p.getTokenStart());
				numberPlaces++;
			}
		}
		double[][] f = VanesaUtility.createMatrix(numberPlaces, numberTransitions);
		double[][] b = VanesaUtility.createMatrix(numberPlaces, numberTransitions);
		// einkommende Kanten (backward matrix)
		Iterator<BiologicalEdgeAbstract> edgeit = GraphInstance.getPathway().getAllEdges().iterator();
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
		SimpleMatrixDouble cMatrix = new SimpleMatrixDouble(
				VanesaUtility.createMatrix(numberPlaces, numberTransitions));
		cMatrix.add(bMatrix);
		cMatrix.add(fMatrix);
		c = new DenseDoubleMatrix2D(cMatrix.getData());
	}
}
