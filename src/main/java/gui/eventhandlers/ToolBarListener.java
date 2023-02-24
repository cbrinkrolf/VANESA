package gui.eventhandlers;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.CreatePathway;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.gui.CompareGraphsGUI;
import graph.hierarchies.AutoCoarse;
import graph.jung.classes.MyGraph;
import gui.InfoWindow;
import gui.MainWindow;
import gui.MyPopUp;
import gui.ParallelChooseGraphsWindow;
import petriNet.OpenModelicaResult;
import petriNet.PNTableDialog;
import petriNet.ReachController;

public class ToolBarListener implements ActionListener {

	private static ToolBarListener instance;

	private ToolBarListener() {
	}

	public static synchronized ToolBarListener getInstance() {
		if (ToolBarListener.instance == null) {
			ToolBarListener.instance = new ToolBarListener();
		}
		return ToolBarListener.instance;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		MainWindow w = MainWindow.getInstance();
		GraphContainer con = GraphContainer.getInstance();
		GraphInstance graphInstance = new GraphInstance();
		ToolbarActionCommands command = ToolbarActionCommands.get(event);
		if (command == null) {
			return;
		}
		switch (command) {
			case newNetwork:
				int option = JOptionPane.showOptionDialog(MainWindow.getInstance().getFrame(), "Which type of modeling do you prefer?",
														  "Choose Network Type...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
														  new String[] { "Biological Graph", "Petri Net" }, JOptionPane.CANCEL_OPTION);
				if (option != -1) {
					new CreatePathway();
					graphInstance.getPathway().setIsPetriNet(option == JOptionPane.NO_OPTION);
					w.getBar().paintToolbar(option == JOptionPane.NO_OPTION);
					w.updateAllGuiElements();
				}
				break;
			case move:
				if (con.containsPathway()) {
					con.changeMouseFunction("move");
					MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
					g.disableGraphTheory();
					// g.getVisualizationViewer().resize(20, 20);
					Dimension d = g.getVisualizationViewer().getPreferredSize();
					d.setSize(d.width * 2, d.height * 2);
					g.getVisualizationViewer().setPreferredSize(d);
					g.getVisualizationViewer().repaint();
				}
				break;
			case pick:
				if (con.containsPathway()) {
					con.changeMouseFunction("pick");
					MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
					g.disableGraphTheory();
				}
				break;
			case center:
				if (con.containsPathway()) {
					// CENTERING WITH SCALING
					graphInstance.getPathway().getGraph().normalCentering();
					// ONLY FOR CENTERING, NOT SCALING
					// graphInstance.getPathway().getGraph().animatedCentering();
				}
				break;
			case zoomIn:
				if (con.containsPathway()) {
					MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
					g.zoomIn();
				}
				break;
			case zoomOut:
				if (con.containsPathway()) {
					MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
					g.zoomOut();
				}
				break;
			case fullScreen:
				w.setFullScreen();
				break;
			case compressEdges:
				if (con.containsPathway()) {
					con.getPathway(w.getCurrentPathway()).stretchGraph(0.9);
					con.getPathway(w.getCurrentPathway()).updateMyGraph();
				}
				break;
			case stretchEdges:
				if (con.containsPathway()) {
					con.getPathway(w.getCurrentPathway()).stretchGraph(1.1);
					con.getPathway(w.getCurrentPathway()).updateMyGraph();
				}
				break;
			case edit:
				if (con.containsPathway()) {
					con.changeMouseFunction("edit");
					MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
					g.disableGraphTheory();
				}
				break;
			case merge:
				if (con.getPathwayNumbers() > 1) {
					MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
					g.disableGraphTheory();
					new CompareGraphsGUI();
				} else {
					MyPopUp.getInstance().show("Error", "Please create a network first!");
				}
				break;
			case del:
				if (con.containsPathway()) {
					Pathway pw = con.getPathway(w.getCurrentPathway());
					// g.stopVisualizationModel();
					pw.removeSelection();
					w.updateElementTree();
					w.updatePathwayTree();
					// w.updateTheoryProperties();
					// g.restartVisualizationModel();
				}
				break;
			case info:
				if (con.containsPathway()) {
					if (con.getPathway(w.getCurrentPathway()).hasGotAtLeastOneElement()) {
						new InfoWindow(false);
					}
				}
				break;
			case infoExtended:
				if (con.containsPathway()) {
					if (con.getPathway(w.getCurrentPathway()).hasGotAtLeastOneElement()) {
						new InfoWindow(true);
					}
				}
				break;
			case modelling:
				con.setPetriView(false);
				for (Component component : MainWindow.getInstance().getFrame().getContentPane().getComponents()) {
					if (component.getClass().getName().equals("javax.swing.JPanel")) {
						MainWindow.getInstance().getBar().paintToolbar(false);
						break;
					}
				}
				break;
			case discretePlace:
				con.changeMouseFunction("edit");
				con.setPetriView(true);
				con.setPetriNetEditingMode(Elementdeclerations.discretePlace);
				break;
			case continuousPlace:
				con.changeMouseFunction("edit");
				con.setPetriView(true);
				con.setPetriNetEditingMode(Elementdeclerations.continuousPlace);
				break;
			case discreteTransition:
				con.changeMouseFunction("edit");
				con.setPetriView(true);
				con.setPetriNetEditingMode(Elementdeclerations.discreteTransition);
				break;
			case continuousTransition:
				con.changeMouseFunction("edit");
				con.setPetriView(true);
				con.setPetriNetEditingMode(Elementdeclerations.continuousTransition);
				break;
			case stochasticTransition:
				con.changeMouseFunction("edit");
				con.setPetriView(true);
				con.setPetriNetEditingMode(Elementdeclerations.stochasticTransition);
				break;
			case createPetriNet:
				con.setPetriView(true);
				for (Component component : MainWindow.getInstance().getFrame().getContentPane().getComponents()) {
					if (component.getClass().getName().equals("javax.swing.JPanel")) {
						MainWindow.getInstance().getBar().paintToolbar(true);
						break;
					}
				}
				/*
				 * if (con.getPathwayNumbers() > 0) { MyGraph g =
				 * con.getPathway(w.getCurrentPathway()).getGraph(); g.disableGraphTheory(); //
				 * new CompareGraphsGUI(); new ConvertToPetriNet(); }
				 */
				// if (con.getPathwayNumbers() > 0) {
				// MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
				// g.disableGraphTheory();
				// //new CompareGraphsGUI();
				// new ConvertToPetriNet();
				// }
				break;
			case createCov:
				// System.out.println("cov erstellen");
				// MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
				// Cov cov = new Cov();
				if (JOptionPane.showConfirmDialog(MainWindow.getInstance().getFrame(),
												  "The calculation of the reach graph could take long time, especially if you have many places in your network. Do you want to perform the calculation anyway?",
												  "Please Conform your action...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					new ReachController();
				}
				if (GraphInstance.getMyGraph() != null) {
					GraphInstance.getMyGraph().changeToGEMLayout();
				} else {
					System.out.println("No Graph exists!");
				}
				break;
			case editElements:
				new PNTableDialog().setVisible(true);
				break;
			case parallelView:
				// create a graph choosing popup and calculate network properties
				new ParallelChooseGraphsWindow();
				break;
			case loadModResult:
				new OpenModelicaResult().execute();
				break;
			case group:
				if (con.containsPathway()) {
					con.getPathway(w.getCurrentPathway()).groupSelectedNodes();
					con.getPathway(w.getCurrentPathway()).updateMyGraph();
				}
				break;
			case deleteGroup:
				if (con.containsPathway()) {
					con.getPathway(w.getCurrentPathway()).deleteGroup();
					con.getPathway(w.getCurrentPathway()).updateMyGraph();
				}
				break;
			case coarseSelectedNodes:
				if (GraphInstance.getMyGraph() != null) {
					Set<BiologicalNodeAbstract> selectedNodes = new HashSet<>(
							graphInstance.getPathway().getSelectedNodes());
					BiologicalNodeAbstract.coarse(selectedNodes);
					graphInstance.getPathway().updateMyGraph();
					graphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
				} else {
					System.out.println("No Graph exists!");
				}
				break;
			case flatSelectedNodes:
				if (GraphInstance.getMyGraph() != null) {
					for (BiologicalNodeAbstract node : con.getPathway((w.getCurrentPathway())).getGraph()
														  .getVisualizationViewer().getPickedVertexState().getPicked()) {
						node.flat();
						graphInstance.getPathway().updateMyGraph();
						MainWindow.getInstance().removeTab(false, node.getTab().getTitelTab(), node);
					}
					new GraphInstance().getPathway().getGraph().getVisualizationViewer().repaint();
				} else {
					System.out.println("No Graph exists!");
				}
				break;
			case enterNode:
				if (GraphInstance.getMyGraph() != null) {
					for (BiologicalNodeAbstract node : graphInstance.getPathway().getGraph().getVisualizationViewer()
																	.getPickedVertexState().getPicked()) {
						if (!node.isCoarseNode() && !node.isMarkedAsCoarseNode()) {
							continue;
						}
						w.getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
						// Pathway newPW = null;
						for (BiologicalNodeAbstract n : node.getVertices().keySet()) {
							node.getVertices().put(n, graphInstance.getPathway().getVertices().get(n));
						}
						String newPathwayName = con.addPathway(node.getLabel(), node);
						Pathway pw = con.getPathway(newPathwayName);
						w.addTab(pw.getTab().getTitelTab());
						w.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						graphInstance.getPathway().setIsPetriNet(node.isPetriNet());
						w.getBar().paintToolbar(node.isPetriNet());
						w.updateAllGuiElements();
						graphInstance.getPathway().updateMyGraph();
						graphInstance.getPathway().getGraph().normalCentering();
					}
				} else {
					System.out.println("No Graph exists!");
				}
				break;
			case autoCoarse:
				if (GraphInstance.getMyGraph() != null) {
					AutoCoarse.coarseSeperatedSubgraphs(graphInstance.getPathway());
					new GraphInstance().getPathway().getGraph().getVisualizationViewer().repaint();
				} else {
					System.out.println("No Graph exists!");
				}
				break;
			case newWindow:
				MainWindow.getInstance().addView();
				break;
			case hierarchy:
				if (con.containsPathway()) {
					con.changeMouseFunction("hierarchy");
					MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
					g.disableGraphTheory();
				}
				break;
			case mergeSelectedNodes:
				if (GraphInstance.getMyGraph() != null) {
					graphInstance.getPathway().mergeNodes(graphInstance.getPathway().getGraph().getVisualizationViewer()
																	   .getPickedVertexState().getPicked());
				} else {
					System.out.println("No Graph exists!");
				}
				break;
			case splitNode:
				if (GraphInstance.getMyGraph() != null) {
					graphInstance.getPathway().splitNode(graphInstance.getPathway().getGraph().getVisualizationViewer()
																	  .getPickedVertexState().getPicked());
				} else {
					System.out.println("No Graph exists!");
				}
				break;
			case adjustDown:
				if (GraphInstance.getMyGraph() != null) {
					Set<BiologicalNodeAbstract> nodes = graphInstance.getPathway().getSelectedNodes();
					graphInstance.getPathway().adjustDown(nodes);
				} else {
					System.out.println("No Graph exists!");
				}
				break;
			case adjustLeft:
				if (GraphInstance.getMyGraph() != null) {
					Set<BiologicalNodeAbstract> nodes = graphInstance.getPathway().getSelectedNodes();
					graphInstance.getPathway().adjustLeft(nodes);
				} else {
					System.out.println("No Graph exists!");
				}
				break;
			case adjustHorizontalSpace:
				if (GraphInstance.getMyGraph() != null) {
					Set<BiologicalNodeAbstract> nodes = graphInstance.getPathway().getSelectedNodes();
					graphInstance.getPathway().adjustHorizontalSpace(nodes);
				} else {
					System.out.println("No Graph exists!");
				}
				break;
			case adjustVerticalSpace:
				if (GraphInstance.getMyGraph() != null) {
					Set<BiologicalNodeAbstract> nodes = graphInstance.getPathway().getSelectedNodes();
					graphInstance.getPathway().adjustVerticalSpace(nodes);
				} else {
					System.out.println("No Graph exists!");
				}
				break;
		}
	}
}
