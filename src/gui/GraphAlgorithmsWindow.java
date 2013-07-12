package gui;

import graph.algorithms.gui.HubDetectionGui;
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
	private HubDetectionGui hub = new HubDetectionGui();

	public GraphAlgorithmsWindow() {

		tabbedPanel = new TabbedPanel();
		tabbedPanel.getProperties().setTabAreaOrientation(Direction.UP);
		tabbedPanel.getProperties().setEnsureSelectedTabVisible(true);
		tabbedPanel.getProperties().setHighlightPressedTab(true);
		tabbedPanel.getProperties().setTabReorderEnabled(false);
		tabbedPanel.getProperties().setTabDropDownListVisiblePolicy(
				TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE);
		tabbedPanel.addTab(shortestPath.getTitledTab());
		//TODO hub detection wieder rein
		//tabbedPanel.addTab(hub.getTitledTab());

	}

	public void revalidateView() {

		if (emptyPane) {
			p.add(tabbedPanel, BorderLayout.CENTER);
			emptyPane = false;
		}
		//System.out.println("bin im revalidatview drin");
		shortestPath.revalidateView();
		//TODO hub detection wieder rein
		//hub.revalidateView();

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
