package gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import graph.algorithms.gui.GraphColoringGUI;
import graph.algorithms.gui.GraphNodeDimensionGUI;
import graph.algorithms.gui.GraphNodeRemovalGUI;
import graph.algorithms.gui.ShortestPathGui;
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
		init();
	}

	public void revalidateView() {
		//System.out.println("revalidate");
		int index = tabbedPanel.getSelectedIndex();
		removeAllElements();
		if (emptyPane) {
			//System.out.println("pane empty");
			init();
			p.add(tabbedPanel);//, BorderLayout.CENTER);
			emptyPane = false;
		}
		coloredgraph.revalidateView();
		nodeweight.revalidateView();
		noderemoval.revalidateView();
		shortestPath.revalidateView();
//		denselyConnected.revalidateView();
		//tabbedPanel.revalidate();
		p.setVisible(true);
		p.repaint();
		tabbedPanel.setSelectedIndex(index);

	}

	public void removeAllElements() {
		emptyPane = true;
		p.removeAll();
		p.setVisible(false);
		p.repaint();
	}

	public JPanel getTheoryPane() {
		p.setVisible(false);
		return p;
	}
	
	private void init(){
		
		shortestPath = new ShortestPathGui();
		coloredgraph = new GraphColoringGUI();
		noderemoval = new GraphNodeRemovalGUI();
		nodeweight = new GraphNodeDimensionGUI();
		
		tabbedPanel = new JTabbedPane();
		tabbedPanel.addTab("Coloring", coloredgraph.getPanel());
		tabbedPanel.setTabComponentAt(0, new JPanel().add(new JLabel("Coloring")));
		
		tabbedPanel.addTab("Node Weighting", nodeweight.getPanel());
		tabbedPanel.setTabComponentAt(1, new JPanel().add(new JLabel("Node Weighting")));
		
		tabbedPanel.addTab("Node Removal", noderemoval.getPanel());
		tabbedPanel.setTabComponentAt(2, new JPanel().add(new JLabel("Node Removal")));

		tabbedPanel.addTab("Shortest Path", shortestPath.getPanel());
		tabbedPanel.setTabComponentAt(3, new JPanel().add(new JLabel("Shortest Path")));
		
	}
}
