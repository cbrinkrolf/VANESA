package gui;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import net.infonode.tabbedpanel.TitledTab;

import javax.swing.*;

public class GraphTab {
	private static final ImageIcon biologicalNetworkIcon = ImagePath.getInstance().getImageIcon(
			"biologicalNetworkTiny.png");
	private static final ImageIcon petriNetworkIcon = ImagePath.getInstance().getImageIcon("petriNetworkTiny.png");

	private final TitledTab tab;

	public GraphTab(final String name, final GraphZoomScrollPane viewer) {
		tab = new TitledTab(name, null, viewer, new CloseButton());
	}

	public String getTitle() {
		return tab.getText();
	}

	public void setTitle(final String title) {
		tab.setText(title);
	}

	public Icon getIcon() {
		return tab.getIcon();
	}

	public void setIcon(final Icon icon) {
		tab.setIcon(icon);
	}

	public void setBiologicalNetworkIcon() {
		setIcon(biologicalNetworkIcon);
	}

	public void setPetriNetworkIcon() {
		setIcon(petriNetworkIcon);
	}

	public TitledTab getTitleTab() {
		return tab;
	}
}
