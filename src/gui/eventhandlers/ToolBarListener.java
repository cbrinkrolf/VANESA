package gui.eventhandlers;

import graph.ContainerSingelton;
import graph.CreatePathway;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.gui.CompareGraphsGUI;
import graph.hierarchies.AutoCoarse;
import graph.jung.classes.MyGraph;
import gui.HeatmapChooseGraphsWindow;
import gui.InfoWindow;
import gui.MainWindow;
import gui.MainWindowSingleton;
import gui.ParallelChooseGraphsWindow;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import petriNet.ConvertToPetriNet;
import petriNet.OpenModellicaResult;
import petriNet.PNTableDialog;
import petriNet.PetriNetSimulation;
import petriNet.ReachController;
import save.graphPicture.WriteGraphPicture;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ToolBarListener implements ActionListener {

	public void showCreateBeforeMessage() {
		JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(),
				"Please create a network first!");
	}

	public void showCreate2NetworksMessage() {
		JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(),
				"Please create two networks first!");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String event = e.getActionCommand();
		MainWindow w = MainWindowSingleton.getInstance();
		GraphContainer con = ContainerSingelton.getInstance();
		GraphInstance graphInstance = new GraphInstance();

		if ("new Network".equals(event)) {
			
			
			int option= JOptionPane.showOptionDialog(MainWindowSingleton.getInstance(), "Which type of modeling do you prefer?", "Choose Network Type...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Biological Graph","Petri Net"}, JOptionPane.CANCEL_OPTION);
			if (option!=-1){
				new CreatePathway();
				graphInstance.getPathway().setPetriNet(option==JOptionPane.NO_OPTION);
				w.getBar().paintToolbar(option==JOptionPane.NO_OPTION);
				w.updateAllGuiElements();
			}
			
		} else if ("move".equals(event)) {
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
		} else if ("pick".equals(event)) {
			if (con.containsPathway()) {
				con.changeMouseFunction("pick");
				MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
				g.disableGraphTheory();
			}
		} else if ("center".equals(event)) {
			if (con.containsPathway()) {
				graphInstance.getPathway().getGraph().animatedCentering();
			}
		} else if ("zoom in".equals(event)) {
			if (con.containsPathway()) {
				MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
				g.zoomIn();
			}
		} else if ("full screen".equals(event)) {

			w.setFullScreen();

		} else if ("zoom out".equals(event)) {
			if (con.containsPathway()) {
				MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
				g.zoomOut();
			}
		} else if("compressEdges".equals(event)){ 
			if (con.containsPathway()) {
				con.getPathway(w.getCurrentPathway()).stretchGraph(0.9);
			}
		
		} else if("stretchEdges".equals(event)){
			if (con.containsPathway()) {
				con.getPathway(w.getCurrentPathway()).stretchGraph(1.1);
			}
		}else if ("edit".equals(event)) {
			if (con.containsPathway()) {
				con.changeMouseFunction("edit");
				MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
				g.disableGraphTheory();
			}
		} else if ("merge".equals(event)) {
			if (con.getPathwayNumbers() > 1) {
				MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
				g.disableGraphTheory();
				new CompareGraphsGUI();
			} else {
				this.showCreate2NetworksMessage();
			}
		} else if ("del".equals(event)) {
			if (con.containsPathway()) {
				MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
				g.lockVertices();
				//g.stopVisualizationModel();
				g.removeSelection();
				w.updateElementTree();
				w.updateFilterView();
				w.updatePathwayTree();
				//w.updateTheoryProperties();
				g.unlockVertices();
				//g.restartVisualizationModel();
			}
		} else if ("info".equals(event)) {
			new InfoWindow(false);
		} else if ("infoextended".equals(event)) {
			new InfoWindow(true);
		} else if ("3DView".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					Pathway p = graphInstance.getPathway();
					Pathway[] pathways = new Pathway[1];
					pathways[0] = p;
					// CompareGraphs3D view3d =
					//new CompareGraphs3D(pathways);

				} else {
					this.showCreateBeforeMessage();
				}
			} else {
				this.showCreateBeforeMessage();
			}
		} else if ("heatmap".equals(event)) {
			if (con.getPathwayNumbers() > 1) {
				new HeatmapChooseGraphsWindow();
			} else {
				this.showCreate2NetworksMessage();
			}
		} else if ("picture".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new WriteGraphPicture().writeFile();
				} else {
					this.showCreateBeforeMessage();
				}
			} else {
				this.showCreateBeforeMessage();
			}
		} else if ("print".equals(event)) {
			if (con.containsPathway()) {
				if (graphInstance.getPathway().hasGotAtLeastOneElement()) {
					new WriteGraphPicture().printGraph();
				} else {
					this.showCreateBeforeMessage();
				}
			} else {
				this.showCreateBeforeMessage();
			}
		} else if ("modelling".equals(event)) {

			con.setPetriView(false);

			Component[] c = MainWindowSingleton.getInstance().getContentPane()
					.getComponents();
			for (int i = 0; i < c.length; i++) {
				if (c[i].getClass().getName().equals("javax.swing.JPanel")) {
					MainWindowSingleton.getInstance().getBar().paintToolbar(
							false);
					break;
				}

			}
		} else if ("discretePlace".equals(event)) {
			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(event);

		} else if ("convertIntoPetriNet".equals(event) && (con.getPathwayNumbers() > 0) ) {	
				MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
				g.disableGraphTheory();
				// new CompareGraphsGUI();
				new ConvertToPetriNet();
			
			Component[] c = MainWindowSingleton.getInstance().getContentPane()
			.getComponents();
			for (int i = 0; i < c.length; i++) {
				if (c[i].getClass().getName().equals("javax.swing.JPanel")) {
					MainWindowSingleton.getInstance().getBar().paintToolbar(
							con.getPathway(w.getCurrentPathway()).isPetriNet());
					break;
				}
			}			
		} else if ("continuousPlace".equals(event)) {

			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(event);

		} else if ("discreteTransition".equals(event)) {

			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(event);

		} else if ("continiousTransition".equals(event)) {

			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(event);

		} else if ("stochasticTransition".equals(event)) {

			con.changeMouseFunction("edit");
			con.setPetriView(true);
			con.setPetriNetEditingMode(event);

		} else if ("createPetriNet".equals(event)) {
			con.setPetriView(true);

			Component[] c = MainWindowSingleton.getInstance().getContentPane()
					.getComponents();
			for (int i = 0; i < c.length; i++) {
				if (c[i].getClass().getName().equals("javax.swing.JPanel")) {
					MainWindowSingleton.getInstance().getBar().paintToolbar(
							true);
					break;
				}
			}

			/*
			 * if (con.getPathwayNumbers() > 0) { MyGraph g =
			 * con.getPathway(w.getCurrentPathway()).getGraph();
			 * g.disableGraphTheory(); // new CompareGraphsGUI(); new
			 * ConvertToPetriNet(); }
			 */

			// if (con.getPathwayNumbers() > 0) {
			// MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
			// g.disableGraphTheory();
			// //new CompareGraphsGUI();
			// new ConvertToPetriNet();
			// }

		} else if ("createCov".equals(event)) {
			// System.out.println("cov erstellen");
			// MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
			// Cov cov = new Cov();
			if (JOptionPane.showConfirmDialog(
					MainWindowSingleton.getInstance(),
				    "The calculation of the reach graph could take long time, especially if you have many places in your network. Do you want to perform the calculation anyway?",
				    "Please Conform your action...",
				    JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION) new ReachController();
			GraphInstance.getMyGraph().changeToGEMLayout();
		
		}
		else if ("editElements".equals(event)) {
			new PNTableDialog().setVisible(true);
		}
		else if ("parallelview".equals(event)) {
			//create a graph choosing popup and calculate network properties
			new ParallelChooseGraphsWindow();
		}
		else if ("loadModResult".equals(event)){
			new OpenModellicaResult().execute();
		}
		else if ("simulate".equals(event)){
			new PetriNetSimulation();
		}
		else if ("coarseSelectedNodes".equals(event)){
			if(GraphInstance.getMyGraph() != null){
				//System.out.println("coarse");
				Set<BiologicalNodeAbstract> selectedNodes = new HashSet<BiologicalNodeAbstract>();
				selectedNodes.addAll(graphInstance.getPathway().getSelectedNodes());
				BiologicalNodeAbstract.coarse(selectedNodes);
				graphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
			}else{
				System.out.println("No Graph exists!");
			}
		}
		else if ("flatSelectedNodes".equals(event)){
			if(GraphInstance.getMyGraph() != null){
				for(BiologicalNodeAbstract node : con.getPathway((w.getCurrentPathway())).getGraph().
						getVisualizationViewer().getPickedVertexState().getPicked()){
					node.flat();
					MainWindowSingleton.getInstance().removeTab(false, node.getTab().getTitelTab(), node);
				}
				new GraphInstance().getPathway().getGraph().getVisualizationViewer().repaint();
			}else{
				System.out.println("No Graph exists!");
				}
		}
		else if ("enterNode".equals(event)){
			if(GraphInstance.getMyGraph() != null){
					for(BiologicalNodeAbstract node : graphInstance.getPathway().getGraph().
							getVisualizationViewer().getPickedVertexState().getPicked()){
						w.returnFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
						//Pathway newPW = null;
						String newPathwayName = con.addPathway(node.getLabel(), node);
						Pathway pw = con.getPathway(newPathwayName);
						w.addTab(pw.getTab().getTitelTab());
						w.returnFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						graphInstance.getPathway().setPetriNet(node.isPetriNet());
						w.getBar().paintToolbar(node.isPetriNet());
						w.updateAllGuiElements();
					}	
			}else{
				System.out.println("No Graph exists!");
				}
		}else if ("autocoarse".equals(event)){
			if(GraphInstance.getMyGraph() != null){
				AutoCoarse.coarseSeperatedSubgraphs(graphInstance.getPathway());
				new GraphInstance().getPathway().getGraph().getVisualizationViewer().repaint();
			}else{
			System.out.println("No Graph exists!");
			}
	}
		else if("newWindow".equals(event)){
			MainWindowSingleton.getInstance().addView();
		} 
		else if("hierarchy".equals(event)){
			if (con.containsPathway()) {
				con.changeMouseFunction("hierarchy");
				MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
				g.disableGraphTheory();
			}
		}
		else if("mergeSelectedNodes".equals(event)){
			if(GraphInstance.getMyGraph() != null){
			//System.out.println("merge");
			graphInstance.getPathway().mergeNodes(graphInstance.getPathway().getGraph().getVisualizationViewer().getPickedVertexState().getPicked());
			}else{
				System.out.println("No Graph exists!");
			}
		}
		else if("splitNode".equals(event)){
			if(GraphInstance.getMyGraph() != null){
				graphInstance.getPathway().splitNode(graphInstance.getPathway().getGraph().getVisualizationViewer().getPickedVertexState().getPicked());
			}
		}
	}
}
