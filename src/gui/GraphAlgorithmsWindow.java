package gui;

import graph.algorithms.gui.DenselyConnectedBiclusteringGUI;
import graph.algorithms.gui.GraphColoringGUI;
import graph.algorithms.gui.GraphNodeRemovalGUI;
import graph.algorithms.gui.GraphNodeDimensionGUI;
import graph.algorithms.gui.ShortestPathGui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

public class GraphAlgorithmsWindow {

	private JTabbedPane tabbedPanel;
	private JPanel p = new JPanel(new MigLayout("ins 0, wrap 1"));
	boolean emptyPane = true;

	private ShortestPathGui shortestPath = new ShortestPathGui();
	private GraphColoringGUI coloredgraph = new GraphColoringGUI();
	private GraphNodeRemovalGUI noderemoval = new GraphNodeRemovalGUI();
	private GraphNodeDimensionGUI nodeweight = new GraphNodeDimensionGUI();
//	private DenselyConnectedBiclusteringGUI denselyConnected = new DenselyConnectedBiclusteringGUI();

	public GraphAlgorithmsWindow() {

		
		tabbedPanel = new JTabbedPane();
		//tabbedPanel.getProperties().setTabAreaOrientation(Direction.UP);
		//tabbedPanel.getProperties().setEnsureSelectedTabVisible(true);
		//tabbedPanel.getProperties().setHighlightPressedTab(true);
		//tabbedPanel.getProperties().setTabReorderEnabled(false);
		//tabbedPanel.getProperties().setTabDropDownListVisiblePolicy(
		//		TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE);
		
		//Martin
		tabbedPanel.addTab("Coloring", coloredgraph.getPanel());
		//JPanel p = new JPanel(new MigLayout("ins 0"));
		//p.add(new JLabel("Coloring"));
		//tabbedPanel.setTabComponentAt(0, p);
		
		tabbedPanel.addTab("Node Weighting", nodeweight.getPanel());
		//JPanel p1 = new JPanel(new MigLayout("ins 0"));
		//p1.add(new JLabel("Node Weighting"));
		//tabbedPanel.setTabComponentAt(1, p1);
		
		tabbedPanel.addTab("Node Removal", noderemoval.getPanel());
		//JPanel p2 = new JPanel(new MigLayout("ins 0"));
		//p2.add(new JLabel("Node Removal"));
		//tabbedPanel.setTabComponentAt(2, p2);
		
		tabbedPanel.addTab("Shortest Path", shortestPath.getPanel());
		//JPanel p3 = new JPanel(new MigLayout("ins 0"));
		//p3.add(new JLabel("Shortest Path"));
		//tabbedPanel.setTabComponentAt(3, p3);
		
//		tabbedPanel.addTab("DCB", denselyConnected.getPanel());

	}

	public void revalidateView() {

		if (emptyPane) {
			p.add(tabbedPanel);//, BorderLayout.CENTER);
			emptyPane = false;
		}
		coloredgraph.revalidateView();
		nodeweight.revalidateView();
		noderemoval.revalidateView();
		shortestPath.revalidateView();
//		denselyConnected.revalidateView();

		p.setVisible(true);
		p.repaint();

	}

	public void removeAllElements() {
		emptyPane = true;
		p.removeAll();
		p.setVisible(false);
	}

	public JPanel getTheoryPane() {
		p.setVisible(false);
		return p;
	}

}
