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
import database.mirna.MirnaSearch;
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
import gui.PopUpDialog;
import gui.NodesEdgesTypesWindow;
import gui.visualization.PreRenderManager;
import io.OpenDialog;
import io.PNDoc;
import io.SaveDialog;
import gui.tables.MyTable;
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
import io.sbml.JSBMLOutput;

public class MenuListener implements ActionListener {
	private Object[][] rP;
	private Object[][] rT;
	private Object[][] rI;
	private final ArrayList<Double> start = new ArrayList<>();

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
		MainWindow w = MainWindow.getInstance();
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
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new SaveDialog(SaveDialog.FORMAT_GRAPHML + SaveDialog.FORMAT_MO + SaveDialog.FORMAT_CSML +
							SaveDialog.FORMAT_PNML + SaveDialog.FORMAT_TXT, SaveDialog.DATA_TYPE_NETWORK_EXPORT);
				} else {
					PopUpDialog.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case saveAs:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new SaveDialog(SaveDialog.FORMAT_SBML, SaveDialog.DATA_TYPE_NETWORK_EXPORT);
				} else {
					PopUpDialog.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case save:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					if (graphInstance.getPathway().getFile() != null) {
						JSBMLOutput jsbmlOutput;
						File file = graphInstance.getPathway().getFile();
						try {
							jsbmlOutput = new JSBMLOutput(new FileOutputStream(file), new GraphInstance().getPathway());
							String out = jsbmlOutput.generateSBMLDocument();
							if (out.length() > 0) {
								PopUpDialog.getInstance().show("Error", out);
							} else {
								GraphContainer.getInstance().renamePathway(GraphInstance.getPathwayStatic(),
										file.getName());
								GraphInstance.getPathwayStatic().setName(file.getName());
								GraphInstance.getPathwayStatic().setTitle(file.getName());
								MainWindow.getInstance().renameSelectedTab(file.getName());
								PopUpDialog.getInstance().show("JSbml export", "Saving was successful!");
							}
						} catch (FileNotFoundException | XMLStreamException e1) {
							e1.printStackTrace();
						}
					} else {
						new SaveDialog(SaveDialog.FORMAT_SBML, SaveDialog.DATA_TYPE_NETWORK_EXPORT);
					}
				} else {
					PopUpDialog.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
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
					PopUpDialog.getInstance().show("Error", "Please create a network before.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network before.");
			}
			break;
		case kkLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToKKLayout();
					LayoutConfig.changeToLayout(KKLayout.class);
				} else {
					PopUpDialog.getInstance().show("Error", "Please create a network before.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network before.");
			}
			break;
		case frLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToFRLayout();
					LayoutConfig.changeToLayout(FRLayout.class);
				} else {
					PopUpDialog.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case circleLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToCircleLayout();
					LayoutConfig.changeToLayout(CircleLayout.class);
				} else {
					PopUpDialog.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case hebLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					LayoutConfig.changeToLayout(HEBLayout.class);
				} else {
					PopUpDialog.getInstance().show("Error", "Please create a network before.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network before.");
			}
			break;
		case hctLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					LayoutConfig.changeToLayout(HCTLayout.class);
				} else {
					PopUpDialog.getInstance().show("Error", "Please create a network before.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network before.");
			}
			break;
		case gemLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToCircleLayout();
					LayoutConfig.changeToLayout(GEMLayout.class);
					// graphInstance.getMyGraph().changeGraphLayout(4);

				} else {
					PopUpDialog.getInstance().show("Error", "Please create a network before.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network before.");
			}
			break;
		case isomLayout:
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					// graphInstance.getMyGraph().changeToISOMLayout();
					LayoutConfig.changeToLayout(ISOMLayout.class);
				} else {
					PopUpDialog.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case internet:
			new Settings(0);
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
			new Settings(1);
			break;
		case visualizationSettings:
			new Settings(2);
			break;
		case openTestP:
			Pathway pw = graphInstance.getPathway();
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
			MyTable tP = new MyTable(rP, new String[]{"Vertex", "Value"});
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
			d.setLocationRelativeTo(w.getFrame());
			d.setVisible(true);
			break;
		case openTestT:
			pw = graphInstance.getPathway();
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
			i = 0;
			for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
				if (bna instanceof Transition) {
					rT[i][0] = bna.getName();
					rT[i][1] = 0;
					i++;
				}
			}
			MyTable tT = new MyTable(rT, new String[]{"Transition", "Value"});
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
			d.setLocationRelativeTo(w.getFrame());
			d.setVisible(true);
			break;
		case testP:
			if (c == null) {
				createCMatrix();
			}
			double[] vd = new double[places];
			for (i = 0; i < places; i++) {
				vd[i] = Double.parseDouble(rP[i][1].toString());
			}
			DenseDoubleMatrix1D v = new DenseDoubleMatrix1D(vd);
			DenseDoubleMatrix1D x = new DenseDoubleMatrix1D(c.columns());
			c.zMult(v, x, 1, 0, true);
			IntArrayList l = new IntArrayList();
			x.getNonZeros(l, null);
			if (l.size() == 0) {
				invariant.setText("This vector is a valid invariant");
			} else {
				invariant.setText("This vector is not a valid invariant");
			}
			d.pack();
			d.setLocationRelativeTo(w.getFrame());
			d.setVisible(true);
			break;
		case testT:
			if (c == null) {
				createCMatrix();
			}
			vd = new double[this.transitions];
			for (i = 0; i < transitions; i++) {
				vd[i] = Double.parseDouble(rT[i][1].toString());
			}
			v = new DenseDoubleMatrix1D(vd);
			x = new DenseDoubleMatrix1D(c.rows());
			c.zMult(v, x, 1, 0, false);
			l = new IntArrayList();
			x.getNonZeros(l, null);
			if (l.size() == 0) {
				invariant.setText("This vector is a valid invariant");
			} else {
				invariant.setText("This vector is not a valid invariant");
			}
			d.pack();
			d.setLocationRelativeTo(w.getFrame());
			d.setVisible(true);
			break;
		case openCov:
			pw = graphInstance.getPathway();
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
			i = 0;
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
			tP = new MyTable(rP, new String[]{"Node", "Value"});
			MyTable tI = new MyTable(rI, new String[]{"Node", "Value"});
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
			break;
		case cov:
			// Teste ob Invariante:
			if (c == null) {
				createCMatrix();
			}
			vd = new double[places];
			for (i = 0; i < places; i++) {
				vd[i] = Double.parseDouble(rI[i + 1][1].toString());
			}
			v = new DenseDoubleMatrix1D(vd);
			x = new DenseDoubleMatrix1D(c.columns());
			c.zMult(v, x, 1, 0, true);
			l = new IntArrayList();
			x.getNonZeros(l, null);
			boolean validInvariant = false;
			if (l.size() == 0) {
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
				for (i = 0; i < start.size(); i++) {
					s[i] = start.get(i);
				}
				double[] target = new double[places];
				for (i = 0; i < places; i++) {
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
					double[] markierung = new double[name2id.size()];
					for (i = 1; i <= places; i++) {
						String name = rP[i][0].toString();
						String value = rP[i][1].toString();
						int index = name2id.get(name);
						markierung[index] = Double.parseDouble(value);
					}
					pw = graphInstance.getContainer().getPathway(pwName);
					boolean reachable = false;
					for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
						if (bna instanceof CovNode) {
							CovNode n = (CovNode) bna;
							if (n.getTokenList().isGreaterEqual(markierung)) {
								if (n.getTokenList().isEqual(markierung)) {
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
								if (n.getTokenList().isGreater(markierung)) {
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
			break;
		case createCov:
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
					PopUpDialog.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case dataMappingColor:
			DataMappingColorMVC.createDataMapping();
			break;
		case datamining:
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				new SmacofView();
			} else
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			break;
		case rendererSettings:
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				PreRenderManager.getInstance();
			} else
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			break;
		case createDoc:
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
						PopUpDialog.getInstance().show("Latex compilation successful!", "PDF can be found at:\n" + docDir);
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
			break;
		case dataLabelMapping:
			// Open new window for file input
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				try {
					new LabelToDataMappingWindow();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} catch (InputFormatException ife) {
					PopUpDialog.getInstance().show("Inputfile error", ife.getMessage());
					// JOptionPane.showMessageDialog(w, ife.getMessage(), "Inputfile error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case enrichMirna:
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				MirnaSearch.enrichMirnas(graphInstance.getPathway(), true, true, false);
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case enrichGene:
			if (con.containsPathway() && graphInstance.getPathway().hasGotAtLeastOneElement()) {
				MirnaSearch.enrichGenes(graphInstance.getPathway(), true, true, false);
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
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
					PopUpDialog.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case wuff:
			if (con.containsPathway()) {
				pw = graphInstance.getPathway();
				if (pw.hasGotAtLeastOneElement()) {
					for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
						if (bna instanceof DynamicNode) {
							DynamicNode dn = (DynamicNode) bna;
							String maximalSpeed = KineticBuilder.createConvenienceKinetic(bna);
							dn.setMaximalSpeed(maximalSpeed);
						}
					}
				} else {
					PopUpDialog.getInstance().show("Error", "Please create a network first.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case transform:
			if (con.containsPathway()) {
				pw = graphInstance.getPathway();
				if (pw.hasGotAtLeastOneElement() && !pw.isPetriNet()) {
					List<Rule> rules = RuleManager.getInstance().getActiveRules();
					if(rules.size() == 0){
						PopUpDialog.getInstance().show("Error", "No active transformation rules found!.");
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
					PopUpDialog.getInstance().show("Error",
                                                   "Please create a biological network first. A Petri net cannot be transformed!.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
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
					PopUpDialog.getInstance().show("Error",
                                                   "Please transform the biological network into a Petri net first!.");
				}
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
			}
			break;
		case showTransformResult:
			if (con.containsPathway()) {
				pw = graphInstance.getPathway();
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
			break;
		case allPopUps:
			new AllPopUpsWindow();
			break;
		case nodesEdgesTypes:
			if (con.containsPathway()) {
				pw = graphInstance.getPathway();
				new NodesEdgesTypesWindow(pw);
			} else {
				PopUpDialog.getInstance().show("Error", "Please create a network first.");
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
		HashMap<BiologicalNodeAbstract, Integer> hmplaces = new HashMap<>();
		HashMap<BiologicalNodeAbstract, Integer> hmtransitions = new HashMap<>();
		int numberPlaces = 0;
		int numberTransitions = 0;
		ArrayList<String> names = new ArrayList<>();
		for (BiologicalNodeAbstract bna : graphInstance.getPathway().getAllGraphNodes()) {
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
		double[][] f = initArray(numberPlaces, numberTransitions);
		double[][] b = initArray(numberPlaces, numberTransitions);
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
		SimpleMatrixDouble cMatrix = new SimpleMatrixDouble(initArray(numberPlaces, numberTransitions));
		cMatrix.add(bMatrix);
		cMatrix.add(fMatrix);
		c = new DenseDoubleMatrix2D(cMatrix.getData());
	}
}
