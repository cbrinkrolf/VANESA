package gui;

import graph.algorithms.gui.GraphColoringGUI;
import graph.algorithms.gui.GraphNodeRemovalGUI;
import graph.algorithms.gui.GraphNodeDimensionGUI;
import graph.algorithms.gui.ShortestPathGui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import net.infonode.tabbedpanel.TabDropDownListVisiblePolicy;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.util.Direction;

public class GraphAlgorithmsWindow {

	private TabbedPanel tabbedPanel;
	private JPanel p = new JPanel();
	boolean emptyPane = true;

	private ShortestPathGui shortestPath = new ShortestPathGui();
	private GraphColoringGUI coloredgraph = new GraphColoringGUI();
	private GraphNodeRemovalGUI noderemoval = new GraphNodeRemovalGUI();
	private GraphNodeDimensionGUI nodeweight = new GraphNodeDimensionGUI();

	public GraphAlgorithmsWindow() {

		tabbedPanel = new TabbedPanel();
		tabbedPanel.getProperties().setTabAreaOrientation(Direction.UP);
		tabbedPanel.getProperties().setEnsureSelectedTabVisible(true);
		tabbedPanel.getProperties().setHighlightPressedTab(true);
		tabbedPanel.getProperties().setTabReorderEnabled(false);
		tabbedPanel.getProperties().setTabDropDownListVisiblePolicy(
				TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE);
		
		//Martin
		tabbedPanel.addTab(coloredgraph.getTitledTab());
		tabbedPanel.addTab(nodeweight.getTitledTab());
		tabbedPanel.addTab(noderemoval.getTitledTab());
		tabbedPanel.addTab(shortestPath.getTitledTab());

	}

	public void revalidateView() {

		if (emptyPane) {
			p.add(tabbedPanel, BorderLayout.CENTER);
			emptyPane = false;
		}
		shortestPath.revalidateView();
		nodeweight.revalidateView();
		noderemoval.revalidateView();
		coloredgraph.revalidateView();

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
